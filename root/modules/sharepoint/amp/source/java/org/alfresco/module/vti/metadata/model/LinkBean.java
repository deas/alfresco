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
 * <p>Represents Sharepoint link.</p>
 * @author AndreyAk
 *
 */
public class LinkBean implements Serializable
{

    private static final long serialVersionUID = -7781309737681728753L;
    
    private String url;
    private String description;
    private String comments;
    private String created;
    private String author;
    private String modified;
    private String editor;
    private int owshiddenversion;
    private String id;

    public LinkBean()
    {
    }
    
    public LinkBean(String url, String description, String comments, String created, String author, String modified, String editor, int owshiddenversion, String id)
    {
        super();
        this.url = url;
        this.description = description;
        this.comments = comments;
        this.created = created;
        this.author = author;
        this.modified = modified;
        this.editor = editor;
        this.owshiddenversion = owshiddenversion;
        this.id = id;
    }

    /**
     * 
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * <p>Sets the url for link.</p>
     * 
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * 
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }
    
    /**
     * <p>Sets the description for link.</p>
     * 
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * 
     * @return the comments
     */
    public String getComments()
    {
        return comments;
    }

    /**
     * 
     * @param comments the comments to set
     */
    public void setComments(String comments)
    {
        this.comments = comments;
    }

    /**
     * 
     * @return the created
     */
    public String getCreated()
    {
        return created;
    }

    /**
     * 
     * @param created the created to set
     */
    public void setCreated(String created)
    {
        this.created = created;
    }

    /**
     * 
     * @return the author
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * 
     * @param author the author to set
     */
    public void setAuthor(String author)
    {
        this.author = author;
    }

    /**
     * 
     * @return the modified
     */
    public String getModified()
    {
        return modified;
    }

    /**
     * 
     * @param modified the modified to set
     */
    public void setModified(String modified)
    {
        this.modified = modified;
    }

    /**
     * 
     * @return the editor
     */
    public String getEditor()
    {
        return editor;
    }

    /**
     * 
     * @param editor the editor to set
     */
    public void setEditor(String editor)
    {
        this.editor = editor;
    }

    /**
     * 
     * @return the owshiddenversion
     */
    public int getOwshiddenversion()
    {
        return owshiddenversion;
    }

    /**
     * 
     * @param owshiddenversion the owshiddenversion to set
     */
    public void setOwshiddenversion(int owshiddenversion)
    {
        this.owshiddenversion = owshiddenversion;
    }

    /**
     * 
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * 
     * @param id the id ti set
     */
    public void setId(String id)
    {
        this.id = id;
    }
        
    
}
