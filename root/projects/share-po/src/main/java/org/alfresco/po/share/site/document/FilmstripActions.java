/**
 * 
 */
package org.alfresco.po.share.site.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Encapsulates all the actions of the Filmstrip view on document library.
 * 
 * @author Abhijeet Bharade
 * 
 */
public class FilmstripActions extends SharePage
{

    private static final By FILMSTRIP_MAIN_DIV = By.cssSelector("div.alf-filmstrip.alf-gallery.documents");
    private static final By FILMSTRIP_NAV_NEXT = By.cssSelector("div[id$='default-filmstrip-nav-main-next']");
    private static final By FILMSTRIP_NAV_PREVIOUS = By.cssSelector("div[id$='_default-filmstrip-nav-main-previous']");
    private static final By FILMSTRIP_TAPE_NEXT = By.cssSelector("div[id$='-filmstrip-nav-next']");
    private static final By FILMSTRIP_TAPE_PREVIOUS = By.cssSelector("div[id$='-filmstrip-nav-previous']");
    private static final By FILMSTRIP_ITEM_DISLAYED = By.cssSelector("li[class$='item-selected'] div.alf-header div.alf-label");
    private static final By FILMSTRIP_NAV_HANDLE = By.cssSelector("div[id$='_default-filmstrip-nav-handle']");

    private Log logger = LogFactory.getLog(this.getClass());

    protected FilmstripActions(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Checks whether the tape with file names is open or closed.
     */
    public boolean isFilmstripTapeDisplpayed()
    {
        try
        {
            WebElement element = drone.find(FILMSTRIP_MAIN_DIV);
            if (element.isDisplayed())
            {

                element = drone.find(By.cssSelector("div[id$='_default-filmstrip']"));

                return !element.getAttribute("class").contains("alf-filmstrip-content-only");
            }

        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("isFilmstripTapeDisplpayed - Filmstrip view not loaded");
            }
        }
        throw new PageOperationException("Filmstrip view not loaded - FilmStrip tape may not be displayed.");
    }

