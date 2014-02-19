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

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.rest.api.tests.PublicApiDateFormat;
import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.Comment;
import org.alfresco.rest.api.tests.client.data.Site;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.api.CommentsAPI;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.SitesAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for Comments apis implemented in alfresco-remote-api.
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne", "Cloud2" })
public class CommentsAPITests extends CommentsAPI
{
    private static final String TEST_COMMENT = "Test Comment";
    private String testName;
    private String testUser;
    private String anotherTestUser;
    private String testUserInvalid;
    private String siteName;
    private String fileName;
    private String fileName2;
    private String docGuid;
    private String docGuid2;
    private String siteRef;

    private DocumentLibraryPage doclibPage;
    private DocumentDetailsPage docDetails;
    private Comment comment;
    private String invalidNoderef;
    private int commentCount;
    private static Log logger = LogFactory.getLog(CommentsAPITests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName();

        testUser = getUserNameFreeDomain(testName);
        anotherTestUser = getUserNameFreeDomain("another" + testName);
        testUserInvalid = "invalid" + testUser;

        siteName = getSiteName(testName) + System.currentTimeMillis();
        fileName = getFileName(testName + "_1") + System.currentTimeMillis();
        fileName2 = getFileName(testName + "_2") + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, anotherTestUser);

        ShareUser.login(drone, testUser);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC, true).render();


        Site site = new SitesAPI().getSiteById(testUser, DOMAIN, siteName);
        siteRef = site.getGuid();

        doclibPage = ShareUser.uploadFileInFolder(drone, new String[] { fileName });

        docGuid = ShareUser.getGuid(drone, fileName);


        ShareUser.uploadFileInFolder(drone, new String[] { fileName2 });
        docGuid2 = ShareUser.getGuid(drone, fileName2);

        docDetails = doclibPage.selectFile(fileName).render();
        docDetails.addComment("<p>" + TEST_COMMENT + "</p>");
        docDetails.addComment("<p>" + TEST_COMMENT + "_1 </p>");
        docDetails.addComment("<p>" + TEST_COMMENT + "_2 </p>");
        docDetails.addComment("<p>" + TEST_COMMENT + "_3 </p>");
        
        commentCount = 2;

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, anotherTestUser, siteName, UserRole.CONSUMER);

        invalidNoderef = docGuid2 + "yy";
    }

    @Test
    public void ALF_151701() throws Exception
    {
        ListResponse<Comment> comments = getNodeComments(testUser, DOMAIN, null, docGuid);

        DateFormat format = PublicApiDateFormat.getDateFormat();
        // Status: 200
        assertNotNull(comments);
        comment = comments.getList().get(0);
        logger.info("Received comments - " + comment + " for doc - " + docGuid);
        assertTrue(comments.getList().get(0).getContent().contains(TEST_COMMENT), "Received comment - " + comments.getList());

        if(comments.getList().size() > 1){
            Date commentDate0 = (Date) format.parse(comments.getList().get(0).getCreatedAt());
            Date commentDate1 = (Date) format.parse(comments.getList().get(1).getCreatedAt());
            assertTrue(commentDate0.after(commentDate1), comments.getList().get(1) + " should be created before " + comments.getList().get(0));
        }




        // Status: 401
        try
        {
            getNodeComments(testUserInvalid, DOMAIN, null, docGuid);
            Assert.fail(String.format("ALF_151701: , %s, Expected Result: %s", "get nodes comments request with incorrect auth", "Error 401"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }

        // Status: 404
        try
        {
            getNodeComments(testUser, DOMAIN, null, invalidNoderef);
            Assert.fail(String.format("ALF_151701: , %s, Expected Result: %s", "get nodes comments request with incorrect nodeId - " + invalidNoderef,
                    "Error 404"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        // Status: 400
        try
        {
            getNodeComments(testUser, DOMAIN, null, siteRef);
            Assert.fail(String.format("ALF_151701: , %s, Expected Result: %s", "get nodes comments request with incorrect node id type", "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
    }

    @Test
    public void ALF_197101() throws Exception
    {
        
        publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
        HttpResponse comments = commentsClient.getAll("nodes", docGuid, "comments", null, null, "Could not retrieve the comment");
        assertNotNull(comments);
        assertTrue(comments.getStatusCode() == 200, "Response code - " + comments.getStatusCode());        
        
        // Get Comment
        ListResponse<Comment> nodeComments = getNodeComments(testUser, DOMAIN, null, docGuid);
        assertNotNull(comments);
        comment = nodeComments.getList().get(0);
        
        // Not allowed: GET /comments/commentId
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            
            commentsClient.getSingle("nodes", docGuid, "comments", comment.getId(), "Could not get the comment");
            Assert.fail(String.format("ALF_197101: , %s, Expected Result: %s", "GET comments-commentId not allowed", 405));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Not allowed: POST /comments/commentId
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            commentsClient.create("nodes", docGuid, "comments", comment.getId(), null, "Could not create the comment");
            Assert.fail(String.format("ALF_197101: , %s, Expected Result: %s", "POST comment not allowed", "Error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Not allowed: PUT /comments
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            commentsClient.update("nodes", docGuid, "comments", null, null, "Could not create the comment");
            Assert.fail(String.format("ALF_197101: , %s, Expected Result: %s", "PUT comment not allowed", "Error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Not allowed: PUT /comments/commentId
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            commentsClient.create("nodes", docGuid, "comments", comment.getId(), null, "Could not update the comment");
            Assert.fail(String.format("ALF_197101: , %s, Expected Result: %s", "PUT comment not allowed. Bad request.", "Error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Not allowed: DELETE /comments
        try
        {
            publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
            commentsClient.remove("nodes", docGuid, "comments", null, "Could not delete the comment");
            Assert.fail(String.format("ALF_197101: , %s, Expected Result: %s", "DELETE comment not allowed", "Error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }

        // Allowed: DELETE /comments/commentId
        publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));
        HttpResponse response = commentsClient.remove("nodes", comment.getNodeId(), "comments", comment.getId(), "Could not remove the comment");
        assertEquals(response.getStatusCode(), 204, response.toString());
    }

    @Test
    public void ALF_218401() throws Exception
    {
        DateFormat format = PublicApiDateFormat.getDateFormat();
        ListResponse<Comment> comments = getNodeComments(testUser, DOMAIN, null, docGuid);
        assertNotNull(comments);
        Comment oldComment = comments.getList().get(0);
        String newComment = "new comment1";
        oldComment.setContent("<p>" + newComment + "</p>");
        Date beforeModDate = (Date) format.parse(oldComment.getModifiedAt());

        // Status: 200
        Comment updatedComment = updateNodeComment(testUser, DOMAIN, oldComment.getNodeId(), oldComment.getId(), oldComment);

        Date updateDate = (Date) format.parse(updatedComment.getModifiedAt());
        assertTrue(updateDate.after(beforeModDate), updateDate + " should be created after " + beforeModDate);

        ShareUser.login(drone, testUser);
        doclibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        docDetails = doclibPage.selectFile(fileName).render();

        List<String> commentsOnShare = docDetails.getComments();
        assertTrue(commentsOnShare.contains(newComment), "New comment should be in comments - " + docDetails.getComments());

        // Get Comment
        ListResponse<Comment> nodeComments = getNodeComments(testUser, DOMAIN, null, docGuid);
        assertNotNull(comments);
        comment = nodeComments.getList().get(0);
        
        // Status: 404
        try
        {
            updateNodeComment(testUser, DOMAIN, invalidNoderef, comment.getId(), updatedComment);
            Assert.fail(String.format("ALF_218401: , %s, Expected Result: %s", "PUT node comment request with incorrect noderef", "Error 404"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        // Status: 401
        try
        {
            updateNodeComment(testUserInvalid, DOMAIN, docGuid2, comment.getId(), updatedComment);
            Assert.fail(String.format("ALF_218401: , %s, Expected Result: %s", "PUT node comment request with incorrect auth", "Error 401"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }

        // Status: 403
        try
        {
            updateNodeComment(anotherTestUser, DOMAIN, updatedComment.getNodeId(), updatedComment.getId(), updatedComment);
            Assert.fail(String.format("ALF_218401: , %s, Expected Result: %s", "PUT node comment request with incorrect user", "Error 403"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 403);
        }
    }

    @Test
    public void ALF_151801() throws Exception
    {
        Comment nodeComment = new Comment("New Title", "<p>new comment1</p>");
        createNodeComment(testUser, DOMAIN, docGuid2, nodeComment);
        nodeComment = new Comment("New Title2", "<p>new comment2</p>");
        createNodeComment(testUser, DOMAIN, docGuid2, nodeComment);
        nodeComment = new Comment("New Title3", "<p>new comment3</p>");
        createNodeComment(testUser, DOMAIN, docGuid2, nodeComment);

        ShareUser.login(drone, testUser);

        doclibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        docDetails = doclibPage.selectFile(fileName2).render();

        List<String> comments = docDetails.getComments();
        assertTrue(comments.contains("new comment2"), "New comment should be in comments - " + comments);
        
        // Invalid Auth: 401
        try
        {
            createNodeComment(testUserInvalid, DOMAIN, docGuid2, nodeComment);
            Assert.fail(String.format("ALF_151801: , %s, Expected Result: %s", "Post node comment request with incorrect auth", "Error 401"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }

        // Invalid Node: 404
        try
        {
            createNodeComment(testUser, DOMAIN, invalidNoderef, nodeComment);
            Assert.fail(String.format("ALF_151801: , %s, Expected Result: %s", "Post node comment request with incorrect nodeId - " + invalidNoderef,
                    "Error 404"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        // Invalid Node Type: Not Document or Folder
        try
        {
            Site site = sitesProxy.getSite(siteName);
            createNodeComment(testUser, DOMAIN, site.getGuid(), nodeComment);
            Assert.fail(String.format("ALF_151801: , %s, Expected Result: %s", "Post node comment request with incorrect node ref type", "Error 405"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }
    }

    @Test
    public void ALF_220510() throws Exception
    {
        Map<String, String> param = new HashMap<String, String>();
        try
        {
            param.put("maxItems", "a");
            getNodeComments(testUser, DOMAIN, param, docGuid2);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getNodecomments request with incorrect maxItems - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
        try
        {
            param.clear();
            param.put("skipCount", "s");
            getNodeComments(testUser, DOMAIN, param, docGuid2);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getNodecomments request with incorrect skipCount - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("maxItems", "-1");
            getNodeComments(testUser, DOMAIN, param, docGuid2);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getNodecomments request with incorrect maxItems - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        try
        {
            param.clear();
            param.put("skipCount", "-2");
            getNodeComments(testUser, DOMAIN, param, docGuid2);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getNodecomments request with incorrect skipCount - " + param, "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }

        param.clear();
        param.put("maxItems", "2147483647");
        param.put("skipCount", "1");
        ListResponse<Comment> response = getNodeComments(testUser, DOMAIN, param, docGuid2);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(2147483647));
        assertEquals(response.getPaging().getSkipCount(), new Integer(1));

        param.clear();
        param.put("skipCount", "2");
        response = getNodeComments(testUser, DOMAIN, param, docGuid2);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));
        assertEquals(response.getPaging().getSkipCount(), new Integer(2));

        param.clear();
        param.put("maxItems", "4");
        response = getNodeComments(testUser, DOMAIN, param, docGuid2);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(4));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));

        response = getNodeComments(testUser, DOMAIN, null, docGuid2);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(100));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));

        try
        {
            param.clear();
            param.put("skipCount", "a");
            param.put("maxItems", "b");
            getNodeComments(testUser, DOMAIN, param, docGuid2);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getNodecomments request with incorrect skipCount and maxItems - " + param,
                    "Error 400"));
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
            getNodeComments(testUser, DOMAIN, param, docGuid2);
            Assert.fail(String.format("Test: , %s, Expected Result: %s", "getNodecomments request with incorrect skipCount and maxItems - " + param,
                    "Error 400"));
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 400);
        }
    }

    @Test
    public void ALF_221701() throws Exception
    {
        ShareUser.login(drone, testUser);

        doclibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        docDetails = doclibPage.selectFile(fileName).render();

        commentCount = docDetails.getCommentCount();

        Map<String, String> param = new HashMap<String, String>();
        param.put("maxItems", "1");
        ListResponse<Comment> response = getNodeComments(testUser, DOMAIN, param, docGuid);
        assertNotNull(response);
        assertEquals(response.getPaging().getMaxItems(), new Integer(1));
        assertEquals(response.getPaging().getSkipCount(), new Integer(0));
        
        assertTrue(response.getPaging().getHasMoreItems());

        param.clear();

        param.put("maxItems", "" + commentCount);
        response = getNodeComments(testUser, DOMAIN, param, docGuid);
        assertNotNull(response.getList().size());
        assertEquals(response.getPaging().getMaxItems(), new Integer(commentCount));
        assertFalse(response.getPaging().getHasMoreItems());

        param.clear();

        param.put("maxItems", "" + (commentCount + 1));
        response = getNodeComments(testUser, DOMAIN, param, docGuid);
        assertNotNull(response.getList().size());
        assertEquals(response.getPaging().getMaxItems(), new Integer(commentCount + 1));
        assertFalse(response.getPaging().getHasMoreItems());
    }

}
