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
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.po.share.site.document.DocumentAspect.*;
import static org.alfresco.share.util.ShareUser.openSiteDashboard;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests related to Adding and Removing the aspects.
 * @author Shan Nagarajan
 */
@Listeners(FailedTestListener.class)
public class ManageAspectTest extends AbstractAspectTests
{
    private static Log logger = LogFactory.getLog(ManageAspectTest.class);
    
    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }
    
    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2086() throws Exception
    {
        addAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void AONE_2086() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(AUDIO);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getAudioAspectKey());
        //proptery.setSizeAfterAspectAdded(21);
        
        addAspectTest(proptery);
    }
    
    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2087() throws Exception
    {
        addAspectDataPrep(getTestName());
    }

    @Test(groups="EnterpriseOnly")
    public void AONE_2087() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(INDEX_CONTROL);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getIndexControlAspectKey());
        //proptery.setSizeAfterAspectAdded(12);
        
        addAspectTest(proptery);
    }
    
    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2071() throws Exception
    {
        addAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void AONE_2071() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(CLASSIFIABLE);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getClassifiableAspectKey());
        //proptery.setSizeAfterAspectAdded(11);
        
        addAspectTest(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2084() throws Exception
    {
        addAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void AONE_2084() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(EXIF);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getExifAspectKey());
        //proptery.setSizeAfterAspectAdded(25);
        
        addAspectTest(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2083() throws Exception
    {
        addAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void AONE_2083() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(GEOGRAPHIC);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getGeographicAspectKey());
        //proptery.setSizeAfterAspectAdded(12);
        
        addAspectTest(proptery);

        // check that View on Google Maps link is displayed in the Document Actions section
        DocumentDetailsPage documentDetailsPage = drone.getCurrentPage().render();
        List<String> actionsList = documentDetailsPage.getDocumentActionList();
        assertTrue(actionsList.contains("View on Google Maps"));

    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2088() throws Exception
    {
        addAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void AONE_2088() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(RESTRICTABLE);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getRestrictableAspectKey());
        //proptery.setSizeAfterAspectAdded(11);
        
        addAspectTest(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2089() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void AONE_2089() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(CLASSIFIABLE);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getClassifiableAspectKey());
        //proptery.setSizeAfterAspectAdded(11);
        
        removeAspectTest(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2101() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void AONE_2101() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(RESTRICTABLE);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getRestrictableAspectKey());
        //proptery.setSizeAfterAspectAdded(11);
        
        removeAspectTest(proptery);
    }
    
    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2100() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void AONE_2100() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(INDEX_CONTROL);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getIndexControlAspectKey());
        //proptery.setSizeAfterAspectAdded(11);
        
        removeAspectTest(proptery);
    }
    
    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2099() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void AONE_2099() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(AUDIO);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getAudioAspectKey());
        //proptery.setSizeAfterAspectAdded(21);
        
        removeAspectTest(proptery);
    }
    
    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2097() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void AONE_2097() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(EXIF);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getExifAspectKey());
        //proptery.setSizeAfterAspectAdded(25);
        
        removeAspectTest(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2096() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void AONE_2096() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(GEOGRAPHIC);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getGeographicAspectKey());
        //proptery.setSizeAfterAspectAdded(12);
        
        removeAspectTest(proptery);

        // check that View on Google Maps link isn't displayed in the Document Actions section
        DocumentDetailsPage documentDetailsPage = drone.getCurrentPage().render();
        List<String> actionsList = documentDetailsPage.getDocumentActionList();
        assertFalse(actionsList.contains("View on Google Maps"));
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2076() throws Exception
    {
        addAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void AONE_2076() throws Exception
    {
        try
        {            
            String testName = getTestName();
            String testUser = getUserNameFreeDomain(testName);
            String siteName = getSiteName(testName);
            String fileName = getFileName(testName) + "-" + System.currentTimeMillis();

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Open Site DashBoard
            openSiteDashboard(drone, siteName);
            
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);
            
            // Create File
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(fileName);
            CreatePlainTextContentPage contentPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
            DocumentDetailsPage documentDetailsPage = contentPage.create(contentDetails).render();                       
            
            // Add Versinable aspect
            SelectAspectsPage aspectsPage = documentDetailsPage.selectManageAspects();
            List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
            aspects.add(VERSIONABLE);
            aspectsPage = aspectsPage.add(aspects).render();
            documentDetailsPage = aspectsPage.clickApplyChanges().render();

            documentDetailsPage = documentDetailsPage.render();

            // Edit metadata of the content
            EditDocumentPropertiesPage propertiesPage = documentDetailsPage.selectEditProperties();
            propertiesPage.render();
            
            propertiesPage.setAuthor(testUser);
            documentDetailsPage = propertiesPage.selectSave().render();
            documentDetailsPage.render();
            
            //Check that the version of the content is incremented
            assertEquals(documentDetailsPage.getDocumentVersion(), "1.1");
            
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_14988() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }

    @Test(groups="EnterpriseOnly")
    public void AONE_14988() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(DUBLIN_CORE);
        proptery.setExpectedProprtyKey(getDublinCoreAspectKey());

        removeAspectTest(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_14989() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }

    @Test(groups="EnterpriseOnly")
    public void AONE_14989() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(SUMMARIZABLE);
        proptery.setExpectedProprtyKey(getSummarisableAspectKey());

        removeAspectTest(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2092() throws Exception
    {
        addAspectDataPrep(getTestName());
    }

    @Test(groups="EnterpriseOnly")
    public void AONE_2092() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + "-" + System.currentTimeMillis();
        try{
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Open Site DashBoard
            openSiteDashboard(drone, siteName);
            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);

            //Create File
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(fileName);

            CreatePlainTextContentPage contentPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
            DocumentDetailsPage documentDetailsPage = contentPage.create(contentDetails).render();

            // Add aspect
            SelectAspectsPage aspectsPage = documentDetailsPage.selectManageAspects();

            List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
            aspects.add(VERSIONABLE);
            aspectsPage = aspectsPage.add(aspects).render();
            documentDetailsPage = aspectsPage.clickApplyChanges().render();

            documentDetailsPage = documentDetailsPage.render();

            EditDocumentPropertiesPage propertiesPage = documentDetailsPage.selectEditProperties();
            propertiesPage.render();

            propertiesPage.setAuthor(testUser);
            documentDetailsPage = propertiesPage.selectSave().render();
            documentDetailsPage.render();

            assertEquals(documentDetailsPage.getDocumentVersion(), "1.1");

            // Remove aspect
            aspectsPage = documentDetailsPage.selectManageAspects();
            aspectsPage = aspectsPage.remove(aspects).render();
            documentDetailsPage = aspectsPage.clickApplyChanges().render();

            // Edit some properties
            propertiesPage = documentDetailsPage.selectEditProperties();
            propertiesPage.render();

            propertiesPage.setAuthor(testUser + "1");
            documentDetailsPage = propertiesPage.selectSave().render();
            documentDetailsPage.render();

            assertEquals(documentDetailsPage.getDocumentVersion(), "1.0");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }


    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_14991() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }

    @Test(groups="EnterpriseOnly")
    public void AONE_14991() throws Exception
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(EMAILED);
        proptery.setExpectedProprtyKey(getEmailedAspectKey());

        removeAspectTest(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_AONE_2095() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }

    @Test(groups="EnterpriseOnly")
    public void AONE_2095()throws Exception
    {

        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(INLINE_EDITABLE);
        proptery.setExpectedProprtyKey(getEmailedAspectKey());

        addRemoveAspectDoc(proptery, true, false);

    }

}