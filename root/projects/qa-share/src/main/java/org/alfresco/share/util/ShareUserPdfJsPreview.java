package org.alfresco.share.util;

import org.alfresco.po.share.preview.PdfJsPlugin;
import org.alfresco.webdrone.WebDrone;
import org.apache.log4j.Logger;

/**
 * Utility with helper methods for pdf.js-based doc previews
 * 
 * @author wabson
 *
 */
public class ShareUserPdfJsPreview extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(ShareUserPdfJsPreview.class);

    // TODO: Pl amend to create a method on DocumentDetailsPage to return the Previewer
    public static PdfJsPlugin preview(WebDrone driver)
    {
        return new PdfJsPlugin(driver);
    }
}
