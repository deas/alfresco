package org.alfresco.wcm.client.impl;

import java.io.InputStream;

import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.commons.data.ContentStream;

public class ContentStreamCmisRenditionImpl implements org.alfresco.wcm.client.Rendition
{
    private final Rendition cmisRendition;
    private ContentStream cmisContentStream;

    public ContentStreamCmisRenditionImpl(Rendition cmisRendition)
    {
        super();
        this.cmisRendition = cmisRendition;
    }

    public String getFileName()
    {
        return getContentStream().getFileName();
    }

    public long getLength()
    {
        return cmisRendition.getLength();
    }

    public String getMimeType()
    {
        return cmisRendition.getMimeType();
    }

    public InputStream getStream()
    {
        return getContentStream().getStream();
    }
    
    private ContentStream getContentStream()
    {
        if (cmisContentStream == null)
        {
            cmisContentStream = cmisRendition.getContentStream();
        }
        return cmisContentStream;
    }

    @Override
    public long getHeight()
    {
        return cmisRendition.getHeight();
    }

    @Override
    public long getWidth()
    {
        return cmisRendition.getWidth();
    }
}
