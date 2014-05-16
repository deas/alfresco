package org.alfresco.po.share.adminconsole;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author Roman.Chul
 */
public class ConfirmDeleteChannelPage extends SharePage
{
    private static final Log LOGGER = LogFactory.getLog(ConfirmDeleteChannelPage.class);
    private static final By BUTTON_GROUP = By.cssSelector(".button-group");
    private static final By PROMPT = By.cssSelector("div[id$='prompt']");

    protected ConfirmDeleteChannelPage(WebDrone drone)
    {
        super(drone);

    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfirmDeleteChannelPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(BUTTON_GROUP), getVisibleRenderElement(PROMPT));
        }
        catch (NoSuchElementException e)
        {
            LOGGER.error(BUTTON_GROUP + "or" + PROMPT + " not found!");

        }
        catch (TimeoutException e)
        {
            LOGGER.error(BUTTON_GROUP + "or" + PROMPT + " not found!");
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfirmDeleteChannelPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfirmDeleteChannelPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Select Action "Delete" or "Cancel" to perform.
     * 
     * @param action
     * @return - HtmlPage
     */
    public HtmlPage selectAction(Action action)
    {

        try
        {
            List<WebElement> buttons = drone.findAll(By.cssSelector(".button-group span span button"));
            for (WebElement button : buttons)
            {
                if (action.getActionName().equals(button.getText()))
                {
                    button.click();
                    return drone.getCurrentPage();
                }

            }

        }
        catch (NoSuchElementException nse)
        {
            LOGGER.error(BUTTON_GROUP + "not present in this page");

        }
        throw new PageOperationException(BUTTON_GROUP + "not present in this page");
    }

    public enum Action
    {
        Delete("OK"),
        Cancel("Cancel");

        private String actionName;

        Action(String action) {
            actionName = action;
        }

        public String getActionName() {
            return actionName;
        }
    }

}
