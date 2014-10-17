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
package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;

import static org.alfresco.po.share.util.SiteUtil.prepareJpg;
import static org.testng.Assert.*;

/**
 * Tests for Image Preview dashlet web elements
 *
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "alfresco-one" })

public class ImagePreviewDashletTest extends AbstractSiteDashletTest
{
    private static final String IMAGE_PREVIEW = "image-preview";
    private ImagePreviewDashlet imagePreviewDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private SelectImageFolderBoxPage selectImageFolderBoxPage = null;
    private String jpgName;

    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows a thumbnail of each image in the document library. Clicking a thumbnail opens the image in the current window.";
    private static final String IMG_PREVIEW_TITLE = "Image Preview";

    @BeforeClass
    public void setUp() throws Exception
    {
        siteName = "imagePreviewDashletTest" + System.currentTimeMillis();
        loginAs(username, password);
        SiteUtil.createSite(drone, siteName, "description", "Public");
        openSiteDocumentLibraryFromSearch(drone, siteName);
        File jpg = prepareJpg(ImagePreviewDashlet.class.getName());
        jpgName = jpg.getName();
        uploadContent(drone, jpg.getAbsolutePath());
    }

    @Test
    public void instantiateDashlet()
    {
        navigateToSiteDashboard();
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.IMAGE_PREVIEW, 1).render();
        imagePreviewDashlet = siteDashBoard.getDashlet(IMAGE_PREVIEW).render();
        assertNotNull(imagePreviewDashlet);
    }


    @Test(dependsOnMethods = "instantiateDashlet")
    public void getTitle()
    {
        String actualTitle = imagePreviewDashlet.getTitle();
        assertEquals(actualTitle, IMG_PREVIEW_TITLE);
    }

    @Test(dependsOnMethods = "getTitle")
    public void verifyHelpIcon()
    {
        assertTrue(imagePreviewDashlet.isHelpIconDisplayed());
    }

    @Test(dependsOnMethods = "verifyHelpIcon")
    public void verifyConfigureIcon()
    {
        assertTrue(imagePreviewDashlet.isConfigureIconDisplayed());
    }

    @Test(dependsOnMethods = "verifyConfigureIcon")
    public void selectHelpIcon()
    {
        imagePreviewDashlet.clickOnHelpIcon();
        assertTrue(imagePreviewDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = imagePreviewDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
    }

    @Test(dependsOnMethods = "selectHelpIcon")
    public void closeHelpIcon()
    {
        imagePreviewDashlet.closeHelpBallon();
        assertFalse(imagePreviewDashlet.isBalloonDisplayed());
    }

    @Test(dependsOnMethods = "closeHelpIcon")
    public void clickConfigureButton()
    {
        selectImageFolderBoxPage = imagePreviewDashlet.clickOnConfigure().render();
        assertNotNull(selectImageFolderBoxPage);
    }

    @Test(dependsOnMethods = "clickConfigureButton", expectedExceptions = PageRenderTimeException.class)
    public void clickCancelConfigure()
    {
        selectImageFolderBoxPage.clickCancel();
        selectImageFolderBoxPage.render();
    }

    @Test(dependsOnMethods = "clickCancelConfigure")
    public void verifyImageCount() throws Exception
    {
        Thread.sleep(20000);
        drone.refresh();
        siteDashBoard = drone.getCurrentPage().render();
        imagePreviewDashlet = siteDashBoard.getDashlet(IMAGE_PREVIEW).render();
        assertEquals(imagePreviewDashlet.getImagesCount(), 1);
    }

    @Test(dependsOnMethods = "verifyImageCount")
    public void verifyIsDisplayed()
    {
        assertTrue(imagePreviewDashlet.isImageDisplayed(jpgName));
    }
}
