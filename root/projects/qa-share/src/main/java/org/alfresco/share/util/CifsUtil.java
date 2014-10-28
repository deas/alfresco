package org.alfresco.share.util;

import jcifs.smb.*;

import org.alfresco.po.share.util.PageUtils;
import org.testng.Assert;

import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Olga Antonik
 */
public class CifsUtil extends AbstractUtils implements Transferable, ClipboardOwner
{

    private Image image;
    
    /**
     * Method to add document to the Alfresco via CIFS
     * 
     * @param shareUrl
     * @param username
     * @param password
     * @param cifsPath
     * @param filename
     * @param contents
     * @return boolean
     */
    public static boolean addContent(String shareUrl, String username, String password, String cifsPath, String filename, String contents)
    {

        boolean successful;
        String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
        try
        {
            File file = new File(filename);
            if (!file.exists())
            {
                if (contents != null)
                {
                    FileOutputStream out = null;
                    try
                    {
                        out = new FileOutputStream(file.getAbsolutePath());
                        out.write(contents.getBytes());
                        out.close();
                    }
                    catch (Exception ex)
                    {
                        throw new RuntimeException(ex.getMessage());
                    }
                    finally
                    {
                        if (out != null)
                            out.close();
                    }
                }
                else
                {
                    file.createNewFile();
                }
            }

            String user = username + ":" + password;
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(user);
            SmbFile sFile = new SmbFile("smb://" + server + "/" + cifsPath + "/" + filename, auth);
            SmbFileOutputStream smbFileOutputStream = null;
            FileInputStream fileInputStream = null;
            try
            {
                smbFileOutputStream = new SmbFileOutputStream(sFile);
                fileInputStream = new FileInputStream(new File(filename));

                byte[] buf = new byte[16 * 1024 * 1024];
                int len;
                while ((len = fileInputStream.read(buf)) > 0)
                {
                    smbFileOutputStream.write(buf, 0, len);
                }
                fileInputStream.close();
                smbFileOutputStream.close();
            }
            catch (Exception ex)
            {
                throw new IllegalAccessException("Seem access denied");
            }
            finally
            {
                if (fileInputStream != null)
                    fileInputStream.close();
                if (smbFileOutputStream != null)
                    smbFileOutputStream.close();
            }

            assertTrue(sFile.exists(), "File isn't added via CIFS");

            successful = true;
        }
        catch (Exception ex)
        {
            successful = false;
        }
        finally
        {
            File file = new File(filename);
            if (file.exists())
            {
                file.delete();
            }
        }

        return successful;
    }

    /**
     * Method to upload document to the Alfresco via CIFS
     * 
     * @param shareUrl
     * @param username
     * @param password
     * @param cifsPath
     * @param file
     * @return boolean
     */
    public static boolean uploadContent(String shareUrl, String username, String password, String cifsPath, File file)
    {

        boolean successful;
        String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
        try
        {

            String user = username + ":" + password;
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(user);
            SmbFile sFile = new SmbFile("smb://" + server + "/" + cifsPath + "/" + file.getName(), auth);
            SmbFileOutputStream smbFileOutputStream = null;
            FileInputStream fileInputStream = null;
            try
            {
                smbFileOutputStream = new SmbFileOutputStream(sFile);
                fileInputStream = new FileInputStream(file);

                byte[] buf = new byte[1024 * 1024 * 40];
                int len;
                while ((len = fileInputStream.read(buf)) > 0)
                {
                    smbFileOutputStream.write(buf, 0, len);
                }

                fileInputStream.close();
                smbFileOutputStream.flush();
                smbFileOutputStream.close();
            }
            catch (Exception ex)
            {
                throw new IllegalAccessException("Seem access denied");
            }
            finally
            {
                if (fileInputStream != null)
                    fileInputStream.close();
                if (smbFileOutputStream != null)
                    smbFileOutputStream.close();
            }

            assertTrue(sFile.exists(), "File isn't added via CIFS");

            successful = true;
        }
        catch (Exception ex)
        {
            successful = false;
        }
        finally
        {
            if (file.exists())
            {
                file.delete();
            }
        }

        return successful;
    }

    /**
     * Method to verify that item presents in Alfresco
     * 
     * @param shareUrl
     * @param username
     * @param password
     * @param cifsPath
     * @param item
     * @return boolean
     */
    public static boolean checkItem(String shareUrl, String username, String password, String cifsPath, String item)
    {
        boolean successful;
        try
        {

            String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
            String user = username + ":" + password;
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(user);

            SmbFile sFile = new SmbFile("smb://" + server + "/" + cifsPath, auth);

            String[] listing = sFile.list();
            List<String> list = Arrays.asList(listing);

            successful = list.contains(item);

        }
        catch (Exception ex)
        {
            successful = false;
        }
        return successful;
    }

    /**
     * Method to verify that item presents in Alfresco
     * 
     * @param shareUrl
     * @param username
     * @param password
     * @param cifsPath
     * @param fileName
     * @return boolean
     */
    public static boolean deleteContent(String shareUrl, String username, String password, String cifsPath, String fileName)
    {
        boolean successful;
        try
        {

            String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
            String user = username + ":" + password;
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(user);

            SmbFile sFile = new SmbFile("smb://" + server + "/" + cifsPath + "/" + fileName, auth);
            SmbFile sFileParent = new SmbFile("smb://" + server + "/" + cifsPath + "/", auth);
            sFile.getPermission();
            if (sFile.exists())
            {
                sFile.delete();
            }
            else
            {
                Assert.fail("Item isn't exist");
            }

            String[] listing = sFileParent.list();
            List<String> list = Arrays.asList(listing);

            assertFalse(list.contains(fileName) && !sFile.exists(), "Item " + fileName + " isn't removed");

            successful = true;
        }
        catch (Exception ex)
        {
            successful = false;
        }
        return successful;
    }

