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
import java.text.MessageFormat;

/**
 * <p>Class that represent version of document</p>
 *
 * @author PavelYur
 */
public class DocumentVersionBean implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 8732427482027589331L;
    
    // version id
    private String id;

    // version number
    private String version;
    
    // url of the version
    private String url;
    
    // time when version was created
    private String createdTime;
    
    // name of the user that creates version 
    private String createdBy;
    
    // size in bytes
    private long size;
    
    // comments
    private String comments;

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
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * <p>Sets the version number</p>
     * 
     * @param version the version to set
     */
    public void setVersion(String version)
    {
        this.version = version;
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
     * 
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * 
     * @return the createdTime
     */
    public String getCreatedTime()
    {
        return createdTime;
    }

    /**
     * 
     * @param createdTime the createdTime to set 
     */
    public void setCreatedTime(String createdTime)
    {
        this.createdTime = createdTime;
    }

    /**
     * 
     * @return the createdBy
     */
    public String getCreatedBy()
    {
        return createdBy;
    }

    /**
     * 
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    /**
     * 
     * @return the size
     */
    public long getSize()
    {
        return size;
    }

    /**
     * 
     * @param size the size to set
     */
    public void setSize(long size)
    {
        this.size = size;
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
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }
    
    /**
     * <p>Constructor</p>
     * 
     * @param version
     * @param url
     * @param createdTime
     * @param createdBy
     * @param size
     * @param comments
     */
    public DocumentVersionBean(String version, String url, String createdTime, String createdBy, int size, String comments)
    {
        super();
        this.version = version;
        this.url = url;
        this.createdTime = createdTime;
        this.createdBy = createdBy;
        this.size = size;
        this.comments = comments;
    }

    /**
     * default costructor
     */
    public DocumentVersionBean()
    {
    }

    public String toString()
    {
        return MessageFormat.format("[version = {0}, url = ''{1}'', createdTime = {2}, createdBy = {3}, size = {4}, comments = ''{5}'']",
                version, url, createdTime, createdBy, size, comments);
    }

}
