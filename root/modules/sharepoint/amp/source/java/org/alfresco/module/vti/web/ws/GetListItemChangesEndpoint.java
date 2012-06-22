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
package org.alfresco.module.vti.web.ws;

import java.util.Date;
import java.util.HashMap;

import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.alfresco.module.vti.metadata.model.ListInfoBean;

/**
 * Class for handling GetListItemChanges method from lists web service
 *
 * @author PavelYur
 */
public class GetListItemChangesEndpoint extends GetListItemsEndpoint
{
    /**
     * Constructor
     */
    public GetListItemChangesEndpoint(ListServiceHandler listHander, MethodHandler methodHandler)
    {
        super(listHander, methodHandler);
    }

    /**
     * TODO Support all kinds of lists
     * TODO Filter by change since date
     */
    @Override
    protected DocsMetaInfo getListInfo(String siteName, ListInfoBean list, String initialUrl, Date changesSince)
    {
        return methodHandler.getListDocuments(siteName, false, false, "", initialUrl, false, false, true, true, false, false, false, false, new HashMap<String, Object>(0), false);
    }
}