package org.alfresco.share.util;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPopup;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.InlineEditPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.MimeType;
import org.alfresco.po.share.site.document.SortField;
import org.alfresco.po.share.site.document.TagPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.SkipException;

public class ShareUserSitePage extends AbstractTests
{
    private static Log logger = LogFactory.getLog(ShareUser.class);
    protected static final String DEFAULT_FOLDER = "Documents";

    public ShareUserSitePage()
    {
        if (logger.isTraceEnabled())
        {
            logger.debug(this.getClass().getSimpleName() + " instantiated");
        }

    }

    /**
     * Assumes a specific Site is open Opens the Document Library Page and navigates to the Path specified.
     * 
     * @param driver WebDrone Instance
     * @param folderPath: String folder path relative to DocumentLibrary e.g. DOC_LIB + file.seperator + folderName1
     * @throws SkipException if error in this API
     */
    public static DocumentLibraryPage navigateToFolder(WebDrone driver, String folderPath) throws Exception
    {
        DocumentLibraryPage docPage;

        try
        {
            if (folderPath == null)
            {
                throw new UnsupportedOperationException("Incorrect FolderPath: Null");
            }                      

            // Navigation logic
            if (folderPath.startsWith(REPO))
            {
                // Assume RepositoryPage is open
                docPage = (DocumentLibraryPage) ShareUser.getSharePage(driver);
            }
            else
            {
                // TODO: Consider taking the step to openDocumentLibrary out
                // Open DocumentLibraryPage for the Open Site
                docPage = ShareUser.openDocumentLibrary(driver);
            }

            // Resolve folderPath, considering diff treatment for non-windows OS
            logger.info(folderPath);
            String[] path = folderPath.split(Pattern.quote(SLASH));

            // Navigate to the parent Folder where the file needs to be uploaded
            for (int i = 0; i < path.length; i++)
            {
                if (path[i].isEmpty())
                {
                    // Ignore, Continue to the next;
                }
                else
                {
                    if ((i == 0) && (path[i].equalsIgnoreCase(REPO) || path[i].equalsIgnoreCase(DOCLIB)))
                    {
                        // Repo or Doclib is already open
                    }
                    else
                    {
                        logger.info("Navigating to Folder: " + path[i]);
                        docPage.selectFolder(path[i]).render();
                    }
                 }	
            }
            logger.info("Selected Folder:" + folderPath);
        }
        catch (Exception e)
        {
            throw new SkipException("Skip test. Error in navigateToFolder: " + e.getMessage());
        }

        return docPage;
    }



    /**
     * Assumes User is logged in and a specific Site's Doclib is open, Parent Folder is pre-selected.
     * 
     * @param file File Object for the file in reference
     * @return DocumentLibraryPage
     * @throws SkipException if error in this API
     */
    public static DocumentLibraryPage uploadFile(WebDrone driver, File file) throws Exception
    {
        DocumentLibraryPage docPage;
        checkIfDriverNull(driver);
        docPage = driver.getCurrentPage().render(refreshDuration);
        try
        {
            // Upload File
            UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
            docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
            docPage.setContentName(file.getName());
            logger.info("File Uploaded:" + file.getCanonicalPath());
        }
        catch (Exception e)
        {
            throw new SkipException("Skip test. Error in UploadFile: " + e);
        }

        return docPage.render();
    }

    /**
     * Creates a new folder at the Path specified, Starting from the Document Library Page.
     * Assumes User is logged in and a specific Site is open.
     * 
     * @param driver WebDrone Instance
     * @param folderName String Name of the folder to be created
     * @param folderDesc String Description of the folder to be created
     * @return DocumentLibraryPage
     */
    public static DocumentLibraryPage createFolder(WebDrone driver, String folderName, String folderDesc)
    {
        return createFolder(driver, folderName, null, folderDesc);
    }

