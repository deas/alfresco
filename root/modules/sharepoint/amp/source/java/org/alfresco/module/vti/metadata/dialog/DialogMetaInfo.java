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

import java.io.Serializable;

/**
 * <p>This class is a java bean that represents items in FileOpen/Save Dialogs. 
 * Is used in dialogview method (FileOpen/Save).</p> 
 * 
 * @author PavelYur
 *
 */
public class DialogMetaInfo implements Serializable
{

    private static final long serialVersionUID = -2507258363715335001L;
    
    // name to display in dialog 
    private String name = "";
    
    // path relative to root node 
    private String path = "";
    
    // name of the user that was last modifier 
    private String modifiedBy = "";
    
    // last modified time
    private String modifiedTime = "";
    
    // name of the user that checked out document or empty string if document is not checked out
    private String checkedOutTo = "";
    
    // isFolder?
    private boolean isFolder;
    
    /**
     * @param isFolder
     */
    public DialogMetaInfo(boolean isFolder)
    {
        super();
        this.isFolder = isFolder;
    }
    
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    /**
     * @return the path
     */
    public String getPath()
    {
        return path;
    }
    /**
     * @param path the path to set
     */
    public void setPath(String path)
    {
        this.path = path;
    }
    /**
     * @return the modifiedBy
     */
    public String getModifiedBy()
    {
        return modifiedBy;
    }
    /**
     * @param modifiedBy the modifiedBy to set
     */
    public void setModifiedBy(String modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    /**
     * @return the modifiedTime
     */
    public String getModifiedTime()
    {
        return modifiedTime;
    }
    /**
     * @param modifiedTime the modifiedTime to set
     */
    public void setModifiedTime(String modifiedTime)
    {
        this.modifiedTime = modifiedTime;
    }
    /**
     * @return the checkedOutTo
     */
    public String getCheckedOutTo()
    {
        return checkedOutTo;
    }
    /**
     * @param checkedOutTo the checkedOutTo to set
     */
    public void setCheckedOutTo(String checkedOutTo)
    {
        this.checkedOutTo = checkedOutTo;
    }
    /**
     * @return the isFolder
     */
    public boolean isFolder()
    {
        return isFolder;
    }
}
