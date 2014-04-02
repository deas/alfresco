/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * 
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.po.share.dashlet.mydiscussions;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;


import org.alfresco.po.share.site.SitePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * Class that represents topics list page
 *  
 * @author jcule
 *
 */
public class TopicsListPage extends SitePage
{

    private static By NEW_TOPICS_TITLE = By.cssSelector("div.listTitle");
    
    public TopicsListPage(WebDrone drone)
    {
        super(drone);

    }

    @SuppressWarnings("unchecked")
    @Override
    public TopicsListPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(NEW_TOPICS_TITLE));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TopicsListPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TopicsListPage render()
    {
        return render(maxPageLoadingTime);
    }

}
