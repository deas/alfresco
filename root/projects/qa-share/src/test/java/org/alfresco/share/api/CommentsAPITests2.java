/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.api;

import org.alfresco.json.JSONUtil;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.AlfrescoHttpClient;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by olga.lokhach
 */

@Listeners(FailedTestListener.class)
@Test(groups = "EnterpriseOnly")
public class CommentsAPITests2 extends AlfrescoHttpClient

{
    public CommentsAPITests2() throws Exception
    {
        super();
    }

    private static Log logger = LogFactory.getLog(CommentsAPITests2.class);
    private String testName;
    private String testUser;
    private String siteName;
    private String fileName;
    private String docGuid;
    private String reqURL;
    private DocumentLibraryPage doclibPage;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        logger.info("Starting Tests: " + testName);

        testName = this.getClass().getSimpleName();

        testUser = getUserNameFreeDomain(testName);

        siteName = getSiteName(testName) + System.currentTimeMillis();
        fileName = getFileName(testName + "_1") + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC, true).render();

        doclibPage = ShareUser.uploadFileInFolder(drone, new String[] { fileName });

        docGuid = ShareUser.getGuid(drone, fileName);

        reqURL = PageUtils.getProtocol(shareUrl) + PageUtils.getAddress(shareUrl) + "/alfresco/s/api/node/workspace/SpacesStore/" + docGuid + "/comments";

    }

    @Test
    public void AONE_14355() throws Exception
    {

        String[] authDetails = getAuthDetails(testUser);
        String[] headers = getRequestHeaders("application/json;charset=utf-8");
        HttpClient client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);

        for (int i = 1; i < 22; i++)
        {
            String[] body = { "title", "comment title " + i, "content", "comment body text " + i };
            HttpPost request = generatePostRequest(reqURL, headers, body);
            HttpResponse response = executeRequest(client, request);
            assertEquals(response.getStatusLine().getStatusCode(), 200, "Http response code must be 200");
            String result = JSONUtil.readStream(response.getEntity()).toJSONString();
            assertNotNull(result);
            assertEquals(getParameterValue("item", "title", result), "comment title " + i);
            assertEquals(getParameterValue("item", "content", result), "comment body text " + i);
            assertTrue(getParameterValue("item", "author", result).contains(testUser));
        }
    }

    @Test(dependsOnMethods = "AONE_14355")
    public void AONE_14356() throws Exception
    {

        String[] authDetails = getAuthDetails(testUser);
        String[] headers = getRequestHeaders("application/json;charset=utf-8");
        String query = "?reverse=false";

        HttpClient client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
        HttpGet request = generateGetRequest(reqURL + query, headers);
        HttpResponse response = executeRequestHttpResp(client, request);
        assertEquals(response.getStatusLine().getStatusCode(), 200, "Http response code must be 200");

        String result = JSONUtil.readStream(response.getEntity()).toJSONString();
        assertNotNull(result);
        assertEquals(getParameterValue("total", "", result), "21");
        assertEquals(getParameterValue("pageSize", "", result), "10");
        assertEquals(getParameterValue("startIndex", "", result), "0");
        assertEquals(getParameterValue("itemCount", "", result), "10");

        JSONObject json = new JSONObject(result);
        JSONArray jArray = (JSONArray) json.get("items");

        if (jArray.length() == 10)
        {
            int j = 1;
            for (int i = 0; i < jArray.length(); i++)
            {
                assertTrue(jArray.get(i).toString().contains("comment title " + j));
                j++;
            }
        }

        else
        {
            fail("JSON response must contain an array of 10 comments, but actual is " + jArray.length());
        }

        releaseConnection(client, response.getEntity());

    }

    @Test(dependsOnMethods = "AONE_14355")
    public void AONE_14357() throws Exception
    {
        String[] authDetails = getAuthDetails(testUser);
        String[] headers = getRequestHeaders("application/json;charset=utf-8");
        String query = "?reverse=false&startIndex=15&pageSize=5";

        HttpClient client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
        HttpGet request = generateGetRequest(reqURL + query, headers);
        HttpResponse response = executeRequestHttpResp(client, request);
        assertEquals(response.getStatusLine().getStatusCode(), 200, "Http response code must be 200");

        String result = JSONUtil.readStream(response.getEntity()).toJSONString();
        assertNotNull(result);

        assertEquals(getParameterValue("total", "", result), "21");
        assertEquals(getParameterValue("pageSize", "", result), "5");
        assertEquals(getParameterValue("startIndex", "", result), "15");
        assertEquals(getParameterValue("itemCount", "", result), "5");

        JSONObject json = new JSONObject(result);
        JSONArray jArray = (JSONArray) json.get("items");

        if (jArray.length() == 5)
        {
            int j = 16;
            for (int i = 0; i < jArray.length(); i++)
            {
                assertTrue(jArray.get(i).toString().contains("comment title " + j));
                j++;
            }
        }

        else
        {
            fail("JSON response must contain an array of 5 comments, but actual is " + jArray.length());
        }

        query = "?reverse=false&startIndex=20&pageSize=5";
        request = generateGetRequest(reqURL + query, headers);
        response = executeRequestHttpResp(client, request);
        assertEquals(response.getStatusLine().getStatusCode(), 200, "Http response code must be 200");

        result = JSONUtil.readStream(response.getEntity()).toJSONString();
        assertNotNull(result);

        assertEquals(getParameterValue("total", "", result), "21");
        assertEquals(getParameterValue("pageSize", "", result), "5");
        assertEquals(getParameterValue("startIndex", "", result), "20");
        assertEquals(getParameterValue("itemCount", "", result), "1");

        json = new JSONObject(result);
        jArray = (JSONArray) json.get("items");

        if (jArray.length() == 1)
        {
            for (int i = 0; i < jArray.length(); i++)
            {
                assertTrue(jArray.get(i).toString().contains("comment title 21"));
            }
        }

        else
        {
            fail("JSON response must contain an array of 1 comment, but actual is " + jArray.length());
        }

        releaseConnection(client, response.getEntity());
    }

}
