package org.alfresco.share.sanity;

/**
 * Created with IntelliJ IDEA.
 * User: maryia.zaichanka
 * Date: 4/2/14
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.webdrone.WebDroneImpl;

import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.*;

import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.joda.time.DateTime;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

import static org.alfresco.po.share.site.document.TreeMenuNavigation.DocumentsMenu.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This class contains the sanity tests for document library
 * 
 * @author Zaichanka Maryia
 */

@Listeners(FailedTestListener.class)
public class DocLibTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(DocLibTest.class);
    protected String testUser;
    protected String folderName;
    protected String testName;
    protected String fileName;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void ALF_3056() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        File sampleFile = SiteUtil.prepareFile();
        String fileName = getFileName(testName) + ".txt";
        String contentName = "cont" + getFileName(testName);
        String[] fileInfo = { fileName, DOCLIB };

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Upload template file
        String folderPath = REPO + SLASH + "Data Dictionary" + SLASH + "Node Templates";
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, folderPath);
        ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        String templateFile = sampleFile.getName();

        // Upload template folder
        folderPath = REPO + SLASH + "Data Dictionary" + SLASH + "Space Templates";
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, folderPath);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);

        // Create user
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

        // Open Document Library
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload file
        ShareUser.uploadFileInFolder(drone, fileInfo);
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "File isn't uploaded");

        String[] pref = { ".txt", ".html", ".xml" };

        for (String files : pref)
        {
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(contentName + files);
            contentDetails.setContent(contentName);
            if (files.equals(pref[0]))
            {
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
            }
            if (files.equals(pref[1]))
            {
                ShareUser.createContent(drone, contentDetails, ContentType.HTML);
            }
            if (files.equals(pref[2]))
            {
                ShareUser.createContent(drone, contentDetails, ContentType.XML);
            }

            DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, contentName + files).render();
            Assert.assertEquals(detailsPage.getDocumentTitle(), contentName + files, "File isn't created");
            // Assert.assertTrue(detailsPage.isFlashPreviewDisplayed(), "Preview isn't correctly displayed on details page");
           // Assert.assertTrue(detailsPage.getPreviewerClassName().endsWith("PdfJs"));

        }

        // Create document from template
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        documentLibraryPage.createContentFromTemplateHover(templateFile).render();

        drone.getCurrentPage().render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(templateFile), "Document isn't created");

        // Click on Create Folder link (the link is displayed when the folder is empty) and create folder
        ShareUserSitePage.createFolder(drone, folderName + 1, folderName + 1).render();
        documentLibraryPage.selectFolder(folderName + 1).render();
        NewFolderPage newFolderPage = documentLibraryPage.getNavigation().selectCreateAFolder().render();
        newFolderPage.createNewFolder(folderName + 2, folderName + 2, folderName + 2).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(folderName + 2), "Folder isn't created");

        // Click Create > Folder and create a folder
        ShareUserSitePage.createFolder(drone, folderName + 3, folderName + 3).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(folderName + 3), "Folder isn't created");

        // Create folder from template
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        documentLibraryPage.createFolderFromTemplateHover(folderName).render();
        drone.getCurrentPage().render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(folderName), "Folder isn't created");

        ShareUser.logout(drone);
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void ALF_3100() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName) + ".txt";
        String copyFileName = "Copy of " + 1 + fileName;
        String workFlowName = testName + System.currentTimeMillis() + "-1-WF";
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
        String filePath = downloadDirectory + copyFileName;
        String tag = getRandomString(5);
        String content = "test content";
        String catAspect = "Categories";
        String dublinAspect = "Publisher";
        String moveIntoFolder = 1 + folderName;
        String contentName = "cont" + getFileName(testName);

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        String testUser2 = "WF" + getTestName();
        String[] testUserInfo2 = new String[] { testUser2 };

        String[][] users = { testUserInfo, testUserInfo2 };

        try
        {

            customDrone = ((WebDroneImpl) ctx.getBean(WebDroneType.DownLoadDrone.getName()));
            dronePropertiesMap.put(customDrone, (ShareTestProperty) ctx.getBean("shareTestProperties"));
            maxWaitTime = ((WebDroneImpl) customDrone).getMaxPageRenderWaitTime();

            // Create 2 users
            for (String[] user : users)
            {
                CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, user);
            }

            // Login with the first user
            ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(customDrone, siteName, SITE_VISIBILITY_PUBLIC);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render(maxWaitTime);

            // Create any document
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(fileName);
            contentDetails.setContent(content);
            contentDetails.setDescription(fileName);
            ShareUser.createContent(customDrone, contentDetails, ContentType.PLAINTEXT);

            // Create 3 types of content
            String[] pref = { ".txt", ".html", ".xml" };

            for (String files : pref)
            {
                contentDetails = new ContentDetails();
                contentDetails.setName(contentName + files);
                contentDetails.setContent(contentName);
                if (files.equals(pref[0]))
                {
                    ShareUser.createContent(customDrone, contentDetails, ContentType.PLAINTEXT);
                }
                if (files.equals(pref[1]))
                {
                    ShareUser.createContent(customDrone, contentDetails, ContentType.HTML);
                }
                if (files.equals(pref[2]))
                {
                    ShareUser.createContent(customDrone, contentDetails, ContentType.XML);
                }

            }

            // Invite user2 to the created site
            ShareUserMembers.inviteUserToSiteWithRole(customDrone, testUser, testUser2, siteName, UserRole.COLLABORATOR);

            // Open document Details Page
            // ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName).render();
            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render(maxWaitTime);

            // Mark document as favourite
            detailsPage.selectFavourite().render();
            Assert.assertTrue(detailsPage.isFavourite(), "The document isn't favourite");

            // Like / Unlike the document
            detailsPage.selectLike().render();
            Assert.assertTrue(detailsPage.isLiked(), "The document isn't liked");
            Assert.assertEquals(detailsPage.getLikeCount(), "1", "The number of likes didn't increase");

            detailsPage.selectLike().render();
            Assert.assertFalse(detailsPage.isLiked(), "The document is liked");
            Assert.assertEquals(detailsPage.getLikeCount(), "0", "The number of likes didn't decrease");

            // Comment on the document via Comment Link
            AddCommentForm addCommentForm = detailsPage.selectAddComment();

            String comment = getRandomString(5);
            TinyMceEditor tinyMceEditor = addCommentForm.getTinyMceEditor();
            tinyMceEditor.setText(comment);
            Assert.assertEquals(tinyMceEditor.getText(), comment, "Text didn't enter in MCE box or didn't correct.");

            addCommentForm.clickAddCommentButton().render();
            Assert.assertEquals(detailsPage.getComments().size(), 1, "Comment isn't added");

            // Add comment via Add comment button
            String comment2 = getRandomString(5);
            detailsPage.addComment(comment2);
            detailsPage.render();
            Assert.assertEquals(detailsPage.getComments().size(), 2, "Comment isn't added");

            // Edit the comment
            String newComment = getRandomString(5);
            detailsPage.editComment(comment2, newComment);
            detailsPage.saveEditComments();
            detailsPage.render();
            Assert.assertTrue(detailsPage.isCommentCorrect(newComment), "Comment isn't changed");

            // Delete the comment
            detailsPage.removeComment(newComment).render();
            documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();
            FileDirectoryInfo doc = documentLibraryPage.getFileDirectoryInfo(fileName);
            Assert.assertTrue(doc.getCommentsCount() == 1);

            // Click Download from the document actions
            detailsPage = documentLibraryPage.selectFile(fileName).render();
            detailsPage.selectDownloadFromActions(null).render();

            detailsPage.waitForFile(downloadDirectory + fileName);

            List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
            Assert.assertTrue(extractedChildFilesOrFolders.contains(fileName), "The file isn't downloaded");

            // Click Download button
            detailsPage.selectDownload(null).render();

            detailsPage.waitForFile(downloadDirectory + fileName);

            extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
            Assert.assertTrue(extractedChildFilesOrFolders.contains(fileName), "The file isn't downloaded");

            // Click view in browser
            ShareUser.openDocumentLibrary(customDrone).render();
            String mainWindow = customDrone.getWindowHandle();
            documentLibraryPage.getFileDirectoryInfo(fileName).selectViewInBrowser();
            String htmlSource = ((WebDroneImpl) customDrone).getDriver().getPageSource();
            Assert.assertTrue(htmlSource.contains(content), "Document isn't opened in a browser");
            customDrone.closeWindow();
            customDrone.switchToWindow(mainWindow);

            // Edit Properties for folder and add any tag to it
            documentLibraryPage = customDrone.getCurrentPage().render(maxWaitTime);
            detailsPage = documentLibraryPage.selectFile(fileName).render();
            EditDocumentPropertiesPage editProperties = detailsPage.selectEditProperties().render();
            editProperties.setName(1 + fileName);
            TagPage tagPage = editProperties.getTag().render();
            Assert.assertTrue(tagPage.isTagInputVisible(), "Tag window didn't open");
            tagPage = tagPage.enterTagValue(tag).render();
            tagPage.clickOkButton();
            editProperties.selectSave().render();

            detailsPage = customDrone.getCurrentPage().render();
            Assert.assertTrue(detailsPage.getTagList().contains(tag), "Tag hasn't added");

            // Upload New Version, e.g. minor
            ShareUser.uploadNewVersionOfDocument(customDrone, 1 + fileName, "New version uploaded.", false).render();
            documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();
            detailsPage = documentLibraryPage.selectFile(1 + fileName).render();

            Assert.assertTrue(detailsPage.getDocumentVersion().equals("1.1"), "Version isn't changed");

            // Edit content Inline (for txt, html,xml)
            String[] pref2 = { ".txt", ".xml", ".html" };

            contentDetails = new ContentDetails();
            for (String files : pref2)
            {
                contentDetails.setName(contentName + files);
                contentDetails.setContent(contentName);

                ShareUser.openDocumentLibrary(customDrone);
                detailsPage = ShareUser.openDocumentDetailPage(customDrone, contentName + files).render();
                Assert.assertEquals(detailsPage.getDocumentTitle(), contentName + files, "File isn't created");

                if (files.equals(pref2[2]))
                {
                    InlineEditPage inlineEditPage = detailsPage.selectInlineEdit();
                    EditHtmlDocumentPage editDocPage = (EditHtmlDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.HTML);
                    editDocPage.setName(contentName + 1 + files);
                    editDocPage.saveText();
                    customDrone.getCurrentPage().render();
                    detailsPage = customDrone.getCurrentPage().render();
                    Assert.assertEquals(detailsPage.getDocumentTitle(), contentName + 1 + files, "File isn't edited inline");

                }
                else
                {
                    EditTextDocumentPage inlineEditPage = detailsPage.selectInlineEdit().render();
                    contentDetails.setName(contentName + 1 + files);
                    detailsPage = inlineEditPage.save(contentDetails).render();
                    detailsPage.render();
                    Assert.assertEquals(detailsPage.getDocumentTitle(), contentName + 1 + files, "File isn't edited inline");
                }

            }

            // Edit document offline
            documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();
            String docLibPage = customDrone.getCurrentUrl();
            detailsPage = documentLibraryPage.selectFile(1 + fileName).render();
            DocumentEditOfflinePage editOffline = detailsPage.selectEditOffLine(null).render(maxWaitTime);
            Assert.assertTrue(detailsPage.getContentInfo().equals("This document is locked by you for offline editing."), "Document isn't locked while");

            // Click View Original Document action for the document
            editOffline.selectViewOriginalDocument().render(maxWaitTime);
            Assert.assertTrue(editOffline.getContentInfo().equals("This document is locked by you."), "Document isn't locked while");
            Assert.assertTrue(editOffline.isViewWorkingCopyDisplayed(), "Working copy link isn't present");

            // Click View Working Copy action
            editOffline.selectViewWorkingCopy().render();
            Assert.assertTrue(editOffline.getContentInfo().equals("This document is locked by you for offline editing."), "Document isn't locked while");

            // Click Cancel Editing
            editOffline.selectCancelEditing().render();
            Assert.assertFalse(editOffline.isViewWorkingCopyDisplayed(), "Editing isn't canceled");

            // Copy the document to any place
