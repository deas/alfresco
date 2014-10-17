package org.alfresco.share.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Properties;

import org.alfresco.po.share.exception.ShareException;

public class PropertiesUtil
{

    /***
     * Sets the key & value
     * 
     * @param path
     * @param key
     * @param value
     */
    public static void setPropertyValue(String path, String key, String value)
    {
        try
        {

            FileReader reader;
            Properties fileProperty = new Properties();
            reader = new FileReader(path);
            fileProperty.load(reader);
            fileProperty.setProperty(key, value);

            fileProperty.store(new FileWriter(path), null);
        }
        catch (Exception e)
        {
            throw new ShareException("Failed to set the value into the file " + path);

        }
    }
    
    
    /**
     * Sets the key & value
     * 
     * @param path
     * @param key
     * @return
     */
    public static String getPropertyValue(String path, String key)
    {
        try
        {

            FileReader reader;
            Properties fileProperty = new Properties();
            reader = new FileReader(path);
            fileProperty.load(reader);
            return fileProperty.getProperty(key);
                
        }
        catch (Exception e)
        {
            throw new ShareException("Failed to get the value from the file " + path + e.getMessage());
        }
    }
    
    
    /**
     * Returns the value for the key
     * 
     * @param path
     * @param key
     * @return
     */
    public static String getPropertyValue(InputStream path, String key)
    {
        try
        {
            Properties fileProperty = new Properties();
            fileProperty.load(path);
            return fileProperty.getProperty(key);
                
        }
        catch (Exception e)
        {
            throw new ShareException("Failed to get the value from the file " + path + e.getMessage());
        }
    }
    
}
