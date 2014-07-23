/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.solr.content;

import java.io.File;
import java.util.Date;

import org.alfresco.repo.content.ContentContext;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A content store specific to SOLR's requirements:
 * The URL is generated from a set of properties such as:
 * <ul>
 *   <li>ACL ID</li>
 *   <li>DB ID</li>
 *   <li>Other metadata</li>
 * </ul>
 * The URL, if not known, can be reliably regenerated using the {@link SolrContentUrlBuilder}.
 * 
 * @author Derek Hulley
 * @since 5.0
 */
public class SolrContentStore implements ContentStore
{
    protected final static Logger log = LoggerFactory.getLogger(SolrContentStore.class);
    
    private final String root;
    
    public SolrContentStore(String rootStr)
    {
        File rootFile = new File(rootStr);
        try
        {
            FileUtils.forceMkdir(rootFile);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create directory for content store: " + this.root, e);
        }
        this.root = rootFile.getAbsolutePath();
    }

    @Override
    public boolean isContentUrlSupported(String contentUrl)
    {
        return (contentUrl != null && contentUrl.startsWith(SolrContentUrlBuilder.SOLR_PROTOCOL_PREFIX));
    }

    /**
     * @return                  <tt>true</tt> always
     */
    @Override
    public boolean isWriteSupported()
    {
        return true;
    }

    /**
     * @return                  -1 always
     */
    @Override
    public long getSpaceFree()
    {
        return -1L;
    }

    /**
     * @return                  -1 always
     */
    @Override
    public long getSpaceTotal()
    {
        return -1L;
    }

    @Override
    public String getRootLocation()
    {
        return root;
    }

    /**
     * Convert a content URL into a File, whether it exists or not
     */
    private File getFileFromUrl(String contentUrl)
    {
        String path = contentUrl.replace(SolrContentUrlBuilder.SOLR_PROTOCOL_PREFIX, root + "/");
        return new File(path);
    }
    
    @Override
    public boolean exists(String contentUrl)
    {
        File file = getFileFromUrl(contentUrl);
        return file.exists();
    }

    @Override
    public ContentReader getReader(String contentUrl)
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public ContentWriter getWriter(ContentContext context)
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public void getUrls(ContentUrlHandler handler) throws ContentIOException
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public void getUrls(Date createdAfter, Date createdBefore, ContentUrlHandler handler) throws ContentIOException
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public boolean delete(String contentUrl)
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }
}
