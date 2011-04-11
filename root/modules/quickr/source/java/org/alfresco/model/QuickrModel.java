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
package org.alfresco.model;

import org.alfresco.service.namespace.QName;

public interface QuickrModel
{
    static final String QUICKR_MODEL_1_0_URI = "http://www.alfresco.org/model/quickr/1.0";
    static final String PROP_SHHET_MODEL_1_0_URI = "http://www.alfresco.org/model/quickr/psheet/1.0";
    static final String DRAFT_APPROVE__MODEL_1_0_URI = "http://www.alfresco.org/model/quickr/draft/approve/1.0";

    static final QName ASPECT_QUICKR_DOC_TYPE = QName.createQName(QUICKR_MODEL_1_0_URI, "docType");
    static final QName ASPECT_QUICKR_PROP_SHEET = QName.createQName(QUICKR_MODEL_1_0_URI, "propSheet");
    static final QName ASPECT_QUICKR_DRAFT_APPROVAL_TYPE = QName.createQName(QUICKR_MODEL_1_0_URI, "draftApprovals");
    static final QName ASPECT_QUICKR_VERSION_PROP = QName.createQName(QUICKR_MODEL_1_0_URI, "versioning");

    static final QName ASPECT_QUICKR_INITIAL_DRAFT = QName.createQName("http://www.alfresco.org/model/quickr/1.0", "draft");

}
