/**
 * 
 */
package org.alfresco.share.api.cmis;

import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.share.enums.CMISBinding;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CmisUtils;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * @author abharade
 * 
 */
public abstract class CMISAppendTest extends CmisUtils
{

    protected String testName;
    protected String testUser;
    protected String siteName;
    protected String fileName;
    protected String fileName1;
    protected String folderName;
    protected String folderRef;
    protected String fileNameRef;
    protected String targetNodeRef;
    protected final String fileNameContent = "This is main file content";

    protected String
            FILE_5MB = "FILE1_5MB-FILE";

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
        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, "", fileNameContent });
        targetNodeRef = ShareUser.getGuid(drone, fileName1);
    }

    /**
     * 
     * @param nodeRef
     * @return
     */
    CmisObject getObject(String nodeRef)
    {
        return getObject(binding, testUser, DOMAIN, nodeRef);
    }

    protected Document appendTest(WebDrone drone, String content, boolean isLastChunk, String thisFileName) throws IOException
    {
        ShareUser.openDocumentLibrary(drone);
        String thisFileNameRef = ShareUser.getGuid(drone, thisFileName);
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, thisFileName);

        String expectedVersion = documentDetailsPage.getDocumentVersion();
        Document doc1 = (Document) getObject(thisFileNameRef);
        long documentSize = doc1.getContentStreamLength();
        appendContent(binding, testUser, DOMAIN, thisFileNameRef, content, isLastChunk);

        doc1 = (Document) getObject(thisFileNameRef);
        assertTrue(streamToString(doc1.getContentStream()).contains(content));

        ShareUser.openDocumentLibrary(drone);
        documentDetailsPage = ShareUser.openDocumentDetailPage(drone, thisFileName);

        String stringVersion = documentDetailsPage.getDocumentVersion();
        double docVersion = 0;
        double versionBefore = 0;
        try {
            docVersion = Double.parseDouble(stringVersion);
            versionBefore = Double.parseDouble(expectedVersion);
        } catch (NumberFormatException e) {
            fail("The version before - " + expectedVersion + "or the later version - " + stringVersion + " was not received properly.");
        }
        if (isLastChunk)
        {
            assertTrue(docVersion > versionBefore,
                    stringVersion + " should be greater than " + versionBefore);
        }
        else
        {
            assertTrue(docVersion == versionBefore,
                    stringVersion + " should be greater than " + versionBefore);
        }
        assertTrue(doc1.getContentStreamLength() > documentSize, doc1.getContentStreamLength() + " should be more than " + (documentSize));
        return doc1;
    }

    protected void appendSeveralChunksTest(WebDrone drone, String testFileName) throws Exception
    {
        ShareUser.uploadFileInFolder(drone, new String[] { testFileName, DOCLIB, "The First part." });


        String secondPart = "The second part.";
        appendTest(drone, secondPart, false, testFileName);
        String thirdPart = "The third part.";
        Document doc = appendTest(drone, thirdPart, true, testFileName);


        assertTrue(streamToString(doc.getContentStream()).contains(secondPart));
        assertTrue(streamToString(doc.getContentStream()).contains(thirdPart));
        /*ShareUser.openDocumentLibrary(drone);
        FileDirectoryInfo thisRow = ShareUserSitePage.getFileDirectoryInfo(drone, testFileName);
        String mainWindow = drone.getWindowHandle();
        thisRow.selectViewInBrowser();

        String htmlSource = ((WebDroneImpl) drone).getDriver().getPageSource();
        assertTrue(htmlSource.contains(secondPart), htmlSource + "should contain " + secondPart);
        assertTrue(htmlSource.contains(thirdPart), htmlSource + "should contain " + thirdPart);

        drone.closeTab();
        drone.switchToWindow(mainWindow);*/
    }

    protected void appendLargeChunksTest(WebDrone drone, String testFileName) throws Exception
    {
        ShareUser.uploadFileInFolder(drone, new String[] { testFileName, DOCLIB });

        String content5MB = FileUtils.readFileToString(new File(DATA_FOLDER + testFileName));
        appendTest(drone, content5MB, false, testFileName);
        Document doc1 = (Document) appendTest(drone, content5MB, true, testFileName);

        assertTrue(doc1.getContentStreamLength() > 9000, doc1.getContentStreamLength() + " should be more than 9000 bytes");
    }

    /**
     * Removes " bytes" and returns number.
     * 
     * @param documentSize
     * @return
     */
    private double getNumericalSize(String documentSize)
    {
        return Double.parseDouble(StringUtils.substringBefore(documentSize, " bytes"));
    }

    /**
     * Returns String content from Stream.
     * 
     * @param stream
     * @return
     * @throws IOException
     */
    static String streamToString(ContentStream stream) throws IOException
    {
        InputStream in = stream.getStream();
        StringWriter writer = new StringWriter();
        IOUtils.copy(in, writer, "UTF-8");
        String content = writer.toString();
        return content;
    }

    protected void createDocTest(String thisTestName) throws Exception
    {
        Folder parentFolder = (Folder) getObject(folderRef);

        Map<String, String> properties = new HashMap<String, String>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, thisTestName);

        Document doc = parentFolder.createDocument(properties, streamContent("New content", MimetypeMap.MIMETYPE_TEXT_PLAIN), VersioningState.MAJOR);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, folderName);
        assertTrue(documentLibraryPage.isFileVisible(thisTestName), "Filename - '" + thisTestName + "' should be visible in:" + documentLibraryPage.getFiles());
        String versionInfo = ShareUserSitePage.getFileDirectoryInfo(drone, thisTestName).getVersionInfo();
        assertTrue(versionInfo.equals("1.0"), "Version should be 1.0 but was found to be:" + versionInfo);
    }
}
