/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.share.dashlet;

import org.alfresco.po.share.dashlet.*;
import org.alfresco.po.share.dashlet.InsertOrEditLinkPage.InsertLinkPageTargetItems;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.TinyMceColourCode;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.TinyMceEditor.FormatType;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ConfigureSiteNoticeActions;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Chiran
 */
@Listeners(FailedTestListener.class)
public class SiteNoticetDashletTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(SiteNoticetDashletTest.class);
    private String siteDomain = "siteNotice.test";
    private static final String DEFAULT_SITE_NOTICE_TITLE = Dashlets.SITE_NOTICE.getDashletName();
    private static final String DEFAULT_SITE_NOTICE_TEXT = "No text has been configured";
    private static final String EMPTY = "";
    private static final String SPACE = " ";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13966() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Verify Site Notice Dashlet is added successfully.</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13966() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        Assert.assertNotNull(siteDashBoard.getDashlet(SITE_NOTICE).render());
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13967() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Verify Site Notice Dashlet is added successfully.</li>
     * <li>Verify available actions.</li>
     * <li>Click ?,Configure and X icons and verify the expected windows and functionality</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13967() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String expectedHelpBallonMsg = "This dashlet displays a custom message on the dashboard, specified by the site manager";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        Assert.assertNotNull(siteNoticeDashlet);
        Assert.assertTrue(siteNoticeDashlet.isHelpIconDisplayed());
        Assert.assertTrue(siteNoticeDashlet.isConfigureIconDisplayed());

        // Click on Help Icon.
        siteNoticeDashlet.clickOnHelpIcon();
        String actualHelpBallonMsg = siteNoticeDashlet.getHelpBalloonMessage();

        Assert.assertEquals(actualHelpBallonMsg, expectedHelpBallonMsg);
        siteNoticeDashlet.closeHelpBallon();

        // Click on configure Icon and get Configure Site Notice Dialog box.
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        Assert.assertNotNull(configureSiteNotice);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13968() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>In the Title field, type the text you want to appear in the dashlet header. In the Text box, type a new message</li>
     * <li>Click OK button</li>
     * <li>Data is successfully saved and displayed in Site Novice dashlet</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13968() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = testName + siteName;

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and save title and text.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, title, text, ConfigureSiteNoticeActions.OK);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Verify the given title and text on site Notice dashlet.
        Assert.assertEquals(siteNoticeDashlet.getTitle(), title);
        Assert.assertEquals(siteNoticeDashlet.getContent(), text);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13969() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>In the Title field, type the text you want to appear in the dashlet header. In the Text box, type a new message</li>
     * <li>Click Cancel button</li>
     * <li>Verfiy that No Changes are applied to text.</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13969() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = testName + siteName;

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and enter title and text, click cancel.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, title, text, ConfigureSiteNoticeActions.CANCEL);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Verify the given title and text on site Notice dashlet.
        Assert.assertNotEquals(siteNoticeDashlet.getTitle(), title);
        Assert.assertNotEquals(siteNoticeDashlet.getContent(), text);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13970() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>In the Title field and the text enter EMPTY</li>
     * <li>Click OK button</li>
     * <li>Verfiy that "Site Notice" is set as title and "No text has been configured message" is displayed in the box.</li>
     * <li>Open Configure Site Notice dialog box again and fill in title field with spaces and press OK button</li>
     * <li>"Site Notice" is set as the title.</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13970() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and enter title and text as EMPTY, click ok.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, EMPTY, EMPTY, ConfigureSiteNoticeActions.OK);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Verify the Site Notice Dashlet title and text as set to default values.
        Assert.assertEquals(siteNoticeDashlet.getTitle(), DEFAULT_SITE_NOTICE_TITLE);
        Assert.assertEquals(siteNoticeDashlet.getContent(), DEFAULT_SITE_NOTICE_TEXT);

        // Click on configure Icon and get Configure Site Notice Dialog box.
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        Assert.assertNotNull(configureSiteNotice);

        // Enter title as EMPTY, click ok.
        configureSiteNotice.setTitle(SPACE);
        configureSiteNotice.clickOnOKButton().render();

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Verify the Site Notice Dashlet title and text as set to default values.
        Assert.assertEquals(siteNoticeDashlet.getTitle(), DEFAULT_SITE_NOTICE_TITLE, "MNT-10001:'Site Notice' dashlet and title with spaces.");
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13971() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>Type the new title and edit the current message. Use the features provided to format the text; insert bulleted and numbered lists; insert links and
     * images; and help with editing.</li>
     * <li>Click Close button.</li>
     * <li>The form is closed. No changes are applied;</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13971() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = testName + siteName;
        //String fontStyle = "<font color=\"#0000FF\">";
        String fontStyle = "style=\"color: rgb(0, 0, 255);\"";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and enter title and text as given, click ok.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, title, text, ConfigureSiteNoticeActions.OK);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // Get SiteContent Configure tinymce editor.
        ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Test text as BOLD
        siteNoticeEditor.clickTextFormatter(FormatType.BOLD);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<strong>" + text + "</strong>"));

        // Test text as ITALIC
        siteNoticeEditor.clickTextFormatter(FormatType.ITALIC);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<em><strong>" + text + "</strong></em>"), "Actual: " + siteNoticeEditor.getContent() + ", Expected: " + ("<em><strong>" + text + "</strong></em>"));

        // Test text as UNDERLINED
        siteNoticeEditor.clickTextFormatter(FormatType.UNDERLINED);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<span style=\"text-decoration: underline;\"><em><strong>" + text + "</strong></em></span>"), "siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent());

        // Test BULLET on text
        siteNoticeEditor.clickTextFormatter(FormatType.BULLET);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<li><span style=\"text-decoration: underline;\"><em><strong>" + text + "</strong></em></span></li>"), "siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent());

        // Test NUMBER on test
        siteNoticeEditor.clickTextFormatter(FormatType.NUMBER);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<ol style=\"\"><li><span style=\"text-decoration: underline;\"><em><strong>" + text + "</strong></em></span></li></ol>"), "siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent());
        siteNoticeEditor.removeFormatting();

        InsertOrEditLinkPage insertOrEditLinkPage = siteNoticeEditor.clickInsertOrEditLink();
        insertOrEditLinkPage.setLinkUrl("http://electrictower.ru/");
        insertOrEditLinkPage.setTitle("amazing blog");
        insertOrEditLinkPage.clickOKButton();
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<ol><li><a href=\"http://electrictower.ru/\" data-mce-href=\"http://electrictower.ru/\">" + text + "</a><br></li></ol>"), "siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent());

        InsertOrEditImagePage insertOrEditImagePage = siteNoticeEditor.selectInsertOrEditImage();
        insertOrEditImagePage.setImageUrl("http://www.alfresco.com/sites/www/themes/alfrescodotcom/img/logo.png");
        insertOrEditImagePage.setDescription("Logo alfresco");
        insertOrEditImagePage.clickOKButton();
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<img data-mce-selected=\"1\" src=\"http://www.alfresco.com/sites/www/themes/alfrescodotcom/img/logo.png\""), "siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent());

        // Click on Close button present on SiteContent Configure dialog box.
        configureSiteNotice.clickOnCloseButton();

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // Get SiteContent Configure tinymce editor.
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Verify the text doesn't get edited.
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertFalse(siteNoticeEditor.getContent().contains("<strong>"));
        Assert.assertFalse(siteNoticeEditor.getContent().contains("<em>"));
        Assert.assertFalse(siteNoticeEditor.getContent().contains("<span style=\"text-decoration: underline;\">"));
        Assert.assertFalse(siteNoticeEditor.getContent().contains(fontStyle));
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13972() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>Type the new title and edit the current message. Use the features provided to format the text; insert bulleted and numbered lists; insert links and
     * images; and help with editing.</li>
     * <li>Click OK button.</li>
     * <li>The form is closed. All changes are applied;</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13972() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = testName + siteName;

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and enter title and text as given, click ok.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, title, text, ConfigureSiteNoticeActions.OK);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // Get SiteContent Configure tinymce editor.
        ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Test text as BOLD
        siteNoticeEditor.clickTextFormatter(FormatType.BOLD);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<strong>" + text + "</strong>"));

        // Test text as ITALIC
        siteNoticeEditor.clickTextFormatter(FormatType.ITALIC);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<em><strong>" + text + "</strong></em>"));

        // Test text as UNDERLINED
        siteNoticeEditor.clickTextFormatter(FormatType.UNDERLINED);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<span style=\"text-decoration: underline;\"><em><strong>" + text + "</strong></em></span>"), "Actual: " + siteNoticeEditor.getContent() + ", Expected: " + "<u><em><strong>" + text + "</strong></em></u>");

        // Test BULLET on text
        siteNoticeEditor.clickTextFormatter(FormatType.BULLET);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<li><span style=\"text-decoration: underline;\"><em><strong>" + text + "</strong></em></span></li>"), "siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent());

        // Test NUMBER on test
        siteNoticeEditor.clickTextFormatter(FormatType.NUMBER);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<ol style=\"\"><li><span style=\"text-decoration: underline;\"><em><strong>" + text + "</strong></em></span></li></ol>"), "siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent());

        InsertOrEditLinkPage insertOrEditLinkPage = siteNoticeEditor.clickInsertOrEditLink();
        insertOrEditLinkPage.setLinkUrl("http://electrictower.ru/");
        insertOrEditLinkPage.setTitle("amazing blog");
        insertOrEditLinkPage.clickOKButton();
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<ol style=\"\"><li><span style=\"text-decoration: underline;\"><em><strong><a href=\"http://electrictower.ru/\" data-mce-href=\"http://electrictower.ru/\">" + text + "</a></strong></em></span><br></li></ol>"), "siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent());

        InsertOrEditImagePage insertOrEditImagePage = siteNoticeEditor.selectInsertOrEditImage();
        insertOrEditImagePage.setImageUrl("http://www.alfresco.com/sites/www/themes/alfrescodotcom/img/logo.png");
        insertOrEditImagePage.setDescription("Logo alfresco");
        insertOrEditImagePage.clickOKButton();
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<img data-mce-selected=\"1\" src=\"http://www.alfresco.com/sites/www/themes/alfrescodotcom/img/logo.png\""), "siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent());

        // Click on OK button present on SiteContent Configure dialog box.
        configureSiteNotice.clickOnOKButton().render();

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // Get SiteContent Configure tinymce editor.
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Verify the text is edited.
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<strong>"));
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<em>"));
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<span style=\"text-decoration: underline;\">"));
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<ol style=\"\"><li>"));
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13973() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>Type the title and text as native chars</li>
     * <li>Click OK button.</li>
     * <li>The form is closed. All changes are applied;</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13973() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = "aあなたの名前何ですか";
        String text = "cあなたの名前何ですか";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and enter title and text as given, click ok.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, title, text, ConfigureSiteNoticeActions.OK);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Verify the title and text on Site Notice Dashlet.
        Assert.assertEquals(siteNoticeDashlet.getTitle(), title);
        Assert.assertTrue(siteNoticeDashlet.getContent().contains(text));
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13974() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>Type the title and text as wildcards</li>
     * <li>Click OK button.</li>
     * <li>The form is closed. All changes are applied;</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne", "IntermittentBugs"})
    public void AONE_13974() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = "!@#$%^&*()_+:\"|<>?;";
        String text = "!@#$%^&*()_+:\"|<>?;";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and enter title and text as given, click ok.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, title, text, ConfigureSiteNoticeActions.OK);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Verify the title and text on Site Notice Dashlet.
        Assert.assertEquals(siteNoticeDashlet.getTitle(), title);
        Assert.assertEquals(siteNoticeDashlet.getContent(), text);

        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();
        ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();
        Assert.assertEquals(configureSiteNotice.getTitle(), title, "It's bug ALF-18940.");
        Assert.assertEquals(siteNoticeEditor.getText(), text);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13975() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>Type the title and text with morethan 1024 chars</li>
     * <li>Click OK button.</li>
     * <li>The form is closed. All changes are applied;</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13975() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String titleAndText = ShareUser.getRandomStringWithNumders(1030);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and enter title and text as given, click ok.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, titleAndText, titleAndText, ConfigureSiteNoticeActions.OK);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Verify the title and text on Site Notice Dashlet.
        Assert.assertEquals(siteNoticeDashlet.getTitle(), titleAndText);
        Assert.assertEquals(siteNoticeDashlet.getContent(), titleAndText);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13977() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>Create some bulleted and numbered lists</li>
     * <li>Click OK button.</li>
     * <li>The form is closed. All changes are applied;</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13977() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = testName + "<p>" + siteName + "</p>";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // Get SiteContent Configure tinymce editor.
        ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Set title and text on editor
        configureSiteNotice.setTitle(title);
        configureSiteNotice.setText(text);

        // Set the tinymce iframe.
        // siteNoticeEditor.setTinyMceOfConfigureDialogBox(siteName);

        // test BULLET on text
        siteNoticeEditor.clickTextFormatter(FormatType.BULLET);
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<li>" + testName + "</li>"), "Actual: " + siteNoticeEditor.getContent() + ", Expected: " + "<li>" + testName + "</li>");
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<li>" + siteName + "</li></ul>"), "Verifying Actual: " + siteNoticeEditor.getContent() + ", Expected: " + "<li>" + siteName + "</li></ul>");

        // Click on OK button present on Site Notice configure dialog box.
        configureSiteNotice.clickOnOKButton().render();

        // Get Site Notice dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();
        siteNoticeEditor.removeFormatting();

        // Test NUMBER on text
        siteNoticeEditor.clickTextFormatter(FormatType.NUMBER);
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<li>" + testName + "</li>"));
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<li>" + siteName + "</li></ol>"));
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13978() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>Type any text into Text field and click Undo button</li>
     * <li>Click OK button.Changes are applied.</li>
     * <li>Click Redo button</li>
     * <li>Click OK button.Changes are applied.</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13978() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = siteName;

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and enter title and text as given, click ok.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, title, text, ConfigureSiteNoticeActions.OK);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // Get SiteContent Configure tinymce editor.
        ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // UNDO & REDO button on text
        siteNoticeEditor.clickTextFormatter(FormatType.BOLD);

        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<strong>" + text + "</strong>"), "Actual: " + siteNoticeEditor.getContent() + ", Expected: " + "<b>" + text + "</b>");

        // Verify UNDO changes
        siteNoticeEditor.clickUndo();

        configureSiteNotice.clickOnOKButton().render();
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);
        siteNoticeEditor = siteNoticeDashlet.clickOnConfigureIcon().render().getContentTinyMceEditor();

        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertFalse(siteNoticeEditor.getContent().contains("<strong>" + text + "</strong>"));

        // Verify REDO changes
        siteNoticeEditor.clickRedo();

        Assert.assertTrue(siteNoticeEditor.getContent().contains("<strong>" + text + "</strong>"));

        configureSiteNotice.clickOnOKButton().render();
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);
        siteNoticeEditor = siteNoticeDashlet.clickOnConfigureIcon().render().getContentTinyMceEditor();

        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<strong>" + text + "</strong>"));
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13984() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>Type any text in Text field, change it color and background color</li>
     * <li>Click OK button.Changes are applied.</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13984() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = siteName;
        // String fontStyle = "<font color=\"#0000FF\">";
        String fontStyle = "style=\"color: rgb(0, 0, 255);\"";
        String fontBackColorAttr = "style=\"background-color: rgb(255, 255, 0);\"";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and enter title and text as given, click ok.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, title, text, ConfigureSiteNoticeActions.OK);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // Get SiteContent Configure tinymce editor.
        ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Test text color as BLUE
        siteNoticeEditor.clickColorCode(TinyMceColourCode.BLUE);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains(fontStyle), "siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent() + ", fontStyle: " + fontStyle);
        siteNoticeEditor.removeFormatting();

        // Test text back color as yellow
        siteNoticeEditor.clickBackgroundColorCode(TinyMceColourCode.YELLOW);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains(fontBackColorAttr), "siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent() + ", fontBackColorAttr: " + fontBackColorAttr);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13979() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>Create any link</li>
     * <li>Verify that link is created successfully</li>
     * <li>update the link details</li>
     * <li>Verify that link is updated successfully</li>
     * <li>click on link in site notice dashlet</li>
     * <li>Verify that link is opened successfully</li>
     * <li>click unlink</li>
     * <li>Verify that link is removed successfully</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13979() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = testName;
        String linkTitle = "Test";
        String linkContent = "<a href=\"https://google.co.uk\" target=\"_blank\" data-mce-href=\"https://google.co.uk\">%s</a>";
        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and enter title and text as given, click ok.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, title, text, ConfigureSiteNoticeActions.OK);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // Get SiteContent Configure tinymce editor.
        ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Clickin gon Insert/Edit link
        siteNoticeEditor.selectTextFromEditor();
        siteNoticeEditor.removeFormatting();
        InsertOrEditLinkPage editLinkPage = siteNoticeEditor.clickInsertOrEditLink().render();

        // Set Title and Url,Target for link
        editLinkPage.setTarget(InsertLinkPageTargetItems.NEW_WINDOW);
        editLinkPage.setTitle(linkTitle);
        editLinkPage.setLinkUrl("https://google.co.uk");

        // click Save
        configureSiteNotice = editLinkPage.clickOKButton().render();
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Verify the link created successfully
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(linkContent, text)), "Actual: " + siteNoticeEditor.getContent() + ", Expected: " + String.format(linkContent, text));

        // Update the link
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();
        siteNoticeEditor.selectTextFromEditor();
        editLinkPage = siteNoticeEditor.clickInsertOrEditLink().render();
        linkTitle = linkTitle + "updated";
        editLinkPage.setTitle(linkTitle);
        configureSiteNotice = editLinkPage.clickOKButton().render();
        configureSiteNotice.clickOnOKButton().render();

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);
        configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();
        Assert.assertNotNull(configureSiteNotice);

        // verify the link title is updated successfully.
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(linkContent, linkTitle, text)), "It's a bug! ACE-1883");

        // Click ok on Site Notice tinymce editor to close it
        configureSiteNotice.clickOnOKButton().render();
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        String mainDrone = drone.getWindowHandle();

        // Select created link on SiteNoticeDashlet
        try
        {
            siteNoticeDashlet.selectLink(text);
        }
        catch (PageException e)
        {
            Assert.fail("It's a bug! ACE-1883");
        }

        // Verify that link is working properly
        Assert.assertTrue(switchToWindowName(drone, drone.getValue("page.google.title")));

        // Close the new window
        drone.closeWindow();

        // Back to main window
        drone.switchToWindow(mainDrone);

        configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // click on Unlink
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();
        siteNoticeEditor.selectTextFromEditor();
        siteNoticeEditor.clickUnLink();
        Assert.assertFalse(siteNoticeEditor.getContent().contains(String.format(linkContent, linkTitle, text)));
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13980() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>Create any anchor</li>
     * <li>Verify that link is created successfully</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13980() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = "";
        String anchorName = "testAnchor";
        String anchorContent = "<a class=\"mceItemAnchor\" name=\"%s\"";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and enter title and text as given, click ok.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, title, text, ConfigureSiteNoticeActions.OK);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // Get SiteContent Configure tinymce editor.
        ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Clicking on Insert/Edit Anchor
        InsertOrEditAnchorPage insertOrEditAnchor = siteNoticeEditor.selectInsertOrEditAnchor().render();

        // Creating Anchor
        insertOrEditAnchor.setName(anchorName);

        // Saving Anchor
        configureSiteNotice = insertOrEditAnchor.clickOKButton().render();
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(anchorContent, anchorName)), "It's a bug![ACE-1883] siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent() + ", String.format(anchorContent, anchorName): " + String.format(anchorContent, anchorName));

        // Update Anchor
        anchorName = anchorName + "Update";
        insertOrEditAnchor = siteNoticeEditor.selectInsertOrEditAnchor().render();
        insertOrEditAnchor.setName(anchorName);
        configureSiteNotice = insertOrEditAnchor.clickOKButton().render();
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(anchorContent, anchorName)), "It's a bug![ACE-1883] siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent() + ", String.format(anchorContent, anchorName): " + String.format(anchorContent, anchorName));
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13981() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>Create any Image</li>
     * <li>Verify that link is created successfully</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13981() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = "Test";
        String imageURL = "http://cdn2.business2community.com/wp-content/uploads/2013/04/google-.jpg";
        String imageDescription = "Alfresco Business Image";
        long imageWidth = 100;
        long imageHeight = 100;
        String image_src_content = "src=\"%s\"";
        String image_data_src_content = "data-mce-src=\"%s\"";
        String image_align_content = "align=\"bottom\"";
        String image_height_content = "height=\"%s\"";
        String image_width_content = "width=\"%s\"";
        String image_alt_content = "alt=\"%s\"";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and enter title and text as given, click ok.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, title, text, ConfigureSiteNoticeActions.OK);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // Get SiteContent Configure tinymce editor.
        ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Clicking on Insert/Edit Image
        InsertOrEditImagePage insertOrEditImage = siteNoticeEditor.selectInsertOrEditImage().render();
        Assert.assertNotNull(insertOrEditImage);
        //Assert.assertTrue(insertOrEditImage.getTitle().equalsIgnoreCase(drone.getValue("page.insert.edit.image.title")));

        // Inserting image
        insertOrEditImage.setImageUrl(imageURL);
        insertOrEditImage.setDescription("Alfresco Business Image");
        //        insertOrEditImage.setAlignment(ImageAlignment.BOTTOM);
        insertOrEditImage.setDimensions(imageWidth, imageHeight);
        configureSiteNotice = insertOrEditImage.clickOKButton().render();
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Verifying the image added successfully.
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_src_content, imageURL)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_data_src_content, imageURL).split("=")[1]));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_alt_content, imageDescription)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_height_content, imageHeight)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_width_content, imageWidth)));
        // Assert.assertTrue(siteNoticeEditor.getContent().contains(image_align_content));

        InsertOrEditImagePage insertOrEditImagePage = siteNoticeEditor.selectInsertOrEditImage();
        insertOrEditImagePage.setImageUrl("http://www.alfresco.com/sites/www/themes/alfrescodotcom/img/logo.png");
        insertOrEditImagePage.setDescription("Logo alfresco");
        insertOrEditImagePage.clickOKButton();
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<img data-mce-selected=\"1\" data-mce-src=\"http://www.alfresco.com/sites/www/themes/alfrescodotcom/img/logo.png\" src=\"http://www.alfresco.com/sites/www/themes/alfrescodotcom/img/logo.png\""), "siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent());
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13982() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: public</li>
     * <li>Open Site Dashboard</li>
     * <li>Click Customize Dashboard button</li>
     * <li>Add Site Notice Dashlet</li>
     * <li>Click the Configure icon</li>
     * <li>Configure Site Notice dialog box is opened</li>
     * <li>Create any HTML Source</li>
     * <li>Verify that HTML Source is created successfully</li>
     * <li>update HTML Source</li>
     * <li>Verify that HTML Source is updated successfully</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13982() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = "";
        String htmlSource = "<p>hello</p>";
        String newHtmlSource = "<p><strong>hello</strong></p>";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);

        // Get Site Notice Dashlet
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Open Configure Site Notice dialog box and enter title and text as given, click ok.
        ShareUserDashboard.configureSiteNoticeDialogBox(siteNoticeDashlet, title, text, ConfigureSiteNoticeActions.OK);

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // Get SiteContent Configure tinymce editor.
        ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Clicking on Insert/Edit Image
        HtmlSourceEditorPage htmlSourcePage = siteNoticeEditor.selectHtmlSourceEditor().render();
        htmlSourcePage.setHTMLSource(htmlSource);
        configureSiteNotice = htmlSourcePage.clickOKButton().render();
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();
        Assert.assertTrue(siteNoticeEditor.getContent().contains(htmlSource));

        // Update the html source
        htmlSourcePage = siteNoticeEditor.selectHtmlSourceEditor().render();
        htmlSourcePage.setHTMLSource(newHtmlSource);
        configureSiteNotice = htmlSourcePage.clickOKButton().render();
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();
        Assert.assertFalse(siteNoticeEditor.getContent().contains(htmlSource));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(newHtmlSource), "siteNoticeEditor.getContent(): " + siteNoticeEditor.getContent() + ", newHtmlSource: " + newHtmlSource);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13976() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }


    @Test(groups = { "AlfrescoOne" })
    public void AONE_13976() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = testName + "<p>" + siteName + "</p>";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // Get SiteContent Configure tinymce editor.
        ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Set title and text on editor
        configureSiteNotice.setTitle(title);
        configureSiteNotice.setText(text);

        // Click any test formatting button. Bold
        siteNoticeEditor.clickTextFormatter(FormatType.BOLD);
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<strong>" + testName + "</strong>"), "Changes aren't implemented");

        // Click on OK button present on Site Notice configure dialog box.
        configureSiteNotice.clickOnOKButton().render();

        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);
        siteNoticeDashlet.clickOnConfigureIcon().render();
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<strong>" + testName + "</strong>"), "Changes aren't saved");

        // Click any test formatting button. Italic
        siteNoticeEditor.removeFormatting();
        siteNoticeEditor.clickTextFormatter(FormatType.ITALIC);
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<em>" + testName + "</em>"), "Changes aren't implemented");

        // Click on OK button present on Site Notice configure dialog box.
        configureSiteNotice.clickOnOKButton().render();

        // Get Site Notice dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);
        siteNoticeDashlet.clickOnConfigureIcon().render();
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<em>" + testName + "</em>"), "Changes aren't saved");

        // Click any test formatting button. Underline
        siteNoticeEditor.removeFormatting();
        siteNoticeEditor.clickTextFormatter(FormatType.UNDERLINED);
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<span style=\"text-decoration: underline;\">" + testName + "</span>"), "Changes aren't implemented");

        // Click on OK button present on Site Notice configure dialog box.
        configureSiteNotice.clickOnOKButton().render();

        // Get Site Notice dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        siteNoticeDashlet.clickOnConfigureIcon().render();
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<span style=\"text-decoration: underline;\">" + testName + "</span>"), "Changes aren't saved");
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13983() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }


    @Test(groups = { "AlfrescoOne" })
    public void AONE_13983() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = testName + "<p>" + siteName + "</p>";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add SiteNotice Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_NOTICE);
        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        ConfigureSiteNoticeDialogBoxPage configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();

        // Get SiteContent Configure tinymce editor.
        ConfigureSiteNoticeTinyMceEditor siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Set title and text on editor
        configureSiteNotice.setTitle(title);
        configureSiteNotice.setText(text);

        // Click any test formatting button. Bold
        siteNoticeEditor.clickTextFormatter(FormatType.BOLD);
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<strong>" + testName + "</strong>"), "Changes aren't implemented");

        // Click on OK button present on Site Notice configure dialog box.
        configureSiteNotice.clickOnOKButton().render();

        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);
        siteNoticeDashlet.clickOnConfigureIcon().render();
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<strong>" + testName + "</strong>"), "Changes aren't saved");

        // Remove formating
        siteNoticeEditor.removeFormatting();
        //        siteNoticeEditor.clickTextFormatter(FormatType.ITALIC);
        //        Assert.assertTrue(siteNoticeEditor.getContent().contains("<em>" + testName + "</em>"), "");
        //
        //        // Click on OK button present on Site Notice configure dialog box.
        //        configureSiteNotice.clickOnOKButton().render();
        //
        //        // Get Site Notice dashlet
        //        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);
        //        siteNoticeDashlet.clickOnConfigureIcon().render();
        //        Assert.assertTrue(siteNoticeEditor.getContent().contains("<em>" + testName + "</em>"), "Changes aren't saved");
        //
        //        // Click any test formatting button. Underline
        //        siteNoticeEditor.removeFormatting();
        //        siteNoticeEditor.clickTextFormatter(FormatType.UNDERLINED);
        //        Assert.assertTrue(siteNoticeEditor.getContent().contains("<span style=\"text-decoration: underline;\">" + testName + "</span>"), "");

        // Click on OK button present on Site Notice configure dialog box.
        configureSiteNotice.clickOnOKButton().render();

        // Get Site Notice dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        siteNoticeDashlet.clickOnConfigureIcon().render();
        Assert.assertFalse(siteNoticeEditor.getContent().contains("<strong>" + testName + "</strong"), "Changes aren't saved");
    }
}