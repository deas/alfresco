/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.po.alfresco;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Created by ivan.kornilov on 22.04.2014.
 */

public class TenantAdminConsolePage extends SharePage
{
    private final By INPUT_FIELD = By.xpath("//input[@id='searchForm:command']");
    private final By SUBMIT_BUTTON = By.xpath("//input[@id='searchForm:submitCommand']");
    private final By CLOSE_BUTTON = By.xpath("//*[@id='TenantAdmin-console-title:_idJsp1']");

    public TenantAdminConsolePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TenantAdminConsolePage render(RenderTime renderTime)
    {
        elementRender(renderTime,
            getVisibleRenderElement(INPUT_FIELD),
            getVisibleRenderElement(SUBMIT_BUTTON),
            getVisibleRenderElement(CLOSE_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TenantAdminConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TenantAdminConsolePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for click Close Button
     *
     * @param
     * @return
     */
    public void clickClose()
    {
        drone.findAndWait(CLOSE_BUTTON).click();

    }

    /**
     * Method to return Tenant Admin Console URL
     *
     * @param shareUrl
     * @return String
     */
    public String getTenantURL(String shareUrl)
    {
        String tenantUrl = PageUtils.getProtocol(shareUrl) + PageUtils.getAddress(shareUrl) + "/alfresco/faces/jsp/admin/tenantadmin-console.jsp";
        return tenantUrl;
    }

    /**
     * Method for create tenant
     *
     * @param tenantName
     * @param password
     * @return
     */
    public void createTenant(String tenantName, String password)
    {
        drone.findAndWait(INPUT_FIELD).clear();
        drone.findAndWait(INPUT_FIELD).sendKeys(String.format("create %s %s", tenantName, password));
        drone.findAndWait(SUBMIT_BUTTON).click();
    }

    /**
     * Method for Managing tenants
     *
     * @param request
     * @return
     */
    public void sendCommands(String request)
    {
        drone.findAndWait(INPUT_FIELD).clear();
        drone.findAndWait(INPUT_FIELD).sendKeys(String.format("%s", request));
        drone.findAndWait(SUBMIT_BUTTON).click();
    }

    /**
     * Method to verify Tenant Admin Console Page is opened
     *
     * @return boolean
     */
    public boolean isOpened()
    {
        return drone.findAndWait(By.xpath("//span[@id='TenantAdmin-console-title:titleTenantAdminConsole']")).isDisplayed();
    }

    public String findText()
    {
        return drone.findAndWait(By.xpath("//*[@id='result']")).getText();

    }

    public boolean isEnabled()
    {
        return drone.findAndWait(INPUT_FIELD).isEnabled() && drone.findAndWait(SUBMIT_BUTTON).isEnabled();
    }

    @Override
    public String toString()
    {
        return "Tenant Admin Console";
    }

}
