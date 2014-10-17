package org.alfresco.share.documents.mail;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.application.util.Application;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.ShareLinkPage;
import org.alfresco.po.share.site.document.ViewPublicLinkPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.MailUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.windows.application.MicrosoftOffice2013;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;

public class ShareDocumentEmail extends AbstractUtils
{

    private String testName;
    private String testUser;
    private String siteName;

    private static DocumentLibraryPage documentLibPage;
    MicrosoftOffice2013 outlook = new MicrosoftOffice2013(Application.OUTLOOK, "2013");
    private String pptxFileName_9677;
    private String userCloudEmail;
    private String userCloudPassword;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);
        siteName = getSiteName(testName);

        pptxFileName_9677 = "AONE_9677.pptx";
        userCloudEmail = googleUserName;   // "alfresco.cloud@gmail.com";
        userCloudPassword = googlePassword;  // "eiWei6vieiWei6vi";
    }

    @Test(groups = { "MailShare" })
    public void dataPrep_AONE() throws Exception
    {

        // Create normal User
        String[] testUser2 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        // login with user
        ShareUser.login(drone, testUser);

        // // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Any office document was saved to the site document library;
        File file = new File(DATA_FOLDER + SLASH + pptxFileName_9677);
        ShareUserSitePage.uploadFile(drone, file).render();
    }

    @Test(groups = "alfresco-one")
    public void AONE_14086() throws Exception
    {
        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(documentLibPage.isFileVisible(pptxFileName_9677), "The saved document is not displayed.");

        FileDirectoryInfo fileInfo1 = ShareUserSitePage.getFileDirectoryInfo(drone, pptxFileName_9677);

        // 1. Click on Share document link and choose e-mail icon.
        ShareLinkPage shareLinkPage = (ShareLinkPage) fileInfo1.clickShareLink().render();
        assertTrue(shareLinkPage.isEmailLinkPresent());
        shareLinkPage.clickEmailLink();
        
        // New mail form of the e-mail client is opened.
        String outlookName = "File Shared from Alfresco";
        String name = outlook.getAbstractUtil().findWindowName(outlookName);
        assertTrue(name.contains("File Shared from Alfresco"), "Outlook window " + name + " not found.");

        // 2. Leave all default data, enter e-mail address of User2 and click send.
        // e-mail is successfully sent.
        Ldtp l1 = outlook.getAbstractUtil().setOnWindow(outlookName);
        l1.enterString("txtTo", userCloudEmail);
        l1.mouseLeftClick("btnSend");

        // 3. Verify the received e-mail.
        // e-mail message with the following content is received:
        // Subject:
        // File Shared from Alfresco
        //
        // Body:
        // File test1.txt shared from Alfresco, look at it here: http://localhost:8080/share/s/S5gHOI0RSACPOVw9yOhxCg
        String emailSubject = "File Shared from Alfresco";
        String expectedContent = "File " + pptxFileName_9677 + " shared from Alfresco, look at it here:";
        String emailContent = MailUtil.checkGmail(userCloudEmail, userCloudPassword, emailSubject);
        Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");
        Assert.assertTrue(emailContent.contains(expectedContent), "Actual email: " + emailContent + " Expected: " + expectedContent);

        // 4. Click on the link in the message.
        Pattern p = Pattern.compile("<a href=\".*\">");
        Matcher m = p.matcher(emailContent);
        if (m.find())
        {
            String fileLink = m.group().replace("<a href=\"", "").replace("\">", "");
            drone.createNewTab();
            drone.navigateTo(fileLink);
            
            // 4. A new page is opened in the browser. It contains preview of the document and a Document Details button on the top right corner.
            ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);
            viewPublicLinkPage.render();

            // verify that the page contains the document
            assertEquals(viewPublicLinkPage.getContentTitle(), pptxFileName_9677);
            assertTrue(viewPublicLinkPage.isDocumentViewDisplayed());

            // verify button Document Details
            assertEquals(viewPublicLinkPage.getButtonName(), "Document Details");

        }
        else
            Assert.fail("File link shared from Alfresco was not found in email");
        
    }
}
