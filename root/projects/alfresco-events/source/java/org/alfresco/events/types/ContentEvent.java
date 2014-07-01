/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

/**
 * An event that occurs on an Alfresco content, we can get more information.
 * 
 * @author Gethin James
 */
public interface ContentEvent extends BasicNodeEvent
{
    public static final String DOWNLOAD = "content.download";
    public static final String READ_RANGE = "content.range";
    
    public long getSize();
    public String getMimeType();
    public String getEncoding();
    public void setSize(long size);
    public void setMimeType(String mimeType);
    public void setEncoding(String encoding);
}
