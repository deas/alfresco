package org.alfresco.po.share.site.document;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.ShareDialogue;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Abstract of edit properties
 * 
 * @author Michael Suzuki
 * @since 1.4
 */
@SuppressWarnings("unchecked")
public abstract class AbstractEditProperties extends ShareDialogue
{
    protected AbstractEditProperties(WebDrone drone)
    {
        super(drone);
    }

    protected static final By INPUT_NAME_SELECTOR = By.cssSelector("input[id$='prop_cm_name']");
    protected static final By INPUT_TITLE_SELECTOR = By.cssSelector("input[id$='prop_cm_title']");
    protected static final By INPUT_DESCRIPTION_SELECTOR = By.cssSelector("textarea[id$='prop_cm_description']");
    protected static final By INPUT_AUTHOR_SELECTOR = By.cssSelector("input[id$='prop_cm_author']");
    protected static final By INPUT_RESOLUTION_UNIT_SELECTOR = By.cssSelector("input[id$='prop_exif_resolutionUnit']");
    protected static final By INPUT_VERTICAL_RESOLUTION_SELECTOR = By.cssSelector("input[id$='_prop_exif_yResolution']");
    protected static final By INPUT_ORIENTATION_SELECTOR = By.cssSelector("input[id$='prop_exif_orientation']");
    protected static final By BUTTON_SELECT_TAG = By.cssSelector("div[id$='cntrl-itemGroupActions']");
    protected static final By CATEGORY_BUTTON_SELECT_TAG = By.cssSelector("div[id$='categories-cntrl-itemGroupActions']");
    protected static final By BUTTON_ALL_PROPERTIES = By.cssSelector("a[id$='editMetadata-button']");

    /**
     * Clear the input field and inserts the new value.
     * 
     * @param input {@link WebElement} represents the form input
     * @param value String input value to enter
     */
    public void setInput(final WebElement input, final String value)
    {
        input.clear();
        input.sendKeys(value);
    }

    /**
     * Gets the value of the input field
     * 
     * @param by input field descriptor
     * @return String input value
     */
    protected String getValue(By by)
    {
        return drone.find(by).getAttribute("value");
    }

    /**
     * Get the String value of name input value.
     */
    public String getName()
    {
        return getValue(INPUT_NAME_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param name String name input
     */
    public void setName(final String name)
    {
        setInput(drone.findAndWait(INPUT_NAME_SELECTOR), name);
    }

    /**
     * Get value seen on the title input value.
     */
    public String getDocumentTitle()
    {
        return getValue(INPUT_TITLE_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param title String name input
     */
    public void setDocumentTitle(final String title)
    {
        setInput(drone.findAndWait(INPUT_TITLE_SELECTOR), title);
    }

    /**
     * Get value seen on the description input value.
     */
    public String getDescription()
    {
        return getValue(INPUT_DESCRIPTION_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param description String name input
     */
    public void setDescription(final String description)
    {
        setInput(drone.findAndWait(INPUT_DESCRIPTION_SELECTOR), description);
    }

    /**
     * Click on Select button to go to Tag page
     * 
     * @return TagPage
     */
    public TagPage getTag()
    {
        WebElement tagElement = drone.find(BUTTON_SELECT_TAG);
        tagElement.findElement(By.tagName("button")).click();
        return new TagPage(drone);
    }

    /**
     * Click on Select button to go to Category page
     * 
     * @return CategoryPage
     */
    public CategoryPage getCategory()
    {
        WebElement tagElement = drone.findAndWait(CATEGORY_BUTTON_SELECT_TAG);
        tagElement.findElement(By.tagName("button")).click();
        return new CategoryPage(drone);
    }

    /**
     * Get the {@link List} of added {@link Categories}.
     * 
     * @return {@link List} of {@link Categories}
     * @depricated Use {@link #getCategoryList()} instead.
     */
    @Deprecated
    public List<Categories> getCategories()
    {
        List<Categories> categories = new ArrayList<Categories>();
        try
        {
            List<WebElement> categoryElements = drone.findAll(By.cssSelector("div[class='itemtype-cm:category']"));
            for (WebElement webElement : categoryElements)
            {
                categories.add(Categories.getCategory(webElement.getText()));
            }
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Not able to find categories", e);
        }
        return categories;
    }

    /**
     * Get the {@link List} of added categories.
     * 
     * @return {@link List} of categories
     */
    public List<String> getCategoryList()
    {
        List<String> categories = new ArrayList<>();
        try
        {
            List<WebElement> categoryElements = drone.findAll(By.cssSelector("div[class='itemtype-cm:category']"));
            for (WebElement webElement : categoryElements)
            {
                categories.add(webElement.getText());
            }
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Not able to find categories", e);
        }
        return categories;
    }

    /**
     * Select cancel button.
     */
    public void clickOnCancel()
    {
        drone.findAndWait(By.cssSelector("button[id$='form-cancel-button']")).click();
    }

    /**
     * Selects the save button that posts the form.
     */
    public void clickSave()
    {
        WebElement saveButton = drone.findAndWait(By.cssSelector("button[id$='form-submit-button']"));
        if (saveButton.isDisplayed())
        {
            String id = saveButton.getAttribute("id");
            saveButton.click();
            drone.waitUntilElementDeletedFromDom(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
    }

    /**
     * Select all properties button.
     */
    public void clickAllProperties()
    {
        drone.findAndWait(BUTTON_ALL_PROPERTIES).click();
    }
}
