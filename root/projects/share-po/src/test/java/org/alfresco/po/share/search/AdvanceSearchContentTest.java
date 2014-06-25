/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

package org.alfresco.po.share.search;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.MimeType;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to validate the advance Search Content Page.
 * 
 * @author Subashni Prasanna
 * @since 1.6
 */
@Listeners(FailedTestListener.class)
public class AdvanceSearchContentTest extends AbstractTest
{
    private String siteName;
    private File file;
    private String fileName;
    DashBoardPage dashBoard;
    SiteDashboardPage site;
    AdvanceSearchContentPage contentSearchPage;
    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date todayDate = new Date();

    /**
     * Pre test to create a content with properties set.
     * 
     * @throws Exception
     */
    @BeforeClass(groups={"Enterprise-only"})
    public void prepare() throws Exception
    {
        siteName = "AdvanceSearchContent" + System.currentTimeMillis();
        file = SiteUtil.prepareFile();
        StringTokenizer st = new StringTokenizer(file.getName(), ".");
        fileName = st.nextToken();
        File file = SiteUtil.prepareFile();
        fileName = file.getName();
        loginAs(username, password);
        SiteUtil.createSite(drone, siteName, "Public");
        SitePage site = drone.getCurrentPage().render();
        DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
        docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
        DocumentDetailsPage docDetailsPage = docPage.selectFile(fileName).render();
        EditDocumentPropertiesPage editPage = docDetailsPage.selectEditProperties().render();
        editPage.selectMimeType(MimeType.XHTML);
        editPage.setAuthor("me");
        editPage.setDescription("my description");
        editPage.setDocumentTitle("my title");
        editPage.setName("my.txt");
        docDetailsPage = editPage.selectSave().render();
        dashBoard = docDetailsPage.getNav().selectMyDashBoard().render();
    }

    @AfterClass(groups={"Enterprise-only"})
    public void deleteSite()
    {
        dashBoard.getNav().selectMyDashBoard().render();
        SiteUtil.deleteSite(drone, siteName);
    }

    /**
     * This Test case is to Test content search with all the fields entered.
     * Currently this feature is only present in Alfresco Enterprise.
     * @throws Exception
     */
    @Test(groups={"Enterprise-only","Enterprise4.2Bug"})
    public void contentSearchTest() throws Exception
    {
        Date todayDate = new Date();
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputName("my.txt");
        contentSearchPage.inputTitle("my title");
        contentSearchPage.inputDescription("my description");
        contentSearchPage.inputFromDate(dateFormat.format(todayDate));
        contentSearchPage.inputToDate(dateFormat.format(todayDate));
        contentSearchPage.inputModifier(username);
        SearchResultsPage searchResults = searchRetry();
        Assert.assertTrue(searchResults.hasResults());
        searchResults.goBackToAdvanceSearch().render();
        // Validated the entered search data is all correct
        advanceSearchFormValuesRetained();
    }

    /**
     * This method is keep searching the search until we get results.
     * 
     * @return SearchResultsPage the search page object
     * @throws Exception if error
     */
    public SearchResultsPage searchRetry() throws Exception
    {
        int counter = 0;
        int waitInMilliSeconds = 2000;
        while(counter < 3)
        {
            SearchResultsPage searchResults = contentSearchPage.clickSearch().render();
            if (searchResults.hasResults())
            {
                return searchResults;
            }
            else
            {
                counter++;
                searchResults.goBackToAdvanceSearch().render();
            }
            //double wait time to not over do solr search
            waitInMilliSeconds = (waitInMilliSeconds*2);
            synchronized (this)
            {
                try{ this.wait(waitInMilliSeconds); } catch (InterruptedException e) {}
            }
        }
        throw new Exception("search failed");
    }
    /**
     * This method is Check the entered value in content search is all correct.
     * 
     * @throws Exception if error
     */
    public void advanceSearchFormValuesRetained() throws Exception
    {
        Assert.assertEquals("my.txt", contentSearchPage.getName());
        Assert.assertEquals("my title", contentSearchPage.getTitle());
        Assert.assertEquals("my description", contentSearchPage.getDescription());
        Assert.assertEquals(dateFormat.format(todayDate), contentSearchPage.getFromDate());
        Assert.assertEquals(dateFormat.format(todayDate), contentSearchPage.getToDate());
        Assert.assertEquals(username, contentSearchPage.getModifier());
    }

    /**
     * Test to validate mime type
     */
    @Test(groups={"Enterprise-only","Enterprise4.2Bug"})
    public void mimeTypeSearchTest() throws Exception
    {
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.selectMimeType("XHTML");
        SearchResultsPage searchResults = searchRetry();
        Assert.assertTrue(searchResults.hasResults());
        searchResults.goBackToAdvanceSearch().render();
        // Assert.assertEquals("XHTML",contentSearchPage.getMimeType());
    }

