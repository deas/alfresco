/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events;

import java.util.Set;

/**
 * Event type registry.
 * 
 * @author steveglover
 *
 */
public interface EventRegistry
{
	boolean isEventTypeRegistered(String eventType);
	void addEventType(String eventType);
	Set<String> getEventTypes();
}
