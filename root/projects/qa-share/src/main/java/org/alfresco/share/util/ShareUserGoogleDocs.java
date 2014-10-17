package org.alfresco.share.util;

import org.alfresco.po.share.*;
import org.alfresco.po.share.site.document.*;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;

public class ShareUserGoogleDocs extends AbstractCloudSyncTest
{
    public static String googleURL = "https://accounts.google.com";
    public static String googlePlusURL = "https://plus.google.com";

    private static Log logger = LogFactory.getLog(ShareUserGoogleDocs.class);

    public ShareUserGoogleDocs()
    {
        if (logger.isTraceEnabled())
        {
            logger.debug(this.getClass().getSimpleName() + " instantiated");
        }
    }

    /**
     * This method provides the user to login into edit google docs page through
     * google authorization.
     * 
     * @param drone
     * @return EditInGoogleDocsPage
     */
    public static EditInGoogleDocsPage signIntoEditGoogleDocFromDetailsPage(WebDrone drone)
    {
        DocumentDetailsPage detailsPage = ShareUser.getSharePage(drone).render();
        detailsPage.render();
        GoogleDocsAuthorisation googleAuthorisationPage = detailsPage.editInGoogleDocs().render();
        return signInGoogleDocs(googleAuthorisationPage);
    }

    /**
     * This method provides the user to login into edit google docs page through
     * google authorization.
     *
     * @param drone
     * @return EditInGoogleDocsPage
     */
    public static EditInGoogleDocsPage signIntoResumeEditGoogleDocFromDetailsPage(WebDrone drone)
    {
        DocumentDetailsPage detailsPage = ShareUser.getSharePage(drone).render();
        detailsPage.render();
        GoogleDocsAuthorisation googleAuthorisationPage = detailsPage.resumeEditInGoogleDocs().render();
        return signInGoogleDocs(googleAuthorisationPage);
    }

    /**
     * This method provides edit google docs page.
     * 
     * @param drone
     * @return EditInGoogleDocsPage
     */
    public static EditInGoogleDocsPage openEditGoogleDocFromDetailsPage(WebDrone drone)
    {
        DocumentDetailsPage detailsPage = ShareUser.getSharePage(drone).render();
        detailsPage.render();
        EditInGoogleDocsPage googleDocsPage = detailsPage.editInGoogleDocs().render();
        return googleDocsPage.render();
    }

    /**
     * This method provides the user to login into create google docs with given
     * filename through google authorization. And Saves the document return to
     * the document library page. User should be already logged
     * 
     * @param drone
     * @param fileName
     * @param contentType
     * @return DocumentLibraryPage
     * @throws Exception
     */
    public static DocumentLibraryPage createAndSavegoogleDocBySignIn(WebDrone drone, String fileName, ContentType contentType) throws Exception
    {
        DocumentLibraryPage docLibPage = ShareUser.getSharePage(drone).render();

        GoogleDocsAuthorisation googleAuthorisationPage = docLibPage.getNavigation().selectCreateContent(contentType).render();
        googleAuthorisationPage.render();

        EditInGoogleDocsPage googleDocsPage = signInGoogleDocs(googleAuthorisationPage);

        googleDocsPage = renameGoogleDocName(fileName, googleDocsPage);

        docLibPage = googleDocsPage.selectSaveToAlfresco().render();

        return docLibPage.render();
    }

    /**
     * This method provides the user to edit google docs name with the given
     * name.
     * 
     * @param fileName
     * @param googleDocsPage
     * @return EditInGoogleDocsPage
     */
    public static EditInGoogleDocsPage renameGoogleDocName(String fileName, EditInGoogleDocsPage googleDocsPage)
    {
        GoogleDocsRenamePage renameDocs = googleDocsPage.renameDocumentTitle().render();
        return renameDocs.updateDocumentName(fileName).render();
    }

    /**
     * Saving the google doc with the minor version and if isCreate boolean
     * value is true for saving the new google doc otherwise existing google
     * doc.
     * 
     * @param drone
     * @param isCreateDoc
     * @return SharPage
     */
    public static SharePage saveGoogleDoc(WebDrone drone, boolean isCreateDoc)
    {
        EditInGoogleDocsPage googleDocsPage = ShareUser.getSharePage(drone).render();
        googleDocsPage.setGoogleCreate(isCreateDoc);
        GoogleDocsUpdateFilePage googleUpdatefile = googleDocsPage.selectSaveToAlfresco().render();
        googleUpdatefile.render();
        googleUpdatefile.selectMinorVersionChange();
        return googleUpdatefile.submit().render();
    }

