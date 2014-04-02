
/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 */package org.alfresco.po.share.enums;


import static org.testng.Assert.assertEquals;

import org.alfresco.po.share.site.document.Links;
import org.alfresco.po.share.site.document.Links.DetailsPageType;
import org.testng.annotations.Test;

/**
 * @author nshah
 *
 */
public class LinksTest
{
   
    
    @Test
    public void getCommonLinks()
    {
        
        assertEquals(Links.COPY_TO.getType(), Links.DetailsPageType.COMMON);
        assertEquals(Links.COPY_TO.getLink(DetailsPageType.COMMON), "div[id$='onActionCopyTo']");
        assertEquals(Links.MOVE_TO.getLink(DetailsPageType.COMMON), "div[id$='onActionMoveTo']");
        assertEquals(Links.DELETE_CONTENT.getLink(DetailsPageType.COMMON), "div[id$='onActionDelete']");
        assertEquals(Links.MANAGE_ASPECTS.getLink(DetailsPageType.COMMON), ".onActionManageAspects");
        assertEquals(Links.MANAGE_PERMISSION.getLink(DetailsPageType.COMMON), ".document-manage-granular-permissions");
        assertEquals(Links.CHNAGE_TYPE.getLink(DetailsPageType.COMMON), "div[id$='onActionChangeType']");
        assertEquals(Links.EDIT_PROPERTIES.getLink(DetailsPageType.COMMON), ".document-edit-metadata");
         
       
    }
    
    @Test
    public void getFolderLinks()
    {
        assertEquals(Links.MANAGE_RULES.getType(), Links.DetailsPageType.FOLDER);
        assertEquals(Links.MANAGE_RULES.getLink(DetailsPageType.FOLDER), ".folder-manage-rules");
        assertEquals(Links.DOWNLOAD_FOLDER.getLink(DetailsPageType.FOLDER), ".onActionFolderDownload");
        assertEquals(Links.VIEW_IN_EXPLORER.getLink(DetailsPageType.FOLDER), ".view-in-explorer");
    }
    
    @Test
    public void getDocumentLinks()
    {
        assertEquals(Links.DOWNLOAD_DOCUMENT.getType(), Links.DetailsPageType.DOCUMENT);
        assertEquals(Links.DOWNLOAD_DOCUMENT.getLink(DetailsPageType.DOCUMENT), ".document-download");
        assertEquals(Links.VIEW_IN_EXLPORER.getLink(DetailsPageType.DOCUMENT), ".document-view-content");
        assertEquals(Links.UPLOAD_DOCUMENT.getLink(DetailsPageType.DOCUMENT), ".onActionUploadNewVersion");
        assertEquals(Links.DOCUMENT_INLINE_EDIT.getLink(DetailsPageType.DOCUMENT), ".document-inline-edit");       
        assertEquals(Links.EDIT_OFFLINE.getLink(DetailsPageType.DOCUMENT), ".onActionEditOffline");
        assertEquals(Links.GOOGLE_DOCS_EDIT.getLink(DetailsPageType.DOCUMENT), ".onGoogledocsActionEdit");
        assertEquals(Links.START_WORKFLOW.getLink(DetailsPageType.DOCUMENT), ".onActionAssignWorkflow");
        assertEquals(Links.PUBLISH_ACTION.getLink(DetailsPageType.DOCUMENT), ".onActionPublish");
    }
}
