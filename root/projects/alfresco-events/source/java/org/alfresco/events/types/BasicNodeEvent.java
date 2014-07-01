/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;


/**
 * A Basic Event that occurs on an Alfresco node.
 * 
 * @author Gethin James
 */
public interface BasicNodeEvent extends SiteEvent, ClientEvent
{
    public String getNodeId();
    public String getNodeType();
    public String getName();

    public void setNodeId(String nodeId);
    public void setNodeType(String nodeType);
    public void setName(String name);
    
    public void setSiteId(String siteId);
}