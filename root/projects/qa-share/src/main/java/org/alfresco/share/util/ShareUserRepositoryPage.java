package org.alfresco.share.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.document.Categories;
import org.alfresco.po.share.site.document.CategoryPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.SelectAspectsPage;
import org.alfresco.po.share.site.document.SortField;
import org.alfresco.po.share.site.document.TagPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.SkipException;

public class ShareUserRepositoryPage extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(ShareUserRepositoryPage.class);
    private static final String DATA_DICTIONARY_FOLDER = "Data Dictionary";
    private static final String NODE_TEMPLATES_FOLDER = "Node Templates";

    public ShareUserRepositoryPage()
    {
        if (logger.isTraceEnabled())
        {
            logger.debug(this.getClass().getSimpleName() + " instantiated");
        }
    }

    /**
     * Open Repository Page: Top Level Assumes User is logged in - Opens
     * repository in simple View
     * 
     * @param driver
     *            WebDrone Instance
     * @return RepositoryPage
     */

    public static RepositoryPage openRepositorySimpleView(WebDrone driver)
    {

        // Assumes User is logged in
        SharePage page = ShareUser.getSharePage(driver);

        RepositoryPage repositorypage = page.getNav().selectRepository();
        repositorypage = ((RepositoryPage) ShareUserSitePage.selectView(driver, ViewType.SIMPLE_VIEW));
        logger.info("Opened RepositoryPage");
        return repositorypage;
    }

    /**
     * Open Repository Page: Top Level Assumes User is logged in - Opens
     * repository in detailed View
     * 
     * @param driver
     *            WebDrone Instance
     * @return RepositoryPage
     */

    public static RepositoryPage openRepositoryDetailedView(WebDrone driver)
    {

        // Assumes User is logged in
        RepositoryPage repositorypage = openRepository(driver);

        repositorypage = ((RepositoryPage) ShareUserSitePage.selectView(driver, ViewType.DETAILED_VIEW)).render();
        logger.info("Opened RepositoryPage");
        return repositorypage;
    }

    /**
     * Open Repository Page: Default View
     * 
     * @param driver
     *            WebDrone Instance
     * @return RepositoryPage
     */
    public static RepositoryPage openRepository(WebDrone driver)
    {
        SharePage page = ShareUser.getSharePage(driver);

        RepositoryPage repositorypage = page.getNav().selectRepository().render();
        logger.info("Opened RepositoryPage");
        return repositorypage;
    }

    /**
     * Assumes Repository Page is open and navigates to the Path specified.
     * 
     * @param driver
     *            WebDrone Instance
     * @param folderPath
     *            : String folder path relative to RepositoryPage e.g. Repo +
     *            file.seperator + folderName1
     * @throws SkipException
     *             if error in this API
     * @return RepositoryPage
     */

    public static RepositoryPage navigateToFolderInRepository(WebDrone driver, String folderPath) throws Exception
    {
        openRepository(driver);
        RepositoryPage repositoryPage = ((RepositoryPage) ShareUserSitePage.navigateToFolder(driver, folderPath)).render();
        return repositoryPage;
    }

    /**
     * Assumes User is logged in and a specific Site's RepositoryPage is open,
     * Parent Folder is pre-selected.
     * 
     * @param file
     *            File Object for the file in reference
     * @return RepositoryPage
     * @throws SkipException
     *             if error in this API
     */
    public static RepositoryPage uploadFileInRepository(WebDrone driver, File file) throws Exception
    {
        RepositoryPage repositoryPage = ((RepositoryPage) ShareUserSitePage.uploadFile(driver, file)).render();
        return repositoryPage;
    }

    /**
     * Creates a new folder at the Path specified, Starting from the Document
     * Library Page. Assumes User is logged in and a specific Site is open.
     * 
     * @param driver
     *            WebDrone Instance
     * @param folderName
     *            String Name of the folder to be created
     * @param folderDesc
     *            String Description of the folder to be created
     * @return RepositoryPage
     */
    public static RepositoryPage createFolderInRepository(WebDrone driver, String folderName, String folderDesc)
    {
        return createFolderInRepository(driver, folderName, null, folderDesc);
    }

    /**
     * Creates a new folder at the Path specified, Starting from the
     * RepositoryPage Page. Assumes User is logged in and a specific Site is
     * open.
     * 
     * @param driver
     *            WebDrone Instance
     * @param folderName
     *            String Name of the folder to be created
     * @param folderTitle
     *            String Title of the folder to be created
     * @param folderDesc
     *            String Description of the folder to be created
     * @return RepositoryPage
     */
    public static RepositoryPage createFolderInRepository(WebDrone driver, String folderName, String folderTitle, String folderDesc)
    {
        RepositoryPage repositoryPage = ShareUserSitePage.createFolder(driver, folderName, folderTitle, folderDesc).render();
        return repositoryPage;
    }

    /**
     * Creates a new folder at the Path specified, Starting from the
     * RepositoryPage Page. Assumes User is logged in and a specific Site is
     * open.
     * 
     * @param driver
     *            WebDrone Instance
     * @param folderName
     *            String Name of the folder to be created
     * @param folderTitle
     *            String Title of the folder to be created
     * @param folderDesc
     *            String Description of the folder to be created
     * @return RepositoryPage
     */
    public static HtmlPage createFolderInRepositoryWithValidation(WebDrone driver, String folderName, String folderTitle, String folderDesc)
    {
        HtmlPage htmlPage = ShareUserSitePage.createFolderWithValidation(driver, folderName, folderTitle, folderDesc);
        if (htmlPage instanceof DocumentLibraryPage)
        {
            RepositoryPage repositoryPage = htmlPage.render();
            return repositoryPage;
        }
        return htmlPage;
    }

    /**
     * This method does the copy or move the folder or document into another
     * folder. User should be on RepositoryPage Page.
     * 
     * @param isCopy
     * @param testFolderName
     * @param copyFolderName
     * @param docLibPage
     * @return CopyOrMoveContentPage
     */
    public static CopyOrMoveContentPage copyOrMoveToFolderInRepository(WebDrone drone, String sourceFolder, String[] destinationFolders, boolean isCopy)
    {
        if ((StringUtils.isEmpty(sourceFolder)) || (destinationFolders.length == 0))
        {
            throw new IllegalArgumentException("sitename/sourceFolder/destinationFolders should not be empty or null");
        }
        
        CopyOrMoveContentPage copyOrMoveContentPage;

        FileDirectoryInfo contentRow = ShareUserSitePage.getFileDirectoryInfo(drone,sourceFolder);
        if (isCopy)
        {
            copyOrMoveContentPage = contentRow.selectCopyTo().render();
        }
        else
        {
            copyOrMoveContentPage = contentRow.selectMoveTo().render();
        }
        copyOrMoveContentPage = copyOrMoveContentPage.selectDestination(REPO).render();
        copyOrMoveContentPage = copyOrMoveContentPage.selectPath(destinationFolders).render();
        return copyOrMoveContentPage;
    }

    /**
     * Selects cancel button
     * 
     * @param drone
     * @param sourceFolder
     * @param destinationFolders
     * @param isCopy
     * @return RepositoryPage
     */
    public static RepositoryPage copyOrMoveToFolderInRepositoryCancel(WebDrone drone, String sourceFolder, String[] destinationFolders, boolean isCopy)
    {

        CopyOrMoveContentPage copyOrMoveContentPage = copyOrMoveToFolderInRepository(drone, sourceFolder, destinationFolders, isCopy);

        RepositoryPage repoPage = copyOrMoveContentPage.selectCancelButton().render();

        return repoPage;
    }

    /**
     * Selects ok button
     * 
     * @param drone
     * @param sourceFolder
     * @param destinationFolders
     * @param isCopy
     * @return
     */
    public static RepositoryPage copyOrMoveToFolderInRepositoryOk(WebDrone drone, String sourceFolder, String[] destinationFolders, boolean isCopy)
    {

        CopyOrMoveContentPage copyOrMoveContentPage = copyOrMoveToFolderInRepository(drone, sourceFolder, destinationFolders, isCopy);

        RepositoryPage repoPage = copyOrMoveContentPage.selectOkButton().render();

        return repoPage;
    }

    /**
     * Creates a new folder at the Path specified, Starting from the Re. Assumes
     * User is logged in and a specific Site is open.
     * 
     * @param driver
     *            WebDrone Instance
     * @param folderName
     *            String Name of the folder to be created
     * @param folderDesc
     *            String Description of the folder to be created
     * @param parentFolderPath
     *            String Path for the folder to be created, under
     *            DocumentLibrary : such as ConstRepo + file.seperator +
     *            parentFolderName1 + file.seperator + parentFolderName2
     * @throws Excetion
     */
    public static RepositoryPage createFolderInFolderInRepository(WebDrone driver, String folderName, String folderDesc, String parentFolderPath) throws Exception
    {
        return ((RepositoryPage) ShareUser.createFolderInFolder(driver, folderName, folderDesc, parentFolderPath)).render();
    }

    /**
     * Navigates to the Path specified, Starting from the Repository Page.
     * Assumes User is logged in and a specific Site is open.
     * 
     * @param fileName
     * @param parentFolderPath
     *            : such as Repository + file.seperator + parentFolderName1
     * @throws SkipException
     *             if error in this API
     */
    public static RepositoryPage uploadFileInFolderInRepository(WebDrone driver, String[] fileInfo) throws Exception
    {
        Integer argCount = fileInfo.length;
        if (argCount < 1)
        {
            throw new IllegalArgumentException("Specify at least Filename");
        }
        else if (argCount == 1)
        {
            fileInfo[1] = REPO;
        }

        openRepository(driver);
        RepositoryPage repositoryPage = ((RepositoryPage) ShareUser.uploadFileInFolder(driver, fileInfo)).render();
        return repositoryPage;
    }

    /**
     * This method is used to create content with name, title and description.
     * User should be logged in
     * 
     * @param drone
     * @param contentDetails
     * @param contentType
     * @return {@link RepositoryPage}
     * @throws Exception
     */
    public static RepositoryPage createContentInFolder(WebDrone drone, ContentDetails contentDetails, ContentType contentType, String... folderPath)
            throws Exception
    {
        // Open Folder in repository Library
        RepositoryPage repositoryPage = navigateFoldersInRepositoryPage(drone, folderPath);
        DocumentDetailsPage detailsPage = null;

        try
        {
            CreatePlainTextContentPage contentPage = repositoryPage.getNavigation().selectCreateContent(contentType).render();
            detailsPage = contentPage.create(contentDetails).render();
            repositoryPage = detailsPage.navigateToFolderInRepositoryPage().render();
        }
        catch (Exception e)
        {
            throw new SkipException("Error in creating content." + e);
        }

        return repositoryPage;
    }

    /**
     * This method is used to create content with name, title and description.
     * User should be logged in
     * 
     * @param drone
     * @param contentDetails
     * @param contentType
     * @return {@link RepositoryPage}
     * @throws Exception
     */
    public static HtmlPage createContentInFolderWithValidation(WebDrone drone, ContentDetails contentDetails, ContentType contentType, String... folderPath)
            throws Exception
    {
        // Open Folder in repository Library
        RepositoryPage repositoryPage = navigateFoldersInRepositoryPage(drone, folderPath);

        try
        {
            CreatePlainTextContentPage contentPage = repositoryPage.getNavigation().selectCreateContent(contentType).render();

            HtmlPage page = contentPage.createWithValidation(contentDetails).render();

            if(page instanceof DocumentDetailsPage)
            {
                DocumentDetailsPage detailsPage = page.render();
                repositoryPage = detailsPage.navigateToFolderInRepositoryPage().render();

                return repositoryPage;
            }
            return page;
        }
        catch (Exception e)
        {
            throw new SkipException("Error in creating content." + e.getMessage());
        }
    }

    /**
     * Method to navigate to site dashboard url, based on siteshorturl, rather
     * than sitename This is to be used to navigate only as a util, not to test
     * getting to the site dashboard
     * 
     * @param drone
     * @param siteShortURL
     * @return {@link SiteDashBoardPage
     *
     */
    public static RepositoryPage openSiteFromSitesFolderOfRepository(WebDrone drone, String siteName)
    {
        String url = drone.getCurrentUrl();      
        //http://127.0.0.1:8081/share  /page/repository#filter=path|%2FUser%2520Homes%2F userEnterprise42-5405%40freetht1.test-1  |&page=1
        String target = url.substring(0, url.indexOf("/page/")) + "/page/repository#filter=path|%2FSites%2F" + SiteUtil.getSiteShortname(siteName)
                + "|&page=1";

        drone.navigateTo(target);
        drone.waitForPageLoad(maxWaitTime);
        RepositoryPage repoPage = (RepositoryPage) ShareUser.getSharePage(drone);

        return repoPage.render();
    }

    public static RepositoryPage openUserFromUserHomesFolderOfRepository(WebDrone drone, String usrName)
    {
        String url = drone.getCurrentUrl();      
        //http://127.0.0.1:8081/share  /page/repository#filter=path|%2FUser%2520Homes%2F userEnterprise42-5405%40freetht1.test-1  |&page=1
        String target = url.substring(0, url.indexOf("/page/")) + "/page/repository#filter=path|%2FUser%2520Homes%2F" + StringUtils.replace(usrName, "@", "%40")
                + "|&page=1";

        drone.navigateTo(target);
        drone.waitForPageLoad(maxWaitTime);
        RepositoryPage repoPage = (RepositoryPage) ShareUser.getSharePage(drone);

        return repoPage.render();
    }

    /**
     * method to Navigate folder
     * 
     * @param drone
     * @param List
     *            of Folders
     */
    public static RepositoryPage navigateFoldersInRepositoryPage(WebDrone drone, String... folderPath)
    {
        boolean selected = false;
        if (folderPath == null || folderPath.length < 1)
        {
            throw new IllegalArgumentException("Invalid Folder path!!");
        }

        RepositoryPage repoPage = ShareUser.getSharePage(drone).render();

        try
        {

            for (String folder : folderPath)
            {
                repoPage.selectFolder(folder).render();
                selected = true;
                logger.info("Folder \"" + folder + "\" selected");

            }
            if (!selected)
            {
                throw new ShareException("Cannot select the folder metioned in the path");
            }
        }
        catch (Exception e)
        {
            throw new ShareException("Cannot select the folder metioned in the path");
        }

        return ShareUser.getSharePage(drone).render();

    }

    /**
     * @param drone
     * @param folderName
     * @param properString
     * @param doSave
     * @return
     */
    public static RepositoryPage editContentProperties(WebDrone drone, String folderName, String properString, boolean doSave)
    {
        return ((RepositoryPage) ShareUserSitePage.editContentProperties(drone, folderName, properString, doSave)).render();
    }

    /**
     * Add given tags to file or folder in {@link TagPage}.
     * Assume that user currently in {@link RepositoryPage}.
     * 
     * @param drone
     * @param contentName
     * @param tags - Tags to be added to content
     * @return {@link RepositoryPage}
     */
    public static RepositoryPage addTagsInRepo(WebDrone drone, String contentName, List<String> tags)
    {        
        DetailsPage detailsPage = ShareUserSitePage.addTags(drone, contentName, tags).render();
        RepositoryPage repoPage = detailsPage.navigateToParentFolder().render();        

        return repoPage;
    }

    /**
     * Opens the content details page starting from the parent folder within DocumentLibrary
     * Assume that user currently in {@link RepositoryPage}.
     * 
     * @param drone
     * @param contentName
     * @param isFile
     * @return DetailsPage
     */
    public static DetailsPage getContentDetailsPage(WebDrone drone, String contentName)
    {
        return ShareUserSitePage.getContentDetailsPage(drone, contentName);
    }

    public static DocumentDetailsPage uploadNewVersionFromDocDetail(WebDrone drone, boolean majorVersion, String fileName, String comments)
    {
        return ShareUserSitePage.uploadNewVersionFromDocDetail(drone, majorVersion, fileName, comments);
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
        return ShareUserSitePage.editTextDocumentInLine(drone, fileName, details);
    }

    /**
     * @param drone
     * @param contentName
     * @return
     */
    public static ContentDetails getInLineEditContentDetails(WebDrone drone, String contentName)
    {
        return ShareUserSitePage.getInLineEditContentDetails(drone, contentName);
    }
    
    public enum Operation 
    {
        REMOVE, ADD_AND_CANCEL, SAVE;
    }
    
    /**
     * Select operation to be performed on Tag.
     * @param drone
     * @param operation
     * @param tagName
     */
    public static void operationOnTag(WebDrone drone, Operation operation, String tagName)
    {
        EditDocumentPropertiesPage editPropPopUp = (EditDocumentPropertiesPage) getSharePage(drone);
        TagPage tagPage = (editPropPopUp).getTag().render();
        if (Operation.REMOVE.equals(operation))
        {
            tagPage.removeTagValue(tagName).render();
        }
        else
        {
            tagPage.enterTagValue(tagName).render();
           
            if (Operation.ADD_AND_CANCEL.equals(operation))
            {
                tagPage.clickCancelButton().render();
            }
            else
            // Save the tag
            {
                tagPage.clickOkButton().render();
            }
        }
    }
    
    /**
     * Add categories from EditDocumentPropertiesPopUp.
     * @param drone
     * @param folderName
     * @param category
     * @deprecated Use {@link #addCategories(WebDrone, String, String, boolean, String...)} instead.
     */
    @Deprecated
    public static EditDocumentPropertiesPage addCategories(WebDrone drone, String folderName, Categories category, boolean isOk )
    {
        return addCategories(drone, folderName, category.getValue(), isOk);
    }
    
    
    /**
     * Add categories from EditDocumentPropertiesPopUp.
     * 
     * @param drone
     * @param folderName
     * @param category
     * @return
     */
    public static EditDocumentPropertiesPage addCategories(WebDrone drone, String folderName, String category, boolean isOk, String... parentCategories )
    {
        CategoryPage categoryPage = ((EditDocumentPropertiesPage)getSharePage(drone)).getCategory();
        categoryPage.addCategories(Arrays.asList(category), parentCategories);

        if(isOk)
        {
            return categoryPage.clickOk().render();
        }
        else
        {
            return categoryPage.clickCancel().render();
        }
    }
    
    /**
     * Return EditDocumentPropertiesPopup from RepositoryPage or documentLibrary page.
     * @param drone
     * @param folderName
     * @return
     */
    public static EditDocumentPropertiesPage returnEditDocumentProperties(WebDrone drone, String folderName)
    {            
        return ShareUserSitePage.getFileDirectoryInfo(drone,folderName).selectEditProperties().render();       
    }
    
    
    /**
     * Add aspect.
     * @param drone
     * @param folderName
     * @param aspect
     */
    public static void addAspect(WebDrone drone, String folderName, DocumentAspect aspect)
    {
        RepositoryPage repositoryPage = (RepositoryPage)getSharePage(drone);
        
        // Select more options in folder1 and click on Manage Aspects
        SelectAspectsPage selectAspectsPage = repositoryPage.getFileDirectoryInfo(folderName).selectManageAspects().render();

        // Get several aspects in left hand side
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        
        aspects.add(aspect);

        // Add several aspects to right hand side
        selectAspectsPage = selectAspectsPage.add(aspects).render();

        // Verify assert added to currently selected right hand side
        Assert.assertTrue(selectAspectsPage.getSelectedAspects().contains(aspect));

        // Click on Apply changes on select aspects page
        selectAspectsPage.clickApplyChanges().render();
    }
    
    /**
     * Get properties.
     * @param drone
     * @param folderName
     * @param aspect
     * @return
     */
    public static Map<String, Object> getProperties(WebDrone drone, String folderName)
    {
        FolderDetailsPage folderDetailsPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderName).selectViewFolderDetails().render();
        return folderDetailsPage.getProperties();
    }
    
    /**
     * Copy folder to destination. 
     * @param drone
     * @param folderName
     * @param destinationFolderName
     */
    public static void copyToFolderInDestination(WebDrone drone, String folderName, String destinationFolderName)
    {

      CopyOrMoveContentPage copyOrMoveContentPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderName).selectCopyTo().render();
    
      copyOrMoveContentPage = copyOrMoveContentPage.selectDestination(destinationFolderName).render();
    
      copyOrMoveContentPage.selectOkButton().render();
    }
    
    /**
     * Add tag from repository page.
     * @param drone
     * @param contentName
     * @param tagName
     */
    public static RepositoryPage addTag(WebDrone drone, String contentName, String tagName)
    {
        RepositoryPage repoPage = ((RepositoryPage) ShareUserSitePage.addTag(drone, contentName, tagName)).render();
        return repoPage;
    }
    
    /**
     * Sorts the document library by the given field.
     * 
     * @param drone
     * @param field         The field to sort by.
     * @param sortAscending <code>true</code> if ascending. <code>false</code> if descending.
     * @return {@link RepositoryPage}
     */
    public static RepositoryPage sortLibraryOn(WebDrone drone, SortField field, boolean sortAscending)
    {
        return (RepositoryPage)ShareUserSitePage.sortLibraryOn(drone, field, sortAscending);
    }

    /**
     * Open Repository Page: Top Level Assumes User is logged in
     * Opens repository in simple View
     * 
     * @param driver WebDrone Instance
     * @return RepositoryPage
     */
    public static RepositoryPage openRepositoryGalleryView(WebDrone driver)
    {

        // Assumes User is logged in
        SharePage page = ShareUser.getSharePage(driver);

        RepositoryPage repositorypage = page.getNav().selectRepository();
        repositorypage = ((RepositoryPage) ShareUserSitePage.selectView(driver, ViewType.GALLERY_VIEW));
        logger.info("Opened RepositoryPage");
        return repositorypage;
    }

    /**
     * Method to create Node Template
     * @param drone
     * @param templateDetails
     * @param contentType
     * @throws Exception
     */
    public static void createNodeTemplate(WebDrone drone, ContentDetails templateDetails, ContentType contentType) throws Exception
    {
        openRepository(drone);
        String[] contentFolderPath = { DATA_DICTIONARY_FOLDER, NODE_TEMPLATES_FOLDER };
        createContentInFolder(drone, templateDetails, contentType, contentFolderPath);
    }

    /**
     * Method to create Node Template
     * @param drone
     * @param folderName
     * @throws Exception
     */
    public static void createFolderNodeTemplate(WebDrone drone, String folderName) throws Exception
    {
        openRepository(drone);
        String[] folderPath = { DATA_DICTIONARY_FOLDER, NODE_TEMPLATES_FOLDER };
        navigateFoldersInRepositoryPage(drone, folderPath);
        createFolderInRepository(drone, folderName, folderName);
    }

    /**
     * Method to get given Node Template Node Ref.
     * @param drone
     * @param contentName
     * @return
     * @throws Exception
     */
    public static String getNodeTemplateNodeRef(WebDrone drone, String contentName) throws Exception
    {
        navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + NODE_TEMPLATES_FOLDER);

        return ShareUserSitePage.getFileDirectoryInfo(drone, contentName).getNodeRef();
    }
    
    /**
     * Assumes User is logged in
     * Selects the specified view for the open folder in repository
     * 
     * @param driver WebDrone Instance
     * @param view ViewType
     * @return RepositoryPage
     */
    public static RepositoryPage selectView(WebDrone driver, ViewType view)
    {
        // Assumes User is logged in

        RepositoryPage repositorypage = ((RepositoryPage) ShareUserSitePage.selectView(driver, view));
        logger.info("Opened RepositoryPage");
        return repositorypage;
    }

}