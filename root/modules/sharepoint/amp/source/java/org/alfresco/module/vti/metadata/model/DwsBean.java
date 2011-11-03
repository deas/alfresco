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
import java.util.List;

/**
 * <p>Represents Sharepoint document work space after creation. </p>
 * 
 * @author AndreyAk
 *
 */
public class DwsBean implements Serializable
{

    private static final long serialVersionUID = -1659680970545671188L;

    private String url;
    private String doclibUrl;
    private String parentWeb;
    private List<String> failedUsers;

    public DwsBean()
    {
        super();
    }
    
    /**
     * @param url
     * @param doclibUrl
     * @param parentWeb
     * @param failedUsers
     */
    public DwsBean(String url, String doclibUrl, String parentWeb, List<String> failedUsers)
    {
        this.url = url;
        this.doclibUrl = doclibUrl;
        this.parentWeb = parentWeb;
        this.failedUsers = failedUsers;
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * <p>Sets the dws url.</p>
     * 
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * @return the doclibUrl
     */
    public String getDoclibUrl()
    {
        return doclibUrl;
    }

    /**
     * <p>Sets the document library url for current dws.</p>
     * 
     * @param doclibUrl the doclibUrl to set
     */
    public void setDoclibUrl(String doclibUrl)
    {
        this.doclibUrl = doclibUrl;
    }

    /**
     * @return the parentWeb
     */
    public String getParentWeb()
    {
        return parentWeb;
    }

    /**
     * <p>Sets the url of the parent site.</p>
     * 
     * @param parentWeb the parentWeb to set
     */
    public void setParentWeb(String parentWeb)
    {
        this.parentWeb = parentWeb;
    }

    /**
     * @return the failedUsers
     */
    public List<String> getFailedUsers()
    {
        return failedUsers;
    }

    /**
     * @param failedUsers the failedUsers to set
     */
    public void setFailedUsers(List<String> failedUsers)
    {
        this.failedUsers = failedUsers;
    }
        
}