//            customDrone.findAndWait(By.cssSelector(DocumentAction.COPY_TO.getDocumentAction(DocumentAction.DetailsPageType.DOCUMENT))).click();
//            CopyOrMoveContentPage copyPage = customDrone.getCurrentPage().render();
//            Assert.assertTrue(copyPage.isShareDialogueDisplayed(), "Copy dialogue isn't opened");
//            copyPage.selectSite(siteName).render();
//            copyPage.selectOkButton().render(maxWaitTime);
//            documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();
            documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();
            FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(1 + fileName);
            CopyOrMoveContentPage copyPage = fileInfo.selectCopyTo().render();
            copyPage.selectSite(siteName).render();
            copyPage.selectOkButton().render(maxWaitTime);

            Assert.assertTrue(documentLibraryPage.isFileVisible(copyFileName), "Document isn't copied");

            // Move the document to any place
            ShareUserSitePage.createFolder(customDrone, moveIntoFolder, moveIntoFolder).render(maxWaitTime);
//            documentLibraryPage.selectFile(1 + fileName).render();
//            customDrone.findAndWait(By.cssSelector(DocumentAction.MOVE_TO.getDocumentAction(DocumentAction.DetailsPageType.DOCUMENT))).click();
//            customDrone.getCurrentPage().render();
//            webDriverWait(customDrone, 7000);
//            CopyOrMoveContentPage movePage = customDrone.getCurrentPage().render();
//            Assert.assertTrue(movePage.isShareDialogueDisplayed(), "Move dialogue isn't opened");
//            movePage.selectPath(moveIntoFolder).render();
//            movePage.selectOkButton().render();
//            documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();
            fileInfo = documentLibraryPage.getFileDirectoryInfo(1 + fileName);
            CopyOrMoveContentPage movePage = fileInfo.selectMoveTo().render();
            movePage.selectPath(moveIntoFolder).render();
            movePage.selectOkButton().render(maxWaitTime);
            Assert.assertFalse(documentLibraryPage.isFileVisible(1 + fileName), "Document isn't moved");

            documentLibraryPage = documentLibraryPage.selectFolder(moveIntoFolder).render();
            documentLibraryPage.render(maxWaitTime);
            customDrone.getCurrentPage().render(maxWaitTime);
            Assert.assertTrue(documentLibraryPage.isFileVisible(1 + fileName), "Document isn't moved");

            // Start Workflow for the document
            detailsPage = documentLibraryPage.selectFile(1 + fileName).render();
            StartWorkFlowPage startWorkFlowPage = ShareUserWorkFlow.selectStartWorkFlowFromDetailsPage(customDrone).render();

            NewWorkflowPage newWorkflowPage = startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();

            List<String> reviewers = new ArrayList<String>();
            reviewers.add(testUser2);

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
            formDetails.setMessage(workFlowName);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);

            newWorkflowPage.startWorkflow(formDetails).render();

            // Check the document is marked with icon
            Assert.assertTrue(detailsPage.isPartOfWorkflow(), "Workflow isn't started");

            // Search for some user (a member of the site), add him with some other permissions
            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(testUser2);
            userProfile.setlName(DEFAULT_LASTNAME);

            detailsPage.selectAction(DocumentAction.MANAGE_PERMISSION_DOC).render();
            ManagePermissionsPage managePermissions = customDrone.getCurrentPage().render();

            ManagePermissionsPage.UserSearchPage userSearchPage = managePermissions.selectAddUser().render();
            managePermissions = userSearchPage.searchAndSelectUser(userProfile).render();
            managePermissions.setAccessType(userProfile, UserRole.COORDINATOR);

            managePermissions = ShareUser.returnManagePermissionPage(customDrone, 1 + fileName);
            Assert.assertNotNull(managePermissions.getExistingPermission(testUser2), "User isn't presented in Manage permissions page");
            Assert.assertTrue(managePermissions.getExistingPermission(testUser2).equals(UserRole.COORDINATOR), "The role isn't changed");
            managePermissions.selectCancel().render();

            // Manage Aspects, add some aspect, remove some aspect
            // Add Classifiable aspect
            List<DocumentAspect> aspects = Arrays.asList(DocumentAspect.CLASSIFIABLE);
            detailsPage = (DocumentDetailsPage) ShareUser.addAspects(customDrone, aspects);
            Map<String, Object> properties = detailsPage.getProperties();
            Assert.assertTrue("(None)".equalsIgnoreCase((String) properties.get(catAspect)), "Aspect isn't added");

            aspects = Arrays.asList(DocumentAspect.DUBLIN_CORE);
            detailsPage = (DocumentDetailsPage) ShareUser.addAspects(customDrone, aspects);
            properties = detailsPage.getProperties();
            Assert.assertTrue("(None)".equalsIgnoreCase((String) properties.get(dublinAspect)), "Aspect isn't added");

            // Remove Dublin core aspect
            aspects = Arrays.asList(DocumentAspect.DUBLIN_CORE);
            detailsPage = (DocumentDetailsPage) ShareUser.removeAspects(customDrone, aspects);
            customDrone.refresh();
            properties = detailsPage.getProperties();
            Assert.assertFalse("(None)".equalsIgnoreCase((String) properties.get(dublinAspect)), "Aspect isn't removed");

            // Change Type for the folder
            properties = detailsPage.getProperties();

            // Click 'Change Type' action
            detailsPage = customDrone.getCurrentPage().render();

            ChangeTypePage changeTypePage = detailsPage.selectChangeType().render();
            assertTrue(changeTypePage.isChangeTypeDisplayed());

            List<String> types = changeTypePage.getTypes();
            assertTrue(types.contains("Select type..."));

            // Select any type if present
            int typeCount = types.size();

            if (typeCount > 1)
            {

                String randomType = types.get(1);
                changeTypePage.selectChangeType(randomType);

                // Click Change Type again, select any type and click OK
                changeTypePage.selectSave().render();
                Map<String, Object> propertiesAfter = detailsPage.getProperties();
                Assert.assertNotSame(properties, propertiesAfter, "Document type hasn't changed");

            }
            else
            {
                changeTypePage.selectCancel();
                Map<String, Object> propertiesAfter = detailsPage.getProperties();
                assertEquals(properties, propertiesAfter, "Document type has changed, although shouldn't");
            }

            // Click Edit Icon near Tags section. Add some tags and categories
            EditDocumentPropertiesPage editPage = detailsPage.clickEditTagsIcon(false).render();
            tagPage = editPage.getTag().render();
            tagPage = tagPage.enterTagValue(tag + 1).render();
            tagPage.clickOkButton();
            CategoryPage catPage = editPage.getCategory().render();
            String catLanguages = customDrone.getValue("category.languages");
            catPage.addCategories(Arrays.asList(catLanguages)).render();
            List<String> categories = catPage.getAddedCatgoryList();
            editPage = catPage.clickOk().render();

            detailsPage = editPage.selectSave().render();
            List<String> tagsList = detailsPage.getTagList();
            Assert.assertEquals(tagsList.size(), 2, "New tag isn't added");
            Assert.assertTrue(tagsList.contains(tag + 1), "New tag isn't added");

            Assert.assertTrue(categories.contains(catLanguages), "New category isn't added");
            Assert.assertEquals(categories.size(), 1, "New category isn't added");

            // Click Edit icon near Properties section. Edit some properties and click Cancel
            detailsPage.clickEditPropertiesIcon(false);
            editPage = customDrone.getCurrentPage().render();
            editPage.setName(3 + fileName);
            editPage.clickOnCancel();
            Assert.assertFalse(detailsPage.getProperties().containsValue(3 + fileName), "Folder name has changed");

            // Click Manage Permissions icon. Search for some user (a member of the site), add him with some other permissions
            documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();
            detailsPage = documentLibraryPage.selectFile(copyFileName).render();
            Assert.assertFalse(detailsPage.isPermissionsPanelPresent(), "Manage permission panel is present, ACE-436");

            // Click Start Workflow icon. Start any workflow
            startWorkFlowPage = detailsPage.selectStartWorkFlowIcon().render();

            newWorkflowPage = startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();

            reviewers = new ArrayList<String>();
            reviewers.add(testUser2);

            formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
            formDetails.setMessage(workFlowName);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);

            newWorkflowPage.startWorkflow(formDetails).render();
            Assert.assertTrue(detailsPage.isPartOfWorkflow(), "Workflow isn't started");

            // Verify the version history
            Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent(), "Version history panel isn't present, ACE-1628");

            // Upload New version form versions history
            String fileContents = "New File being created via newFile";
            File newFileName = newFile(DATA_FOLDER + (copyFileName), fileContents);

            UpdateFilePage updatePage = detailsPage.selectUploadNewVersion().render();
            updatePage.selectMajorVersionChange();

            updatePage.uploadFile(newFileName.getCanonicalPath());
            detailsPage = updatePage.submit().render();

            Assert.assertTrue(detailsPage.getDocumentVersion().equals("2.0"), "Version isn't changed");

            // Click Revert for any old version
            RevertToVersionPage revertToVersionPage = detailsPage.selectRevertToVersion("1.0").render();
            revertToVersionPage.selectMinorVersionChange();
            detailsPage = revertToVersionPage.submit().render();
            Assert.assertTrue(detailsPage.getDocumentVersion().equals("2.1"), "New minor version isn't created");

            // Click Download for any old version
            detailsPage.selectDownloadPreviousVersion("2.0");

            // Check the file is downloaded successfully
            detailsPage.waitForFile(maxWaitTime, downloadDirectory + copyFileName);
            webDriverWait(customDrone, 5000);
            String body = FileUtils.readFileToString(new File(filePath));
            if (body.length() == 0)
            {
                body = FileUtils.readFileToString(new File(filePath));
            }

            Assert.assertTrue(body.contains(String.format("New File being created via newFile")), "Download failed");

            // Click View Properties
            detailsPage.selectViewProperties("1.0");
            ViewPropertiesPage propPage = FactorySharePage.resolvePage(customDrone).render();
            Assert.assertTrue(propPage.isShareDialogueDisplayed(), "View properties dialog isn't opened");

            String version = propPage.getVersionButtonTitle();
            Assert.assertEquals(version, "Version: 1.0");
            for (int i = 1; i < 4; i++)
            {
                propPage.selectOtherVersion(true);
                version = propPage.getVersionButtonTitle();
                if (i == 1)
                    Assert.assertEquals(version, "Version: 2.0", "View properties dialog isn't opened for specific version");
                else if (i == 3)
                    Assert.assertEquals(version, "Version: 2.1", "View properties dialog isn't opened for specific version");

            }

            propPage.clickClose().render();

            // Delete the document. Confirm deletion
            detailsPage = customDrone.getCurrentPage().render();
            detailsPage.delete();

            ShareUser.openUserDashboard(customDrone);
            ShareUser.openSiteDashboard(customDrone, siteName);
            ShareUser.openDocumentLibrary(customDrone).render();

            documentLibraryPage = documentLibraryPage.selectFolder(moveIntoFolder).render();
            Assert.assertFalse(documentLibraryPage.isFileVisible(copyFileName), "The document isn't deleted");

            // Click Download from the document actions
            detailsPage = documentLibraryPage.selectFile(fileName).render();
            detailsPage.selectDownloadFromActions(null).render();

            detailsPage.waitForFile(downloadDirectory + fileName);

            extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
            Assert.assertTrue(extractedChildFilesOrFolders.contains(fileName), "The file isn't downloaded");

            // Go to User Dashboard activities and ensure all activities are displayed
            ShareUser.openUserDashboard(customDrone);
            String activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_COMMENT_DELETED + FEED_COMMENTED_FROM + fileName + FEED_LOCATION + siteName;
            Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(customDrone, DASHLET_ACTIVITIES, activityEntry, true),
                    "Info about deleting comment isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_COMMENTED_ON + " " + fileName + FEED_LOCATION + siteName;
            Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(customDrone, DASHLET_ACTIVITIES, activityEntry, true),
                    "Info about commenting on filename isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_UPDATED + FEED_FOR_FILE + 1 + fileName + FEED_LOCATION + siteName;
            Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(customDrone, DASHLET_ACTIVITIES, activityEntry, true),
                    "Info about updating document isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_CREATED + FEED_FOR_FILE + fileName + FEED_LOCATION + siteName;
            Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(customDrone, DASHLET_ACTIVITIES, activityEntry, true),
                    "Info about adding document isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_DELETED + FEED_FOR_FILE + copyFileName + FEED_LOCATION + siteName;
            Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(customDrone, DASHLET_ACTIVITIES, activityEntry, true),
                    "Info about deleting document isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_UPDATED_COMMENT_ON + FEED_FOR_FILE + fileName + FEED_LOCATION + siteName;
            Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(customDrone, DASHLET_ACTIVITIES, activityEntry, true),
                    "Info about updating comment isn't displayed");

            // Go to Site Dashboard activities and ensure all activities are displayed
            ShareUser.openSiteDashboard(customDrone, siteName).render();
            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_COMMENT_DELETED + FEED_COMMENTED_FROM + fileName;
            Assert.assertTrue(ShareUser.searchSiteDashBoardDescriptionsWithRetry(customDrone, SITE_ACTIVITIES, activityEntry, true, siteName),
                    "Info about deleting comment isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_COMMENTED_ON + " " + fileName;
            Assert.assertTrue(ShareUser.searchSiteDashBoardDescriptionsWithRetry(customDrone, SITE_ACTIVITIES, activityEntry, true, siteName),
                    "Info about commenting on folder isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_UPDATED + FEED_FOR_FILE + 1 + fileName;
            Assert.assertTrue(ShareUser.searchSiteDashBoardDescriptionsWithRetry(customDrone, SITE_ACTIVITIES, activityEntry, true, siteName),
                    "Info about updating document isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_CREATED + FEED_FOR_FILE + fileName;
            Assert.assertTrue(ShareUser.searchSiteDashBoardDescriptionsWithRetry(customDrone, SITE_ACTIVITIES, activityEntry, true, siteName),
                    "Info about adding document isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_DELETED + FEED_FOR_FILE + copyFileName;
            Assert.assertTrue(ShareUser.searchSiteDashBoardDescriptionsWithRetry(customDrone, SITE_ACTIVITIES, activityEntry, true, siteName),
                    "Info about deleting document isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_UPDATED_COMMENT_ON + FEED_FOR_FILE + fileName;
            Assert.assertTrue(ShareUser.searchSiteDashBoardDescriptionsWithRetry(customDrone, SITE_ACTIVITIES, activityEntry, true, siteName),
                    "Info about updating comment isn't displayed");
        }

        catch (Throwable e)
        {
            reportError(customDrone, testName, e);
        }

        finally
        {
            ShareUser.logout(customDrone);
            customDrone.quit();
        }

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void ALF_3057() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String copyFolderName = "Copy of " + getFolderName(testName);
        String zip = ".zip";
        String tag = getRandomString(5);
        String fileName = getFileName(testName) + ".txt";
        String catAspect = "Categories";
        String aliasAspect = "Alias";
        String moveIntoFolder = 1 + folderName;

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        String testUser2 = testUser + 1;
        String[] testUserInfo2 = new String[] { testUser2 };

        String[][] users = { testUserInfo, testUserInfo2 };

        try
        {
            customDrone = ((WebDroneImpl) ctx.getBean(WebDroneType.DownLoadDrone.getName()));
            dronePropertiesMap.put(customDrone, (ShareTestProperty) ctx.getBean("shareTestProperties"));
            maxWaitTime = ((WebDroneImpl) customDrone).getMaxPageRenderWaitTime();

            // Create 2 users
            for (String[] user : users)
            {
                CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, user);
            }

            // Login with the first user
            ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(customDrone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(customDrone).render(maxWaitTime);

            // Create Folder
            ShareUserSitePage.createFolder(customDrone, folderName, folderName);

            // Invite user2 to the created site
            ShareUserMembers.inviteUserToSiteWithRole(customDrone, testUser, testUser2, siteName, UserRole.COLLABORATOR);

            // Open folder Details Page
            ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName).render();
            FolderDetailsPage detailsPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();
            String DetailPageUrl = customDrone.getCurrentUrl();

            // Mark folder as favourite
            detailsPage.selectFavourite().render();
            Assert.assertTrue(detailsPage.isFavourite(), "The folder isn't favourite");

            // Like / Unlike the folder
            detailsPage.selectLike().render();
            Assert.assertTrue(detailsPage.isLiked(), "Folder isn't liked");
            Assert.assertEquals(detailsPage.getLikeCount(), "1", "The number of likes didn't increase");

            detailsPage.selectLike().render();
            Assert.assertFalse(detailsPage.isLiked(), "Folder is liked");
            Assert.assertEquals(detailsPage.getLikeCount(), "0", "The number of likes didn't decrease");

            // Comment on the folder via Comment Link
            AddCommentForm addCommentForm = detailsPage.selectAddComment();

            String comment = getRandomString(5);
            TinyMceEditor tinyMceEditor = addCommentForm.getTinyMceEditor();
            tinyMceEditor.setText(comment);
            Assert.assertEquals(tinyMceEditor.getText(), comment, "Text didn't enter in MCE box or didn't correct.");

            addCommentForm.selectAddCommentButton().render(maxWaitTime);
            webDriverWait(customDrone, 5000);
            Assert.assertEquals(detailsPage.getComments().size(), 1, "Comment isn't added");

            // Add comment via Add comment button
            String comment2 = getRandomString(5);
            addCommentForm = detailsPage.clickAddCommentButton();
            tinyMceEditor = addCommentForm.getTinyMceEditor();
            tinyMceEditor.setText(comment2);
            Assert.assertEquals(tinyMceEditor.getText(), comment2, "Text didn't enter in MCE box or didn't correct.");

            addCommentForm.selectAddCommentButton().render(maxWaitTime);
            webDriverWait(customDrone, 5000);
            Assert.assertEquals(detailsPage.getComments().size(), 2, "Comment isn't added");

            // Edit the comment
            String newComment = getRandomString(8);
            detailsPage.editComment(comment2, newComment);
            detailsPage.saveEditComments();
            detailsPage.render();
            Assert.assertTrue(detailsPage.isCommentCorrect(newComment), "Comment isn't changed");

            // Delete the comment
            detailsPage.removeComment(newComment).render();
            documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();
            FileDirectoryInfo folder = documentLibraryPage.getFileDirectoryInfo(folderName);
            Assert.assertTrue(folder.getCommentsCount() == 1);

            // Edit Properties for folder and add any tag to it
            customDrone.navigateTo(DetailPageUrl);
            detailsPage = customDrone.getCurrentPage().render(maxWaitTime);
            EditDocumentPropertiesPage editProperties = detailsPage.selectEditProperties().render(maxWaitTime);
            editProperties.setName(folderName + 1);
            TagPage tagPage = editProperties.getTag().render();
            Assert.assertTrue(tagPage.isTagInputVisible(), "Tag window didn't open");
            tagPage = tagPage.enterTagValue(tag).render();
            tagPage.clickOkButton();
            editProperties.selectSave().render();

            detailsPage = customDrone.getCurrentPage().render();
            Assert.assertTrue(detailsPage.getTagList().contains(tag), "Tag hasn't added");

            // Copy the folder to any place
//            detailsPage.selectAction(DocumentAction.COPY_TO).render();
//            CopyOrMoveContentPage copyPage = customDrone.getCurrentPage().render();
//            copyPage.selectSite(siteName).render(maxWaitTime);
//            copyPage.selectOkButton().render();
            documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();
            FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(folderName + 1);
            CopyOrMoveContentPage copyPage = fileInfo.selectCopyTo().render();
            copyPage.selectSite(siteName).render(maxWaitTime);
            copyPage.selectOkButton().render(maxWaitTime);
            List<FileDirectoryInfo> folders = documentLibraryPage.getFiles();
            Assert.assertEquals(folders.size(), 2, "Folder isn't copied");
            Assert.assertTrue(documentLibraryPage.isFileVisible(copyFolderName + 1), "Folder isn't copied");

            // Move the folder to any place
            ShareUserSitePage.createFolder(customDrone, moveIntoFolder, moveIntoFolder).render(maxWaitTime);
//            detailsPage = documentLibraryPage.getFileDirectoryInfo(folderName + 1).selectViewFolderDetails().render();
//            detailsPage.selectAction(DocumentAction.MOVE_TO);
//            customDrone.getCurrentPage().render(maxWaitTime);
//            CopyOrMoveContentPage movePage = customDrone.getCurrentPage().render();
            fileInfo = documentLibraryPage.getFileDirectoryInfo(folderName + 1);
            CopyOrMoveContentPage movePage = fileInfo.selectMoveTo().render();
            movePage.selectPath(moveIntoFolder).render();
            movePage.selectOkButton().render();
            Assert.assertFalse(documentLibraryPage.isFileVisible(folderName + 1), "Folder isn't moved");

            ShareUser.openDocumentLibrary(customDrone).render();
            documentLibraryPage = documentLibraryPage.selectFolder(moveIntoFolder).render();
            documentLibraryPage.render();
            documentLibraryPage.render(maxWaitTime);
            Assert.assertTrue(documentLibraryPage.isFileVisible(folderName + 1), "Folder isn't moved");

            // Manage rules for the folder
            // Create an item
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(fileName);
            ShareUser.createContent(customDrone, contentDetails, ContentType.PLAINTEXT);
            ShareUser.openDocumentDetailPage(customDrone, fileName).render(maxWaitTime);

            // Add Classifiable aspect
            List<DocumentAspect> aspects = Arrays.asList(DocumentAspect.CLASSIFIABLE);
            DocumentDetailsPage documentDetailsPage = (DocumentDetailsPage) ShareUser.addAspects(customDrone, aspects).render(maxWaitTime);

            Map<String, Object> properties = documentDetailsPage.getProperties();
            Assert.assertTrue("(None)".equalsIgnoreCase((String) properties.get(catAspect)), "Aspect isn't added");

            // Create the rule for folder
            ShareUser.openDocumentLibrary(customDrone).render();
            documentLibraryPage = documentLibraryPage.selectFolder(moveIntoFolder).render(maxWaitTime);

            detailsPage = documentLibraryPage.getFileDirectoryInfo(folderName + 1).selectViewFolderDetails().render();
            detailsPage.selectAction(DocumentAction.MANAGE_RULES);
            customDrone.getCurrentPage().render(maxWaitTime);
            FolderRulesPage folderRulesPage = customDrone.getCurrentPage().render();

            Assert.assertTrue(folderRulesPage.isPageCorrect(folderName + 1), "Rule page isn't correct");

            // Fill "Name" field with correct data
            CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
            createRulePage.fillNameField("Remove Aspect Rule Name");
            createRulePage.fillDescriptionField("Remove Aspect Rule Description");

            // Select "Inbound" value from "When" drop-down select control
            WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
            whenSelectorIml.selectInbound();

            // Select 'All items' from "If" drop-down select control
            AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
            ifSelector.selectIFOption(0);

            // Select Remove an aspect" from "Perform Action" drop-down select control
            // Select 'Classifiable' from drop-down select control
            ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
            actionSelectorEnterpImpl.selectRemoveAspect(DocumentAspect.CLASSIFIABLE.getValue());

            // Click "Create" button
            FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
            Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName + 1), "Rule page with rule isn't correct");

            documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render(maxWaitTime);

            // Move the document to the folder
            movePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
            customDrone.getCurrentPage().render(maxWaitTime);
            movePage = customDrone.getCurrentPage().render();

            movePage.selectPath(moveIntoFolder);
            movePage.selectPath(folderName + 1);
            movePage.selectOkButton();

            // Navigating to movefolder
            ShareUser.openDocumentLibrary(customDrone).render();
            documentLibraryPage.selectFolder(moveIntoFolder).render(maxWaitTime);
            documentLibraryPage.selectFolder(folderName + 1).render(maxWaitTime);

            // Verifying that file is moved successfully.
            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "File " + fileName + " isn't visible");

            DocumentDetailsPage docDetailsPage = ShareUser.openDocumentDetailPage(customDrone, fileName).render();

            // View 'Manage Aspects' page for the added item
            properties = docDetailsPage.getProperties();
            Assert.assertFalse(properties.containsValue((catAspect)), "Property 'Categories' is visible");

            SelectAspectsPage aspectsPage = docDetailsPage.selectManageAspects().render();

            // Classifiable aspect has been removed
            Assert.assertFalse(aspectsPage.getSelectedAspects().contains(DocumentAspect.CLASSIFIABLE), "'Classifiable'' aspect hasn't been removed)");
            aspectsPage.clickCancel().render();
            documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();

            detailsPage = documentLibraryPage.getFileDirectoryInfo(copyFolderName + 1).selectViewFolderDetails().render();

            detailsPage.selectAction(DocumentAction.MANAGE_PERMISSION_FOL);
            ManagePermissionsPage managePermissions = customDrone.getCurrentPage().render();

            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(testUser2);
            userProfile.setlName(DEFAULT_LASTNAME);

            ManagePermissionsPage.UserSearchPage userSearchPage = managePermissions.selectAddUser().render();
            managePermissions = userSearchPage.searchAndSelectUser(userProfile).render();

            // Click on Cancel button
            managePermissions.selectCancel();

            // Go to the manage permission of Folder
            managePermissions = ShareUser.returnManagePermissionPage(customDrone, copyFolderName + 1);

            // Verify the testUser2 is not added
            Assert.assertFalse(managePermissions.isUserExistForPermission(testUser2), "It's impossible to cancel changes");
            managePermissions.selectCancel().render();

            // Manage Aspects, add some aspect, remove some aspect
            // Add Classifiable aspect
            aspects = Arrays.asList(DocumentAspect.CLASSIFIABLE);
            detailsPage = (FolderDetailsPage) ShareUser.addAspects(customDrone, aspects);
            customDrone.refresh();
            properties = detailsPage.getProperties();
            Assert.assertTrue("(None)".equalsIgnoreCase((String) properties.get(catAspect)), "Aspect isn't added");

//            // Add Alias aspect
//            aspects = Arrays.asList(DocumentAspect.ALIASABLE_EMAIL);
//            detailsPage = (FolderDetailsPage) ShareUser.addAspects(customDrone, aspects);
//            customDrone.refresh();
//            properties = docDetailsPage.getProperties();
//            Assert.assertTrue("(None)".equalsIgnoreCase((String) properties.get(aliasAspect)), "Aspect isn't added");

            // Remove Classifiable aspect
//            aspects = Arrays.asList(DocumentAspect.CLASSIFIABLE);
            ShareUser.removeAspects(customDrone, aspects);
            customDrone.refresh();
            FolderDetailsPage detailsFPage = drone.getCurrentPage().render();
            properties = detailsFPage.getProperties();
            Assert.assertFalse("(None)".equalsIgnoreCase((String) properties.get(catAspect)), "Aspect isn't removed");

            // Change Type for the folder
            properties = detailsFPage.getProperties();

            // Click 'Change Type' action
            detailsPage = customDrone.getCurrentPage().render();

            ChangeTypePage changeTypePage = detailsPage.selectChangeType().render();
            assertTrue(changeTypePage.isChangeTypeDisplayed());

            List<String> types = changeTypePage.getTypes();
            assertTrue(types.contains("Select type..."));

            // Select any type if present
            int typeCount = types.size();

            if (typeCount > 1)
            {

                String randomType = types.get(1);
                changeTypePage.selectChangeType(randomType);

                // Click Change Type again, select any type and click OK
                changeTypePage.selectSave().render();
                Map<String, Object> propertiesAfter = detailsPage.getProperties();
                Assert.assertNotSame(properties, propertiesAfter, "Folder type hasn't changed");

            }
            else
            {
                changeTypePage.selectCancel();
                Map<String, Object> propertiesAfter = detailsPage.getProperties();
                assertEquals(properties, propertiesAfter, "Folder type has changed, although shouldn't");
            }

            // Click Edit Icon near Tags section. Edit some properties and click Cancel
            detailsPage.clickEditTagsIcon(true);
            EditDocumentPropertiesPage editPage = customDrone.getCurrentPage().render();
            editPage.setName(folderName + 3);
            editPage.clickOnCancel();
            Assert.assertFalse(detailsPage.getProperties().containsValue(folderName + 3), "Folder name has changed");

            // Click Edit icon near Properties section. Edit some properties and click Cancel
            detailsPage.clickEditPropertiesIcon(true);
            editPage = customDrone.getCurrentPage().render();
            editPage.setName(folderName + 3);
            editPage.clickOnCancel();
            Assert.assertFalse(detailsPage.getProperties().containsValue(folderName + 3), "Folder name has changed");

            // Click Manage Permissions icon. Search for some user (a member of the site), add him with some other permissions
            Assert.assertFalse(detailsPage.isPermissionsPanelPresent(), "Manage permission panel is present, ACE-436");

            // Delete the folder. Confirm deletion
            detailsPage.delete();

            ShareUser.openUserDashboard(customDrone);
            String myDashboard = customDrone.getCurrentUrl();
            ShareUser.openSiteDashboard(customDrone, siteName);
            String siteDasbord = customDrone.getCurrentUrl();
            ShareUser.openDocumentLibrary(customDrone).render();

            Assert.assertFalse(documentLibraryPage.isFileVisible(copyFolderName + 1), "The folder isn't deleted");

            // Click Download as Zip from the folder actions
            documentLibraryPage.getFileDirectoryInfo(moveIntoFolder).selectDownloadFolderAsZip();
            documentLibraryPage.waitForFile(downloadDirectory + moveIntoFolder + zip);

            Assert.assertTrue(ShareUser.extractDownloadedArchieve(customDrone, moveIntoFolder + zip), "Folder isn't download as zip");

            // Go to Site Dashboard activities and ensure all activities are displayed
            // ShareUser.openSiteDashboard(customDrone, siteName).render();
            customDrone.navigateTo(siteDasbord);
            String activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_COMMENT_DELETED + FEED_COMMENTED_FROM + folderName;
            Assert.assertTrue(ShareUser.searchSiteDashBoardDescriptionsWithRetry(customDrone, SITE_ACTIVITIES, activityEntry, true, siteName),
                    "Info about deleting comment isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_COMMENTED_ON + " " + folderName;
            Assert.assertTrue(ShareUser.searchSiteDashBoardDescriptionsWithRetry(customDrone, SITE_ACTIVITIES, activityEntry, true, siteName),
                    "Info about commenting on folder isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_LIKED + FEED_FOR_FOLDER + folderName;
            Assert.assertTrue(ShareUser.searchSiteDashBoardDescriptionsWithRetry(customDrone, SITE_ACTIVITIES, activityEntry, true, siteName),
                    "Info about liking folder isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_ADDED + FEED_FOR_FOLDER + folderName;
            Assert.assertTrue(ShareUser.searchSiteDashBoardDescriptionsWithRetry(customDrone, SITE_ACTIVITIES, activityEntry, true, siteName),
                    "Info about adding folder isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_DELETED + FEED_FOR_FOLDER + moveIntoFolder;
            Assert.assertTrue(ShareUser.searchSiteDashBoardDescriptionsWithRetry(customDrone, SITE_ACTIVITIES, activityEntry, true, siteName),
                    "Info about deleting folder isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_UPDATED_COMMENT_ON + FEED_FOR_FOLDER + moveIntoFolder;
            Assert.assertTrue(ShareUser.searchSiteDashBoardDescriptionsWithRetry(customDrone, SITE_ACTIVITIES, activityEntry, true, siteName),
                    "Info about updating comment isn't displayed");

            // Go to My Dashboard activities and ensure all activities are displayed
            // ShareUser.openUserDashboard(customDrone);
            customDrone.navigateTo(myDashboard);
            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_ADDED + FEED_FOR_FOLDER + folderName + FEED_LOCATION + siteName;
            Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(customDrone, DASHLET_ACTIVITIES, activityEntry, true),
                    "Info about adding folder isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_COMMENT_DELETED + FEED_COMMENTED_FROM + folderName + FEED_LOCATION + siteName;
            Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(customDrone, DASHLET_ACTIVITIES, activityEntry, true),
                    "Info about deleting comment isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_COMMENTED_ON + " " + folderName + FEED_LOCATION + siteName;
            Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(customDrone, DASHLET_ACTIVITIES, activityEntry, true),
                    "Info about commenting on folder isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_LIKED + FEED_FOR_FOLDER + " " + folderName + FEED_LOCATION + siteName;
            Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(customDrone, DASHLET_ACTIVITIES, activityEntry, true),
                    "Info about liking folder isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_DELETED + FEED_FOR_FOLDER + moveIntoFolder + FEED_LOCATION + siteName;
            Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(customDrone, DASHLET_ACTIVITIES, activityEntry, true),
                    "Info about deleting folder isn't displayed");

            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_UPDATED_COMMENT_ON + FEED_FOR_FOLDER + moveIntoFolder + FEED_LOCATION + siteName;
            Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(customDrone, DASHLET_ACTIVITIES, activityEntry, true),
                    "Info about updating comment isn't displayed");

        }

        catch (Throwable e)
        {
            reportError(customDrone, testName, e);

        }
        finally
        {
            ShareUser.logout(customDrone);
            customDrone.quit();
        }

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void ALF_3058() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName) + ".txt";
        String tag = "testtag";
        String testUser = getUserNameFreeDomain(testName);
        String fileNameDocs = getFileName(testName) + ".docs";

        String[] testUserInfo = new String[] { testUser };

        String testUser2 = testUser + 1;
        String[] testUserInfo2 = new String[] { testUser2 };

        // create 2 users
        try
        {
            customDrone = ((WebDroneImpl) ctx.getBean(WebDroneType.DownLoadDrone.getName()));
            dronePropertiesMap.put(customDrone, (ShareTestProperty) ctx.getBean("shareTestProperties"));
            maxWaitTime = ((WebDroneImpl) customDrone).getMaxPageRenderWaitTime();

            CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);
            CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo2);

            // Login with the first user
            ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(customDrone, siteName, SITE_VISIBILITY_PUBLIC);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render(maxWaitTime);

            // Create folders
            for (int i = 1; i <= 3; i++)
            {
                ShareUserSitePage.createFolder(customDrone, folderName + i, folderName + i).render(maxWaitTime);
            }

            documentLibraryPage.getFileDirectoryInfo(folderName + 1).addTag(tag + 1);
            documentLibraryPage.getFileDirectoryInfo(folderName + 2).addTag(tag + 2);

            // Add categories for some folders
            // Add Classifable Aspect
            for (int i = 1; i <= 2; i++)
            {

                ShareUser.openFolderDetailPage(customDrone, folderName + i).render();

                List<DocumentAspect> aspects = Arrays.asList(DocumentAspect.CLASSIFIABLE);
                FolderDetailsPage detailPage = (FolderDetailsPage) ShareUser.addAspects(customDrone, aspects);

                // Add category
                EditDocumentPropertiesPage editPage = detailPage.selectEditProperties().render();

                CategoryPage catPage = editPage.getCategory().render();
                String catLanguages = customDrone.getValue("category.languages");
                catPage.addCategories(Arrays.asList(catLanguages)).render();
                editPage = catPage.clickOk().render();

                editPage.selectSave().render();
                ShareUser.openDocumentLibrary(customDrone).render();

            }

            // Mark any folder as favourite
            documentLibraryPage.getFileDirectoryInfo(folderName + 1).selectFavourite();

            // Create several docs
            for (int i = 1; i <= 2; i++)
            {
                String[] fileInfo = { i + fileName };
                ShareUser.uploadFileInFolder(customDrone, fileInfo).render(maxWaitTime);
            }

            String[] fileInfo = { fileNameDocs };
            ShareUser.uploadFileInFolder(customDrone, fileInfo).render(maxWaitTime);

            String[] fileTest = { fileName, folderName + 3 };
            ShareUser.uploadFileInFolder(customDrone, fileTest).render(maxWaitTime);
            documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();

            // Add tags to some documents
            documentLibraryPage.getFileDirectoryInfo(1 + fileName).addTag(tag + 1);
            documentLibraryPage.getFileDirectoryInfo(2 + fileName).addTag(tag + 2);

            // Add categories to some documents
            for (int i = 1; i <= 2; i++)
            {
                DocumentDetailsPage detailPage = ShareUser.openDocumentDetailPage(customDrone, i + fileName).render();

                List<DocumentAspect> aspects = Arrays.asList(DocumentAspect.CLASSIFIABLE);
                //  ShareUser.addAspects(customDrone, aspects);
                List<DocumentAspect> aspectsList = new ArrayList<DocumentAspect>();
                SelectAspectsPage aspectsPage = detailPage.selectManageAspects().render();
                aspectsList.addAll(aspects);
                aspectsPage.add(aspectsList);
                aspectsPage.clickApplyChanges().render(maxWaitTime);

                // Add category
//                detailPage = customDrone.getCurrentPage().render();
//                drone.refresh();
                documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();
                FileDirectoryInfo fileDInfo = documentLibraryPage.getFileDirectoryInfo(i + fileName);
                EditDocumentPropertiesPage editPage = fileDInfo.selectEditProperties().render();
                assertTrue(editPage.isEditPropertiesPopupVisible());

//                EditDocumentPropertiesPage editPage = detailPage.selectEditProperties().render(maxWaitTime);
                CategoryPage catPage = editPage.getCategory().render();
                String catLanguages = customDrone.getValue("category.regions");
                catPage.addCategories(Arrays.asList(catLanguages)).render();
                editPage = catPage.clickOk().render();

                editPage.selectSave().render();
                ShareUser.openDocumentLibrary(customDrone).render();
            }

            // Mark as favourite
            documentLibraryPage.getFileDirectoryInfo(1 + fileName).selectFavourite();

            // Click offline edit
            String DocLibUrl = customDrone.getCurrentUrl();
            DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(customDrone, 1 + fileName).render();
            detailsPage.selectEditOffLine(null).render();
            customDrone.navigateTo(DocLibUrl);
            documentLibraryPage = customDrone.getCurrentPage().render();

            // // Click online edit
            // DocumentDetailsPage detailPage = ShareUser.openDocumentDetailPage(customDrone, fileNameDocs).render(maxWaitTime);
            // detailPage.selectOnlineEdit().render(maxWaitTime);

            // Invite user2 to the created site
            ShareUserMembers.inviteUserToSiteWithRole(customDrone, testUser, testUser2, siteName, UserRole.COLLABORATOR);

            // Login as user2
            ShareUser.login(customDrone, testUser2, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(customDrone, siteName).render();

            // Mark any folder as favourite
            documentLibraryPage.getFileDirectoryInfo(folderName + 2).selectFavourite();

            // Mark any document as favourite
            documentLibraryPage.getFileDirectoryInfo(2 + fileName).selectFavourite();

            // Click offline edit
            detailsPage = ShareUser.openDocumentDetailPage(customDrone, 2 + fileName).render();
            detailsPage.selectEditOffLine(null).render();

            // Open Document Library
            ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

            // Click All Documents
            TreeMenuNavigation treeMenuNavigation = documentLibraryPage.getLeftMenus();
            treeMenuNavigation.selectDocumentNode(ALL_DOCUMENTS).render();
            customDrone.getCurrentPage().render(maxWaitTime);
            Assert.assertEquals(documentLibraryPage.getFiles().size(), 4, "Wrong All Documents file count displayed.");

            // Click I'm editing
            treeMenuNavigation = documentLibraryPage.getLeftMenus();
            treeMenuNavigation.selectDocumentNode(IM_EDITING).render();
            customDrone.getCurrentPage().render(maxWaitTime);
            Assert.assertEquals(documentLibraryPage.getFiles().size(), 1, "Wrong I'm Editing file count displayed.");

            // Others are Editing
            treeMenuNavigation = documentLibraryPage.getLeftMenus();
            treeMenuNavigation.selectDocumentNode(OTHERS_EDITING).render();
            Assert.assertEquals(documentLibraryPage.getFiles().size(), 1, "Wrong Others are Editing file count displayed.");

            // Click Recently Modified
            treeMenuNavigation = documentLibraryPage.getLeftMenus().render();
            treeMenuNavigation.selectDocumentNode(RECENTLY_MODIFIED).render();
            customDrone.getCurrentPage().render(maxWaitTime);
            Assert.assertEquals(documentLibraryPage.getFiles().size(), 4, "Wrong Recently Modified file count displayed.");

            // Click Recently Added
            treeMenuNavigation = documentLibraryPage.getLeftMenus();
            treeMenuNavigation.selectDocumentNode(RECENTLY_ADDED).render();
            customDrone.getCurrentPage().render(maxWaitTime);
            Assert.assertEquals(documentLibraryPage.getFiles().size(), 4, "Wrong Recently Added file count displayed.");

            // Click My Favourites
            treeMenuNavigation = documentLibraryPage.getLeftMenus().render(maxWaitTime);
            treeMenuNavigation.selectDocumentNode(MY_FAVORITES).render(maxWaitTime);
            customDrone.getCurrentPage().render(maxWaitTime);
            Assert.assertEquals(documentLibraryPage.getFiles().size(), 2, "Wrong My Favourites file count displayed.");

            // Click on any folder under Library section
            treeMenuNavigation = documentLibraryPage.getLeftMenus();
            treeMenuNavigation.selectNode(TreeMenuNavigation.TreeMenu.LIBRARY, folderName + 3).render();
            customDrone.getCurrentPage().render(maxWaitTime);
            Assert.assertEquals(documentLibraryPage.getFiles().size(), 1, "Wrong file count displayed in Library.");
            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "");

            // Click on <category1> under Categories section
            treeMenuNavigation = documentLibraryPage.getLeftMenus();
            treeMenuNavigation.selectNode(TreeMenuNavigation.TreeMenu.CATEGORIES, customDrone.getValue("categories.tree.root"),
                    customDrone.getValue("category.languages")).render();
            customDrone.getCurrentPage().render(maxWaitTime);
            Assert.assertEquals(documentLibraryPage.getFiles().size(), 2, "Wrong file count displayed after specific category.");

            // Click on tag1 under Tags section
            treeMenuNavigation = documentLibraryPage.getLeftMenus();
            treeMenuNavigation.selectTagNode(tag + 1).render();
            customDrone.getCurrentPage().render(maxWaitTime);
            Assert.assertEquals(documentLibraryPage.getFiles().size(), 2, "Wrong file count displayed after specific tag is chosen.");

            // Mark as favourite any folder and document
            // Like/unlike some folders and documents
            // Click comment for any document and folder
            String[] items = { fileName, folderName + 3 };
            for (String item : items)
            {

                documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();

                if (item.equals(items[0]))
                {
                    documentLibraryPage = documentLibraryPage.selectFolder(folderName + 3).render();
                }

                FileDirectoryInfo thisFile = documentLibraryPage.getFileDirectoryInfo(item);
                thisFile.selectFavourite();
                thisFile.selectLike();

                Assert.assertTrue(thisFile.isFavourite(), "Item isn't marked as favourite");
                Assert.assertTrue(thisFile.isLiked(), "Item isn't liked as favourite");

                thisFile.selectLike();
                Assert.assertFalse(thisFile.isLiked(), "Item isn't unliked as favourite");

                if (item.equals(items[0]))
                {
                    detailsPage = thisFile.clickCommentsLink().render();
                    customDrone.getCurrentPage().render(maxWaitTime);
                    Assert.assertTrue(detailsPage.getTitle().contains("Document Details"), "Details page isn't opened");
                    Assert.assertTrue(detailsPage.isCommentFieldPresent(), "Comment field isn't present");
                }
                else
                {
                    FolderDetailsPage folDetailsPage = thisFile.clickCommentsLink().render();
                    customDrone.getCurrentPage().render(maxWaitTime);
                    Assert.assertTrue(folDetailsPage.getTitle().contains("Folder Details"), "Details page isn't opened");
                    Assert.assertTrue(folDetailsPage.isCommentFieldPresent(), "Comment field isn't present");
                }
            }

            // Share any document
            documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render();
            documentLibraryPage.selectFolder(folderName + 3).render();
            ShareLinkPage shareLinkPage = documentLibraryPage.getFileDirectoryInfo(fileName).clickShareLink().render();

            Assert.assertTrue(shareLinkPage.isEmailLinkPresent(), "Email link isn't present");
            Assert.assertTrue(shareLinkPage.isFaceBookLinkPresent(), "Facebook link isn't present");
            Assert.assertTrue(shareLinkPage.isGooglePlusLinkPresent(), "Google+ link isn't present");
            Assert.assertTrue(shareLinkPage.isTwitterLinkPresent(), "Twitter link isn't present");
            Assert.assertTrue(shareLinkPage.isUnShareLinkPresent(), "Unshare link isn't present");
            Assert.assertTrue(shareLinkPage.isViewLinkPresent(), "View link isn't present");

        }

        catch (Throwable e)
        {
            reportError(customDrone, testName, e);
        }
        finally
        {
            ShareUser.logout(customDrone);
            customDrone.quit();
        }

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void ALF_3059() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName) + ".txt";
        String startWorkflow = "Start Workflow";
        String moveIntoFolder = "move" + folderName;

        String testUser = getUserNameFreeDomain(testName);
        String testUser2 = testUser + 1;
        String zip = ".zip";
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(testUser2);
        userProfile.setlName(DEFAULT_LASTNAME);

        String[] testUserInfo = new String[] { testUser };

        try
        {
            customDrone = ((WebDroneImpl) ctx.getBean(WebDroneType.DownLoadDrone.getName()));
            dronePropertiesMap.put(customDrone, (ShareTestProperty) ctx.getBean("shareTestProperties"));
            maxWaitTime = ((WebDroneImpl) customDrone).getMaxPageRenderWaitTime();

            // Create user
            CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

            // Create site
            ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD).render();
            ShareUser.createSite(customDrone, siteName, SITE_VISIBILITY_PUBLIC).render(maxWaitTime);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(customDrone).render(maxWaitTime);

            // Create several folders
            for (int i = 1; i <= 2; i++)
            {
                ShareUserSitePage.createFolder(customDrone, folderName + i, folderName + i);
                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(i + fileName);
                contentDetails.setContent(i + fileName);
                documentLibraryPage = ShareUser.createContentInCurrentFolder(customDrone, contentDetails, ContentType.PLAINTEXT, documentLibraryPage);

            }

            // Click Select > Documents
            documentLibraryPage.getNavigation().selectDocuments().render();
            for (int i = 1; i <= 2; i++)
            {
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(i + fileName).isCheckboxSelected(), "Document " + i + fileName + " isn't checked");
            }

            // Click Select > Folders
            documentLibraryPage.getNavigation().selectFolders().render();
            for (int i = 1; i <= 2; i++)
            {
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName + i).isCheckboxSelected(), "Folder " + folderName + i + " isn't checked");
            }

            // Click Select > None
            documentLibraryPage.getNavigation().selectNone().render();
            for (int i = 1; i <= 2; i++)
            {
                Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(i + fileName).isCheckboxSelected(), "Document " + i + fileName + " isn't checked");
                Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName + i).isCheckboxSelected(), "Folder " + folderName + i + " isn't checked");
            }

            // Click Select > All
            documentLibraryPage.getNavigation().selectAll().render();
            for (int i = 1; i <= 2; i++)
            {
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(i + fileName).isCheckboxSelected(), "Document " + i + fileName + " isn't checked");
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName + i).isCheckboxSelected(), "Folder " + folderName + i + " isn't checked");
            }

            // Click Select > Invert Selection
            documentLibraryPage.getNavigation().selectInvert().render();
            for (int i = 1; i <= 2; i++)
            {
                Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(i + fileName).isCheckboxSelected(), "Document " + i + fileName + " isn't checked");
                Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName + i).isCheckboxSelected(), "Folder " + folderName + i + " isn't checked");
            }

            // Select only documents, verify available actions
            documentLibraryPage.getNavigation().selectDocuments().render();
            Assert.assertTrue(documentLibraryPage.getNavigation().isSelectedItemMenuCorrectForDocument(), "Not all the actions are available");
            documentLibraryPage.getNavigation().clickSelectedItems().render();

            // Start Workflow. Select any type of workflow
            StartWorkFlowPage startWorkFlowPage = documentLibraryPage.getNavigation().selectStartWorkFlow().render(maxWaitTime);
            customDrone.getCurrentPage().render();