    /**
     * Check whether FilmStrip view is displayed.
     * 
     * @return - boolean
     */
    public boolean isFilmStripViewDisplayed()
    {
        try
        {
            return drone.find(FILMSTRIP_MAIN_DIV).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("isFilmStripViewDisplayed - Filmstrip view not loaded", e);
            }
        }
        return false;
    }

    private HtmlPage clickFilmStripViewElement(By locator, String elementName)
    {
        String exceptionMessage = "";
        if (isFilmStripViewDisplayed())
        {
            try
            {
                WebElement element = drone.find(FILMSTRIP_MAIN_DIV).findElement(locator);
                if (element.isEnabled())
                {
                    element.click();
                    return FactorySharePage.resolvePage(drone);
                }
                else
                {
                    exceptionMessage = elementName + " not enable to click.";
                }
            }
            catch (NoSuchElementException nse)
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("selectNextFilmstripItem - Filmstrip view not loaded", nse);
                }
            }
            exceptionMessage = "Foreground " + elementName + " not visible.";
        }
        else
        {
            exceptionMessage = "Current view is not Film view, Please change view";
        }
        throw new PageOperationException(exceptionMessage);
    }

    /**
     * Selects the next arrow on filmstrip pane.
     */
    public HtmlPage selectNextFilmstripItem()
    {
        return clickFilmStripViewElement(FILMSTRIP_NAV_NEXT, "next arrow");
    }

    /**
     * Selects the previous arrow on filmstrip pane.
     */
    public HtmlPage selectPreviousFilmstripItem()
    {
        return clickFilmStripViewElement(FILMSTRIP_NAV_PREVIOUS, "previous arrow");
    }

    /**
     * Selects the next arrow on filmstrip pane.
     */
    public HtmlPage selectNextFilmstripTape()
    {
        HtmlPage page = clickFilmStripViewElement(FILMSTRIP_TAPE_NEXT, "Next Tape Arrow");
        try
        {
            drone.findAndWait(FILMSTRIP_TAPE_PREVIOUS, 3000);
        }
        catch (TimeoutException e)
        {
        }
        return page;
    }

    /**
     * Selects the previous arrow on filmstrip pane.
     */
    public HtmlPage selectPreviousFilmstripTape()
    {
        HtmlPage page = clickFilmStripViewElement(FILMSTRIP_TAPE_PREVIOUS, "Previous Tape Arrow");
        try
        {
            drone.findAndWait(FILMSTRIP_TAPE_NEXT, 3000);
        }
        catch (TimeoutException e)
        {
        }
        return page;
    }

    /**
     * Filmstrip Toggles the nav row.
     */
    public HtmlPage toggleNavHandleForFilmstrip()
    {
        HtmlPage page = clickFilmStripViewElement(FILMSTRIP_NAV_HANDLE, "Toggle Nav Handler");
        try
        {
            drone.findAndWait(By.cssSelector("div.alf-filmstrip.alf-gallery.documents.alf-filmstrip-content-only"), 2000);
            drone.findAndWait(By.cssSelector("div[id$='_default-filmstrip-nav-carousel']"), 2000);
        }
        catch (TimeoutException e)
        {
        }
        return page;
    }

    /**
     * Checks whether next arrow is present
     */
    public boolean isNextFilmstripArrowPresent()
    {
        return checkIfElementPresent(FILMSTRIP_NAV_NEXT, "next filmstrip arrow");
    }

    /**
     * Checks whether next arrow is present
     */
    public boolean isPreviousFilmstripArrowPresent()
    {
        return checkIfElementPresent(FILMSTRIP_NAV_PREVIOUS, "previous filmstrip arrow");
    }

    /**
     * Checks whether next arrow is present
     */
    public boolean isNextFilmstripTapeArrowPresent()
    {
        return checkIfElementPresent(FILMSTRIP_TAPE_NEXT, "next filmstrip thumbnail tape arrow");
    }

    /**
     * Checks whether next arrow is present
     */
    public boolean isPreviousFilmstripTapeArrowPresent()
    {
        return checkIfElementPresent(FILMSTRIP_TAPE_PREVIOUS, "previous filmstrip thumbnail tape arrow");
    }

    private boolean checkIfElementPresent(By byElement, String elementName)
    {
        try
        {
            WebElement element = drone.find(byElement);
            return element.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Element not present: " + elementName, nse);
            }
        }
        return false;
    }

    /**
     * Gets the filmStrip item opened for preview.
     */
    public String getDisplyedFilmstripItem()
    {
        try
        {
            return drone.findAndWait(FILMSTRIP_ITEM_DISLAYED).getText();
        }
        catch (TimeoutException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("getDisplyedFilmstripItem - Filmstrip view not loaded", nse);
            }
            throw new PageOperationException("Could not find the displayed file", nse);
        }
    }

    private HtmlPage sendKeysFilmStripViewElement(String elementName, CharSequence... keysToSend)
    {
        String exceptionMessage = "";
        if (isFilmStripViewDisplayed())
        {
            try
            {
                WebElement element = drone.find(By.cssSelector("div.alf-gallery-item-thumbnail"));
                if (element.isDisplayed())
                {
                    element.sendKeys(keysToSend);
                    return FactorySharePage.resolvePage(drone);
                }
                else
                {
                    exceptionMessage = elementName + " not displayed.";
                }
            }
            catch (NoSuchElementException nse)
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("selectNextFilmstripItem - Filmstrip view not loaded", nse);
                }
            }
            exceptionMessage = "Foreground " + elementName + " not visible.";
        }
        else
        {
            exceptionMessage = "Current view is not Film view, Please change view";
        }
        throw new PageOperationException(exceptionMessage);
    }

    /**
     * Filmstrip view send right arrow.
     */
    public HtmlPage sendKeyRightArrowForFilmstrip()
    {
        return sendKeysFilmStripViewElement("Send right key ", Keys.TAB, Keys.ARROW_RIGHT);
    }

    /**
     * Filmstrip view send left arrow.
     */
    public HtmlPage sendKeyLeftArrowForFilmstrip()
    {
        return sendKeysFilmStripViewElement("Send left key ", Keys.TAB, Keys.ARROW_LEFT);
    }

    /**
     * Filmstrip view send right arrow.
     */
    public HtmlPage sendKeyUpArrowForFilmstrip()
    {
        return sendKeysFilmStripViewElement("Send Up Arrow ", Keys.TAB, Keys.ARROW_UP);
    }

    /**
     * Filmstrip view send left arrow.
     */
    public HtmlPage sendKeyDownArrowForFilmstrip()
    {
        return sendKeysFilmStripViewElement("Send Down Arrow ", Keys.TAB, Keys.ARROW_DOWN);
    }

    /**
     * Filmstrip gets selected files.
     */
    public List<String> getSelectedFIlesForFilmstrip()
    {
        try
        {
            List<WebElement> selectedDivs = drone.findAll(By.cssSelector("div.alf-filmstrip-nav-item.alf-selected div.alf-label"));
            List<String> selectedFiles = new ArrayList<String>();
            for (WebElement webElement : selectedDivs)
            {
                selectedFiles.add(webElement.getText());
            }
            return selectedFiles;
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("getSelectedFIlesForFilmstrip - No items selected", nse);
            }
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public FilmstripActions render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public FilmstripActions render(final long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public FilmstripActions render(RenderTime timer)
    {
        try
        {
            elementRender(timer, RenderElement.getVisibleRenderElement(FILMSTRIP_MAIN_DIV));
        }
        catch (NoSuchElementException e)
        {
        }
        return this;
    }


}
