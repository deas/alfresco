/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts;

import java.io.IOException;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.springframework.extensions.webscripts.TestWebScriptServer.PostRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.PutRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Request;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * Unit test to test Web Script API
 * 
 * @author David Ward
 */

public class WebScriptFormatReaderTest extends TestCase
{
    private static final String URL_REQUESTBODY = "/test/requestbody";
    private static final String URL_JSONECHO = "/test/jsonecho";
    private static final String URL_ENCODEDPOST = "/test/encodedpost";
    private static final String URL_BOGUS = "/test/bogus";
    private static final TestWebScriptServer TEST_SERVER = TestWebScriptServer.getTestServer();

    /**
     * Ensure that, for a non request type specific .js script, the request body
     * is available as requestbody.
     * 
     * @throws Exception
     */
    public void testRequestBody() throws Exception
    {
        String requestBody = "<html><head>Expected Result</head><body>Hello World</body></html>";
        sendRequest(new PutRequest(URL_REQUESTBODY, requestBody, "text/html"), 200, requestBody);
    }

    /**
     * Ensure that for a .json.js script and an application/json request, the
     * json string is available as "json".
     * 
     * @throws Exception
     */
    public void testJson() throws Exception
    {
        JSONObject json = new JSONObject();
        json.put("company", "Alfresco Software Inc.");
        json.put("building", "Park House");
        json.put("street", "Park Street");
        json.put("town", "Maidenhead");

        String postCode = "SL6 1SL";
        json.put("postCode", postCode);
        json.put("country", "United Kingdom");
        json.put("year", 2008);
        json.put("valid", true);

        String requestBody = json.toString();
        sendRequest(new PostRequest(URL_JSONECHO, requestBody, "application/json; charset=UTF-8"), 200, postCode);
    }

    /**
     * Ensure that for a .bogus.js script and an application/bogus request, an
     * error is returned because the bogus format is registered, but no
     * FormatReader is registered.
     * 
     * @throws Exception
     */
    public void testBogus() throws Exception
    {
        String requestBody = "I've got a lovely bunch of coconuts";
        sendRequest(new PostRequest(URL_BOGUS, requestBody, "application/bogus"), 500, null);
    }

    /**
     * @param req
     * @param expectedStatus
     * @param expectedResponse
     * @return
     * @throws IOException
     */
    private Response sendRequest(Request req, int expectedStatus, String expectedResponse) throws IOException
    {
        System.out.println();
        System.out.println("* Request: " + req.getMethod() + " " + req.getFullUri()
                + (req.getBody() == null ? "" : "\n" + req.getBody()));

        Response res = TEST_SERVER.submitRequest(req);

        System.out.println();
        System.out.println("* Response: " + res.getStatus() + " " + req.getMethod() + " " + req.getFullUri() + "\n"
                + res.getContentAsString());
        if (expectedStatus > 0)
        {
            assertEquals("Unexpected status code", expectedStatus, res.getStatus());
        }
        if (expectedResponse != null)
        {
            assertEquals("Unexpected response", expectedResponse, res.getContentAsString());
        }
        return res;
    }
}
