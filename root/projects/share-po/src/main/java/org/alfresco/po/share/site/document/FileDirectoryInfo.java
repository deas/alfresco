package org.alfresco.po.share.site.document;

import java.util.List;

import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.webdrone.HtmlPage;
import org.openqa.selenium.WebElement;

public interface FileDirectoryInfo
{

    /**
     * Gets the name of the file or directory, if none then empty string is
     * returned.
     *
     * @return String title
     */
     String getName();

    /**
     * Click on title.
     */
     void clickOnTitle();

    /**
     * Checks if the FileDirectory is of a folder type.
     *
     * @return true if folder
     */
     boolean isTypeFolder();

    /**
     * Gets the description of the file or directory, if none then empty string
     * is returned.
     *
     * @return String Content description
     */
     String getDescription();

    /**
     * Gets the Create / Edit Information of the file or directory, if none then
     * empty string is returned.
     *
     * @return String Content Edit Information
     */
     String getContentEditInfo();

    /**
     * Gets the Tag Information of the file or directory, if none then 'No Tags'
     * string is returned.
     *
     * @return List<String> List of tags added to the content
     */
     List<String> getTags();

    /**
     * Get the {@link List} of added {@link Categories}.
     *
     * @return {@link List} of {@link Categories}
     */
     List<Categories> getCategories();

    /**
     * Select the delete button on the item.
     * @return boolean <tt>true</tt> if delete option is available and clicked
     */
     ConfirmDeletePage selectDelete();

    /**
     * Selects the edit properties link on the select data row on
     * DocumentLibrary Page.
     *
     * @return {@link EditDocumentPropertiesPopup} response
     */
     EditDocumentPropertiesPopup selectEditProperties();

    /**
     * Selects the view in browser link on the select data row on
     * DocumentLibrary Page.
     *
     */
     void selectViewInBrowser();

    /**
     * Selects or de-selects the favorite option on the select data row on DocumentLibrary Page.
     */
     void selectFavourite();

    /**
     * Selects or de selects the Like option on the select data row on
     * DocumentLibrary Page.
     */
     void selectLike();

    /**
     * Checks if the Like option is selected on the selected data row on
     * DocumentLibrary Page
     *
     * @return {boolean} true if the content is liked
     */
     boolean isLiked();

    /**
     * Checks if the Favourite option is selected on the selected data row on
     * DocumentLibrary Page
     *
     * @return {Boolean} true if the content is marked as Favourite
     */
     boolean isFavourite();

    /**
     * Gets the like count for the selected data row on DocumentLibrary Page
     *
     * @return {String} Like Count
     */
     String getLikeCount();

    /**
     * Check if tags are attached to the selected content.
     *
     * @return boolean <tt>true</tt> if content has one or more Tags
     */
     boolean hasTags();

    /**
     * Adds the specified Tag to the file or directory.
     *
     * @param tagName String tag to be added
     */
     void addTag(String tagName);

    /**
     * Get NodeRef for the content on the selected data row on DocumentLibrary
     * Page.
     *
     * @return {String} Node Ref / GUID
     */
     String getContentNodeRef();

    /**
     * Gets the Title of the file or directory, if none then empty string
     * is returned.
     *
     * @return String Content description
     */
     String getTitle();

    /**
     * Mimics the action of hovering over a tag until edit tag icon appears.
     */
     void clickOnAddTag();

    /**
     * This method gets the status whether given tagname remove button
     * has found or not.
     * @return boolean if icon is displayed
     */
     boolean removeTagButtonIsDisplayed(String tagName);

    /**
     * This method clicks on given tag name remove button.
     * @param tagName String tag name
     */
     void clickOnTagRemoveButton(String tagName);

    /**
     * This method is used to click on save button when editing a tag.
     */
     void clickOnTagSaveButton();

    /**
     * This method is used to click on cancel button when editing a tag.
     */
     void clickOnTagCancelButton();

    /**
     * Selects checkbox next to the contentRow.
     */
     void selectCheckbox();

    /**
     * Verify if checkbox next to the contentRow is selected.
     * @return true if selected
     */
     boolean isCheckboxSelected();

    /**
     * Clicks on the thumbnail next to the contentRow.
     *
     * @return {Link SitePage} Instance of SitePage page object
     */
    HtmlPage selectThumbnail();

    /**
     * Returns true if content in the selected data row on DocumentLibrary is
     * folder Page.
     *
     * @return {boolean} <tt>true</tt> if the content is of type folder.
     */
     boolean isFolder();

    /**
     * Returns whether the file / dir is cloud synced.
     *
     * @return
     */
     boolean isCloudSynced();

    /**
     * Returns whether the file / dir is part of workflow.
     *
     * @return
     */
     boolean isPartOfWorkflow();

    /**
     * Selects the <Download as zip> link on the select data row on DocumentLibrary
     * Page. Only available for content type = Folder.
     */
     void selectDownloadFolderAsZip();

    /**
     * Selects the <Download> link on the select data row on DocumentLibrary Page.
     */
     void selectDownload();

    /**
     * Gets the node ref id of the content.
     * @return String node identifier
     */
     String getNodeRef();

    /**
     * Selects the <View Details> link on the select data row on DocumentLibrary Page.
     * Only available for content type = Folder.
     *
     * @return {@link DocumentLibraryPage} response
     */
     FolderDetailsPage selectViewFolderDetails();

    /**
     * This method clicks on tag Name link.
     *
     * @param tagName
     * @return {@link DocumentLibraryPage}
     */
     DocumentLibraryPage clickOnTagNameLink(String tagName);

