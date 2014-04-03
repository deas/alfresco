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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import org.testng.Assert;


/**
 * Contains the common methods to add & remove aspects, get aspects keys.
 * 
 * @author Shan Nagarajan
 * @since 1.1
 */
public class AbstractAspectTests extends AbstractTests
{
    private Log logger = LogFactory.getLog(this.getClass());

    public void removeAspectDataPrep(String name) throws Exception
    {
        aspectDataPrep(name, true);
    }

    public void aspectDataPrep(String name, boolean addDoc) throws Exception
    {

        try
        {
            String testUser = getUserNameFreeDomain(name);
            String siteName = getSiteName(name);

            // User
            String[] testUserInfo = new String[] { testUser };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            if (addDoc)
            {
                // Upload File
                String fileName = getFileName(name) + ".txt";
                String[] fileInfo = { fileName, DOCLIB };
                ShareUser.uploadFileInFolder(drone, fileInfo);
            }

        }
        catch (Exception e)
        {
            saveScreenShot(drone, name);
            logger.error("Error in dataPrep: " + name, e);
        }

    }

    public void addAspectDataPrep(String name) throws Exception
    {
        aspectDataPrep(name, false);
    }

    public void addAspectTest(AspectTestProptery proptery)
    {
        addRemoveAspectTest(proptery, false);
    }

    public void removeAspectTest(AspectTestProptery proptery)
    {
        addRemoveAspectTest(proptery, true);
    }

    public void addRemoveAspectTest(AspectTestProptery proptery, boolean removeAspect)
    {

        try
        {
            String testName = proptery.getTestName();
            this.testName = testName;
            String testUser = getUserNameFreeDomain(testName);
            String siteName = getSiteName(testName);
            String fileName = getFileName(testName) + ".txt";

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Open Site DashBoard
            openSiteDashboard(drone, siteName);

            DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);
            if (!removeAspect)
            {
                // Upload File
                String[] fileInfo = { fileName, DOCLIB };
                ShareUser.uploadFileInFolder(drone, fileInfo);
            }

            DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName);

            SelectAspectsPage aspectsPage = documentDetailsPage.selectManageAspects();

            Map<String, Object> properties = documentDetailsPage.getProperties();

            // Check and Set property size before adding aspect
            proptery.setSizeBeforeAspectAdded(properties.size());

            List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
            aspects.add(proptery.getAspect());
            aspectsPage = aspectsPage.add(aspects).render();
            documentDetailsPage = aspectsPage.clickApplyChanges().render();

            // TODO: Shan: Do we check notification as in Testlink: Successfully updated aspects'?
            documentDetailsPage = documentDetailsPage.render();

            // Set property size before adding aspect
            int propsToBeAdded = proptery.getExpectedProprtyKey().size();

            // Get property size after adding aspect
            properties = documentDetailsPage.getProperties();

            // Check appropriate properties have been added: Count
            assertEquals(properties.size(), proptery.getSizeBeforeAspectAdded() + propsToBeAdded);

            // Check appropriate properties have been added: List of properties
            Set<String> actualPropertKey = properties.keySet();
            assertTrue(actualPropertKey.containsAll(proptery.getExpectedProprtyKey()));

            if (removeAspect)
            {
                aspectsPage = documentDetailsPage.selectManageAspects();
                aspectsPage = aspectsPage.remove(aspects).render();
                documentDetailsPage = aspectsPage.clickApplyChanges().render();

                properties = documentDetailsPage.getProperties();
                assertEquals(properties.size(), proptery.getSizeBeforeAspectAdded());
                actualPropertKey = properties.keySet();

                assertFalse(actualPropertKey.containsAll(proptery.getExpectedProprtyKey()));
            }
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

    public void removeAspectDataPrepFolder(String name) throws Exception
    {
        aspectDataPrepFolder(name, true);
    }

    public void aspectDataPrepFolder(String name, boolean addFold) throws Exception
    {

        try
        {
            String testUser = getUserNameFreeDomain(name);
            String siteName = getSiteName(name);

            // User
            String[] testUserInfo = new String[] { testUser };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            if (addFold)
            {
                // Upload File
//                String fileName = getFileName(name) + ".txt";
//                String[] fileInfo = { fileName, DOCLIB };
//                ShareUser.uploadFileInFolder(drone, fileInfo);
                DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                String folderName = getFolderName(name) + "folder";
                docLibPage = ShareUserSitePage.createFolder(drone, folderName, folderName);

            }

        }
        catch (Exception e)
        {
            saveScreenShot(drone, name);
            logger.error("Error in dataPrep: " + name, e);
        }

    }

