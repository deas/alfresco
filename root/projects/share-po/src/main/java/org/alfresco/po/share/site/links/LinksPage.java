package org.alfresco.po.share.site.links;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.discussions.TopicDirectoryInfo;
import org.alfresco.po.share.site.discussions.TopicDirectoryInfoImpl;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
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

    @RenderWebElement
    private static final By NEW_LINK_BTN = By.cssSelector("button[id*='default-create-link']");
    @RenderWebElement
    private static final By LINK_FILTER = By.cssSelector(".filter.links-filter");
    @RenderWebElement
    private static final By ALL_LINK_TITLE = By.cssSelector("div[id$='default-listTitle']");
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
        webElementRender(timer);
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
     * Method to create a link with tag
     *
     * @param name
     * @param url
     * @param tagName
     * @return LinksDetailsPage
     */
    public LinksDetailsPage createLink(String name, String url, String tagName)
    {
        try
        {
            AddLinkForm addLinkForm = new AddLinkForm(drone);
            addLinkForm.setTitleField(name);
            addLinkForm.setUrlField(url);
            addLinkForm.addTag(tagName);
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
        return drone.find(NEW_LINK_BTN).isEnabled();
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
        LinkDirectoryInfo theItem = getLinkDirectoryInfo(title);
        theItem.clickDelete();
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

    /**
     * Method to verify whether edit link is displayed
     *
     * @param linkName
     * @return boolean
     */
    public boolean isEditLinkDisplayed(String linkName)
    {
        return getLinkDirectoryInfo(linkName).isEditDisplayed();
    }

    /**
     * Method to verify whether delete link is displayed
     *
     * @param linkName
     * @return boolean
     */
    public boolean isDeleteLinkDisplayed(String linkName)
    {
        return getLinkDirectoryInfo(linkName).isDeleteDisplayed();
    }

    /**
     * Method to click to the link
     *
     * @param linkTitle
     * @return LinksDetailsPage
     */
    public LinksDetailsPage clickLink(String linkTitle)
    {
        try
        {
            WebElement link = drone.findAndWait(By.xpath("//a[text()='" + linkTitle + "']"));
            link.click();
            return new LinksDetailsPage(drone);
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to get links count");
        }
    }
}