//            Assert.assertTrue(startWorkFlowPage.getTitle().contains(startWorkflow), "Start Workflow page isn't opened");

            NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW));
            String workFlowName = testName + System.currentTimeMillis();
            String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
            List<String> reviewers = new ArrayList<>();
            reviewers.add(username);
            WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
            formDetails.setMessage(workFlowName);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);
            documentLibraryPage = newWorkflowPage.startWorkflow(formDetails).render();
            Assert.assertTrue(documentLibraryPage.isFileVisible(1 + fileName), "File " + 1 + fileName + " isn't visible");

            // The icon indicating that the document belongs to workflow is displayed near both documents
            for (int i = 1; i <= 2; i++)
            {
                Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(i + fileName).isPartOfWorkflow(), "The file " + i + fileName
                        + " isn't part of a workflow");
            }

            // Select only folders, verify available actions
            documentLibraryPage.getNavigation().selectFolders().render();
            Assert.assertTrue(documentLibraryPage.getNavigation().isSelectedItemMenuCorrectForFolder(), "Not all the actions are available");

            // Select both folders and documents, verify available actions
            documentLibraryPage.getNavigation().selectAll().render();
            Assert.assertTrue(documentLibraryPage.getNavigation().isSelectedItemMenuCorrectForFolder(), "Not all the actions are available");

            // Select several documents and folders and click Selected Items > Copy to. Copy the items to any location
            documentLibraryPage = customDrone.getCurrentPage().render();
            NewFolderPage newFolderPage = documentLibraryPage.getNavigation().selectCreateNewFolder().render(maxWaitTime);
            documentLibraryPage = (DocumentLibraryPage) newFolderPage.createNewFolder(moveIntoFolder, moveIntoFolder, moveIntoFolder).render();
            ShareUser.selectContentCheckBox(customDrone, folderName + 1);
            ShareUser.selectContentCheckBox(customDrone, 1 + fileName);

            CopyOrMoveContentPage copyContentPage = documentLibraryPage.getNavigation().selectCopyTo().render();
            copyContentPage = copyContentPage.selectSite(siteName).render();

            copyContentPage.selectPath(moveIntoFolder).render();
            copyContentPage.selectOkButton().render();

            // Navigating to path folder
            documentLibraryPage.selectFolder(moveIntoFolder).render();

            Assert.assertTrue(documentLibraryPage.isFileVisible(folderName + 1), "Folder isn't copied to a folder");
            Assert.assertTrue(documentLibraryPage.isFileVisible(1 + fileName), "Document isn't copied to a folder");

            // Select several documents and folders and click Selected Items > Move to. Move the items to any location
            ShareUser.openDocumentLibrary(customDrone);
            ShareUser.selectContentCheckBox(customDrone, folderName + 2);
            ShareUser.selectContentCheckBox(customDrone, 2 + fileName);

            CopyOrMoveContentPage moveContentPage = documentLibraryPage.getNavigation().selectMoveTo().render();
            moveContentPage = moveContentPage.selectSite(siteName).render();

            moveContentPage.selectPath(moveIntoFolder).render();
            moveContentPage.selectOkButton().render();

            // Navigating to path folder
            documentLibraryPage.selectFolder(moveIntoFolder).render();

            Assert.assertTrue(documentLibraryPage.isFileVisible(folderName + 2), "Folder isn't moved to a folder");
            Assert.assertTrue(documentLibraryPage.isFileVisible(2 + fileName), "Document isn't moved to a folder");

            // Select several documents and folders and click Selected Items > Delete
            ShareUser.selectContentCheckBox(customDrone, folderName + 2);
            ShareUser.selectContentCheckBox(customDrone, 2 + fileName);

            ConfirmDeletePage confirmDeletePage = documentLibraryPage.getNavigation().selectDelete().render();

            confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render();

            // The documents are deleted
            Assert.assertFalse(documentLibraryPage.isFileVisible(folderName + 2), "Folder isn't visible");
            Assert.assertFalse(documentLibraryPage.isFileVisible(2 + fileName), "Content isb;t visible");

            // Select several documents and folders and click Selected Items > Deselect All
            documentLibraryPage.getNavigation().selectAll().render();
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(1 + fileName).isCheckboxSelected(), "Document " + 1 + fileName + " isn't checked");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName + 1).isCheckboxSelected(), "Folder " + folderName + 1 + " isn't checked");

            documentLibraryPage.getNavigation().selectDesellectAll().render();
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(1 + fileName).isCheckboxSelected(), "Document " + 1 + fileName + " isn't checked");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName + 1).isCheckboxSelected(), "Folder " + folderName + 1 + " isn't checked");

            // Select several documents and folders and click Selected Items > Download as Zip
            ShareUser.selectContentCheckBox(customDrone, folderName + 1);
            ShareUser.selectContentCheckBox(customDrone, 1 + fileName);

            // String DocLibUrl = customDrone.getCurrentUrl();

            documentLibraryPage = customDrone.getCurrentPage().render();
            documentLibraryPage = documentLibraryPage.getNavigation().selectDownloadAsZip().render();

            documentLibraryPage.waitForFile(downloadDirectory + "Archive" + zip);
            webDriverWait(customDrone, maxDownloadWaitTime);

            // Extract the zip folder.
            Assert.assertTrue(ShareUser.extractDownloadedArchieve(customDrone, "Archive" + zip));
            List<String> filesOfFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);

//            Assert.assertTrue(filesOfFolders.contains(1 + fileName));
        }

        catch (Throwable e)
        {
            reportError(customDrone, testName, e);
        }
        finally
        {
            ShareUser.logout(customDrone);
            customDrone.quit();
        }
    }

}