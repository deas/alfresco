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
package org.alfresco.po.share.site.document;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.util.FileDownloader;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Site document library page object, holds all element of the HTML page
 * relating to share's site document library page.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public class DocumentDetailsPage extends DetailsPage
{
    private static final Log logger = LogFactory.getLog(DocumentDetailsPage.class);
    private static final String UPLOADED_DOCUMENT_NEW_VERSION_PLACEHOLDER = "div[class$='document-upload-new-version']";
    private static final String REVISON_HISTORY_PANEL = "document.detail.version.history.panel";
    private static final String DOWNLOAD_FILE_CSS_LOCATOR = "div.node-header>div.node-action>span>span>a";
    private static final String EDIT_OFFLINE_LINK = "div.document-edit-offline>a";
    private static final String INLINE_EDIT_LINK = ".document-inline-edit>a";
    private static final String DOCUMENT_PROPERTIES_DISPLAYED_SIZE = "span[id$='-formContainer_prop_size']";
    private static final String DOCUMENT_PREVIEW_WITH_FLASH_PLAYER = "div.web-preview.real";
    private static final String DOCUMENT_PREVIEW_WITHOUT_FLASH_PLAYER = "div[id$='default-previewer-div']>img";
    private static final String NO_DOCUMENT_PREVIEW = "div.message";
    private static final String FILE_ISNT_VISIBLE = "Unfortunately the file can't be viewed in your web browser.";
    private static final String DOCUMENT_CANT_BE_PREVIEWED = "This document can't be previewed.";
    private static final String CLICK_HERE_TO_DOWNLOAD_LINK = "Click here to download it.";
    private static final String THIN_DARK_TITLE_ELEMENT = "div.node-header>div.node-info>h1.thin.dark";
    private static final String SYNC_TO_CLOUD = "a[title='Sync to Cloud'].action-link>span";
    private static final String UNSYNC_FROM_CLOUD = "a[title='Unsync from Cloud'].action-link>span";
    protected static final String CHECKEDOUT_MESSAGE_PLACEHOLDER = "div.node-header>div.status-banner.theme-bg-color-2.theme-border-4";
    protected static final String ACTION_SET_ID= "document.detail.action.set.id";
    public static final String DOCUMENT_VERSION_PLACEHOLDER = "div.node-header>div.node-info>h1.thin.dark>span.document-version";
    private static final String LINK_EDIT_IN_GOOGLE_DOCS = "div[id$='default-actionSet'] div.google-docs-edit-action-link a";
    private static final String LINK_RESUME_EDIT_IN_GOOGLE_DOCS = "div[id$='default-actionSet'] div.google-docs-resume-action-link a";
    private static final String REQUEST_SYNC_ICON = "a.document-requestsync-link[title='Request Sync']";
    private static final String LOCATION_IN_CLOUD = "p.location";
    private static final String SYNC_STATUS = ".cloud-sync-details-info>p:not(.location)";
    private static final String COMMENT_COUNT = "span.comment-count";
    protected String previousVersion;
    private String expectedVersion;
    protected String documentVersion;
    private boolean isGoogleCreate = false;
    private static final String GOOGLE_DOCS_URL = "googledocsEditor?";
    private static final By WORKFLOW_INFO = By.cssSelector("div.document-workflows>div>div.info");
    private static final String COPY_THIS_LINK_TO_SHARE_THE_CURRENT_PAGE = "div.link-info input";

    public synchronized void setPreviousVersion(final String previousVersion)
    {
        this.previousVersion = previousVersion;
    }
    /**
     * Constructor
     */
    public DocumentDetailsPage(WebDrone drone)
    {
        this(drone, null);
    }

    /**
     * Constructor.
     */
    public DocumentDetailsPage(WebDrone drone, final String previousVersion)
    {
        super(drone);
        this.previousVersion = previousVersion;
    }
    /**
     * Verifies if the page has rendered completely by checking the page load is
     * complete and in addition it will observe key HTML elements have rendered.
     *
     * @param timer Max time to wait
     * @return {@link DocumentDetailsPage}
     */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized DocumentDetailsPage render(RenderTime timer)
    {
        String docVersionOnScreen;
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try{ this.wait(100L); } catch (InterruptedException e) {}
            }
            try
            {
                //If popup is not displayed start render check
                if(!drone.find(By.cssSelector("div.bd")).isDisplayed())
                {
                    //upload dialog should not be displayed.
                    if(!drone.find(By.cssSelector("div.yui-dt-bd")).isDisplayed())
                    {
                        docVersionOnScreen = drone.find(By.cssSelector(DOCUMENT_VERSION_PLACEHOLDER)).getText().trim();
                        // If the text is not what we expect it to be, then repeat
                        if (this.previousVersion != null && docVersionOnScreen.equals(this.previousVersion))
                        {
                            // We are still seeing the old version number
                            // Go around again
                            continue;
                        }
                        //If we see expected version number, stop and serve.
                        if(expectedVersion != null && !expectedVersion.isEmpty())
                        {
                            if(docVersionOnScreen.equals(this.expectedVersion))
                            {
                                break;
                            }
                        }
                        // Populate the doc version
                        break;
                    }
                    
                }
            }
            catch (TimeoutException te)
            {
                throw new PageException("Document version not rendered in time",te);
            }
            catch (NoSuchElementException te)
            {
                // Expected if the page has not rendered
            }
            catch (StaleElementReferenceException e)
            {
                // This occurs occasionally, as well
            }
            finally
            {
                timer.end();
            }
        }
        this.documentVersion = docVersionOnScreen;
        return this;
    }

    /**
     * Gets document version value from top of the details page
     *
     * @return String value of document version
     */
    public synchronized String getDocumentVersion()
    {
        // Render must have populated the document version
        return this.documentVersion;
    }

    /**
     * Verify if button on version history view is displayed
     *
     * @return true if visible on the page
     */
    public boolean isUploadNewVersionDisplayed()
    {
        try
        {
            return drone.find(By.cssSelector(UPLOADED_DOCUMENT_NEW_VERSION_PLACEHOLDER)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Verify if the action to edit offline is available
     *
     * @return true if visible on the page
     */
    public boolean isEditOfflineLinkDisplayed()
    {
        try
        {
        	boolean isEditOff = drone.find(By.cssSelector(EDIT_OFFLINE_LINK)).isDisplayed();
        	return isEditOff;
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }
    /**
     * Check for edit offline banner that appears on the top
     * of the page when document is locked by edit off line.
     * @return          returns <tt>false</tt> always
     */
    public boolean isEditOfflineDisplayed()
    {
        try
        {
            return drone.find(By.cssSelector("span.editing")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /**
     * Check for locked by you banner that appears on the top
     * of the page when document is locked by edit off line and locked by you when viewing original.
     * @return          returns <tt>false</tt> always
     */
    public boolean isLockedByYou()
    {
        try
        {
            return drone.find(By.cssSelector("span.lock-owner")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public DocumentDetailsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocumentDetailsPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if the page viewed is the document details page.
     */
    public boolean isDocumentDetailsPage()
    {
    	return isDetailsPage("document");
    }

    /**
     * Mimics the action of deleting a document detail.
     */
    public DocumentLibraryPage delete()
    {
        WebElement button = drone.findAndWait(By.cssSelector("div.document-delete>a"));
        button.click();
        confirmDelete();
        return new DocumentLibraryPage(drone);
    }

    /**
     * Gets the document detail title.
     *
     * @return String document detail page title
     */
    public String getDocumentTitle()
    {
    	WebElement element = drone.findAndWait(By.cssSelector(THIN_DARK_TITLE_ELEMENT));
        WebElement header = element.findElement(By.tagName("span"));
        return element.getText().replace(header.getText(), "");
    }

    /**
     * Mimics the action of clicking on the upload new version button.
     *
     * @return HtmlPage page response object
     */
    public HtmlPage selectUploadNewVersion()
    {
    	if(!alfrescoVersion.isFileUploadHtml5())
    	{
    		setSingleMode();
    	}
        WebElement link = drone.findAndWait(By.cssSelector("div.document-upload-new-version>a"));
        String version = getDocumentVersion();
        link.click();

        return getFileUpdatePage(drone, version, isEditOfflineLinkDisplayed());
    }

    /**
     * File upload page pop up object.
     *
     * @param drone {@link WebDrone} browser client
     * @param version the number of the original document
     * @param editOffLine mode status
     * @return {@link UpdateFilePage} page object response
     */
    public HtmlPage getFileUpdatePage(WebDrone drone, final String version, final boolean editOffLine)
    {
        return new UpdateFilePage(drone, version, editOffLine);
    }
    /**
     * Locates the revision history DIV.
     *
     * @return {@link WebElement} represent revision history
     */
    public WebElement getRevisionPanel()
    {
        return drone.findByKey(REVISON_HISTORY_PANEL);
    }

    /**
     * Get the comments of the last commit from the revision history panel
     *
     * @return String comments
     */
    public String getCommentsOfLastCommit()
    {
        /**
         * Use an element as an anchor as it has a unique id and dive into its
         * child elements to obtain the comments as it has not been given an id
         * or CSS that is unique.
         **/
        WebElement commentField = drone.findAndWait(By.cssSelector("div[id$='default-latestVersion'] div.version-details-right"));
        String comments = commentField.getText();
        if (!comments.isEmpty())
        {
            int index = comments.indexOf('\n');
            comments = comments.substring(index + 1);
        }
        return comments;
    }

    /**
     * Verifies if document is locked for off line editing.
     * @return true if locked for off line editing
     */
    public synchronized boolean isCheckedOut()
    {
        try
        {
            return drone.find(By.cssSelector(CHECKEDOUT_MESSAGE_PLACEHOLDER)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Get status banner message (This document is locked by you., This document is locked by you for offline editing., Last sync failed.)
     * @return String status banner text message
     */
    public synchronized String getContentInfo()
    {
        try
        {
            return drone.findAndWait(By.cssSelector(CHECKEDOUT_MESSAGE_PLACEHOLDER + ">span")).getText();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find Info banner.");
        }
        return "";
    }

    /**
     * Select the edit off line link.
     * @return {@link HtmlPage} edit off line page.
     */
    public HtmlPage selectEditOffLine(File file)
    {
        try
        {
            WebElement link = drone.find(By.cssSelector(EDIT_OFFLINE_LINK));
            link.click();
            if(file != null)
            {
                String path = downloadFile(DOWNLOAD_FILE_CSS_LOCATOR,false);
                FileDownloader downloader = new FileDownloader(drone);
                try
                {
                    downloader.download(path, file);
                }
                catch (Exception e)
                {
                    throw new PageException("Edit offline file download error", e);
                }
            }
            return new DocumentEditOfflinePage(drone);
        }
        catch (NoSuchElementException nse)
        {
        	throw new PageException("Unable to edit offline", nse);
        }
    }

    /**
     * Mimics the action of selecting In Line Edit.
     *
     * @return {@link InlineEditPage}
     */
    public EditTextDocumentPage selectInlineEdit()
    {
        try
        {
            WebElement link = drone.findAndWait(By.cssSelector(INLINE_EDIT_LINK));
            link.click();
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element");
            throw new PageOperationException("Unable to select Inline Edit", exception);
        }

        return new EditTextDocumentPage(drone);

    }

    /**
     * Finds and clicks on the file download link.
     * @param selector css selector descriptor
     * @return filePath String file url
     */
    private String downloadFile(final String selector, boolean click)
    {
        String filePath = "";
        try
        {
            WebElement link = drone.find(By.cssSelector(selector));
            if(click)
            {
                link.click();
            }
            String fileUrl = link.getAttribute("href");
            if(fileUrl != null && !fileUrl.isEmpty())
            {
               filePath = fileUrl.replace("?a=true", "");
            }
        }
        catch (NoSuchElementException nse) { }
        return filePath;
    }

    /**
     * Downloads the document shown by the current page, optionally
     * doing it by clicking the link in the browser (no control) or
     * by doing a URL-based download.
     *
     * @param file          optional file to download to.  When given
     *                      the link <b>will not be clicked</b> but the
     *                      file will be downloaded directly from the server.
     * @return {@link HtmlPage} page response
     */
    public HtmlPage selectDownload(File file)
    {
        boolean click = (file == null);
        String path = downloadFile(DOWNLOAD_FILE_CSS_LOCATOR, click);

        // A file was provided so stream into it
        if (file != null)
        {
            FileDownloader downloader = new FileDownloader(drone);
            try
            {
                downloader.download(path, file);
            }
            catch (Exception e)
            {
                throw new PageException("Unable to download file", e);
            }
        }
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Get the file size from the properties
     * section of the document details page.
     * @return String size
     */
    public String getDocumentSize()
    {
        WebElement fileSize = drone.find(By.cssSelector(DOCUMENT_PROPERTIES_DISPLAYED_SIZE));
        return fileSize.getText();
    }


    /**
     * This test case needs flash player installed on linux box and till that it will be disabled.
     * Gets the Preview status on the document page.
     *
     * @return boolean
     */
    public boolean isPreviewDisplayed() {
        try
        {
            return drone.findAndWait(By.cssSelector(DOCUMENT_PREVIEW_WITH_FLASH_PLAYER)).isDisplayed();
        }
        catch (TimeoutException nse)
        {
            try
            {
                return drone.find(By.cssSelector(DOCUMENT_PREVIEW_WITHOUT_FLASH_PLAYER)).isDisplayed();
            }
            catch (TimeoutException e) { }
        }

        return false;
    }

    /**
     * Gets the No Preview Message on the document page.
     *
     * @return boolean
     */
    public boolean isNoPreviewMessageDisplayed()
    {
        try
        {
            WebElement noPreviewElement = drone.findAndWait(By.cssSelector(NO_DOCUMENT_PREVIEW));

            if (noPreviewElement != null)
            {
                String message = noPreviewElement.getText();

                if(message != null && (message.contains(DOCUMENT_CANT_BE_PREVIEWED) || message.contains(FILE_ISNT_VISIBLE)))
                {
                    return true;
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Preview Message is not displayed", nse);
            }
        }
        catch (TimeoutException tme)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Preview Message is not displayed", tme);
            }
        }
        return false;
    }

    /**
     * Clicks on download link when no preview message is displayed for
     * unsupported details.
     *
     * @return {@link DocumentDetailsPage}
     */
    public DocumentDetailsPage clickOnDownloadLinkForUnsupportedDocument() {

		try {
			WebElement noPreviewElement = drone.findAndWait(By
					.cssSelector(NO_DOCUMENT_PREVIEW));
			noPreviewElement.findElement(
					By.linkText(CLICK_HERE_TO_DOWNLOAD_LINK)).click();
		} catch (Exception exception) {
			logger.error("Not able to find the web element: details can't be Previewed");
			throw new PageException("Unable to find option for Download Link",
					exception);
		}

		return new DocumentDetailsPage(drone);
    }

    /**
     * Mimics the action of selecting the Sync to Cloud icon on the document
     * page.
     *
     * @return If Cloud Sync has not been set up yet, returns {@link DocumentDetailsPage} else it returns {@link DestinationAndAssigneePage}
     */
    public HtmlPage selectSyncToCloud()
    {
        drone.findAndWait(By.cssSelector(SYNC_TO_CLOUD)).click();
        if(isSignUpDialogVisible())
        {
            return new CloudSignInPage(drone);
        }
        else
        {
            return new DestinationAndAssigneePage(drone);
        }
    }

    /**
     * Mimics the action of selecting the Sync to Cloud icon on the document page.
     * @param isCloudSyncSetUp
     * @return  If Cloud Sync has not been set up yet, returns {@link DocumentDetailsPage} else it returns {@link DestinationAndAssigneePage}
     */
    public HtmlPage selectSyncToCloud(boolean isCloudSyncSetUp)
    {
        if(isCloudSyncSetUp)
        {
            drone.findAndWait(By.cssSelector(SYNC_TO_CLOUD)).click();
            return new DestinationAndAssigneePage(drone).render();
        }
        else
        {
            return selectSyncToCloud();
        }
    }

    /**
     * Gets the No Preview Message on the document page.
     *
     * @return boolean
     */
    public boolean isSignUpDialogVisible()
    {
        try
        {
            return drone.findAndWait(By.cssSelector("form.cloud-auth-form"), WAIT_TIME_3000).isDisplayed();
        }
        catch (TimeoutException te)
        {
             return false;
        }

    }

    /**
     * Checks whether the cloud destination folder to sync page is displayed.
     *
     * @return boolean
     */
    public boolean isDestAndAssigneeVisible()
    {
        try
        {
            return drone.findAndWait(By.cssSelector("div[id$='_default-cloud-folder-dialog']")).isDisplayed();
        }
        catch (TimeoutException te)
        {
            return false;
        }
    }

    /**
     * Mimics the action of selecting the UnSync from Cloud icon on the document
     * page.
     *
     * @return {@link DocumentDetailsPage}
     */
    public  DocumentDetailsPage selectUnSyncFromCloud()
    {
        try
        {
            drone.findAndWait(By.cssSelector(UNSYNC_FROM_CLOUD)).click();
            drone.waitForElement(By.cssSelector("div#prompt_h"), maxPageLoadingTime);
            drone.findAndWait(By.cssSelector("input#requestDeleteRemote")).click();
            drone.findAndWait(By.cssSelector("span.button-group>span[class$='yui-push-button']")).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find element");
        }
        return new DocumentDetailsPage(drone);
    }

    /**
     * Method to check if the file synced to the Cloud
     * @return true if "UnSync From Cloud" icon is displayed
     */
    public boolean isFileSyncSetUp(){
        try
        {
            drone.findAndWait(By.cssSelector(UNSYNC_FROM_CLOUD)).isDisplayed();
            return true;
        }
        catch (TimeoutException te)
        {
            return false;
        }
    }

    /**
     * Mimics the action of clicking on the assign workflow link.
     *
     * @return StartWorkFlowPage page response object
     */
    public StartWorkFlowPage selectStartWorkFlowPage()
   {
        try
        {
            drone.findAndWait(By.cssSelector("div.document-assign-workflow>a")).click();
            return new StartWorkFlowPage(drone);
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element" + exception);
        }
        throw new PageException("Unable to find assign workflow.");
    }

    /**
     * Mimics the action of clicking on the Start workflow icon in WorkFlow section.
     *
     * @return {@link StartWorkFlowPage} page response object
     */
    public HtmlPage selectStartWorkFlowIcon()
    {
        try
        {
            drone.findAndWait(By.cssSelector("a[name='.onAssignWorkflowClick']")).click();
            return new StartWorkFlowPage(drone);
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element" + exception);
        }
        throw new PageException("Unable to find assign workflow.");
    }

    /**
     * Checks weather the "Sync to Cloud" option is displayed
     * @return boolean
     */
    public boolean isSyncToCloudOptionDisplayed()
    {
        try
        {
            drone.find(By.cssSelector(SYNC_TO_CLOUD)).isDisplayed();
            return true;
        }
        catch (NoSuchElementException te)
        {
            return false;
        }
    }

    /**
     * Public method to transfer control based to google docs based on how the
     * session gets initiated.
     *
     * @author sprasanna
     * @return {@link HtmlPage} page response
     */
    public HtmlPage editInGoogleDocs()
    {
        WebElement link = drone.findAndWait(By.cssSelector(LINK_EDIT_IN_GOOGLE_DOCS));
        link.click();

        By jsMessage = By.cssSelector("div.bd>span.message");

        //TODO Remove try Catch Block Once Cloud version in 31
        try
        {
            String text = "Editing in Google Docs";
            drone.waitUntilVisible(jsMessage, text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            drone.waitUntilNotVisibleWithParitalText(jsMessage, text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (TimeoutException timeoutException)
        {

        }

        if (!drone.getCurrentUrl().contains(GOOGLE_DOCS_URL))
        {
            return new GoogleDocsAuthorisation(drone, documentVersion, isGoogleCreate);
        }
        else
        {
            String errorMessage = "";
            try
            {
                errorMessage = drone.find(By.cssSelector("div.bd>span.message")).getText();
            }
            catch (NoSuchElementException e)
            {
                return new EditInGoogleDocsPage(drone, documentVersion, isGoogleCreate);
            }

            throw new PageException(errorMessage);
        }
    }

    /**
     * Public method to transfer control based to google docs based on how the
     * session gets initiated.
     *
     * @author sprasanna
     * @return {@link HtmlPage} page response
     */
    public HtmlPage resumeEditInGoogleDocs()
    {
        WebElement link = drone.findAndWait(By.cssSelector(LINK_RESUME_EDIT_IN_GOOGLE_DOCS));
        link.click();
        drone.waitUntilElementDeletedFromDom(By.cssSelector("span[class='message']"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        if (!drone.getCurrentUrl().contains(GOOGLE_DOCS_URL))
        {
            return new GoogleDocsAuthorisation(drone, documentVersion, isGoogleCreate);
        }
        else
        {
            return new EditInGoogleDocsPage(drone, documentVersion, isGoogleCreate);
        }
    }

    /**
     * Verify if Link Edit in Google docs is visible.
     *
     * @author sprasanna
     * @return true if displayed
     */
    public boolean isEditInGoogleDocsLinkVisible()
    {
        try
        {
            return drone.find(By.cssSelector(LINK_EDIT_IN_GOOGLE_DOCS)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Verify if Link Resume Editing in Google docs is visible.
     *
     * @author sprasanna
     * @return true if displayed
     */
    public boolean isResumeEditingInGoogleDocsLinkVisible()
    {
        try
        {
            return drone.find(By.cssSelector(LINK_RESUME_EDIT_IN_GOOGLE_DOCS)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Verify if the action to edit offline is available
     *
     * @return true if visible on the page
     */
    public boolean isInlineEditLinkDisplayed()
    {
        try
        {
            return drone.find(By.cssSelector(INLINE_EDIT_LINK)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Verify if the comment count is displayed or not
     * @param waitTime
     * @return
     */
    public boolean isCommentCountPresent(long waitTime)
    {
        try
        {
            return drone.findAndWaitWithRefresh(By.cssSelector(COMMENT_COUNT), waitTime).isDisplayed();
        }
        catch (TimeoutException e)
        {
            if(logger.isInfoEnabled())
            {
                logger.info("Comment count is not displayed");
            }
        }
        return false;
    }

    /**
     * Checks if hide record link is displayed.
     * This will only be visible under the following
     * condition:
     * <ul>
     *  <li> Record management module enabled</li>
     *  <li> When the document has been declared as record</li>
     * </ul>
     * @return true if link is displayed
     */
    public boolean isHideRecordLinkDisplayed()
    {
        try
        {
            return drone.find(By.cssSelector("div#onHideRecordAction.rm-hide-record")).isDisplayed();
        }
        catch (NoSuchElementException nse) { }
        return false;
    }

    /**
     * Method to get Sync Status
     * @return
     */
    public String getSyncStatus()
    {
        try
        {
            WebElement location = getDrone().findAndWait(By.cssSelector(SYNC_STATUS));
            return location.getText();
        }
        catch (TimeoutException te)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Sync Status element is not visible");
            }
        }
        return "";
    }

    /**
     * Method to get location in cloud
     * @return
     */
    public String getLocationInCloud()
    {
        try
        {
            WebElement location = getDrone().findAndWait(By.cssSelector(LOCATION_IN_CLOUD));
            return location.getText().split("\n")[0];
        }
        catch (TimeoutException te)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Cloud location element is not visible");
            }
        }
        return "";
    }

    /**
     * Method to check if "Request To Sync" icon is displayed in DocumentDetails page
     * @return
     */
    public boolean isRequestSyncIconDisplayed()
    {
        try
        {
            return getDrone().find(By.cssSelector(REQUEST_SYNC_ICON)).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Request to Sync icon is not displayed");
            }
        }
        return false;
    }

    /**
     * Method to revert the document to the specified version.
     * @param versionNumber revision number to revert
     * @return {@link HtmlPage} page response.
     */
    public HtmlPage selectRevertToVersion(String versionNumber)
    {
        try
        {
            Double.parseDouble(versionNumber);
        }
        catch(NumberFormatException e)
        {
            throw new IllegalArgumentException("Version number passed is not a number : " + versionNumber, e);
        }
        try
        {
            drone.find(By.cssSelector("a[rel='"+versionNumber+"']")).click();
            return new RevertToVersionPage(drone, versionNumber, false);
        }
        catch (NoSuchElementException nse)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Revert to version button for " + versionNumber + " is not displayed");
            }
        }
        throw new PageException("Revert to version button for " + versionNumber + " is not displayed");
    }

    /**
     * Method to verify if a document is part of workflow or not
     * @return True if it is part of a Workflow
     */
    public boolean isPartOfWorkflow()
    {
        try
        {
            String workFlowInfo = drone.find(WORKFLOW_INFO).getText();
            if(workFlowInfo.equals("This document is part of the following workflow(s):"))
            {
                return true;
            }
        }
        catch (NoSuchElementException nse) {}
        return false;
    }

    /**
     * Opens the "Copy this link to share the current page" in the new tab
     *
     * @return {@link DocumentDetailsPage}
     */
    public DocumentDetailsPage openCopyThisLinkInNewTab() {

		try {
			WebElement copyThisLink = drone.findAndWait(By.cssSelector(COPY_THIS_LINK_TO_SHARE_THE_CURRENT_PAGE));
			drone.createNewTab();
			drone.navigateTo(copyThisLink.getAttribute("value"));
		} catch (TimeoutException exception) {
			logger.error("Not able to find the web element: Copy This Link To Share The Current Page ");
			throw new PageException("Unable to find  Copy This Link To Share The Current Page ", exception);
		}

		return new DocumentDetailsPage(drone);
    }

    public String getExpectedVersion()
	{
        return expectedVersion;
	}
	public synchronized void setExpectedVersion(String expectedVersion)
	{
        this.expectedVersion = expectedVersion;
	}


    /**
     * Selects the Change Type link from Folder actions.
     *
     * @return {@link ChangeTypePage} response
     */
    public ChangeTypePage selectChangeType()
    {
        if (alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("Operation available only for Enterprise version");
        }
        By changeTypeLink;
        switch (alfrescoVersion)
        {
            case Enterprise41:
                changeTypeLink = By.cssSelector("div.document-change-type a");
                break;

            default:
                changeTypeLink = By.cssSelector("div#onActionChangeType a");
                break;
        }
        drone.findAndWait(changeTypeLink).click();
        return new ChangeTypePage(drone);
    }

    /**
     * Method to download the document to the specified version.
     * @param versionNumber revision number
     */
    public void selectDownloadPreviousVersion(String versionNumber)
    {
        WebElement downloadButton = drone.findAndWait(By.cssSelector("a[rel='"+versionNumber+"'] + a.download"));
        downloadButton.click();
        // Assumes driver capability settings to save file in a specific location when
    }


}