/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.events.types.ActivityEvent;
import org.alfresco.events.types.BrowserEvent;
import org.alfresco.events.types.ContentEvent;
import org.alfresco.events.types.ContentReadRangeEvent;
import org.alfresco.events.types.Event;
import org.alfresco.events.types.RepositoryEvent;
import org.alfresco.events.types.RepositoryEventImpl;
import org.alfresco.events.types.SiteEvent;
import org.alfresco.events.types.SiteManagementEvent;
import org.alfresco.events.types.SyncEvent;
import org.alfresco.util.FileFilterMode.Client;

/**
 * Produces Events for testing
 *
 * @author Gethin James
 */
public class EventFactory
{
    private static String TYPE_CONTENT = "{http://www.alfresco.org/model/content/1.0}content";
    /**
     * Produces ActivityEvent objects
     * @param type
     * @param nodeId
     * @param siteId
     * @param name
     * @param mimeType
     * @return ActivityEvent
     */
    public static ActivityEvent createActivityEvent(String type, String username, String nodeId, String siteId, String name, String mimeType)
    {
        return new ActivityEvent(type, "t123", "alfresco.com", username, nodeId,siteId,null,null,
                  "{\"title\": \"exception.docx\", \"nodeRef\": \"workspace://SpacesStore\20a64aa3-392a-449b-abf7-3599a371cc0a\", \"page\": \"document-details?nodeRef=workspace://SpacesStore/20a64aa3-392a-449b-abf7-3599a371cc0a\"}"
                    , name, mimeType, 50l, "UTF-8");
    }
    
    /**
     * Produces RepositoryEvent objects
     * @param type
     * @param usernname
     * @return RepositoryEvent
     */
    public static RepositoryEvent createRepositoryEvent(String type, String username)
    {
        return new RepositoryEventImpl(type, "t123", "alfresco.com", new Date().getTime(), username);
    }
    
    /**
     * Produces BrowserEvent objects
     * @param siteId
     * @param username
     * @param component
     * @param action
     * @return
     */
    public static BrowserEvent createBrowserEvent( String siteId, String username, String component, String action)
    {
        return new BrowserEvent(username,"alfresco.com", "t123", siteId, component, action, null, null);
    }
    
    /**
     * Produces ContentEvent objects
     * @param username
     * @param siteId
     * @param nodeId
     * @param mimeType
     * @return
     */
    public static ContentEvent createContentEvent(String siteId,  String username, String nodeId, String mimeType)
    {
        return new ContentReadRangeEvent(username,"alfresco.com", "t123", nodeId, siteId, TYPE_CONTENT,
                    Client.ftp, "filename",mimeType, 50l, "UTF-8", "1-4");
    }
    

    /**
     * Produces SyncEvent objects
     * @param type
     * @param username
     * @param siteId
     * @param nodeId
     * @param mimeType
     * @return
     */
    public static SyncEvent createSyncEvent(String type, String username, String siteId,  String nodeId, String mimeType)
    {
        return new SyncEvent(type, username, "alfresco.com", "t123", nodeId, siteId, TYPE_CONTENT,
                    Client.imap, "filename",mimeType, 50l, "UTF-8", "remote"+nodeId, "remote.alfresco.com", "sync2389");
    }
    
    /**
     * Produces SiteEvent objects
     * @param type
     * @param username
     * @param siteId
     * @return
     */
    public static SiteEvent createSiteEvent(String type, String username, String siteId)
    {
        return new SiteManagementEvent(type, "t123", "alfresco.com", new Date().getTime(), username, siteId,
                    "title for"+siteId, "desc for"+siteId, "PUBLIC", "site-dashboard");
    }
    
    public static List<Event> createEvents(String siteId, String username)
    {
        List<Event> events = new ArrayList<Event>();
        events.add(createActivityEvent("org.alfresco.documentlibrary.file-added",username, null, siteId, "filename.txt", "text/html"));
        events.add(createActivityEvent("org.alfresco.documentlibrary.file-created", username, null, siteId, "filename1.txt", "text/plain"));
        events.add(createBrowserEvent(siteId, username, "webpage", "view"));
        events.add(createContentEvent(siteId, username, "node234", "application/pdf"));   
        events.add(createRepositoryEvent("login", username));
        events.add(createSyncEvent("to.cloud", username, siteId, "node134", "application/pdf"));                    
        events.add(createSiteEvent("site.create", username, siteId));
        return events;
    }
  
}
