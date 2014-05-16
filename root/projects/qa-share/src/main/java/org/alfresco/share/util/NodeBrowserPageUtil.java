/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.util;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.adminconsole.NodeBrowserPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.share.adminconsole.NodeBrowserPage.*;

/**
 *  NOT FOR CLOUD!
 *
 * @author Aliaksei Boole
 */
public class NodeBrowserPageUtil
{

    private static final Log logger = LogFactory.getLog(NodeBrowserPageUtil.class);

    /**
     * Open NodeBrowser page from any share page. Work if you are admin.
     * NOT FOR CLOUD!
     *
     * @param drone
     * @return
     */
    public static NodeBrowserPage openNodeBrowserPage(WebDrone drone)
    {
        try
        {
            SharePage page = drone.getCurrentPage().render();
            return page.getNav().getNodeBrowserPage();
        }
        catch (PageRenderTimeException e)
        {
            throw new PageOperationException("Node Browser Page does not render in time. May be you trying use method not as administrator OR it's bug.", e);
        }
    }

    /**
     * Execute query on NodeBrowserPage.
     * NOT FOR CLOUD!
     *
     * @param drone
     * @param query
     * @param queryType
     * @param store
     */
    public static NodeBrowserPage executeQuery(WebDrone drone, String query, QueryType queryType, Store store)
    {
        checkNotNull(query, "query");
        checkNotNull(queryType, "queryType");
        checkNotNull(store, "sore");
        return queryExec(drone, query, queryType, store);
    }

    /**
     * Execute query on NodeBrowserPage.
     * NOT FOR CLOUD!
     *
     * @param drone
     * @param query
     * @param queryType
     */
    public static NodeBrowserPage executeQuery(WebDrone drone, String query, QueryType queryType)
    {
        checkNotNull(query, "query");
        checkNotNull(queryType, "queryType");
        return queryExec(drone, query, queryType, null);
    }

    /**
     * Execute query on NodeBrowserPage.
     * NOT FOR CLOUD!
     *
     * @param drone
     * @param query
     * @param store
     */
    public static NodeBrowserPage executeQuery(WebDrone drone, String query, Store store)
    {
        checkNotNull(query, "query");
        checkNotNull(store, "sore");
        return queryExec(drone, query, null, store);
    }

    /**
     * Execute query on NodeBrowserPage.
     * NOT FOR CLOUD!
     *
     * @param drone
     * @param query
     */
    public static NodeBrowserPage executeQuery(WebDrone drone, String query)
    {
        checkNotNull(query, "query");
        return queryExec(drone, query, null, null);
    }

    /**
     * Execute query on NodeBrowserPage.
     * NOT FOR CLOUD!
     *
     * @param drone
     * @param queryType
     * @param store
     * @return
     */
    public static NodeBrowserPage executeQuery(WebDrone drone, QueryType queryType, Store store)
    {
        checkNotNull(store, "store");
        checkNotNull(queryType, "queryType");
        return queryExec(drone, null, queryType, store);
    }

    /**
     * Execute query on NodeBrowserPage.
     * NOT FOR CLOUD!
     *
     * @param drone
     * @param store
     * @return
     */
    public static NodeBrowserPage executeQuery(WebDrone drone, Store store)
    {
        checkNotNull(store, "store");
        return queryExec(drone, null, null, store);
    }

    /**
     * Execute query on NodeBrowserPage. If param null not change page state.
     *
     * @param drone
     * @param query
     * @param queryType
     * @param store
     */
    private static NodeBrowserPage queryExec(WebDrone drone, String query, QueryType queryType, Store store)
    {
        NodeBrowserPage nodeBrowserPage = drone.getCurrentPage().render();
        if (queryType != null)
        {
            nodeBrowserPage.selectQueryType(queryType);
        }
        if (store != null)
        {
            nodeBrowserPage.selectStore(store);
        }
        if (query != null)
        {
            nodeBrowserPage.fillQueryField(query);
        }
        nodeBrowserPage.clickSearchButton();
        return nodeBrowserPage.render();
    }
}
