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

import org.alfresco.po.share.dashlet.ConfigureSiteNoticeDialogBoxPage;
import org.alfresco.po.share.dashlet.ConfigureSiteNoticeTinyMceEditor;
import org.alfresco.po.share.dashlet.HtmlSourceEditorPage;
import org.alfresco.po.share.dashlet.InsertOrEditAnchorPage;
import org.alfresco.po.share.dashlet.InsertOrEditImagePage;
import org.alfresco.po.share.dashlet.InsertOrEditImagePage.ImageAlignment;
import org.alfresco.po.share.dashlet.InsertOrEditLinkPage;
import org.alfresco.po.share.dashlet.InsertOrEditLinkPage.InsertLinkPageTargetItems;
import org.alfresco.po.share.dashlet.SiteNoticeDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.TinyMceColourCode;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.TinyMceEditor.FormatType;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ConfigureSiteNoticeActions;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
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

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_8948() throws Exception
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
    @Test
    public void ALF_8948() throws Exception
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

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_8949() throws Exception
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
    @Test
    public void ALF_8949() throws Exception
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

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_8950() throws Exception
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
    @Test
    public void ALF_8950() throws Exception
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

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_8951() throws Exception
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
    @Test
    public void ALF_8951() throws Exception
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

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_8952() throws Exception
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
    @Test
    public void ALF_8952() throws Exception
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

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_8962() throws Exception
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
    @Test
    public void ALF_8962() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = testName + siteName;
        String fontStyle = "<font color=\"#0000FF\">";

        if (alfrescoVersion.isCloud())
        {
            fontStyle = "<font style=\"color: rgb(0, 0, 255);\">";
        }

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
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<b>" + text + "</b>"));

        // Test text as ITALIC
        siteNoticeEditor.clickTextFormatter(FormatType.ITALIC);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<i><b>" + text + "</b></i>"));

        // Test text as UNDERLINED
        siteNoticeEditor.clickTextFormatter(FormatType.UNDERLINED);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<u><i><b>" + text + "</b></i></u>"));

        // Test BULLET on text
        siteNoticeEditor.clickTextFormatter(FormatType.BULLET);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<li><u><i><b>" + text + "</b></i></u></li>"));

        // Test NUMBER on test
        siteNoticeEditor.clickTextFormatter(FormatType.NUMBER);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<ol><li><u><i><b>" + text + "</b></i></u></li></ol>"));
        siteNoticeEditor.removeFormatting();

        // TODO : Inserting images and links functionality will be added once it is implemented.

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
        Assert.assertFalse(siteNoticeEditor.getContent().contains("<b>"));
        Assert.assertFalse(siteNoticeEditor.getContent().contains("<i>"));
        Assert.assertFalse(siteNoticeEditor.getContent().contains("<u>"));
        Assert.assertFalse(siteNoticeEditor.getContent().contains(fontStyle));
    }

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_8963() throws Exception
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
    @Test
    public void ALF_8963() throws Exception
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
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<b>" + text + "</b>"));

        // Test text as ITALIC
        siteNoticeEditor.clickTextFormatter(FormatType.ITALIC);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<i><b>" + text + "</b></i>"));

        // Test text as UNDERLINED
        siteNoticeEditor.clickTextFormatter(FormatType.UNDERLINED);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<u><i><b>" + text + "</b></i></u>"));

        // Test BULLET on text
        siteNoticeEditor.clickTextFormatter(FormatType.BULLET);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<li><u><i><b>" + text + "</b></i></u></li>"));

        // Test NUMBER on test
        siteNoticeEditor.clickTextFormatter(FormatType.NUMBER);
        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<ol><li><u><i><b>" + text + "</b></i></u></li></ol>"));

        // TODO : Inserting images and links functionality will be added once it is implemented.

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
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<b>"));
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<i>"));
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<u>"));
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<ol><li>"));
    }

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_8964() throws Exception
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
    @Test
    public void ALF_8964() throws Exception
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
        ;

        // Verify the title and text on Site Notice Dashlet.
        Assert.assertEquals(siteNoticeDashlet.getTitle(), title);
        Assert.assertTrue(siteNoticeDashlet.getContent().contains(text));
    }

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_8965() throws Exception
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
    @Test
    public void ALF_8965() throws Exception
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
    }

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_8966() throws Exception
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
    @Test
    public void ALF_8966() throws Exception
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

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_10821() throws Exception
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
    @Test
    public void ALF_10821() throws Exception
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
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<ul><li>" + testName + "</li>"));
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<li>" + siteName + "</li></ul>"));

        // Click on OK button present on Site Notice configure dialog box.
        configureSiteNotice.clickOnOKButton().render();

        // Get Site Notice dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        // Click on configure Icon and gets Configure Site Notice Dialog box.
        configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();
        siteNoticeEditor.removeFormatting();

        // Test NUMBER on test
        siteNoticeEditor.clickTextFormatter(FormatType.NUMBER);
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<ol><li>" + testName + "</li>"));
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<li>" + siteName + "</li></ol>"));
    }

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_10822() throws Exception
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
    @Test
    public void ALF_10822() throws Exception
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
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<b>" + text + "</b>"));

        // Verify UNDO changes
        siteNoticeEditor.clickUndo();

        configureSiteNotice.clickOnOKButton().render();
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);
        siteNoticeEditor = siteNoticeDashlet.clickOnConfigureIcon().render().getContentTinyMceEditor();

        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertFalse(siteNoticeEditor.getContent().contains("<b>" + text + "</b>"));

        // Verify REDO changes
        siteNoticeEditor.clickRedo();

        Assert.assertTrue(siteNoticeEditor.getContent().contains("<b>" + text + "</b>"));

        configureSiteNotice.clickOnOKButton().render();
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);
        siteNoticeEditor = siteNoticeDashlet.clickOnConfigureIcon().render().getContentTinyMceEditor();

        Assert.assertEquals(text, siteNoticeEditor.getText());
        Assert.assertTrue(siteNoticeEditor.getContent().contains("<b>" + text + "</b>"));
    }

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_10828() throws Exception
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
    @Test
    public void ALF_10828() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = siteName;
        String fontStyle = "<font color=\"#0000FF\">";
        String fontBackColorAttr = "<font style=\"background-color: rgb(0, 0, 255);\">";

        if (alfrescoVersion.isCloud())
        {
            fontStyle = "<font style=\"color: rgb(0, 0, 255);\">";
        }

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
        Assert.assertTrue(siteNoticeEditor.getContent().contains(fontStyle));
        siteNoticeEditor.removeFormatting();

        // Test text back color as yellow
        siteNoticeEditor.clickBackgroundColorCode(TinyMceColourCode.BLUE);
        Assert.assertEquals(text, siteNoticeEditor.getText());        
        Assert.assertTrue(siteNoticeEditor.getContent().contains(fontBackColorAttr));
    }

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_10823() throws Exception
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
    @Test
    public void ALF_10823() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = testName;
        String linkTitle = "Test";
        String linkContent = "<a target=\"_blank\" title=\"%s\" href=\"https://google.co.uk\" data-mce-href=\"https://google.co.uk\">%s</a>";
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
        editLinkPage.setTarget(InsertLinkPageTargetItems.OPEN_LINK_IN_NEW_WINDOW);
        editLinkPage.setTitle(linkTitle);
        editLinkPage.setLinkUrl("https://google.co.uk");

        // click Save
        configureSiteNotice = editLinkPage.clickInsertOrUpdateButton().render();
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Verify the link created successfully
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(linkContent, linkTitle, text)));

        // Update the link
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();
        siteNoticeEditor.selectTextFromEditor();
        editLinkPage = siteNoticeEditor.clickInsertOrEditLink().render();
        linkTitle = linkTitle + "updated";
        editLinkPage.setTitle(linkTitle);
        configureSiteNotice = editLinkPage.clickInsertOrUpdateButton().render();
        configureSiteNotice.clickOnOKButton().render();

        // Get Site Notice Dashlet
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);
        configureSiteNotice = siteNoticeDashlet.clickOnConfigureIcon().render();
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();
        Assert.assertNotNull(configureSiteNotice);

        // verify the link title is updated successfully.
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(linkContent, linkTitle, text)));

        // Click ok on Site Notice tinymce editor to close it
        configureSiteNotice.clickOnOKButton().render();
        siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);

        String mainDrone = drone.getWindowHandle();

        // Select created link on SiteNoticeDashlet
        siteNoticeDashlet.selectLink(text);

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

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_10824() throws Exception
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
    @Test
    public void ALF_10824() throws Exception
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
        configureSiteNotice = insertOrEditAnchor.clickInsertOrUpdateButton().render();
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(anchorContent, anchorName)));
    }

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_10825() throws Exception
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
    @Test
    public void ALF_10825() throws Exception
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
        Assert.assertTrue(insertOrEditImage.getTitle().equalsIgnoreCase(drone.getValue("page.insert.edit.image.title")));

        // Inserting image
        insertOrEditImage.setImageUrl(imageURL);
        insertOrEditImage.setDescription("Alfresco Business Image");
        insertOrEditImage.setAlignment(ImageAlignment.BOTTOM);
        insertOrEditImage.setDimensions(imageWidth, imageHeight);
        configureSiteNotice = insertOrEditImage.clickInsertOrUpdateButton().render();
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();

        // Verifying the image added successfully.
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_src_content,imageURL)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_data_src_content,imageURL)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_alt_content, imageDescription)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_height_content, imageHeight)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(String.format(image_width_content, imageWidth)));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(image_align_content));
    }

    @Test(groups = { "DataPrepSiteNoticeDashlet" })
    public void dataPrep_ALF_10826() throws Exception
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
    @Test
    public void ALF_10826() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String title = testName;
        String text = "";
        String htmlSource = "<p>hello</p>";
        String newHtmlSource = "<p><b>hello</b></p>";

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
        configureSiteNotice = htmlSourcePage.clickInsertOrUpdateButton().render();
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();
        Assert.assertTrue(siteNoticeEditor.getContent().contains(htmlSource));

        // Update the html source
        htmlSourcePage = siteNoticeEditor.selectHtmlSourceEditor().render();
        htmlSourcePage.setHTMLSource(newHtmlSource);
        configureSiteNotice = htmlSourcePage.clickInsertOrUpdateButton().render();
        siteNoticeEditor = configureSiteNotice.getContentTinyMceEditor();
        Assert.assertFalse(siteNoticeEditor.getContent().contains(htmlSource));
        Assert.assertTrue(siteNoticeEditor.getContent().contains(newHtmlSource));
    }

}