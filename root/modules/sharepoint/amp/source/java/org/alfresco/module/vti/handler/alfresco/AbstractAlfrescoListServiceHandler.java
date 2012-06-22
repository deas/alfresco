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

import org.alfresco.model.ContentModel;
import org.alfresco.model.DataListModel;
import org.alfresco.model.ForumModel;
import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.metadata.model.ListTypeBean;
import org.alfresco.repo.calendar.CalendarModel;
import org.alfresco.repo.calendar.CalendarServiceImpl;
import org.alfresco.repo.discussion.DiscussionServiceImpl;
import org.alfresco.repo.links.LinksModel;
import org.alfresco.repo.links.LinksServiceImpl;
import org.alfresco.repo.wiki.WikiServiceImpl;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Parent class for the Alfresco implementation of {@link ListServiceHandler}
 * 
 * @author Nick Burch
 */
public abstract class AbstractAlfrescoListServiceHandler implements ListServiceHandler
{
    protected static Log logger = LogFactory.getLog(AbstractAlfrescoListServiceHandler.class);
    
    protected static final String DATALIST_CONTAINER = "dataLists";

    // These are commonly used Types
    public static final ListTypeBean TYPE_DOCUMENT_LIBRARY = buildType(VtiBuiltInListType.DOCLIB);
    public static final ListTypeBean TYPE_DISCUSSIONS = buildType(VtiBuiltInListType.DISCUSS);
    public static final ListTypeBean TYPE_LINKS = buildType(VtiBuiltInListType.LINKS);
    public static final ListTypeBean TYPE_WIKI = buildType(VtiBuiltInListType.WIKI);
    public static final ListTypeBean TYPE_TASKS = buildType(VtiBuiltInListType.TASKS);

    /**
     * Builds the Type, or null if it's not 
     *  suitable to be used
     */
    protected static ListTypeBean buildType(VtiBuiltInListType type)
    {
       if(type.component != null)
       {
          boolean isDataList = false;
          if (type.component == DATALIST_CONTAINER)
          {
              isDataList = true;
          }
          
          ListTypeBean list = new ListTypeBean(
                type.id, type.type.id, isDataList,
                type.entryType, type.name, null, null
          );
          return list;
       }
       return null;
    }

    /**
     * This holds the details of all the standard List Templates
     *  that are build into SharePoint. We merge this list
     *  with the configured DataList types to produce our
     *  available list templates.
     */
    protected static enum VtiBuiltInListType
    {
       DOCLIB(101, VtiListBaseType.DOCUMENT_LIBRARY, "doclib", "documentLibrary", ContentModel.TYPE_CONTENT),
       SURVEY(102, VtiListBaseType.SURVEY, "survey", null, null),
       LINKS(103,  VtiListBaseType.GENERIC_LIST, "links", LinksServiceImpl.LINKS_COMPONENT, LinksModel.TYPE_LINK),
       ANNOUNCE(104, VtiListBaseType.GENERIC_LIST, "announce", null, null),
       CONTACTS(105, VtiListBaseType.GENERIC_LIST, "contacts", DATALIST_CONTAINER, DataListModel.TYPE_CONTACT),
       EVENTS(106,   VtiListBaseType.GENERIC_LIST, "events", CalendarServiceImpl.CALENDAR_COMPONENT, CalendarModel.TYPE_EVENT),
       TASKS(107,    VtiListBaseType.GENERIC_LIST, "tasks", DATALIST_CONTAINER, DataListModel.TYPE_TASK),
       DISCUSS(108,  VtiListBaseType.DISCUSSION_BOARD, "discuss", DiscussionServiceImpl.DISCUSSION_COMPONENT, ForumModel.TYPE_POST),
       PICTURE_LIBRARY(109, VtiListBaseType.GENERIC_LIST, "piclib", null, null),
       DATA_SOURCES(110, VtiListBaseType.GENERIC_LIST, "datasrcs", null, null),
       SITE_TEMPLATE_GALLERY(111, VtiListBaseType.GENERIC_LIST, null, null, null),
       WEB_PART_GALLERY(113, VtiListBaseType.GENERIC_LIST, null, null, null),
       LIST_TEMPLATE_GALLERY(114, VtiListBaseType.GENERIC_LIST, null, null, null),
       XML_FORMS(115, VtiListBaseType.GENERIC_LIST, "xmlform", null, null),
       NO_CODE_WORKFLOWS(117, VtiListBaseType.GENERIC_LIST, "nocodewf", null, null),
       CUSTOM_WORKFLOWS(118, VtiListBaseType.GENERIC_LIST, "workflowProcess", null, null),
       WIKI(119, VtiListBaseType.GENERIC_LIST, "webpagelib", WikiServiceImpl.WIKI_COMPONENT, ContentModel.TYPE_CONTENT),
       GRID_LIST(120, VtiListBaseType.GENERIC_LIST, "gridlist", null, null),
       NO_CODE_PUBLIC_WORKFLOWS(122, VtiListBaseType.GENERIC_LIST, "nocodepublicwf", null, null),
       ISSUE(1100, VtiListBaseType.ISSUE, "issue", DATALIST_CONTAINER, DataListModel.TYPE_ISSUE),
       ;
       
       public final int id;
       public final VtiListBaseType type;
       /** The name of the List */
       public final String name;
       /** The site component of the List */
       public final String component;
       /** The type of entries within the list */
       public final QName entryType;
       private VtiBuiltInListType(int id, VtiListBaseType type, String name, 
                                  String component, QName entryType)
       {
          this.id = id;
          this.type = type;
          this.name = name;
          this.component = component;
          this.entryType = entryType;
       }
    }
    protected static enum VtiListBaseType 
    {
       GENERIC_LIST(0),
       DOCUMENT_LIBRARY(1),
       DISCUSSION_BOARD(3),
       SURVEY(4),
       ISSUE(5);

       public final int id;
       private VtiListBaseType(int id)
       {
          this.id = id;
       }
    }
}
