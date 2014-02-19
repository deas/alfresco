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

import static org.alfresco.share.util.ShareUser.openSiteDashboard;
import static org.testng.Assert.assertEquals;
import static org.alfresco.po.share.site.document.DocumentAspect.AUDIO;
import static org.alfresco.po.share.site.document.DocumentAspect.CLASSIFIABLE;
import static org.alfresco.po.share.site.document.DocumentAspect.EXIF;
import static org.alfresco.po.share.site.document.DocumentAspect.GEOGRAPHIC;
import static org.alfresco.po.share.site.document.DocumentAspect.INDEX_CONTROL;
import static org.alfresco.po.share.site.document.DocumentAspect.RESTRICTABLE;
import static org.alfresco.po.share.site.document.DocumentAspect.VERSIONABLE;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.SelectAspectsPage;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.alfresco.share.util.ShareUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

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
    public void dataPrep_Dashlets_Enterprise40x_9652() throws Exception
    {
        addAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_9652()
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
    public void dataPrep_Dashlets_Enterprise40x_9653() throws Exception
    {
        addAspectDataPrep(getTestName());
    }

    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_9653()
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
    public void dataPrep_Dashlets_Enterprise40x_14321() throws Exception
    {
        addAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14321()
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
    public void dataPrep_Dashlets_Enterprise40x_14327() throws Exception
    {
        addAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14327()
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
    public void dataPrep_Dashlets_Enterprise40x_14326() throws Exception
    {
        addAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14326()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(GEOGRAPHIC);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getGeographicAspectKey());
        //proptery.setSizeAfterAspectAdded(12);
        
        addAspectTest(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_14328() throws Exception
    {
        addAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14328()
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
    public void dataPrep_Dashlets_Enterprise40x_14338() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14338()
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
    public void dataPrep_Dashlets_Enterprise40x_14350() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14350()
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
    public void dataPrep_Dashlets_Enterprise40x_14349() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14349()
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
    public void dataPrep_Dashlets_Enterprise40x_14348() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14348()
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
    public void dataPrep_Dashlets_Enterprise40x_14346() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14346()
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
    public void dataPrep_Dashlets_Enterprise40x_14345() throws Exception
    {
        removeAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14345()
    {
        AspectTestProptery proptery = new AspectTestProptery();
        proptery.setTestName(getTestName());
        proptery.setAspect(GEOGRAPHIC);
        //proptery.setSizeBeforeAspectAdded(10);
        proptery.setExpectedProprtyKey(getGeographicAspectKey());
        //proptery.setSizeAfterAspectAdded(12);
        
        removeAspectTest(proptery);
    }

    @Test(groups={"DataPrepDocumentLibrary"})
    public void dataPrep_Dashlets_Enterprise40x_14322() throws Exception
    {
        addAspectDataPrep(getTestName());
    }
    
    @Test(groups="EnterpriseOnly")
    public void Enterprise40x_14322()
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
            
            //Create File
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(fileName);
            
            // TODO: Shan: Use util to create content: i.e. ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
            CreatePlainTextContentPage contentPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
            DocumentDetailsPage documentDetailsPage = contentPage.create(contentDetails).render();                       
            
            SelectAspectsPage aspectsPage = documentDetailsPage.selectManageAspects();
            
            List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
            aspects.add(VERSIONABLE);
            aspectsPage = aspectsPage.add(aspects).render();
            documentDetailsPage = aspectsPage.clickApplyChanges().render();
            
            //TODO: Shan: Do we check notification as in Testlink: Successfully updated aspects'?
            documentDetailsPage = documentDetailsPage.render();
            
            EditDocumentPropertiesPage propertiesPage = documentDetailsPage.selectEditProperties();
            propertiesPage.render();
            
            propertiesPage.setAuthor(testUser);
            documentDetailsPage = propertiesPage.selectSave().render();
            documentDetailsPage.render();
            
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

}