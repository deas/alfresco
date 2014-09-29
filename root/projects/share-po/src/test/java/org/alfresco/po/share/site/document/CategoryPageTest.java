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
import static org.testng.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.enums.ViewType;
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
    
    private String categoryTags;
    private String categoryRegions;


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
        documentLibPage = documentLibPage.getNavigation().selectDetailedView().render();
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
        categoryTags = drone.getValue("category.tags");
        categoryRegions = drone.getValue("category.regions");

    }

    @AfterClass(groups="alfresco-one")
    public void teardown() throws Exception
    {
        if (siteName != null)
        {
            SiteUtil.deleteSite(drone, siteName);
        }
    }
    
    @Test(groups = {"Enterprise4.2"})
    public void getAddAbleCatgories()
    {
        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
        CategoryPage categoryPage = propertiesPage.getCategory().render();
        List<String> addAbleCategories = categoryPage.getAddAbleCatgoryList();
        assertTrue(addAbleCategories.size() > 0);
        assertTrue(addAbleCategories.contains(Categories.TAGS.getValue()));
        categoryPage.addCategories(Arrays.asList(Categories.TAGS.getValue())).render();
        List<String> addedCategories = categoryPage.getAddedCatgoryList();
        assertTrue(addedCategories.size() > 0);
        assertTrue(addedCategories.contains(Categories.TAGS.getValue()));
        propertiesPage = categoryPage.clickCancel().render();
        detailsPage = propertiesPage.selectCancel().render();
    }
    
    @Test(dependsOnMethods="getAddAbleCatgories", groups = {"Enterprise4.2"})
    public void getAddAbleCatgoryList()
    {
        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
        CategoryPage categoryPage = propertiesPage.getCategory().render();
        List<String> addAbleCategoryList = categoryPage.getAddAbleCatgoryList();
        assertFalse(addAbleCategoryList.isEmpty(), "addable category list is empty.");
        assertTrue(addAbleCategoryList.contains(categoryTags), categoryTags + "is not in category list.");
        categoryPage.addCategories(Arrays.asList(categoryTags)).render();
        categoryPage.addCategories(Arrays.asList(drone.getValue("category.english")), drone.getValue("category.languages")).render();
        List<String> addedCategories = categoryPage.getAddedCatgoryList();
        assertTrue(addedCategories.size() > 0);
        assertTrue(addedCategories.contains(categoryTags));
        assertTrue(addedCategories.contains(drone.getValue("category.english")));
        propertiesPage = categoryPage.clickCancel().render();
        detailsPage = propertiesPage.selectCancel().render();
    }
    
    @Test(dependsOnMethods="getAddAbleCatgories", groups = {"Enterprise4.2"})
    public void clickCancel()
    {
        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
        CategoryPage categoryPage = propertiesPage.getCategory().render();
        categoryPage.addCategories(Arrays.asList(categoryTags));
        List<String> addedCategories = categoryPage.getAddedCatgoryList();
        assertTrue(addedCategories.size() > 0);
        assertTrue(addedCategories.contains(categoryTags));
        propertiesPage = categoryPage.clickCancel().render();
        detailsPage = propertiesPage.selectSave().render();
        propertiesPage = detailsPage.selectEditProperties().render();
        categoryPage = propertiesPage.getCategory().render();
        addedCategories = categoryPage.getAddedCatgoryList();
        assertTrue(addedCategories.size() == 0);
        propertiesPage = categoryPage.clickCancel().render();
        detailsPage = propertiesPage.selectCancel().render();
    }
    
    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods="clickCancel", groups = {"Enterprise4.2"})
    public void clickOk()
    {
        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
        CategoryPage categoryPage = propertiesPage.getCategory().render();
        categoryPage.addCategories(Arrays.asList(categoryTags, categoryRegions));
        List<String> addedCategoryList = categoryPage.getAddedCatgoryList();
        assertTrue(addedCategoryList.size() > 0);
        assertTrue(addedCategoryList.contains(categoryTags));
        propertiesPage = categoryPage.clickOk().render();
        addedCategoryList = propertiesPage.getCategoryList();
        assertTrue(addedCategoryList.size() > 0);
        assertTrue(addedCategoryList.contains(categoryTags));
        categoryPage = propertiesPage.getCategory().render();
        addedCategoryList = categoryPage.getAddedCatgoryList();
        assertTrue(addedCategoryList.size() > 0);
        assertTrue(addedCategoryList.contains(categoryTags));
        propertiesPage = categoryPage.clickCancel().render();
        detailsPage = propertiesPage.selectSave().render();
        List<Categories> addedCategories = (List<Categories>) detailsPage.getProperties().get("Categories");
        assertTrue(addedCategories.size() == 2);
        for (Categories category : addedCategories)
        {
            Arrays.asList(categoryTags, categoryRegions).contains(category);
        }
        documentLibPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.setViewType(ViewType.DETAILED_VIEW);
        FileDirectoryInfo  directoryInfo = documentLibPage.getFileDirectoryInfo("Test Doc");
        addedCategoryList = directoryInfo.getCategoryList();
        assertTrue(addedCategoryList.size() > 0);
        assertTrue(addedCategoryList.contains(categoryTags));
    }
    
   /* @Test(dependsOnMethods="clickOk", groups = {"Enterprise4.2"})
    public void clickRemove()
    {
        FileDirectoryInfo  directoryInfo = documentLibPage.getFileDirectoryInfo("Test Doc");
        EditDocumentPropertiesPage propertiesPage = directoryInfo.selectEditProperties().render();
        
        CategoryPage categoryPage = propertiesPage.getCategory().render();
        categoryPage.removeCategories(Arrays.asList(categoryTags));
        List<String> addedCategoryList = categoryPage.getAddedCatgoryList();
        //assertTrue(addedCategoryList.size() > 0);
        assertFalse(addedCategoryList.contains(categoryTags));
        propertiesPage = categoryPage.clickOk().render();
        addedCategoryList = propertiesPage.getCategoryList();
        //assertTrue(addedCategoryList.size() > 0);
        assertFalse(addedCategoryList.contains(categoryTags));
        //categoryPage = propertiesPage.getCategory().render();
        //addedCategoryList = categoryPage.getAddedCatgoryList();
        //assertTrue(addedCategoryList.size() > 0);
        //assertTrue(addedCategoryList.contains(categoryTags));
        //propertiesPage = categoryPage.clickCancel().render();
        directoryInfo = propertiesPage.selectSave().render();

        documentLibPage.setViewType(ViewType.DETAILED_VIEW);
        directoryInfo = documentLibPage.getFileDirectoryInfo("Test Doc");
        addedCategoryList = directoryInfo.getCategoryList();
        //assertTrue(addedCategoryList.size() > 0);
        assertFalse(addedCategoryList.contains(categoryTags));
    }*/
}
