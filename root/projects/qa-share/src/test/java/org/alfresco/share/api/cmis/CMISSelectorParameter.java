/**
 * 
 */
package org.alfresco.share.api.cmis;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.CMISNode;
import org.alfresco.rest.api.tests.client.data.ContentData;
import org.alfresco.rest.api.tests.client.data.FolderNode;
import org.alfresco.share.enums.CMISBinding;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CmisUtils;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Policy;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;

import static org.testng.Assert.*;

/**
 * @author abharade
 * 
 */
public abstract class CMISSelectorParameter extends CmisUtils
{
    private static Log logger = LogFactory.getLog(CMISSelectorParameter.class);

    protected String testName;
    protected String testUser;
    protected String siteName;
    protected String fileName;
    protected String fileName1;
    protected String folderName;
    protected String folderRef;
    protected String fileNameRef;
    protected String fileName1Ref;
    protected final String fileNameContent = "This is main file content";

    protected final String folderTypeId = "cmis:folder";
    protected final String docTypeId = "cmis:document";
    protected final String secondaryTypeId = "cmis:secondary";
    protected final String relationshipTypeId = "cmis:relationship";

    protected String FILE_5MB = "FILE1_5MB-FILE";

    protected CMISBinding binding;

    /**
     * @throws Exception
     * @param testName
     */
    protected void createTestData(String testName) throws Exception
    {
        testUser = getUserNameFreeDomain(testName);

        siteName = getSiteName(testName) + System.currentTimeMillis();

        fileName = getFileName(testName) + System.currentTimeMillis();
        fileName1 = getFileName(testName + "1") + System.currentTimeMillis();
        folderName = getFolderName(testName) + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser);

