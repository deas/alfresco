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
package org.alfresco.po.share;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Share Dialogue page object, holds all the methods relevant to Share Dialogue Page
 * 
 * @author Meenal Bhave
 * @since 4.3.0HBF
 */
public class ShareDialogue extends SharePage
{
    // private static final By SHARE_DIALOGUE = By.cssSelector("div.hd");
    // private static final By CLOSE_BUTTON = By.cssSelector("a.container-close");
    // private static final By TITLE_TEXT = By.cssSelector("div.bd");

    private static final By SHARE_DIALOGUE_PARENT = By.xpath("//div[@class='hd']/..");
    private static final By SHARE_DIALOGUE_HEADER = By.xpath("//div[@class='hd']");
    private static final By CLOSE_BUTTON = By.cssSelector("a.container-close");
    private static final By TITLE_TEXT_FILE_UPLOAD = By.xpath("//div[@class='hd']//span");

    // TODO: Identify UploadFile: //div[contains(@class,'hd') and contains(@id,'flash-upload')]

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public ShareDialogue(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ShareDialogue render(RenderTime timer)
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
            if (isShareDialogueDisplayed())
            {
                break;
            }
            timer.end();
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ShareDialogue render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ShareDialogue render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Helper method to click on the Close button to return to the original page
     */
    public HtmlPage clickClose()
    {
        WebElement dialogue = getDialogue();
        WebElement closeButton = drone.findElementDisplayed(CLOSE_BUTTON);
        closeButton.click();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Helper method to get the Dialogue title
     * 
     * @return String
     */
    public String getDialogueTitle()
    {
        try
        {
            WebElement dialogue = getDialogue().findElement(SHARE_DIALOGUE_HEADER);
            String title = dialogue.getText();
//            // TODO: There are 2 types of dialogues for now, with 2 diff css for getTitle. Other needs to be implemented
//            if (dialogue instanceof UploadFilePage)
//            {
//                title = dialogue.findElement(By.cssSelector("span")).getText();
//            }
            return title;
        }
        catch (NoSuchElementException nse)
        {
        }
        return null;
    }

    /**
     * Helper method to return true if Share Dialogue is displayed
     * 
     * @return boolean <tt>true</tt> is Share Dialogue is displayed
     */
    public boolean isShareDialogueDisplayed()
    {
        try
        {
            WebElement dialogue = getDialogue();
            if (dialogue != null && dialogue.isDisplayed())
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Helper method to return WebElement for the failure prompt
     * 
     * @return WebElement
     */
    private WebElement getDialogue()
    {
        try
        {
            WebElement shareDialogue = drone.findElementDisplayed(SHARE_DIALOGUE_PARENT);

            return shareDialogue;
        }
        catch (NoSuchElementException nse)
        {
            return null;
        }
    }

    // /**
    // * This function will return visible element with the specified selector
    // * @return - WebElement
    // */
    // protected WebElement findElementDisplayed(By selector)
    // {
    // List<WebElement> elementList = drone.findAll(selector);
    // for (WebElement elementSelected : elementList)
    // {
    // if (elementSelected.isDisplayed())
    // {
    // return elementSelected;
    // }
    // }
    // throw new NoSuchElementException("Element Not found");
    // }

}
