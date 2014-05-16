package org.alfresco.po.share.site.datalist;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.datalist.lists.ContactList;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * An abstract of Data List
 *
 * @author Marina.Nenadovets
 */
public abstract class AbstractDataList extends SharePage
{
    protected static final By NEW_ITEM_LINK = By.cssSelector("button[id$='newRowButton-button']");
    protected static final By LIST_TABLE = By.cssSelector("table");
    protected static final By EDIT_LINK = By.cssSelector(".onActionEdit>a");
    protected static final By DUPLICATE_LINK = By.cssSelector(".onActionDuplicate>a");
    protected static final By DELETE_LINK = By.cssSelector(".onActionDelete>a");
    private static final By CONFIRM_DELETE = By.xpath("//span[@class='button-group']/span[1]/span/button");
    private static final By CANCEL_DELETE = By.xpath("//span[@class='button-group']/span[2]/span/button");
    private static final By ITEM_CONTAINER = By.cssSelector("table>tbody>tr[class*='dt']");

    protected AbstractDataList(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractDataList render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractDataList render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractDataList render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to locate an item in the table
     *
     * @param fieldValue
     */
    private void locateAnItem(String fieldValue)
    {
        WebElement theItem = drone.findAndWait(By.xpath(String.format("//td//div[text()='%s']", fieldValue)));
        drone.mouseOver(theItem);
    }

    /**
     * Method to duplicate an item
     *
     * @param fieldValue
     */
    public void duplicateAnItem(String fieldValue)
    {
        locateAnItem(fieldValue);
        drone.findAndWait(DUPLICATE_LINK).click();
        waitUntilAlert();
    }

    /**
     * Method to delete an item
     *
     * @param fieldValue
     */
    public void deleteAnItemWithConfirm(String fieldValue)
    {
        locateAnItem(fieldValue);
        drone.findAndWait(DELETE_LINK).click();
        drone.findAndWait(CONFIRM_DELETE).click();
        waitUntilAlert();
    }

    /**
     * Method to click edit for item
     *
     * @param fieldValue
     */
    public void clickEditItem(String fieldValue)
    {
        locateAnItem(fieldValue);
        drone.findAndWait(EDIT_LINK).click();
        waitUntilAlert();
    }

    /**
     * Method to select New Item link
     */
    protected void selectNewItem()
    {
        try
        {
            waitUntilAlert();
            drone.findAndWait(NEW_ITEM_LINK).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find " + NEW_ITEM_LINK);
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Timed out finding " + NEW_ITEM_LINK);
        }
    }

    private boolean isDisplayed(By locator)
    {
        try
        {
            return drone.findAndWait(locator, 2000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to verify whether New Item link is displayed
     *
     * @return
     */
    public boolean isNewItemEnabled()
    {
        String someButton = drone.findAndWait(NEW_ITEM_LINK).getAttribute("disabled");
        if (someButton.contains("true"))
        {
            return false;
        }
        else return true;
    }

    /**
     * Method to verify whether duplicate item link is available
     *
     * @param itemName
     * @return boolean
     */
    public boolean isDuplicateDisplayed(String itemName)
    {
        locateAnItem(itemName);
        return isDisplayed(DUPLICATE_LINK);
    }

    /**
     * Method to verify whether delete item link is available
     * @param itemName
     * @return boolean
     */
    public boolean isDeleteDisplayed (String itemName)
    {
        locateAnItem(itemName);
        return isDisplayed(DELETE_LINK);
    }

    /**
     * Method to get the count of items in the list
     *
     * @return number of items
     */
    public int getItemsCount ()
    {
        try
        {
            List<WebElement> allItems = drone.findAll(ITEM_CONTAINER);
            if(allItems.size()==1)
            {
                return 0;
            }
            return allItems.size()-1;
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + ITEM_CONTAINER);
        }
    }
}