    /**
     * Creates a new folder at the Path specified, Starting from the Document Library Page.
     * Assumes User is logged in and a specific Site is open.
     * 
     * @param driver WebDrone Instance
     * @param folderName String Name of the folder to be created
     * @param folderTitle String Title of the folder to be created
     * @param folderDesc String Description of the folder to be created
     * @return DocumentLibraryPage
     */
    public static DocumentLibraryPage createFolder(WebDrone driver, String folderName, String folderTitle, String folderDesc)
    {
        DocumentLibraryPage docPage = null;

        // Open Document Library
        SharePage thisPage = ShareUser.getSharePage(driver);

        if (!(thisPage instanceof RepositoryPage) && (!(thisPage instanceof DocumentLibraryPage)))
        {
            docPage = ShareUser.openDocumentLibrary(driver);
        }
        else
        {
            docPage = (DocumentLibraryPage) thisPage;
        }

        NewFolderPage newFolderPage = docPage.getNavigation().selectCreateNewFolder().render();
        docPage = (DocumentLibraryPage) newFolderPage.createNewFolder(folderName, folderTitle, folderDesc);

        logger.info("Folder Created" + folderName);
        return docPage.render();
    }

    /**
     * Creates a new folder at the Path specified, Starting from the Document Library Page.
     * Assumes User is logged in and a specific Site is open.
     * 
     * @param driver WebDrone Instance
     * @param folderName String Name of the folder to be created
     * @param folderTitle String Title of the folder to be created
     * @param folderDesc String Description of the folder to be created
     * @return DocumentLibraryPage
     */
    public static HtmlPage createFolderWithValidation(WebDrone driver, String folderName, String folderTitle, String folderDesc)
    {
        DocumentLibraryPage docPage = null;

        // Open Document Library
        SharePage thisPage = ShareUser.getSharePage(driver);

        if (!(thisPage instanceof RepositoryPage) && (!(thisPage instanceof DocumentLibraryPage)))
        {
            docPage = ShareUser.openDocumentLibrary(driver);
        }
        else
        {
            docPage = (DocumentLibraryPage) thisPage;
        }

        NewFolderPage newFolderPage = docPage.getNavigation().selectCreateNewFolder().render();
        HtmlPage htmlPage = newFolderPage.createNewFolderWithValidation(folderName, folderTitle, folderDesc);

        logger.info("Folder Created" + folderName);
        return htmlPage.render();
    }

    /**
     * This method is used to edit the properties of a file/folder from document library page.
     * Assumes User is logged in and a specific Site is open.
     * 
     * @param driver WebDrone Instance
     * @param siteName String Name of the Site to be accessed
     * @param testFile String Name of the file/folder to be edited
     * @return {@link DocumentLibraryPage}
     */
    public static DocumentLibraryPage editPropertiesFromDocLibPage(WebDrone drone, String siteName, String fileOrFolderName)
    {
        return getEditPropertiesFromDocLibPage(drone, siteName, fileOrFolderName).selectSave().render();
    }

    /**
     * This method does the copy or move the folder or document into another folder.
     * User should be on Document Library Page.
     * 
     * @param siteName
     * @param isCopy
     * @param testFolderName
     * @param copyFolderName
     * @param docLibPage
     * @return DocumentLibraryPage
     */
    public static DocumentLibraryPage copyOrMoveToFolder(WebDrone drone, String siteName, String sourceFolder, String[] destinationFolders, boolean isCopy)
    {
        if (StringUtils.isEmpty(siteName) || StringUtils.isEmpty(sourceFolder) || destinationFolders.length == 0)
        {
            throw new IllegalArgumentException("sitename/sourceFolder/destinationFolders should not be empty or null");
        }

        String[] destinationFolderNames = new String[destinationFolders.length + 1];
        destinationFolderNames[0] = DEFAULT_FOLDER;
        CopyOrMoveContentPage copyOrMoveContentPage;

        for (int i = 0; i < destinationFolders.length; i++)
        {
            destinationFolderNames[i + 1] = destinationFolders[i];
        }

        DocumentLibraryPage docLibPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);

        FileDirectoryInfo contentRow = docLibPage.getFileDirectoryInfo(sourceFolder);

        if (isCopy)
        {
            copyOrMoveContentPage = contentRow.selectCopyTo().render();
        }
        else
        {
            copyOrMoveContentPage = contentRow.selectMoveTo().render();
        }
        copyOrMoveContentPage = copyOrMoveContentPage.selectSite(siteName).render();

        copyOrMoveContentPage = copyOrMoveContentPage.selectPath(destinationFolderNames).render();