    /**
     * This Test is to check when I pass Null value to the keyword field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Enterprise-only")
    public void keywordNullCheckTest()
    {
        AdvanceSearchContentPage searchpage = new AdvanceSearchContentPage(drone);
        searchpage.inputKeyword(null);
    }

    /**
     * This Test is to check when I pass Null value to the name field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Enterprise-only")
    public void nameNullCheckTest()
    {
        AdvanceSearchContentPage searchpage = new AdvanceSearchContentPage(drone);
        searchpage.inputName(null);
    }

    /**
     * This Test is to check when I pass Null value to the title field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Enterprise-only")
    public void titleNullCheckTest()
    {
        AdvanceSearchContentPage searchpage = new AdvanceSearchContentPage(drone);
        searchpage.inputTitle(null);
    }

    /**
     * This Test is to check when I pass Null value to the description field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Enterprise-only")
    public void descriptionNullCheckTest()
    {
        AdvanceSearchContentPage searchpage = new AdvanceSearchContentPage(drone);
        searchpage.inputDescription(null);
    }

    /**
     * This Test is to check when I pass Null value to the modifier field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Enterprise-only")
    public void modifierNullCheckTest()
    {
        AdvanceSearchContentPage searchpage = new AdvanceSearchContentPage(drone);
        searchpage.inputModifier(null);
    }

    /**
     * This Test is to check when I pass Null value to the from Date field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Enterprise-only")
    public void fromDateNullCheckTest()
    {
        AdvanceSearchContentPage searchpage = new AdvanceSearchContentPage(drone);
        searchpage.inputFromDate(null);
    }

    /**
     * This Test is to check when I pass Null value to the To date field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Enterprise-only")
    public void toDateNullCheckTest()
    {
        AdvanceSearchContentPage searchpage = new AdvanceSearchContentPage(drone);
        searchpage.inputToDate(null);
    }
    
    /**
     * Test to validate modified from date.
     */
    @Test(groups={"Enterprise-only"})
    public void validateFromDateTest() throws Exception
    {
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputFromDate(dateFormat.format(todayDate));
        Assert.assertTrue(contentSearchPage.isValidFromDate());
    }
    
    /**
     * Test to validate invalid modified from date.
     */
    @Test(groups={"Enterprise-only"})
    public void validateInvalidFromDateTest() throws Exception
    {
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputFromDate("0/06/2013");
        Assert.assertFalse(contentSearchPage.isValidFromDate());
    }

    /**
     * This test is to test whether the first result item is Document or not.
     * 
     * @throw Exception
     */
    @Test(dependsOnMethods = "toDateNullCheckTest", groups="Enterprise-only")
    public void testIsFolder() throws Exception
    {
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputName("my.txt");
        SearchResultsPage searchResults = searchRetry();
        Assert.assertTrue(searchResults.hasResults());
        Assert.assertFalse(searchResults.getResults().get(0).isFolder());
        Assert.assertTrue(searchResults.getResults().get(0).getFolderNamesFromContentPath().size() == 0);
    }

    /**
     * This test is to click on download icon and view in browser icon of the selected search result item.
     * Note: This test will be enabled only with chrome browser execution.
     * @throw Exception
     */
    @Test(dependsOnMethods = "testIsFolder", groups = { "Enterprise-only", "chromeOnly" }, enabled=false)
    public void testClickOnDownloadAndViewInBrowserLink() throws Exception
    {
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputName("my.txt");
        SearchResultsPage searchResults = searchRetry();
        Assert.assertTrue(searchResults.hasResults());
        SearchResultItem searchResultItem = searchResults.getResults().get(0);
        searchResultItem.clickOnDownloadIcon();
        
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputName("my.txt");
        searchResults = searchRetry();
        Assert.assertTrue(searchResults.hasResults());
        searchResultItem = searchResults.getResults().get(0);
        String url = searchResultItem.clickOnViewInBrowserIcon();
        Assert.assertNotNull(url);
        Assert.assertTrue(url.contains("my.txt"));
    }
    
    /**
     * This test is to click on download icon and view in browser icon of the selected search result item.
     * Note: This test will be enabled only with chrome browser execution.
     * @throw Exception
     */
    @Test(dependsOnMethods="testIsFolder", groups={"Enterprise-only"}, enabled=false)
    public void testGetFolderNamesFromPath() throws Exception
    {
        File newFile = SiteUtil.prepareFile("folderPath");
        String fileName =  newFile.getName();

        drone.navigateTo(shareUrl + String.format("/page/site/%s/documentlibrary", siteName));
        DocumentLibraryPage docPage = drone.getCurrentPage().render();
        NewFolderPage folderPage = docPage.getNavigation().selectCreateNewFolder().render();
        folderPage.createNewFolder("Attachments", "testFolder Description").render();
        docPage.selectFolder("Attachments").render();
        
        folderPage = docPage.getNavigation().selectCreateNewFolder().render();
        docPage = folderPage.createNewFolder("TestFolder", "testFolder Description").render();
        docPage.selectFolder("TestFolder").render();
        
        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
        docPage = (DocumentLibraryPage) upLoadPage.uploadFile(newFile.getCanonicalPath());
        docPage.render();
        
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputName(fileName);
        SearchResultsPage searchResults = searchRetry();
        Assert.assertTrue(searchResults.hasResults());
        SearchResultItem searchResultItem = searchResults.getResults().get(0);
        
        List<String> list = searchResultItem.getFolderNamesFromContentPath();
        Assert.assertTrue(list.size() > 0);
        Assert.assertTrue(list.size() == 2);
        Assert.assertTrue(list.get(0).equalsIgnoreCase("Attachments"));
        Assert.assertTrue(list.get(1).equalsIgnoreCase("TestFolder"));
    }
}