    /**
     * Selects the "Sync to Cloud" link on the select data row on
     * DocumentLibrary Page.
     *
     * @return {@link DestinationAndAssigneePage} response
     */
     HtmlPage selectSyncToCloud();

    /**
     * Selects the edit in google docs link
     *
     * @return {@link DestinationAndAssigneePage} response
     */
     HtmlPage selectEditInGoogleDocs();

    /**
     * Returns true if Sign In To Alfresco Cloud popup opens (User haven't set up CloudSync)
     *
     * @return boolean
     */
     boolean isSignUpDialogVisible();

    /**
     * Selects the "unSync to Cloud" link on the select data row on
     * DocumentLibrary Page.
     *
     * @return {@link DestinationAndAssigneePage} response
     */
     DocumentLibraryPage selectUnSyncAndRemoveContentFromCloud(boolean doRemoveContentOnCloud);

    /**
     * Selects the "Sync to Cloud" link on the select data row on
     * DocumentLibrary Page.
     *
     * @return {@link DestinationAndAssigneePage} response
     */
     void selectUnSyncFromCloud();

    /**
     * This method verifies the viewCloudSyncInfo link is present or not.
     * @return boolean
     */
     boolean isViewCloudSyncInfoLinkPresent();

    /**
     * This method clicks on the viewCloudSyncInfo link.
     * @return SyncInfoPage
     */
     SyncInfoPage clickOnViewCloudSyncInfo();

    /**
     * Selects the "Inline Edit" link on the select data row on
     * DocumentLibrary Page.
     *
     * @return {@link InlineEditPage} response
     */
     HtmlPage selectInlineEdit();

    /**
     * This method clicks on the viewCloudSyncInfo link.
     * @return SyncInfoPage
     */
     String getCloudSyncType();

    /**
     * Retrieve content info (This document is locked by you., This document is locked by you for offline editing., Last sync failed.)
     * @return
     */
     String getContentInfo();

    /**
     * Method to check if the content is locked or not
     * @return
     */
     boolean isLocked();

    /**
     * Method to check if Inline Edit Link is displayed or not
     *
     * @return true if visible on the page
     */
     boolean isInlineEditLinkPresent();

    /**
     * Method to check if Edit Offline Link is displayed or not
     *
     * @return true if visible on the page
     */
     boolean isEditOfflineLinkPresent();

    /**
     * This method verifies the editInGoogleDocs link is present or not.
     * @return boolean
     */
     boolean isEditInGoogleDocsPresent();

    /**
     * This method verifies the delete link is present or not.
     * @return boolean
     */
     boolean isDeletePresent();

    /**
     * Select the link manage rules from
     * the actions drop down.
     */
     HtmlPage selectManageRules();

    /**
     * Check "UnSync to Cloud" link on the select data row.
     * DocumentLibrary Page.
     * @author nshah
     * @return {@link DestinationAndAssigneePage} response
     */
     boolean isUnSyncFromCloudLinkPresent();

    /**
     * Verify if the Sync failed icon is displayed or not
     * @param waitTime
     * @return
     */
     boolean isSyncFailedIconPresent(long waitTime);

    /**
     * Select the link Request Sync from
     * the actions drop down.
     */
     DocumentLibraryPage selectRequestSync();

    /**
     * Request to sync is present or not.
     * the actions drop down.
     */
     boolean isRequestToSyncLinkPresent();

    /**
     * Check "Sync to Cloud" link on the select data row.
     * DocumentLibrary Page.
     * @author rmanyam
     * @return
     */
     boolean isSyncToCloudLinkPresent();

    /**
     * select Manage permission link from more option of document library.
     * @return
     */
     ManagePermissionsPage selectManagePermission();

    /**
     * select Copy to... link from more option of document library.
     *
     * @return CopyOrMoveContentPage
     */
     CopyOrMoveContentPage selectCopyTo();

    /**
     * select Move to... link from more option of document library.
     *
     * @return CopyOrMoveContentPage
     */
     CopyOrMoveContentPage selectMoveTo();

     HtmlPage delete();

    /**
     * select StartWorkFlow... link from more option of document library.
     *
     * @return StartWorkFlowPage
     */
     StartWorkFlowPage selectStartWorkFlow();

    /**
     * Select UploadNewVersion - link fro more option of document library
     *
     * @return - UpdateFilePage
     */

     UpdateFilePage selectUploadNewVersion();

    /**
     * check  Manage permission link from more option of document library.
     * @return
     */
     boolean isManagePermissionLinkPresent();

    /**
     * check  Edit properties link from more option of document library.
     * @return
     */
     boolean isEditPropertiesLinkPresent();

    /**
     * Method to select Edit Offline link
     * @return {@link DocumentLibraryPage}
     */
     DocumentLibraryPage selectEditOffline();

    /**
     * Method to select Cancel Editing link
     * @return {@link DocumentLibraryPage}
     */
     DocumentLibraryPage selectCancelEditing();

    /**
     * Returns whether the file is being edited
     *
     * @return
     */
     boolean isEdited();

    /**
     * Mimics the action of select the manage aspects.
     *
     * @return {@link SelectAspectsPage}
     */
     SelectAspectsPage selectManageAspects();
     
     /**
     * @return
     */
    public boolean isCommentLinkPresent();

     /**
      * Performs the find with an added resolveStaleness.
      * If we encounter the staleness exception we refresh the web
      * element we are working with and re-do the search.
      * @param By css selector
      * @return {@link WebElement}
      */
     String getLikeOrUnlikeTip();

    /**
     * Checks if quick share link present
     * 
     * @return boolean
     */
    boolean isShareLinkVisible();

    boolean isViewInBrowserVisible();

    void enterTagString(final String tagName);
}