        return copyOrMoveContentPage.selectOkButton().render();
    }

    /**
     * Get Edit Document PRoperties pop up.
     * 
     * @param drone
     * @param siteName
     * @param fileOrFolderName
     * @return
     */
    public static EditDocumentPropertiesPopup getEditPropertiesFromDocLibPage(WebDrone drone, String siteName, String fileOrFolderName)
    {
        logger.info("Editing " + fileOrFolderName + " properties from DocumentLibraryPage.");

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        return docLibPage.getFileDirectoryInfo(fileOrFolderName).selectEditProperties().render();
    }

    /**
     * Add comment for file and folder.
     * 
     * @param drone
     * @param contentName
     * @param isFile
     * @param comment
     * @return
     */
    public static DetailsPage addComment(WebDrone drone, String contentName, String comment)
    {
        DetailsPage detailsPage = getContentDetailsPage(drone, contentName);
        return detailsPage.addComment(comment).render();
    }

    /**
     * Opens the content details page starting from the parent folder within DocumentLibrary
     * 
     * @param drone
     * @param contentName
     * @param isFile
     * @return DetailsPage
     */
    public static DetailsPage getContentDetailsPage(WebDrone drone, String contentName)
    {
        DocumentLibraryPage docLibPage = (DocumentLibraryPage) getSharePage(drone);
        DetailsPage detailsPage = null;

        if (docLibPage.getFileDirectoryInfo(contentName).isFolder())
        {
            detailsPage = docLibPage.getFileDirectoryInfo(contentName).selectViewFolderDetails().render();
        }
        else
        {
            detailsPage = docLibPage.selectFile(contentName).render();
        }
        return detailsPage.render();
    }

    /**
     * Navigating to ManagePermission.
     * 
     * @param drone
     * @param contentName
     * @param comment
     * @return
     */
    public static ManagePermissionsPage manageContentPermissions(WebDrone drone, String contentName)
    {
        return getContentRow(drone, contentName).selectManagePermission().render();
    }

    /**
     * Get the specified contentRow from DocumentLibraryPage
     * 
     * @param drone
     * @param contentName
     * @return FileDirectoryInfo
     */
    private static FileDirectoryInfo getContentRow(WebDrone drone, String contentName)
    {
        DocumentLibraryPage doclibPage = (DocumentLibraryPage) getSharePage(drone);
        return doclibPage.getFileDirectoryInfo(contentName);
    }

    /**
     * private method to do upload new version
     */
    private static HtmlPage UploadNewVersion(WebDrone drone, UpdateFilePage updatePage, boolean majorVersion, String fileName, String comments)
    //throws Exception
    {
        String fileContents = "New File being created on repository page:" + fileName;
        File newFileName = newFile(fileName, fileContents);
        HtmlPage page = null;
        try
        {
            updatePage.uploadFile(fileName);
            if (majorVersion)
            {
                updatePage.selectMajorVersionChange();
            }
            else
            {
                updatePage.selectMinorVersionChange();
            }

            updatePage.uploadFile(newFileName.getCanonicalPath());
            updatePage.setComment(comments);
            page = updatePage.submit().render();
        }
        catch (Exception e)
        {
            throw new SkipException("Error in updating file in repository." + e);
        }
        return page;
    }

    public static DocumentDetailsPage uploadNewVersionFromDocDetail(WebDrone drone, boolean majorVersion, String fileName, String comments) //throws Exception
    {
        DocumentDetailsPage docdetailPage = (DocumentDetailsPage) ShareUser.getSharePage(drone);
        UpdateFilePage updateFilePage = docdetailPage.selectUploadNewVersion().render();
        docdetailPage = (DocumentDetailsPage) UploadNewVersion(drone, updateFilePage, majorVersion, fileName, comments);
        return docdetailPage.render();
    }

    /**
     * @param drone
     * @param contentName
     * @param modifyDetails
     * @param doSave
     * @return
     */
    public static DocumentLibraryPage editContentProperties(WebDrone drone, String contentName, String modifyDetails, boolean doSave)
    {
        EditDocumentPropertiesPopup editDocumentPropertiesPopup = getFileDirectoryInfo(drone, contentName).selectEditProperties().render();
        editDocumentPropertiesPopup.setDescription(modifyDetails);
        if (doSave)
        {
            return editDocumentPropertiesPopup.selectSave().render();
        }
        else
        {
            return editDocumentPropertiesPopup.selectCancel().render();
        }
    }

    /**
     * @param drone
     * @param contentName
     * @return
     */
    public static ContentDetails getInLineEditContentDetails(WebDrone drone, String contentName)
    {
        InlineEditPage inlineEditPage = getFileDirectoryInfo(drone, contentName).selectInlineEdit().render();
        EditTextDocumentPage editTextDocumentPage = (EditTextDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT);
        ContentDetails contentDetails = editTextDocumentPage.getDetails();
        editTextDocumentPage.selectCancel().render();
        return contentDetails;
    }

    /**
     * @param drone
     * @param contentName
     * @return
     */
    public static FileDirectoryInfo getFileDirectoryInfo(WebDrone drone, String contentName)
    {
        SharePage sharePage = getSharePage(drone);
        if (sharePage instanceof RepositoryPage)
        {
            return ((RepositoryPage) sharePage).getFileDirectoryInfo(contentName);
        }
        else
        {
            return ((DocumentLibraryPage) sharePage).getFileDirectoryInfo(contentName);
        }
    }

    /**
     * Open DocumentLibrary Page in Simple / Detailed View based on Enum value
     * Assumes User is logged in
     * 
     * @param driver
     * @param viewType {@link} ViewType: Enum value specifying DocumentLibraryView to be opened
     * @return DocumentLibraryPage
     */
    public static DocumentLibraryPage selectView(WebDrone driver, ViewType viewType)
    {
        DocumentLibraryPage docPage = (DocumentLibraryPage) ShareUser.getSharePage(driver);

        switch (viewType)
        {
            case SIMPLE_VIEW:
                docPage = docPage.getNavigation().selectSimpleView().render();
                logger.info("Opened Simple View");
                return docPage;
            case DETAILED_VIEW:
                docPage = docPage.getNavigation().selectDetailedView().render();
                logger.info("Opened Detailed View");
                return docPage;
            case GALLERY_VIEW:
                docPage = docPage.getNavigation().selectGalleryView().render();
                logger.info("Opened Gallery View");
                return docPage;
            case TABLE_VIEW:
                docPage = docPage.getNavigation().selectTableView().render();
                logger.info("Opened Table View");
                return docPage;
            default:
                logger.info("Failed to find specified View: View not changed");
                return docPage;
        }
    }

    /**
     * Waits for the specified entry to appear on the DocumentLibrary Page-Filtered based on tag
     * 
     * @param driver
     * @param entry
     * @param entryPresent
     * @return
     */
    public static Boolean searchDocumentLibraryWithRetry(WebDrone driver, String entry, Boolean entryPresent)
    {
        Boolean found = false;
        Boolean resultAsExpected = false;

        // Repeat search until the element is found or Timeout is hit
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(driver, refreshDuration);
                driver.refresh();
            }

            DocumentLibraryPage documentLibraryPage = (DocumentLibraryPage) getSharePage(driver);
            found = documentLibraryPage.isFileVisible(entry);

            resultAsExpected = (entryPresent.equals(found));
            if (resultAsExpected)
            {
                break;
            }
        }

        return resultAsExpected;
    }

    /**
     * Add given tags to file or folder in {@link TagPage}.
     * Assume that user currently in {@link DocumentLibraryPage}.
     * 
     * @param drone
     * @param contentName
     * @param tags - Tags to be added to file
     * @return {@link DocumentLibraryPage}
     */
    public static DocumentLibraryPage addTagsFromDocLib(WebDrone drone, String contentName, List<String> tags)
    {
        DetailsPage detailsPage = addTags(drone, contentName, tags).render();
        return detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
    }

    /**
     * Add given tags to file or folder in {@link TagPage}.
     * Assume that user currently in {@link DocumentLibraryPage}.
     * 
     * @param drone
     * @param contentName
     * @param tags - Tags to be added to file
     * @return {@link DocumentLibraryPage}
     */
    public static HtmlPage addTags(WebDrone drone, String contentName, List<String> tags)
    {
        DocumentLibraryPage doclibPage = getSharePage(drone).render();
        DetailsPage detailsPage;

        if (doclibPage.getFileDirectoryInfo(contentName).isFolder())
        {
            detailsPage = doclibPage.getFileDirectoryInfo(contentName).selectViewFolderDetails();
        }
        else
        {
            detailsPage = doclibPage.selectFile(contentName).render();
        }
        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
        TagPage tagPage = propertiesPage.getTag().render();
        for (String tag : tags)
        {
            tagPage = tagPage.enterTagValue(tag).render();
            logger.info("Tag being added: " + tag);
        }
        propertiesPage = tagPage.clickOkButton().render();
        return propertiesPage.selectSave().render();
    }

    public static String getDocumentContents(WebDrone drone, String fileName)
    {
        DocumentLibraryPage doclibPage = getSharePage(drone).render();

        if(doclibPage.getFileDirectoryInfo(fileName).isFolder())
        {
            throw new UnsupportedOperationException("This util isn't supported for Folders, files only");
        }

        DocumentDetailsPage detailsPage = doclibPage.selectFile(fileName).render();

        EditTextDocumentPage editTextDocumentPage = detailsPage.selectInlineEdit().render();

        return editTextDocumentPage.getDetails().getContent();
    }

    /**
     * Uses the in-line rename function to rename content
     * Assumes User is logged in and a DocumentLibraryPage of the selected site is open
     * 
     * @param drone
     * @param contentName
     * @param newName
     * @param saveChanges   <code>true</code> saves the changes, <code>false</code> cancels without saving.
     * @return
     */
    public static DocumentLibraryPage editContentNameInline(WebDrone drone, String contentName, String newName, boolean saveChanges)
    {        
        FileDirectoryInfo fileDirInfo = getFileDirectoryInfo(drone, contentName);

        fileDirInfo.contentNameEnableEdit();
        fileDirInfo.contentNameEnter(newName);
        if(saveChanges)
        {
            fileDirInfo.contentNameClickSave();
        }
        else
        {
            fileDirInfo.contentNameClickCancel();
        }

        return getSharePage(drone).render();
    }

    /**
     * This does the adding the Properties for file or foler.
     * User should be on document library page.
     * 
     * @param drone
     * @param contentName
     * @param newName
     * @param newTitle
     * @param newDescription
     * @param doSave
     * @return {@link DocumentLibraryPage}
     */
    public static DocumentLibraryPage editProperties(WebDrone drone, String contentName, String newName, String newTitle, String newDescription, boolean doSave)
    {
        if (drone == null || StringUtils.isEmpty(contentName))
        {
            throw new IllegalArgumentException("Mandatory params Drone/contentName should not be null or blank.");
        }

        DocumentLibraryPage docLibPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(contentName);

        // Click on Edit in propertis link from gallery view.
        EditDocumentPropertiesPopup editDocPropertiesPage = thisRow.selectEditProperties().render();

        if (!StringUtils.isEmpty(newName))
        {
            editDocPropertiesPage.setName(newName);
        }

        if (!StringUtils.isEmpty(newDescription))
        {
            editDocPropertiesPage.setDescription(newDescription);
        }

        if (!StringUtils.isEmpty(newTitle))
        {
            editDocPropertiesPage.setDocumentTitle(newTitle);
        }

        if (doSave)
        {
            return editDocPropertiesPage.selectSave().render();
        }
        else
        {
            return editDocPropertiesPage.selectCancel().render();
        }
    }

    /**
     * Sorts the document library by the given field.
     * 
     * @param drone
     * @param field         The field to sort by.
     * @param sortAscending <code>true</code> if ascending. <code>false</code> if descending.
     * @return {@link DocumentLibraryPage}
     */
    public static DocumentLibraryPage sortLibraryOn(WebDrone drone, SortField field, boolean sortAscending)
    {
        DocumentLibraryPage docLibPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);

        docLibPage.getNavigation().selectSortFieldFromDropDown(field).render();
        if(sortAscending)
        {
            return docLibPage.getNavigation().sortAscending().render();
        }
        else
        {
            return docLibPage.getNavigation().sortDescending().render();
        }
    }

    /**
     * Edits the document using the in-line edit form.
     * 
     * @param drone
     * @param fileName
     * @param mimeType
     * @param details
     * @return
     */
    public static HtmlPage editTextDocumentInLine(WebDrone drone, String fileName, ContentDetails details)
    {
        InlineEditPage inlineEditPage;
        SharePage page = ShareUser.getSharePage(drone);
        if(page instanceof DocumentLibraryPage)
        {
            DocumentLibraryPage docLibPage = (DocumentLibraryPage)ShareUser.getSharePage(drone);

            inlineEditPage = docLibPage.getFileDirectoryInfo(fileName).selectInlineEdit().render();
        }
        else
        {
            inlineEditPage = (InlineEditPage)page;
        }
        EditTextDocumentPage editTextDocumentPage = inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT).render();

        return editTextDocumentPage.saveWithValidation(details).render();
    }
}