/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.share.ShareLink;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;

/**
 * My documents dashlet object, holds all element of the HTML page relating to
 * share's my documents dashlet on dashboard page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class MyDocumentsDashlet extends AbstractDashlet implements Dashlet
{
    private static final String DATA_LIST_CSS_LOCATION = "h3.filename > a";
    private static final String DASHLET_DIV_CONTAINER_PLACEHOLDER = "div.dashlet.my-documents";

    /**
     * Constructor.
     */
    protected MyDocumentsDashlet(WebDrone drone)
    {
        super(drone, By.cssSelector(DASHLET_DIV_CONTAINER_PLACEHOLDER));
    }

    @SuppressWarnings("unchecked")
    public MyDocumentsDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyDocumentsDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * The collection of documents displayed on my documents dashlet.
     * 
     * @return List<ShareLink> links
     */
    public synchronized List<ShareLink> getDocuments()
    {
        return getList(DATA_LIST_CSS_LOCATION);
    }

    /**
     * Selects a document that appears on my documents dashlet by the matching name and
     * clicks on the link.
     */
    public synchronized ShareLink selectDocument(final String title)
    {
        return getLink(DATA_LIST_CSS_LOCATION, title);
    }

    @SuppressWarnings("unchecked")
    public MyDocumentsDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                timer.start();
                synchronized (this)
                {
                    try
                    {
                        this.wait(100L);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                if (isEmpty(DASHLET_DIV_CONTAINER_PLACEHOLDER))
                {
                    // There are no results
                    break;
                }
                else if (isVisibleResults())
                {
                    // populate results
                    break;
                }
                timer.end();
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }
}
