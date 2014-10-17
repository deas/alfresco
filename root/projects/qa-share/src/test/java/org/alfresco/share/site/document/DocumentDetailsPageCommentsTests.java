package org.alfresco.share.site.document;

import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.BOLD;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.BULLET;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.ITALIC;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.NUMBER;
import static org.alfresco.po.share.site.document.TinyMceEditor.FormatType.UNDERLINED;
import static org.testng.Assert.assertEquals;

import org.alfresco.po.share.enums.TinyMceColourCode;
import org.alfresco.po.share.site.document.AddCommentForm;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Bogdan.Bocancea
 */

@Listeners(FailedTestListener.class)
public class DocumentDetailsPageCommentsTests extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(DocumentDetailsActionsTest.class);
    protected String testUser;
    protected String siteName = "";
    String fileName;
    String fontStyle = "style=\"color: rgb(0, 0, 255);\"";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_AONE_12352() throws Exception
    {

        String testName = getTestName();
        String fileName = getFileName(testName) + ".txt";
        String siteName = getSiteName(testName);

        // Create User
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();
        ShareUser.openSiteDashboard(drone, siteName).render();

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_12352()
    {
        DocumentLibraryPage documentLibraryPage;
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String testUser = getUserNameFreeDomain(testName);
        String textBold = "Bold Text";
        String textItalic = "Italic Text";
        String textUnderline = "Underline Text";
        String textBullet = "Bullet Text";
        String textNumber = "Number Text";
        String colorText = "Color Text";
        String allFormats = "All in one";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // open site document library page
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // select the file
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);

        // click add comment
        AddCommentForm addCommentForm = detailsPage.clickAddCommentButton();

        // ---- Step 1 ----
        // ---- Step action ---
        // Type any text and make it bold;
        // ---- Expected results ----
        // The text is marked as bold;
        TinyMceEditor tinyMceEditor = addCommentForm.getTinyMceEditor();
        tinyMceEditor.setText(textBold);
        tinyMceEditor.clickTextFormatter(BOLD);
        assertEquals(tinyMceEditor.getContent(), String.format("<p><strong>%s</strong></p>", textBold), "The text didn't mark as bold.");
        addCommentForm.clickAddCommentButton().render();
        String boldHTML = detailsPage.getCommentHTML(textBold);
        assertEquals(boldHTML, String.format("<p><strong>%s</strong></p>", textBold), "The text didn't mark as bold.");

        // ---- Step 2 ----
        // ---- Step action ---
        // Type any text and make it italic
        // ---- Expected results ----
        // The text is italic;
        addCommentForm = detailsPage.clickAddCommentButton();
        tinyMceEditor.setText(textItalic);
        tinyMceEditor.clickTextFormatter(ITALIC);
        assertEquals(tinyMceEditor.getContent(), String.format("<p><em>%s</em></p>", textItalic), "The text didn't italic.");
        addCommentForm.clickAddCommentButton().render();
        assertEquals(detailsPage.getCommentHTML(textItalic), String.format("<p><em>%s</em></p>", textItalic), "The text didn't mark as italic.");

        // ---- Step 3 ----
        // ---- Step action ---
        // Type any text and make it underlined;
        // ---- Expected results ----
        // The text is marked as underlined;
        addCommentForm = detailsPage.clickAddCommentButton();
        tinyMceEditor.setText(textUnderline);
        tinyMceEditor.clickTextFormatter(UNDERLINED);
        assertEquals(tinyMceEditor.getContent(), String.format("<p><span style=\"text-decoration: underline;\">%s</span></p>", textUnderline),
                "The text didn't underlined.");
        addCommentForm.clickAddCommentButton().render();
        assertEquals(detailsPage.getCommentHTML(textUnderline), String.format("<p><span style=\"text-decoration: underline;\">%s</span></p>", textUnderline),
                "The text didn't mark as underlined.");

        // ---- Step 4 ----
        // ---- Step action ---
        // Type any text and make list
        // ---- Expected results ----
        // List is displayed;
        addCommentForm = detailsPage.clickAddCommentButton();
        tinyMceEditor.setText(textBullet);
        tinyMceEditor.clickTextFormatter(BULLET);
        assertEquals(tinyMceEditor.getContent(), String.format("<ul style=\"\"><li>%s</li></ul>", textBullet), "List didn't display.");
        addCommentForm.clickAddCommentButton().render();
        assertEquals(detailsPage.getCommentHTML(textBullet), String.format("<ul>\n<li>%s</li>\n</ul>", textBullet), "The text didn't mark as list.");

        // ---- Step 5 ----
        // ---- Step action ---
        // Type any text and make numbered list;
        // ---- Expected results ----
        // Numbered list is displayed;
        addCommentForm = detailsPage.clickAddCommentButton();
        tinyMceEditor.setText(textNumber);
        tinyMceEditor.clickTextFormatter(NUMBER);
        assertEquals(tinyMceEditor.getContent(), String.format("<ol style=\"\"><li>%s</li></ol>", textNumber), "Numbered list didn't display.");
        addCommentForm.clickAddCommentButton().render();
        assertEquals(detailsPage.getCommentHTML(textNumber), String.format("<ol>\n<li>%s</li>\n</ol>", textNumber), "The text didn't mark as list.");

        // ---- Step 6 ----
        // ---- Step action ---
        // Type any text and highlight it with any color;
        // ---- Expected results ----
        // The text is highlighted with any color;
        addCommentForm = detailsPage.clickAddCommentButton();
        tinyMceEditor.setText(colorText);
        tinyMceEditor.clickColorCode(TinyMceColourCode.BLUE);
        assertEquals(tinyMceEditor.getContent(), String.format("<p><span style=\"color: rgb(0, 0, 255);\">%s</span></p>", colorText),
                "Text didn't get colored.");
        addCommentForm.clickAddCommentButton().render();
        assertEquals(detailsPage.getCommentHTML(colorText), String.format("<p><span style=\"color: #0000ff;\">%s</span></p>", colorText),
                "Text didn't get colored.");

        // ---- Step 7 ----
        // ---- Step action ---
        // Click Create button;
        // ---- Expected results ----
        /**
         * Comment is successfully created; The formatting is successfully displayed:
         * - the text is marked as bold;
         * - the text is italic;
         * - the text is marked as underlined;
         * - the list is displayed;
         * - the numbered lIst is displayed;
         * - the text is highlighted with any color;
         */
        addCommentForm = detailsPage.clickAddCommentButton();
        addCommentForm.clickAddCommentButton();
        tinyMceEditor.setText(allFormats);
        tinyMceEditor.clickTextFormatter(BOLD);
        tinyMceEditor.clickTextFormatter(ITALIC);
        tinyMceEditor.clickTextFormatter(UNDERLINED);
        tinyMceEditor.clickTextFormatter(NUMBER);
        tinyMceEditor.clickColorCode(TinyMceColourCode.BLUE);
        addCommentForm.clickAddCommentButton().render();
        assertEquals(detailsPage.getCommentHTML(allFormats), String.format(
                "<ol>\n<li><span style=\"text-decoration: underline; color: #0000ff;\"><em><strong>%s</strong></em></span></li>\n</ol>", allFormats));

    }
}
