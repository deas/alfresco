/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.share.search.SearchBox;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.Page;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Abstract of an Alfresco Share HTML page.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 */
public abstract class SharePage extends Page
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final String USER_LOGGED_IN_LABEL = "navigation.dropdown.user";
    protected static final int WAIT_TIME_3000 = 3000;
    private boolean dojoSupport;
    protected static final By PROMPT_PANEL_ID = By.id("prompt"); 
    protected AlfrescoVersion alfrescoVersion;
    protected long popupRendertime;
    protected long elementWaitInSeconds;

    protected SharePage(WebDrone drone)
    {
        super(drone);
        alfrescoVersion = drone.getProperties().getVersion();
        dojoSupport = alfrescoVersion.isDojoSupported();
    }

    /**
     * Check if javascript message is displayed.
     * The message details the background action taking place.
     * Some possible messages are document being uploaded, site
     * being created.
     * @return if message displayed
     */
    protected boolean isJSMessageDisplayed()
    {
        try 
        {
            return drone.find(By.cssSelector("div.bd")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
        catch (StaleElementReferenceException ser)
        {
            drone.refresh();
            return isJSMessageDisplayed();
        }
    }

    /**
     * Basic render that checks if the page has rendered.
     * @param timer {@link RenderTime} 
     */
    public void basicRender(RenderTime timer)
    {
        try
        {
            timer.start();
            drone.isRenderComplete(timer.timeLeft());
        }
        finally
        {
            timer.end();
        }
    }

    /**
     * Verify if the Alfresco logo is present on the page.
     * 
     * @return true if logo element exists
     */
    public boolean isLogoPresent()
    {
        By selector;
        switch (alfrescoVersion)
        {
            case Enterprise41:
                selector = By.cssSelector("span.logo a img");
                break;
            case Enterprise42:
                selector = By.cssSelector("div.logo img");
                break;
                //Latest share 
            default:
                selector = By.cssSelector("div.alfresco-logo-Logo img");
                break;
        }
        return drone.find(selector).isDisplayed();
    }

    /**
     * Alfresco share based layout and style page title label element.
     * 
     * @return String page title label
     */
    public String getPageTitle()
    {
        String selector;
        switch (alfrescoVersion) 
        {
            case Enterprise41:
                selector = "h1.theme-color-3";
                break;
            case Cloud2:
                selector = "div.alf-menu-title span.alf-menu-title-text";
                break;
            default:
                selector = "a.alf-menu-title-text";
                break;
        }
        return drone.find(By.cssSelector(selector)).getText().trim();
    }

    /**
     * Verify share page title is present and matches the page
     * 
     * @return true if exists
     */
    public boolean isTitlePresent(final String title)
    {
        boolean titleExists = false;
        try
        {
            titleExists = title.equalsIgnoreCase(getPageTitle());
        }
        catch (NoSuchElementException e)
        {
        }
        return titleExists;
    }

    /**
     * Verifies if the element is visible on the page.
     * 
     * @param panelName the css location of the element
     * @return true if element is visible
     */
    public boolean panelExists(String panelName)
    {
        try
        {
            return drone.find(By.cssSelector(panelName)).isDisplayed();
        }
        catch (Exception e)
        {
            // Valid exception as element is not on page
        }
        return false;
    }

    /**
     * Gets the {@link LoginPage}
     * 
     * @return LoginPage page object
     */
    public LoginPage getLogin()
    {
        return new LoginPage(drone);
    }

    /**
     * Get the {@link Navigation}
     * 
     * @return Navigation page object
     */
    public Navigation getNav()
    {
        return new Navigation(drone);
    }

    /**
     * Perform inputing a search term in to the search box on the main
     * navigation.
     * 
     * @return Search page object
     * @throws Exception if error
     */
    public SearchBox getSearch()
    {
        return new SearchBox(drone,isDojoSupport());
    }

    /**
     * Helper to resolve the delete button from the collection of buttons.
     * 
     * @param button String button name value to find
     * @param elements List<WebElement> collection of buttons
     * @return {@link WebElement} delete button
     */
    public WebElement findButton(final String button, List<WebElement> elements)
    {
        WebElement result = null;
        for (WebElement element : elements)
        {
            String siteTitle = element.getText();
            if (button.equalsIgnoreCase(siteTitle))
            {
                result = element;
            }
        }
        if (result == null) { throw new NoSuchElementException("Can not find the delete button"); }
        return result;
    }

    /**
     * Helper method to disable flash on file upload component, as flash is not
     * supported by WebDriver.
     */
    public void disbaleFileUploadFlash()
    {
        drone.executeJavaScript("Alfresco.util.ComponentManager.findFirst('Alfresco.FileUpload').options.adobeFlashEnabled=false;");
    }
    /**
     * Change share file upload to single mode upload.
     * As selenium is unable to interact with flash we disable the normal file upload mode.
     * In addtion firefox does not display the html5 input element to send data hence
     * we use single mode. 
     */
    public void setSingleMode()
    {
        drone.executeJavaScript("var singleMode=Alfresco.util.ComponentManager.findFirst('Alfresco.HtmlUpload'); Alfresco.util.ComponentManager.findFirst('Alfresco.FileUpload').uploader=singleMode;");
    }

    /**
     * Verifies if a user is currently logged in
     * 
     * @return true if user is logged in
     */
    public boolean isLoggedIn()
    {
        boolean loggedin = false;
        try
        {
            loggedin = drone.findByKey(USER_LOGGED_IN_LABEL).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return loggedin;
    }

    /**
     * Get copy right text from alfresco footer.
     * @return String copy right text.
     */
    public String getCopyRight()
    {
        WebElement elemenent = drone.findAndWait(By.cssSelector("span.copyright"));
        return elemenent.getText();
    }
    /**
     * Waits for site pop up message to disappear to allow the drone to resume
     * operations on the page.
     * 
     * @param waitTime timer in milliseconds
     * @return true if message has gone
     */
    protected boolean canResume(final long waitTime)
    {
        RenderTime timer = new RenderTime(waitTime);
        boolean messagePresent = true;
        while (messagePresent)
        {
            try
            {
                timer.start();
                WebElement deletedMessage = drone.find(By.cssSelector("div.bd"));
                messagePresent = deletedMessage.isDisplayed();
                timer.end();
            }
            catch (TimeoutException te)
            {
                messagePresent = false;
            }
            catch (StaleElementReferenceException se)
            {
                messagePresent = false;
            }
            catch (PageRenderTimeException pe)
            {
                messagePresent = false;
                // if exception was thrown and caught, then the popup message
                // was not on
                // the page.
            }
        }
        return true;
    }
    /**
     * Default wait for site pop up message to disappear, this 
     * is currently set to 3 seconds. Once the popup disappears
     * drone can resume operations on the page as the focus is
     * off the div.bd and back on the main page.
     * 
     * @return true if message has gone
     */
    protected boolean canResume()
    {
        return canResume(WAIT_TIME_3000);
    }

    /**
     * Waits for given {@link ElementState} of all render elements when rendering a page.
     * If the given element not reach element state, it will time out and throw 
     * {@link TimeoutException}. If operation to find all elements
     * times out a {@link PageRenderTimeException} is thrown
     * 
     * Renderable elements will be scanned from class using {@link RenderWebElement} annotation.
     *
     * @param renderTime render timer
     */
    public void webElementRender(RenderTime renderTime)
    {
        if(renderTime == null)
        {
            throw new UnsupportedOperationException("RenderTime is required");
        }
        List<RenderElement> elements = new ArrayList<RenderElement>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) 
        {
            if(field.isAnnotationPresent(RenderWebElement.class))
            {
                if(field.getType().equals(By.class))
                {
                    RenderWebElement webElement = (RenderWebElement) field.getAnnotation(RenderWebElement.class);
                    field.setAccessible(true);
                    try 
                    {
                        elements.add(new RenderElement((By) field.get(this), webElement.state()));
                    } 
                    catch (IllegalArgumentException e) 
                    {
                        logger.error("Object may not be instance of By class " + e);
                    } 
                    catch (IllegalAccessException e) 
                    {
                        logger.error("Not able access the field: " + field.getName() + e);
                    }
                }
                else
                {
                    throw new PageOperationException("");
                }
            }
        }
        elementRender(renderTime, elements.toArray(new RenderElement[elements.size()]));
    }

    /**
     * Waits for given {@link ElementState} of all render elements when rendering a page.
     * If the given element not reach element state, it will time out and throw 
     * {@link TimeoutException}. If operation to find all elements
     * times out a {@link PageRenderTimeException} is thrown 
     *
     * @param renderTime render timer
     * @param RenderElement collection of {@link RenderElement}
     */
    public void elementRender(RenderTime renderTime, RenderElement... elements)
    {
        if(renderTime == null)
        {
            throw new UnsupportedOperationException("RenderTime is required");
        }
        if(elements == null || elements.length < 1)
        {
            throw new UnsupportedOperationException("RenderElements are required");
        }
        for(RenderElement element:elements)
        {
            try
            {
                renderTime.start();
                long waitSeconds = TimeUnit.MILLISECONDS.toSeconds(renderTime.timeLeft());
                element.render(drone, waitSeconds);
            }
            catch(TimeoutException e)
            {
                logger.error("Not able to render the element : " + element.getLocator().toString());
                throw new PageRenderTimeException("element not rendered in time.", e);
            }
            finally
            {
                renderTime.end(element.getLocator().toString());
            }
        }
    }


    /**
     * Wait for file to be present given path for maximum page loading time.
     * @param pathname Absolute Path Name with File Name.
     */
    public void waitForFile(final long time, String pathname)
    {
        waitForFile(new RenderTime(time), pathname);
    }

    /**
     * Wait for file to be present given path for maximum page loading time.
     * @param pathname Absolute Path Name with File Name.
     */
    public void waitForFile(String pathname)
    {
        waitForFile(new RenderTime(maxPageLoadingTime), pathname);
    }

    /**
     * Wait for file to be present given path.
     * @param renderTime Render Time
     * @param pathname Absolute Path Name with File Name.
     */
    protected void waitForFile(RenderTime renderTime, String pathname)
    {
        while (true)
        {
            try
            {
                renderTime.start();
                File file = new File(pathname);
                if(file.exists())
                {
                    break;
                }
            }
            finally
            {
                renderTime.end();
            }
        }
    }

    /**
     * <li>Click the element which passed and wait for given ElementState on the same element.</li>
     * <li>If the Element State not changed, then render the {@link SharePopup} Page, if it is rendered the return {@link SharePopup} page.</li>
     * 
     * @param locator
     * @param elementState
     * @return {@link HtmlPage}
     */
    protected HtmlPage submit(By locator, ElementState elementState)
    {
        try
        {
            WebElement button = drone.find(locator);
            String id = button.getAttribute("id");
            button.click();
            By locatorById = By.id(id);
            RenderTime time = new RenderTime(maxPageLoadingTime);
            time.start();
            while (true)
            {
                try
                {
                    switch (elementState)
                    {
                        case INVISIBLE:
                            drone.waitUntilElementDisappears(locatorById, elementWaitInSeconds);
                            break;
                        case DELETE_FROM_DOM:
                            drone.waitUntilElementDeletedFromDom(locatorById, elementWaitInSeconds);
                            break;
                        default:
                            throw new UnsupportedOperationException(elementState + "is not currently supported by submit.");
                    }
                }
                catch (TimeoutException e)
                {
                    SharePopup errorPopup = new SharePopup(drone);
                    try
                    {
                        errorPopup.render(new RenderTime(popupRendertime));
                        return errorPopup;
                    }
                    catch (PageRenderTimeException exception)
                    {
                        continue;
                    }
                }
                finally
                {
                    time.end(locatorById.toString());
                }
                break;
            }
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException te)
        {}
        throw new PageException("Not able to find the Page, may be locator missing in the page : " + locator.toString());
    }

    /**
     * Method to get element text for given locator.
     * If the element is not found, returns empty string
     * @param locator
     * @return
     */
    public String getElementText(By locator)
    {
        try
        {
            return drone.find(locator).getText();
        }
        catch (NoSuchElementException nse)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Element not found" + locator.toString());
            }
        }
        return "";
    }
    public void setAlfrescoVersion(AlfrescoVersion alfrescoVersion)
    {
        this.alfrescoVersion = alfrescoVersion;
    }
    public long getPopupRendertime() 
    {
        return popupRendertime;
    }

    public void setPopupRendertime(long popupRendertime) 
    {
        this.popupRendertime = popupRendertime;
    }
    public void setElementWaitInSeconds(long elementWaitInSeconds) 
    {
        this.elementWaitInSeconds = elementWaitInSeconds;
    }   

    /**
     * Helper to consistently get the Site Short Name.
     *
     * @param siteName String Name of the test for uniquely identifying / mapping test data with the test
     *
     * @return String site short name
     */
    public static String getSiteShortName(String siteName)
    {
        String siteShortName = "";
        String[] unAllowedCharacters = {"_", "!"};

        for(String removeChar:unAllowedCharacters)
        {
            siteShortName = siteName.replace(removeChar, "");
        }

        return siteShortName.toLowerCase();
    }

    /**
     * Wait until the black message box appear with text then wait until same black message disappear with text.
     * 
     * @param text - Text to be checked in the black message.
     * @param timeInSeconds - Time to wait in seconds.
     */
    protected void waitUntilMessageAppearAndDisappear(String text, long timeInSeconds)
    {
        drone.waitUntilElementDisappears(By.cssSelector("div.bd>span.message"), timeInSeconds);
    }
    /**
     * Return the {@link RenderElement} of the action message.
     * Checks that the black box with the message is not showing
     * on the page or showing pending state passed invisible vs visible.
     * @param state {@link ElementState} the visiblity state of element
     * @return {@link RenderElement} of action message based on state
     */
    public RenderElement getActionMessageElement(ElementState state)
    {
        String messageSelector = "div#message, div.bd";
        if(AlfrescoVersion.Enterprise41.equals(drone.getProperties().getVersion()))
        {
            messageSelector = "div.bd";
        }
        return new RenderElement(By.cssSelector(messageSelector), ElementState.INVISIBLE);
    }

    /**
     * Find the all the elements for given locator and returns the first visible {@link WebElement}.
     * It could be used to elemanate the hidden element with same locators.
     * 
     * @param locator
     * @return {@link WebElement}
     */
    protected WebElement getVisibleElement(By locator)
    {
        List<WebElement> searchElements = drone.findAll(locator);
        for (WebElement webElement : searchElements)
        {
            if(webElement.isDisplayed())
            {
                return webElement;
            }
        }
        throw new PageOperationException("Not able find the visible element for given locator : " + locator.toString());
    }

    protected boolean isDojoSupport()
    {
        return dojoSupport;
    }

    /**
     * Returns the validation message from the validation popup balloon for the web element
     * or an empty string if there is no message or the field is not validated.
     * 
     * @param locator
     * @return The validation message
     */
    public String getValidationMessage(By locator)
    {

        try
        {
            return drone.find(locator).getAttribute("alf-validation-msg");
        }
        catch (NoSuchElementException exception)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace(exception);
            }
        }

        return "";
    }

    /**
     * Returns the validation message from the validation popup balloon for the web element
     * or an empty string if there is no message or the field is not validated.
     * 
     * @param element
     * @return The validation message
     */
    public String getValidationMessage(WebElement webElement)
    {
        String message = webElement.getAttribute("alf-validation-msg");

        return (message == null ? "" : message);
    }
}