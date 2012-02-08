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

/**
 * <p>Custom comparator to compare DialogMetaInfo beans.</p>
 * 
 * @author PavelYur
 */
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

import org.alfresco.module.vti.handler.alfresco.VtiUtils;
import org.alfresco.module.vti.metadata.dic.VtiSort;
import org.alfresco.module.vti.metadata.dic.VtiSortField;

public class DialogMetaInfoComparator implements Comparator<DialogMetaInfo>
{
    //Default values for comparison
    private VtiSortField sortField = VtiSortField.TYPE;
    private VtiSort sort = VtiSort.ASC;
    
    /**
     * Constructor
     * 
     * @param sortField field that is used as a key in sorting
     * @param sort sorting type (ascending or descending)
     */
    public DialogMetaInfoComparator(VtiSortField sortField, VtiSort sort)
    {
        this.sortField = sortField;
        this.sort = sort;
    }
    
    /**
     * Sort DialogMetaInfo beans as MS clients do that.
     */
    public int compare(DialogMetaInfo o1, DialogMetaInfo o2)
    {         
        if (o1.isFolder() != o2.isFolder())
        {
            if (o1.isFolder())
            {
                if (sort.equals(VtiSort.ASC))                
                {
                    return -1;
                }
                if (sort.equals(VtiSort.DESC))                
                {
                    return 1;
                }
            }            
            else
            {
                if (sort.equals(VtiSort.ASC))                
                {
                    return 1;
                }
                if (sort.equals(VtiSort.DESC))                
                {
                    return -1;
                }
            }
        }
        else
        {
            if (sort.equals(VtiSort.ASC))
            {
                if (sortField.equals(VtiSortField.TYPE))
                {                    
                    int extIndex1 = o1.getName().lastIndexOf('.');
                    int extIndex2 = o2.getName().lastIndexOf('.');
                    String ext1, ext2;
                    if (extIndex1 != -1 && o1.getName().length() > extIndex1 + 1)
                    {
                        ext1 = o1.getName().substring(extIndex1 + 1);
                    }
                    else
                    {
                        ext1 = "";
                    }
                    if (extIndex2 != -1 && o2.getName().length() > extIndex2 + 1)
                    {
                        ext2 = o2.getName().substring(extIndex2 + 1);
                    }
                    else
                    {
                        ext2 = "";
                    }                    
                    return ext1.compareToIgnoreCase(ext2);                    
                }                
                if (sortField.equals(VtiSortField.NAME))
                {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
                if (sortField.equals(VtiSortField.MODIFIEDBY))
                {
                    return o1.getModifiedBy().compareToIgnoreCase(o2.getModifiedBy());
                }
                if (sortField.equals(VtiSortField.MODIFIED))
                {
                    // ALF-11054 fix, compare as dates
                    return compareModified(o1, o2);
                }
                if (sortField.equals(VtiSortField.CHECKEDOUTTO))
                {
                    return o1.getCheckedOutTo().compareToIgnoreCase(o2.getCheckedOutTo());
                }
            }
            if (sort.equals(VtiSort.DESC))
            {
                if (sortField.equals(VtiSortField.TYPE))
                {                    
                    int extIndex1 = o1.getName().lastIndexOf('.');
                    int extIndex2 = o2.getName().lastIndexOf('.');
                    String ext1, ext2;
                    if (extIndex1 != -1 && o1.getName().length() > extIndex1 + 1)
                    {
                        ext1 = o1.getName().substring(extIndex1 + 1);
                    }
                    else
                    {
                        ext1 = "";
                    }
                    if (extIndex2 != -1 && o2.getName().length() > extIndex2 + 1)
                    {
                        ext2 = o2.getName().substring(extIndex2 + 1);
                    }
                    else
                    {
                        ext2 = "";
                    }                    
                    return -ext1.compareToIgnoreCase(ext2);                    
                }                
                if (sortField.equals(VtiSortField.NAME))
                {
                    return -o1.getName().compareToIgnoreCase(o2.getName());
                }
                if (sortField.equals(VtiSortField.MODIFIEDBY))
                {
                    return -o1.getModifiedBy().compareToIgnoreCase(o2.getModifiedBy());
                }
                if (sortField.equals(VtiSortField.MODIFIED))
                {
                    // ALF-11054 fix, compare as dates
                    return -compareModified(o1, o2);
                }
                if (sortField.equals(VtiSortField.CHECKEDOUTTO))
                {
                    return -o1.getCheckedOutTo().compareToIgnoreCase(o2.getCheckedOutTo());
                }                
            }            
        }          
        return 0;
    }

    private int compareModified(DialogMetaInfo o1, DialogMetaInfo o2)
    {
        try
        {
            Date date1 = VtiUtils.parseVersionDate(o1.getModifiedTime());
            Date date2 = VtiUtils.parseVersionDate(o2.getModifiedTime());

            return date1.compareTo(date2);
        }
        catch (ParseException e)
        {
            // ignore parse exception
            return 0;
        }
    }
}
