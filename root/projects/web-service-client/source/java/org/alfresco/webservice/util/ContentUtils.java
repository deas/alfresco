/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.webservice.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.net.ssl.SSLSocketFactory;
import org.alfresco.webservice.content.Content;
import org.springframework.util.FileCopyUtils;

/**
 * Content Utils Class
 * 
 * @author Roy Wetherall
 */
public class ContentUtils
{   
    public static final int BUFFER_SIZE = 4096;
    
    /**
     * Convert an input stream to a byte array
     * 
     * @param inputStream   the input stream
     * @return              the byte array
     * @throws Exception
     */
    public static byte[] convertToByteArray(InputStream inputStream) throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileCopyUtils.copy(inputStream, out);
        return out.toByteArray();
    }
    
    /**
     * Get the content from the download servlet as a string
     * 
     * @param content   the content object
     * @return          the content as a string
     */
    public static String getContentAsString(Content content)
    {
        // Get the url and the ticket
        String ticket = AuthenticationUtils.getTicket();
        String strUrl = content.getUrl() + "?ticket=" + ticket;
        
        StringBuilder readContent = new StringBuilder();
        InputStreamReader is = null;
        try
        {
            // Connect to donwload servlet            
            URL url = new URL(strUrl);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Cookie", "JSESSIONID=" + AuthenticationUtils.getAuthenticationDetails().getSessionId() + ";");
            if (content.getFormat() != null && content.getFormat().getEncoding() != null)
            {
                is = new InputStreamReader(conn.getInputStream(), content.getFormat().getEncoding());
            }
            else
            {
                is = new InputStreamReader(conn.getInputStream());
            }
            int read = is.read();
            while (read != -1)
            {
               readContent.append((char)read);
               read = is.read();
            }
        }
        catch (Exception exception)
        {
            throw new WebServiceException("Unable to get content as string.", exception);
        }
        finally
        {
            if (is != null)
            {
                try { is.close(); } catch (Throwable e) {}
            } 
        }
        
        // return content as a string
        return readContent.toString();
    }
    
    /**
     * Get the content as an imput stream
     * 
     * @param content
     * @return
     */
    public static InputStream getContentAsInputStream(Content content)
    {
        // Get the url and the ticket
        String ticket = AuthenticationUtils.getTicket();
        String strUrl = content.getUrl() + "?ticket=" + ticket;
 
        try
        {
            // Create the url connection to the download servlet            
            URL url = new URL(strUrl);
            URLConnection conn = url.openConnection();
            
            // Set the cookie information
            conn.setRequestProperty("Cookie", "JSESSIONID=" + AuthenticationUtils.getAuthenticationDetails().getSessionId() + ";");
            
            // Return the input stream
            return conn.getInputStream();
        }
        catch (Exception exception)
        {
            throw new WebServiceException("Unable to get content as inputStream.", exception);
        }
    }
    
    /**
     * Streams content into the repository.  Once done a content details string is returned and this can be used to update 
     * a content property in a CML statement.
     * 
     * Uses the repository host and port details currently set in the WebServiceFactory based on the end point address.
     * 
     * @param file  the file to stream into the repository
     * @return      the content data that can be used to set the content property in a CML statement  
     */
    public static String putContent(File file)
    {
        return putContent(file, WebServiceFactory.getHost(), WebServiceFactory.getPort(), null, null);
    }
    
    /**
     * Streams content into the repository.  Once done a content details string is returned and this can be used to update 
     * a content property in a CML statement.
     * 
     * @param file  the file to stream into the repository
     * @param host  the host name of the destination repository
     * @param port  the port name of the destination repository
     * @return      the content data that can be used to set the content property in a CML statement  
     */
    public static String putContent(File file, String host, int port)
    {
        return putContent(file, host, port, null, null);
    }
    
    /**
     * Streams content into the repository.  Once done a content details string is returned and this can be used to update 
     * a content property in a CML statement.
     * 
     * @param file  the file to stream into the repository
     * @param host  the host name of the destination repository
     * @param port  the port name of the destination repository
     * @param mimetype the mimetype of the file, ignored if null
     * @param encoding the encoding of the file, ignored if null
     * @return      the content data that can be used to set the content property in a CML statement  
     */
    @SuppressWarnings("deprecation")
    public static String putContent(File file, String host, int port, String mimetype, String encoding)
    {
        boolean isSSL = WebServiceFactory.getEndpointAddress().toLowerCase().startsWith("https:");
        return putContent(file, host, port, WebServiceFactory.getEndpointWebapp(), mimetype, encoding, isSSL);
    }
    
    /**
     * Streams content into the repository using nonSSL connection.  Once done a content details string is returned and this can be used to update 
     * a content property in a CML statement.
     * 
     * @param file  the file to stream into the repository
     * @param host  the host name of the destination repository
     * @param port  the port name of the destination repository
     * @param webAppName        the name of the target web application (default 'alfresco')
     * @param mimetype the mimetype of the file, ignored if null
     * @param encoding the encoding of the file, ignored if null
     * @return      the content data that can be used to set the content property in a CML statement  
     */
    public static String putContent(File file, String host, int port, String webAppName, String mimetype, String encoding)
    {      
        return putContent(file, host, port, webAppName, mimetype, encoding, false);
    }
    
    /**
     * Streams content into the repository.  Once done a content details string is returned and this can be used to update 
     * a content property in a CML statement.
     * 
     * @param file  the file to stream into the repository
     * @param host  the host name of the destination repository
     * @param port  the port name of the destination repository
     * @param webAppName        the name of the target web application (default 'alfresco')
     * @param mimetype the mimetype of the file, ignored if null
     * @param encoding the encoding of the file, ignored if null
     * @param isSSL true if HTTPS protocol is used
     * @return      the content data that can be used to set the content property in a CML statement  
     */
    @SuppressWarnings("deprecation")
    public static String putContent(File file, String host, int port, String webAppName, String mimetype, String encoding, boolean isSSL)
    {      
        String result = null;
        
        try 
        {
            String url = "/" +
                         webAppName +
                         "/upload/" + 
                         URLEncoder.encode(file.getName(), "UTF-8") + 
                         "?ticket=" + AuthenticationUtils.getTicket();
            if (mimetype != null)
            {
                url = url + "&mimetype=" + mimetype;
            }
            if (encoding != null)
            {
                url += "&encoding=" + encoding;
            }
            
            String request = "PUT " + url + " HTTP/1.1\n" +
                          "Cookie: JSESSIONID=" + AuthenticationUtils.getAuthenticationDetails().getSessionId() + ";\n" + 
                          "Content-Length: " + file.length() + "\n" +
                          "Host: " + host + ":" + port + "\n" +
                          "Connection: Keep-Alive\n" +
                          "\n";
            
            // Open sockets and streams
            Socket socket = null;
            if (isSSL)
            {
                socket = SSLSocketFactory.getDefault().createSocket(host, port);
            }
            else
            {
                socket = new Socket(host, port);
            }
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());
              
            try
            {
                if (socket != null && os != null && is != null) 
                {            
                    // Write the request header
                    os.writeBytes(request);
                    
                    // Stream the content onto the server
                    InputStream fileInputStream = new FileInputStream(file);
                    int byteCount = 0;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead = -1;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) 
                    {
                        os.write(buffer, 0, bytesRead);
                        byteCount += bytesRead;
                    }
                    os.flush();
                    fileInputStream.close();
                
                    // Read the response and deal with any errors that might occur
                    boolean firstLine = true;
                    String responseLine;
                    while ((responseLine = is.readLine()) != null) 
                    {
                        if (firstLine == true)
                        {
                            if (responseLine.contains("200") == true)
                            {
                                firstLine = false;
                            }
                            else if (responseLine.contains("401") == true)
                            {
                                throw new RuntimeException("Content could not be uploaded because invalid credentials have been supplied.");
                            }
                            else if (responseLine.contains("403") == true)
                            {
                                throw new RuntimeException("Content could not be uploaded because user does not have sufficient priveledges.");
                            }
                            else
                            {
                                throw new RuntimeException("Error returned from upload servlet (" + responseLine + ")");
                            }
                        }
                        else if (responseLine.contains("contentUrl") == true)
                        {
                            result = responseLine;
                            break;
                        }
                    }      
                }
            }
            finally
            {
                try
                {
                    // Close the streams and socket
                    if (os != null) { os.close(); }
                    if (is != null) { is.close(); }
                    if (socket != null) { socket.close(); }
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Error closing sockets and streams", e);
                }
            }
        } 
        catch (Exception e) 
        {
            throw new RuntimeException("Error writing content to repository server", e);
        } 
        
        return result;
    }
    
    /**
     * Copy the content into a given file.
     * 
     * @param content   the content object
     * @param file      the file
     */
    public static void copyContentToFile(Content content, File file)
    {
        try
        {
            FileOutputStream os = new FileOutputStream(file);
            FileCopyUtils.copy(getContentAsInputStream(content), os);
        }
        catch (IOException exception)
        {
            throw new WebServiceException("Unable to copy content into file.", exception);
        }
    }
    
    /**
     * Helper method to copy from one stream to another
     * 
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    public static int copy(InputStream in, OutputStream out) throws IOException 
    {
        try 
        {
            int byteCount = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) 
            {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        }
        finally 
        {
            try 
            {
                in.close();
            }
            catch (IOException ex) 
            {
                // Could not close input stream
            }
            try 
            {
                out.close();
            }
            catch (IOException ex) 
            {
                // Could not close output stream
            }
        }
    }
}
