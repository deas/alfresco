/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.util;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.FileServersPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.testng.Assert;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by olga.lokhach on 6/25/2014.
 */
public class FtpUtil extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(FtpUtil.class);
    private static final String JMX_FILE_SERVERS_CONFIG = "Alfresco:Type=Configuration,Category=fileServers,id1=default";
    private static final String FTP_STOP = "stop";
    private static final String FTP_START = "start";
    private static final String FTP_PORT = "ftp.port";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");

    /**
     * Method to init the client
     *
     * @param shareUrl
     * @param user
     * @param password
     * @return ftpClient
     */

    private static FTPClient connectServer(String shareUrl, String user, String password)
    {

        String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
        int port = Integer.parseInt(ftpPort);

        try
        {
            FTPClient ftpClient = new FTPClient();
            ftpClient.setDataTimeout(10000);
            ftpClient.connect(server, port);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setConnectTimeout(2000);
            boolean success = ftpClient.login(user, password);
            if (!success)
            {
                throw new RuntimeException(ftpClient.getReplyString());
            }
            return ftpClient;
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

    }

    /**
     * Method to upload a content
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param contentName
     * @param remoteFolderPath
     * @return true if content is uploaded
     */

    public static boolean uploadContent(String shareUrl, String user, String password, File contentName, String remoteFolderPath)
    {

        InputStream inputStream;
        OutputStream outputStream;
        boolean result = false;

        try
        {
            FTPClient ftpClient = connectServer(shareUrl, user, password);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory(remoteFolderPath);
            ftpClient.setControlKeepAliveTimeout(600);
            if (ftpClient.isConnected())
                try
                {
                    inputStream = new FileInputStream(contentName);
                    outputStream = ftpClient.storeFileStream(contentName.getName());

                    if (outputStream != null)
                    {

                        byte[] buffer = new byte[4096];
                        int l;
                        while ((l = inputStream.read(buffer)) != -1)
                        {
                            outputStream.write(buffer, 0, l);
                        }

                        inputStream.close();
                        outputStream.flush();
                        outputStream.close();
                        ftpClient.logout();
                        ftpClient.disconnect();
                        result = true;
                    }
                    else
                    {
                        logger.error(ftpClient.getReplyString());
                    }
                }
                catch (IOException ex)
                {
                    throw new RuntimeException(ex.getMessage());
                }
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

        return result;
    }

    /**
     * Method to get list of remote objects from FTP
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param remoteObject
     * @param remoteFolderPath
     * @return true if object is exist
     */

    public static boolean isObjectExists(String shareUrl, String user, String password, String remoteObject, String remoteFolderPath)
    {

        try
        {
            FTPClient ftpClient = connectServer(shareUrl, user, password);
            ftpClient.changeWorkingDirectory(remoteFolderPath);

            for (String content : ftpClient.listNames())
            {
                if (content.equals(remoteObject))
                {
                    return true;
                }
            }
            ftpClient.logout();
            ftpClient.disconnect();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        return false;
    }

    /**
     * Method to create a remote folder
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param spaceName
     * @param remoteFilePath
     * @return true if folder is created
     */

    public static boolean createSpace(String shareUrl, String user, String password, String spaceName, String remoteFilePath)
    {

        boolean result;

        try
        {
            FTPClient ftpClient = connectServer(shareUrl, user, password);
            ftpClient.changeWorkingDirectory(remoteFilePath);
            result = ftpClient.makeDirectory(spaceName);
            if (!result)
            {
                logger.error(ftpClient.getReplyString());
            }
            ftpClient.logout();
            ftpClient.disconnect();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        return result;
    }

    /**
     * Method to edit a content via FTP
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param remoteContentName
     * @param remoteFolderPath
     * @return true if content is edited
     */

    public static boolean editContent(String shareUrl, String user, String password, String remoteContentName, String remoteFolderPath)
    {

        OutputStream outputStream;
        boolean result = false;

        try
        {
            FTPClient ftpClient = connectServer(shareUrl, user, password);
            ftpClient.changeWorkingDirectory(remoteFolderPath);
            outputStream = ftpClient.storeFileStream(remoteContentName);
            if (outputStream != null)
            {
                outputStream.write(user.getBytes());
                outputStream.close();
                ftpClient.logout();
                ftpClient.disconnect();
                result = true;
            }
            else
            {
                logger.error(ftpClient.getReplyString());
            }

        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

        return result;
    }

    /**
     * Method to delete a remote folder
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param remoteSpaceName
     * @param remoteFolderPath
     * @return true if folder is deleted
     */

    public static boolean deleteFolder(String shareUrl, String user, String password, String remoteSpaceName, String remoteFolderPath)
    {
        boolean result;

        try
        {
            FTPClient ftpClient = connectServer(shareUrl, user, password);
            ftpClient.changeWorkingDirectory(remoteFolderPath);
            result = ftpClient.removeDirectory(remoteFolderPath + "/" + remoteSpaceName);
            if (!result)
            {
                logger.error(ftpClient.getReplyString());
            }
            ftpClient.logout();
            ftpClient.disconnect();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        return result;
    }

    /**
     * Method to delete a remote content
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param remoteContentName
     * @param remoteFilePath
     * @return true if a content is deleted
     */

    public static boolean deleteContentItem(String shareUrl, String user, String password, String remoteContentName, String remoteFilePath)
    {

        boolean result;

        try
        {
            FTPClient ftpClient = connectServer(shareUrl, user, password);
            ftpClient.changeWorkingDirectory(remoteFilePath);
            result = ftpClient.deleteFile(remoteContentName);
            if (!result)
            {
                logger.error(ftpClient.getReplyString());
            }
            ftpClient.logout();
            ftpClient.disconnect();

        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        return result;
    }

    /**
     * Method to get a content from FTP
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param remoteContentName
     * @param remoteFilePath
     * @return String message
     */

    public static String getContent(String shareUrl, String user, String password, String remoteContentName, String remoteFilePath)
    {
        StringBuilder content = new StringBuilder();
        BufferedReader reader;
        String inputLine;

        try
        {
            FTPClient ftpClient = connectServer(shareUrl, user, password);
            ftpClient.changeWorkingDirectory(remoteFilePath);
            InputStream inputStream = ftpClient.retrieveFileStream(remoteContentName);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((inputLine = reader.readLine()) != null)
            {
                content.append(inputLine);
            }
            reader.close();
            ftpClient.logout();
            ftpClient.disconnect();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

        return content.toString();

    }

    /**
     * Config ftp port
     */
    public static void configFtpPort()
    {
        JmxUtils.invokeAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, FTP_STOP);
        JmxUtils.setAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, FTP_PORT, ftpPort);
        JmxUtils.invokeAlfrescoServerProperty(JMX_FILE_SERVERS_CONFIG, FTP_START);
    }

    /**
     * Method to set a custom ftp port through Admin Console
     */

    public static void setCustomFtpPort(WebDrone drone, String port)
    {
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        FileServersPage fileServersPage = sysSummaryPage.openConsolePage(AdminConsoleLink.FileServers).render();
        fileServersPage.configFtpPort(port);
    }



    public static boolean DeleteSpace(String shareUrl, String user, String password, String remoteSpaceName, String remoteFolderPath)
    {
        boolean result = false;
        FTPClient ftpclient = connectServer(shareUrl, user, password);

        try
        {
            boolean spaceexists = ftpclient.changeWorkingDirectory(remoteFolderPath + "/" + remoteSpaceName);

            if (spaceexists)
            {

                if (ftpclient.listNames().length == 0)
                {

                    ftpclient.changeToParentDirectory();
                    result = ftpclient.removeDirectory(remoteSpaceName);
                }
                else
                {
                    for (FTPFile file : ftpclient.listFiles())
                    {
                        if (file.isFile())
                        {
                            ftpclient.deleteFile(file.getName());
                        }
                        if (file.isDirectory())
                        {
                            EmptyFolderContents(ftpclient, file.getName());
                        }

                    }
                    ftpclient.changeToParentDirectory();
                    result = ftpclient.removeDirectory(remoteSpaceName);

                }

            }

            ftpclient.logout();
            ftpclient.disconnect();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        return result;

    }

    private static void EmptyFolderContents(FTPClient ftpclient, String foldername)
    {
        try
        {
            boolean spaceexists = ftpclient.changeWorkingDirectory(ftpclient.printWorkingDirectory() + "/" + foldername);

            if (spaceexists)
            {

                if (ftpclient.listNames().length == 0)
                {
                    ftpclient.changeToParentDirectory();
                    ftpclient.removeDirectory(foldername);
                    return;
                }

                for (FTPFile file : ftpclient.listFiles())
                {
                    if (file.isFile())
                    {
                        ftpclient.deleteFile(file.getName());
                    }
                    if (file.isDirectory())
                    {

                        EmptyFolderContents(ftpclient, file.getName());

                    }

                }

                ftpclient.changeToParentDirectory();
                ftpclient.removeDirectory(foldername);
            }

        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

    }

    public static boolean renameFile(String shareUrl, String username, String userpass, String ftppath, String object, String newName)
    {
        boolean result;
        FTPClient ftpclient = connectServer(shareUrl, username, userpass);

        try
        {
            ftpclient.login(username, userpass);
            ftpclient.enterLocalPassiveMode();
            ftpclient.changeWorkingDirectory(ftppath);

            result = ftpclient.rename(object, newName);
            if (!result)
            {
                ftpclient.logout();
                ftpclient.disconnect();
                try
                {
                    throw new IllegalAccessException("Seem access denied");
                }
                catch (Exception ex)
                {
                    System.out.println("IOException: " + ex.getMessage());
                }
            }

            ftpclient.logout();
            ftpclient.disconnect();
            result = true;
        }
        catch (IOException ex)
        {
            result = false;
        }
        return result;

    }

    public static boolean EditContent(String shareUrl, String username, String userpass, String ftppath, String filename, String contents)
    {
        boolean successful;

        String ftpUrl = "ftp://%s:%s@%s/%s;type=i";
        // FTPClient ftpclient = connectServer(shareUrl, username, userpass);
        String serverIP = getAddress(shareUrl);

        try
        {

            ftpUrl = String.format(ftpUrl, username, userpass, serverIP, ftppath + filename);
            if (isObjectExists(shareUrl, username, userpass, filename, ftppath))
            {
                URL url = new URL(ftpUrl);
                OutputStream outputStream = null;
                try
                {
                    URLConnection conn = url.openConnection();
                    outputStream = conn.getOutputStream();
                    outputStream.write(contents.getBytes());
                    outputStream.close();
                }
                catch (IOException e)
                {
                    throw new IllegalAccessException("Seem access denied");
                }
                finally
                {
                    if (outputStream != null)
                        outputStream.close();
                }
            }

            // check editing
            if (isObjectExists(shareUrl, username, userpass, filename, ftppath))
            {
                String inputLine;
                URL url = new URL(ftpUrl);
                BufferedReader in = null;
                try
                {
                    URLConnection conn = url.openConnection();
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    while ((inputLine = in.readLine()) != null)
                        Assert.assertTrue(inputLine.equals(contents), "Expected item isn't edited '" + filename);
                    in.close();

                }
                catch (IOException e)
                {
                    throw new IllegalAccessException("Seem access denied");
                }
                finally
                {
                    if (in != null)
                        in.close();
                }
            }

            successful = true;
        }
        catch (Exception ex)
        {
            successful = false;
        }
        finally
        {
            RemoveLocalFile(filename);
        }
        return successful;
    }

    public static boolean checkContent(String shareUrl, String username, String userpass, String ftppath, String filename, String contents)
    {
        boolean successful;

        String ftpUrl = "ftp://%s:%s@%s/%s;type=i";
        String serverIP = getAddress(shareUrl);

        try
        {

            ftpUrl = String.format(ftpUrl, username, userpass, serverIP, ftppath + filename);

            // check editing
            if (isObjectExists(shareUrl, username, userpass, filename, ftppath))
            {
                String inputLine;
                URL url = new URL(ftpUrl);
                BufferedReader in = null;
                try
                {
                    URLConnection conn = url.openConnection();
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    while ((inputLine = in.readLine()) != null)
                        Assert.assertTrue(inputLine.equals(contents), "Expected item isn't edited '" + filename);
                    in.close();

                }
                catch (IOException e)
                {
                    throw new IllegalAccessException("Seem access denied");
                }
                finally
                {
                    if (in != null)
                        in.close();
                }
            }

            successful = true;
        }
        catch (Exception ex)
        {
            successful = false;
        }
        finally
        {
            RemoveLocalFile(filename);
        }
        return successful;
    }

    public static String getAddress(String url)
    {
        checkNotNull(url);
        Matcher m = IP_PATTERN.matcher(url);
        if (m.find())
        {
            return m.group();
        }
        else
        {
            m = DOMAIN_PATTERN.matcher(url);
            if (m.find())
            {
                return m.group();
            }
        }
        throw new PageOperationException(String.format("Can't parse address from url[%s]", url));
    }

    public static void RemoveLocalFile(String filename)
    {
        File file = new File(filename);
        if (file.exists())
        {
            file.delete();
        }
    }

    /**
     * Method to copy a folder
     *
     * @param shareUrl
     * @param user
     * @param password
     * @param remoteFolderName
     * @param remoteFolderPath
     * @param destination
     *
     */

    public static boolean copyFolder(String shareUrl, String user, String password, String remoteFolderPath, String remoteFolderName, String destination)

    {
        boolean result;
        FTPClient ftpClient = connectServer(shareUrl, user, password);

        try
        {
            ftpClient.changeWorkingDirectory(remoteFolderPath + "/" + remoteFolderName);
            ftpClient.setControlKeepAliveTimeout(600);

            if (ftpClient.listNames().length == 0)
            {

                ftpClient.changeWorkingDirectory(destination);
                result = ftpClient.makeDirectory(remoteFolderName);
                if (!result)
                {
                    logger.error(ftpClient.getReplyString());
                    ftpClient.logout();
                    ftpClient.disconnect();
                    return result;
                }
            }

            else

            {
                for (FTPFile file : ftpClient.listFiles())
                {
                    if (!ftpClient.printWorkingDirectory().startsWith(remoteFolderPath))
                    {
                        ftpClient.changeWorkingDirectory(remoteFolderPath + "/" + remoteFolderName);
                    }

                    if (file.isFile())
                    {

                        InputStream inputStream = null;
                        ByteArrayOutputStream outputStream = null;

                        try
                        {
                            outputStream = new ByteArrayOutputStream();
                            ftpClient.retrieveFile(file.getName(), outputStream);
                            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                            ftpClient.changeWorkingDirectory(destination);
                            if (ftpClient.listDirectories().length == 0)
                            {
                                result = ftpClient.makeDirectory(remoteFolderName);

                                if (!result)
                                {
                                    logger.error(ftpClient.getReplyString());
                                    ftpClient.logout();
                                    ftpClient.disconnect();
                                    return result;
                                }
                            }
                            else
                            {
                                for (FTPFile content : ftpClient.listDirectories())
                                {
                                    if (!content.getName().equalsIgnoreCase(remoteFolderName))
                                    {
                                        result = ftpClient.makeDirectory(remoteFolderName);
                                        if (!result)
                                        {
                                            logger.error(ftpClient.getReplyString());
                                            ftpClient.logout();
                                            ftpClient.disconnect();
                                            return result;
                                        }
                                    }
                                }
                            }

                            ftpClient.changeWorkingDirectory(destination + "/" + remoteFolderName);
                            ftpClient.storeFile(file.getName(), inputStream);

                        }
                        catch (IOException ex)
                        {
                            throw new RuntimeException(ex.getMessage());
                        }
                        finally
                        {
                            if (inputStream != null)
                                inputStream.close();

                            if (outputStream != null)
                                outputStream.close();
                        }

                    }

                    if (file.isDirectory())
                    {
                        copyFolderContents(ftpClient, remoteFolderPath, file.getName(), destination);
                    }

                }
            }

            ftpClient.logout();
            ftpClient.disconnect();
            result = true;
        }

        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

        return result;
    }

    private static void copyFolderContents(FTPClient ftpClient, String remoteFolderPath, String folderName, String destination)
    {
      try
        {
            String newDestination = destination + ftpClient.printWorkingDirectory().replace(remoteFolderPath, "");
            boolean spaceexists = ftpClient.changeWorkingDirectory(ftpClient.printWorkingDirectory() + "/" + folderName);


            if (spaceexists)
            {

                if (ftpClient.listNames().length == 0)
                {
                    ftpClient.changeWorkingDirectory(newDestination);
                    ftpClient.makeDirectory(folderName);
                }

                for (FTPFile file : ftpClient.listFiles())
                {
                    if (!ftpClient.printWorkingDirectory().startsWith(remoteFolderPath))
                    {
                        ftpClient.changeWorkingDirectory(ftpClient.printWorkingDirectory().replace(destination, remoteFolderPath));
                    }

                    if (file.isFile())
                    {
                        InputStream inputStream = null;
                        ByteArrayOutputStream outputStream = null;

                        try
                        {
                            outputStream = new ByteArrayOutputStream();
                            ftpClient.retrieveFile(file.getName(), outputStream);
                            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                            ftpClient.changeWorkingDirectory(newDestination);
                            if (ftpClient.listDirectories().length == 0)
                            {
                                boolean result = ftpClient.makeDirectory(folderName);

                                if (!result)
                                {
                                    logger.error(ftpClient.getReplyString());
                                    return;
                                }
                            }
                            else
                            {
                                for (FTPFile content : ftpClient.listDirectories())
                                {
                                    if (!content.getName().equalsIgnoreCase(folderName))
                                    {
                                        boolean result = ftpClient.makeDirectory(folderName);

                                        if (!result)
                                        {
                                            logger.error(ftpClient.getReplyString());
                                            return;
                                        }
                                    }
                                }
                            }
                            ftpClient.changeWorkingDirectory(newDestination + "/" + folderName);
                            ftpClient.storeFile(file.getName(), inputStream);
                        }
                        catch (IOException ex)
                        {
                            throw new RuntimeException(ex.getMessage());
                        }
                        finally
                        {
                            if (inputStream != null)
                                inputStream.close();

                            if (outputStream != null)
                                outputStream.close();
                        }
                    }
                    if (file.isDirectory())
                    {
                        copyFolderContents(ftpClient, remoteFolderPath, file.getName(), destination);
                    }
                }

            }
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

    }



}
