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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Locale;

import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentStreamListener;
import org.alfresco.service.cmr.repository.ContentWriter;

/**
 * Bare-bones implementation of the writer for SOLR purposes
 * 
 * @author Derek Hulley
 * @since 5.0
 */
public class SolrFileContentWriter implements ContentWriter
{

    @Override
    public boolean isChannelOpen()
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public void addListener(ContentStreamListener listener)
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public long getSize()
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public ContentData getContentData()
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public String getContentUrl()
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public String getMimetype()
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public void setMimetype(String mimetype)
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public String getEncoding()
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public void setEncoding(String encoding)
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public Locale getLocale()
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public void setLocale(Locale locale)
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public ContentReader getReader() throws ContentIOException
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public boolean isClosed()
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public WritableByteChannel getWritableChannel() throws ContentIOException
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public FileChannel getFileChannel(boolean truncate) throws ContentIOException
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public OutputStream getContentOutputStream() throws ContentIOException
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public void putContent(ContentReader reader) throws ContentIOException
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public void putContent(InputStream is) throws ContentIOException
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public void putContent(File file) throws ContentIOException
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public void putContent(String content) throws ContentIOException
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public void guessMimetype(String filename)
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }

    @Override
    public void guessEncoding()
    {
        throw new UnsupportedOperationException("Auto-created method not implemented.");
    }
}
