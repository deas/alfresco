package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.enums.Dashlet;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

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
    SelectImageFolderBoxPage selectImageFolderBoxPage = null;
    private static final String expectedHelpBallonMsg = "This dashlet shows a thumbnail of each image in the document library. Clicking a thumbnail opens the image in the current window.";
    private static final String imagePreviewTitle = "Image Preview";

    @BeforeTest
    public void prepare() throws Exception
    {
        siteName = "imagepreviewdashlettest" + System.currentTimeMillis();
    }

    @BeforeClass
    public void setUp() throws Exception
    {
        loginAs(username, password);
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlet.IMAGE_PREVIEW, 1).render();
        imagePreviewDashlet = siteDashBoard.getDashlet(IMAGE_PREVIEW).render();
        assertNotNull(imagePreviewDashlet);
    }

    @Test(dependsOnMethods="instantiateDashlet")
    public void verifyHelpIcon()
    {
        assertTrue(imagePreviewDashlet.isHelpIconDisplayed());
    }

    @Test(dependsOnMethods="verifyHelpIcon")
    public void verifyConfigureIcon ()
    {
        assertTrue(imagePreviewDashlet.isConfigureIconDisplayed());
    }

    @Test(dependsOnMethods="verifyHelpIcon")
    public void selectHelpIcon ()
    {
        imagePreviewDashlet.clickOnHelpIcon();
        assertTrue(imagePreviewDashlet.isBalloonDisplayed());
        String actualHelpBallonMsg = imagePreviewDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBallonMsg, expectedHelpBallonMsg);
    }

    @Test(dependsOnMethods="selectHelpIcon")
    public void closeHelpIcon ()
    {
        imagePreviewDashlet.closeHelpBallon();
        assertFalse(imagePreviewDashlet.isBalloonDisplayed());
    }

    @Test(dependsOnMethods="verifyConfigureIcon")
    public void clickConfigureButton ()
    {
        selectImageFolderBoxPage = imagePreviewDashlet.clickOnConfigure().render();
        assertNotNull(selectImageFolderBoxPage);
    }

    @Test(dependsOnMethods="instantiateDashlet")
    public void getTitle ()
    {
        String actualTitle = imagePreviewDashlet.getTitle();
        assertEquals(actualTitle, imagePreviewTitle);
    }
}
