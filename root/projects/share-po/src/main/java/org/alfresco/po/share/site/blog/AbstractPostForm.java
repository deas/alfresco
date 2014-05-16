package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import java.util.*;

/**
 * Abstract of Blog post Form
 *
 * @author Marina.Nenadovets
 */
public abstract class AbstractPostForm extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());

    protected static final String POST_FORMAT_IFRAME = ("template_x002e_postedit_x002e_blog-postedit_x0023_default-content_ifr");
    protected static final By TITLE_FIELD = By.cssSelector("#template_x002e_postedit_x002e_blog-postedit_x0023_default-title");
    protected static final By CANCEL_BTN = By.cssSelector("#template_x002e_postedit_x002e_blog-postedit_x0023_default-cancel-button-button");
    protected static final By DEFAULT_SAVE = By.cssSelector("button[id$='default-save-button-button']");
    protected static final By PUBLISH_INTERNALLY_EXTERNALLY = By.cssSelector("button[id$='default-publishexternal-button-button']");

    protected AbstractPostForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    public AbstractPostForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    public AbstractPostForm render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to set String input in the field
     *
     * @param input
     * @param value
     */

    private void setInput(final WebElement input, final String value)
    {
        try
        {
            input.clear();
            input.sendKeys(value);
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to find " + input);
        }
    }

    /**
     * Method for inserting text into the title field
     *
     * @param title
     */
    protected void setTitleField(final String title)
    {
        setInput(drone.findAndWait(TITLE_FIELD), title);
    }

    /**
     * Method for inserting text into the text field
     *
     * @param txtLines
     */
    public void insertText(String txtLines)
    {
        try
        {
            drone.executeJavaScript(String.format("tinyMCE.activeEditor.setContent('%s');", txtLines));
            drone.switchToFrame(POST_FORMAT_IFRAME);
            WebElement element = drone.findAndWait(By.cssSelector("#tinymce"));
            if (!element.getText().isEmpty())
            {
                element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            }
            drone.switchToDefaultContent();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding #tinymce", toe);
        }
    }

    /**
     * Method for clicking Save as Draft button
     */
    protected PostViewPage clickSaveAsDraft()
    {
        WebElement saveButton = drone.findAndWait(DEFAULT_SAVE);
        try
        {
            saveButton.click();
            return new PostViewPage(drone).render();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find Save button");
        }
    }
}