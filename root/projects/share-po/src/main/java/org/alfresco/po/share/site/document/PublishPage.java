package org.alfresco.po.share.site.document;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.adminconsole.Channel;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Roman.Chul
 */
public class PublishPage extends SharePage
{

    private static final By SELECT_CHANNEL_BUTTON = By.cssSelector("#alfresco-socialPublishing-instance-channel-select-button-button");
    private static final By PUBLISH_BUTTON = By.xpath("//button[contains(@id,'publish-button')]");
    protected static final By CANCEL_BUTTON = By.xpath("//button[contains(@id,'cancel-button')]");;
    private final static String VISIBLE_CHANNELS_DROPDOWN = "//div[contains(@id,'instance-publishChannel-menu')]";

    protected PublishPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PublishPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public PublishPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public PublishPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, RenderElement.getVisibleRenderElement(SELECT_CHANNEL_BUTTON));
        }
        catch (NoSuchElementException e)
        {
        }
        return this;
    }

    private By getChannelSelector(Channel channel)
    {
        return By.xpath(String.format("%s/div[@class='bd']/ul/li/span/a/span[contains(text(),'%s')]", VISIBLE_CHANNELS_DROPDOWN, channel.getChannelName()));
    }

    public void selectChannel(Channel channel)
    {
        try
        {
            WebElement selectChannelButton = drone.findAndWait(SELECT_CHANNEL_BUTTON);
            selectChannelButton.click();
            drone.waitForElement(By.xpath(VISIBLE_CHANNELS_DROPDOWN), 5000);
            WebElement channelLink = drone.findAndWait(getChannelSelector(channel));
            channelLink.click();
        }
        catch (TimeoutException tEx)
        {
            throw new TimeoutException("Timeout to wait Channels Dropdown", tEx);
        }
    }

    /**
     * Clicks on publish button to publish content
     * 
     * @return {@link FolderDetailsPage}
     */
    public SharePage selectPublish()
    {
        drone.find(PUBLISH_BUTTON).click();
        return drone.getCurrentPage().render();
    }

    /**
     * Clicks on publish button to publish content
     * 
     * @return {@link FolderDetailsPage}
     */
    public SharePage selectCancelPublish()
    {
        drone.find(CANCEL_BUTTON).click();
        return drone.getCurrentPage().render();
    }

}
