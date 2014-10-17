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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.repository;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.site.document.TreeMenuNavigation.DocumentsMenu;
import org.alfresco.po.share.site.document.TreeMenuNavigation.TreeMenu;
import org.alfresco.share.util.*;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Repository Tests
 * 
 * @author Jamie Allison
 * @since 4.3
 */
@Listeners(FailedTestListener.class)
public class RepositoryBrowserTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(RepositoryBrowserTests.class);
    private static final String TEST_DOCX_FILE = "WordDocument.docx";

    protected String testUser;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "DataPrepRepository", "EnterpriseOnly" })
    public void dataPrep_AONE_3487() throws Exception
    {
        // NA
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_3487() throws Exception
    {
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepositoryDetailedView(drone);

        TreeMenuNavigation treeMenu = ShareUserSitePage.getLeftTreeMenu(drone);

        assertTrue(treeMenu.isMenuTreeVisible(TreeMenu.DOCUMENTS));
        assertTrue(treeMenu.isMenuTreeVisible(TreeMenu.LIBRARY));
        assertTrue(treeMenu.isMenuTreeVisible(TreeMenu.CATEGORIES));
        assertTrue(treeMenu.isMenuTreeVisible(TreeMenu.TAGS));

        assertTrue(treeMenu.isDocumentNodeVisible(DocumentsMenu.IM_EDITING));
        assertTrue(treeMenu.isDocumentNodeVisible(DocumentsMenu.MY_FAVORITES));

        assertTrue(repoPage.getNavigation().isSelectVisible());
        assertTrue(repoPage.getNavigation().isCreateContentVisible());
        repoPage = repoPage.getNavigation().selectCreateContentDropdown().render();
        assertTrue(repoPage.getNavigation().isNewFolderVisible());

        assertTrue(repoPage.getNavigation().isFileUploadVisible());

        repoPage = repoPage.getNavigation().selectNone().render();
        assertFalse(repoPage.getNavigation().isSelectedItemEnabled());
        repoPage = repoPage.getNavigation().selectAll().render();
        assertTrue(repoPage.getNavigation().isSelectedItemEnabled());

        assertTrue(repoPage.getNavigation().isFolderUpVisible());
        assertTrue(repoPage.getNavigation().isCrumbTrailVisible());

        assertTrue(repoPage.paginatorRendered());

        // Check the Options menu
        assertTrue(repoPage.getNavigation().isOptionPresent(LibraryOption.SHOW_FOLDERS) ^ repoPage.getNavigation().isOptionPresent(LibraryOption.HIDE_FOLDERS));
        assertTrue(repoPage.getNavigation().isOptionPresent(LibraryOption.SHOW_BREADCRUMB)
                ^ repoPage.getNavigation().isOptionPresent(LibraryOption.HIDE_BREADCRUMB));
        assertTrue(repoPage.getNavigation().isOptionPresent(LibraryOption.RSS_FEED));
        assertTrue(repoPage.getNavigation().isOptionPresent(LibraryOption.FULL_WINDOW));
        assertTrue(repoPage.getNavigation().isOptionPresent(LibraryOption.FULL_SCREEN));
        assertTrue(repoPage.getNavigation().isOptionPresent(LibraryOption.SIMPLE_VIEW));
        assertTrue(repoPage.getNavigation().isOptionPresent(LibraryOption.DETAILED_VIEW));
        assertTrue(repoPage.getNavigation().isOptionPresent(LibraryOption.GALLERY_VIEW));
        assertTrue(repoPage.getNavigation().isOptionPresent(LibraryOption.TABLE_VIEW));
        assertTrue(repoPage.getNavigation().isOptionPresent(LibraryOption.AUDIO_VIEW));
        assertTrue(repoPage.getNavigation().isOptionPresent(LibraryOption.MEDIA_VIEW));

        assertTrue(repoPage.getNavigation().selectFolderInNavBar(drone.getValue("repository.tree.root")).render() instanceof DetailsPage);
    }

    @Test(groups = { "DataPrepRepository", "EnterpriseOnly" })
    public void dataPrep_AONE_3488() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_3488() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String subFolderName = folderName + System.currentTimeMillis();
        String testFolder = subFolderName + "_2";
        String fileName1 = getFileName(testName) + System.currentTimeMillis() + "_1";
        String fileName2 = getFileName(testName) + System.currentTimeMillis() + "_2";
        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        ShareUserRepositoryPage.createFolderInRepository(drone, subFolderName, subFolderName);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName + SLASH + subFolderName);
        ShareUserRepositoryPage.createFolderInRepository(drone, testFolder, testFolder);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file1);
        RepositoryPage repoPage = ShareUserRepositoryPage.uploadFileInRepository(drone, file2);

        repoPage.getFileDirectoryInfo(fileName1).selectEditOffline().render();
        repoPage = getCurrentPage(drone).render();
        repoPage.getFileDirectoryInfo(fileName2).selectFavourite();
        repoPage.getFileDirectoryInfo(testFolder).selectFavourite();

        // Test starts here
        ShareUserSitePage.clickOnDocumentsInTreeMenu(drone, DocumentsMenu.IM_EDITING).render();
        
        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, fileName1, "editing", "", true));

        ShareUserSitePage.clickOnDocumentsInTreeMenu(drone, DocumentsMenu.MY_FAVORITES).render();
        
        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, fileName2, "isContentVisible", "", true), fileName2 + "is not visible.");
        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, testFolder, "isContentVisible", "", true), testFolder + "is not visible.");
    }

    @Test(groups = { "DataPrepRepository", "EnterpriseOnly" })
    public void dataPrep_AONE_3489() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_3489() throws Exception
    {
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositoryDetailedView(drone);

        TreeMenuNavigation treeMenu = ShareUserSitePage.getLeftTreeMenu(drone).render();

        List<String> children = treeMenu.getNodeChildren(TreeMenu.LIBRARY, drone.getValue("repository.tree.root"));

        assertTrue(children.contains(drone.getValue("system.folder.sites")), "System folder " + drone.getValue("system.folder.sites")
                + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.imap.home")), "System folder " + drone.getValue("system.folder.imap.home")
                + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.imap.attachments")), "System folder " + drone.getValue("system.folder.imap.attachments")
                + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.shared")), "System folder " + drone.getValue("system.folder.shared")
                + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.user.homes")), "System folder " + drone.getValue("system.folder.user.homes")
                + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.guest.home")), "System folder " + drone.getValue("system.folder.guest.home")
                + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.data.dictionary")), "System folder " + drone.getValue("system.folder.data.dictionary")
                + "could not be found in Repository tree");

        treeMenu = ShareUserSitePage.getLeftTreeMenu(drone).render();
        children = treeMenu.getNodeChildren(TreeMenu.LIBRARY, drone.getValue("repository.tree.root"), drone.getValue("system.folder.data.dictionary"));

        assertTrue(children.contains(drone.getValue("system.folder.email.templates")),
                "Data Dictionary folder " + drone.getValue("system.folder.email.templates") + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.imap.configs")), "Data Dictionary folder " + drone.getValue("system.folder.imap.configs")
                + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.messages")), "Data Dictionary folder " + drone.getValue("system.folder.messages")
                + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.models")), "Data Dictionary folder " + drone.getValue("system.folder.models")
                + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.node.templates")),
                "Data Dictionary folder " + drone.getValue("system.folder.node.templates") + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.presentation.templates")),
                "Data Dictionary folder " + drone.getValue("system.folder.presentation.templates") + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.publishing.root")),
                "Data Dictionary folder " + drone.getValue("system.folder.publishing.root") + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.rendering.actions.space")),
                "Data Dictionary folder " + drone.getValue("system.folder.rendering.actions.space") + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.replication.actions.space")),
                "Data Dictionary folder " + drone.getValue("system.folder.replication.actions.space") + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.rss.templates")), "Data Dictionary folder " + drone.getValue("system.folder.rss.templates")
                + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.saved.searches")),
                "Data Dictionary folder " + drone.getValue("system.folder.saved.searches") + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.scheduled.actions")),
                "Data Dictionary folder " + drone.getValue("system.folder.scheduled.actions") + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.scripts")), "Data Dictionary folder " + drone.getValue("system.folder.scripts")
                + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.space.templates")),
                "Data Dictionary folder " + drone.getValue("system.folder.space.templates") + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.transfers")), "Data Dictionary folder " + drone.getValue("system.folder.transfers")
                + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.web.client.extension")),
                "Data Dictionary folder " + drone.getValue("system.folder.web.client.extension") + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.web.scripts")), "Data Dictionary folder " + drone.getValue("system.folder.web.scripts")
                + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.web.scripts.extensions")),
                "Data Dictionary folder " + drone.getValue("system.folder.web.scripts.extensions") + "could not be found in Repository tree");
        assertTrue(children.contains(drone.getValue("system.folder.workflow.definitions")),
                "Data Dictionary folder " + drone.getValue("system.folder.workflow.definitions") + "could not be found in Repository tree");
    }

    @Test(groups = { "DataPrepRepository", "EnterpriseOnly" })
    public void dataPrep_AONE_3490() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String testFolder1 = folderName + "_1";
        String testFolder2 = folderName + "_2";
        String fileName1 = getFileName(testName) + "_1";
        String fileName2 = getFileName(testName) + "_2";
        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositoryDetailedView(drone);

        // Create content
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        ShareUserRepositoryPage.createFolderInRepository(drone, testFolder1, testFolder1);
        ShareUserRepositoryPage.createFolderInRepository(drone, testFolder2, testFolder2);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file1);
        RepositoryPage repoPage = ShareUserRepositoryPage.uploadFileInRepository(drone, file2);

        // Add Classifiable aspect
        ShareUserRepositoryPage.addAspect(drone, testFolder1, DocumentAspect.CLASSIFIABLE);
        ShareUserRepositoryPage.addAspect(drone, testFolder2, DocumentAspect.CLASSIFIABLE);

        List<DocumentAspect> aspects = new ArrayList<>();
        aspects.add(DocumentAspect.CLASSIFIABLE);

        DocumentDetailsPage docDetailsPage = repoPage.selectFile(fileName1).render();
        SelectAspectsPage selectAspectsPage = docDetailsPage.selectManageAspects().render();
        selectAspectsPage = selectAspectsPage.add(aspects).render();
        docDetailsPage = selectAspectsPage.clickApplyChanges().render();

        ShareUserRepositoryPage.openRepository(drone);
        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        docDetailsPage = repoPage.selectFile(fileName2).render();
        selectAspectsPage = docDetailsPage.selectManageAspects().render();
        selectAspectsPage = selectAspectsPage.add(aspects).render();
        docDetailsPage = selectAspectsPage.clickApplyChanges().render();

        ShareUserRepositoryPage.openRepository(drone);
        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        // add categories
        EditDocumentPropertiesPage editDocPropsPage = repoPage.getFileDirectoryInfo(testFolder1).selectEditProperties().render();
        ShareUserRepositoryPage.addCategories(drone, testFolder1, drone.getValue("category.languages"), true);
        editDocPropsPage.clickSave();

        repoPage.getFileDirectoryInfo(testFolder2).selectEditProperties().render();
        editDocPropsPage = ShareUserRepositoryPage.addCategories(drone, testFolder2, drone.getValue("category.english"), true,
                drone.getValue("category.languages"));
        editDocPropsPage.clickSave();

        repoPage.render();

        docDetailsPage = repoPage.selectFile(fileName1).render();
        docDetailsPage.selectEditProperties().render();
        editDocPropsPage = ShareUserRepositoryPage.addCategories(drone, fileName1, drone.getValue("category.languages"), true);
        editDocPropsPage.clickSave();

        ShareUserRepositoryPage.openRepository(drone);
        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        docDetailsPage = repoPage.selectFile(fileName2).render();
        docDetailsPage.selectEditProperties().render();
        editDocPropsPage = ShareUserRepositoryPage.addCategories(drone, fileName2, drone.getValue("category.english"), true,
                drone.getValue("category.languages"));
        editDocPropsPage.clickSave();
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_3490() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String testFolder1 = folderName + "_1";
        String testFolder2 = folderName + "_2";
        String fileName1 = getFileName(testName) + "_1";
        String fileName2 = getFileName(testName) + "_2";

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositoryDetailedView(drone);        
        
        ShareUserSitePage.clickOnCategoriesInTreeMenu(drone, drone.getValue("categories.tree.root"), drone.getValue("category.languages"));

        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, fileName1, "isContentVisible", "", true), fileName1 + "is not visible.");
        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, testFolder1, "isContentVisible", "", true), testFolder1 + "is not visible.");

        ShareUserSitePage.clickOnCategoriesInTreeMenu(drone, drone.getValue("categories.tree.root"), drone.getValue("category.languages"), drone.getValue("category.english"));

        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, fileName2, "isContentVisible", "", true), fileName2 + "is not visible.");
        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, testFolder2, "isContentVisible", "", true), testFolder2 + "is not visible.");
   }

    @Test(groups = { "DataPrepRepository", "EnterpriseOnly" })
    public void dataPrep_AONE_3491() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        String subFolderName1 = folderName + "_1";
        String subFolderName2 = folderName + "_2";

        String fileName1 = getFileName(testName) + "_1";
        String fileName2 = getFileName(testName) + "_2";

        String tagName1 = getTagName(testName + "_1");
        String tagName2 = getTagName(testName + "_2");

        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositoryDetailedView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        ShareUserRepositoryPage.createFolderInRepository(drone, subFolderName1, subFolderName1);
        ShareUserRepositoryPage.createFolderInRepository(drone, subFolderName2, subFolderName2);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file1);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file2);

        ShareUserRepositoryPage.selectView(drone, ViewType.DETAILED_VIEW);
        ShareUserRepositoryPage.addTag(drone, subFolderName1, tagName1);
        ShareUserRepositoryPage.addTag(drone, subFolderName2, tagName2);
        ShareUserRepositoryPage.addTag(drone, fileName1, tagName1);
        ShareUserRepositoryPage.addTag(drone, fileName2, tagName2);

    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_3491() throws Exception
    {
        String testName = getTestName();

        String folderName = getFolderName(testName);

        String subFolderName1 = folderName + "_1";
        String subFolderName2 = folderName + "_2";

        String fileName1 = getFileName(testName) + "_1";
        String fileName2 = getFileName(testName) + "_2";

        String tagName1 = getTagName(testName + "_1");
        String tagName2 = getTagName(testName + "_2");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Begin Test
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        
        ShareUserSitePage.clickOnTagNameInTreeMenu(drone, tagName1);

        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, fileName1, "isContentVisible", "", true), fileName1 + "is not visible.");
        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, subFolderName1, "isContentVisible", "", true), subFolderName1 + "is not visible.");
        
        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, fileName2, "isContentVisible", "", false), fileName2 + "is visible.");
        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, subFolderName2, "isContentVisible", "", false), subFolderName2 + "is visible.");

        ShareUserSitePage.clickOnTagNameInTreeMenu(drone, tagName2);

        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, fileName1, "isContentVisible", "", false), fileName1 + "is visible.");
        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, subFolderName1, "isContentVisible", "", false), subFolderName1 + "is visible.");
        
        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, fileName2, "isContentVisible", "", true), fileName2 + "is not visible.");
        assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, subFolderName2, "isContentVisible", "", true), subFolderName2 + "is not visible.");
    }

    @Test(groups = { "DataPrepRepository", "EnterpriseOnly" })
    public void dataPrep_AONE_3498() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
    }

    @Test(groups = "EnterpriseOnly")
    public void AONE_3498() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String subFolderName1 = folderName + System.currentTimeMillis();
        String subFolderName2 = subFolderName1 + "_2";
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String officeFileName = TEST_DOCX_FILE;
        File officeFile = new File(DATA_FOLDER + officeFileName);
        File file1 = newFile(fileName, fileName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        ShareUserRepositoryPage.createFolderInRepository(drone, subFolderName1, subFolderName1);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName + SLASH + subFolderName1);
        ShareUserRepositoryPage.uploadFileInRepository(drone, officeFile);

        // Test starts here
        RepositoryPage repoPage = ShareUserRepositoryPage.uploadFileInRepository(drone, file1);

        repoPage.renderItem(maxWaitTime, officeFileName);
        repoPage.renderItem(maxWaitTime, fileName);
        
        assertEquals(repoPage.getFiles().size(), 2);

        ShareUserRepositoryPage.createFolderInRepository(drone, subFolderName2, subFolderName2);

        repoPage.renderItem(maxWaitTime, officeFileName);
        repoPage.renderItem(maxWaitTime, fileName);
        repoPage.renderItem(maxWaitTime, subFolderName2);
        
        assertEquals(repoPage.getFiles().size(), 3);
    }
}