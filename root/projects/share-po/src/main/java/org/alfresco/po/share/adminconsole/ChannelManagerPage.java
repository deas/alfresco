package org.alfresco.po.share.adminconsole;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.thirdparty.flickr.FlickrUserPage;
import org.alfresco.po.thirdparty.flickr.YahooSignInPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.base.Preconditions.checkArgument;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author Roman.Chul
 */
public class ChannelManagerPage extends SharePage
{
    private static Log logger = LogFactory.getLog(ChannelManagerPage.class);

    //for lock all manipulation with Channels if creation in Process.
    private final static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final static Lock read = readWriteLock.readLock();
    private final static Lock write = readWriteLock.writeLock();

    private final static By TITLE_LABEL = By.cssSelector("div.title");
    private final static By NEW_CHANNEL_BUTTON = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-new-button");
    private final static By NO_CHANNELS_CONTROL = By.xpath("//div[contains(text(), 'There are no channels. Please create a new channel.')]");
    private final static By CHANNELS = By.xpath("//tr/td/div/div[contains(@class, 'channel')]");

    private final static By CHANNEL_DELETE_LINK = By.xpath("./span/a[contains(@class, 'delete')]");
    private final static By CHANNEL_RE_AUTH_LINK = By.xpath("./span/a[contains(@class, 'reauth')]");

    private final static String VISIBLE_CHANNELS_DROPDOWN = "#page_x002e_ctool_x002e_admin-console_x0023_default-newChannel-menu";