    /**
     * Method to rename document in Alfresco via CIFS
     * 
     * @param shareUrl
     * @param username
     * @param password
     * @param cifsPath
     * @param oldName
     * @param newName
     * @return boolean
     */
    public static boolean renameItem(String shareUrl, String username, String password, String cifsPath, String oldName, String newName)
    {
        boolean successful;
        try
        {

            String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
            String user = username + ":" + password;
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(user);

            SmbFile sFile = new SmbFile("smb://" + server + "/" + cifsPath + "/", auth);
            SmbFile sFileOld = new SmbFile("smb://" + server + "/" + cifsPath + "/" + oldName, auth);
            SmbFile sFileNew = new SmbFile("smb://" + server + "/" + cifsPath + "/" + newName, auth);

            sFileOld.renameTo(sFileNew);

            String[] listing = sFile.list();
            List<String> list = Arrays.asList(listing);

            Assert.assertTrue(list.contains(newName), "Item " + oldName + " isn't renamed' to " + newName + "'");
            Assert.assertFalse(list.contains(oldName), "Item " + oldName + " isn't renamed' to " + newName + "'");

            successful = true;
        }
        catch (Exception ex)
        {
            successful = false;
        }
        return successful;
    }

    /**
     * Method to rename document in Alfresco via CIFS
     * 
     * @param shareUrl
     * @param username
     * @param password
     * @param cifsPath
     * @param filename
     * @param contents
     * @return boolean
     */
    public static boolean editContent(String shareUrl, String username, String password, String cifsPath, String filename, String contents)
    {
        boolean successful;

        try
        {
            String user = username + ":" + password;
            String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(user);

            SmbFile sFile = new SmbFile("smb://" + server + "/" + cifsPath + "/" + filename, auth);

            if (sFile.exists() && sFile.isFile() && sFile.canWrite())
            {
                SmbFileOutputStream sfos = null;
                try
                {
                    sfos = new SmbFileOutputStream(sFile);
                    sfos.write(contents.getBytes());
                    sfos.close();
                }
                catch (Exception ex)
                {
                    throw new IllegalAccessException("Seem access denied");
                }
                finally
                {
                    if (sfos != null)
                        sfos.close();
                }
            }
            else
                throw new IllegalAccessException();

            if (sFile.exists() && sFile.isFile() && sFile.canWrite())
            {
                SmbFileInputStream fstream = null;
                try
                {
                    fstream = new SmbFileInputStream(sFile);
                    String text = org.apache.commons.io.IOUtils.toString(fstream);
                    fstream.close();
                    Assert.assertTrue(text.contains(contents), "Expected item isn't edited '" + filename);
                }
                catch (Exception ex)
                {
                    return false;
                }
                finally
                {
                    if (fstream != null)
                        fstream.close();
                }

            }
            else if (sFile.canWrite())
            {
                Assert.fail("Item isn't exist or not file");
            }

            successful = true;
        }
        catch (Exception ex)
        {
            successful = false;
        }
        return successful;
    }

    /**
     * Method to check content of file
     * 
     * @param shareUrl
     * @param username
     * @param password
     * @param cifsPath
     * @param fileName
     * @param content
     * @return boolean
     */
    public static boolean checkContent(String shareUrl, String username, String password, String cifsPath, String fileName, String content)
    {
        StringBuilder builder;
        BufferedReader reader;
        boolean successful;
        try
        {

            String user = username + ":" + password;
            String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(user);

            SmbFile sFile = new SmbFile("smb://" + server + "/" + cifsPath + "/" + fileName, auth);
            try
            {
                builder = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(sFile)));
                String lineReader;
                try
                {
                    while ((lineReader = reader.readLine()) != null)
                    {
                        builder.append(lineReader).append("\n");
                    }
                }
                catch (IOException exception)
                {
                    exception.printStackTrace();
                }
                finally
                {
                    try
                    {
                        reader.close();
                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
                String currentContent = builder.toString();

                Assert.assertTrue(currentContent.contains(content), "Item " + fileName + " wasn't edit");

            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }

            successful = true;
        }
        catch (Exception ex)
        {
            successful = false;
        }
        return successful;
    }

    /**
     * Method to create folder via CIFS
     * 
     * @param shareUrl
     * @param username
     * @param password
     * @param cifsPath
     * @param spaceName
     * @return boolean
     */
    public static boolean addSpace(String shareUrl, String username, String password, String cifsPath, String spaceName)
    {
        boolean successful;
        try
        {

            String user = username + ":" + password;
            String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(user);

            SmbFile sFile = new SmbFile("smb://" + server + "/" + cifsPath + "/" + spaceName, auth);
            try
            {
                sFile.mkdir();
            }
            catch (SmbException e)
            {
                throw new IllegalAccessException();
            }
            Assert.assertTrue(sFile.exists(), "Folder isn't added via CIFS");

            successful = true;
        }
        catch (Exception ex)
        {
            successful = false;
        }
        return successful;
    }
    
    public CifsUtil(Image image)
    {
        this.image = image;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
    {

        if (flavor.equals(DataFlavor.imageFlavor) && image != null)
        {
            return image;
        }
        else
        {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        DataFlavor[] flavors = new DataFlavor[1];
        flavors[0] = DataFlavor.imageFlavor;
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        DataFlavor[] flavors = getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++)
        {
            if (flavor.equals(flavors[i]))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void lostOwnership(Clipboard arg0, Transferable arg1)
    {
        // TODO Auto-generated method stub

    }
}
