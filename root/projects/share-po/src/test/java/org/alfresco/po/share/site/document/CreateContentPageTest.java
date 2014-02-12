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
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page is operating correctly.
 * 
 * @author Shan Nagarajan
 * @since 1.61.
 */
@Listeners(FailedTestListener.class)
public class CreateContentPageTest extends AbstractDocumentTest
{
    private String siteName;
    private DocumentLibraryPage documentLibPage;
    DashBoardPage dashBoard;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unused")
    @BeforeClass(groups="alfresco-one")
    private void prepare() throws Exception
    {
        dashBoard = loginAs(username, password).render();
        siteName = "CreateContentPageTest" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
    }

    @AfterClass(alwaysRun=true, groups="alfresco-one")
    public void teardown() throws Exception
    {
        if (siteName != null)
        {
            SiteUtil.deleteSite(drone, siteName);
        }
    }
    
    /**
     * Test case to create content with plain text.
     * 
     * @throws Exception
     */
    @Test(expectedExceptions=UnsupportedOperationException.class, groups="alfresco-one")
    public void createContentWithNullName() throws Exception
    {
        CreatePlainTextContentPage contentPage = new CreatePlainTextContentPage(drone);
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(null);
        contentDetails.setTitle("Test");
        contentDetails.setDescription("Desc");
        contentDetails.setContent("Shan Test Doc");
        contentPage.create(contentDetails);
    }
    /**
     * Test case to create content with plain text.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "createContentWithNullName", expectedExceptions=UnsupportedOperationException.class, groups="alfresco-one")
    public void createContentWithEmptyName() throws Exception
    {
        CreatePlainTextContentPage contentPage = new CreatePlainTextContentPage(drone);
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("");
        contentDetails.setTitle("Test");
        contentDetails.setDescription("Desc");
        contentDetails.setContent("Shan Test Doc");
        contentPage.create(contentDetails);
    }
    
    /**
     * Test case to create content with plain text.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "createContentWithEmptyName", expectedExceptions=UnsupportedOperationException.class, groups="alfresco-one")
    public void createContentWithNullDetails() throws Exception
    {
        CreatePlainTextContentPage contentPage = new CreatePlainTextContentPage(drone);
        contentPage.create(null);
    }
    /**
     * Test case to create content with plain text.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "createContentWithNullDetails", groups="Enterprise-only")
    public void createContent() throws Exception
    {
        SiteDashboardPage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        CreatePlainTextContentPage contentPage = documentLibPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("Test Doc");
        contentDetails.setTitle("Test");
        contentDetails.setDescription("Desc");
        contentDetails.setContent("Shan Test Doc");
        DocumentDetailsPage detailsPage = contentPage.create(contentDetails).render();
        assertNotNull(detailsPage);
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(DocumentAspect.VERSIONABLE);
        aspects.add(DocumentAspect.CLASSIFIABLE);
        detailsPage = aspectsPage.add(aspects).render();
        detailsPage.render();
        EditTextDocumentPage editPage = detailsPage.selectInlineEdit().render();
        contentDetails = editPage.getDetails();
        contentDetails.setContent("123456789");
        editPage.save(contentDetails).render();
   }
}
