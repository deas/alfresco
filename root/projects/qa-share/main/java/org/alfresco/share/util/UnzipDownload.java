/**
 * 
 */
package org.alfresco.share.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class does the unzip of the downloaded archieve.
 * 
 * @author cbairaajoni
 * 
 */
public class UnzipDownload
{

    private static Log logger = LogFactory.getLog(UnzipDownload.class);

    /**
     * This method is used to unzip and extract the folder or files of the downloaded archieve 
     * 
     * @param zipFile input zip file
     * @param output zip file output folder
     * @return boolean : This is true if unzip is successful, otherwise false.
     */
    public boolean unzip(String zipFile, String outputFolder)
    {
        logger.info("Extracting the file : " + zipFile + " to " + outputFolder);

        String fileName = null;
        File newFile = null;
        FileOutputStream fos = null;
        byte[] buffer = new byte[1024];

        try
        {
            // get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));

            // get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            if (ze == null)
            {
                logger.error("There is no file:" + zipFile + " present to extract.");
                return false;
            }

            while (ze != null)
            {
                fileName = ze.getName();
                newFile = new File(outputFolder + File.separator + fileName);

                logger.info("file unzip : " + newFile.getAbsoluteFile());

                // Creating the folders of zip
                if (ze.isDirectory())
                {
                    (new File(newFile.getAbsolutePath())).mkdir();
                    ze = zis.getNextEntry();
                    continue;
                }

                // creating the files of zip
                fos = new FileOutputStream(newFile);
                int len;

                while ((len = zis.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            logger.info("Extracting the file : " + zipFile + " done.");
            zis.closeEntry();
            zis.close();

            return true;
        }
        catch (Exception e)
        {
            logger.error("Error in unzip() :" + zipFile + e);
            return false;
        }
    }
}