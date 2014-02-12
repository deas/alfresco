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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * The rules page is accessed from manage rules page.
 * When there are no rules the page will display
 * a message with link to create rules otherwise
 * the view seen is edit rules.
 * 
 * @author Michael Suzuki
 * @version 1.7.1
 */
public class RulesPage extends SharePage
{

    public RulesPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RulesPage render(RenderTime timer)
    {
        while(true)
        {
            synchronized (this)
            {
                try{ this.wait(100L); }catch (InterruptedException ie) { }
            }
            try
            {
                if(drone.find(By.cssSelector("div#bd > div[id$='_rule-edit']")).isDisplayed())
                {
                    break;
                }
            }
            catch (NoSuchElementException nse) { }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RulesPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RulesPage render()
    {
        return render(maxPageLoadingTime);
    }
    
    /**
     * Enters title in to title input field.
     * @param title String title value
     */
    public void enterTitle(final String title)
    {
        WebElement input = drone.find(By.name("title"));
        input.clear();
        input.sendKeys(title);
    }
    /**
     * Get title input filed value.
     * @param String title value
     */
    public String getTitle()
    {
        WebElement input = drone.find(By.name("title"));
        return input.getAttribute("value");
    }
    
    /**
     * Enters description in to description input field.
     * @param description String description value
     */
    public void enterDescription(final String description)
    {
        WebElement input = drone.find(By.name("description"));
        input.clear();
        input.sendKeys(description);
    }
    
    /**
     * Get description field value.
     */
    public String getDescription()
    {
        WebElement input = drone.find(By.name("description"));
        return input.getAttribute("value");
    }
    
    /**
     * Selecting a value from the action drop down.
     */
    public void selectPerformAction(final String label)
    {
        WebElement performActionDropdown = drone.findAndWait(By.cssSelector("div.name > select.config-name"));
        WebElement option = performActionDropdown.findElement(By.xpath(String.format("//option[text()='%s']",label)));
        option.click();
    }

    /**
     * Action mimicking select cancel button.
     * @return {@link ManageRulesPage} page response
     */
    public ManageRulesPage selectCancel()
    {
        drone.find(By.cssSelector("button[id$='default-cancel-button-button']")).click();
        return new ManageRulesPage(drone);
    }
    
    /**
     * Action mimicking select create button.
     * @return {@link ManageRulesPage} page response
     */
    public HtmlPage selectCreate()
    {
        drone.find(By.cssSelector("button[id$='default-create-button-button']")).click();
        canResume();
        return FactorySharePage.resolvePage(drone);
    }
}
