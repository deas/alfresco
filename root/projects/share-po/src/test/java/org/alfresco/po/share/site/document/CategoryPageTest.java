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
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class CategoryPageTest extends AbstractDocumentTest
{
    
    private String siteName;
    private DocumentLibraryPage documentLibPage;
    DashBoardPage dashBoard;
    DocumentDetailsPage detailsPage;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups="alfresco-one")
    public void prepare() throws Exception
    {
        dashBoard = loginAs(username, password).render();
        siteName = "CreateContentPageTest" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        SiteDashboardPage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        CreatePlainTextContentPage contentPage = documentLibPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("Test Doc");
        contentDetails.setTitle("Test");
        contentDetails.setDescription("Desc");
        contentDetails.setContent("Shan Test Doc");
        detailsPage = contentPage.create(contentDetails).render();
        assertNotNull(detailsPage);
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(DocumentAspect.CLASSIFIABLE);
        aspectsPage = aspectsPage.add(aspects).render();
        detailsPage = aspectsPage.clickApplyChanges().render();
    }

    @AfterClass(groups="alfresco-one")
    public void teardown() throws Exception
    {
        if (siteName != null)
        {
            SiteUtil.deleteSite(drone, siteName);
        }
    }
    
    @Test
    public void getAddAbleCatgories()
    {
        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
        CategoryPage categoryPage = propertiesPage.getCategory().render();
        List<Categories> addAbleCategories = categoryPage.getAddAbleCatgories();
        assertTrue(addAbleCategories.size() > 0);
        assertTrue(addAbleCategories.contains(Categories.TAGS));
        categoryPage.add(Arrays.asList(Categories.TAGS)).render();
        List<Categories> addedCategories = categoryPage.getAddedCatgories();
        assertTrue(addedCategories.size() > 0);
        assertTrue(addedCategories.contains(Categories.TAGS));
        propertiesPage = categoryPage.clickCancel().render();
        detailsPage = propertiesPage.selectCancel().render();
    }
    
    @Test(dependsOnMethods="getAddAbleCatgories")
    public void clickCancel()
    {
        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
        CategoryPage categoryPage = propertiesPage.getCategory().render();
        categoryPage.add(Arrays.asList(Categories.TAGS));
        List<Categories> addedCategories = categoryPage.getAddedCatgories();
        assertTrue(addedCategories.size() > 0);
        assertTrue(addedCategories.contains(Categories.TAGS));
        propertiesPage = categoryPage.clickCancel().render();
        detailsPage = propertiesPage.selectSave().render();
        propertiesPage = detailsPage.selectEditProperties().render();
        categoryPage = propertiesPage.getCategory().render();
        addedCategories = categoryPage.getAddedCatgories();
        assertTrue(addedCategories.size() == 0);
        propertiesPage = categoryPage.clickCancel().render();
        detailsPage = propertiesPage.selectCancel().render();
    }
    
    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods="clickCancel")
    public void clickOk()
    {
        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
        CategoryPage categoryPage = propertiesPage.getCategory().render();
        categoryPage.add(Arrays.asList(Categories.TAGS, Categories.REGIONS));
        List<Categories> addedCategories = categoryPage.getAddedCatgories();
        assertTrue(addedCategories.size() > 0);
        assertTrue(addedCategories.contains(Categories.TAGS));
        propertiesPage = categoryPage.clickOk().render();
        addedCategories = propertiesPage.getCategories();
        assertTrue(addedCategories.size() > 0);
        assertTrue(addedCategories.contains(Categories.TAGS));
        categoryPage = propertiesPage.getCategory().render();
        addedCategories = categoryPage.getAddedCatgories();
        assertTrue(addedCategories.size() > 0);
        assertTrue(addedCategories.contains(Categories.TAGS));
        propertiesPage = categoryPage.clickCancel().render();
        detailsPage = propertiesPage.selectSave().render();
        addedCategories = (List<Categories>) detailsPage.getProperties().get("Categories");
        assertTrue(addedCategories.size() == 2);
        for (Categories categories : addedCategories)
        {
            Arrays.asList(Categories.TAGS, Categories.REGIONS).contains(categories);
        }
        documentLibPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
        FileDirectoryInfo  directoryInfo = documentLibPage.getFileDirectoryInfo("Test Doc");
        addedCategories = directoryInfo.getCategories();
        assertTrue(addedCategories.size() > 0);
        assertTrue(addedCategories.contains(Categories.TAGS));
    }

}
