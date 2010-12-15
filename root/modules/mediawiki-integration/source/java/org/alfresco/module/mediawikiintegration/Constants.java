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
package org.alfresco.module.mediawikiintegration;

import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.namespace.QName;

/**
 * MediaWiki integration model constants 
 * 
 * @author Roy Wetherall
 */
public interface Constants 
{
    /** Namespace details */
    public static final String NAMESPACE = "http://www.alfresco.org/model/mediawikiintegration/1.0";
    public static final String CONFIG_NAMESPACE = "http://www.alfresco.org/model/mediawikiintegrationconfigproperty/1.0";
    public static final String PREFIX = "mw";
    public static final String CONFIG_PREFIX = "mwcp";
    
    /** MediWiki main page URL */
    public static final String MEDIAWIKI_PAGE_URL = "alfresco-php://{0}?title={1}";
    
    /** MediaWiki Type */
    public static final QName TYPE_MEDIAWIKI = QName.createQName(NAMESPACE, "mediaWiki");
    public static final QName ASSOC_CONFIG = QName.createQName(NAMESPACE, "config");
    
    /** MediaWiki Config Type */
    public static final QName TYPE_MEDIAWIKI_CONFIG = QName.createQName(NAMESPACE, "mediaWikiConfig");
    public static final QName PROP_SITENAME         = QName.createQName(CONFIG_NAMESPACE, "wgSitename");
    public static final QName PROP_DB_TYPE          = QName.createQName(CONFIG_NAMESPACE, "wgDBtype");
    public static final QName PROP_DB_SERVER        = QName.createQName(CONFIG_NAMESPACE, "wgDBserver");
    public static final QName PROP_DB_NAME          = QName.createQName(CONFIG_NAMESPACE, "wgDBname");
    public static final QName PROP_DB_USER          = QName.createQName(CONFIG_NAMESPACE, "wgDBuser");
    public static final QName PROP_DB_PASSWORD      = QName.createQName(CONFIG_NAMESPACE, "wgDBpassword");
    public static final QName PROP_DB_PORT          = QName.createQName(CONFIG_NAMESPACE, "wgDBport");
    public static final QName PROP_DB_PREFIX        = QName.createQName(CONFIG_NAMESPACE, "wgDBprefix");
    public static final QName PROP_SQL_DROP_TABLES  = QName.createQName(NAMESPACE, "sqlDropTables");
    
    /** Site custom properties aspect */
    public static final QName ASPECT_SITE_CUSTOM_PROPERTIES = QName.createQName(NAMESPACE, "siteCustomProperties");
    public static final QName PROP_MEDIA_WIKI_URL = QName.createQName(SiteModel.SITE_CUSTOM_PROPERTY_URL, "mediaWikiURL");    
}
