
package net.sforce.soap._2005._09.outbound;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * Notification Service Implementation
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "NotificationService", targetNamespace = "http://soap.sforce.com/2005/09/outbound", wsdlLocation = "file:/home/srikanth/testautomation.wsdl")
public class NotificationService
    extends Service
{

    private final static URL NOTIFICATIONSERVICE_WSDL_LOCATION;
    private final static WebServiceException NOTIFICATIONSERVICE_EXCEPTION;
    private final static QName NOTIFICATIONSERVICE_QNAME = new QName("http://soap.sforce.com/2005/09/outbound", "NotificationService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/home/srikanth/testautomation.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        NOTIFICATIONSERVICE_WSDL_LOCATION = url;
        NOTIFICATIONSERVICE_EXCEPTION = e;
    }

    public NotificationService() {
        super(__getWsdlLocation(), NOTIFICATIONSERVICE_QNAME);
    }

    public NotificationService(WebServiceFeature... features) {
        super(__getWsdlLocation(), NOTIFICATIONSERVICE_QNAME, features);
    }

    public NotificationService(URL wsdlLocation) {
        super(wsdlLocation, NOTIFICATIONSERVICE_QNAME);
    }

    public NotificationService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, NOTIFICATIONSERVICE_QNAME, features);
    }

    public NotificationService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public NotificationService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns NotificationPort
     */
    @WebEndpoint(name = "Notification")
    public NotificationPort getNotification() {
        return super.getPort(new QName("http://soap.sforce.com/2005/09/outbound", "Notification"), NotificationPort.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns NotificationPort
     */
    @WebEndpoint(name = "Notification")
    public NotificationPort getNotification(WebServiceFeature... features) {
        return super.getPort(new QName("http://soap.sforce.com/2005/09/outbound", "Notification"), NotificationPort.class, features);
    }

    private static URL __getWsdlLocation() {
        if (NOTIFICATIONSERVICE_EXCEPTION!= null) {
            throw NOTIFICATIONSERVICE_EXCEPTION;
        }
        return NOTIFICATIONSERVICE_WSDL_LOCATION;
    }

}
