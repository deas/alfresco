package org.alfresco.share.util;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UserProfilePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditInGoogleDocsPage;
import org.alfresco.po.share.site.document.GoogleDocsAuthorisation;
import org.alfresco.po.share.site.document.GoogleDocsDiscardChanges;
import org.alfresco.po.share.site.document.GoogleDocsRenamePage;
import org.alfresco.po.share.site.document.GoogleDocsUpdateFilePage;
import org.alfresco.po.share.site.document.GoogleSignUpPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ShareUserGoogleDocs extends AbstractCloudSyncTest
{
    protected String googleURL = "https://accounts.google.com";
    protected String googlePlusURL = "https://plus.google.com";

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
    protected EditInGoogleDocsPage signIntoEditGoogleDocFromDetailsPage(WebDrone drone)
    {
        DocumentDetailsPage detailsPage = ((DocumentDetailsPage) ShareUser.getSharePage(drone)).render();
        GoogleDocsAuthorisation googleAuthorisationPage = detailsPage.editInGoogleDocs().render();
        return signInGoogleDocs(googleAuthorisationPage);
    }

    /**
     * This method provides edit google docs page.
     * 
     * @param drone
     * @return EditInGoogleDocsPage
     */
    protected EditInGoogleDocsPage openEditGoogleDocFromDetailsPage(WebDrone drone)
    {
        DocumentDetailsPage detailsPage = (DocumentDetailsPage) ShareUser.getSharePage(drone);
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
    protected DocumentLibraryPage createAndSavegoogleDocBySignIn(WebDrone drone, String fileName, ContentType contentType) throws Exception
    {
        DocumentLibraryPage docLibPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);
        docLibPage.render();

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
    protected EditInGoogleDocsPage renameGoogleDocName(String fileName, EditInGoogleDocsPage googleDocsPage)
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
    protected SharePage saveGoogleDoc(WebDrone drone, boolean isCreateDoc)
    {
        EditInGoogleDocsPage googleDocsPage = ((EditInGoogleDocsPage) ShareUser.getSharePage(drone)).render();
        googleDocsPage.setGoogleCreate(isCreateDoc);
        GoogleDocsUpdateFilePage googleUpdatefile = googleDocsPage.selectSaveToAlfresco().render();
        googleUpdatefile.render();
        googleUpdatefile.selectMinorVersionChange();
        return googleUpdatefile.submit().render();
    }

    /**
     * Discarding the changes made in google doc.
     * 
     * @param drone
     * @return SharePage
     */
    protected SharePage discardGoogleDocsChanges(WebDrone drone)
    {
        EditInGoogleDocsPage googleDocsPage = (EditInGoogleDocsPage) ShareUser.getSharePage(drone);
        GoogleDocsDiscardChanges googleDocsDiscardChanges = googleDocsPage.selectDiscard();
        googleDocsDiscardChanges.render();
        googleDocsDiscardChanges.clickOkButton();
        return drone.getCurrentPage().render();
    }

    /**
     * This method provides the sign in page to log into google docs.
     * 
     * @param googleAuth
     * @return EditInGoogleDocsPage
     */
    protected EditInGoogleDocsPage signInGoogleDocs(GoogleDocsAuthorisation googleAuth)
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
    protected GoogleDocsUpdateFilePage saveGoogleDocWithVersionAndComment(WebDrone drone, String comments, boolean isMinorVersion)
    {
        EditInGoogleDocsPage googleDocsPage = (EditInGoogleDocsPage) ShareUser.getSharePage(drone);
        GoogleDocsUpdateFilePage googleUpdatefile = googleDocsPage.selectSaveToAlfresco().render();
        googleUpdatefile.render();

        if (isMinorVersion)
        {
            googleUpdatefile.selectMinorVersionChange();
        } else
        {
            googleUpdatefile.selectMajorVersionChange();
        }

        if (!StringUtils.isEmpty(comments))
        {
            googleUpdatefile.setComment(comments);
        }

        return googleUpdatefile;
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
        DashBoardPage dashBoard = (DashBoardPage) drone.getCurrentPage();
        UserSearchPage page = (UserSearchPage) dashBoard.getNav().getUsersPage();
        page = page.searchFor(testUser).render();
        UserProfilePage userProfile = page.clickOnUser(testUser).render();
        return userProfile.deleteUser().render();
    }
}