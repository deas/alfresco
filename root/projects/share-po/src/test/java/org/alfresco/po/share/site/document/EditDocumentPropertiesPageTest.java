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
package org.alfresco.po.share.site.document;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
/**
 * Integration test to verify document CRUD is operating correctly.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups={"alfresco-one", "Firefox17Ent"})
public class EditDocumentPropertiesPageTest extends AbstractDocumentTest
{
    private String siteName;
    private String fileName;
    private String title;
    private File file;
    private String tagName;
    DashBoardPage dashBoard;
    SiteDashboardPage site;

    @AfterClass(groups={"alfresco-one"})	
    public void quit()
    {
        if (site != null)
        {
            SiteUtil.deleteSite(drone, siteName);
        }
        closeWebDrone();
    }
    
    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups={"alfresco-one"})
    public void prepare()throws Exception
    {
        siteName = "editDocumentSiteTest" + System.currentTimeMillis();
        file = SiteUtil.prepareFile();
        StringTokenizer st = new StringTokenizer(file.getName(), ".");
        fileName = st.nextToken();
        title = "";
        tagName = siteName;

        File file = SiteUtil.prepareFile();
        fileName = file.getName();
        loginAs(username, password);
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        site = createSite.createNewSite(siteName).render();
        DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        // DocumentLibraryPage docPage =
        // getDocumentLibraryPage(siteName).render();
        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
        docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
        docPage.selectFile(fileName);
    }
    
    @Test
    public void editPropertiesAndCancel() throws Exception
    {
        DocumentDetailsPage detailsPage = drone.getCurrentPage().render();
        EditDocumentPropertiesPage editPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPage.isEditPropertiesVisible());
        Assert.assertEquals(editPage.getName(), fileName);
        Assert.assertEquals(editPage.getDocumentTitle(), title);
        Assert.assertEquals(editPage.getDescription(), "");
        Assert.assertEquals(editPage.getAuthor(), "");
        Assert.assertFalse(editPage.hasTags());
        detailsPage = editPage.selectCancel().render();
        Assert.assertTrue(detailsPage.isDocumentDetailsPage());
    }

    @Test(dependsOnMethods="editPropertiesAndCancel")
    public void editPropertiesAndSave() throws Exception
    {
        DocumentDetailsPage detailsPage = drone.getCurrentPage().render();
        EditDocumentPropertiesPage editPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPage.isEditPropertiesVisible());
        Assert.assertEquals(editPage.getName(), fileName);
        Assert.assertEquals(editPage.getDocumentTitle(), title);
        Assert.assertEquals(editPage.getDescription(), "");
        Assert.assertEquals(editPage.getMimeType(), "Plain Text");
        Assert.assertEquals(editPage.getAuthor(), "");
        Assert.assertFalse(editPage.hasTags());
        editPage.setAuthor("me");
        editPage.setDescription("my description");
        editPage.setDocumentTitle("my title");
        editPage.setName("my.txt");
        editPage.selectMimeType(MimeType.XHTML);
        TagPage tagPage = editPage.getTag().render();
        tagPage = tagPage.enterTagValue(tagName).render();
        EditDocumentPropertiesPage editpage = tagPage.clickOkButton().render();
        detailsPage = editpage.selectSave().render();      
        Map<String, Object> properties = detailsPage.getProperties();
        Assert.assertEquals(properties.get("Author"), "me");
        Assert.assertEquals(properties.get("Description"), "my description");
        Assert.assertEquals(properties.get("Title"), "my title");
        Assert.assertEquals(properties.get("Name"), "my.txt");
        Assert.assertEquals(properties.get("Mimetype"), "XHTML");
        //Check if input fields show correct value
        List<String> tagName = detailsPage.getTagList();
        Assert.assertFalse((tagName.isEmpty()) , "tag were not added correctly");
    }
    
    @Test(dependsOnMethods="editPropertiesAndSave")
    public void checkInputFieldsHaveUpdatedValues() throws Exception
    {
    	DocumentDetailsPage detailsPage = drone.getCurrentPage().render();
    	EditDocumentPropertiesPage editPage = detailsPage.selectEditProperties().render();
    	Assert.assertTrue(editPage.isEditPropertiesVisible());
    	Assert.assertEquals(editPage.getName(), "my.txt");
		Assert.assertEquals(editPage.getDocumentTitle(), "my title");
		Assert.assertEquals(editPage.getDescription(), "my description");
		Assert.assertEquals(editPage.getMimeType(), "XHTML");
		Assert.assertEquals(editPage.getAuthor(), "me");
		Assert.assertTrue(editPage.hasTags());
    }
}
