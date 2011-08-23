package org.alfresco.httpclient;

import java.io.IOException;

/**
 * 
 * @since 4.0
 *
 */
public interface AlfrescoHttpClient
{
    /**
     * Send Request to the repository
     */
    public Response sendRequest(Request req) throws AuthenticationException, IOException;
}