    /**
     * Basic constructor.
     *
     * @param drone
     */
    public ChannelManagerPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChannelManagerPage render(RenderTime renderTime)
    {
        elementRender(renderTime,
                getVisibleRenderElement(TITLE_LABEL),
                getVisibleRenderElement(NEW_CHANNEL_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChannelManagerPage render(long time)
    {
        checkArgument(time > 0);
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChannelManagerPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Check is no channels present
     *
     * @return true if no channels present
     */
    public boolean isNoChannels()
    {
        try
        {
            return drone.findAndWait(NO_CHANNELS_CONTROL).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Getting list of channels
     *
     * @return Collection of WebElements
     */
    public List<WebElement> getChannelList()
    {
        read.lock();
        try
        {
            return drone.findAndWaitForElements(CHANNELS);
        }
        catch (TimeoutException tEx)
        {
            return Collections.emptyList();
        }
        catch (StaleElementReferenceException e)
        {
            return getChannelList();
        }
        catch (NoSuchElementException e)
        {
            return Collections.emptyList();
        }
        finally
        {
            read.unlock();
        }
    }

    /**
     * Check is channel authorised
     *
     * @param channel
     * @return true if channel authorised
     */
    public boolean isChannelAuthorised(Channel channel)
    {
        WebElement channelElement = getChannelElement(channel);
        read.lock();
        try
        {
            return !(channelElement == null) && channelElement.getAttribute("class").equals("channel authorised");
        }
        finally
        {
            read.unlock();
        }
    }

    /**
     * Check is channel present
     *
     * @param channel
     * @return true if channel present
     */
    public boolean isChannelPresent(Channel channel)
    {
        WebElement channelElement = getChannelElement(channel);
        read.lock();
        try
        {
            return !(channelElement == null) && (channelElement.isDisplayed());
        }
        finally
        {
            read.unlock();
        }
    }

    /**
     * Delete channel action
     *
     * @param channel
     */
    public void deleteChannel(Channel channel)
    {
        WebElement channelElement = getChannelElement(channel);
        write.lock();
        try
        {
            WebElement deleteButton = channelElement.findElement(CHANNEL_DELETE_LINK);
            deleteButton.click();
            ConfirmDeleteChannelPage confirmDeletePage = new ConfirmDeleteChannelPage(drone);
            confirmDeletePage.selectAction(ConfirmDeleteChannelPage.Action.Delete).render();
        }
        finally
        {
            write.unlock();
        }
    }

    /**
     * Reauthorise channel action
     *
     * @param channel
     */
    @Deprecated
    public void reAuthoriseChannel(Channel channel, String userName, String password)
    {
        WebElement channelElement = getChannelElement(channel);
        write.lock();
        try
        {
            WebElement reAuth = channelElement.findElement(CHANNEL_RE_AUTH_LINK);
            reAuth.click();
            switch (channel)
            {
                case Flickr:
                    authorizeInFlickrChannel(userName, password);
                    break;
                default:
                    throw new PageOperationException("Not implemented.");
            }
        }
        finally
        {
            write.unlock();
        }
    }

    /**
     * Get channel selector
     *
     * @param channel
     * @return By selector
     */
    private By getChannelSelector(Channel channel)
    {
        return By.cssSelector(String.format("%s > div.bd > ul > li > span > a[rel='%s']", VISIBLE_CHANNELS_DROPDOWN, channel.getChannelName().toLowerCase()));
    }

    /**
     * Create flickr channel with authorisation
     *
     * @param userName, userPassword
     */
    public void createFlickrChannel(String userName, String userPassword)
    {
        write.lock();
        try
        {
            createChannelNotAuthorise(Channel.Flickr);
            authorizeInFlickrChannel(userName, userPassword);
        }
        finally
        {
            write.unlock();
        }
    }

    /**
     * Create channel without authorisation and locks(public method)
     *
     * @param channel
     */
    public void createChannelWithOutAuthorise(Channel channel)
    {
        write.lock();
        try
        {
            createChannelNotAuthorise(channel);
        }
        finally
        {
            write.unlock();
        }
    }

    /**
     * Create channel without authorisation and withOut locks
     *
     * @param channel
     */
    private void createChannelNotAuthorise(Channel channel)
    {
        try
        {
            WebElement newChannelButton = drone.findAndWait(NEW_CHANNEL_BUTTON);
            newChannelButton.click();
            drone.waitForElement(By.cssSelector(VISIBLE_CHANNELS_DROPDOWN), 5000);
            WebElement channelLink = drone.findAndWait(getChannelSelector(channel));
            channelLink.click();
        }
        catch (Exception ex)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Error in  createChannelNotAuthorise.", ex);
            }
        }
    }

    /**
     * Return WebElement associated with created channel. If Channel didn't found return null;
     *
     * @param channel
     * @return
     */
    private WebElement getChannelElement(Channel channel)
    {
        try
        {
            List<WebElement> elements = getChannelList();
            for (WebElement element : elements)
            {
                // detect is appropriate channel
                String imgHref = element.findElement(By.xpath("./a/img")).getAttribute("src");
                if (imgHref.contains(channel.getChannelName().toLowerCase()))
                {
                    return element;
                }
            }
        }
        catch (StaleElementReferenceException e)
        {
            return getChannelElement(channel);
        }
        return null;
    }

    /**
     * For authorize in Flickr using google acc
     *
     * @param userName
     * @param userPassword
     */
    private void authorizeInFlickrChannel(String userName, String userPassword)
    {
        try
        {
            YahooSignInPage yahooSignInPage = switchToYahooSignIn();
            FlickrUserPage flickrUserPage = yahooSignInPage.login(userName, userPassword);
            flickrUserPage.confirmAlfrescoAuthorize();
            switchToAdminConsole();
            waitUntilAlert();
            this.render();
        }
        catch (TimeoutException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Error in method authorizeInFlickrChannel", e);
            }
        }
    }

    /**
     * Switch to Yahoo Sign up Window based on title.
     */
    private YahooSignInPage switchToYahooSignIn()
    {
        sleep(15000);
        Set<String> windowHandles = drone.getWindowHandles();
        for (String windowHandle : windowHandles)
        {
            drone.switchToWindow(windowHandle);
            if (drone.getTitle().endsWith("Yahoo"))
            {
                return new YahooSignInPage(drone).render();
            }
        }
        throw new PageOperationException("Can't switch to Sign In Yahoo Page");
    }

    /**
     * Switch to Admin Console.
     */
    private ChannelManagerPage switchToAdminConsole()
    {
        Set<String> windowHandles = drone.getWindowHandles();
        for (String windowHandle : windowHandles)
        {
            drone.switchToWindow(windowHandle);
            if (drone.getTitle().endsWith("Admin Console"))
            {
                return drone.getCurrentPage().render();
            }
        }
        throw new PageOperationException("Can't switch to AdminConsole Page.");
    }

    private void sleep(long ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

}
