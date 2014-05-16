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
import java.util.Map;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.NodeRating;
import org.alfresco.rest.api.tests.client.data.Tag;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.NodesAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for favourite rest apis implemented in alfresco-remote-api.
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne" })
public class NodesAPITests extends NodesAPI
{
    private String testName;
    private String testUser;
    private String testUserInvalid;
    private String siteName;
    private String fileName;
    private String fileName2;
    private String docGuid;
    private String docGuidInvalid;
    private String docGuid2;
    private String tagId;

    private DocumentLibraryPage doclibPage;
    private String testUserAnotherDomain;
    private static Log logger = LogFactory.getLog(NodesAPITests.class);

    AlfrescoVersion version = null;

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName();

        testUser = getUserNameFreeDomain(testName);
        testUserAnotherDomain = getUserNameForDomain(testName, "domain1.test");
        testUserInvalid = "invalid" + testUser;

        siteName = getSiteName(testName) + System.currentTimeMillis();
        fileName = getFileName(testName + "_1") + System.currentTimeMillis();
        fileName2 = getFileName(testName + "_2") + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserAnotherDomain);

        ShareUser.login(drone, testUser);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC, true).render();
        doclibPage = ShareUser.uploadFileInFolder(drone, new String[] { fileName });

        docGuid = ShareUser.getGuid(drone, fileName);
        docGuidInvalid = "invaLid" + docGuid;
        FileDirectoryInfo fileDirInfo = doclibPage.getFileDirectoryInfo(fileName);
        fileDirInfo.selectFavourite();
        fileDirInfo.selectLike();

        ShareUserSitePage.addTagsFromDocLib(drone, fileName, Arrays.asList("TAG1"));

        ShareUser.uploadFileInFolder(drone, new String[] { fileName2 });

        docGuid2 = ShareUser.getGuid(drone, fileName2);

        ShareUserSitePage.addTagsFromDocLib(drone, fileName2, Arrays.asList("TAG2"));

        ShareUserSitePage.addTagsFromDocLib(drone, fileName2, Arrays.asList("TAG3"));

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUserAnotherDomain, siteName, UserRole.COLLABORATOR);
    }

    @Test
    public void ALF_197301() throws Exception
    {
        // Status: 200
        ListResponse<Tag> tags = getNodeTags(testUser, DOMAIN, docGuid, null);
        assertNotNull(tags);
        for (Tag tag : tags.getList())
        {
            tagId = tag.getId();
            assertEquals(tag.getTag(), "tag1", "Received tag - " + tag);
        }

        // Status: 401
        try
        {
            getNodeTags(testUserInvalid, DOMAIN, docGuid, null);
            Assert.fail(String.format("ALF_152001: , %s, Expected Result: %s", "get nodes tags request with incorrect auth", "Expected error 401"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }

        // Status: 404
        try
        {
            getNodeTags(testUser, DOMAIN, docGuidInvalid, null);
            Assert.fail(String.format("ALF_152001: , %s, Expected Result: %s", "get nodes tags request with incorrect nodeId - " + docGuidInvalid,
                    "Expected error 404"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        // Status: 200 for invited network
        if (isAlfrescoVersionCloud(drone))
        {
            tags = getNodeTags(testUserAnotherDomain, DOMAIN, docGuid, null);
            assertNotNull(tags);
            for (Tag tag : tags.getList())
            {
                tagId = tag.getId();
                assertEquals(tag.getTag(), "tag1", "Received tag - " + tag);
            }
        }

    }

    @Test
    public void ALF_197401() throws Exception
    {
        // Get: tags/tagId
        Tag nodeTag = new Tag("tagNew");

        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            nodesClient.getAll("nodes", docGuid, "tags", tagId, null, "Could not retrieve the tag");
            Assert.fail(String.format("ALF_197701: , %s, Expected Result: %s", "GET nodes tag not allowed for tag", "Expected error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            nodesClient.getAll("nodes", docGuid, "tags", "tag1", null, "Could not retrieve the tag");
            Assert.fail(String.format("ALF_197401: , %s, Expected Result: %s", "GET nodes tag not allowed for tag and tag id", "Expected error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Post: tags
        // TODO: Add step to TestLink
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            nodesClient.create("nodes", docGuid, "tags", "tag1", nodeTag.toJSON().toJSONString(), "Could not create the tag");
            Assert.fail(String.format("ALF_197401: , %s, Expected Result: %s", "POST nodes tag not allowed for tag and tag id", "Expected error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Put: tags
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            nodesClient.update("nodes", docGuid, "tags", null, nodeTag.toJSON().toJSONString(), "Could not update the tag");
            Assert.fail(String.format("ALF_197401: , %s, Expected Result: %s", "PUT nodes tag not allowed for tags", "Expected error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Put: tags/tagId
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            nodesClient.update("nodes", docGuid, "tags", nodeTag.getNodeId(), null, "Could not update the tag");
            Assert.fail(String.format("ALF_197401: , %s, Expected Result: %s", "PUT nodes tag not allowed for tag and tag id", "Expected error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Delete: tags
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            nodesClient.remove("nodes", docGuid, "tags", null, "Could not delete the tag");
            Assert.fail(String.format("ALF_197401: , %s, Expected Result: %s", "DELETE nodes tag not allowed", "Expected error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Delete: tags/tagId
        publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
        HttpResponse response = nodesClient.remove("nodes", docGuid, "tags", tagId, "Could not remove the tag");
        assertEquals(response.getStatusCode(), 204, response.toString());
    }

    @Test
    public void ALF_197501() throws Exception
    {
        // Post Tag
        Tag nodeTag = new Tag("tag4");
        nodeTag = createTag(testUser, DOMAIN, docGuid2, nodeTag);

        // Check on Share UI
        ShareUser.login(drone, testUser);
        doclibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        FileDirectoryInfo fileDirInfo = doclibPage.getFileDirectoryInfo(fileName2);
        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, fileName2, "tags", "tag4", true), fileDirInfo.getTags().toString());

        // Post Tag: Invalid User
        try
        {
            createTag(testUserInvalid, DOMAIN, docGuid, nodeTag);
            Assert.fail(String.format("ALF_197501: , %s, Expected Result: %s", "create nodes tags request with incorrect auth", "Expected error 401"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }

        // Post Tag: Invalid nodeId
        try
        {
            createTag(testUser, DOMAIN, docGuidInvalid, nodeTag);
            Assert.fail(String.format("ALF_197501: , %s, Expected Result: %s", "get nodes tags request with incorrect nodeId - " + docGuid + "yy",
                    "Expected error 404"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test
    public void ALF_220801() throws Exception
    {
        Map<String, String> param = new HashMap<String, String>();
        try
        {
            param.put("maxItems", "a");
            getNodeTags(testUser, DOMAIN, docGuid, param);
            Assert.fail(String.format("ALF_220801: , %s, Expected Result: %s", "getNodeTags request with incorrect maxItems - " + param, "Expected error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("skipCount", "s");
            getNodeTags(testUser, DOMAIN, docGuid, param);
            Assert.fail(String.format("ALF_220801: , %s, Expected Result: %s", "getNodeTags request with incorrect skipCount - " + param, "Expected error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("maxItems", "-1");
            getNodeTags(testUser, DOMAIN, docGuid, param);
            Assert.fail(String.format("ALF_220801: , %s, Expected Result: %s", "getNodeTags request with incorrect maxItems - " + param, "Expected error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("skipCount", "-2");
            getNodeTags(testUser, DOMAIN, docGuid, param);
            Assert.fail(String.format("ALF_220801: , %s, Expected Result: %s", "getNodeTags request with incorrect skipCount - " + param, "Expected error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        param.clear();
        param.put("maxItems", "2147483647");
        param.put("skipCount", "1");
        ListResponse<Tag> response = getNodeTags(testUser, DOMAIN, docGuid, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(2147483647));
        assertEquals(response.getPaging().getSkipCount(), new Integer(1));

        param.clear();
        param.put("skipCount", "2");
        response = getNodeTags(testUser, DOMAIN, docGuid, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));

        param.clear();
        param.put("maxItems", "4");
        response = getNodeTags(testUser, DOMAIN, docGuid, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(4));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));

        response = getNodeTags(testUser, DOMAIN, docGuid, null);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));

        try
        {
            param.clear();
            param.put("skipCount", "a");
            param.put("maxItems", "b");
            getNodeTags(testUser, DOMAIN, docGuid, param);
            Assert.fail(String.format("ALF_220801: , %s, Expected Result: %s", "getNodeTags request with incorrect skipCount and maxItems - " + param,
                    "Expected error 400"));
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
            getNodeTags(testUser, DOMAIN, docGuid, param);
            Assert.fail(String.format("ALF_220801: , %s, Expected Result: %s", "getNodeTags request with incorrect skipCount and maxItems - " + param,
                    "Expected error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
    }

    @Test
    public void ALF_221901() throws Exception
    {

        Map<String, String> param = new HashMap<String, String>();
        param.put("maxItems", "2147483647");
        param.put("skipCount", "1");
        ListResponse<Tag> response = getNodeTags(testUser, DOMAIN, docGuid2, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(2147483647));
        assertEquals(response.getPaging().getSkipCount(), new Integer(1));
        assertFalse(response.getPaging().getHasMoreItems());

        param.clear();
        param.put("maxItems", "1");
        response = getNodeTags(testUser, DOMAIN, docGuid2, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(1));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
        assertTrue(response.getPaging().getHasMoreItems());
    }

    @Test
    public void ALF_152001() throws Exception
    {
        // Status: 200
        ListResponse<NodeRating> ratings = getNodeRatings(testUser, DOMAIN, docGuid, null);
        assertNotNull(ratings);
        for (NodeRating rating : ratings.getList())
        {
            if (rating.getId().equalsIgnoreCase("likes"))
            {
                assertEquals(rating.getMyRating(), true, "Received rating - " + rating);
            }
        }

        // Status: 401
        try
        {
            getNodeRatings(testUserInvalid, DOMAIN, docGuid, null);
            Assert.fail(String.format("ALF_152001: , %s, Expected Result: %s", "get nodes ratings request with incorrect auth", "Expected error 401"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }

        // Status: 404
        try
        {
            getNodeRatings(testUser, DOMAIN, docGuidInvalid, null);
            Assert.fail(String.format("ALF_152001: , %s, Expected Result: %s", "get nodes ratings request with incorrect nodeId - " + docGuid + "a",
                    "Expected error 404"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        // Status: 200 - for invited network.
        if (isAlfrescoVersionCloud(drone))
        {
            ratings = getNodeRatings(testUserAnotherDomain, DOMAIN, docGuid, null);
            boolean ratingFound = false;
            assertNotNull(ratings);
            for (NodeRating rating : ratings.getList())
            {
                if (rating.getId().equalsIgnoreCase("likes"))
                {
                    ratingFound = true;
                    break;
                }
            }
            assertTrue(ratingFound, "Received ratings - " + ratings);
        }
    }

    @Test
    public void ALF_197701() throws Exception
    {
        // Step 1
        NodeRating nodeRating = new NodeRating("likes", true);
        publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
        HttpResponse ratings = nodesClient.getAll("nodes", docGuid, "ratings", "likes", null, "Could not retrieve the rating");
        assertNotNull(ratings);
        assertEquals(ratings.getStatusCode(), 200, ratings.getJsonResponse().toJSONString());

        // Step 2 - Post: rating/ratingsId
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            nodesClient.create("nodes", docGuid, "ratings", "likes", nodeRating.toJSON().toJSONString(), "Could not create the rating");
            Assert.fail(String.format("ALF_197701: , %s, Expected Result: %s", "POST nodes rating not allowed for rating and rating id", "Expected error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Step 3 - Put: /ratings
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            nodesClient.update("nodes", docGuid, "ratings", null, nodeRating.toJSON().toJSONString(), "Could not update the rating");
            Assert.fail(String.format("ALF_197701: , %s, Expected Result: %s", "PUT nodes rating not allowed for rating and rating id", "Expected error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Step 4 - Put: /ratings/ratingId
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            nodesClient.update("nodes", docGuid, "ratings", nodeRating.getId(), null, "Could not update the rating");
            Assert.fail(String.format("ALF_197701: , %s, Expected Result: %s", "PUT nodes rating not allowed for rating and rating id", "Expected error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Step 4 - Delete: rating
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            nodesClient.remove("nodes", docGuid, "ratings", null, "Could not update the rating");
            Assert.fail(String
                    .format("ALF_197701: , %s, Expected Result: %s", "DELETE nodes rating not allowed for rating and rating id", "Expected error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Step 5 - Delete: rating/ratingsId
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            HttpResponse resp = nodesClient.remove("nodes", docGuid, "ratings", null, "Could not update the rating");
            assertTrue(resp.getStatusCode() == 405, String.format("Expected response: %s,  Actual Response: %s", 405, resp.getStatusCode()));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
        HttpResponse resp = nodesClient.remove("nodes", docGuid, "ratings", "likes", "Could not update the rating");
        assertTrue(resp.getStatusCode() == 204, String.format("Expected response: %s,  Actual Response: %s", 204, resp.getStatusCode()));
    }

    @Test
    public void ALF_197601() throws Exception
    {

        // Status: 200
        NodeRating nodeRating = new NodeRating("likes", true);
        createNodeRating(testUser, DOMAIN, docGuid2, nodeRating);

        // Check on Share
        ShareUser.login(drone, testUser);
        doclibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        FileDirectoryInfo fileDirInfo = doclibPage.getFileDirectoryInfo(fileName2);
        assertTrue(Integer.parseInt(fileDirInfo.getLikeCount()) > 0, fileDirInfo.getLikeCount());

        // Status: 401
        try
        {
            createNodeRating(testUserInvalid, DOMAIN, docGuid, nodeRating);
            Assert.fail(String.format("ALF_197601: , %s, Expected Result: %s", "create nodes ratings request with incorrect auth", "Expected error 401"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }

        // Status: 404
        try
        {
            createNodeRating(testUser, DOMAIN, docGuidInvalid, nodeRating);
            Assert.fail(String.format("ALF_197601: , %s, Expected Result: %s", "POST nodes ratings request with incorrect nodeId - " + docGuidInvalid,
                    "Expected error 404"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test
    public void ALF_220901() throws Exception
    {
        Map<String, String> param = new HashMap<String, String>();
        try
        {
            param.put("maxItems", "a");
            getNodeRatings(testUser, DOMAIN, docGuid, param);
            Assert.fail(String.format("ALF_220901: , %s, Expected Result: %s", "getNodeRatings request with incorrect maxItems - " + param,
                    "Expected error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("skipCount", "s");
            getNodeRatings(testUser, DOMAIN, docGuid, param);
            Assert.fail(String.format("ALF_220901: , %s, Expected Result: %s", "getNodeRatings request with incorrect skipCount - " + param,
                    "Expected error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("maxItems", "-1");
            getNodeRatings(testUser, DOMAIN, docGuid, param);
            Assert.fail(String.format("ALF_220901: , %s, Expected Result: %s", "getNodeRatings request with incorrect maxItems - " + param,
                    "Expected error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("skipCount", "-2");
            getNodeRatings(testUser, DOMAIN, docGuid, param);
            Assert.fail(String.format("ALF_220901: , %s, Expected Result: %s", "getNodeRatings request with incorrect skipCount - " + param,
                    "Expected error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        param.clear();
        param.put("maxItems", "2147483647");
        param.put("skipCount", "1");
        ListResponse<NodeRating> response = getNodeRatings(testUser, DOMAIN, docGuid, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(2147483647));
        assertEquals(response.getPaging().getSkipCount(), new Integer(1));

        param.clear();
        param.put("skipCount", "2");
        response = getNodeRatings(testUser, DOMAIN, docGuid, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));

        param.clear();
        param.put("maxItems", "4");
        response = getNodeRatings(testUser, DOMAIN, docGuid, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(4));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));

        response = getNodeRatings(testUser, DOMAIN, docGuid, null);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));

        try
        {
            param.clear();
            param.put("skipCount", "a");
            param.put("maxItems", "b");
            getNodeRatings(testUser, DOMAIN, docGuid, param);
            Assert.fail(String.format("ALF_220901: , %s, Expected Result: %s", "getNodeRatings request with incorrect skipCount and maxItems - " + param,
                    "Expected error 400"));
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
            getNodeRatings(testUser, DOMAIN, docGuid, param);
            Assert.fail(String.format("ALF_220901: , %s, Expected Result: %s", "getNodeRatings request with incorrect skipCount and maxItems - " + param,
                    "Expected error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
    }

    @Test
    public void ALF_222001() throws Exception
    {

        Map<String, String> param = new HashMap<String, String>();
        param.put("maxItems", "2147483647");
        param.put("skipCount", "1");
        ListResponse<NodeRating> response = getNodeRatings(testUser, DOMAIN, docGuid, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(2147483647));
        assertEquals(response.getPaging().getSkipCount(), new Integer(1));
        assertFalse(response.getPaging().getHasMoreItems());

        param.clear();
        param.put("maxItems", "1");
        response = getNodeRatings(testUser, DOMAIN, docGuid, param);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(1));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
        assertTrue(response.getPaging().getHasMoreItems());
    }

}
