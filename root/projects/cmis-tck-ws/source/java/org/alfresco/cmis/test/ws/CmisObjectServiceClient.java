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

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.repo.cmis.ws.AddObjectToFolder;
import org.alfresco.repo.cmis.ws.CheckIn;
import org.alfresco.repo.cmis.ws.CheckInResponse;
import org.alfresco.repo.cmis.ws.CheckOut;
import org.alfresco.repo.cmis.ws.CheckOutResponse;
import org.alfresco.repo.cmis.ws.CmisAccessControlEntryType;
import org.alfresco.repo.cmis.ws.CmisAccessControlListType;
import org.alfresco.repo.cmis.ws.CmisAccessControlPrincipalType;
import org.alfresco.repo.cmis.ws.CmisContentStreamType;
import org.alfresco.repo.cmis.ws.CmisFaultType;
import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisPropertiesType;
import org.alfresco.repo.cmis.ws.CmisPropertyId;
import org.alfresco.repo.cmis.ws.CmisPropertyString;
import org.alfresco.repo.cmis.ws.CmisPropertyStringDefinitionType;
import org.alfresco.repo.cmis.ws.CmisRenditionType;
import org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType;
import org.alfresco.repo.cmis.ws.CmisTypeDefinitionType;
import org.alfresco.repo.cmis.ws.CreateDocument;
import org.alfresco.repo.cmis.ws.CreateDocumentFromSource;
import org.alfresco.repo.cmis.ws.CreateFolder;
import org.alfresco.repo.cmis.ws.DeleteContentStream;
import org.alfresco.repo.cmis.ws.DeleteContentStreamResponse;
import org.alfresco.repo.cmis.ws.DeleteObject;
import org.alfresco.repo.cmis.ws.DeleteTree;
import org.alfresco.repo.cmis.ws.DeleteTreeResponse;
import org.alfresco.repo.cmis.ws.EnumCapabilityACL;
import org.alfresco.repo.cmis.ws.EnumCapabilityRendition;
import org.alfresco.repo.cmis.ws.EnumIncludeRelationships;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.EnumUnfileObject;
import org.alfresco.repo.cmis.ws.EnumVersioningState;
import org.alfresco.repo.cmis.ws.GetAllVersions;
import org.alfresco.repo.cmis.ws.GetAllowableActions;
import org.alfresco.repo.cmis.ws.GetAllowableActionsResponse;
import org.alfresco.repo.cmis.ws.GetContentStream;
import org.alfresco.repo.cmis.ws.GetContentStreamResponse;
import org.alfresco.repo.cmis.ws.GetObject;
import org.alfresco.repo.cmis.ws.GetObjectByPath;
import org.alfresco.repo.cmis.ws.GetObjectByPathResponse;
import org.alfresco.repo.cmis.ws.GetObjectResponse;
import org.alfresco.repo.cmis.ws.GetProperties;
import org.alfresco.repo.cmis.ws.GetPropertiesOfLatestVersion;
import org.alfresco.repo.cmis.ws.GetPropertiesOfLatestVersionResponse;
import org.alfresco.repo.cmis.ws.GetPropertiesResponse;
import org.alfresco.repo.cmis.ws.GetRenditions;
import org.alfresco.repo.cmis.ws.MoveObject;
import org.alfresco.repo.cmis.ws.ObjectServicePortBindingStub;
import org.alfresco.repo.cmis.ws.SetContentStream;
import org.alfresco.repo.cmis.ws.UpdateProperties;
import org.alfresco.repo.cmis.ws.VersioningServicePortBindingStub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

/**
 * Client for Object Service
 */
