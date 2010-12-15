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
package org.alfresco.module.vti.metadata.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.module.vti.metadata.dic.VtiSort;
import org.alfresco.module.vti.metadata.dic.VtiSortField;

/**
 * <p>Utils class for dialogview method (FileOpen/Save).</p>
 *  
 * @author PavelYur
 *
 */
public class DialogUtils
{
    private static final Map<String, String> fileExtensionMap = new HashMap<String, String>(89, 1.0f);
    private static final String IMAGE_PREFIX = "images/filetypes/";
    private static final String IMAGE_POSTFIX = ".gif";
    private static final String DEFAULT_IMAGE = "images/filetypes/_default.gif";
    
    private static final ReadWriteLock fileExtensionMapLock = new ReentrantReadWriteLock();
    
    /**
     * <p>Recognize image for the given file extension, if image was not found or extension
     * is empty string then default image is returned.</p>  
     * 
     * @param fileName name of the file is being displayed 
     * @return server path to the correct image
     */
    public static String getFileTypeImage(String fileName)
    {
        String image = null;
        int extIndex = fileName.lastIndexOf('.');
        if (extIndex != -1 && fileName.length() > extIndex + 1)
        {
            String ext = fileName.substring(extIndex + 1).toLowerCase();

            try
            {
                fileExtensionMapLock.readLock().lock();
                image = fileExtensionMap.get(ext);
            }
            finally
            {
                fileExtensionMapLock.readLock().unlock();
            }
            if (image == null)
            {
                image = IMAGE_PREFIX + ext + IMAGE_POSTFIX;                
                if (DialogUtils.class.getClassLoader().getResourceAsStream("../../" + image) != null)
                {
                    try
                    {
                        fileExtensionMapLock.writeLock().lock();
                        fileExtensionMap.put(ext, image);
                    }
                    finally
                    {
                        fileExtensionMapLock.writeLock().unlock();
                    }
                }
                else
                {
                    image = DEFAULT_IMAGE;
                }
            }
        }

        return image;
    }
    
    /**
     * <p>According the current sorting field and sorting order return new sorting order.</p> 
     * 
     * @param sortFieldValue value of the sorting field
     * @param currentSortField current sorting field value
     * @param sort current sorting order
     * @return new sorting order
     */
    public String getSortDir(String sortFieldValue, VtiSortField currentSortField, VtiSort sort)
    {
        VtiSortField sortField = VtiSortField.value(sortFieldValue);

        if (sortField.equals(currentSortField))
        {
            if (sort.equals(VtiSort.ASC))
            {
                return VtiSort.DESC.toString();
            }
            if (sort.equals(VtiSort.DESC))
            {
                return VtiSort.ASC.toString();
            }
        }
        return VtiSort.ASC.toString();
    }

    /**
     * <p>Cast String to VtiSortField</p>
     * 
     * @param sortFieldValue
     * @return
     */
    public VtiSortField getSortFieldValue(String sortFieldValue)
    {
        return VtiSortField.value(sortFieldValue);
    }

    /**
     * <p>Cast String to VtiSort</p>
     * 
     * @param sortValue
     * @return
     */
    public VtiSort getSortValue(String sortValue)
    {
        return VtiSort.value(sortValue);
    }
}
