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
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.share.enums.CMISBinding;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserAdmin;
import org.alfresco.share.util.ShareUserSearchPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;

/**
 * Class to include utils to test CMIS add aspects to document.
 * 
 * @author Ranjith Manyam
 */
public class CmisDocumentAspectUtils extends AbstractCmisSecondaryTypeIDTests
{
    private final String PROPERTY_DATE_FORMAT = "EEE d MMM yyyy HH:mm:ss";
    private static Log logger = LogFactory.getLog(CmisDocumentAspectUtils.class);

    public void dataPrep(WebDrone drone, String userName, String testName, String siteName) throws Exception
    {
        String fileName = getFileName(testName);
        String[] fileInfo = { fileName };

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        if (!ShareUserSitePage.isFileVisible(drone, fileName))
        {
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }
        ShareUser.logout(drone);
    }

    public void addClasifiableAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        String categoryNodeRef = "";
        Map<String, Object> propertyMap = new HashMap<>();
        if (!alfrescoVersion.isCloud())
        {
            categoryNodeRef = ShareUserAdmin.getCategoryNodeRef(drone, "cm:category", "cm:Languages");
            propertyMap.put("cm:categories", Arrays.asList(categoryNodeRef));
        }
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.CLASSIFIABLE);

        addAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToAdd);
        if (!alfrescoVersion.isCloud())
        {
            addProperties(cmisBinding, userName, DOMAIN, documentNodeRef, propertyMap);
        }

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.CLASSIFIABLE);

        if (!alfrescoVersion.isCloud())
        {
            DocumentDetailsPage documentDetailsPage = ShareUser.getSharePage(drone).render();
            Map<String, Object> properties = documentDetailsPage.getProperties();
            List<Categories> categoriesList = (List<Categories>) properties.get("Categories");
            Assert.assertEquals(categoriesList.size(), 1);
            Assert.assertEquals(categoriesList.get(0).getValue(), "Languages");
        }

        ShareUser.logout(drone);
    }

    public void removeClasifiableAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.CLASSIFIABLE);

        removeAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.CLASSIFIABLE);

        if (!alfrescoVersion.isCloud())
        {
            DocumentDetailsPage documentDetailsPage = ShareUser.getSharePage(drone).render();
            Map<String, Object> properties = documentDetailsPage.getProperties();
            Assert.assertNull(properties.get("Categories"));
        }

        ShareUser.logout(drone);
    }

    public void addComplianceableAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.COMPLIANCEABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 25);
        Date removeAfterDate = calendar.getTime();

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:removeAfter", removeAfterDate);

        addAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, documentNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.COMPLIANCEABLE);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertEquals(properties.get("RemoveAfter"), new SimpleDateFormat(PROPERTY_DATE_FORMAT).format(removeAfterDate));

        ShareUser.logout(drone);
    }

    public void removeComplianceableAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.COMPLIANCEABLE);

        removeAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.COMPLIANCEABLE);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertNull(properties.get("RemoveAfter"));

        ShareUser.logout(drone);
    }

    public void addDublinCoreAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.DUBLIN_CORE);

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:contributor", "test-contributor");
        propertyMap.put("cm:publisher", "test-publisher");
        propertyMap.put("cm:subject", "test-subject");
        propertyMap.put("cm:type", "test-type");
        propertyMap.put("cm:identifier", "test-identifier");
        propertyMap.put("cm:rights", "test-rights");
        propertyMap.put("cm:coverage", "test-coverage");

        addAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, documentNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.DUBLIN_CORE);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertEquals(properties.get("Contributor"), "test-contributor");
        Assert.assertEquals(properties.get("Publisher"), "test-publisher");
        Assert.assertEquals(properties.get("Subject"), "test-subject");
        Assert.assertEquals(properties.get("Type"), "test-type");
        Assert.assertEquals(properties.get("Identifier"), "test-identifier");
        Assert.assertEquals(properties.get("Rights"), "test-rights");
        Assert.assertEquals(properties.get("Coverage"), "test-coverage");

        ShareUser.logout(drone);
    }

    public void removeDublinCoreAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.DUBLIN_CORE);

        removeAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.DUBLIN_CORE);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertNull(properties.get("Contributor"));
        Assert.assertNull(properties.get("Publisher"));
        Assert.assertNull(properties.get("Subject"));
        Assert.assertNull(properties.get("Type"));
        Assert.assertNull(properties.get("Identifier"));
        Assert.assertNull(properties.get("Rights"));
        Assert.assertNull(properties.get("Coverage"));

        ShareUser.logout(drone);
    }

    public void addEffectivityAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.EFFECTIVITY);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 2);
        Date toDate = calendar.getTime();
        Date fromDate = new Date();

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:from", fromDate);
        propertyMap.put("cm:to", toDate);

        addAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, documentNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.EFFECTIVITY);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertEquals(properties.get("EffectiveFrom"), new SimpleDateFormat(PROPERTY_DATE_FORMAT).format(fromDate));
        Assert.assertEquals(properties.get("EffectiveTo"), new SimpleDateFormat(PROPERTY_DATE_FORMAT).format(toDate));

        ShareUser.logout(drone);
    }

    public void removeEffectivityAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.EFFECTIVITY);

        removeAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.EFFECTIVITY);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertNull(properties.get("EffectiveFrom"));
        Assert.assertNull(properties.get("EffectiveTo"));

        ShareUser.logout(drone);
    }

    public void addSummarizableAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.SUMMARIZABLE);

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:summary", "test-summary");

        addAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, documentNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.SUMMARIZABLE);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertEquals(properties.get("Summary"), "test-summary");

        ShareUser.logout(drone);
    }

    public void removeSummarizableAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.SUMMARIZABLE);

        removeAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.SUMMARIZABLE);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertNull(properties.get("Summary"));

        ShareUser.logout(drone);
    }

    public void dataPrepTemplatableAspect(WebDrone drone, String userName, String testName, String siteName) throws Exception
    {
        String fileName = getFileName(testName);
        String templateFileName = "Template-" + getFileName(testName);
        String[] fileInfo = { fileName };
        String[] templateFileInfo = { templateFileName };

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        if (!ShareUserSitePage.isFileVisible(drone, fileName))
        {
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }
        if (!ShareUserSitePage.isFileVisible(drone, templateFileName))
        {
            ShareUser.uploadFileInFolder(drone, templateFileInfo);
        }
        ShareUser.logout(drone);
    }

    public void addTemplatableAspect(WebDrone drone, String userName, String fileName, String siteName, String templateName, CMISBinding cmisBinding)
            throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String templateNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", templateName);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.TEMPLATABLE);

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:template", templateNodeRef);

        addAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, documentNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.TEMPLATABLE);

        ShareUser.logout(drone);
    }

    public void removeTemplatableAspect(WebDrone drone, String userName, String fileName, String siteName, String templateName, CMISBinding cmisBinding)
            throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.TEMPLATABLE);

        removeAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.TEMPLATABLE);

        ShareUser.logout(drone);
    }

    public void addEmailedAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.EMAILED);

        Date sentDate = new Date();

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:addressee", "test Addressee");
        propertyMap.put("cm:addressees", Arrays.asList("addresses@test.com"));
        propertyMap.put("cm:subjectline", "test-Subject");
        propertyMap.put("cm:originator", "test-originator");
        propertyMap.put("cm:sentdate", sentDate);

        addAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, documentNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.EMAILED);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertEquals(properties.get("Addressee"), "test Addressee");
        Assert.assertEquals(properties.get("Addressees"), "addresses@test.com");
        Assert.assertEquals(properties.get("Subject"), "test-Subject");
        Assert.assertEquals(properties.get("Originator"), "test-originator");
        Assert.assertEquals(properties.get("SentDate"), new SimpleDateFormat(PROPERTY_DATE_FORMAT).format(sentDate));

        ShareUser.logout(drone);
    }

    public void removeEmailedAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.EMAILED);

        removeAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.EMAILED);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertNull(properties.get("Addressee"));
        Assert.assertNull(properties.get("Addressees"));
        Assert.assertNull(properties.get("Subject"));
        Assert.assertNull(properties.get("Originator"));
        Assert.assertNull(properties.get("SentDate"));

        ShareUser.logout(drone);
    }

    public void addTaggableAspect(WebDrone drone, String userName, String fileName, String siteName, String tagName, CMISBinding cmisBinding) throws Exception
    {
        String tagNodeRef = ShareUserAdmin.getTagNodeRef(drone, tagName);

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.TAGGABLE);

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:taggable", Arrays.asList(tagNodeRef));

        addAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, documentNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.TAGGABLE);

        DetailsPage documentDetailsPage = getDetailsPage(drone, siteName, fileName);

        Assert.assertTrue(documentDetailsPage.getTagList().contains(tagName), "Verifying " + tagName + " is displayed");

        ShareUser.logout(drone);
    }

    public void removeTaggableAspect(WebDrone drone, String userName, String fileName, String siteName, String tagName, CMISBinding cmisBinding)
            throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.TAGGABLE);

        removeAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.TAGGABLE);

        DetailsPage documentDetailsPage = getDetailsPage(drone, siteName, fileName);

        Assert.assertTrue(documentDetailsPage.getTagList().isEmpty(), "Verifying " + tagName + " is NOT displayed");

        ShareUser.logout(drone);
    }

    public void addGeographicAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        double longitude = 444.4;
        double latitude = 444.4;

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.GEOGRAPHIC);

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:longitude", longitude);
        propertyMap.put("cm:latitude", latitude);

        addAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, documentNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.GEOGRAPHIC);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);

        Assert.assertEquals(properties.get("Longitude"), String.valueOf(longitude));
        Assert.assertEquals(properties.get("Latitude"), String.valueOf(latitude));

        Assert.assertTrue(getDetailsPage(drone, siteName, fileName).isViewOnGoogleMapsLinkVisible(), "Verifying \"View on Google Maps\" link is displayed");

        ShareUser.logout(drone);
    }

    public void removeGeographicAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.GEOGRAPHIC);

        removeAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.GEOGRAPHIC);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertNull(properties.get("Longitude"));
        Assert.assertNull(properties.get("Latitude"));

        Assert.assertFalse(getDetailsPage(drone, siteName, fileName).isViewOnGoogleMapsLinkVisible(), "Verifying \"View on Google Maps\" link is NOT displayed");

        ShareUser.logout(drone);
    }

    public void addEXIFAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

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

        addAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, documentNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.EXIF);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertEquals(properties.get("DateandTime"), new SimpleDateFormat(PROPERTY_DATE_FORMAT).format(dateTimeOriginal));
        Assert.assertEquals(properties.get("CameraManufacturer"), manufacturer);
        Assert.assertEquals(properties.get("FlashActivated"), "Yes");
        Assert.assertEquals(properties.get("FocalLength"), String.valueOf(focalLength));
        Assert.assertEquals(properties.get("HorizontalResolution"), String.valueOf(xResolution));
        Assert.assertEquals(properties.get("VerticalResolution"), String.valueOf(yResolution));
        Assert.assertEquals(properties.get("CameraModel"), model);
        Assert.assertEquals(properties.get("CameraSoftware"), software);
        Assert.assertEquals(properties.get("Orientation"), String.valueOf(orientation));
        Assert.assertEquals(properties.get("ResolutionUnit"), resolutionUnit);
        Assert.assertEquals(properties.get("ImageHeight"), String.valueOf(pixelYDimension));
        Assert.assertEquals(properties.get("ImageWidth"), String.valueOf(pixelXDimension));
        Assert.assertEquals(properties.get("ISOSpeed"), isoSpeedRatings);
        Assert.assertEquals(properties.get("FNumber"), String.valueOf(fNumber));

        ShareUser.logout(drone);
    }

    public void removeEXIFAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.EXIF);

        removeAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.EXIF);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertNull(properties.get("DateandTime"));
        Assert.assertNull(properties.get("CameraManufacturer"));
        Assert.assertNull(properties.get("FlashActivated"));
        Assert.assertNull(properties.get("FocalLength"));
        Assert.assertNull(properties.get("HorizontalResolution"));
        Assert.assertNull(properties.get("VerticalResolution"));
        Assert.assertNull(properties.get("CameraModel"));
        Assert.assertNull(properties.get("CameraSoftware"));
        Assert.assertNull(properties.get("Orientation"));
        Assert.assertNull(properties.get("ResolutionUnit"));
        Assert.assertNull(properties.get("ImageHeight"));
        Assert.assertNull(properties.get("ImageWidth"));
        Assert.assertNull(properties.get("ISOSpeed"));
        Assert.assertNull(properties.get("FNumber"));

        ShareUser.logout(drone);
    }

    public void addAudioAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

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

        addAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, documentNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.AUDIO);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertEquals(properties.get("Engineer"), engineer);
        Assert.assertEquals(properties.get("Compressor"), compressor);
        Assert.assertEquals(properties.get("ChannelType"), channelType);
        Assert.assertEquals(properties.get("SampleType"), sampleType);
        Assert.assertEquals(properties.get("SampleRate"), String.valueOf(sampleRate));
        Assert.assertEquals(properties.get("TrackNumber"), String.valueOf(trackNumber));
        Assert.assertEquals(properties.get("ReleaseDate"), new SimpleDateFormat("EEE d MMM yyyy").format(releaseDate));
        Assert.assertEquals(properties.get("Genre"), genre);
        Assert.assertEquals(properties.get("Composer"), String.valueOf(composer));
        Assert.assertEquals(properties.get("Artist"), artist);
        Assert.assertEquals(properties.get("Album"), String.valueOf(album));

        ShareUser.logout(drone);
    }

    public void removeAudioAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.AUDIO);

        removeAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.AUDIO);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertNull(properties.get("Engineer"));
        Assert.assertNull(properties.get("Compressor"));
        Assert.assertNull(properties.get("ChannelType"));
        Assert.assertNull(properties.get("SampleType"));
        Assert.assertNull(properties.get("SampleRate"));
        Assert.assertNull(properties.get("TrackNumber"));
        Assert.assertNull(properties.get("ReleaseDate"));
        Assert.assertNull(properties.get("Genre"));
        Assert.assertNull(properties.get("Composer"));
        Assert.assertNull(properties.get("Artist"));
        Assert.assertNull(properties.get("Album"));

        ShareUser.logout(drone);
    }

    public void addIndexControlAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.INDEX_CONTROL);

        boolean isIndexed = false;
        boolean isContentIndexed = false;

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("cm:isIndexed", isIndexed);
        propertyMap.put("cm:isContentIndexed", isContentIndexed);

        addAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, documentNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.INDEX_CONTROL);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertEquals(properties.get("IsIndexed"), "No");
        Assert.assertEquals(properties.get("IsContentIndexed"), "No");

        ShareUserSearchPage.basicSearch(drone, fileName, false);
        // Code amended to cater for Eventual consistency
        Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, BASIC_SEARCH, fileName, fileName, false),
                "Document NOT found in search results after adding the INDEX_CONTROL aspect");

        ShareUser.logout(drone);
    }

    public void removeIndexControlAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.INDEX_CONTROL);

        removeAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.INDEX_CONTROL);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertNull(properties.get("IsIndexed"));
        Assert.assertNull(properties.get("IsContentIndexed"));

        ShareUserSearchPage.basicSearch(drone, fileName, false);
        // Code amended to cater for Eventual consistency
        Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, BASIC_SEARCH, fileName, fileName, true),
                "Document found in search results after adding the INDEX_CONTROL aspect");

        ShareUser.logout(drone);
    }

    public void addRestrictableAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.RESTRICTABLE);

        int offlineExpiresAfter = 3600000;

        Map<String, Object> propertyMap = new HashMap<>();
        propertyMap.put("dp:offlineExpiresAfter", offlineExpiresAfter);

        addAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToAdd);
        addProperties(cmisBinding, userName, DOMAIN, documentNodeRef, propertyMap);

        verifyAspectIsAdded(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.RESTRICTABLE);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertEquals(properties.get("OfflineExpiresAfter(hours)"), String.valueOf(offlineExpiresAfter / (1000 * 60 * 60)));

        ShareUser.logout(drone);
    }

    public void removeRestrictableAspect(WebDrone drone, String userName, String fileName, String siteName, CMISBinding cmisBinding) throws Exception
    {
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        String documentNodeRef = getNodeRef(cmisBinding, userName, DOMAIN, siteName, "", fileName);

        List<DocumentAspect> aspectsToRemove = new ArrayList<>();
        aspectsToRemove.add(DocumentAspect.RESTRICTABLE);

        removeAspect(cmisBinding, userName, DOMAIN, documentNodeRef, aspectsToRemove);

        verifyAspectIsRemoved(drone, cmisBinding, userName, DOMAIN, documentNodeRef, fileName, siteName, DocumentAspect.RESTRICTABLE);

        Map<String, Object> properties = getProperties(drone, siteName, fileName);
        Assert.assertNull(properties.get("OfflineExpiresAfter(hours)"));

        ShareUser.logout(drone);
    }
}
