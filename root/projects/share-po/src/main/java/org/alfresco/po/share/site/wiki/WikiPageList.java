package org.alfresco.po.share.site.wiki;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Created by Marina.Nenadovets on 02.05.14.
 */
@SuppressWarnings("unused")
public class WikiPageList extends WikiPage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final By BUTTON_CREATE = By.cssSelector("button[id$='default-create-button-button']");
    private static final By DETAILS_WIKI = By.cssSelector("a[href*='action=details']");
    private static final By DELETE_WIKI = By.cssSelector("button[id$='default-delete-button-button']");
    private static final By EDIT_WIKI = By.cssSelector("a[href*='action=edit']");
    private static final By WIKI_CONTAINER = By.cssSelector("div[id*='default-pagelist']>.wikipage");

    /**
     * Constructor
     */
    public WikiPageList(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WikiPageList render(RenderTime timer)
    {
        elementRender(timer,
            getVisibleRenderElement(BUTTON_CREATE));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WikiPageList render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WikiPageList render(long time)
    {
        return render(new RenderTime(time));
    }

    public WikiPageDirectoryInfo getWikiPageDirectoryInfo(final String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement row = null;

        try
        {
            row = drone.findAndWait(By.xpath(String.format("//a[text()='%s']/../..", title)), WAIT_TIME_3000);
            drone.mouseOverOnElement(row);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        catch (TimeoutException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        return new WikiPageDirectoryInfo (drone, row);
    }

    /**
     * Method to edit a wiki page based on title provided
     * @param title
     * @param txtLines
     * @return
     */
    public WikiPage editWikiPage (String title, String txtLines)
    {
        WikiPage wikiPage = getWikiPageDirectoryInfo(title).clickEdit();
        wikiPage.editWikiText(txtLines);
        logger.info("Edited Wiki page");
        return wikiPage.clickSaveButton();
    }

    /**
     * Method to rename a wiki page
     *
     * @param wikiOldTitle
     * @param wikiNewTitle
     * @return WikiPage object
     */
    public WikiPage renameWikiPage (String wikiOldTitle, String wikiNewTitle)
    {
        WikiPage wikiPage = getWikiPageDirectoryInfo(wikiOldTitle).clickDetails();
        return wikiPage.renameWikiPage(wikiNewTitle);
    }

    /**
     * Method to retrieve wiki count
     *
     * @return number of pages
     */
    public int getWikiCount()
    {
        try
        {
            if (!drone.isElementDisplayed(WIKI_CONTAINER))
            {
                return 0;
            }
            return drone.findAll(WIKI_CONTAINER).size();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to get wiki count");
        }
    }

    /**
     * Method to delete a wiki with confirmation
     * @param title
     * @return
     */
    public WikiPageList deleteWikiWithConfirm(String title)
    {
        try
        {
            getWikiPageDirectoryInfo(title).clickDelete();
            if(!drone.isElementDisplayed(PROMPT_PANEL_ID))
            {
                throw new ShareException("Prompt isn't popped up");
            }
            drone.findAndWait(CONFIRM_DELETE).click();
            waitUntilAlert();
            logger.info("Deleted Wiki page");
            return new WikiPageList(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to delete wiki");
        }
    }
}