    public void addAspectFolder(AspectTestProptery proptery)
    {
        addRemoveAspectFolder(proptery, false, false);
    }

    public void removeAspectFolder(AspectTestProptery proptery)
    {
        addRemoveAspectFolder(proptery, true, false);
    }

    public void addAspectFolderKey(AspectTestProptery proptery)
    {
        addRemoveAspectFolder(proptery, false, true);
    }

    public void removeAspectFolderKey(AspectTestProptery proptery)
    {
        addRemoveAspectFolder(proptery, true, true);
    }

    public void addRemoveAspectFolder(AspectTestProptery proptery, boolean removeAspect, boolean propertyKey)
    {

        try
        {
            String testName = proptery.getTestName();
            this.testName = testName;
            String testUser = getUserNameFreeDomain(testName);
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName) + "folder";

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Open Site DashBoard
            DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            if (!removeAspect)
            {
                // Create Folder
                docLibPage = ShareUserSitePage.createFolder(drone, folderName, folderName);
            }


            // Open Folder Details Page
            FolderDetailsPage folderDetailsPage = docLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

            SelectAspectsPage aspectsPage = folderDetailsPage.selectManageAspects();

            Map<String, Object> properties = folderDetailsPage.getProperties();

            // Check and Set property size before adding aspect
            proptery.setSizeBeforeAspectAdded(properties.size());

            List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
            aspects.add(proptery.getAspect());

            aspectsPage = aspectsPage.add(aspects).render();
            aspectsPage = aspectsPage.add(aspects).render();

            folderDetailsPage = aspectsPage.clickApplyChanges().render();

            // TODO: Shan: Do we check notification as in Testlink: Successfully updated aspects'?
            folderDetailsPage = folderDetailsPage.render();
            drone.refresh();

            if (propertyKey){
                 // Set property size before adding aspect
                int propsToBeAdded = proptery.getExpectedProprtyKey().size();

                // Get property size after adding aspect
                properties = folderDetailsPage.getProperties();

                // Check appropriate properties have been added: Count
                assertEquals(properties.size(), proptery.getSizeBeforeAspectAdded() + propsToBeAdded);

                // Check appropriate properties have been added: List of properties
                Set<String> actualPropertKey = properties.keySet();
           assertTrue(actualPropertKey.containsAll(proptery.getExpectedProprtyKey()));


            }
            else{
                // Get property size after adding aspect
                properties = folderDetailsPage.getProperties();
                assertEquals(properties.size(), proptery.getSizeBeforeAspectAdded());

            }
            if (removeAspect)
            {
                aspectsPage = folderDetailsPage.selectManageAspects();
                aspectsPage = aspectsPage.remove(aspects).render();
                folderDetailsPage = aspectsPage.clickApplyChanges().render();
                drone.refresh();

                properties = folderDetailsPage.getProperties();
                assertEquals(properties.size(), proptery.getSizeBeforeAspectAdded());
                Set<String> actualPropertKey = properties.keySet();

                assertFalse(actualPropertKey.containsAll(proptery.getExpectedProprtyKey()));
            }
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

    public void addAspectLinkCheck(AspectTestProptery proptery, boolean removeAspect)
    {

        try
        {
            String testName = proptery.getTestName();
            this.testName = testName;
            String testUser = getUserNameFreeDomain(testName);
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName) + "folder";

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Open Site DashBoard
            DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            if(!removeAspect){
                // Create Folder
                docLibPage = ShareUserSitePage.createFolder(drone, folderName, folderName);
            }

            // Open Folder Details Page
            FolderDetailsPage folderDetailsPage = docLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

            SelectAspectsPage aspectsPage = folderDetailsPage.selectManageAspects();

            Map<String, Object> properties = folderDetailsPage.getProperties();

            // Check and Set property size before adding aspect
            proptery.setSizeBeforeAspectAdded(properties.size());

            List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
            aspects.add(proptery.getAspect());
//            aspects.add(proptery.getAspect());
            aspectsPage = aspectsPage.add(aspects).render();

            aspectsPage = aspectsPage.add(aspects).render();
            folderDetailsPage = aspectsPage.clickApplyChanges().render();

            // TODO: Shan: Do we check notification as in Testlink: Successfully updated aspects'?

            // Check and Set property size before adding aspect
            proptery.setSizeBeforeAspectAdded(properties.size());
            folderDetailsPage = folderDetailsPage.render();
//            drone.refresh();

            // Get property size after adding aspect
            properties = folderDetailsPage.getProperties();
            assertEquals(properties.size(), proptery.getSizeBeforeAspectAdded());

            if(!removeAspect){
            assertTrue(folderDetailsPage.isViewOnGoogleMapsLinkVisible());
            }
            else
            {
                drone.refresh();
                aspectsPage = folderDetailsPage.selectManageAspects();
                aspectsPage = aspectsPage.remove(aspects).render();
//                aspectsPage = aspectsPage.remove(aspects).render();

                folderDetailsPage = aspectsPage.clickApplyChanges().render();

                properties = folderDetailsPage.getProperties();
                assertEquals(properties.size(), proptery.getSizeBeforeAspectAdded());
//                Set<String> actualPropertKey = properties.keySet();
//
//                assertFalse(actualPropertKey.containsAll(proptery.getExpectedProprtyKey()));
                assertFalse(folderDetailsPage.isViewOnGoogleMapsLinkVisible());

            }
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

    public Set<String> getAudioAspectKey()
    {
        Set<String> audioAspectKey = new HashSet<String>();
        audioAspectKey.add("Album");
        audioAspectKey.add("Artist");
        audioAspectKey.add("Compressor");
        audioAspectKey.add("Engineer");
        audioAspectKey.add("Genre");
        audioAspectKey.add("TrackNumber");
        audioAspectKey.add("ReleaseDate");
        audioAspectKey.add("SampleRate");
        audioAspectKey.add("SampleType");
        audioAspectKey.add("ChannelType");
        audioAspectKey.add("Composer");

        return audioAspectKey;
    }

    public Set<String> getIndexControlAspectKey()
    {
        Set<String> indexControlAspectKey = new HashSet<String>();
        indexControlAspectKey.add("IsIndexed");
        indexControlAspectKey.add("IsContentIndexed");
        return indexControlAspectKey;
    }

    public Set<String> getClassifiableAspectKey()
    {
        Set<String> expectedProprtyKey = new HashSet<String>();
        expectedProprtyKey.add("Categories");
        return expectedProprtyKey;
    }

    public Set<String> getExifAspectKey()
    {
        Set<String> exifAspectKey = new HashSet<String>();
        exifAspectKey.add("DateandTime");
        exifAspectKey.add("ImageWidth");
        exifAspectKey.add("ImageHeight");
        exifAspectKey.add("ExposureTime");
        exifAspectKey.add("FNumber");
        exifAspectKey.add("FlashActivated");
        exifAspectKey.add("FocalLength");
        exifAspectKey.add("ISOSpeed");
        exifAspectKey.add("CameraManufacturer");
        exifAspectKey.add("CameraModel");
        exifAspectKey.add("CameraSoftware");
        exifAspectKey.add("Orientation");
        exifAspectKey.add("HorizontalResolution");
        exifAspectKey.add("VerticalResolution");
        exifAspectKey.add("ResolutionUnit");
        return exifAspectKey;
    }

    public Set<String> getGeographicAspectKey()
    {
        Set<String> geographicAspectKey = new HashSet<String>();
        geographicAspectKey.add("Latitude");
        geographicAspectKey.add("Longitude");
        return geographicAspectKey;
    }

    public Set<String> getRestrictableAspectKey()
    {
        Set<String> restrictableAspectKey = new HashSet<String>();
        restrictableAspectKey.add("OfflineExpiresAfter(hours)");
        return restrictableAspectKey;
    }

    public Set<String> getAliaAbleAspectKey()
    {
        Set<String> aliasAbleAspectKey = new HashSet<String>();
        aliasAbleAspectKey.add("Alias");
        return aliasAbleAspectKey;
    }

    public Set<String> getDublinCoreAspectKey()
    {
        Set<String> dublinCoreAspectKey = new HashSet<String>();
        dublinCoreAspectKey.add("Publisher");
        dublinCoreAspectKey.add("Contributor");
        dublinCoreAspectKey.add("Type");
        dublinCoreAspectKey.add("Identifier");
        dublinCoreAspectKey.add("Source");
        dublinCoreAspectKey.add("Coverage");
        dublinCoreAspectKey.add("Rights");
        dublinCoreAspectKey.add("Subject");
        return dublinCoreAspectKey;
    }

    public Set<String> getSummarisableAspectKey()
    {
        Set<String> summarisableAspectKey = new HashSet<String>();
        summarisableAspectKey.add("Summary");
        return summarisableAspectKey;
    }

    public Set<String> getEmailedAspectKey()
    {
        Set<String> emailedAspectKey = new HashSet<String>();

        emailedAspectKey.add("Addresses");
        emailedAspectKey.add("Subject");
        emailedAspectKey.add("Originator");
        emailedAspectKey.add("Sent Date");
        emailedAspectKey.add("Addressee");

        return emailedAspectKey;
    }
}
