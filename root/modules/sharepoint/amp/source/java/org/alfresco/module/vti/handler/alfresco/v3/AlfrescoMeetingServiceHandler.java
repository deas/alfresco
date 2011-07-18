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

package org.alfresco.module.vti.handler.alfresco.v3;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.VtiExceptionUtils;
import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.alfresco.module.vti.metadata.model.MeetingsInformation;
import org.alfresco.module.vti.metadata.model.MwsStatus;
import org.alfresco.module.vti.metadata.model.MwsTemplate;
import org.alfresco.module.vti.metadata.model.TimeZoneInformation;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.calendar.CalendarModel;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.calendar.CalendarService;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Alfresco implementation of MeetingServiceHandler
 * 
 * TODO Switch this to using the new {@link CalendarService}
 * 
 * @author PavelYur
 */
public class AlfrescoMeetingServiceHandler implements MeetingServiceHandler
{
    public static String MEETING_WORKSPACE_NAME = "meeting-workspace";

    private static String CALENDAR_CONTAINER_NAME = "calendar";

    private static String DOCUMENT_LIBRARY_CONTAINER_NAME = "documentLibrary";

    private static Log logger = LogFactory.getLog(AlfrescoMeetingServiceHandler.class);

    private static final Pattern illegalCharactersRegExpPattern = Pattern.compile("[^A-Za-z0-9_]+");

    protected SiteService siteService;

    protected AuthenticationService authenticationService;

    protected TransactionService transactionService;

    protected NodeService nodeService;

    protected FileFolderService fileFolderService;

    protected PersonService personService;
    
    protected SearchService searchService;

    protected NamespaceService namespaceService;

    protected ShareUtils shareUtils;

