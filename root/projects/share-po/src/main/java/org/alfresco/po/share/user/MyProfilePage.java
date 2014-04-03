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
package org.alfresco.po.share.user;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * My profile page object, holds all element of the html page relating to
 * share's my profile page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class MyProfilePage extends SharePage
{

    private final By editProfileButton = By.cssSelector("button[id$='-button-edit-button'], button[id$='-button-following-button']");

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public MyProfilePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyProfilePage render(RenderTime timer)
    {
        elementRender(timer, RenderElement.getVisibleRenderElement(editProfileButton));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyProfilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyProfilePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if home page banner web element is present
     * 
     * @return true if exists
     */
    public boolean titlePresent()
    {
        boolean isPresent = false;
        String title = getPageTitle();
        isPresent = title.contains("Profile");
        return isPresent;
    }

    public ProfileNavigation getProfileNav()
    {
        return new ProfileNavigation(drone);
    }
}