package org.alfresco.webservice.test;


import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.extensions.webscripts.Status;

public class CXFAuthenticationSystemTest extends TestCase
{

    protected static final String USERNAME = "admin";
    protected static final String PASSWORD = "admin";
    public final static String ALFRESCO_URL = "http://localhost:8080/alfresco";

    private HttpClient httpClient = new HttpClient();

    private void sendPostRequest(String cxfEndpoint, String postBody) throws HttpException, IOException
    {
        PostMethod post = new PostMethod(cxfEndpoint);
        post.setRequestEntity(new ByteArrayRequestEntity(postBody.getBytes("UTF-8"), "text/xml"));
        int status = httpClient.executeMethod(post);

        if (Status.STATUS_OK != status)
        {
            fail("Status code " + status + " returned, but expected " + Status.STATUS_OK);
        }
    }

    public void testValidPasswordTypeCXFLogin() throws Exception
    {
        String cxfEndpoint = ALFRESCO_URL + "/cmis/RepositoryService";
        String postBody = "<?xml version='1.0' encoding='UTF-8'?>" +
            "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<S:Header>" +
                "<Security xmlns=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">" +
                "<Timestamp xmlns=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"></Timestamp>" +
                "<UsernameToken><Username>" + USERNAME +"</Username>" +
                "<Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">" + PASSWORD + "</Password></UsernameToken>" +
                "</Security>" +
            "</S:Header>" +
            "<S:Body><ns2:getRepositories xmlns=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:ns2=\"http://docs.oasis-open.org/ns/cmis/messaging/200908/\"/></S:Body>" +
            "</S:Envelope>";
        sendPostRequest(cxfEndpoint, postBody);
    }

    // Post request without PasswordType
    public void testInvalidPasswordTypeCXFLogin() throws Exception
    {
        String cxfEndpoint = ALFRESCO_URL + "/cmis/RepositoryService";
        String postBody = "<?xml version='1.0' encoding='UTF-8'?>" +
            "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<S:Header>" +
            "<wsse:Security S:mustUnderstand=\"1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">" +
                "<wsu:Timestamp wsu:Id=\"Timestamp-7485188\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"></wsu:Timestamp>" +
                "<wsse:UsernameToken wsu:Id=\"UsernameToken-20851530\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">" +
                "<wsse:Username>"+ USERNAME +"</wsse:Username>" +
                "<wsse:Password>"+ PASSWORD +"</wsse:Password>" +
                "</wsse:UsernameToken></wsse:Security>" +
            "</S:Header>" +
            "<S:Body><ns2:getRepositories xmlns=\"http://docs.oasis-open.org/ns/cmis/core/200908/\" xmlns:ns2=\"http://docs.oasis-open.org/ns/cmis/messaging/200908/\"/></S:Body>" +
            "</S:Envelope>";
        sendPostRequest(cxfEndpoint, postBody);
    }

}