    /**
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#addMeetingFromICal(String, MeetingBean)
     */
    public void addMeetingFromICal(String siteName, final MeetingBean meeting)
    {
        NodeRef calendarContainer = null;
        String dws = siteName;
        if (siteService.hasContainer(dws, CALENDAR_CONTAINER_NAME))
        {
            calendarContainer = siteService.getContainer(dws, CALENDAR_CONTAINER_NAME);
        }
        else
        {
            calendarContainer = siteService.createContainer(dws, CALENDAR_CONTAINER_NAME, ContentModel.TYPE_FOLDER, null);
        }

        if (calendarContainer == null)
        {
            throw new VtiHandlerException(VtiHandlerException.LIST_NOT_FOUND);
        }

        if (meeting.getSubject() == null)
        {
            throw new RuntimeException(getMessage("vti.meeting.error.no_subject"));
        }

        final Map<QName, Serializable> props = fillMeetingProperties(meeting, true);
        props.put(CalendarModel.PROP_IS_OUTLOOK, true);

        final NodeRef finalcalendarContainer = calendarContainer;
        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute()
            {
                nodeService.createNode(finalcalendarContainer, ContentModel.ASSOC_CONTAINS, 
                            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, (String) props.get(ContentModel.PROP_NAME)), 
                            CalendarModel.TYPE_EVENT, props);
                return null;
            }
        });

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
        title = removeIllegalCharacters(title);

        if (title.equals("_"))
        {
            throw new RuntimeException(getMessage("vti.meeting.error.workspace_name"));
        }

        SiteInfo siteInfo = null;
        String newTitle = null;
        int i = 0;
        do
        {
            newTitle = title + (i == 0 ? "" : "_" + i);
            siteInfo = siteService.getSite(newTitle);
            i++;
        } while (siteInfo != null);

        shareUtils.createSite(user, MEETING_WORKSPACE_NAME, newTitle, newTitle, "", true);

        return newTitle;
    }

    /**
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#getMeetingWorkspaces(boolean)
     */
    public List<String> getMeetingWorkspaces(boolean recurring)
    {
        List<String> resultList = new ArrayList<String>();
        for (SiteInfo siteInfo : siteService.listSites(authenticationService.getCurrentUserName()))
        {
            String memberRole = siteService.getMembersRole(siteInfo.getShortName(), authenticationService.getCurrentUserName());
            if (MEETING_WORKSPACE_NAME.equals(siteInfo.getSitePreset()) && SiteModel.SITE_MANAGER.equals(memberRole))
            {
                resultList.add(siteInfo.getShortName());
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

        if (requestFlags == 3)
        {
            info.getTemplateLanguages().add(new Integer(1033));
        }

        if (requestFlags == 5)
        {
            info.getTemplates().add(MwsTemplate.getDefault());
        }

        if (requestFlags == 8)
        {
            MwsStatus siteStatus = MwsStatus.getDefault();

            NodeRef calendarNodeRef = siteService.getContainer(siteName, CALENDAR_CONTAINER_NAME);

            List<FileInfo> childs = fileFolderService.list(calendarNodeRef);
            int count = 0;

            for (FileInfo child : childs)
            {
                if (nodeService.getType(child.getNodeRef()).equals(CalendarModel.TYPE_EVENT))
                {
                    count++;
                }
            }

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

        final NodeRef meetingNodeRef = getEvent(calendarNodeRef, uid);

        if (meetingNodeRef == null)
        {
            throw new RuntimeException(getMessage("vti.meeting.error.no_meeting"));
        }

        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute()
            {
                if (recurrenceId == 0)
                {
                    nodeService.deleteNode(meetingNodeRef);
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
                    nodeService.createNode(meetingNodeRef, CalendarModel.ASSOC_IGNORE_EVENT_LIST, QName.createQName(NamespaceService.CONTENT_MODEL_PREFIX, "ignoreEvent_" + recurrenceId + "_"
                            + GUID.generate()), CalendarModel.TYPE_IGNORE_EVENT, properties);
                }
                return null;
            }
        });
    }

    /**
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#updateMeetingFromICal(String, MeetingBean, boolean)
     */
    public void updateMeetingFromICal(final String siteName, MeetingBean meeting, boolean ignoreAttendees)
    {
        NodeRef calendarContainer = null;

        calendarContainer = siteService.getContainer(siteName, CALENDAR_CONTAINER_NAME);

        if (calendarContainer == null)
        {
            throw new VtiHandlerException(getMessage("vti.meeting.error.no_site_update"));
        }

        final NodeRef eventRef = getEvent(calendarContainer, meeting.getId());

        if (eventRef == null)
        {
            throw new VtiHandlerException(getMessage("vti.meeting.error.no_meeting_update"));
        }

        final Map<QName, Serializable> eventProps = nodeService.getProperties(eventRef);
        eventProps.putAll(fillMeetingProperties(meeting, false));

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
                nodeService.setProperties(eventRef, eventProps);

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
     * @see org.alfresco.module.vti.handler.MeetingServiceHandler#deleteWorkspace(String, SessionUser)
     */
    public void deleteWorkspace(String siteName, SessionUser user) throws Exception
    {
        SiteInfo siteInfo = siteService.getSite(siteName);

        if (siteInfo == null)
        {
            throw new RuntimeException("vti.meeting.error.no_site");
        }

        if (!siteInfo.getSitePreset().equals(MEETING_WORKSPACE_NAME))
        {
            throw new RuntimeException("vti.meeting.error.bad_type");
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

    protected Map<QName, Serializable> fillMeetingProperties(MeetingBean meeting, boolean generateName)
    {
        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        if (generateName)
        {
            String name = generateEventName();
            props.put(ContentModel.PROP_NAME, name);
        }
        if (meeting.getSubject() != null)
        {
            props.put(CalendarModel.PROP_WHAT, meeting.getSubject());
        }
        if (meeting.getLocation() != null)
        {
            props.put(CalendarModel.PROP_WHERE, meeting.getLocation());
        }
        
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
            meeting.setEndDate(to.getTime());
        }
        
        props.put(CalendarModel.PROP_FROM_DATE, meeting.getStartDate());
        props.put(CalendarModel.PROP_TO_DATE, meeting.getEndDate());
        props.put(CalendarModel.PROP_DESCRIPTION, "");
        props.put(CalendarModel.PROP_OUTLOOK_UID, meeting.getId());
        props.put(CalendarModel.PROP_RECURRENCE_RULE, meeting.getReccurenceRule());
        props.put(CalendarModel.PROP_RECURRENCE_LAST_MEETING, meeting.getLastMeetingDate()); 
        return props;
    }

    private String generateEventName()
    {
        long timestamp = new Date().getTime();
        long random = Math.round(Math.random() * 10000);

        return timestamp + "-" + random + ".ics";
    }

    private NodeRef getEvent(NodeRef calendarNodeRef, String uid)
    {
        NodeRef result = nodeService.getChildByName(calendarNodeRef, ContentModel.ASSOC_CONTAINS, uid + ".ics");

        if (result == null)
        {
            List<NodeRef> nodeRefs = searchService.selectNodes(calendarNodeRef, "*//.[@" + CalendarModel.PROP_OUTLOOK_UID.toPrefixString(namespaceService) + "='" + uid + "']", null,
                    namespaceService, false);
            if (nodeRefs != null && nodeRefs.size() == 1)
            {
                result = nodeRefs.get(0);
            }
        }

        return result;
    }

    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }

    public void setShareUtils(ShareUtils shareUtils)
    {
        this.shareUtils = shareUtils;
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

    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
}
