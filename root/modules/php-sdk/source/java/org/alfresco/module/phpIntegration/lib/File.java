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
package org.alfresco.module.phpIntegration.lib;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * File object.  Models the standard cm_content node type.
 * 
 * @author Roy Wetherall
 */
public class File extends Node
{
    /** The script object name */
    private static final String SCRIPT_OBJECT_NAME = "File";
    
    /** The cm_content property name */
    private static final String PROP_CONTENT = ContentModel.PROP_CONTENT.toString();
    
    /**
     * Constructor
     * 
     * @param session   the session
     * @param nodeRef   the node reference
     */
    public File(Session session, NodeRef nodeRef)
    {
        super(session, nodeRef);
    }
    
    /**
     * Constructor 
     * 
     * @param session   the session
     * @param store     the store
     * @param id        the node id
     */
    public File(Session session, Store store, String id)
    {
        super(session, store, id);
    }
    
    /**
     * Constructor
     * 
     * @param session   the session
     * @param store     the store
     * @param id        the node id
     * @param type      the node type
     */
    public File(Session session, Store store, String id, String type)
    {
        super(session, store, id, type);
    }
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    @Override
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    /**
     * Gets the encoding for the file
     * 
     * @return  String the file's encoding
     */
    public String getEncoding()
    {
        String encoding = null;
        ContentData contentData = getContentData(false);
        if (contentData != null)
        {
            encoding = contentData.getEncoding();
        }
        return encoding;
    }
    
    /**
     * Sets the encoding for the file
     * 
     * @param encoding  the encoding
     */
    public void setEncoding(String encoding)
    {
        ContentData contentData = getContentData(true);
        contentData.setEncoding(encoding);
    }
    
    /**
     * Gets the files mimetype
     * 
     * @return  String  the mimetype
     */
    public String getMimetype()
    {
        String mimetype = null;
        ContentData contentData = getContentData(false);
        if (contentData != null)
        {
            mimetype = contentData.getMimetype();
        }
        return mimetype;
    }
    
    /**
     * Sets the files mimetype
     * 
     * @param mimetype  the mimetype
     */
    public void setMimetype(String mimetype)
    {
        ContentData contentData = getContentData(true);
        contentData.setMimetype(mimetype);
    }
    
    /**
     * Gets the file's content size
     * 
     * @return  long    the content size, -1 if unknown
     */
    public long getSize()
    {
        long size = -1;        
        ContentData contentData = getContentData(false);
        if (contentData != null)
        {
            size = contentData.getSize();
        }
        return size;
    }
    
    /**
     * Gets the files content download URL.
     * 
     * @return  String   the download URL
     */
    public String getUrl()
    {
        String url = null;
        ContentData contentData = getContentData(false);
        if (contentData != null)
        {
            url = contentData.getUrl();
        }
        return url;
    }
    
    /**
     * Get the file's content
     * 
     * @return  String  the file's content
     */
    public String getContent()
    {
        String content = null;
        ContentData contentData = getContentData(false);
        if (contentData != null)
        {
            content = contentData.getContent();
        }
        return content;
    }
    
    /**
     * Sets the file's content
     * 
     * @param content   the content
     */
    public void setContent(String content)
    {
        ContentData contentData = getContentData(true);
        contentData.setContent(content);
    }
    
    /**
     * Gets the content data object for the cm_content property.  If required to, will create a new content data object
     * and assign it to the cm_content property.
     * 
     * @param create        indicates whether to create a new content data object if one does not exist or not
     * @return ContentData  the content data object for the cm_content property
     */
    private ContentData getContentData(boolean create)
    {
        ContentData contentData = (ContentData)getProperty(PROP_CONTENT);
        if (contentData == null && create == true)
        {
            contentData = new ContentData(this, PROP_CONTENT, null, null);
            setProperty(PROP_CONTENT, contentData);
        }
        return contentData;
    }

}
