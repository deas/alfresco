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
package org.alfresco.module.knowledgeBase;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;

/**
 * Knowledge base model contants
 * 
 * @author Roy Wetherall
 */
public interface KbModel
{
    public static final StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    
    public static final String KB_URI = "http://www.alfresco.org/model/knowledgebase/1.0";
    public static final String KB_PREFIX = "kb";
    
    public static final QName ASPECT_ARTICLE = QName.createQName(KB_URI, "article");
    public static final QName TYPE_KNOWLEDGE_BASE = QName.createQName(KB_URI, "knowledgeBase");
    
    public static final QName PROP_KB_ID = QName.createQName(KB_URI, "kbId");
    public static final QName PROP_STATUS = QName.createQName(KB_URI, "status");
    public static final QName PROP_VISIBILITY = QName.createQName(KB_URI, "visibility");
    
    public static final QName ASSOC_KNOWLEDGE_BASE = QName.createQName(KB_URI, "knowledgeBase");
    public static final QName ASSOC_PUBLISHED = QName.createQName(KB_URI, "published");
    
    // Group names
    public static final String GROUP_INTERNAL = "GROUP_KnowledgeBase_Internal";
    public static final String GROUP_TIER_1 = "GROUP_KnowledgeBase_Partners";
    public static final String GROUP_TIER_2 = "GROUP_KnowledgeBase_Customers";
    
    // Categories
    public static final NodeRef STATUS_DRAFT = new NodeRef(SPACES_STORE, "kb:status-draft");
    public static final NodeRef STATUS_PENDING = new NodeRef(SPACES_STORE, "kb:status-pending");
    public static final NodeRef STATUS_PUBLISHED = new NodeRef(SPACES_STORE, "kb:status-published");
    public static final NodeRef STATUS_ARCHIVED = new NodeRef(SPACES_STORE, "kb:status-archived");
    
    // Visibility
    public static final NodeRef VISIBILITY_INTERNAL = new NodeRef(SPACES_STORE, "kb:visibility-internal"); 
    public static final NodeRef VISIBILITY_TIER_1 = new NodeRef(SPACES_STORE, "kb:visibility-partners");
    public static final NodeRef VISIBILITY_TIER_2 = new NodeRef(SPACES_STORE, "kb:visibility-customers");
    public static final NodeRef VISIBILITY_TIER_3 = new NodeRef(SPACES_STORE, "kb:visibility-community");
}
