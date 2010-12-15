package org.alfresco.wcm.client.impl;

import java.io.InputStream;

import org.apache.chemistry.opencmis.commons.data.ContentStream;

public class ContentStreamCmisImpl implements org.alfresco.wcm.client.ContentStream
{
    private final ContentStream cmisContentStream;

    
    public ContentStreamCmisImpl(ContentStream cmisContentStream)
    {
        super();
        this.cmisContentStream = cmisContentStream;
    }

    /* (non-Javadoc)
     * @see org.alfresco.wcm.client.impl.ContentStream#getFileName()
     */
    public String getFileName()
    {
        return cmisContentStream.getFileName();
    }

    /* (non-Javadoc)
     * @see org.alfresco.wcm.client.impl.ContentStream#getLength()
     */
    public long getLength()
    {
        return cmisContentStream.getLength();
    }

    /* (non-Javadoc)
     * @see org.alfresco.wcm.client.impl.ContentStream#getMimeType()
     */
    public String getMimeType()
    {
        return cmisContentStream.getMimeType();
    }

    /* (non-Javadoc)
     * @see org.alfresco.wcm.client.impl.ContentStream#getStream()
     */
    public InputStream getStream()
    {
        return cmisContentStream.getStream();
    }
    
}
