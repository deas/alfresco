package org.alfresco.share.site.wikipage;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditHtmlDocumentPage;
import org.alfresco.po.share.site.document.InlineEditPage;
import org.alfresco.po.share.site.document.MimeType;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.po.share.site.document.TinyMceEditor.FormatType;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.site.wiki.WikiPage.FONT_ATTR;
import org.alfresco.po.share.site.wiki.WikiPage.Mode;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/** 
 * @author nshah
 * @description This class is to test Wiki page.   
 */
@Listeners(FailedTestListener.class)
public class WikiPageTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(WikiPageTest.class);

    protected String testUser;
    protected String siteName = "";

    /**
     * Class includes: Tests from TestLink in Area: Site Document Library Tests
     * <ul>
     * <li>Perform an Activity on Library</li>
     * <li>Site Document library shows documents</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("[Suite ] : Start Tests in: " + testName);
    }

    @Test(groups={"DataPrepWikiPage"})
    public void dataPrep_Enterprise40x_5329() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        CustomizeSitePage customizeSizePage = ShareUser.customizeSite(drone, siteName);
        
        List<SitePageType> pageTypes = new ArrayList<SitePageType>();

        pageTypes.add(SitePageType.WIKI);
        customizeSizePage.addPages(pageTypes);

    }

    /**
     * <ul>
     * <li>Login</li>
     * <li>Create Site</li>
     * <li>Open Document library page</li>
     * <li>select added any supported document</li>
     * <li>Document details page opens</li>
     * <li>Add Folder in document library
     * <li>View Folder Details page
     * <li>Open Comment text editor</li>
     * <li>click on bold to change text to bold</li>
     * <li>click on italic to change text to italic</li>
     * <li>click on underlined to change text to underlined</li>
     * <li>click on bullet to change text with bullet point</li>
     * <li>click on number to change text with number point</li>
     * <li>click on fore colour to change text with other colour</li>
     * <li>click on undo to change text back</li>
     * <li>click on redo to change text with other colour</li>
     * <li></li>
     * </ul>
     */
    @Test(groups = "Enterprise41")
    public void enterprise40x_5329() throws Exception
    {  
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        List<String> textLines = new ArrayList<String>();
        
        WikiPage wikiPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectSiteWikiPage().render();
        wikiPage.clickOnNewPage();
        
        Assert.assertTrue(wikiPage.isWikiPageDisplayed());
        
        wikiPage.createWikiPageTitle("Wiki Page " + Math.random());
        textLines.add("This is a new Wiki text!");
        wikiPage.insertText(textLines);
        
        TinyMceEditor tinyMCEEditor = wikiPage.getTinyMCEEditor();        
        tinyMCEEditor.clickTextFormatter(FormatType.BULLET);
        Assert.assertEquals(textLines.get(0), tinyMCEEditor.getText());       
        Assert.assertTrue(tinyMCEEditor.getContent().contains("<li>"+textLines.get(0)+"</li>"));
           
        // default select is fontstyle = symbol
        wikiPage.clickFontStyle();
        Assert.assertEquals(textLines.get(0), wikiPage.retrieveWikiText("FONT"));
        
        wikiPage.clickOnRemoveFormatting();
        Assert.assertEquals(textLines.get(0), wikiPage.retrieveWikiText(""));
        // default select is font size=3
        wikiPage.clickFontSize();
        Assert.assertEquals(textLines.get(0), wikiPage.retrieveWikiText("FONT"));
        
        wikiPage.clickOnRemoveFormatting();
        Assert.assertEquals(textLines.get(0), wikiPage.retrieveWikiText(""));
                
        wikiPage.clickFontStyle();
        wikiPage.clickFontSize();
        Assert.assertEquals(textLines.get(0), wikiPage.retrieveWikiText("FONT"));
        
        wikiPage.clickSaveButton();
        
        wikiPage = wikiPage.editWikiPage();
        Assert.assertTrue("symbol".equals(wikiPage.getFontAttributeValue(FONT_ATTR.face)) ? true : false);
        Assert.assertTrue("3".equals(wikiPage.getFontAttributeValue(FONT_ATTR.size)) ? true : false);
        wikiPage.clickSaveButton();
       
    }

    @Test(groups={"DataPrepWikiPage"})
    public static void dataPrep_Enterprise40x_5330() throws Exception
    {
        /*
         * No data preparation for this test case it will be executed on user:
         * admin and use "Sample" website which has default images in its
         * library and same images will be used to render in wiki page.
         */

    }

    /**
     * <ul>
     * <li>Login As Admin user</li>
     * <li>Search "Sample" site</li>
     * <li>Navigate to "Project Wiki" page</li>
     * <li>Open Image library and click on one image</li>
     * <li>Image will render and retrieve it through given CSS.</li>
     * </ul>
     */
    @Test(groups = "Enterprise-only")
    public void enterprise40x_5330()
    {
        String siteName = "sample";
        String siteFullName = "Sample: Web Site Design Project";
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        SiteFinderPage siteFinderPage = SiteUtil.searchSite(drone, siteName);
        
        SiteDashboardPage dashBoardPage = siteFinderPage.selectSite(siteFullName);
        
        WikiPage wikiPage = (WikiPage) dashBoardPage.getDashBoardElement("Project Wiki -1");
        wikiPage.render();
        
        List<String> txtLines = new ArrayList<String>();
        txtLines.add("Wiki Text line");
        
        wikiPage.clickOnNewPage();
        
        wikiPage.createWikiPageTitle("Wiki Page Image insertion test!!!!");
        
        wikiPage.insertText(txtLines);
        
        wikiPage.isImageLibraryDisplayed();
        
        wikiPage.clickImageOfLibrary();
        
        Assert.assertEquals(1, wikiPage.imageCount(Mode.ADD));
        
        wikiPage.isImageLibraryDisplayed();
        
        wikiPage.clickImageOfLibrary();
        
        Assert.assertEquals(2, wikiPage.imageCount(Mode.ADD));        
       
        wikiPage.copyImageFromLib();
        
        wikiPage.pasteImageOnEditor();
        
        Assert.assertEquals(4, wikiPage.imageCount(Mode.ADD));
        wikiPage.clickSaveButton();
        
        wikiPage.editWikiPage();
        
        Assert.assertEquals(4, wikiPage.imageCount(Mode.EDIT));
        
        wikiPage.clickSaveButton(); 
        
        wikiPage.deleteWiki();
        
               
     
    }

    @Test(groups={"DataPrepWikiPage"})
    public void dataPrep_Enterprise40x_8413() throws Exception
    {
        String testName = getTestName();
        
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

    }

    @Test(groups = "Enterprise-only")
    public void enterprise40x_8413() throws Exception
    {      
        String testName = getTestName();
       
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String user1FileName = "test_8413_inlineEdit.html";
        String txtLine = "<a href=\"./examples.html\">Examples1</a>";
        String[] userFileInfo = { user1FileName, DOCLIB };

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Document library
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Upload a file
        docLibPage = ShareUser.uploadFileInFolder(drone, userFileInfo).render();

        InlineEditPage inLineEditPage = ((InlineEditPage) docLibPage.getFileDirectoryInfo(user1FileName).selectInlineEdit()).render();
        EditHtmlDocumentPage editHtmlDocPage = ((EditHtmlDocumentPage)inLineEditPage.getInlineEditDocumentPage(MimeType.HTML)).render();        
        editHtmlDocPage.editText(txtLine);
        Assert.assertEquals(2, editHtmlDocPage.countOfTxtsFromEditor());       
        docLibPage = (DocumentLibraryPage)editHtmlDocPage.saveText();
        Assert.assertTrue(docLibPage.isFileVisible(user1FileName));
        docLibPage.deleteItem(user1FileName);
    }

}
