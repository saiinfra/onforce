package com.services.component;

import java.util.List;

import com.domain.MetaBean;
import com.domain.MetadataLogDO;
import com.ds.salesforce.dao.comp.DeployMetadataDAO;
import com.exception.SFErrorCodes;
import com.exception.SFException;
import com.services.application.RDAppService;
import com.tasks.PreProcessingTask;
import com.util.Constants;
import com.util.FileBasedQuickDeploy;
import com.util.SFoAuthHandle;
import com.util.oauth.RefreshTokens;

/**
 * 
 * @author FDDeployCompService used to Process Deploy Request
 *
 */
public class FDQuickDeployCompService {

	public FDQuickDeployCompService() {
		super();
	}

	/**
	 * 
	 * @param bOrgId
	 * @param bOrgToken
	 * @param bOrgURL
	 * @param refreshToken
	 * @param metadataLogId
	 * @param isValidate
	 */
	public void deployRecentValidation(String bOrgId, String bOrgToken,
			String bOrgURL, String refreshToken, String metadataLogId,
			boolean isValidate, String validationId) {
		MetadataLogDO metadataLogDO = null;
		SFoAuthHandle sfSourceHandle = null;
		SFoAuthHandle sfTargetHandle = null;

		// do pre-processing
		// does some sanity checks on input variables
		// updates the refreshed tokens in Environment
		PreProcessingTask preProcessingTask = new PreProcessingTask(bOrgId,
				bOrgToken, bOrgURL, refreshToken, Constants.BaseOrgID,
				metadataLogId);
		preProcessingTask.doPreProcess();

		// get refreshed base token
		bOrgToken = RefreshTokens.getoAuthToken();
		String packageName = "";

		try {
			// Get Meta data Log details
			metadataLogDO = RDAppService.findMetadataLog(metadataLogId,
					FDGetSFoAuthHandleService.getSFoAuthHandle(bOrgId,
							bOrgToken, bOrgURL, refreshToken,
							Constants.BaseOrgID));
			// nullify connection
			FDGetSFoAuthHandleService.setSfHandleToNUll();

			// updating metadataLog to prcessing state
			RDAppService.updateMetadataLogStatus(metadataLogDO,
					Constants.PROCESSING_STATUS, FDGetSFoAuthHandleService
							.getSFoAuthHandle(bOrgId, bOrgToken, bOrgURL,
									refreshToken, Constants.BaseOrgID));
			// nullify connection
			FDGetSFoAuthHandleService.setSfHandleToNUll();
			String msg = "";

			if ((metadataLogDO.getAction() != null)
					&& ((metadataLogDO.getAction().equals("Deploy")) || (metadataLogDO
							.getAction().equals("Validate")))) {
				if (metadataLogDO.getStatus() != null
						&& (metadataLogDO.getStatus()
								.equals(Constants.PROCESSING_STATUS))) {
					System.out.println("Action" + metadataLogDO.getAction());
					DeployMetadataDAO deployMetadataDAO = new DeployMetadataDAO();

					// find to be deployed object list
					List<Object> deployList = deployMetadataDAO.findById(
							metadataLogDO.getLogName(),
							FDGetSFoAuthHandleService.getSFoAuthHandle(bOrgId,
									bOrgToken, bOrgURL, refreshToken,
									Constants.BaseOrgID));
					FDGetSFoAuthHandleService.setSfHandleToNUll();

					// get source salesforce handle
					sfSourceHandle = FDGetSFoAuthHandleService
							.getSFoAuthHandle(((MetaBean) deployList.get(0))
									.getSourceOrg(), bOrgId, bOrgToken,
									bOrgURL, refreshToken,
									Constants.CustomOrgID);

					// get target salesforce handle
					sfTargetHandle = FDGetSFoAuthHandleService
							.getSFoAuthHandle(((MetaBean) deployList.get(0))
									.getTargetOrg(), bOrgId, bOrgToken,
									bOrgURL, refreshToken,
									Constants.CustomOrgID);

					try {

						deployObjToTargetOrg(bOrgId, bOrgToken, bOrgURL,
								refreshToken, sfSourceHandle, sfTargetHandle,
								packageName, isValidate, metadataLogDO,
								validationId);

						Thread.sleep(Constants.waitFor1Sec);
					} catch (Exception e) {
						msg = e.getMessage();
					} finally {
						// String packgNames =
						// metadataLogDO.getNoOfPackgsByOrderMap().get(orderKey);
						System.out.println("package Names: " + packageName);
						RDAppService.updateDeploymentDetails(metadataLogId, msg
								+ " for package: " + packageName, metadataLogDO
								.getSourceOrgId(), FDGetSFoAuthHandleService
								.getSFoAuthHandle(bOrgId, bOrgToken, bOrgURL,
										refreshToken, Constants.BaseOrgID));
					}
				}

			}

		} catch (SFException e) {
			if (metadataLogDO != null) {
				// refresh connection
				FDGetSFoAuthHandleService.setSfHandleToNUll();
				// updating metadataLog
				RDAppService.updateMetadataLogStatus(metadataLogDO,
						Constants.ERROR_STATUS, FDGetSFoAuthHandleService
								.getSFoAuthHandle(bOrgId, bOrgToken, bOrgURL,
										refreshToken, Constants.BaseOrgID));
				// refresh connection
				FDGetSFoAuthHandleService.setSfHandleToNUll();
				// updating Deploy Details
				RDAppService.updateDeploymentDetails(metadataLogId, e
						.getMessage(), metadataLogDO.getSourceOrgId(),
						FDGetSFoAuthHandleService.getSFoAuthHandle(bOrgId,
								bOrgToken, bOrgURL, refreshToken,
								Constants.BaseOrgID));
				// refresh connection
				FDGetSFoAuthHandleService.setSfHandleToNUll();
			} else {
				System.out.println("Salesforce Exception " + e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (metadataLogDO != null) {
				// refresh connection
				FDGetSFoAuthHandleService.setSfHandleToNUll();
				// updating metadataLog
				RDAppService.updateMetadataLogStatus(metadataLogDO,
						Constants.ERROR_STATUS, FDGetSFoAuthHandleService
								.getSFoAuthHandle(bOrgId, bOrgToken, bOrgURL,
										refreshToken, Constants.BaseOrgID));

				// refresh connection
				FDGetSFoAuthHandleService.setSfHandleToNUll();
				// updating Deploy Details
				RDAppService.updateDeploymentDetails(metadataLogId, e
						.getMessage(), metadataLogDO.getSourceOrgId(),
						FDGetSFoAuthHandleService.getSFoAuthHandle(bOrgId,
								bOrgToken, bOrgURL, refreshToken,
								Constants.BaseOrgID));
				// refresh connection
				FDGetSFoAuthHandleService.setSfHandleToNUll();
			} else {
				System.out.println("Salesforce Exception " + e.getMessage());
			}
		}
	}

	private void deployObjToTargetOrg(String bOrgId, String bOrgToken,
			String bOrgURL, String refreshToken, SFoAuthHandle sfSourceHandle,
			SFoAuthHandle sfTargetHandle, String packageName,
			boolean isValidate, MetadataLogDO metadataLogDO, String validationId)
			throws SFException {

		try {
			(new FileBasedQuickDeploy()).deployRecentValidation(bOrgId,
					bOrgToken, bOrgURL, refreshToken, sfTargetHandle,
					packageName, isValidate, metadataLogDO, validationId);
		} catch (Exception e) {
			// e.printStackTrace();
			// System.out.println(e.toString());
			throw new SFException(e.toString(), SFErrorCodes.FileDeploy_Error);
		}
	}
}
