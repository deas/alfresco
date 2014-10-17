package org.alfresco.share.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sergey Kardash
 */
public class GoogleDriveUtil extends AbstractUtils
{

    private static final Log logger = LogFactory.getLog(GoogleDriveUtil.class);

    /**
     * Build and returns a Drive service object authorized with the service accounts
     * that act on behalf of the given user.
     * 
     * @return Drive service object that is ready to make requests.
     */
    public static Drive getDriveService(String googleServiceAccountEmail, String googleEmail, String googlePKCS12FileName) throws GeneralSecurityException,
            IOException
    {
        Drive service = null;
        try
        {
            HttpTransport httpTransport = new NetHttpTransport();
            JacksonFactory jsonFactory = new JacksonFactory();
            GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(jsonFactory)
                    .setServiceAccountId(googleServiceAccountEmail).setServiceAccountScopes(Arrays.asList(DriveScopes.DRIVE))
                    .setServiceAccountUser(googleEmail).setServiceAccountPrivateKeyFromP12File(new java.io.File(googlePKCS12FileName)).build();
            service = new Drive.Builder(httpTransport, jsonFactory, null).setHttpRequestInitializer(credential).build();
        }
        catch (GeneralSecurityException e)
        {
            throw new GeneralSecurityException(e.getMessage());
        }
        catch (IOException e)
        {
            throw new IOException(e.getMessage());
        }

        logger.info("Get service via Drive API");
        return service;
    }

    /**
     * Retrieve a list of File resources.
     * 
     * @param service Drive API service instance.
     * @return List of File resources.
     */
    public static List<File> retrieveAllFiles(Drive service) throws IOException
    {
        List<File> result = new ArrayList<>();
        Drive.Files.List request = service.files().list();

        do
        {
            try
            {
                FileList files = request.execute();

                result.addAll(files.getItems());
                request.setPageToken(files.getNextPageToken());
            }
            catch (IOException e)
            {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        }
        while (request.getPageToken() != null && request.getPageToken().length() > 0);

        logger.info("Retrieve all files via Drive API");
        return result;
    }

    /**
     * Insert a new permission.
     * 
     * @param service Drive API service instance.
     * @param fileId ID of the file to insert permission for.
     * @param value User or group e-mail address, domain name or {@code null} "default" type.
     * @param type The value "user", "group", "domain" or "default".
     * @param role The value "owner", "writer" or "reader".
     * @return The inserted permission if successful, {@code null} otherwise.
     */
    public static Permission insertPermission(Drive service, String fileId, String value, String type, String role)
    {
        Permission newPermission = new Permission();

        newPermission.setValue(value);
        newPermission.setType(type);
        newPermission.setRole(role);
        try
        {
            return service.permissions().insert(fileId, newPermission).execute();
        }
        catch (IOException e)
        {
            System.out.println("An error occurred: " + e);
            logger.info("insert Permission for Drive API. An error occurred: " + e);
        }

        logger.info("insert Permission via Drive API.");
        return null;
    }

    public static File updateFile(Drive service, String fileId, String newTitle, String newDescription, String newMimeType, String newFilename)
    {
        java.io.File fileContent;
        try
        {
            // First retrieve the file from the API.
            File file = service.files().get(fileId).execute();

            // File's new metadata.
            if (newTitle != null)
            {
                file.setTitle(newTitle);
            }
            if (newDescription != null)
            {
                file.setDescription(newDescription);
            }
            file.setMimeType(newMimeType);

            // File's new content.
            fileContent = new java.io.File(newFilename);
            FileContent mediaContent = new FileContent(newMimeType, fileContent);

            logger.info("update File via Drive API.");
            // Send the request to the API.
            return service.files().update(fileId, file, mediaContent).execute();
        }
        catch (IOException e)
        {
            System.out.println("An error occurred: " + e);
            logger.info("update File via Drive API. An error occurred: " + e);
            return null;
        }
    }

    public static String getFileID(Drive service, String filename)
    {
        String fileId = null;
        List<File> filesList = null;
        try
        {
            filesList = retrieveAllFiles(service);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (filesList != null)
        {
            for (File aFilesList : filesList)
            {

                if (aFilesList.getTitle().equals(filename))
                {
                    fileId = aFilesList.getId();
                    break;
                }
            }
        }
        logger.info("get ID for File via Drive API.");
        return fileId;
    }

}
