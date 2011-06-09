/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import static org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel.TYPE_WEB_ROOT;
import static org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel.TYPE_WEB_SITE;

import java.util.Map;

import org.alfresco.repo.publishing.AbstractChannelType;
import org.alfresco.service.namespace.QName;

/**
 * @author Nick Smith
 * @since 4.0
 *
 */
public class WebSiteChannelType extends AbstractChannelType
{
    public static String ID = "WebSiteChannelType";
    /**
    * {@inheritDoc}
    */
    public String getId()
    {
        return ID;
    }

    /**
    * {@inheritDoc}
    */
    public Map<String, String> getCapabilities()
    {
        return null;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public QName getChannelNodeType()
    {
        return TYPE_WEB_SITE;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public QName getContentRootNodeType()
    {
        return TYPE_WEB_ROOT;
    }
}
