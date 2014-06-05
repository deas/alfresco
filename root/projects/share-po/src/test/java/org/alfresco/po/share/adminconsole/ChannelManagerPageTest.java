package org.alfresco.po.share.adminconsole;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.testng.annotations.Test;

import java.util.Set;

import static org.alfresco.po.share.adminconsole.Channel.Flickr;
import static org.testng.Assert.*;

/**
 * @author Roman.Chul
 */
public class ChannelManagerPageTest extends AbstractTest
{

    @Test(groups = "Enterprise-only", timeOut = 400000)
    public void checkThatFactoryReturnChannelManagerPage() throws Exception
    {
        SharePage page = loginAs("admin", "admin");
        page.getNav().getChannelManagerPage().render();
        assertNotNull(drone.getCurrentPage().render());
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "checkThatFactoryReturnChannelManagerPage", timeOut = 400000)
    public void checkIsNoChannelsMessagePresent() throws Exception
    {
        ChannelManagerPage channelManagerPage = drone.getCurrentPage().render();
        if (channelManagerPage.isChannelPresent(Flickr))
        {
            channelManagerPage.deleteChannel(Flickr);
        }
        assertTrue(channelManagerPage.isNoChannels());
        assertTrue(channelManagerPage.getChannelList().size() == 0);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "checkIsNoChannelsMessagePresent", timeOut = 400000)
    public void checkCreatingNonAuthorisedChannel() throws Exception
    {
        ChannelManagerPage channelManagerPage = drone.getCurrentPage().render();
        channelManagerPage.createChannelWithOutAuthorise(Flickr);

        Thread.sleep(15000);
        // close windows except admin console
        Set<String> windowHandles = drone.getWindowHandles();
        String currentWindow = drone.getWindowHandle();
        for (String windowHandle : windowHandles)
        {
            if (!windowHandle.equals(currentWindow))
            {
                drone.switchToWindow(windowHandle);
                drone.closeWindow();
            }
        }
        drone.switchToWindow(currentWindow);

        channelManagerPage = channelManagerPage.getNav().getChannelManagerPage().render();
        assertTrue(channelManagerPage.isChannelPresent(Flickr));
        assertFalse(channelManagerPage.isChannelAuthorised(Flickr));
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "checkCreatingNonAuthorisedChannel", timeOut = 400000)
    public void checkDeletingNonAuthorisedChannel() throws Exception
    {
        ChannelManagerPage channelManagerPage = drone.getCurrentPage().render();
        channelManagerPage.deleteChannel(Flickr);
        channelManagerPage = channelManagerPage.getNav().getChannelManagerPage().render();
        assertFalse(channelManagerPage.isChannelPresent(Flickr));
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "checkDeletingNonAuthorisedChannel", timeOut = 400000, enabled=false)
    public void checkCreatingAuthorisedChannel() throws Exception
    {
        ChannelManagerPage channelManagerPage = drone.getCurrentPage().render();
        channelManagerPage.createFlickrChannel("gogigruzinidze@yahoo.com", "parkh0useG");
        drone.refresh();
        assertTrue(channelManagerPage.isChannelPresent(Flickr));
        assertTrue(channelManagerPage.isChannelAuthorised(Flickr));
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "checkCreatingAuthorisedChannel", timeOut = 400000, enabled=false)
    public void checkDeletingAuthorisedChannel() throws Exception
    {
        ChannelManagerPage channelManagerPage = drone.getCurrentPage().render();
        channelManagerPage.deleteChannel(Flickr);
        drone.refresh();
        assertFalse(channelManagerPage.isChannelPresent(Flickr));
    }

}