        ShareUser.createSite(drone, getSiteShortname(siteName), SITE_VISIBILITY_PUBLIC, true);
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
        folderRef = ShareUser.getGuid(drone, folderName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName, "", fileNameContent });
        fileNameRef = ShareUser.getGuid(drone, fileName);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, folderName, fileNameContent });
        fileName1Ref = ShareUser.getGuid(drone, fileName1);
    }

    protected void selectorTypeChildren(String thisTestName)
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        ItemIterable<ObjectType> typeChildren = cmisSession.getTypeChildren(folderTypeId,true);
        assertNotNull(typeChildren);
        assertTrue(typeChildren.iterator().hasNext(), "Got children:" + typeChildren);
    }

    protected void selectorTypeDefinition(String thisTestName)
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);

        ObjectType typeDefinition = cmisSession.getTypeDefinition(docTypeId);
        assertTrue(typeDefinition.getDisplayName().equals("Document"), "Got display name:" + typeDefinition.getDisplayName());

        typeDefinition = cmisSession.getTypeDefinition(folderTypeId);
        assertTrue(typeDefinition.getDisplayName().equals("Folder"), "Got display name:" + typeDefinition.getDisplayName());

        typeDefinition = cmisSession.getTypeDefinition(secondaryTypeId);
        assertTrue(typeDefinition.getDisplayName().equals("Secondary Type"), "Got display name:" + typeDefinition.getDisplayName());
    }

    protected void selectorTypeDescendants(String thisTestName)
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);

        List<Tree<ObjectType>> typeDescendants = cmisSession.getTypeDescendants(docTypeId, 1, true);
        assertTrue(typeDescendants.get(0).getItem().getDisplayName().equals("Calendar Event"),typeDescendants.get(0).getItem().getDisplayName());

        typeDescendants = cmisSession.getTypeDescendants(folderTypeId, 1, true);
        assertTrue(typeDescendants.size() > 0);
        assertTrue(typeDescendants.get(0).getItem().getDisplayName().equals("System Folder"));

        typeDescendants = cmisSession.getTypeDescendants(secondaryTypeId, 1, true);
        assertTrue(typeDescendants.get(0).getItem().getDisplayName().equals("OAuth2 Authenticated Delivery Channel"));
        assertTrue(typeDescendants.size() > 0);

        typeDescendants = cmisSession.getTypeDescendants(relationshipTypeId, 1, true);
        assertTrue(typeDescendants.get(0).getItem().getDisplayName().equals("R:pub:source"));
        assertTrue(typeDescendants.size() > 0);

    }

    protected void selectorRepoInfo()
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        RepositoryInfo repositoryInfo = cmisSession.getCMISSession().getRepositoryInfo();
        assertTrue(repositoryInfo.getId().equals(DOMAIN));
    }

    protected void selectorRepoURL(String thisTestName)
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        cmisSession.getCMISSession().getRepositoryInfo();
        /*
         * Found no way to implement repositoryURL test
         */
    }

    protected void rootFolderURL(String thisTestName)
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        Folder rootFolder = cmisSession.getCMISSession().getRootFolder();
        assertTrue(rootFolder.isRootFolder());
    }

    protected void selectorVersions(CMISBinding binding, String userName, String thisTestName)
    {
        String fileName = getFileName(thisTestName);
        String nodeRef = getNodeRef(binding, userName, DOMAIN, siteName, "", fileName);
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, userName, DOMAIN);
        List<Document> allVersions = cmisSession.getAllVersions(nodeRef);
        Assert.assertEquals(allVersions.size(), 3, "Verifying the versions");
    }

    protected void objectsUsingPath(String thisTestName)
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        CmisObject objectByPath = cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary");
        assertTrue(objectByPath.getLastModifiedBy().equals(testUser));
    }

    protected void objectsUsingObjectId(String thisTestName)
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        CmisObject objectByPath = cmisSession.getObject(folderRef);
        assertEquals(objectByPath.getLastModifiedBy(), testUser);
        assertEquals(objectByPath.getName(), folderName);
    }

    protected void selectChildren(String thisTestName)
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        FolderNode children = cmisSession.getChildren(folderRef, 0, 100);
        assertTrue(children.getDocumentNodes().get(fileName1Ref).getGuid().contains(fileName1Ref), "" + children.getGuid() + " should contain " + fileName1Ref);
    }

    protected void compactJSONResponse(String thisTestName) throws IOException
    {
        String reqURL = getAPIURL(drone) + DOMAIN + "/public/cmis/versions/1.1/browser/root";

        Map<String, String> params = new HashMap<String, String>();
        params.put("cmisselector", "typeChildren");
        params.put("succinct", "true");

        HttpResponse httpResponse = getHttpResponse(reqURL, params);
        assertTrue(httpResponse.getStatusCode() == 200, httpResponse.getResponse().toString());
    }

    protected void descendants(String thisTestName)
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        FolderNode children = cmisSession.getDescendants(folderRef, 1);
        assertTrue(children.getDocumentNodes().toString().contains(fileName1Ref), "" + children.getDocumentNodes().keySet() + " should contain " + folderRef);
    }

    protected void checkedOut(String thisTestName) throws Exception
    {
        String thisFileName = getFileName(thisTestName);
        ShareUser.login(drone, testUser);
        ShareUser.openSiteDashboard(drone, siteName);
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, new String[] { thisFileName, folderName, thisFileName });
        String thisFileNameRef = ShareUser.getGuid(drone, thisFileName);

        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        cmisSession.checkoutObject(thisFileNameRef);
        Folder folder = (Folder) cmisSession.getObject(folderRef);
        ItemIterable<Document> checkedOutDocs = folder.getCheckedOutDocs();
        Document doc = checkedOutDocs.iterator().next();
        assertTrue(doc.getName().contains(thisFileName));
    }

    protected void cmisSelectorParents(String thisTestName) throws Exception
    {
        String thisFolderName = getFolderName(thisTestName);
        ShareUser.openSiteDashboard(drone, siteName);
        ShareUser.createFolderInFolder(drone, thisFolderName, thisFolderName, folderName);
        String thisFolderNameRef = ShareUser.getGuid(drone, thisFolderName);

        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        List<Folder> objectParents = cmisSession.getObjectParents(fileName1Ref);

        assertEquals(objectParents.get(0).getName(), folderName);

        objectParents = cmisSession.getObjectParents(thisFolderNameRef);

        assertEquals(objectParents.get(0).getName(), folderName);
    }

    protected void allowableActions(String thisTestName) throws Exception
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);

        AllowableActions allowableActions = cmisSession.getAllowableActions(folderRef);
        assertFalse(allowableActions.getAllowableActions().isEmpty());

        allowableActions = cmisSession.getAllowableActions(fileNameRef);
        assertFalse(allowableActions.getAllowableActions().isEmpty());
    }

    protected void objectProperties(String thisTestName) throws Exception
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);
        Folder folder = (Folder) cmisSession.getObject(folderRef);
        List<Property<?>> properties = folder.getProperties();
        assertNotNull(properties);
        Document doc = (Document) cmisSession.getObject(fileName1Ref);
        properties = doc.getProperties();
        assertNotNull(properties);
    }


    protected void renditionsSelector(String thisTestName) throws Exception
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);

        Document doc = (Document) cmisSession.getObject(fileName1Ref);
        List<Rendition> renditions = doc.getRenditions();
        assertNotNull(doc);
        assertNull(renditions);
    }

    protected void selectorContent(String thisTestName) throws Exception
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, testUser, DOMAIN);

        ContentData content = cmisSession.getContent(fileName1Ref);
        assertFalse(byteToString(content.getBytes()).isEmpty(), "Go bytes:" + byteToString(content.getBytes()));
    }

    protected HttpResponse getHttpResponse(String resourceUrl, Map<String, String> params) throws IOException
    {
        String url = getAPIURL(drone) + DOMAIN + resourceUrl;
        System.out.println("URL: " + url);

        System.out.println("getAuthDetails(testUser)[0]: " + getAuthDetails(testUser)[0]);
        System.out.println("getAuthDetails(testUser)[1]: " + getAuthDetails(testUser)[1]);
        publicApiClient.setRequestContext(new RequestContext(DOMAIN, getAuthDetails(testUser)[0], getAuthDetails(testUser)[1]));

        return publicApiClient.get(url, params);
    }

    public String byteToString(byte[] bytes)
    {
        String byteString = "";

        for(int i = 0; i < bytes.length; i++)
        {
            byteString += (char)bytes[i];
        }

        return byteString;
    }

    protected void selectorRelationship(CMISBinding binding, String userName, String siteName, String fileName1, String fileName2)
    {
        String objectID1 = getNodeRef(binding, userName, DOMAIN, siteName, "", fileName1);
        String objectID2 = getNodeRef(binding, userName, DOMAIN, siteName, "", fileName2);

        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, userName, DOMAIN);

        OperationContext operationContext = new OperationContextImpl(null, false, false, false, IncludeRelationships.BOTH, Collections.singleton("cmis:none"), true, null, true, 100);

        List<Relationship> relationships = cmisSession.getCMISSession().getObject(objectID1, operationContext).getRelationships();

        Assert.assertEquals(relationships.size(), 1, "Verifying number of relationships found");
        Assert.assertTrue(relationships.get(0).getSource().getId().contains(objectID1.split("/")[3]), "Verifying Source");
        Assert.assertTrue(relationships.get(0).getTarget().getId().contains(objectID2.split("/")[3]), "Verifying target");
    }

    protected void selectorPolicies(CMISBinding binding, String userName, String siteName, String fileName, String folderName)
    {
        String objectID = getObjectID(binding, userName, DOMAIN, siteName, folderName, fileName);
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, userName, DOMAIN);

        OperationContext operationContext = new OperationContextImpl(null, false, false, true, IncludeRelationships.BOTH, Collections.singleton("cmis:none"), true, null, true, 100);

        CmisObject cmisObject = cmisSession.getCMISSession().getObject(objectID, operationContext);
        List<Policy> policies = cmisObject.getPolicies();

        Assert.assertNotNull(cmisObject, "Verifying Cmis Object is not null");
        assertNull(policies, "Verifying No policies were returned");
    }

    protected void selectorACL(CMISBinding binding, String userName, String siteName, String fileName, String folderName)
    {
        String objectID = getObjectID(binding, userName, DOMAIN, siteName, folderName, fileName);
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, userName, DOMAIN);

        OperationContext operationContext = new OperationContextImpl(null, true, false, false, IncludeRelationships.BOTH, Collections.singleton("cmis:none"), true, null, true, 100);

        Acl acl = cmisSession.getCMISSession().getObject(objectID, operationContext).getAcl();

        Assert.assertEquals(acl.getAces().size(), 5, "Verifying default number of Aces");
        Assert.assertNotNull(acl);
    }

    protected void selectorNotSpecified(CMISBinding binding, String userName, String siteName, String fileName, String folderName)
    {
        String folderObjectID = getObjectID(binding, userName, DOMAIN, siteName, "", folderName);
        String fileObjectID = getObjectID(binding, userName, DOMAIN, siteName, folderName, fileName);

        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, userName, DOMAIN);

        Folder folder = (Folder) cmisSession.getObject(folderObjectID);
        ItemIterable<CmisObject> children = folder.getChildren();

        Assert.assertEquals(children.getTotalNumItems(), 2, "Verifying number of children");

        OperationContext operationContext = new OperationContextImpl(null, true, false, true, IncludeRelationships.BOTH, Collections.singleton("cmis:none"), true, null, true, 100);

        CmisObject cmisObject = cmisSession.getCMISSession().getObject(fileObjectID, operationContext);

        List<Relationship> relationships = cmisObject.getRelationships();

        String relationshipID = relationships.get(0).getId();

        CmisObject relationshipObject = cmisSession.getObject(relationshipID);

        Assert.assertNotNull(relationshipObject, "Verifying CmisObject is not null upon using \"" + relationshipID + "\" as objectID");

        List<Policy> policies = cmisObject.getPolicies();
        assertNull(policies, "Verifying No policies exists");
    }

    protected void selectorQuery(CMISBinding binding, String userName)
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(binding, userName, DOMAIN);

        List<CMISNode> query = cmisSession.query("SELECT * FROM cmis:document", false, 0, 10);

        Assert.assertEquals(query.size(), 10, "Verifying result size");
    }
}
