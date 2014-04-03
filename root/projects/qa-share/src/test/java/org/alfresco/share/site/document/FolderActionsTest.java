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
package org.alfresco.share.site.document;

import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.*;

import static org.alfresco.po.share.site.document.DocumentAspect.*;
import static org.alfresco.po.share.site.document.FolderDetailsPage.*;
import static org.alfresco.po.share.site.document.DocumentDetailsPage.*;
import static org.alfresco.share.util.ShareUser.getRandomStringWithNumders;
import static org.alfresco.share.util.ShareUser.openSiteDashboard;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Listeners(FailedTestListener.class)
public class FolderActionsTest extends AbstractAspectTests
{
    private static Log logger = LogFactory.getLog(FolderActionsTest.class);
    
    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_14329() throws Exception
    {
        addAspectDataPrep(getTestName());

    }
    
    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14329()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(CLASSIFIABLE);
        proptery.setExpectedProprtyKey(getClassifiableAspectKey());

        addAspectFolderKey(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_14351() throws Exception
    {
        removeAspectDataPrepFolder(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14351()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(CLASSIFIABLE);
        proptery.setExpectedProprtyKey(getClassifiableAspectKey());

        removeAspectFolderKey(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_14330() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14330()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(VERSIONABLE);

        addAspectFolder(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_14333() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14333()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(INLINE_EDITABLE);

        addAspectFolder(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_14334() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14334()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(EXIF);

        addAspectFolder(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_14335() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14335()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(AUDIO);

        addAspectFolder(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_14336() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14336()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(INDEX_CONTROL);

        addAspectFolder(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_14337() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14337()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(RESTRICTABLE);
        proptery.setExpectedProprtyKey(getRestrictableAspectKey());

        addAspectFolderKey(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_10773() throws Exception
    {
        addAspectDataPrep(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_10773()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(GEOGRAPHIC);

        addAspectLinkCheck(proptery, false);


    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_14353() throws Exception
    {
        removeAspectDataPrepFolder(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14353()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(GEOGRAPHIC);

        addAspectLinkCheck(proptery, true);


    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_13354() throws Exception
    {
        removeAspectDataPrepFolder(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_13354()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(ALIASABLE_EMAIL);
        proptery.setExpectedProprtyKey(getAliaAbleAspectKey());

        removeAspectFolderKey(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_13355() throws Exception
    {
        removeAspectDataPrepFolder(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_13355()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(RESTRICTABLE);
        proptery.setExpectedProprtyKey(getRestrictableAspectKey());

        removeAspectFolderKey(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_14356() throws Exception
    {
        removeAspectDataPrepFolder(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14356()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(DUBLIN_CORE);
        proptery.setExpectedProprtyKey(getDublinCoreAspectKey());

        removeAspectFolderKey(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_13357() throws Exception
    {
        removeAspectDataPrepFolder(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_13357()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(SUMMARIZABLE);
        proptery.setExpectedProprtyKey(getSummarisableAspectKey());

        removeAspectFolderKey(proptery);
    }


    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_14033() throws Exception
    {
        try
        {
            String testName = getTestName();
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName);

            // User
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            // Create Folder
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

            ShareUser.logout(drone);

        }
        catch (Exception e)
        {
            saveScreenShot(customDrone, testName);
            logger.error("Error in dataPrep: " + testName, e);
        }
    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_14033()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        DocumentLibraryPage documentLibraryPage = null;
        AspectTestProptery proptery = new AspectTestProptery();


        try
        {
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Open folder details page
            FolderDetailsPage detailsPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

            Map<String, Object> properties = detailsPage.getProperties();

            // Check and Set property size before adding aspect
            proptery.setSizeBeforeAspectAdded(properties.size());

            // Click 'Change Type' action
            ChangeTypePage changeTypePage = detailsPage.selectChangeType().render();
            assertTrue(changeTypePage.isChangeTypeDisplayed());

            List<String> types = changeTypePage.getTypes();
            assertTrue(types.contains("Select type..."));

            // Select any type if present and click Cancel
            int typeCount = types.size();

            if (typeCount>1){
                for (int i=0; i<=1; i++){
                    String randomType = types.get(1);
                    changeTypePage.selectChangeType(randomType);
                    if(i==0){
                        changeTypePage.selectCancel();
                        drone.refresh();
                        properties = detailsPage.getProperties();
                        assertEquals(properties.size(), proptery.getSizeBeforeAspectAdded());
                        changeTypePage = detailsPage.selectChangeType().render();
                    }
                    // Click Change Type again, select any type and click OK
                    if (i==1){
                        changeTypePage.selectSave();
                        drone.refresh();
                        properties = detailsPage.getProperties();
                        Assert.assertNotSame(properties.size(), proptery.getSizeBeforeAspectAdded());
                    }

                }

            }
            else{
                changeTypePage.selectCancel();
                drone.refresh();
                properties = detailsPage.getProperties();
                assertEquals(properties.size(), proptery.getSizeBeforeAspectAdded());
            }
            }
            catch (Throwable e)
            {
                reportError(drone, testName, e);

            }
            testCleanup(drone, testName);

    }


    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_14358() throws Exception
    {
        removeAspectDataPrepFolder(getTestName());

    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14358()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(EMAILED);
        proptery.setExpectedProprtyKey(getEmailedAspectKey());

        removeAspectFolderKey(proptery);
    }
}