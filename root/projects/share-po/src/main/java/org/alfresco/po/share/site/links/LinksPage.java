package org.alfresco.po.share.site.links;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.discussions.TopicDirectoryInfo;
import org.alfresco.po.share.site.discussions.TopicDirectoryInfoImpl;
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
 * Site Links Page object
 * relating to Share site Links page
 *
 * @author Marina.Nenadovets
 */
@SuppressWarnings("unused")
public class LinksPage extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By NEW_LINK_BTN = By.cssSelector("[id$='create-link-button']");
    private static final By EDIT_LINK_LINK = By.cssSelector(".edit-link>a>span");
    private static final By DELETE_LINK_LINK = By.cssSelector(".delete-link>a>span");
    private static final By LINKS_CONTAINER = By.cssSelector("tbody[class*='data']>tr");

    /**
     * Constructor
     */
    public LinksPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinksPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public LinksPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinksPage render(long time)
    {
        return render(new RenderTime(time));
    }

    public AddLinkForm clickNewLink()
    {
        try
        {
            drone.findAndWait(NEW_LINK_BTN).click();
            waitUntilAlert();
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to locate New Links button");
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        return new AddLinkForm(drone);
    }

    /**
     * Method to create a link
     *
     * @param name
     * @param url
     * @return
     */
    public LinksDetailsPage createLink(String name, String url)
    {
        try
        {
            AddLinkForm addLinkForm = new AddLinkForm(drone);
            addLinkForm.setTitleField(name);
            addLinkForm.setUrlField(url);
            addLinkForm.clickSaveBtn();
            waitUntilAlert();
            return new LinksDetailsPage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find element");
        }
    }

    /**
     * Method to check if Create Link button is displayed
     *
     * @return true if enabled else false
     */
    public boolean isCreateLinkEnabled ()
    {
        String someButton = drone.findAndWait(NEW_LINK_BTN).getAttribute("class");
        if (someButton.contains("yui-button-disabled"))
        {
            return false;
        }
        else return true;
    }

    private LinkDirectoryInfo getLinkDirectoryInfo(final String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement row = null;

        try
        {
            row = drone.findAndWait(By.xpath(String.format("//a[text()='%s']/../../../..", title)), WAIT_TIME_3000);
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
        return new LinkDirectoryInfo(drone, row);
    }

    /**
     * Method to edit a link
     * @param linkTitle
     * @param linkNewTitle
     * @param url
     * @param desc
     * @param internalChkBox
     * @return
     */
     public LinksDetailsPage editLink (String linkTitle, String linkNewTitle, String url, String desc, boolean internalChkBox)
     {
         AddLinkForm addLinkForm = getLinkDirectoryInfo(linkTitle).clickEdit();
         addLinkForm.setTitleField(linkNewTitle);
         addLinkForm.setDescriptionField(desc);
         addLinkForm.setUrlField(url);
         if (internalChkBox)
         {
             addLinkForm.setInternalChkbox();
         }
         addLinkForm.clickSaveBtn();
         waitUntilAlert();
         return new LinksDetailsPage(drone);
     }

    /**
     * Method to delete a link
     *
     * @param title
     *
     * @return Links page
     */
    public LinksPage deleteLinkWithConfirm (String title)
    {
        getLinkDirectoryInfo(title).clickDelete();
        if (!drone.isElementDisplayed(PROMPT_PANEL_ID))
        {
            throw new ShareException("The prompt isn't popped up");
        }
        drone.findAndWait(CONFIRM_DELETE).click();
        waitUntilAlert();
        return new LinksPage(drone).render();
    }

    /**
     * Method to get the count of links
     *
     * @return number of links
     */
    public int getLinksCount()
    {
        try
        {
            if (!drone.isElementDisplayed(LINKS_CONTAINER))
            {
                return 0;
            }
            return drone.findAll(LINKS_CONTAINER).size();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to get links count");
        }
    }
}
