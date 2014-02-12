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
 */
package org.alfresco.po.share.site;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * Manage rules page object.
 * @author Michael Suzuki
 * @version 1.7.1
 */
public class ManageRulesPage extends SharePage
{

    public ManageRulesPage(WebDrone drone)
    {
        super(drone);
    }

    private RenderElement RULES_HEADER = RenderElement.getVisibleRenderElement(By.cssSelector("div#bd > div[id$='folder-rules']"));
    @SuppressWarnings("unchecked")
    @Override
    public ManageRulesPage render(RenderTime timer)
    {
        elementRender(timer, RULES_HEADER);
        return this;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ManageRulesPage render(long time)
    {
        return render(new RenderTime(time));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ManageRulesPage render()
    {
        return render(maxPageLoadingTime);
    }
    /**
     * Action of selecting create rules link.
     * @return {@link RulesPage} page response
     */
    public RulesPage selectCreateRules()
    {
        drone.find(By.partialLinkText("Create Rules")).click();
        return new RulesPage(drone);
    }
}
