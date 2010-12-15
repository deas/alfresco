/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.cmis.test.ws;

import java.math.BigInteger;

import org.alfresco.repo.cmis.ws.CancelCheckOut;
import org.alfresco.repo.cmis.ws.CheckIn;
import org.alfresco.repo.cmis.ws.CheckInResponse;
import org.alfresco.repo.cmis.ws.CheckOut;
import org.alfresco.repo.cmis.ws.CheckOutResponse;
import org.alfresco.repo.cmis.ws.CmisContentStreamType;
import org.alfresco.repo.cmis.ws.CmisFaultType;
import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisPropertiesType;
import org.alfresco.repo.cmis.ws.DeleteObject;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.EnumVersioningState;
import org.alfresco.repo.cmis.ws.GetAllVersions;
import org.alfresco.repo.cmis.ws.GetPropertiesOfLatestVersion;
import org.alfresco.repo.cmis.ws.GetPropertiesOfLatestVersionResponse;
import org.alfresco.repo.cmis.ws.VersioningServicePort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Versioning Service
 */
public class CmisVersioningServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisVersioningServiceClient.class);

    private static final String TEST_CHECK_IN_COMMENT_MESSAGE = "Test Check In Comment";
    private static final String INVALID_REPOSITORY_ID = "Wrong Repository Id";

    private String documentId;

    private String documentIdHolder;

    public CmisVersioningServiceClient()
    {
    }

    public CmisVersioningServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    /**
     * Initializes Versioning Service client
     */
    @Override
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        documentId = createAndAssertDocument();
        documentIdHolder = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null)).getObjectId();
    }

    /**
     * Invokes all methods in Versioning Service
     */
    @Override
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        VersioningServicePort versioningService = getServicesFactory().getVersioningService(getProxyUrl() + getService().getPath());

        versioningService.cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), documentIdHolder, null));

        documentIdHolder = versioningService.checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null)).getObjectId();

        versioningService.checkIn(new CheckIn(getAndAssertRepositoryId(), documentIdHolder, true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0),
                MIMETYPE_TEXT_PLAIN, generateTestFileName(), new byte[0], null), TEST_CHECK_IN_COMMENT_MESSAGE, null, null, null, null));

        GetAllVersions getAllVersions = new GetAllVersions();
        getAllVersions.setRepositoryId(getAndAssertRepositoryId());
        getAllVersions.setObjectId(documentId);
        getAllVersions.setFilter("*");
        getAllVersions.setIncludeAllowableActions(false);
        versioningService.getAllVersions(getAllVersions);

        GetPropertiesOfLatestVersion getPropertiesOfLatestVersion = new GetPropertiesOfLatestVersion();
        getPropertiesOfLatestVersion.setRepositoryId(getAndAssertRepositoryId());
        getPropertiesOfLatestVersion.setObjectId(documentId);
        getPropertiesOfLatestVersion.setFilter("*");
        versioningService.getPropertiesOfLatestVersion(getPropertiesOfLatestVersion);

    }

    /**
     * Remove initial data
     */
    @Override
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
    }

    /**
     * Main method to start client
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-context.xml");
        AbstractServiceClient client = (CmisVersioningServiceClient) applicationContext.getBean("cmisVersioningServiceClient");
        try
        {
            client.initialize();
            client.invoke();
            client.release();
        }
        catch (Exception e)
        {
            LOGGER.error("Some error occured during client running. Exception message: " + e.getMessage());
        }
    }

    @Override
    protected void onSetUp() throws Exception
    {
        documentId = createAndAssertDocument();
        super.onSetUp();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        try
        {
            if (documentIdHolder != null)
            {
                getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), documentIdHolder, null));
            }
        }
        catch (Exception e)
        {
        }
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        super.onTearDown();
    }

    public void testCheckOut() throws Exception
    {
        CheckOutResponse response = null;
        try
        {
            LOGGER.info("[VersioningService->checkOut]");
            response = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
            assertNotNull("CheckOut response is NULL", response);
            documentIdHolder = response.getObjectId();
            assertNotNull("Checked out document id is NULL", documentIdHolder);
            if (!isVersioningAllowed())
            {
                fail("No Exception was thrown during checkOut while the Document’s Object-Type definition’s versionable attribute is FALSE");
            }
        }
        catch (Exception e)
        {
            if (!isVersioningAllowed())
            {
                assertTrue("Invalid exception was thrown during checkOut while the Document’s Object-Type definition’s versionable attribute is FALSE", e instanceof CmisFaultType
                        && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
            }
            else
            {
                fail(e.toString());
            }
        }
    }

    public void testCheckOutCheckIn()
    {
        try
        {
            LOGGER.info("[VersioningService->checkOut]");
            CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
            assertNotNull("Checkout response is NULL", checkOutResponse);
            documentIdHolder = checkOutResponse.getObjectId();
            assertTrue("Content was not copied", checkOutResponse.isContentCopied());
            assertFalse("Checked out document id is equal to document id", documentId.equals(documentIdHolder));
            if (!isVersioningAllowed())
            {
                fail("No Exception was thrown during checkOut while the Document’s Object-Type definition’s versionable attribute is FALSE");
            }

            LOGGER.info("[VersioningService->checkIn]");
            CheckInResponse checkInResponse = getServicesFactory().getVersioningService().checkIn(
                    new CheckIn(getAndAssertRepositoryId(), documentIdHolder, null, null, null, null, null, null, null, null));
            assertNotNull("checkin response is NULL", checkInResponse);
            documentId = checkInResponse.getObjectId();
            if (!isVersioningAllowed())
            {
                fail("No Exception was thrown during checkIn while the Document’s Object-Type definition’s versionable attribute is FALSE");
            }
        }
        catch (Exception e)
        {
            if (!isVersioningAllowed())
            {
                assertTrue("Invalid exception was thrown during checkOut/checkIn while the Document’s Object-Type definition’s versionable attribute is FALSE",
                        e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
            }
            else
            {
                fail(e.toString());
            }
        }
    }

    public void testCheckOutCancelCheckOut()
    {
        try
        {
            LOGGER.info("[VersioningService->checkOut]");
            CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
            assertNotNull("Checkout response is NULL", checkOutResponse);
            documentIdHolder = checkOutResponse.getObjectId();
            if (!isVersioningAllowed())
            {
                fail("No Exception was thrownduring checkOut while the Document’s Object-Type definition’s versionable attribute is FALSE");
            }
            assertTrue("Content was not copied", checkOutResponse.isContentCopied());
            assertFalse("Checked out document id is equal to document id", documentId.equals(documentIdHolder));

            LOGGER.info("[VersioningService->cancelCheckOut]");
            getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), documentIdHolder, null));
            if (!isVersioningAllowed())
            {
                fail("No Exception was thrown during cancelCheckOut while the Document’s Object-Type definition’s versionable attribute is FALSE");
            }
            assertFalse("Document property '" + PROP_IS_VERSION_SERIES_CHECKED_OUT + "' value is true after cancelCheckOut was performed", getBooleanProperty(documentId,
                    PROP_IS_VERSION_SERIES_CHECKED_OUT));
        }
        catch (Exception e)
        {
            if (!isVersioningAllowed())
            {
                assertTrue("Invalid exception was thrown during checkOut/cancelCheckOut while the Document’s Object-Type definition’s versionable attribute is FALSE",
                        e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
            }
            else
            {
                fail(e.toString());
            }
        }
    }

    public void testCheckOutNotSetContentStream() throws Exception
    {
        if (isVersioningAllowed())
        {
            VersioningServicePort versioningServicePort = getServicesFactory().getVersioningService();
            String docTypeWithNoContentAllowed = searchAndAssertDocumentTypeWithContentNotAllowed();
            if ((null != docTypeWithNoContentAllowed) && !"".equals(docTypeWithNoContentAllowed))
            {
                String documentId = createAndAssertDocument(generateTestFileName(), docTypeWithNoContentAllowed, getAndAssertRootFolderId(), null, null, null);
                CheckOutResponse response = null;
                try
                {
                    LOGGER.info("[VersioningService->checkOut]");
                    response = versioningServicePort.checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
                    assertNotNull("CheckOut response is NULL", response);
                    documentIdHolder = response.getObjectId();
                    assertNotNull("Checked out document id is NULL", documentIdHolder);
                    assertFalse("The content-stream of the Private Working Copy is “not set”, but 'contentCopied' value is TRUE", response.isContentCopied());
                }
                catch (Exception e)
                {
                    fail(e.getMessage());
                }
                LOGGER.info("[VersioningService->cancelCheckOut]");
                versioningServicePort.cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), documentIdHolder, null));
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
            }
            else
            {
                LOGGER.warn("testCheckOutNotSetContentStream() was skipped: Document Type with Not Allowed Content was not found");
            }
        }
        else
        {
            LOGGER.warn("testCheckOutNotSetContentStream() was skipped: Versioning isn't supported");
        }
    }

    public void testCheckinNoExistsCheckOut() throws Exception
    {
        if (isVersioningAllowed())
        {
            try
            {
                LOGGER.info("[VersioningService->checkIn]");
                getServicesFactory().getVersioningService().checkIn(
                        new CheckIn(getAndAssertRepositoryId(), documentId, true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0), MIMETYPE_TEXT_PLAIN,
                                generateTestFileName(), TEST_CONTENT.getBytes(), null), TEST_CHECK_IN_COMMENT_MESSAGE, null, null, null, null));
                fail("No Exception was thrown");

            }
            catch (Exception e)
            {
                assertTrue("Invalid exception was thrown", e instanceof CmisFaultType);
            }
        }
        else
        {
            LOGGER.info("testCheckinNoExistsCheckOut was skipped: Versioning isn't supported");
        }
    }

    public void testCancelNotExistsCheckOut() throws Exception
    {
        if (isVersioningAllowed())
        {
            try
            {
                LOGGER.info("[VersioningService->cancelCheckOut]");
                getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(getAndAssertRepositoryId(), documentId, null));
                fail("No Exception was thrown");

            }
            catch (Exception e)
            {
                assertTrue("Invalid exception was thrown", e instanceof CmisFaultType);
            }
        }
        else
        {
            LOGGER.info("testCancelNotExistsCheckOut was skipped: Versioning isn't supported");
        }
    }

    public void testGetPropertiesOfLatestVersion() throws Exception
    {
        if (isVersioningAllowed())
        {
            CheckInResponse checkInResponse = new CheckInResponse(documentId, null);
            for (int i = 0; i < 2; i++)
            {
                CheckOutResponse checkOutResponse = checkOutAndAssert(checkInResponse.getObjectId());
                checkInResponse = checkInAndAssert(checkOutResponse.getObjectId(), true, new CmisPropertiesType(), null, "");
            }
            checkInResponse = checkInAndAssert(checkOutAndAssert(checkInResponse.getObjectId()).getObjectId(), true, new CmisPropertiesType(), null, "");
            CheckOutResponse checkOutResponse = checkOutAndAssert(checkInResponse.getObjectId());
            String checkInComment = "CheckIn" + System.currentTimeMillis();
            checkInResponse = checkInAndAssert(checkOutResponse.getObjectId(), true, new CmisPropertiesType(), null, checkInComment);

            GetPropertiesOfLatestVersionResponse response = null;
            String versionSeriesId = getIdProperty(documentId, PROP_VERSION_SERIES_ID);
            assertNotNull("'" + PROP_VERSION_SERIES_ID + "' property is NULL", versionSeriesId);
            try
            {
                LOGGER.info("[VersioningService->getPropertiesOfLatestVersion]");
                response = getServicesFactory().getVersioningService().getPropertiesOfLatestVersion(
                        new GetPropertiesOfLatestVersion(getAndAssertRepositoryId(), versionSeriesId, true, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetPropertiesOfLatestVersion response is NULL", response);
            assertNotNull("GetPropertiesOfLatestVersion response is empty", response.getProperties());
            assertTrue("Not latest version propertie were returned", checkInComment.equals(getStringProperty(response.getProperties(), PROP_CHECKIN_COMMENT)));
        }
        else
        {
            LOGGER.info("testGetPropertiesOfLatestVersionDefault was skipped: Versioning isn't supported");
        }
    }

    public void testGetPropertiesOfLatestVersionFiltered() throws Exception
    {
        if (isVersioningAllowed())
        {
            CheckInResponse checkInResponse = new CheckInResponse(documentId, null);
            for (int i = 0; i < 2; i++)
            {
                CheckOutResponse checkOutResponse = checkOutAndAssert(checkInResponse.getObjectId());
                checkInResponse = checkInAndAssert(checkOutResponse.getObjectId(), false, new CmisPropertiesType(), null, "");
            }
            checkInResponse = checkInAndAssert(checkOutAndAssert(checkInResponse.getObjectId()).getObjectId(), true, new CmisPropertiesType(), null, "");
            for (int i = 0; i < 2; i++)
            {
                CheckOutResponse checkOutResponse = checkOutAndAssert(checkInResponse.getObjectId());
                checkInResponse = checkInAndAssert(checkOutResponse.getObjectId(), false, new CmisPropertiesType(), null, "");
            }
            GetPropertiesOfLatestVersionResponse response = null;
            String versionSeriesId = getIdProperty(documentId, PROP_VERSION_SERIES_ID);
            assertNotNull("'" + PROP_VERSION_SERIES_ID + "' property is NULL", versionSeriesId);
            try
            {
                LOGGER.info("[VersioningService->getPropertiesOfLatestVersion]");
                response = getServicesFactory().getVersioningService().getPropertiesOfLatestVersion(
                        new GetPropertiesOfLatestVersion(getAndAssertRepositoryId(), versionSeriesId, true, PROP_NAME + "," + PROP_OBJECT_ID + "," + PROP_IS_LATEST_VERSION, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetPropertiesOfLatestVersion response is NULL", response);
            assertNotNull("GetPropertiesOfLatestVersion response is empty", response.getProperties());

            assertNull("Not expected properties were returned", response.getProperties().getPropertyDecimal());
            assertNull("Not expected properties were returned", response.getProperties().getPropertyHtml());
            assertNull("Not expected properties were returned", response.getProperties().getPropertyInteger());
            assertNull("Not expected properties were returned", response.getProperties().getPropertyUri());
            assertNull("Not expected properties were returned", response.getProperties().getPropertyDateTime());

            assertNotNull("Expected properties were not returned", response.getProperties().getPropertyId());
            assertNotNull("Expected properties were not returned", response.getProperties().getPropertyString());
            assertNotNull("Expected properties were not returned", response.getProperties().getPropertyBoolean());

            assertEquals("Expected property was not returned", 1, response.getProperties().getPropertyId().length);
            assertEquals("Expected property was not returned", 1, response.getProperties().getPropertyString().length);
            assertEquals("Expected property was not returned", 1, response.getProperties().getPropertyBoolean().length);

            assertNotNull("Expected property was not returned", getIdProperty(response.getProperties(), PROP_OBJECT_ID));
            assertNotNull("Expected property was not returned", getStringProperty(response.getProperties(), PROP_NAME));
            assertNotNull("Expected property was not returned", getBooleanProperty(response.getProperties(), PROP_IS_LATEST_VERSION));

        }
        else
        {
            LOGGER.info("testGetPropertiesOfLatestVersionFiltered was skipped: Versioning isn't supported");
        }
    }

    public void testGetPropertiesOfLatestVersionMajor() throws Exception
    {
        if (isVersioningAllowed())
        {
            VersioningServicePort versioningServicePort = getServicesFactory().getVersioningService();
            GetPropertiesOfLatestVersionResponse response = null;
            String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT,
                    EnumVersioningState.minor);
            String versionSeriesId = getIdProperty(documentId, PROP_VERSION_SERIES_ID);
            assertNotNull("'" + PROP_VERSION_SERIES_ID + "' property is NULL", versionSeriesId);
            try
            {
                LOGGER.info("[VersioningService->getPropertiesOfLatestVersion]");
                versioningServicePort.getPropertiesOfLatestVersion(new GetPropertiesOfLatestVersion(getAndAssertRepositoryId(), versionSeriesId, true, null, null));
                fail("No Exception was thrown  during getting properties of latest version while the input parameter major is TRUE and the Version Series contains no major versions");
            }
            catch (Exception e)
            {
                assertTrue(
                        "Invalid exception was thrown during getting properties of latest version while the input parameter major is TRUE and the Version Series contains no major versions",
                        e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.objectNotFound));
            }
            LOGGER.info("[VersioningService->checkOut]");
            CheckOutResponse checkOutResponse = versioningServicePort.checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
            assertNotNull("Checkout response is NULL", checkOutResponse);
            LOGGER.info("[VersioningService->checkIn]");
            CheckInResponse checkInResponse = versioningServicePort.checkIn(new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getObjectId(), true, new CmisPropertiesType(),
                    new CmisContentStreamType(BigInteger.valueOf(0), MIMETYPE_TEXT_PLAIN, generateTestFileName(), TEST_CONTENT.getBytes(), null), "Major", null, null, null, null));
            LOGGER.info("[VersioningService->checkOut]");
            checkOutResponse = versioningServicePort.checkOut(new CheckOut(getAndAssertRepositoryId(), checkInResponse.getObjectId(), null));
            assertNotNull("Checkout response is NULL", checkOutResponse);
            LOGGER.info("[VersioningService->checkIn]");
            checkInResponse = versioningServicePort.checkIn(new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getObjectId(), false, new CmisPropertiesType(),
                    new CmisContentStreamType(BigInteger.valueOf(0), MIMETYPE_TEXT_PLAIN, generateTestFileName(), TEST_CONTENT.getBytes(), null), "Minor", null, null, null, null));

            try
            {
                LOGGER.info("[VersioningService->getPropertiesOfLatestVersion]");
                response = versioningServicePort.getPropertiesOfLatestVersion(new GetPropertiesOfLatestVersion(getAndAssertRepositoryId(), versionSeriesId, true, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetPropertiesOfLatestVersion response is NULL", response);
            assertNotNull("GetPropertiesOfLatestVersion response is empty", response.getProperties());
            assertNotNull("GetPropertiesOfLatestVersion properties are NULL", response.getProperties());
            assertTrue("'" + PROP_IS_MAJOR_VERSION + "' property value is FALSE", (Boolean) getBooleanProperty(response.getProperties(), PROP_IS_MAJOR_VERSION));
            assertTrue("'" + PROP_IS_LATEST_MAJOR_VERSION + "' property value is FALSE", (Boolean) getBooleanProperty(response.getProperties(), PROP_IS_LATEST_MAJOR_VERSION));
            assertEquals("Major", getStringProperty(response.getProperties(), PROP_CHECKIN_COMMENT));

            try
            {
                LOGGER.info("[VersioningService->getPropertiesOfLatestVersion]");
                response = versioningServicePort.getPropertiesOfLatestVersion(new GetPropertiesOfLatestVersion(getAndAssertRepositoryId(), versionSeriesId, false, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetPropertiesOfLatestVersion response is NULL", response);
            assertNotNull("GetPropertiesOfLatestVersion response is empty", response.getProperties());
            assertNotNull("GetPropertiesOfLatestVersion properties are NULL", response.getProperties());
            assertTrue("'" + PROP_IS_LATEST_VERSION + "' property value is FALSE", (Boolean) getBooleanProperty(response.getProperties(), PROP_IS_LATEST_VERSION));
            assertFalse("'" + PROP_IS_MAJOR_VERSION + "' property value is TRUE", (Boolean) getBooleanProperty(response.getProperties(), PROP_IS_MAJOR_VERSION));
            assertFalse("'" + PROP_IS_LATEST_MAJOR_VERSION + "' property value is TRUE", (Boolean) getBooleanProperty(response.getProperties(), PROP_IS_LATEST_MAJOR_VERSION));
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), checkInResponse.getObjectId(), true, null));

        }
        else
        {
            LOGGER.info("testGetPropertiesOfLatestVersionDefault was skipped: Versioning isn't supported");
        }
    }

    public void testGetAllVersions() throws Exception
    {
        if (isVersioningAllowed())
        {
            String checkinComment = "Test checkin" + System.currentTimeMillis();

            LOGGER.info("[VersioningService->checkOut]");
            CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
            assertNotNull("Checkout response is NULL", checkOutResponse);
            documentIdHolder = checkOutResponse.getObjectId();
            LOGGER.info("[VersioningService->checkIn]");
            CheckInResponse checkInResponse = getServicesFactory().getVersioningService().checkIn(
                    new CheckIn(getAndAssertRepositoryId(), documentIdHolder, true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0), MIMETYPE_TEXT_PLAIN,
                            generateTestFileName(), TEST_CONTENT.getBytes(), null), checkinComment, null, null, null, null));
            assertNotNull("Checkin response is NULL", checkInResponse);
            documentId = checkInResponse.getObjectId();

            CmisObjectType[] response = null;
            String versionSeriesId = getIdProperty(documentId, PROP_VERSION_SERIES_ID);
            assertNotNull("'" + PROP_VERSION_SERIES_ID + "' property is NULL", versionSeriesId);
            try
            {
                LOGGER.info("[VersioningService->getAllVersions]");
                response = getServicesFactory().getVersioningService().getAllVersions(new GetAllVersions(getAndAssertRepositoryId(), versionSeriesId, null, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetAllVersions response is NULL", response);
            assertTrue("GetAllVersions response is empty", response.length > 0);
            boolean found = false;
            for (CmisObjectType obj : response)
            {
                assertNotNull("Object is null", obj);
                if (checkinComment.equals(getStringProperty(obj.getProperties(), PROP_CHECKIN_COMMENT)))
                {
                    found = true;
                }
            }
            assertTrue("Not all versions were returned", found);
        }
        else
        {
            LOGGER.info("testGetAllVersionsDefault was skipped: Versioning isn't supported");
        }
    }

    public void testGetAllVersionsFiltered() throws Exception
    {
        if (isVersioningAllowed())
        {
            String checkinComment = "Test checkin" + System.currentTimeMillis();

            LOGGER.info("[VersioningService->checkOut]");
            CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
            assertNotNull("Checkout response is NULL", checkOutResponse);
            documentIdHolder = checkOutResponse.getObjectId();
            LOGGER.info("[VersioningService->checkIn]");
            CheckInResponse checkInResponse = getServicesFactory().getVersioningService().checkIn(
                    new CheckIn(getAndAssertRepositoryId(), documentIdHolder, true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0), MIMETYPE_TEXT_PLAIN,
                            generateTestFileName(), TEST_CONTENT.getBytes(), null), checkinComment, null, null, null, null));
            assertNotNull("Checkin response is NULL", checkInResponse);
            documentId = checkInResponse.getObjectId();

            CmisObjectType[] response = null;
            String versionSeriesId = getIdProperty(documentId, PROP_VERSION_SERIES_ID);
            assertNotNull("'" + PROP_VERSION_SERIES_ID + "' property is NULL", versionSeriesId);
            try
            {
                LOGGER.info("[VersioningService->getAllVersions]");
                response = getServicesFactory().getVersioningService().getAllVersions(
                        new GetAllVersions(getAndAssertRepositoryId(), versionSeriesId, PROP_NAME + "," + PROP_OBJECT_ID + "," + PROP_CHECKIN_COMMENT, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetAllVersions response is NULL", response);
            assertTrue("GetAllVersions response is empty", response.length > 0);
            assertNotNull("GetAllVersions response is empty", response[0]);

            for (CmisObjectType object : response)
            {
                CmisPropertiesType properties = object.getProperties();

                assertNull("Not expected properties were returned", properties.getPropertyBoolean());
                assertNull("Not expected properties were returned", properties.getPropertyDecimal());
                assertNull("Not expected properties were returned", properties.getPropertyHtml());
                assertNull("Not expected properties were returned", properties.getPropertyInteger());
                assertNull("Not expected properties were returned", properties.getPropertyUri());
                assertNull("Not expected properties were returned", properties.getPropertyDateTime());

                assertNotNull("Expected properties were not returned", properties.getPropertyId());
                assertNotNull("Expected properties were not returned", properties.getPropertyString());

                assertEquals("Expected properties were not returned", 1, properties.getPropertyId().length);
                assertTrue("Expected properties were not returned", 2 >= properties.getPropertyString().length);

                assertNotNull("Expected property was not returned", getIdProperty(properties, PROP_OBJECT_ID));
                assertNotNull("Expected property was not returned", getStringProperty(properties, PROP_NAME));
            }
        }
        else
        {
            LOGGER.info("testGetAllVersionsFiltered was skipped: Versioning isn't supported");
        }
    }

    public void testGetAllVersionsForNoVersionHistory() throws Exception
    {
        if (isVersioningAllowed())
        {
            CmisObjectType[] response = null;
            String versionSeriesId = getIdProperty(documentId, PROP_VERSION_SERIES_ID);
            assertNotNull("'" + PROP_VERSION_SERIES_ID + "' property is NULL", versionSeriesId);
            try
            {
                LOGGER.info("[VersioningService->getAllVersions]");
                response = getServicesFactory().getVersioningService().getAllVersions(new GetAllVersions(getAndAssertRepositoryId(), versionSeriesId, "*", null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetAllVersions response is NULL", response);
            assertTrue("GetAllVersions response is empty", response.length > 0);
            assertNotNull("GetAllVersions response is empty", response[0]);
        }
        else
        {
            LOGGER.info("testGetAllVersionsForNoVersionHistory was skipped: Versioning isn't supported");
        }
    }

    public void testGetAllVersionsPWC() throws Exception
    {
        if (isVersioningAllowed())
        {
            LOGGER.info("[VersioningService->checkOut]");
            CheckOutResponse checkOutResponse = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
            assertNotNull("Checkout response is NULL", checkOutResponse);
            documentIdHolder = checkOutResponse.getObjectId();

            CmisObjectType[] response = null;
            String versionSeriesId = getIdProperty(documentId, PROP_VERSION_SERIES_ID);
            assertNotNull("'" + PROP_VERSION_SERIES_ID + "' property is NULL", versionSeriesId);
            try
            {
                LOGGER.info("[VersioningService->getAllVersions]");
                response = getServicesFactory().getVersioningService().getAllVersions(new GetAllVersions(getAndAssertRepositoryId(), versionSeriesId, "*", null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertNotNull("GetAllVersions response is NULL", response);
            assertTrue("GetAllVersions response is empty", response.length > 0);
            assertNotNull("GetAllVersions response is empty", response[0]);

            boolean pwcFound = false;
            for (CmisObjectType cmisObjectType : response)
            {
                if (!pwcFound)
                {
                    pwcFound = getIdProperty(cmisObjectType.getProperties(), PROP_OBJECT_ID).equals(documentIdHolder);
                }
            }
            assertTrue("No private working copy found", pwcFound);
        }
        else
        {
            LOGGER.info("testGetAllVersionsPWC was skipped: Versioning isn't supported");
        }
    }

    public void testWrongRepositoryIdUsing() throws Exception
    {
        if (isVersioningAllowed())
        {
            try
            {
                LOGGER.info("[VersioningService->cancelCheckOut]");
                getServicesFactory().getVersioningService().cancelCheckOut(new CancelCheckOut(INVALID_REPOSITORY_ID, documentId, null));
                fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (Exception e)
            {
            }
            try
            {
                LOGGER.info("[VersioningService->checkOut]");
                documentIdHolder = getServicesFactory().getVersioningService().checkOut(new CheckOut(INVALID_REPOSITORY_ID, documentId, null)).getObjectId();
                fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (Exception e)
            {
            }
            try
            {
                LOGGER.info("[VersioningService->checkIn]");
                getServicesFactory().getVersioningService().checkIn(
                        new CheckIn(INVALID_REPOSITORY_ID, documentId, true, new CmisPropertiesType(), new CmisContentStreamType(BigInteger.valueOf(0), MIMETYPE_TEXT_PLAIN,
                                generateTestFileName(), new byte[0], null), TEST_CHECK_IN_COMMENT_MESSAGE, null, null, null, null));
                fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (Exception e)
            {
            }
            String versionSeriesId = getIdProperty(documentId, PROP_VERSION_SERIES_ID);
            assertNotNull("'VersionSeriesId' property is NULL", versionSeriesId);
            try
            {
                LOGGER.info("[VersioningService->getAllVersions]");
                getServicesFactory().getVersioningService().getAllVersions(new GetAllVersions(INVALID_REPOSITORY_ID, versionSeriesId, "*", null, null));
                fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (Exception e)
            {
            }
            try
            {
                LOGGER.info("[VersioningService->getPropertiesOfLatestVersion]");
                getServicesFactory().getVersioningService()
                        .getPropertiesOfLatestVersion(new GetPropertiesOfLatestVersion(INVALID_REPOSITORY_ID, versionSeriesId, true, null, null));
                fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (Exception e)
            {
            }
        }
        else
        {
            LOGGER.info("testWrongRepositoryIdUsing was skipped: Versioning isn't supported");
        }
    }
}
