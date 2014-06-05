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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.Tag;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.TagsAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for Tags apis implemented in alfresco-remote-api.
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne" })
public class TagsAPITests extends TagsAPI
{

    private String testName;
    private String testUser;
    private String testUserInvalid;
    private String siteName;
    private String fileName;
    private String fileName2;
    private String tagId;

    private DocumentLibraryPage doclibPage;
    private String tagName;
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(TagsAPITests.class);
    private String tag13;

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName();

        testUser = getUserNameFreeDomain(testName);
        testUserInvalid = "invalid" + testUser;

        siteName = getSiteName(testName) + System.currentTimeMillis();
        fileName = getFileName(testName + "_1") + System.currentTimeMillis();
        fileName2 = getFileName(testName + "_2") + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName });
        ShareUserSitePage.addTagsFromDocLib(drone, fileName, Arrays.asList(siteName));

        ShareUser.uploadFileInFolder(drone, new String[] { fileName2 });
        tag13 = "TAG13";
        ShareUserSitePage.addTagsFromDocLib(drone, fileName2, Arrays.asList("TAG12", tag13));

    }

    @Test
    public void ALF_151901() throws Exception
    {
        ListResponse<Tag> tags = getTags(testUser, DOMAIN, null);
        assertNotNull(tags);
        for (Tag tag : tags.getList())
        {
            if (tag.getTag().equalsIgnoreCase(siteName))
            {
                tagId = tag.getId();
                tagName = tag.getTag();
                break;
            }
        }

        // Status: 401
        try
        {
            getTags(testUserInvalid, DOMAIN, null);
            Assert.fail(String.format("ALF_151901: , %s, Expected Result: %s", "get tags request with incorrect auth", "Error 401"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
    }

    @Test(dependsOnMethods = "ALF_151901")
    public void ALF_197201() throws Exception
    {
        // Get: Tag/tagId
        Tag tag = getTag(testUser, DOMAIN, tagId);
        assertNotNull(tag);
        assertEquals(tag.getTag(), tagName, "Received tag - " + tag);

        // Post: tag/tagId
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            tagsClient.create("tags", tagId, null, null, null, "Could not create the tag");
            Assert.fail(String.format("ALF_197201: , %s, Expected Result: %s", "POST tag not allowed for tag/tag id", "Error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Post: tag
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            tagsClient.create("tags", null, null, null, null, "Could not create the tag");
            Assert.fail(String.format("ALF_197201: , %s, Expected Result: %s", "POST tag not allowed for tags", "Error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Put: tag/tagId
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            tagsClient.update("tags", tagId, null, null, null, "Could not update the tag");
            Assert.fail(String.format("ALF_197201: , %s, Expected Result: %s", "PUT tag not allowed for tag and tag id", "Error 400"));
        }
        catch (PublicApiException e)
        {
            // TODO: TestLink: This is put without request body. Put with request body is tested in 218301 and returns 200 Is this test required?
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        // Put: tag
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            tagsClient.update("tags", null, null, null, null, "Could not update the tag");
            Assert.fail(String.format("ALF_197201: , %s, Expected Result: %s", "PUT tag not allowed for tags", "Error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Delete: tag
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            tagsClient.remove("tags", null, null, null, "Could not delete the tag");
            Assert.fail(String.format("ALF_197201: , %s, Expected Result: %s", "DELETE tag not allowed", "Error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // delete: tag/tagId
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            tagsClient.remove("tags", tagId, null, null, "Could not delete the tag");
            Assert.fail(String.format("ALF_197201: , %s, Expected Result: %s", "DELETE tag/tagId is not allowed", "Error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

    }

    @Test(dependsOnMethods = "ALF_151901")
    public void ALF_218301() throws Exception
    {
        String newTag = "newtag" + System.currentTimeMillis();
        Tag tag = new Tag(tagId, newTag);

        publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
        tag = updateTag(testUser, DOMAIN, tag);

        // Check on Share UI: that the tag is updated
        ShareUser.login(drone, testUser);

        doclibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        List<String> tagsAdded = doclibPage.getFileDirectoryInfo(fileName).getTags();

        assertTrue(tagsAdded.size() > 0);
        assertTrue(tagsAdded.contains(newTag), tagsAdded.toString());

        // Status: 401
        try
        {
            updateTag(testUserInvalid, DOMAIN, tag);
            Assert.fail(String.format("ALF_218301: , %s, Expected Result: %s", "update tags request with incorrect auth", "Error 401"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }

        // Status: 404
        try
        {
            tag.setId("blah");
            updateTag(testUser, DOMAIN, tag);
            Assert.fail(String.format("ALF_218301: , %s, Expected Result: %s", "get nodes tags request with incorrect tagid - " + tag, "Error 404"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test
    public void ALF_220601() throws Exception
    {
        Map<String, String> param = new HashMap<String, String>();

        try
        {
            param.put("maxItems", "a");
            getTags(testUser, DOMAIN, param);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getTags request with incorrect maxItems - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("skipCount", "s");
            getTags(testUser, DOMAIN, param);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getTags request with incorrect skipCount - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("maxItems", "-1");
            getTags(testUser, DOMAIN, param);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getTags request with incorrect maxItems - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("skipCount", "-2");
            getTags(testUser, DOMAIN, param);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getTags request with incorrect skipCount - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        param.clear();
        param.put("maxItems", "2147483647");
        param.put("skipCount", "1");
        ListResponse<Tag> response = getTags(testUser, DOMAIN, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(2147483647));
        assertEquals(response.getPaging().getSkipCount(), new Integer(1));

        param.clear();
        param.put("skipCount", "2");
        response = getTags(testUser, DOMAIN, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));

        param.clear();
        param.put("maxItems", "4");
        response = getTags(testUser, DOMAIN, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(4));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));

        response = getTags(testUser, DOMAIN, null);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));

        try
        {
            param.clear();
            param.put("skipCount", "a");
            param.put("maxItems", "b");
            getTags(testUser, DOMAIN, param);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getTags request with incorrect skipCount and maxItems - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("skipCount", "-1");
            param.put("maxItems", "-2");
            getTags(testUser, DOMAIN, param);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getTags request with incorrect skipCount and maxItems - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
    }

    @Test
    public void ALF_221801() throws Exception
    {

        // Check on Share UI: that the tag is updated
        ShareUser.login(drone, testUser);

        doclibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify Tag Value changed successfully
        ShareUserSitePage.clickOnTagNameInTreeMenu(drone, tag13).render();

        ShareUserSitePage.getDocLibInfoWithRetry(drone, fileName2, "isContentVisible", "", true);

        Map<String, String> param = new HashMap<String, String>();
        param.put("maxItems", "2147483647");
        param.put("skipCount", "1");
        ListResponse<Tag> response = getTags(testUser, DOMAIN, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(2147483647));
        assertEquals(response.getPaging().getSkipCount(), new Integer(1));
        assertFalse(response.getPaging().getHasMoreItems());

        param.clear();
        param.put("maxItems", "1");
        response = getTags(testUser, DOMAIN, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(1));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
        assertTrue(response.getPaging().getHasMoreItems());
    }

}
