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

package org.alfresco.module.vti.handler.alfresco;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.handler.ObjectNotFoundException;
import org.alfresco.module.vti.handler.SiteTypeException;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.alfresco.module.vti.metadata.model.MeetingsInformation;
import org.alfresco.module.vti.metadata.model.MwsStatus;
import org.alfresco.module.vti.metadata.model.MwsTemplate;
import org.alfresco.module.vti.metadata.model.TimeZoneInformation;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.calendar.CalendarModel;
import org.alfresco.repo.calendar.CalendarServiceImpl;
import org.alfresco.repo.node.archive.NodeArchiveService;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.calendar.CalendarEntry;
import org.alfresco.service.cmr.calendar.CalendarService;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Alfresco implementation of MeetingServiceHandler.
 */
public class AlfrescoMeetingServiceHandler implements MeetingServiceHandler
{
    public static String MEETING_WORKSPACE_NAME = "meeting-workspace";

    private static String CALENDAR_CONTAINER_NAME = "calendar";

    private static String DOCUMENT_LIBRARY_CONTAINER_NAME = "documentLibrary";
    
    private static String DEFAULT_SITE_NAME = "meeting";

    private static Log logger = LogFactory.getLog(AlfrescoMeetingServiceHandler.class);

    private static final Pattern illegalCharactersRegExpPattern = Pattern.compile("[^A-Za-z0-9_]+");

    private SiteService siteService;

    private CalendarService calendarService;
    
    private AuthenticationService authenticationService;

    private TransactionService transactionService;

    private NodeService nodeService;
    
    private NodeArchiveService nodeArchiveService;

    private FileFolderService fileFolderService;

    private PersonService personService;

    private ShareUtils shareUtils;

    /**
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#addMeeting(String, MeetingBean)
     */
    public void addMeeting(String siteName, final MeetingBean meeting)
    {
        // Sanity check
        SiteInfo siteInfo = siteService.getSite(siteName);
        if (siteInfo == null)
        {
            throw new SiteDoesNotExistException(siteName);
        }
        if (!siteInfo.getSitePreset().equals(MEETING_WORKSPACE_NAME))
        {
            throw new SiteTypeException("vti.meeting.error.bad_type");
        }
        if (meeting.getSubject() == null)
        {
            throw new RuntimeException(getMessage("vti.meeting.error.no_subject"));
        }

        // Adjust, then add
        adjustMeetingProperties(meeting);
        calendarService.createCalendarEntry(siteName, meeting);

        if (logger.isDebugEnabled())
        {
            logger.debug("Meeting with subject '" + meeting.getSubject() + "' was created.");
        }
    }

    /**
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#addMeetingFromICal(String, MeetingBean)
     */
    public void addMeetingFromICal(String siteName, final MeetingBean meeting)
    {
        // Sanity check
        SiteInfo siteInfo = siteService.getSite(siteName);
        if (siteInfo == null)
        {
            throw new SiteDoesNotExistException(siteName);
        }
        if (!siteInfo.getSitePreset().equals(MEETING_WORKSPACE_NAME))
        {
            throw new SiteTypeException("vti.meeting.error.bad_type");
        }
        if (meeting.getSubject() == null || meeting.getSubject().isEmpty())
        {
            throw new RuntimeException(getMessage("vti.meeting.error.no_subject"));
        }

        // Adjust, then add
        adjustMeetingProperties(meeting);
        calendarService.createCalendarEntry(siteName, meeting);

        if (logger.isDebugEnabled())
        {
            logger.debug("Meeting with subject '" + meeting.getSubject() + "' was created.");
        }
    }

    /**
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#createWorkspace(String, String, int, TimeZoneInformation, SessionUser)
     */
    public String createWorkspace(String title, String templateName, int lcid, TimeZoneInformation timeZoneInformation, SessionUser user) throws Exception
    {
        // Build the site name from the title
        String siteName = removeIllegalCharacters(title);

        // A list of underscores is not a valid name.
        int matches = StringUtils.countMatches(siteName, "_");
        if(matches>0 && siteName.length()==matches){
        	siteName = DEFAULT_SITE_NAME;
        }

        // Build a unique name up
        SiteInfo siteInfo = null;
        String newSiteName = null;
        int i = 0;
        do
        {
            newSiteName = truncateSiteName(siteName, i == 0 ? "" : "_" + i);
            siteInfo = siteService.getSite(newSiteName);
            i++;
        } while (siteInfo != null);

        // Have it created
        shareUtils.createSite(user, MEETING_WORKSPACE_NAME, newSiteName, title, "", true);
        return newSiteName;
    }

