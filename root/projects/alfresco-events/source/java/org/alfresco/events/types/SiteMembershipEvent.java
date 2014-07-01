/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

/**
 * Alfresco Site Event that indicates a change of membership
 * for a user or group
 * 
 * @author Gethin James
 */
public interface SiteMembershipEvent extends SiteEvent
{
    public String getAuthorityName();
    public String getRole();
}
