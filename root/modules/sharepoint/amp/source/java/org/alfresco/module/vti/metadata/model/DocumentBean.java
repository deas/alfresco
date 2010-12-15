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
package org.alfresco.module.vti.metadata.model;

import java.io.Serializable;

/**
 * <p>Represents  document in sharepoint workspace</p>
 * 
 * @author AndreyAk
 *
 */
public class DocumentBean implements Serializable
{

    private static final long serialVersionUID = 7409836094969517436L;
    
    private String id;
    private String progID;
    private String fileRef;
    private String objType;
    private String created;
    private String author;
    private String modified;
    private String editor;
    
    
    /**
     * @param id
     * @param progID
     * @param fileRef
     * @param objType
     * @param created
     * @param author
     * @param modified
     * @param editor
     */
    public DocumentBean(String id, String progID, String fileRef, String objType, String created, String author, String modified, String editor)
    {
        super();
        this.id = id;
        this.progID = progID;
        this.fileRef = fileRef;
        this.objType = objType;
        this.created = created;
        this.author = author;
        this.modified = modified;
        this.editor = editor;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the progID
     */
    public String getProgID()
    {
        return progID;
    }

    /**
     * @param progID the progID to set
     */
    public void setProgID(String progID)
    {
        this.progID = progID;
    }

    /**
     * @return the fileRef
     */
    public String getFileRef()
    {
        return fileRef;
    }

    /**
     * @param fileRef the fileRef to set
     */
    public void setFileRef(String fileRef)
    {
        this.fileRef = fileRef;
    }

    /**
     * @return the fSObjType
     */
    public String getObjType()
    {
        return objType;
    }

    /**
     * @param objType the fSObjType to set
     */
    public void setObjType(String objType)
    {
        this.objType = objType;
    }

    /**
     * @return the created
     */
    public String getCreated()
    {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(String created)
    {
        this.created = created;
    }

    /**
     * @return the author
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author)
    {
        this.author = author;
    }

    /**
     * @return the modified
     */
    public String getModified()
    {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(String modified)
    {
        this.modified = modified;
    }

    /**
     * @return the editor
     */
    public String getEditor()
    {
        return editor;
    }

    /**
     * @param editor the editor to set
     */
    public void setEditor(String editor)
    {
        this.editor = editor;
    }
    
}