public class CmisObjectServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisObjectServiceClient.class);

    private static final String UPDATE_FILE_NAME = "UpdatedFileName.txt";

    private static final String TEST_IMAGE_NAME = "TestImage.jpg";
    private static final String MIMETYPE_IMAGE_JPEG = "image/jpeg";

    private static final String PROPERTIES_NOT_RETURNED_MESSAGE = "Properties were not returned";
    private static final String CHECKEDOUT_WITHOUT_REQUEST_MESSAGE = "Document was Checked Out without appropriate 'CHECKEDOUT' Versioning State attribute";

    private Resource imageResource;

    public CmisObjectServiceClient()
    {
    }

    public CmisObjectServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public void setImageResource(Resource imageResource)
    {
        this.imageResource = imageResource;
    }

    /**
     * Initializes Object Service client
     */
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
    }

    /**
     * Invokes all methods in Object Service
     */
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }

        ObjectServicePortBindingStub objectServicePort = getServicesFactory().getObjectService(getProxyUrl() + getService().getPath());

        CmisPropertiesType properties = new CmisPropertiesType();
        CmisPropertyString cmisPropertyName = new CmisPropertyString();
        cmisPropertyName.setPropertyDefinitionId(PROP_NAME);
        cmisPropertyName.setValue(new String[] { generateTestFileName() });
        CmisPropertyId idProperty = new CmisPropertyId();
        idProperty.setPropertyDefinitionId(PROP_OBJECT_TYPE_ID);
        idProperty.setValue(new String[] { getAndAssertDocumentTypeId() });
        properties.setPropertyString(new CmisPropertyString[] { cmisPropertyName });
        properties.setPropertyId(new CmisPropertyId[] { idProperty });
        CmisContentStreamType cmisStream = new CmisContentStreamType();
        cmisStream.setFilename(generateTestFileName());
        cmisStream.setMimeType(MIMETYPE_TEXT_PLAIN);
        cmisStream.setStream(TEST_CONTENT.getBytes(ENCODING));
        CreateDocument createDocumentParameters = new CreateDocument(getAndAssertRepositoryId(), properties, getAndAssertRootFolderId(), cmisStream, EnumVersioningState
                .fromString(EnumVersioningState._major), null, null, null, null);
        String documentId = objectServicePort.createDocument(createDocumentParameters).getObjectId();

        properties = new CmisPropertiesType();
        cmisPropertyName = new CmisPropertyString();
        cmisPropertyName.setPropertyDefinitionId(PROP_NAME);
        cmisPropertyName.setValue(new String[] { generateTestFolderName() });
        idProperty = new CmisPropertyId();
        idProperty.setPropertyDefinitionId(PROP_OBJECT_TYPE_ID);
        idProperty.setValue(new String[] { getAndAssertFolderTypeId() });
        properties.setPropertyString(new CmisPropertyString[] { cmisPropertyName });
        properties.setPropertyId(new CmisPropertyId[] { idProperty });
        CreateFolder createFolderParameters = new CreateFolder(getAndAssertRepositoryId(), properties, getAndAssertRootFolderId(), null, null, null, null);
        String folderId = objectServicePort.createFolder(createFolderParameters).getObjectId();

        GetAllowableActions getAllowableActionsParameters = new GetAllowableActions(getAndAssertRepositoryId(), folderId, null);
        objectServicePort.getAllowableActions(getAllowableActionsParameters);

        objectServicePort.getProperties(new GetProperties(getAndAssertRepositoryId(), documentId, "*", null));

        properties = new CmisPropertiesType();
        cmisPropertyName = new CmisPropertyString();
        cmisPropertyName.setPropertyDefinitionId(PROP_NAME);
        cmisPropertyName.setValue(new String[] { UPDATE_FILE_NAME });
        properties.setPropertyString(new CmisPropertyString[] { cmisPropertyName });
        UpdateProperties updatePropertiesParameters = new UpdateProperties(getAndAssertRepositoryId(), documentId, "", properties, null);
        documentId = objectServicePort.updateProperties(updatePropertiesParameters).getObjectId();

        objectServicePort.getContentStream(new GetContentStream(getAndAssertRepositoryId(), documentId, "", null, null, null));

        MoveObject moveObjectParameters = new MoveObject(getAndAssertRepositoryId(), documentId, folderId, getAndAssertRootFolderId(), null);
        objectServicePort.moveObject(moveObjectParameters);

        CmisContentStreamType contentStream = new CmisContentStreamType();
        contentStream.setFilename(TEST_IMAGE_NAME);
        contentStream.setMimeType(MIMETYPE_IMAGE_JPEG);
        InputStream viewStream = imageResource.getInputStream();
        byte[] streamBytes = new byte[viewStream.available()];
        viewStream.read(streamBytes);
        contentStream.setStream(streamBytes);
        SetContentStream setContentStreamParameters = new SetContentStream(getAndAssertRepositoryId(), documentId, true, "", contentStream, null);
        documentId = objectServicePort.setContentStream(setContentStreamParameters).getObjectId();

        // TODO WSDL does not correspond to specification
        // objectServicePort.deleteContentStream(new DeleteContentStream(getAndAssertRepositoryId(), documentId));

        deleteAndAssertObject(documentId);

        objectServicePort.deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, false, EnumUnfileObject.fromString(EnumUnfileObject._delete), true, null));
    }

    @Override
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
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
        AbstractServiceClient client = (CmisObjectServiceClient) applicationContext.getBean("cmisObjectServiceClient");
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
        super.onSetUp();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        super.onTearDown();
    }

    public void testDocumentCreation() throws Exception
    {
        String documentId = createAndAssertDocument();
        deleteAndAssertObject(documentId);
    }

    public void testDocumentCreationConstrainsObservance() throws Exception
    {
        String rootFolderId = getAndAssertRootFolderId();
        String documentTypeId = getAndAssertDocumentTypeId();
        if (!isContentStreamAllowed())
        {
            assertDocumentConstraitException("Creating Document with Content Stream when Content Stream is 'not allowed'", EnumServiceException.streamNotSupported,
                    generateTestFileName(), documentTypeId, rootFolderId, TEST_CONTENT, null, true);
        }
        assertDocumentConstraitException("Creating Document with 'none document' Type Id", generateTestFileName(), getAndAssertFolderTypeId(), rootFolderId, TEST_CONTENT, null);
        assertNotAllowedObjectException(rootFolderId, true);
        if (isContentStreamRequired())
        {
            assertDocumentConstraitException("Creating Document with 'required' Content Stream Type without Content Stream input parameter", generateTestFileName(),
                    documentTypeId, rootFolderId, null, null);
        }
        String constrainedDocumentTypeId = searchAndAssertNotVersionableDocumentType();
        if (null != constrainedDocumentTypeId)
        {
            assertDocumentConstraitException("Creating not 'versionalbe' Document with Version Type input parameter equal to 'MAJOR'", generateTestFileName(),
                    constrainedDocumentTypeId, getAndAssertFolderTypeId(), TEST_CONTENT, EnumVersioningState.major);
            assertDocumentConstraitException("Creating not 'versionalbe' Document with Version Type input parameter equal to 'CHECKEDOUT'", generateTestFileName(),
                    constrainedDocumentTypeId, getAndAssertFolderTypeId(), TEST_CONTENT, EnumVersioningState.checkedout);
        }
        CmisTypeDefinitionType typeDef = getAndAssertTypeDefinition(documentTypeId);
        CmisPropertyStringDefinitionType propertyDefinition = null;
        for (CmisPropertyStringDefinitionType propDef : typeDef.getPropertyStringDefinition())
        {
            if ((null != propDef.getMaxLength()) && (BigInteger.ZERO.compareTo(propDef.getMaxLength()) < 0))
            {
                propertyDefinition = propDef;
                break;
            }
        }
        if (null != propertyDefinition)
        {
            StringBuilder largeAppender = new StringBuilder("");
            long boundary = propertyDefinition.getMaxLength().longValue();
            for (long i = 0; i <= (boundary + 5); i++)
            {
                largeAppender.append("A");
            }
            CmisPropertiesType properties = new CmisPropertiesType();
            properties
                    .setPropertyString(new CmisPropertyString[] { new CmisPropertyString(propertyDefinition.getId(), null, null, null, new String[] { largeAppender.toString() }) });
            assertDocumentConstraitException(("Creating Document with outing from bounds Max Length of '" + propertyDefinition.getId() + "' property"),
                    EnumServiceException.constraint, generateTestFileName(), documentTypeId, getAndAssertRootFolderId(), properties, TEST_CONTENT, null);
        }

        // TODO: “controllablePolicy” is set to FALSE and at least one policy is provided
        // TODO: “controllableACL” is set to FALSE and at least one ACE is provided
        // TODO: at least one of the permissions is used in an ACE provided which is not supported by the repository
    }

    private void assertNotAllowedObjectException(String rootFolderId, boolean document) throws Exception
    {
        String constrainedTypeId = searchAndAssertNotAllowedForFolderObjectTypeId(rootFolderId, document);
        String folderId = null;
        if (null == constrainedTypeId)
        {
            String customFolderTypeId = searchAndAssertFolderFromNotBaseType();
            CmisPropertiesType properties = new CmisPropertiesType();
            properties
                    .setPropertyId(new CmisPropertyId[] { new CmisPropertyId(PROP_ALLOWED_CHILD_OBJECT_TYPE_IDS, null, null, null, new String[] { getAndAssertDocumentTypeId() }) });
            folderId = createAndAssertFolder(generateTestFolderName(), customFolderTypeId, getAndAssertRootFolderId(), properties);
            constrainedTypeId = searchAndAssertNotAllowedForFolderObjectTypeId(folderId, document);
        }
        if (null != constrainedTypeId)
        {
            rootFolderId = (null != folderId) ? (folderId) : (rootFolderId);
            if (document)
            {
                assertDocumentConstraitException("Creating Document with 'not allowable object type' for Parent Folder", generateTestFileName(), constrainedTypeId, rootFolderId,
                        TEST_CONTENT, null);
            }
            else
            {
                assertFolderConstraitException("Creating Folder with 'not allowable object type' for Parent Folder", EnumServiceException.constraint, generateTestFolderName(),
                        constrainedTypeId, rootFolderId, null);
            }
        }
        if (null != folderId)
        {
            deleteAndAssertObject(folderId);
        }
    }

    private void assertDocumentConstraitException(String constraintCase, String documentName, String documentTypeId, String folderId, String content,
            EnumVersioningState initialVersion) throws Exception
    {
        assertDocumentConstraitException(constraintCase, null, documentName, documentTypeId, folderId, content, initialVersion, false);
    }

    private void assertDocumentConstraitException(String constraintCase, EnumServiceException expectedException, String documentName, String documentTypeId, String folderId,
            String content, EnumVersioningState initialVersion, boolean setContentStreamForcibly) throws Exception
    {
        assertDocumentConstraitException(constraintCase, expectedException, documentName, documentTypeId, folderId, null, content, initialVersion);
    }

    private void assertDocumentConstraitException(String constraintCase, EnumServiceException expectedException, String documentName, String documentTypeId, String folderId,
            CmisPropertiesType properties, String content, EnumVersioningState initialVersion) throws Exception
    {
        try
        {
            String documentId = createAndAssertDocument(documentName, documentTypeId, folderId, properties, content, initialVersion);
            deleteAndAssertObject(documentId);
            fail("Either expected '" + expectedException.getValue() + "' Exception nor any Exception at all was thrown during " + constraintCase);
        }
        catch (Exception e)
        {
            assertException(constraintCase, e, expectedException);
        }
    }

    public void testDocumentCreationWithoutProperties() throws Exception
    {
        assertDocumentConstraitException("Creating Document without mandatory input parameter 'properties'", EnumServiceException.invalidArgument, null, null,
                getAndAssertRootFolderId(), null, TEST_CONTENT, null);
    }

    public void testDocumentCreatingAndUnfilingCapabilitySupporting() throws Exception
    {
        CmisRepositoryCapabilitiesType capabilities = getAndAssertCapabilities();
        if (capabilities.isCapabilityUnfiling())
        {
            String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), null, null, TEST_CONTENT, null);
            deleteAndAssertObject(documentId);
        }
        else
        {
            assertDocumentConstraitException("Creating Document without Parent Folder Id input parameter when Unfiling Capability is not supported", generateTestFileName(),
                    getAndAssertDocumentTypeId(), null, TEST_CONTENT, null);
        }
    }

    public void testDocumentCreationAccordingToVersioningAttribute() throws Exception
    {
        if (!isVersioningAllowed())
        {
            logger.info("No one Document Object Type with 'versionable = true' attribute was found. Test will be skipped...");
            return;
        }

        String documentId = createAndAssertVersionedDocument(EnumVersioningState.minor);
        deleteAndAssertObject(documentId);
        documentId = createAndAssertVersionedDocument(EnumVersioningState.major);
        deleteAndAssertObject(documentId);
        documentId = createAndAssertVersionedDocument(EnumVersioningState.checkedout);
        documentId = cancelCheckOutAndAssert(documentId);
        deleteAndAssertObject(documentId);
    }

    public void testDocumentCreatingWithACL() throws Exception
    {
        if (!EnumCapabilityACL.manage.equals(getAndAssertCapabilities().getCapabilityACL()))
        {
            logger.info("Repository doesn't support 'manage ACL' capability. Test will be skipped...");
            return;
        }
        if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
        {
            logger.info("ACL Credentials or ACL PrincipalId were not set. Test will be skipped...");
            return;
        }

        CmisAccessControlListType acList = new CmisAccessControlListType();
        CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType(aclPrincipalId, null);
        CmisAccessControlEntryType ace = new CmisAccessControlEntryType(principal, new String[] { PERMISSION_READ }, true, null);
        acList.setPermission(new CmisAccessControlEntryType[] { ace });

        String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, null, acList, null);

        getPropertiesUsingCredentials(documentId, aclUsername, aclPassword);

        deleteAndAssertObject(documentId);

        acList = new CmisAccessControlListType();
        principal = new CmisAccessControlPrincipalType(aclPrincipalId, null);
        ace = new CmisAccessControlEntryType(principal, new String[] { PERMISSION_WRITE }, true, null);
        acList.setPermission(new CmisAccessControlEntryType[] { ace });

        documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, null, acList, null);
        documentId = updatePropertiesUsingCredentials(documentId, aclUsername, aclPassword);

        deleteAndAssertObject(documentId);

    }

    private String createAndAssertVersionedDocument(EnumVersioningState versioningState) throws Exception
    {
        String documentId = null;
        try
        {
            documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, versioningState);
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        GetPropertiesOfLatestVersionResponse response = null;
        String versionSeriesId = getIdProperty(documentId, PROP_VERSION_SERIES_ID);
        assertNotNull("'" + PROP_VERSION_SERIES_ID + "' property is NULL", versionSeriesId);
        try
        {
            LOGGER.info("[VersioningService->getPropertiesOfLatestVersion]");
            response = getServicesFactory().getVersioningService().getPropertiesOfLatestVersion(
                    new GetPropertiesOfLatestVersion(getAndAssertRepositoryId(), versionSeriesId, EnumVersioningState._major.equals(versioningState.getValue()), null, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("GetPropertiesOfLatestVersion response is NULL", response);
        assertNotNull("GetPropertiesOfLatestVersion response is empty", response.getProperties());
        boolean majorVersion = getBooleanProperty(response.getProperties(), PROP_IS_MAJOR_VERSION);
        boolean checkedOut = getBooleanProperty(response.getProperties(), PROP_IS_VERSION_SERIES_CHECKED_OUT);
        if (EnumVersioningState._major.equals(versioningState.getValue()))
        {
            assertTrue("Create Document service call was performed with 'MAJOR' Versioning State but it has no 'MAJOR' Versioning State", majorVersion);
            assertFalse(CHECKEDOUT_WITHOUT_REQUEST_MESSAGE, checkedOut);
        }
        else
        {
            if (EnumVersioningState._checkedout.equals(versioningState.getValue()))
            {
                assertTrue("Create Document service call was performed with 'CHECKEDOUT' Versioning State but it has no 'CHECKEDOUT' State", checkedOut);
            }
            else
            {
                assertFalse("Create Document service call was performed with 'MINOR' Versioning State but it has 'MAJOR' Versioning State", majorVersion);
                assertFalse(CHECKEDOUT_WITHOUT_REQUEST_MESSAGE, checkedOut);
            }
        }
        return documentId;
    }

    // TODO: <Array> policies parameter for createDocument testing
    // TODO: <Array> ACE addACEs parameter for createDocument testing
    // TODO: <Array> ACE removeACEs parameter for createDocument testing

    public void testDocumentFromSourceCreation() throws Exception
    {
        String sourceDocumentId = createAndAssertDocument();
        String targetFolderId = createAndAssertFolder();

        String newName = "FROM_SOURCE" + generateTestFileName();
        CmisPropertiesType properties = new CmisPropertiesType();
        CmisPropertyString cmisPropertyName = new CmisPropertyString();
        cmisPropertyName.setPropertyDefinitionId(PROP_NAME);
        cmisPropertyName.setValue(new String[] { newName });
        properties.setPropertyString(new CmisPropertyString[] { cmisPropertyName });

        EnumVersioningState versioningState = isVersioningAllowed() ? EnumVersioningState.major : EnumVersioningState.none;
        String documentId = getServicesFactory().getObjectService().createDocumentFromSource(
                new CreateDocumentFromSource(getAndAssertRepositoryId(), sourceDocumentId, properties, targetFolderId, versioningState, null, null, null, null)).getObjectId();
        assertEquals("Name was not set", newName, getStringProperty(documentId, PROP_NAME));
        GetContentStreamResponse response = null;
        isDocumentInFolder(documentId, targetFolderId);
        if (isContentStreamAllowed())
        {
            try
            {
                LOGGER.info("[ObjectService->getContentStream]");
                response = getServicesFactory().getObjectService().getContentStream(new GetContentStream(getAndAssertRepositoryId(), documentId, "", null, null, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertTrue("No content stream was returned", response != null && response.getContentStream() != null);
            assertTrue("Invalid content stream was set to document", Arrays.equals(TEST_CONTENT.getBytes(), response.getContentStream().getStream()));
            LOGGER.info("[ObjectService->deleteObject]");
        }
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), targetFolderId, false, null));
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), sourceDocumentId, true, null));
    }

    public void testFolderCreation() throws Exception
    {
        String folderId = createAndAssertFolder();
        deleteAndAssertObject(folderId);
    }

    public void testFolderCreatingWithACL() throws Exception
    {
        if (!EnumCapabilityACL.manage.equals(getAndAssertCapabilities().getCapabilityACL()))
        {
            logger.info("Repository doesn't support 'manage ACL' capability. Test will be skipped...");
            return;
        }
        if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
        {
            logger.info("ACL Credentials or ACL PrincipalId were not set. Test will be skipped...");
            return;
        }

        CmisAccessControlListType acList = new CmisAccessControlListType();
        CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType(aclPrincipalId, null);
        CmisAccessControlEntryType ace = new CmisAccessControlEntryType(principal, new String[] { PERMISSION_READ }, true, null);
        acList.setPermission(new CmisAccessControlEntryType[] { ace });

        String folderId = createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), getAndAssertRootFolderId(), null, acList, null);

        getPropertiesUsingCredentials(folderId, aclUsername, aclPassword);

        deleteAndAssertObject(folderId);
    }

    public void testFolderCreationWithoutProperties() throws Exception
    {
        assertFolderConstraitException("Folder Creation without mandatory 'properties' input parameter", EnumServiceException.invalidArgument, null, null,
                getAndAssertRootFolderId(), null);
    }

    public void testFolderCreationWithDifferentlyInvalidParentId() throws Exception
    {
        String folderTypeId = getAndAssertFolderTypeId();
        String documentId = createAndAssertDocument();
        assertFolderConstraitException("Folder Creation with none Folder 'parent folder id' input parameter", null, generateTestFolderName(), folderTypeId, documentId, null);
        deleteAndAssertObject(documentId);
        assertFolderConstraitException("Folder Creation with invalid 'parent folder id' input parameter", null, generateTestFolderName(), folderTypeId, "Invalid Parent Folder Id",
                null);
    }

    public void testFolderCreationConstraintsObservance() throws Exception
    {
        String rootFolderId = getAndAssertRootFolderId();
        assertFolderConstraitException("Folder Creation with none Folder 'type id' input parameter", EnumServiceException.constraint, generateTestFolderName(),
                getAndAssertDocumentTypeId(), rootFolderId, null);
        assertNotAllowedObjectException(rootFolderId, false);

        // TODO: “controllablePolicy” is set to FALSE and at least one policy is provided
        // TODO: “controllableACL” is set to FALSE and at least one ACE is provided
        // TODO: at least one of the permissions is used in an ACE provided which is not supported by the repository
    }

    private void assertFolderConstraitException(String constraintCase, EnumServiceException expectedException, String folderName, String folderTypeId, String parentFolderId,
            CmisPropertiesType properties) throws Exception
    {
        try
        {
            String folderId = createAndAssertFolder(folderName, folderTypeId, parentFolderId, properties);
            deleteAndAssertObject(folderId);
            fail("Either expected '" + expectedException.getValue() + "' Exception nor any Exception at all was thrown during " + constraintCase);
        }
        catch (Exception e)
        {
            assertException(constraintCase, e, expectedException);
        }
    }

    // TODO: <Array> policies parameter for createFolder testing
    // TODO: <Array> ACE addACEs parameter for createFolder testing
    // TODO: <Array> ACE removeACEs parameter for createFolder testing

    public void testCreateRelationship() throws Exception
    {
        String relationshipId = null;
        try
        {
            relationshipId = createAndAssertRelationship();
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), relationshipId, false, null));
    }

    private void assertRelationshipConstraitException(String constraintCase, EnumServiceException expectedException, String sourceId, String targetId, String relationshipTypeId)
            throws Exception
    {
        try
        {
            String relationshipId = createAndAssertRelationship(sourceId, targetId, relationshipTypeId);
            deleteAndAssertObject(relationshipId);
            fail("Either expected '" + expectedException.getValue() + "' Exception nor any Exception at all was thrown during " + constraintCase);
        }
        catch (Exception e)
        {
            assertException(constraintCase, e, expectedException);
        }
    }

    public void testRelationshipCreationConstraintsObservance() throws Exception
    {
        assertRelationshipConstraitException("Relationship Creation with typeId is not an Object-Type whose baseType is Relationship", EnumServiceException.invalidArgument, null,
                null, getAndAssertFolderTypeId());

        String relationshipTypeId = searchAndAssertRelationshipTypeWithAllowedSourceTypes();
        if (relationshipTypeId != null)
        {
            String notAllowdSourceTypeId = searchAndAssertNotAllowedSourceForRelationshipTypeId(relationshipTypeId);
            assertRelationshipConstraitException(
                    "Relationship Creation with the sourceObjectId’s ObjectType is not in the list of “allowedSourceTypes” specified by the Object-Type definition specified by typeId",
                    null, notAllowdSourceTypeId, null, relationshipTypeId);
        }

        relationshipTypeId = searchAndAssertRelationshipTypeWithAllowedTargetTypes();
        if (relationshipTypeId != null)
        {
            String notAllowdTargetTypeId = searchAndAssertNotAllowedSourceForRelationshipTypeId(relationshipTypeId);
            assertRelationshipConstraitException(
                    "Relationship Creation with the sourceObjectId’s ObjectType is not in the list of “allowedTargetTypes” specified by the Object-Type definition specified by typeId",
                    null, null, notAllowdTargetTypeId, relationshipTypeId);
        }

        // TODO: “controllablePolicy” is set to FALSE and at least one policy is provided
        // TODO: “controllableACL” is set to FALSE and at least one ACE is provided
        // TODO: at least one of the permissions is used in an ACE provided which is not supported by the repository
    }

    public void testCreatePolicy() throws Exception
    {
        String policyTypeId = getAndAssertPolicyTypeId();
        if (policyTypeId != null)
        {
            String policyId = null;
            try
            {
                policyId = createAndAssertPolicy();
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), policyId, false, null));
        }
        else
        {
            LOGGER.info("testCreatePolicy was skipped: Policy type is not found");
        }
    }

    public void testCreatePolicyConstraintsObservance() throws Exception
    {
        String policyTypeId = getAndAssertPolicyTypeId();
        if (policyTypeId != null)
        {
            try
            {
                createAndAssertPolicy(generateTestPolicyName(), getAndAssertFolderTypeId(), null, getAndAssertRootFolderId());
                fail("No Exception was thrown during Policy creation with typeId is not an Object-Type whose baseType is Policy");
            }
            catch (Exception e)
            {
                assertTrue("Invalid exception was thrown during Policy creation with typeId is not an Object-Type whose baseType is Policy", e instanceof CmisFaultType
                        && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
            }
            CmisTypeDefinitionType typeDef = getAndAssertTypeDefinition(getAndAssertPolicyTypeId());
            CmisPropertyStringDefinitionType propertyDefinition = null;
            for (CmisPropertyStringDefinitionType propDef : typeDef.getPropertyStringDefinition())
            {
                if (null != propDef.getMaxLength() && propDef.getMaxLength().longValue() > 0)
                {
                    propertyDefinition = propDef;
                    break;
                }
            }
            if (null != propertyDefinition)
            {
                StringBuilder largeAppender = new StringBuilder("");
                long boundary = propertyDefinition.getMaxLength().longValue();
                for (long i = 0; i <= (boundary + 5); i++)
                {
                    largeAppender.append("A");
                }
                CmisPropertiesType properties = new CmisPropertiesType();
                properties.setPropertyString(new CmisPropertyString[] { new CmisPropertyString(propertyDefinition.getId(), null, null, null, new String[] { largeAppender
                        .toString() }) });
                if (!propertyDefinition.getId().equals(PROP_NAME))
                {
                    CmisPropertyString cmisPropertyString = new CmisPropertyString();
                    cmisPropertyString.setPropertyDefinitionId(PROP_NAME);
                    cmisPropertyString.setValue(new String[] { generateTestPolicyName() });
                    properties.setPropertyString(new CmisPropertyString[] { cmisPropertyString });
                }
                CmisPropertyId idProperty = new CmisPropertyId();
                idProperty.setPropertyDefinitionId(PROP_OBJECT_TYPE_ID);
                idProperty.setValue(new String[] { getAndAssertPolicyTypeId() });
                properties.setPropertyId(new CmisPropertyId[] { idProperty });

                try
                {
                    createAndAssertPolicy(null, null, properties, getAndAssertRootFolderId());
                    fail("No Exception was thrown during Policy creation with '" + propertyDefinition.getId() + "' value length greater than MAX length");
                }
                catch (Exception e)
                {
                    assertTrue("Invalid exception was thrown during Policy creation with '" + propertyDefinition.getId() + "' value length greater than MAX length",
                            e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
                }
            }

            String customFolderTypeId = searchAndAssertFolderFromNotBaseType();
            CmisPropertiesType properties = new CmisPropertiesType();
            properties
                    .setPropertyId(new CmisPropertyId[] { new CmisPropertyId(PROP_ALLOWED_CHILD_OBJECT_TYPE_IDS, null, null, null, new String[] { getAndAssertDocumentTypeId() }) });
            String folderId = createAndAssertFolder(generateTestFolderName(), customFolderTypeId, getAndAssertRootFolderId(), properties);
            try
            {
                createAndAssertPolicy(generateTestPolicyName(), getAndAssertPolicyTypeId(), null, folderId);
                fail("No Exception was thrown during Policy creation with 'typeId' value not in the list of AllowedChildObjectTypeIds of the parent-folder specified by folderId");
            }
            catch (Exception e)
            {
                assertTrue(
                        "Invalid exception was thrown during Policy creation with 'typeId' value not in the list of AllowedChildObjectTypeIds of the parent-folder specified by folderId",
                        e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
            }
        }
        else
        {
            LOGGER.info("testCreatePolicy was skipped: Policy type is not found");
        }

        // TODO: “controllablePolicy” is set to FALSE and at least one policy is provided
        // TODO: “controllableACL” is set to FALSE and at least one ACE is provided
        // TODO: at least one of the permissions is used in an ACE provided which is not supported by the repository
    }

    public void testGetAllowableActions() throws Exception
    {
        GetAllowableActionsResponse response = null;
        try
        {
            LOGGER.info("[ObjectService->getAllowableActions]");
            response = getServicesFactory().getObjectService().getAllowableActions(new GetAllowableActions(getAndAssertRepositoryId(), getAndAssertRootFolderId(), null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull("No allowable actions were returned", response);
        assertNotNull("Action 'getProperties' not defined for an object", response.getAllowableActions().getCanGetProperties());
    }

    public void testGetPropertiesDefault() throws Exception
    {
        GetPropertiesResponse response = null;
        try
        {
            LOGGER.info("[ObjectService->getProperties]");
            response = getServicesFactory().getObjectService().getProperties(new GetProperties(getAndAssertRepositoryId(), getAndAssertRootFolderId(), "*", null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertTrue("No properties were returned", response != null && response.getProperties() != null);
        assertNotNull("No 'Name' property was returned", getStringProperty(response.getProperties(), PROP_NAME));
    }

    public void testGetPropertiesFiltered() throws Exception
    {
        GetPropertiesResponse response = null;
        try
        {
            String filter = PROP_NAME + "," + PROP_OBJECT_ID;
            LOGGER.info("[ObjectService->getProperties]");
            response = getServicesFactory().getObjectService().getProperties(new GetProperties(getAndAssertRepositoryId(), getAndAssertRootFolderId(), filter, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertTrue("No properties were returned", response != null && response.getProperties() != null);

        CmisPropertiesType properties = response.getProperties();

        assertNull("Not expected properties were returned", properties.getPropertyBoolean());
        assertNull("Not expected properties were returned", properties.getPropertyDecimal());
        assertNull("Not expected properties were returned", properties.getPropertyHtml());
        assertNull("Not expected properties were returned", properties.getPropertyInteger());
        assertNull("Not expected properties were returned", properties.getPropertyUri());
        assertNull("Not expected properties were returned", properties.getPropertyDateTime());

        assertNotNull("Expected properties were not returned", properties.getPropertyId());
        assertNotNull("Expected properties were not returned", properties.getPropertyString());

        assertNotNull("Expected property was not returned", getIdProperty(properties, PROP_OBJECT_ID));
        assertNotNull("Expected property was not returned", getStringProperty(properties, PROP_NAME));
    }

    public void testGetObject() throws Exception
    {
        GetObjectResponse response = null;

        try
        {
            LOGGER.info("[ObjectService->getObject]");
            response = getServicesFactory().getObjectService().getObject(
                    new GetObject(getAndAssertRepositoryId(), getAndAssertRootFolderId(), "*", true, EnumIncludeRelationships.none, null, null, null, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertTrue("No properties were returned", response != null && response.getObject() != null && response.getObject().getProperties() != null);
        assertNotNull("No 'Name' property was returned", getStringProperty(response.getObject().getProperties(), PROP_NAME));
    }

    public void testGetObjectIncludeRenditions() throws Exception
    {
        if (EnumCapabilityRendition.read.equals(getAndAssertCapabilities().getCapabilityRenditions()))
        {
            String documentId = createAndAssertDocument();
            List<RenditionData> testRenditions = getTestRenditions(documentId);
            if (testRenditions != null)
            {
                for (RenditionData testRendition : testRenditions)
                {
                    LOGGER.info("[ObjectService->getObject]");
                    GetObjectResponse response = getServicesFactory().getObjectService()
                            .getObject(
                                    new GetObject(getAndAssertRepositoryId(), documentId, PROP_OBJECT_ID, false, EnumIncludeRelationships.none, testRendition.getFilter(), null,
                                            null, null));
                    assertTrue("Response is empty", response != null && response.getObject() != null);
                    assertRenditions(response.getObject(), testRendition.getFilter(), testRendition.getExpectedKinds(), testRendition.getExpectedMimetypes());
                }
            }
            else
            {
                LOGGER.info("testGetObjectIncludeRenditions was skipped: No renditions found for document type");
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null));
        }
        else
        {
            LOGGER.info("testGetObjectIncludeRenditions was skipped: Renditions are not supported");
        }
    }

    public void testGetObjectIncludeAllowableActionsAndRelationships() throws Exception
    {
        GetObjectResponse response = null;

        try
        {
            LOGGER.info("[ObjectService->getObject]");
            response = getServicesFactory().getObjectService().getObject(
                    new GetObject(getAndAssertRepositoryId(), getAndAssertRootFolderId(), PROP_NAME, true, EnumIncludeRelationships.none, null, null, null, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertTrue(PROPERTIES_NOT_RETURNED_MESSAGE, response != null && response.getObject() != null && response.getObject().getProperties() != null);
        assertNotNull("Allowable actions were not returned", response.getObject().getAllowableActions());
        assertNotNull("No action 'getProperties' was returned", response.getObject().getAllowableActions().getCanGetProperties());

        String sourceId = createRelationshipSourceObject(getAndAssertRootFolderId());
        String relationshipId = createAndAssertRelationship(sourceId, null);
        try
        {
            LOGGER.info("[ObjectService->getObject]");
            response = getServicesFactory().getObjectService().getObject(
                    new GetObject(getAndAssertRepositoryId(), sourceId, PROP_NAME, false, EnumIncludeRelationships.both, null, null, null, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertTrue(PROPERTIES_NOT_RETURNED_MESSAGE, response != null && response.getObject() != null && response.getObject().getProperties() != null);
        assertTrue("Relationships were not returned", response.getObject().getRelationship() != null && response.getObject().getRelationship().length >= 1);

        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), relationshipId, false, null));
    }

    public void testGetObjectByPath() throws Exception
    {
        String folder1Id = null;
        String folder2Id = null;
        try
        {
            String folder1Name = generateTestFolderName("_1");
            String folder2Name = generateTestFolderName("_2");
            folder1Id = createAndAssertFolder(folder1Name, getAndAssertFolderTypeId(), getAndAssertRootFolderId(), null);
            folder2Id = createAndAssertFolder(folder2Name, getAndAssertFolderTypeId(), folder1Id, null);
            assertNotNull("Folder was not created", folder1Id);
            assertNotNull("Folder was not created", folder2Id);

            String pathToFolder1 = "/" + folder1Name;
            String pathToFolder2 = "/" + folder1Name + "/" + folder2Name;

            LOGGER.info("[ObjectService->getFolderByPath]");
            GetObjectByPathResponse response = getServicesFactory().getObjectService().getObjectByPath(
                    new GetObjectByPath(getAndAssertRepositoryId(), pathToFolder1, "*", false, null, null, null, null, null));
            assertTrue("Folder was not found", response != null && response.getObject() != null && response.getObject().getProperties() != null);
            assertEquals("Wrong folder was found", folder1Id, getIdProperty(response.getObject().getProperties(), PROP_OBJECT_ID));

            LOGGER.info("[ObjectService->getFolderByPath]");
            response = getServicesFactory().getObjectService().getObjectByPath(
                    new GetObjectByPath(getAndAssertRepositoryId(), pathToFolder2, "*", false, null, null, null, null, null));
            assertTrue("Folder was not found", response != null && response.getObject() != null && response.getObject().getProperties() != null);
            assertEquals("Wrong folder was found", folder2Id, getIdProperty(response.getObject().getProperties(), PROP_OBJECT_ID));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folder2Id, false, null));
        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folder1Id, false, null));
    }

    public void testGetContentStream() throws Exception
    {
        if (isContentStreamAllowed())
        {
            GetContentStreamResponse response = null;
            String documentId = createAndAssertDocument();
            try
            {
                LOGGER.info("[ObjectService->getContentStream]");
                response = getServicesFactory().getObjectService().getContentStream(new GetContentStream(getAndAssertRepositoryId(), documentId, "", null, null, null));
            }
            catch (Exception e)
            {
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null));
                fail(e.toString());
            }
            assertTrue("No content stream was returned", response != null && response.getContentStream() != null);
            assertTrue("Invalid content stream was returned", Arrays.equals(TEST_CONTENT.getBytes(), response.getContentStream().getStream()));
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null));
        }
        else
        {
            LOGGER.info("testGetContentStream was skipped: Content stream isn't allowed");
        }
    }

    public void testGetContentStreamForRenditions() throws Exception
    {
        if (isContentStreamAllowed())
        {
            if (getAndAssertCapabilities().getCapabilityRenditions().equals(EnumCapabilityRendition.read))
            {
                String documentId = createAndAssertDocument();
                CmisRenditionType[] renditionTypes = null;
                LOGGER.info("[ObjectService->getRenditions]");
                renditionTypes = getServicesFactory().getObjectService().getRenditions(
                        new GetRenditions(getAndAssertRepositoryId(), documentId, "*", BigInteger.valueOf(1), BigInteger.valueOf(0), null));
                CmisObjectType cmisObject = new CmisObjectType();
                cmisObject.setRendition(renditionTypes);
                assertNotNull("No Renditions were returned", renditionTypes);
                assertRenditions(cmisObject, "*", null, null);
                for (CmisRenditionType cmisRendition : renditionTypes)
                {
                    GetContentStreamResponse response = null;
                    try
                    {
                        LOGGER.info("[ObjectService->getContentStream]");
                        response = getServicesFactory().getObjectService().getContentStream(
                                new GetContentStream(getAndAssertRepositoryId(), documentId, cmisRendition.getStreamId(), null, null, null));
                    }
                    catch (Exception e)
                    {
                        LOGGER.info("[ObjectService->deleteObject]");
                        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null));
                        fail(e.toString());
                    }
                    assertTrue("No content stream was returned", response != null && response.getContentStream() != null);
                }
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null));
            }
            else
            {
                LOGGER.info("testGetRenditions was skipped: Renditions aren't supported");
            }

        }
        else
        {
            LOGGER.info("testGetContentStream was skipped: Content stream isn't allowed");
        }
    }

    public void testGetContentStreamPortioned() throws Exception
    {
        if (isContentStreamAllowed())
        {
            GetContentStreamResponse response = null;
            String documentId = createAndAssertDocument();
            byte[] byteContent = TEST_CONTENT.getBytes();
            // firstPortion offset=2, length=7
            byte[] firstPortion = new byte[7];
            System.arraycopy(byteContent, 2, firstPortion, 0, 7);
            // secondPortion offset=content.length-5
            byte[] secondPortion = new byte[6];
            System.arraycopy(byteContent, byteContent.length - 6, secondPortion, 0, 6);
            try
            {
                LOGGER.info("[ObjectService->getContentStream]");
                response = getServicesFactory().getObjectService().getContentStream(
                        new GetContentStream(getAndAssertRepositoryId(), documentId, "", BigInteger.valueOf(0), null, null));
                assertTrue("No content stream was returned", response != null && response.getContentStream() != null);
                assertTrue("Invalid range of content was returned", Arrays.equals(byteContent, response.getContentStream().getStream()));

                response = getServicesFactory().getObjectService().getContentStream(
                        new GetContentStream(getAndAssertRepositoryId(), documentId, "", BigInteger.valueOf(2), BigInteger.valueOf(7), null));
                assertTrue("No content stream was returned", response != null && response.getContentStream() != null);
                assertTrue("Invalid range of content was returned", Arrays.equals(firstPortion, response.getContentStream().getStream()));

                response = getServicesFactory().getObjectService().getContentStream(
                        new GetContentStream(getAndAssertRepositoryId(), documentId, "", BigInteger.valueOf(byteContent.length - 6), BigInteger.valueOf(10), null));
                assertTrue("No content stream was returned", response != null && response.getContentStream() != null);
                assertTrue("Invalid range of content was returned", Arrays.equals(secondPortion, response.getContentStream().getStream()));
            }
            catch (Exception e)
            {
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null));
                fail(e.toString());
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null));
        }
        else
        {
            LOGGER.info("testGetContentStream was skipped: Content stream isn't allowed");
        }
    }

    public void testGetContentStreamConstraintsObservance() throws Exception
    {
        String docTypeWithNoContentAllowed = searchAndAssertDocumentTypeWithNoContentAlowed();
        if (docTypeWithNoContentAllowed != null && !docTypeWithNoContentAllowed.equals(""))
        {
            String documentId = createAndAssertDocument(generateTestFileName(), docTypeWithNoContentAllowed, getAndAssertRootFolderId(), null, null, null);
            try
            {
                LOGGER.info("[ObjectService->getContentStream]");
                getServicesFactory().getObjectService().getContentStream(new GetContentStream(getAndAssertRepositoryId(), documentId, "", null, null, null));
                fail("Either expected 'constraint' Exception, or no Exception at all was thrown during getting content stream for object which does NOT have a content stream");
            }
            catch (Exception e)
            {
                assertException("Trying to get content stream for object which does NOT have a content stream", e, EnumServiceException.constraint);
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        }
    }

    public void testUpdateProperties() throws Exception
    {
        String documentName = generateTestFileName();
        String documentId = createAndAssertDocument(documentName, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, EnumVersioningState.none);

        String documentNameNew = generateTestFileName("_new");
        try
        {
            CmisPropertiesType properties = fillProperties(documentNameNew, null);
            LOGGER.info("[ObjectService->updateProperties]");
            documentId = getServicesFactory().getObjectService().updateProperties(new UpdateProperties(getAndAssertRepositoryId(), documentId, null, properties, null))
                    .getObjectId();
        }
        catch (Exception e)
        {
            fail(e.toString());
        }

        String receivedPropertyName = getStringProperty(documentId, PROP_NAME);
        assertTrue("Properties was not updated", documentNameNew.equals(receivedPropertyName));

        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null));
    }

    public void testMoveObjectDefault() throws Exception
    {
        String documentName = generateTestFileName();
        String documentId = createAndAssertDocument(documentName, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, EnumVersioningState.none);
        String folderId = createAndAssertFolder();
        try
        {
            LOGGER.info("[ObjectService->moveObject]");
            getServicesFactory().getObjectService().moveObject(new MoveObject(getAndAssertRepositoryId(), documentId, folderId, getAndAssertRootFolderId(), null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }

        assertFalse("Object was not removed from source folder", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
        assertTrue("Object was not added to target folder", isDocumentInFolder(documentId, folderId));

        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null));
        LOGGER.info("[ObjectService->deleteObject]");
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folderId, false, null));
    }

    // FIXME: Maybe cd06 specification have missed notion about un-filling in context of this operation (when sourceFolderId is required)
    /*
     * public void testMoveObjectUnfiled() throws Exception { if (getAndAssertCapabilities().isCapabilityUnfiling()) { String folderId = createAndAssertFolder(); String documentId
     * = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), null, null, null, null); try { LOGGER.info("[ObjectService->moveObject]");
     * getServicesFactory().getObjectService().moveObject(new MoveObject(getAndAssertRepositoryId(), documentId, folderId, null, null));
     * fail("No Exception was thrown during moving unfiled object"); } catch (Exception e) { assertTrue("Invalid exception was thrown during moving unfiled object", e instanceof
     * CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.notSupported)); } LOGGER.info("[ObjectService->deleteObject]");
     * getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, false, null)); LOGGER.info("[ObjectService->deleteObject]");
     * getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folderId, false, null)); } else {
     * LOGGER.info("testMoveObjectUnfiled was skipped: Unfiling isn't supported"); } }
     */

    public void testMoveObjectMultiFiled() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            String documentId = createAndAssertDocument();
            String folderId = createAndAssertFolder();
            String folder2Id = createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), folderId, null);
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId, null, null));

            try
            {
                LOGGER.info("[ObjectService->moveObject]");
                getServicesFactory().getObjectService().moveObject(new MoveObject(getAndAssertRepositoryId(), documentId, folder2Id, folderId, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }

            assertFalse("Object was not removed from source folder", isDocumentInFolder(documentId, folderId));
            assertTrue("Object was removed from not source folder", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
            assertTrue("Object was not added to target folder", isDocumentInFolder(documentId, folder2Id));

            LOGGER.info("[ObjectService->deleteTree]");
            getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, true, EnumUnfileObject.delete, true, null));
        }
    }

    // FIXME: It is NECESSARY test 'versionSeries' existent and test tries for requesting properties etc of some object from 'versionSeries'
    // FIXME: It is NECESSARY test 'getPropertiesOfLatestVersion' service's method call after deleting current document
    public void testDeleteObject() throws Exception
    {
        String documentId = createAndAssertDocument();
        try
        {
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }

        assertFalse("Object was not removed", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
    }

    public void testDeleteFolderWithChild() throws Exception
    {
        String folderId = createAndAssertFolder();
        String folder2Id = createAndAssertFolder(generateTestFolderName("1"), getAndAssertFolderTypeId(), folderId, null);
        try
        {
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folderId, false, null));
            fail("No Exception was thrown during deleting folder with child");
        }
        catch (Exception e)
        {
            assertTrue("Invalid exception was thrown during deleting folder with child", e instanceof CmisFaultType);
        }
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folder2Id, false, null));
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), folderId, false, null));
    }

    public void testDeleteMultiFiledObject() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            String documentId = createAndAssertDocument();
            String folderId = createAndAssertFolder();
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId, null, null));
            try
            {
                // TODO works not correct in Alfresco
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertFalse("Object was not removed", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
            assertFalse("Object was not removed", isDocumentInFolder(documentId, folderId));

            LOGGER.info("[ObjectService->deleteTree]");
            getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, true, EnumUnfileObject.delete, true, null));
        }
    }

    public void testDeletePWC() throws Exception
    {
        if (isVersioningAllowed())
        {
            String documentId = createAndAssertDocument();
            LOGGER.info("[VersioningService->checkOut]");
            String checkedOutId = getServicesFactory().getVersioningService().checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null)).getObjectId();
            try
            {
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), checkedOutId, false, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertFalse("Private working copy was not deleted", getBooleanProperty(documentId, PROP_IS_VERSION_SERIES_CHECKED_OUT));
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        }
        else
        {
            LOGGER.info("testDeletePWC was skipped: Versioning isn't supported");
        }
    }

    public void testDeleteTreeDefault() throws Exception
    {
        DeleteTreeResponse response = null;
        String folderId = createAndAssertFolder();
        createAndAssertDocument();
        createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), folderId, null);
        try
        {
            LOGGER.info("[ObjectService->deleteTree]");
            response = getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, true, EnumUnfileObject.delete, false, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }

        assertTrue("Objects tree was not deleted", response == null || response.getFailedToDelete() == null || response.getFailedToDelete().getObjectIds() == null
                || response.getFailedToDelete().getObjectIds().length == 0);
    }

    public void testDeleteTreeUnfileNonfolderObjects() throws Exception
    {
        if (getAndAssertCapabilities().isCapabilityMultifiling())
        {
            DeleteTreeResponse response = null;
            String folderId = createAndAssertFolder();
            String documentId = createAndAssertDocument();
            String folder2Id = createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), folderId, null);
            String document2Id = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), folderId, null, TEST_CONTENT, EnumVersioningState.major);
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId, true, null));
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), document2Id, folder2Id, true, null));
            try
            {
                LOGGER.info("[ObjectService->deleteTree]");
                response = getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, true, EnumUnfileObject.delete, true, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertTrue("Objects tree was not deleted", response == null || response.getFailedToDelete() == null || response.getFailedToDelete().getObjectIds() == null
                    || response.getFailedToDelete().getObjectIds().length == 0);
            assertFalse("Multifiled document was not removed", isDocumentInFolder(documentId, getAndAssertRootFolderId()));

            folderId = createAndAssertFolder();
            documentId = createAndAssertDocument();
            folder2Id = createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), folderId, null);
            document2Id = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), folderId, null, TEST_CONTENT, EnumVersioningState.major);
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId, true, null));
            LOGGER.info("[MultiFilingService->addObjectToFolder]");
            getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), document2Id, folder2Id, true, null));
            try
            {
                LOGGER.info("[ObjectService->deleteTree]");
                response = getServicesFactory().getObjectService().deleteTree(
                        new DeleteTree(getAndAssertRepositoryId(), folderId, true, EnumUnfileObject.deletesinglefiled, false, null));
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertTrue("Objects tree was not deleted", response == null || response.getFailedToDelete() == null || response.getFailedToDelete().getObjectIds() == null
                    || response.getFailedToDelete().getObjectIds().length == 0);
            assertTrue("Multifiled document was removed", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));

            if (getAndAssertCapabilities().isCapabilityUnfiling())
            {
                folderId = createAndAssertFolder();
                documentId = createAndAssertDocument();
                folder2Id = createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), folderId, null);
                document2Id = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), folderId, null, TEST_CONTENT, EnumVersioningState.major);
                LOGGER.info("[MultiFilingService->addObjectToFolder]");
                getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), documentId, folderId, true, null));
                LOGGER.info("[MultiFilingService->addObjectToFolder]");
                getServicesFactory().getMultiFilingServicePort().addObjectToFolder(new AddObjectToFolder(getAndAssertRepositoryId(), document2Id, folder2Id, true, null));
                try
                {
                    LOGGER.info("[ObjectService->deleteTree]");
                    response = getServicesFactory().getObjectService().deleteTree(new DeleteTree(getAndAssertRepositoryId(), folderId, true, EnumUnfileObject.unfile, false, null));
                }
                catch (Exception e)
                {
                    fail(e.toString());
                }
                assertTrue("Objects tree was not deleted", response == null || response.getFailedToDelete() == null || response.getFailedToDelete().getObjectIds() == null
                        || response.getFailedToDelete().getObjectIds().length == 0);
                assertFalse("Multifiled document not unfiled", isDocumentInFolder(documentId, getAndAssertRootFolderId()));
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
                LOGGER.info("[ObjectService->deleteObject]");
                getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), document2Id, true, null));
            }
        }
    }

    public void testDeleteTreeWithAllVersions() throws Exception
    {
        if (isVersioningAllowed())
        {
            assertDeleteTreeAllVersions(true);
        }
        else
        {
            LOGGER.info("testDeleteTreeWithAllVersions was skipped: Versioning isn't supported");
        }
    }

    public void testDeleteTreeWithoutAllVersions() throws Exception
    {
        if (isVersioningAllowed())
        {
            assertDeleteTreeAllVersions(false);
        }
        else
        {
            LOGGER.info("testDeleteTreeWithoutAllVersions was skipped: Versioning isn't supported");
        }
    }

    private void assertDeleteTreeAllVersions(boolean deleteAllVersion) throws Exception
    {
        String folderId = createAndAssertFolder();
        String folder2Id = createAndAssertFolder(generateTestFolderName(), getAndAssertFolderTypeId(), folderId, null);
        String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), folderId, null, TEST_CONTENT, EnumVersioningState.major);
        String document2Id = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), folder2Id, null, TEST_CONTENT, EnumVersioningState.major);

        for (int i = 0; i < 2; ++i)
        {
            VersioningServicePortBindingStub versioningService = getServicesFactory().getVersioningService();
            LOGGER.info("[VersioningService->checkOut]");
            CheckOutResponse checkOutResponse = versioningService.checkOut(new CheckOut(getAndAssertRepositoryId(), documentId, null));
            LOGGER.info("[VersioningService->checkIn]");
            CheckInResponse checkInResponse = versioningService.checkIn(new CheckIn(getAndAssertRepositoryId(), checkOutResponse.getObjectId(), true, new CmisPropertiesType(),
                    new CmisContentStreamType(BigInteger.valueOf(12), "text/plain", generateTestFileName(), "Test content".getBytes(), null), "", null, null, null, null));
            documentId = checkInResponse.getObjectId();
        }
        LOGGER.info("[VersioningService->getAllVersions]");
        CmisObjectType[] response = getServicesFactory().getVersioningService().getAllVersions(new GetAllVersions(getAndAssertRepositoryId(), documentId, null, null, null));
        List<String> versionsIds = new ArrayList<String>();
        for (CmisObjectType cmisObjectType : response)
        {
            if (cmisObjectType.getProperties() != null)
            {
                versionsIds.add(getIdProperty(cmisObjectType.getProperties(), PROP_OBJECT_ID));
            }
        }
        DeleteTreeResponse deleteTreeResponse = null;
        try
        {
            LOGGER.info("[ObjectService->deleteTree]");
            deleteTreeResponse = getServicesFactory().getObjectService().deleteTree(
                    new DeleteTree(getAndAssertRepositoryId(), folderId, deleteAllVersion, EnumUnfileObject.delete, true, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertTrue("Objects tree was not deleted", deleteTreeResponse == null || deleteTreeResponse.getFailedToDelete() == null
                || deleteTreeResponse.getFailedToDelete().getObjectIds() == null || deleteTreeResponse.getFailedToDelete().getObjectIds().length == 0);

        for (String versionId : versionsIds)
        {
            if (deleteAllVersion)
            {
                assertObjectNotExist(versionId);
            }
            else
            {
                assertObjectExist(versionId);
            }
        }
        assertObjectNotExist(folderId);
        assertObjectNotExist(folder2Id);
        assertObjectNotExist(documentId);
        assertObjectNotExist(document2Id);
    }

    private void assertObjectExist(String objectId) throws Exception
    {
        GetPropertiesResponse propResponse = null;
        try
        {
            LOGGER.info("[ObjectService->getProperties]");
            propResponse = getServicesFactory().getObjectService().getProperties(new GetProperties(getAndAssertRepositoryId(), objectId, "*", null));
            assertTrue("Version was removed while flag deleteAllVersions is FALSE", propResponse != null && propResponse.getProperties() != null
                    && propResponse.getProperties().getPropertyId().length > 0);
        }
        catch (Exception e)
        {
        }
        assertTrue("Object does not exist", propResponse != null && propResponse.getProperties() != null && propResponse.getProperties().getPropertyId().length > 0);
    }

    private void assertObjectNotExist(String objectId) throws Exception
    {
        GetPropertiesResponse propResponse = null;
        try
        {
            LOGGER.info("[ObjectService->getProperties]");
            propResponse = getServicesFactory().getObjectService().getProperties(new GetProperties(getAndAssertRepositoryId(), objectId, "*", null));
            assertTrue("Version was removed while flag deleteAllVersions is FALSE", propResponse != null && propResponse.getProperties() != null
                    && propResponse.getProperties().getPropertyId().length > 0);
        }
        catch (Exception e)
        {
        }
        assertTrue("Object exists", propResponse == null || propResponse.getProperties() == null);
    }

    public void testSetContentStream() throws Exception
    {
        if (isContentStreamAllowed())
        {
            String documentName = generateTestFileName();
            String documentId = createAndAssertDocument(documentName, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, EnumVersioningState.none);
            String newTestCOntent = TEST_CONTENT + System.currentTimeMillis();
            try
            {
                LOGGER.info("[ObjectService->setContentStream]");
                documentId = getServicesFactory().getObjectService().setContentStream(
                        new SetContentStream(getAndAssertRepositoryId(), documentId, true, null, new CmisContentStreamType(BigInteger.valueOf(newTestCOntent.length()),
                                MIMETYPE_TEXT_PLAIN, documentName, newTestCOntent.getBytes(ENCODING), null), null)).getObjectId();
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            assertTrue("Content stream was not updated", Arrays.equals(newTestCOntent.getBytes(), getServicesFactory().getObjectService().getContentStream(
                    new GetContentStream(getAndAssertRepositoryId(), documentId, "", null, null, null)).getContentStream().getStream()));
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        }
        else
        {
            LOGGER.info("testSetContentStream was skipped: Content stream isn't allowed");
        }
    }

    public void testSetContentStreamOverwriteFlag() throws Exception
    {
        if (isContentStreamAllowed())
        {
            String documentName = generateTestFileName();
            String documentId = createAndAssertDocument(documentName, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, EnumVersioningState.none);
            String newTestCOntent = TEST_CONTENT + System.currentTimeMillis();
            try
            {
                LOGGER.info("[ObjectService->setContentStream]");
                documentId = getServicesFactory().getObjectService().setContentStream(
                        new SetContentStream(getAndAssertRepositoryId(), documentId, false, null, new CmisContentStreamType(BigInteger.valueOf(newTestCOntent.length()),
                                MIMETYPE_TEXT_PLAIN, documentName, newTestCOntent.getBytes(ENCODING), null), null)).getObjectId();
                fail("No Exception was thrown during setting content stream while input parameter overwriteFlag is FALSE and the Object already has a content-stream");
            }
            catch (Exception e)
            {
                assertTrue("Invalid exception was thrown during setting content stream while input parameter overwriteFlag is FALSE and the Object already has a content-stream",
                        e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.contentAlreadyExists));
            }
            try
            {
                LOGGER.info("[ObjectService->setContentStream]");
                documentId = getServicesFactory().getObjectService().setContentStream(
                        new SetContentStream(getAndAssertRepositoryId(), documentId, true, null, new CmisContentStreamType(BigInteger.valueOf(newTestCOntent.length()),
                                MIMETYPE_TEXT_PLAIN, documentName, newTestCOntent.getBytes(ENCODING), null), null)).getObjectId();
            }
            catch (Exception e)
            {
                fail(e.toString());
            }

            assertTrue("Content stream was not updated", Arrays.equals(newTestCOntent.getBytes(), getServicesFactory().getObjectService().getContentStream(
                    new GetContentStream(getAndAssertRepositoryId(), documentId, "", null, null, null)).getContentStream().getStream()));
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        }
        else
        {
            LOGGER.info("testSetContentStreamOverwriteFlag was skipped: Content stream isn't allowed");
        }
    }

    public void testSetContentStreamNotAllowed() throws Exception
    {
        String documentTypeId = searchAndAssertDocumentTypeWithContentNotAllowed();

        if (documentTypeId != null)
        {
            String documentId = createAndAssertDocument(generateTestFileName(), documentTypeId, getAndAssertRootFolderId(), null, null, EnumVersioningState.major);
            try
            {
                LOGGER.info("[ObjectService->setContentStream]");
                getServicesFactory().getObjectService().setContentStream(
                        new SetContentStream(getAndAssertRepositoryId(), documentId, true, null, new CmisContentStreamType(BigInteger.valueOf(TEST_CONTENT.length()),
                                MIMETYPE_TEXT_PLAIN, generateTestFileName(), TEST_CONTENT.getBytes(ENCODING), null), null)).getObjectId();
                fail("No Exception was thrown during setting content stream while Object-Type definition specified by the typeId parameter’s “contentStreamAllowed” attribute is set to “not allowed”");
            }
            catch (Exception e)
            {
                // TODO according to specification 2 types of exceptions SHOULD be thrown
                assertTrue(
                        "Invalid exception was thrown during setting content stream while Object-Type definition specified by the typeId parameter’s “contentStreamAllowed” attribute is set to “not allowed”",
                        e instanceof CmisFaultType
                                && (((CmisFaultType) e).getType().equals(EnumServiceException.constraint) || ((CmisFaultType) e).getType().equals(
                                        EnumServiceException.streamNotSupported)));
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        }
    }

    public void testDeleteContentStream() throws Exception
    {
        if (isContentStreamAllowed())
        {
            String documentName = generateTestFileName();
            String documentId = createAndAssertDocument(documentName, getAndAssertDocumentTypeId(), getAndAssertRootFolderId(), null, TEST_CONTENT, EnumVersioningState.none);
            try
            {
                LOGGER.info("[ObjectService->deleteContentStream]");
                DeleteContentStreamResponse response = getServicesFactory().getObjectService().deleteContentStream(
                        new DeleteContentStream(getAndAssertRepositoryId(), documentId, null, null));
                assertNotNull("DeleteContentStream response is NULL", response);
                assertNotNull("DeleteContentStream response is empty", response.getObjectId());
                documentId = response.getObjectId();
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            try
            {
                GetContentStreamResponse contentStreamResponse = getServicesFactory().getObjectService().getContentStream(
                        new GetContentStream(getAndAssertRepositoryId(), documentId, "", null, null, null));
                assertTrue("Content stream was not deleted", contentStreamResponse == null || contentStreamResponse.getContentStream() == null
                        || contentStreamResponse.getContentStream().getStream() == null);
            }
            catch (Exception e)
            {
                assertTrue("Invalid exception was thrown", e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        }
        else
        {
            LOGGER.info("testDeleteContentStream was skipped: Content stream isn't allowed");
        }
    }

    public void testDeleteContentStreamRequired() throws Exception
    {
        String documentTypeId = searchAndAssertDocumentTypeWithContentRequired();
        if (documentTypeId != null)
        {
            String documentId = createAndAssertDocument(generateTestFileName(), documentTypeId, getAndAssertRootFolderId(), null, TEST_CONTENT, EnumVersioningState.major);
            try
            {
                LOGGER.info("[ObjectService->deleteContentStream]");
                getServicesFactory().getObjectService().deleteContentStream(new DeleteContentStream(getAndAssertRepositoryId(), documentId, null, null));
                fail("No Exception was thrown  during deleting content stream while Object’s Object-Type definition “contentStreamAllowed” attribute is set to “required”");
            }
            catch (Exception e)
            {
                assertTrue(
                        "Invalid exception was thrown during deleting content stream while Object’s Object-Type definition “contentStreamAllowed” attribute is set to “required”",
                        e instanceof CmisFaultType && ((CmisFaultType) e).getType().equals(EnumServiceException.constraint));
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        }
    }

    public void testGetRenditions() throws Exception
    {
        if (getAndAssertCapabilities().getCapabilityRenditions().equals(EnumCapabilityRendition.read))
        {
            String documentId = createAndAssertDocument();
            CmisRenditionType[] renditionTypes = null;
            try
            {
                LOGGER.info("[ObjectService->getRenditions]");
                renditionTypes = getServicesFactory().getObjectService().getRenditions(
                        new GetRenditions(getAndAssertRepositoryId(), documentId, "*", BigInteger.valueOf(1), BigInteger.valueOf(0), null));
                assertNotNull("No Renditions were returned", renditionTypes);
                CmisObjectType cmisObject = new CmisObjectType();
                cmisObject.setRendition(renditionTypes);
                assertRenditions(cmisObject, "*", null, null);
            }
            catch (Exception e)
            {
                fail(e.toString());
            }
            LOGGER.info("[ObjectService->deleteObject]");
            getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        }
        else
        {
            LOGGER.info("testGetRenditions was skipped: Renditions aren't supported");
        }
    }
}