    /**
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#setWorkspaceTitle(String, String)
     */
    public void updateWorkspaceTitle(String siteName, String newTitle)
    {
        // Sanity check
        SiteInfo siteInfo = siteService.getSite(siteName);
        if (siteInfo == null)
        {
            throw new SiteDoesNotExistException(siteName);
        }
        if (!siteInfo.getSitePreset().equals(MEETING_WORKSPACE_NAME))
        {
            throw new SiteTypeException("vti.meeting.error.bad_type");
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Updating title of site " + siteName + " to '" + newTitle + "'");
        }
        
        siteInfo.setTitle(newTitle);
        siteService.updateSite(siteInfo);
    }

    /**
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#getMeetingWorkspaces(boolean)
     */
    public List<SiteInfo> getMeetingWorkspaces(boolean recurring)
    {
        // NOTE - The meaning of this flag is currently unclear, and a TDI is open for it
        // Based on the eventual response, the current boolean may need to become a 
        //  Boolean, or may need to become two booleans.
        List<SiteInfo> resultList = new ArrayList<SiteInfo>();
        for (SiteInfo siteInfo : siteService.listSites(authenticationService.getCurrentUserName()))
        {
            String memberRole = siteService.getMembersRole(siteInfo.getShortName(), authenticationService.getCurrentUserName());
            if (MEETING_WORKSPACE_NAME.equals(siteInfo.getSitePreset()) && SiteModel.SITE_MANAGER.equals(memberRole))
            {
                // Work out if this workspace has recurring or non-recurring entries in it
                int count = getCalendarNonRecurringCount(siteInfo.getShortName());
                
                if (count == -1)
                {
                    // Has at least one recurring entry
                    // TODO Based on the TDI, when should this be returned?
                }
                else if (count == 0)
                {
                    // Current spec says that empty workspaces are always returned
                    resultList.add(siteInfo);
                }
                else
                {
                    // Has no recurring events, but at least one non-recurring one
                    // Currently should only be returned if recurring is false
                    if (!recurring)
                    {
                        resultList.add(siteInfo);
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#getMeetingsInformation(String, int, int)
     */
    public MeetingsInformation getMeetingsInformation(String siteName, int requestFlags, int lcid)
    {
        MeetingsInformation info = new MeetingsInformation();

        // Flag 0x1 = Query for user permissions
        if ((requestFlags & 1) == 1)
        {
            // TODO Actually check with the permissions service for this
            info.setAllowCreate(true);
        }
        
        // Flag 0x2 = Query for site template languages
        if ((requestFlags & 2) == 2)
        {
            info.getTemplateLanguages().add(new Integer(1033));
        }

        // Flag 0x4 = Query for the site templates
        if ((requestFlags & 4) == 4)
        {
            info.getTemplates().add(MwsTemplate.getDefault());
        }

        // Flag 0x8 = Query for other status values
        if ((requestFlags & 8) == 8)
        {
            MwsStatus siteStatus = MwsStatus.getDefault();

            // Work out how many calendar entries there are, and check to see if
            //  any of them are recurring entries (they need a special response)
            int count = getCalendarNonRecurringCount(siteName);

            // Record the status details
            siteStatus.setMeetingCount(count);
            info.setStatus(siteStatus);
        }

        return info;
    }

    /**
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#removeMeeting(String, int, String, int, Date, boolean)
     */
    public void removeMeeting(String siteName, final int recurrenceId, String uid, int sequence, Date utcDateStamp, boolean cancelMeeting)
    {
        SiteInfo siteInfo = siteService.getSite(siteName);

        if (siteInfo == null)
        {
            throw new RuntimeException(getMessage("vti.meeting.error.no_site"));
        }

        NodeRef calendarNodeRef = siteService.getContainer(siteName, CALENDAR_CONTAINER_NAME);
        if (calendarNodeRef == null)
        {
            throw new RuntimeException(getMessage("vti.meeting.error.no_calendar"));
        }

        final CalendarEntry entry = getEvent(siteName, uid);

        if (entry == null)
        {
            throw new RuntimeException(getMessage("vti.meeting.error.no_meeting"));
        }

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute()
            {
                if (recurrenceId == 0)
                {
                    calendarService.deleteCalendarEntry(entry);
                }
                else
                {
                    HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
                    try
                    {
                        properties.put(CalendarModel.PROP_IGNORE_EVENT_DATE, new SimpleDateFormat("yyyyMMdd").parse(String.valueOf(recurrenceId)));
                    }
                    catch (ParseException e)
                    {
                        throw new RuntimeException(e);
                    }
                    
                    NodeRef meetingNodeRef = entry.getNodeRef();
                    nodeService.createNode(meetingNodeRef, CalendarModel.ASSOC_IGNORE_EVENT_LIST, 
                          QName.createQName(NamespaceService.CONTENT_MODEL_PREFIX, "ignoreEvent_" + recurrenceId + "_"
                            + GUID.generate()), CalendarModel.TYPE_IGNORE_EVENT, properties);
                }
                return null;
            }
        });
    }

    /**
     * Note - This method may want replacing with a dedicated Canned Query (CQ),
     *  if it every gets heavily used (currently it is very rarely called).
     * 
     * This method requires that the archive store is enabled.
     * 
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#restoreMeeting(String, String)
     */
    @Override
    public void restoreMeeting(String siteName, String uid) throws SiteDoesNotExistException, ObjectNotFoundException
    {
        NodeRef calendarContainer = null;

        calendarContainer = siteService.getContainer(siteName, CALENDAR_CONTAINER_NAME);
        if (calendarContainer == null)
        {
            throw new SiteDoesNotExistException(siteName);
        }

        // Look in the archive store for it
        NodeRef archiveRoot = nodeService.getRootNode(StoreRef.STORE_REF_ARCHIVE_SPACESSTORE);
        
        // This takes a slightly icky brute force approach...
        Set<QName> calendarEntryType = Collections.singleton(CalendarModel.TYPE_EVENT);
        List<ChildAssociationRef> archivedCalendarEntries = 
                nodeService.getChildAssocs(archiveRoot, calendarEntryType);
        
        NodeRef archivedEventNodeRef = null;
        for (ChildAssociationRef ref : archivedCalendarEntries)
        {
            NodeRef archivedNodeRef = ref.getChildRef();
            String outlookUID = (String)nodeService.getProperty(archivedNodeRef, CalendarModel.PROP_OUTLOOK_UID);
            if (uid.equals(outlookUID))
            {
                archivedEventNodeRef = archivedNodeRef;
                break;
            }
        }
        
        // If we found it, have it restored
        if (archivedEventNodeRef == null)
        {
            throw new ObjectNotFoundException();
        }
        nodeArchiveService.restoreArchivedNode(archivedEventNodeRef);
    }

    /**
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#updateMeeting(String, MeetingBean)
     */
    public void updateMeeting(final String siteName, MeetingBean meeting) throws SiteDoesNotExistException, ObjectNotFoundException
    {
        // Sanity check
        SiteInfo siteInfo = siteService.getSite(siteName);
        if (siteInfo == null)
        {
            throw new SiteDoesNotExistException(siteName);
        }
        if (!siteInfo.getSitePreset().equals(MEETING_WORKSPACE_NAME))
        {
            throw new SiteTypeException("vti.meeting.error.bad_type");
        }
        
        // Tweak things on the meeting bean as needed
        adjustMeetingProperties(meeting);
        
        // Get the current event object to update
        final CalendarEntry entry = getEvent(siteName, meeting.getId());
        if (entry == null)
        {
            throw new ObjectNotFoundException();
        }
        
        // Copy the updateable properties onto it
        // This is only a subset of what can be supported via iCal
        entry.setTitle(meeting.getTitle());
        entry.setLocation(meeting.getLocation());
        entry.setStart(meeting.getStart());
        entry.setEnd(meeting.getEnd());

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute()
            {
                calendarService.updateCalendarEntry(entry);
                return null;
            }
        });

        if (logger.isDebugEnabled())
        {
            logger.debug("Meeting with Outlook UID = '" + meeting.getId() + "' was updated.");
        }
    }
    
    /**
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#updateMeetingFromICal(String, MeetingBean, boolean)
     */
    @SuppressWarnings("deprecation")
    public void updateMeetingFromICal(final String siteName, MeetingBean meeting, boolean ignoreAttendees)
    {
        NodeRef calendarContainer = null;

        calendarContainer = siteService.getContainer(siteName, CALENDAR_CONTAINER_NAME);
        if (calendarContainer == null)
        {
            throw new VtiHandlerException(getMessage("vti.meeting.error.no_site_update"));
        }
        
        // Tweak things on the meeting bean as needed
        adjustMeetingProperties(meeting);
        
        // Get the current event object to update
        final CalendarEntry entry = getEvent(siteName, meeting.getId());
        if (entry == null)
        {
            throw new VtiHandlerException(getMessage("vti.meeting.error.no_meeting_update"));
        }
        
        // Copy everything onto it
        // TODO It would be better if the caller asked us for the
        //  MeetingBean rather than creating a new one...
        entry.setTitle(meeting.getTitle());
        entry.setDescription(meeting.getDescription());
        entry.setLocation(meeting.getLocation());
        entry.setStart(meeting.getStart());
        entry.setEnd(meeting.getEnd());
        entry.setRecurrenceRule(meeting.getRecurrenceRule());
        entry.setLastRecurrence(meeting.getLastRecurrence());
        entry.setOutlookUID(meeting.getOutlookUID());
        entry.setSharePointDocFolder(meeting.getSharePointDocFolder());

        // Do the attendees
        // TODO Update this to be more efficient
        final List<String> usersToAdd = new ArrayList<String>();
        Set<NodeRef> peoples = personService.getAllPeople();
        final Set<String> siteMembers = siteService.listMembers(siteName, null, null, -1, true).keySet();

        for (String email : meeting.getAttendees())
        {

            for (NodeRef peopleRef : peoples)
            {
                String personEmail = (String) nodeService.getProperty(peopleRef, ContentModel.PROP_EMAIL);

                if (personEmail == null || !personEmail.equalsIgnoreCase(email))
                {
                    continue;
                }

                String userName = (String) nodeService.getProperty(peopleRef, ContentModel.PROP_USERNAME);

                if (siteMembers.contains(userName))
                {
                    siteMembers.remove(userName);
                }

                if (siteService.isMember(siteName, userName))
                {
                    String memberRole = siteService.getMembersRole(siteName, userName);
                    if (memberRole.equals(SiteModel.SITE_CONSUMER) || memberRole.equals(SiteModel.SITE_CONTRIBUTOR))
                    {
                        usersToAdd.add(userName);
                    }
                }
                else
                {
                    usersToAdd.add(userName);
                }
            }
        }

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute()
            {
                calendarService.updateCalendarEntry(entry);

                SiteInfo siteInfo = siteService.getSite(siteName);
                String siteCreator = (String) nodeService.getProperty(siteInfo.getNodeRef(), ContentModel.PROP_CREATOR);

                for (String member : siteMembers)
                {
                    if (member.equalsIgnoreCase(siteCreator))
                    {
                        continue;
                    }
                    siteService.removeMembership(siteName, member);
                }
                for (String userName : usersToAdd)
                {
                    siteService.setMembership(siteName, userName, SiteModel.SITE_COLLABORATOR);
                }

                return null;
            }
        });

