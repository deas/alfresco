/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

import java.util.List;

/**
 * An Event that occurs on an Alfresco node with information
 * about the node beyond the Basic id/type
 * 
 * @author Gethin James
 */
public interface NodeInfoEvent extends BasicNodeEvent
{
    public List<String> getPaths();
    public List<List<String>> getParentNodeIds();
}