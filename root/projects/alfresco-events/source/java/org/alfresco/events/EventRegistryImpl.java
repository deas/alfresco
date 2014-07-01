/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events;

import java.util.HashSet;
import java.util.Set;

/**
 * In-memory event type registry.
 * 
 * @author steveglover
 *
 */
public class EventRegistryImpl implements EventRegistry
{
	private Set<String> eventTypes = new HashSet<String>();
	
	@Override
	public void addEventType(String eventType)
	{
		eventTypes.add(eventType);
	}
	
	@Override
	public Set<String> getEventTypes()
	{
		return eventTypes;
	}

	@Override
	public boolean isEventTypeRegistered(String eventType)
	{
		return eventTypes.contains(eventType);
	}
}