        if (logger.isDebugEnabled())
        {
            logger.debug("Meeting with Outlook UID = '" + meeting.getId() + "' was updated.");
        }
    }

    /**
     * @see MeetingServiceHandler#updateAttendeeResponse(String, String, AttendeeStatus, String, String, int, Date)
     */
    @Override
    public void updateAttendeeResponse(String siteName, String attendeeEmail, AttendeeStatus status, String uid,
            int recurrenceId, int sequence, Date utcDateStamp) throws SiteDoesNotExistException,
            ObjectNotFoundException
    {
        // Sanity check
        SiteInfo siteInfo = siteService.getSite(siteName);
        if (siteInfo == null)
        {
            throw new SiteDoesNotExistException(siteName);
        }
        if (!siteInfo.getSitePreset().equals(MEETING_WORKSPACE_NAME))
        {
            throw new SiteTypeException("vti.meeting.error.bad_type");
        }
        
        // Get the event object they are responding too
        final CalendarEntry entry = getEvent(siteName, uid);
        if (entry == null)
        {
            throw new ObjectNotFoundException();
        }
        
        // TODO Do something with all of this
        logger.warn("No support to handle response details of " + status + " from Attendee " + attendeeEmail + " of meeting " + uid);
    }

    /**
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#deleteWorkspace(String, SessionUser)
     */
    public void deleteWorkspace(String siteName, SessionUser user) throws Exception
    {
        SiteInfo siteInfo = siteService.getSite(siteName);

        if (siteInfo == null)
        {
            throw new SiteDoesNotExistException(siteName);
        }

        if (!siteInfo.getSitePreset().equals(MEETING_WORKSPACE_NAME))
        {
            throw new SiteTypeException("vti.meeting.error.bad_type");
        }

        shareUtils.deleteSite(user, siteName);
    }

    /**
     * Remove illegal characters from string
     * 
     * @param value input string
     * @return output string
     */
    protected String removeIllegalCharacters(String value)
    {
        return illegalCharactersRegExpPattern.matcher(value).replaceAll("_");
    }
    /**
     * The Site Name is limited by two things, the QName of the ChildAssoc under 
     *  the sites root, and the Site Authority.
     * The QName is limited to 255 characters, while the Authority is limited to
     *  100 characters.
     * To fit in, the Site Short Name, plus its group prefix, plus the authority names 
     *  all need to be within this shorter limit.
     */
    protected String truncateSiteName(String baseSiteName, String suffix)
    {
        // SITE_GROUP_<shortname>_PERMISSION
        int limit = 72;
        
        if (baseSiteName.length() + suffix.length() > limit)
        {
            limit -= suffix.length();
            return baseSiteName.substring(0, limit) + suffix;
        }
        return baseSiteName + suffix;
    }

    protected NodeRef createDocumentsFolder(final MeetingBean meeting, String siteName)
    {
        NodeRef result = null;
        NodeRef docLibraryContainer = null;
        String dws = siteName;
        if (siteService.hasContainer(dws, DOCUMENT_LIBRARY_CONTAINER_NAME))
        {
            docLibraryContainer = siteService.getContainer(dws, DOCUMENT_LIBRARY_CONTAINER_NAME);
        }
        else
        {
            docLibraryContainer = siteService.createContainer(dws, DOCUMENT_LIBRARY_CONTAINER_NAME, ContentModel.TYPE_FOLDER, null);
        }

        if (docLibraryContainer == null)
        {
            throw new VtiHandlerException(VtiHandlerException.LIST_NOT_FOUND);
        }

        final NodeRef finalDocLibraryContainer = docLibraryContainer;
        result = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute()
            {
                FileInfo fileInfo = fileFolderService.create(finalDocLibraryContainer, getFolderName(meeting), ContentModel.TYPE_FOLDER);
                nodeService.setProperty(fileInfo.getNodeRef(), ContentModel.PROP_DESCRIPTION, new MLText(meeting.getSubject()));
                return fileInfo.getNodeRef();
            }
        });

        return result;
    }

    protected void renameDocumentsFolder(String siteName, MeetingBean meeting, String folderNodeRef)
    {
        NodeRef docLibraryContainer = null;
        docLibraryContainer = siteService.getContainer(siteName, DOCUMENT_LIBRARY_CONTAINER_NAME);
        if (docLibraryContainer == null)
        {
            throw new VtiHandlerException(getMessage("vti.meeting.error.no_site_update"));
        }
        if (folderNodeRef == null)
        {
            throw new VtiHandlerException(getMessage("vti.meeting.error.no_meeting_update"));
        }
        NodeRef folderRef = new NodeRef(folderNodeRef);
        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();
            fileFolderService.rename(folderRef, getFolderName(meeting));
            if (meeting.getSubject() != null && meeting.getSubject().length() > 0)
            {
                nodeService.setProperty(folderRef, ContentModel.PROP_DESCRIPTION, new MLText(meeting.getSubject()));
            }
            tx.commit();

            if (logger.isDebugEnabled())
            {
                logger.debug("Folder successfully renamed.");
            }
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex)
            {
            }

            if (e instanceof FileExistsException)
            {
                throw new VtiHandlerException(VtiHandlerException.ITEM_NOT_FOUND);
            }

            throw VtiExceptionUtils.createRuntimeException(e);
        }
    }

    protected static String getFolderName(MeetingBean meeting)
    {
        StringBuilder result = new StringBuilder();
        result.append(new SimpleDateFormat("dd MMM yyyy").format(meeting.getStartDate()));
        result.append(" ");
        result.append(new SimpleDateFormat("kk.mm").format(meeting.getStartDate()));
        result.append("-");
        result.append(new SimpleDateFormat("kk.mm").format(meeting.getEndDate()));
        return result.toString();
    }

    /**
     * TODO Fix up the message files so we can get rid of this nasty hack!
     */
    protected String getMessage(String name)
    {
        String result = null;
        try
        {
            result = new String(I18NUtil.getMessage(name).getBytes("ISO-8859-1"), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
        }
        return result;
    }

    protected void adjustMeetingProperties(MeetingBean meeting)
    {
        Calendar from = Calendar.getInstance();
        from.setTime(meeting.getStartDate());
        
        Calendar to = Calendar.getInstance();
        to.setTime(meeting.getEndDate());
        
        if (from.get(Calendar.HOUR_OF_DAY) + to.get(Calendar.HOUR_OF_DAY) +
            from.get(Calendar.MINUTE) + to.get(Calendar.MINUTE) +
            from.get(Calendar.SECOND) + to.get(Calendar.SECOND) == 0)
        {
            // It is "All day" event
            // Alfresco uses PROP_TO_DATE_EVENT as last day of an event, so change last day in meeting
            to.roll(Calendar.DAY_OF_YEAR, false);
            meeting.setEnd(to.getTime());
        }
    }

    private CalendarEntry getEvent(String siteName, String uid)
    {
        CalendarEntry entry = calendarService.getCalendarEntry(siteName, uid+".ics");

        if (entry == null)
        {
            NodeRef calendarNodeRef = siteService.getContainer(siteName, CalendarServiceImpl.CALENDAR_COMPONENT);
            if(calendarNodeRef == null)
            {
               return null;
            }
            
            // Lookup by the UID property
            PagingResults<CalendarEntry> entries =
                    calendarService.listOutlookCalendarEntries(siteName, uid, new PagingRequest(10));
            if (entries == null || entries.getPage() == null || entries.getPage().size() == 0)
            {
                // No calendar entry with this UID
                return null;
            }
            
            if (entries.getPage().size() == 1)
            {
                entry = entries.getPage().get(0);
            }
            else
            {
                logger.warn("Found multiple calendar entries in " + siteName + " with Outlook UID " +
                            uid + " - expecting 0 or 1 but found " + entries.getPage().size());
                entry = null;
            }
        }

        return entry;
    }
    /**
     * Returns the number of non recurring entries in a site, or
     *  -1 if the site contains at least one recurring entry
     */
    private int getCalendarNonRecurringCount(String siteName)
    {
        // Fetch all the entries in the site's calendar
        PagingResults<CalendarEntry> entries = 
                calendarService.listCalendarEntries(siteName, new PagingRequest(100));
        
        // Count them, aborting if any are recurring
        int count = 0;
        if (entries != null && entries.getPage() != null)
        {
            count = entries.getPage().size();
            for (CalendarEntry entry : entries.getPage())
            {
                if (entry.getRecurrenceRule() != null && entry.getRecurrenceRule().length() > 0)
                {
                    // Site has at least one recurring entry, count must be -1
                    count = -1;
                    break;
                }
            }
        }
        else
        {
            if (logger.isInfoEnabled())
                logger.info("Calendar details queried for " + siteName + " but no Calendar Container exists");
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Searched for non-repeating calendar entries in " + siteName + " and found " + count);

        return count;
    }

    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }

    public void setShareUtils(ShareUtils shareUtils)
    {
        this.shareUtils = shareUtils;
    }
    
    public void setCalendarService(CalendarService calendarService)
    {
        this.calendarService = calendarService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setNodeArchiveService(NodeArchiveService nodeArchiveService)
    {
        this.nodeArchiveService = nodeArchiveService;
    }

    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }
}