    /**
     * Saving the google doc with the minor version and if isCreate boolean
     * value is true for saving the new google doc otherwise existing google
     * doc. Methods used for edition by concurrent user's
     * 
     * @param drone
     * @param isCreateDoc
     * @return SharePage
     */
    public static SharePage saveGoogleDocOtherEditor(WebDrone drone, boolean isCreateDoc)
    {
        EditInGoogleDocsPage googleDocsPage = ShareUser.getSharePage(drone).render();
        googleDocsPage.setGoogleCreate(isCreateDoc);
        GoogleDocsUpdateFilePage googleUpdatefile = googleDocsPage.selectSaveToAlfresco().render();
        googleUpdatefile.render();
        googleUpdatefile.selectMinorVersionChange();
        return googleUpdatefile.submitWithConcurrentEditors().render();
    }

    /**
     * Discarding the changes made in google doc.
     * 
     * @param drone
     * @return SharePage
     */
    public static HtmlPage discardGoogleDocsChanges(WebDrone drone)
    {
        EditInGoogleDocsPage googleDocsPage = ShareUser.getSharePage(drone).render();
        GoogleDocsDiscardChanges googleDocsDiscardChanges = googleDocsPage.selectDiscard().render();
        return googleDocsDiscardChanges.clickOkButton().render();
    }

    /**
     * Discarding the changes made in google doc. Methods used for edition by concurrent user's
     * 
     * @param drone
     * @return HtmlPage
     */
    public static HtmlPage discardGoogleDocsChangesOtherEditor(WebDrone drone)
    {
        EditInGoogleDocsPage googleDocsPage = ShareUser.getSharePage(drone).render();
        GoogleDocsDiscardChanges googleDocsDiscardChanges = googleDocsPage.selectDiscard().render();
        return googleDocsDiscardChanges.clickOkConcurrentEditorButton().render();
    }

    /**
     * This method provides the sign in page to log into google docs.
     * 
     * @param googleAuth
     * @return EditInGoogleDocsPage
     */
    public static EditInGoogleDocsPage signInGoogleDocs(GoogleDocsAuthorisation googleAuth)
    {
        GoogleSignUpPage signUpPage = googleAuth.submitAuth().render();
        return signUpPage.signUp(googleUserName, googlePassword).render();
    }

    /**
     * Saves the google doc with the given comments as minor or major version.
     * 
     * @param drone
     * @param comments
     * @param isMinorVersion
     * @return GoogleDocsUpdateFilePage
     */
    public static GoogleDocsUpdateFilePage saveGoogleDocWithVersionAndComment(WebDrone drone, String comments, boolean isMinorVersion)
    {
        EditInGoogleDocsPage googleDocsPage = drone.getCurrentPage().render();
        GoogleDocsUpdateFilePage googleUpdateFile = googleDocsPage.selectSaveToAlfresco().render();

        if (isMinorVersion)
        {
            googleUpdateFile.selectMinorVersionChange();
        }
        else
        {
            googleUpdateFile.selectMajorVersionChange();
        }

        if (!StringUtils.isEmpty(comments))
        {
            googleUpdateFile.setComment(comments);
        }

        return googleUpdateFile;
    }

    /**
     * This method is used to delete the given user profile.
     * 
     * @param testUser
     * @param drone
     * @return {@link UserSearchPage}
     */
    protected UserSearchPage deleteUser(WebDrone drone, String testUser)
    {
        if (isAlfrescoVersionCloud(drone))
        {
            throw new UnsupportedOperationException("Delete user is available in cloud");
        }
        DashBoardPage dashBoard = drone.getCurrentPage().render();
        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        page = page.searchFor(testUser).render();
        UserProfilePage userProfile = page.clickOnUser(testUser).render();
        return userProfile.deleteUser().render();
    }

    /**
     * Discarding the changes made in google doc.
     *
     * @param filename
     * @return SharePage
     */
    public static GoogleSignUpPage openSignUpPage(WebDrone driver, String filename)
    {
        DocumentLibraryPage docLibPage = driver.getCurrentPage().render();
        GoogleDocsAuthorisation googleAuth = docLibPage.getFileDirectoryInfo(filename).selectEditInGoogleDocs().render();
        Assert.assertTrue(googleAuth.isAuthorisationDisplayed());
        return googleAuth.submitAuth().render();
    }

}