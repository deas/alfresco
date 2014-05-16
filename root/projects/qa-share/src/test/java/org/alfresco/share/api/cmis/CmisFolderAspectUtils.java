/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.api.cmis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.site.document.Categories;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.share.enums.CMISBinding;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserAdmin;
import org.alfresco.share.util.ShareUserSearchPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.Listeners;

/**
 * Class to include: Tests for cmis: Add Folder Aspects
 * 
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
public class CmisFolderAspectUtils extends AbstractCmisSecondaryTypeIDTests
{
    private final String PROPERTY_DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
    private static Log logger = LogFactory.getLog(CmisFolderAspectUtils.class);

    public void dataPrep(WebDrone drone, String userName, String testName, String siteName) throws Exception
    {
        String folderName = getFolderName(testName);

        Map<String, String> properties = new HashMap<String, String>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, folderName);

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        if (!ShareUserSitePage.isFileVisible(drone, folderName))
        {
            // Create Folder
            ShareUserSitePage.createFolder(drone, folderName, folderName);
        }
        ShareUser.logout(drone);
    }

    public void addClasifiableAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        String categoryNodeRef = "";
        Map<String, Object> propertyMap = new HashMap<>();
        if (!alfrescoVersion.isCloud())
        {
            categoryNodeRef = ShareUserAdmin.getCategoryNodeRef(drone, "cm:category", "cm:Languages");
            propertyMap.put("cm:categories", Arrays.asList(categoryNodeRef));
        }
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.CLASSIFIABLE);

        addAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToAdd);
        if (!alfrescoVersion.isCloud())
        {
            addProperties(cmisBinding, userName, DOMAIN, folderNodeRef, propertyMap);
        }

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.CLASSIFIABLE);

        if (!alfrescoVersion.isCloud())
        {
            DetailsPage detailsPage = ShareUser.getSharePage(drone).render();
            Map<String, Object> properties = detailsPage.getProperties();
            List<Categories> categoriesList = (List<Categories>) properties.get("Categories");
            Assert.assertEquals(categoriesList.size(), 1);
            Assert.assertEquals(categoriesList.get(0).getValue(), "Languages");
        }

        ShareUser.logout(drone);
    }

    public void removeClasifiableAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.CLASSIFIABLE);

        removeAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.CLASSIFIABLE);

        if (!alfrescoVersion.isCloud())
        {
            Map<String, Object> properties = getProperties(drone, siteName, folderName);
            Assert.assertNull(properties.get("Categories"));
        }

        ShareUser.logout(drone);
    }

    public void addComplianceableAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.COMPLIANCEABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 25);
        Date removeAfterDate = calendar.getTime();

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:removeAfter", removeAfterDate);

        addAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, folderNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.COMPLIANCEABLE);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);

        Assert.assertEquals(getPropertyValue(folderProperties, "removeAfter"), new SimpleDateFormat(PROPERTY_DATE_FORMAT).format(removeAfterDate));

        ShareUser.logout(drone);
    }

    public void removeComplianceableAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.COMPLIANCEABLE);

        removeAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.COMPLIANCEABLE);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);
        Assert.assertNull(getPropertyValue(folderProperties, "removeAfter"), "Verifying the property doesn't exists");

        ShareUser.logout(drone);
    }

    public void addDublinCoreAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.DUBLIN_CORE);

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:dcsource", "test-source");
        propertyMap.put("cm:contributor", "test-contributor");
        propertyMap.put("cm:publisher", "test-publisher");
        propertyMap.put("cm:subject", "test-subject");
        propertyMap.put("cm:type", "test-type");
        propertyMap.put("cm:identifier", "test-identifier");
        propertyMap.put("cm:rights", "test-rights");
        propertyMap.put("cm:coverage", "test-coverage");

        addAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, folderNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.DUBLIN_CORE);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);

        Assert.assertEquals(getPropertyValue(folderProperties, "dcsource"), "test-source");
        Assert.assertEquals(getPropertyValue(folderProperties, "contributor"), "test-contributor");
        Assert.assertEquals(getPropertyValue(folderProperties, "publisher"), "test-publisher");
        Assert.assertEquals(getPropertyValue(folderProperties, "subject"), "test-subject");
        Assert.assertEquals(getPropertyValue(folderProperties, "type"), "test-type");
        Assert.assertEquals(getPropertyValue(folderProperties, "identifier"), "test-identifier");
        Assert.assertEquals(getPropertyValue(folderProperties, "rights"), "test-rights");
        Assert.assertEquals(getPropertyValue(folderProperties, "coverage"), "test-coverage");

        ShareUser.logout(drone);
    }

    public void removeDublinCoreAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.DUBLIN_CORE);

        removeAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.DUBLIN_CORE);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);
        Assert.assertNull(getPropertyValue(folderProperties, "source"));
        Assert.assertNull(getPropertyValue(folderProperties, "contributor"));
        Assert.assertNull(getPropertyValue(folderProperties, "publisher"));
        Assert.assertNull(getPropertyValue(folderProperties, "subject"));
        Assert.assertNull(getPropertyValue(folderProperties, "type"));
        Assert.assertNull(getPropertyValue(folderProperties, "identifier"));
        Assert.assertNull(getPropertyValue(folderProperties, "rights"));
        Assert.assertNull(getPropertyValue(folderProperties, "coverage"));

        ShareUser.logout(drone);
    }

    public void addEffectivityAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.EFFECTIVITY);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 2);
        Date toDate = calendar.getTime();
        Date fromDate = new Date();

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:from", fromDate);
        propertyMap.put("cm:to", toDate);

        addAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, folderNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.EFFECTIVITY);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);

        Assert.assertEquals(getPropertyValue(folderProperties, "from"), new SimpleDateFormat(PROPERTY_DATE_FORMAT).format(fromDate));
        Assert.assertEquals(getPropertyValue(folderProperties, "to"), new SimpleDateFormat(PROPERTY_DATE_FORMAT).format(toDate));

        ShareUser.logout(drone);
    }

    public void removeEffectivityAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.EFFECTIVITY);

        removeAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.EFFECTIVITY);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);

        Assert.assertNull(getPropertyValue(folderProperties, "from"));
        Assert.assertNull(getPropertyValue(folderProperties, "to"));

        ShareUser.logout(drone);
    }

    public void addSummarizableAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.SUMMARIZABLE);

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:summary", "test-summary");

        addAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, folderNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.SUMMARIZABLE);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);

        Assert.assertEquals(getPropertyValue(folderProperties, "summary"), "test-summary");

        ShareUser.logout(drone);
    }

    public void removeSummarizableAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.SUMMARIZABLE);

        removeAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.SUMMARIZABLE);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);
        Assert.assertNull(getPropertyValue(folderProperties, "summary"));

        ShareUser.logout(drone);
    }

    public void dataPrepTemplatableAspect(WebDrone drone, String userName, String testName, String siteName) throws Exception
    {
        String folderName = getFolderName(testName);
        String templateFolderName = "Template-" + getFolderName(testName);

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        if (!ShareUserSitePage.isFileVisible(drone, folderName))
        {
            ShareUserSitePage.createFolder(drone, folderName, folderName);
        }
        if (!ShareUserSitePage.isFileVisible(drone, templateFolderName))
        {
            ShareUserSitePage.createFolder(drone, templateFolderName, templateFolderName);
        }
        ShareUser.logout(drone);
    }

    public void addTemplatableAspect(WebDrone drone, String userName, String folderName, String siteName, String templateName, CMISBinding cmisBinding)
            throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String templateNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", templateName);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.TEMPLATABLE);

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:template", templateNodeRef);

        addAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, folderNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.TEMPLATABLE);

        ShareUser.logout(drone);
    }

    public void removeTemplatableAspect(WebDrone drone, String userName, String folderName, String siteName, String templateName, CMISBinding cmisBinding)
            throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.TEMPLATABLE);

        removeAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.TEMPLATABLE);

        ShareUser.logout(drone);
    }

    public void addEmailedAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.EMAILED);

        Date sentDate = new Date();

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:addressee", "test Addressee");
        propertyMap.put("cm:addressees", Arrays.asList("addresses@test.com"));
        propertyMap.put("cm:subjectline", "test-Subject");
        propertyMap.put("cm:originator", "test-originator");
        propertyMap.put("cm:sentdate", sentDate);

        addAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, folderNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.EMAILED);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);

        Assert.assertEquals(getPropertyValue(folderProperties, "addressee"), "test Addressee");
        Assert.assertEquals((getPropertyValue(folderProperties, "addressees")), "addresses@test.com");
        Assert.assertEquals(getPropertyValue(folderProperties, "subjectline"), "test-Subject");
        Assert.assertEquals(getPropertyValue(folderProperties, "originator"), "test-originator");
        Assert.assertEquals(getPropertyValue(folderProperties, "sentdate"), new SimpleDateFormat(PROPERTY_DATE_FORMAT).format(sentDate));

        ShareUser.logout(drone);
    }

    public void removeEmailedAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.EMAILED);

        removeAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.EMAILED);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);

        Assert.assertNull(getPropertyValue(folderProperties, "addressee"));
        Assert.assertNull((getPropertyValue(folderProperties, "addressees")));
        Assert.assertNull(getPropertyValue(folderProperties, "subjectline"));
        Assert.assertNull(getPropertyValue(folderProperties, "originator"));
        Assert.assertNull(getPropertyValue(folderProperties, "sentdate"));

        ShareUser.logout(drone);
    }

    public void addTaggableAspect(WebDrone drone, String userName, String folderName, String siteName, String tagName, CMISBinding cmisBinding)
            throws Exception
    {
        String tagNodeRef = ShareUserAdmin.getTagNodeRef(drone, tagName);

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.TAGGABLE);

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:taggable", Arrays.asList(tagNodeRef));

        addAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, folderNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.TAGGABLE);

        DetailsPage detailsPage = ShareUser.getSharePage(drone).render();

        Assert.assertTrue(detailsPage.getTagList().contains(tagName), "Verifying " + tagName + " is displayed");

        ShareUser.logout(drone);
    }

    public void removeTaggableAspect(WebDrone drone, String userName, String folderName, String siteName, String tagName, CMISBinding cmisBinding)
            throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.TAGGABLE);

        removeAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.TAGGABLE);

        DetailsPage detailsPage = getDetailsPage(drone, siteName, folderName);

        Assert.assertTrue(detailsPage.getTagList().isEmpty(), "Verifying " + tagName + " is NOT displayed");

        ShareUser.logout(drone);
    }

    public void addGeographicAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        double longitude = 444.4;
        double latitude = 444.4;

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.GEOGRAPHIC);

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:longitude", longitude);
        propertyMap.put("cm:latitude", latitude);

        addAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, folderNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.GEOGRAPHIC);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);

        Assert.assertEquals(getPropertyValue(folderProperties, "longitude"), String.valueOf(longitude));
        Assert.assertEquals((getPropertyValue(folderProperties, "latitude")), String.valueOf(latitude));

        DetailsPage detailsPage = getDetailsPage(drone, siteName, folderName);
        Assert.assertTrue(detailsPage.isViewOnGoogleMapsLinkVisible(), "Verifying \"View on Google Maps\" link is displayed");

        ShareUser.logout(drone);
    }

    public void removeGeographicAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.GEOGRAPHIC);

        removeAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.GEOGRAPHIC);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);

        Assert.assertNull(getPropertyValue(folderProperties, "longitude"));
        Assert.assertNull(getPropertyValue(folderProperties, "latitude"));

        Assert.assertFalse(getDetailsPage(drone, siteName, folderName).isViewOnGoogleMapsLinkVisible(),
                "Verifying \"View on Google Maps\" link is NOT displayed");

        ShareUser.logout(drone);
    }

    public void addEXIFAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.EXIF);

        Date dateTimeOriginal = new Date();
        String manufacturer = "test-manufacturer";
        boolean flash = true;
        double focalLength = 12.5;
        double xResolution = 400.4;
        double yResolution = 32.5;
        String model = "test-model";
        String software = "test-software";
        int orientation = 12;
        String resolutionUnit = "12";
        int pixelYDimension = 400;
        int pixelXDimension = 500;
        String isoSpeedRatings = "400";
        double fNumber = 12.5;

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("exif:dateTimeOriginal", dateTimeOriginal);
        propertyMap.put("exif:manufacturer", manufacturer);
        propertyMap.put("exif:flash", flash);
        propertyMap.put("exif:focalLength", focalLength);
        propertyMap.put("exif:xResolution", xResolution);
        propertyMap.put("exif:yResolution", yResolution);
        propertyMap.put("exif:model", model);
        propertyMap.put("exif:software", software);
        propertyMap.put("exif:orientation", orientation);
        propertyMap.put("exif:resolutionUnit", resolutionUnit);
        propertyMap.put("exif:pixelYDimension", pixelYDimension);
        propertyMap.put("exif:pixelXDimension", pixelXDimension);
        propertyMap.put("exif:isoSpeedRatings", isoSpeedRatings);
        propertyMap.put("exif:fNumber", fNumber);

        addAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, folderNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.EXIF);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);

        Assert.assertEquals(getPropertyValue(folderProperties, "dateTimeOriginal"), new SimpleDateFormat(PROPERTY_DATE_FORMAT).format(dateTimeOriginal));
        Assert.assertEquals(getPropertyValue(folderProperties, "manufacturer"), manufacturer);
        Assert.assertEquals(getPropertyValue(folderProperties, "flash"), String.valueOf(flash));
        Assert.assertEquals(getPropertyValue(folderProperties, "focalLength"), String.valueOf(focalLength));
        Assert.assertEquals(getPropertyValue(folderProperties, "xResolution"), String.valueOf(xResolution));
        Assert.assertEquals(getPropertyValue(folderProperties, "yResolution"), String.valueOf(yResolution));
        Assert.assertEquals(getPropertyValue(folderProperties, "model"), model);
        Assert.assertEquals(getPropertyValue(folderProperties, "software"), software);
        Assert.assertEquals(getPropertyValue(folderProperties, "orientation"), String.valueOf(orientation));
        Assert.assertEquals(getPropertyValue(folderProperties, "resolutionUnit"), resolutionUnit);
        Assert.assertEquals(getPropertyValue(folderProperties, "pixelYDimension"), String.valueOf(pixelYDimension));
        Assert.assertEquals(getPropertyValue(folderProperties, "pixelXDimension"), String.valueOf(pixelXDimension));
        Assert.assertEquals(getPropertyValue(folderProperties, "isoSpeedRatings"), isoSpeedRatings);
        Assert.assertEquals(getPropertyValue(folderProperties, "fNumber"), String.valueOf(fNumber));

        ShareUser.logout(drone);
    }

    public void removeEXIFAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.EXIF);

        removeAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.EXIF);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);

        Assert.assertNull(getPropertyValue(folderProperties, "dateTimeOriginal"));
        Assert.assertNull(getPropertyValue(folderProperties, "manufacturer"));
        Assert.assertNull(getPropertyValue(folderProperties, "flash"));
        Assert.assertNull(getPropertyValue(folderProperties, "focalLength"));
        Assert.assertNull(getPropertyValue(folderProperties, "xResolution"));
        Assert.assertNull(getPropertyValue(folderProperties, "yResolution"));
        Assert.assertNull(getPropertyValue(folderProperties, "model"));
        Assert.assertNull(getPropertyValue(folderProperties, "software"));
        Assert.assertNull(getPropertyValue(folderProperties, "orientation"));
        Assert.assertNull(getPropertyValue(folderProperties, "resolutionUnit"));
        Assert.assertNull(getPropertyValue(folderProperties, "pixelYDimension"));
        Assert.assertNull(getPropertyValue(folderProperties, "pixelXDimension"));
        Assert.assertNull(getPropertyValue(folderProperties, "isoSpeedRatings"));
        Assert.assertNull(getPropertyValue(folderProperties, "fNumber"));

        ShareUser.logout(drone);
    }

    public void addAudioAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.AUDIO);

        String engineer = "test-engineer";
        String compressor = "test-compressor";
        String channelType = "test-channelType";
        String sampleType = "test-sampleType";
        int sampleRate = 10;
        int trackNumber = 10;
        Date releaseDate = new Date();
        String genre = "test-genre";
        String composer = "test-composer";
        String artist = "test-artist";
        String album = "test-album";

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("audio:engineer", engineer);
        propertyMap.put("audio:compressor", compressor);
        propertyMap.put("audio:channelType", channelType);
        propertyMap.put("audio:sampleType", sampleType);
        propertyMap.put("audio:sampleRate", sampleRate);
        propertyMap.put("audio:trackNumber", trackNumber);
        propertyMap.put("audio:releaseDate", releaseDate);
        propertyMap.put("audio:genre", genre);
        propertyMap.put("audio:composer", composer);
        propertyMap.put("audio:artist", artist);
        propertyMap.put("audio:album", album);

        addAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, folderNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.AUDIO);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);

        Assert.assertEquals(getPropertyValue(folderProperties, "engineer"), engineer);
        Assert.assertEquals(getPropertyValue(folderProperties, "compressor"), compressor);
        Assert.assertEquals(getPropertyValue(folderProperties, "channelType"), channelType);
        Assert.assertEquals(getPropertyValue(folderProperties, "sampleType"), sampleType);
        Assert.assertEquals(getPropertyValue(folderProperties, "sampleRate"), String.valueOf(sampleRate));
        Assert.assertEquals(getPropertyValue(folderProperties, "trackNumber"), String.valueOf(trackNumber));
        Assert.assertEquals(getPropertyValue(folderProperties, "releaseDate"), new SimpleDateFormat(PROPERTY_DATE_FORMAT).format(releaseDate));
        Assert.assertEquals(getPropertyValue(folderProperties, "genre"), genre);
        Assert.assertEquals(getPropertyValue(folderProperties, "composer"), String.valueOf(composer));
        Assert.assertEquals(getPropertyValue(folderProperties, "artist"), artist);
        Assert.assertEquals(getPropertyValue(folderProperties, "album"), String.valueOf(album));

        ShareUser.logout(drone);
    }

    public void removeAudioAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.AUDIO);

        removeAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.AUDIO);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);

        Assert.assertNull(getPropertyValue(folderProperties, "engineer"));
        Assert.assertNull(getPropertyValue(folderProperties, "compressor"));
        Assert.assertNull(getPropertyValue(folderProperties, "channelType"));
        Assert.assertNull(getPropertyValue(folderProperties, "sampleType"));
        Assert.assertNull(getPropertyValue(folderProperties, "sampleRate"));
        Assert.assertNull(getPropertyValue(folderProperties, "trackNumber"));
        Assert.assertNull(getPropertyValue(folderProperties, "releaseDate"));
        Assert.assertNull(getPropertyValue(folderProperties, "genre"));
        Assert.assertNull(getPropertyValue(folderProperties, "composer"));
        Assert.assertNull(getPropertyValue(folderProperties, "artist"));
        Assert.assertNull(getPropertyValue(folderProperties, "album"));

        ShareUser.logout(drone);
    }

    public void addIndexControlAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.INDEX_CONTROL);

        boolean isIndexed = false;
        boolean isContentIndexed = false;

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:isIndexed", isIndexed);
        propertyMap.put("cm:isContentIndexed", isContentIndexed);

        addAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, folderNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.INDEX_CONTROL);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);
        Assert.assertEquals(getPropertyValue(folderProperties, "isIndexed"), String.valueOf(isIndexed));
        Assert.assertEquals(getPropertyValue(folderProperties, "isContentIndexed"), String.valueOf(isContentIndexed));

        ShareUserSearchPage.basicSearch(drone, folderName, false);
        Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, BASIC_SEARCH, folderName, folderName, false),
                "Folder NOT found in search results after adding the INDEX_CONTROL aspect");

        ShareUser.logout(drone);
    }

    public void removeIndexControlAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.INDEX_CONTROL);

        removeAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.INDEX_CONTROL);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);
        Assert.assertNull(getPropertyValue(folderProperties, "isIndexed"));
        Assert.assertNull(getPropertyValue(folderProperties, "isContentIndexed"));

        ShareUserSearchPage.basicSearch(drone, folderName, false);
        Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, BASIC_SEARCH, folderName, folderName, true),
                "Folder found in search results after removing the INDEX_CONTROL aspect");

        ShareUser.logout(drone);
    }

    public void addRestrictableAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.RESTRICTABLE);

        int offlineExpiresAfter = 3600000;

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("dp:offlineExpiresAfter", offlineExpiresAfter);

        addAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, folderNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.RESTRICTABLE);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);
        Assert.assertEquals(getPropertyValue(folderProperties, "offlineExpiresAfter"), String.valueOf(offlineExpiresAfter));

        ShareUser.logout(drone);
    }

    public void removeRestrictableAspect(WebDrone drone, String userName, String folderName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String folderNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", folderName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.RESTRICTABLE);

        removeAspect(cmisBinding, userName, DOMAIN, folderNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, folderNodeRef, folderName, siteName, DocumentAspect.RESTRICTABLE);

        List<Property<?>> folderProperties = getFolderProperties(cmisBinding, userName, DOMAIN, folderNodeRef);
        Assert.assertNull(getPropertyValue(folderProperties, "offlineExpiresAfter"));

        ShareUser.logout(drone);
    }
}
