/*
* Copyright (C) 2005-2010 Alfresco Software Limited.
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

package org.alfresco.module.vti.handler;

import java.util.Date;
import java.util.List;

import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.alfresco.module.vti.metadata.model.MeetingsInformation;
import org.alfresco.module.vti.metadata.model.TimeZoneInformation;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.service.cmr.site.SiteInfo;

/**
 * Interface for meeting web service handler
 * 
 * @author PavelYur
 */
public interface MeetingServiceHandler
{
    /** The different statuses of a Meeting Attendee */
    public enum AttendeeStatus {
        Pending, Accepted, Tentative, Declined
    }

    /**
     * Lists the Meeting Workspace sites that are available, to the current user,
     *  on the Alfresco Server.
     * 
     * @param recurring <code>true</code> if the meeting is recurring; otherwise, <code>false</code>.
     * @return list that contains the details of Meeting Workspace sites in the root directory of the Alfresco server.
     */
    public List<SiteInfo> getMeetingWorkspaces(boolean recurring);

    /**
     * Lists the languages and templates supported by the specified Alfresco server.
     * 
     * @param siteName The site name
     * @param requestFlags
     *        <ul>
     *        <li>canCreateMeetings (returns true or false to indicate whether the caller has ManageSubweb rights on the site being posted to)</li>
     *        <li>templateLanguages (returns the list of languages for which templates are available)</li>
     *        <li>templateList (returns the list of available Meeting Workspace templates available for the specified LCID)</li>
     *        <li>workspacestatus (returns information about the workspace)</li>
     *        </ul>
     * @param lcid The locale identifier to which you want to limit the results.
     * @return meeting information
     */
    public MeetingsInformation getMeetingsInformation(String siteName, int requestFlags, int lcid);

    /**
     * Creates a new Meeting Workspace site on the specified Alfresco server.
     * 
     * @param title The title for the Meeting Workspace site that will be created.
     * @param templateName The name of the template you want to use when the site is created.
     * @param lcid The locale identifier that you want to use when the site is created.
     * @param timeZoneInformation The time zone information that you want to use when the site is created.
     * @param user Current user
     * @return name of newly created Meeting Workspace.
     */
    public String createWorkspace(String title, String templateName, int lcid, TimeZoneInformation timeZoneInformation, SessionUser user) throws Exception;

    /**
     * Updates the title (but not the name) of a Meeting Workspace site on the specified Alfresco server
     * 
     * @param siteName The site name
     * @param title The new title for the Meeting Workspace site
     */
    public void updateWorkspaceTitle(String siteName, String newTitle) throws SiteDoesNotExistException;
    
    /**
     * Creates the meeting information in the Meeting Workspace site on the specified Alfresco server.
     * 
     * @param siteName The site name
     * @param meeting The meeting bean ({@link MeetingBean})
     */
    public void addMeeting(String siteName, MeetingBean meeting) throws SiteDoesNotExistException;

    /**
     * Updates meeting information .
     * 
     * @param siteName The site name
     * @param meeting The meeting bean ({@link MeetingBean})
     */
    public void updateMeeting(String siteName, MeetingBean meeting) throws SiteDoesNotExistException, ObjectNotFoundException;

    /**
     * Associates a meeting represented in Internet Calendar (iCal) format with the Meeting Workspace site on the specified Alfresco server.
     * 
     * @param siteName The site name
     * @param meeting The meeting bean ({@link MeetingBean})
     */
    public void addMeetingFromICal(String siteName, MeetingBean meeting) throws SiteDoesNotExistException;

    /**
     * Updates meeting information stored in Internet Calendar (iCal) format.
     * 
     * @param siteName The site name
     * @param meeting The meeting bean ({@link MeetingBean})
     * @param ignoreAttendees <code>true</code> if you want to skip processing of attendee information in the iCal; otherwise, <code>false</code>.
     */
    public void updateMeetingFromICal(String siteName, MeetingBean meeting, boolean ignoreAttendees) throws SiteDoesNotExistException, ObjectNotFoundException;

    /**
     * Removes the association between a meeting and a Meeting Workspace site.
     * 
     * @param siteName The site name
     * @param recurrenceId The recurrence ID for the meeting that needs its association removed. This parameter can be set to 0 for single-instance meetings.
     * @param uid A persistent GUID for the calendar component.
     * @param sequence An integer that is used to determine the ordering of updates in case they arrive out of sequence. Updates with a lower-than-current sequence are discarded.
     *        If the sequence is equal to the current sequence, the latest update will be applied.
     * @param utcDateStamp The date and time that the instance of the Date object was created. This parameter needs to be in the UTC format (for example,
     *        2003-03-04T04:45:22-08:00).
     * @param cancelMeeting <code>true</code> if you want to delete a meeting; <code>false</code> if you just want to remove its association with a Meeting Workspace site.
     */
    public void removeMeeting(String siteName, int recurrenceId, String uid, int sequence, Date utcDateStamp, 
            boolean cancelMeeting) throws SiteDoesNotExistException, ObjectNotFoundException;

    /**
     * Restores a meeting in a Meeting Workspace site, which was previously removed.
     * This will only be supported if archiving is enabled, and the meeting Calendar NodeRef
     *  is still present in the archive store.
     */
    public void restoreMeeting(String siteName, String uid) throws SiteDoesNotExistException, ObjectNotFoundException;
    
    /**
     * Sets an attendee's response for a meeting. Currently, nothing happens with this information,
     *  but in future we could potentially add activity entries, send emails etc 
     */
    public void updateAttendeeResponse(String siteName, String attendeeEmail, AttendeeStatus status, String uid, 
            int recurrenceId, int sequence, Date utcDateStamp) throws SiteDoesNotExistException, ObjectNotFoundException;
    
    /**
     * Deletes the Meeting Workspace site from the specified Alfresco server.
     * 
     * @param siteName The siteName to delete
     * @param user Current user
     * @throws Exception
     */
    public void deleteWorkspace(String siteName, SessionUser user) throws Exception;
}