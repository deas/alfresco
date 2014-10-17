package org.alfresco.po.share.site.links;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.share.site.links.LinksPage.SelectedAction.DELETE;

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
    private static final By RSS_LINK = By.xpath("//a[contains(@id,'rss-feed-button')]");
    private static final By SELECT_BUTTON = By.cssSelector("button[id$='-select-button-button']");
    private static final By SELECTED_ITEMS_ACTION_BUTTON = By.cssSelector("button[id$='-selected-i-dd-button']");
    private static final By POP_UP_DELETE_BUTTON = By.xpath("//span[@class='button-group']/span[1]//button");
    private static final String LINK_TITLE = "//h3[@class='link-title']/a[text()='%s']";
    private static final String TAG_NONE = "//a[contains(text(),'%s')]/../following-sibling::div/span[@class='tag-item']";
    private static final String TAG_NAME = "//a[contains(text(),'%s')]/../following-sibling::div/span[@class='tag-item']//a[contains(text(),'%s')]";
    private static final By NO_LINKS = By.cssSelector("span[class='datatable-msg-empty']");

    public enum CheckBoxAction
    {
        ALL(".links-action-select-all"), INVERT_SELECTION(".links-action-invert-selection"), SELECT_NONE(".select-button-container .links-action-deselect-all");

        CheckBoxAction(String cssSelector)
        {
            this.BY = By.cssSelector(cssSelector);
        }

        public final By BY;
    }

    public enum SelectedAction
    {
        DELETE(".links-action-delete"), DESELECT_ALL(".links-action-deselect-all");

        SelectedAction(String cssSelector)
        {
            this.BY = By.cssSelector(cssSelector);
        }

        public final By BY;
    }

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
            drone.findAndWait(NEW_LINK_BTN).click();
            AddLinkForm addLinkForm = new AddLinkForm(drone);
            addLinkForm.setTitleField(name);
            addLinkForm.setUrlField(url);
            addLinkForm.clickSaveBtn();
            waitUntilAlert();
            return new LinksDetailsPage(drone).render();
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
            drone.findAndWait(NEW_LINK_BTN).click();
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
     * /**
     * Method to create a link with tag and desc
     *
     * @param name
     * @param url
     * @param tagName
     * @return LinksDetailsPage
     */
    public LinksDetailsPage createLink(String name, String url, String description, String tagName)
    {
        try
        {
            drone.findAndWait(NEW_LINK_BTN).click();
            AddLinkForm addLinkForm = new AddLinkForm(drone);
            addLinkForm.setTitleField(name);
            addLinkForm.setUrlField(url);
            addLinkForm.setDescriptionField(description);
            if(!(tagName == null))
            {
                addLinkForm.addTag(tagName);
            }
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
    public boolean isCreateLinkEnabled()
    {
        return drone.find(NEW_LINK_BTN).isEnabled();
    }

    /**
     * Return object associated with links info block.
     * 
     * @param title
     * @return
     */
    public LinkDirectoryInfo getLinkDirectoryInfo(final String title)
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
     * 
     * @param linkTitle
     * @param linkNewTitle
     * @param url
     * @param desc
     * @param internalChkBox
     * @return
     */
    public LinksDetailsPage editLink(String linkTitle, String linkNewTitle, String url, String desc, boolean internalChkBox)
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
     * @return Links page
     */
    public LinksPage deleteLinkWithConfirm(String title)
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
            return drone.findAndWaitForElements(LINKS_CONTAINER).size();
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
            throw new ShareException("Unable to find link.");
        }
    }

    /**
     * Get rss feed url from link and navigate to.
     * 
     * @return
     */
    public RssFeedPage selectRssFeed(String username, String password)
    {
        checkNotNull(username);
        checkNotNull(password);
        try
        {
            String currentUrl = drone.getCurrentUrl();
            String rssUrl = drone.findAndWait(RSS_LINK).getAttribute("href");
            String protocolVar = PageUtils.getProtocol(currentUrl);
            rssUrl = rssUrl.replace(protocolVar,
                    String.format("%s%s:%s@", protocolVar, URLEncoder.encode(username, "UTF-8"), URLEncoder.encode(password, "UTF-8")));
            drone.navigateTo(rssUrl);
            return new RssFeedPage(drone).render();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css.", nse);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("Can't encode to url password or username.", e);
        }
        throw new PageOperationException("Not able to select RSS Feed option");
    }

    /**
     * Return Object for interacting with left filter panel.
     * 
     * @return
     */
    public LinksListFilter getLinkListFilter()
    {
        return new LinksListFilter(drone);
    }

    /**
     * Mimic interaction with button select"
     * 
     * @param checkBoxAction
     */
    public void selectAction(CheckBoxAction checkBoxAction)
    {
        checkNotNull(checkBoxAction);
        drone.findAndWait(SELECT_BUTTON).click();
        drone.findAndWait(checkBoxAction.BY).click();
        waitUntilAlert();
    }

    /**
     * Mimic interaction with button 'Selected Items'
     * 
     * @param selectedAction
     */
    public void selectedItemsAction(SelectedAction selectedAction)
    {
        checkNotNull(selectedAction);
        drone.findAndWait(SELECTED_ITEMS_ACTION_BUTTON).click();
        drone.findAndWait(selectedAction.BY).click();
        if (selectedAction.equals(DELETE))
        {
            drone.findAndWait(POP_UP_DELETE_BUTTON).click();
            waitUntilAlert();
        }

    }

    /**
     * Return true if link displayed, and return false if link is absent
     * 
     * @param title
     * @return
     */
    public boolean isLinkPresented(String title)
    {
        boolean isDisplayed;

        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        try
        {
            WebElement theItem = drone.find(By.xpath(String.format(LINK_TITLE, title)));
            isDisplayed = theItem.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            isDisplayed = false;
        }
        catch (TimeoutException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        return isDisplayed;
    }

    /**
     * Method to check tags for link page
     * if param tag is null will be return true if 'Tags: (None)'
     * 
     * @param title
     * @param tag
     * @return
     */
    public boolean checkTags(String title, String tag)
    {
        boolean isDisplayed;
        WebElement element;
        String tagXpath;

        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }
        if (tag == null)
        {

            tagXpath = String.format(TAG_NONE, title);
            try
            {
                element = drone.findAndWait(By.xpath(tagXpath));
                isDisplayed = element.getText().contains("None");
            }
            catch (NoSuchElementException ex)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Unable to locate link page or 'Tags: (None)'", ex);
                }
                throw new PageOperationException("Unable to locate link page or 'Tags: (None)'");
            }

        }
        else
        {

            tagXpath = String.format(TAG_NAME, title, tag);
            try
            {
                element = drone.findAndWait(By.xpath(tagXpath));
                isDisplayed = element.isDisplayed();
            }
            catch (NoSuchElementException te)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Unable to locate expected tag or link page", te);
                }
                throw new PageOperationException("Unable to locate expected tag or link page");
            }
        }
        return isDisplayed;
    }

    /**
     * Method check that no links displayed
     * 
     * @return true if no links displayed
     */
    public boolean isNoLinksDisplayed()
    {
        boolean isDisplayed;

        try
        {
            WebElement theItem = drone.findAndWait(NO_LINKS);
            isDisplayed = theItem.isDisplayed();
        }
        catch (TimeoutException te)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Unable to locate expected element.'There are currently no pages to display'", te);
            }
            throw new PageOperationException("Unable to locate expected element.'There are currently no pages to display'");
        }
        return isDisplayed;
    }
}
