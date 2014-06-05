package org.alfresco.po.share.site.datalist.lists;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.datalist.AbstractDataList;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.datalist.items.ContactListItem;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Page object to hold elements of Contact Data List
 *
 * @author Marina.Nenadovets
 */
public class ContactList extends AbstractDataList
{
    private static final Log logger = LogFactory.getLog(ContactList.class);

    public ContactList(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ContactList render(RenderTime timer)
    {
        elementRender(timer,
            getVisibleRenderElement(LIST_TABLE));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ContactList render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ContactList render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for creating and Item
     *
     * @param data
     */
    public ContactList createItem(String data)
    {
        logger.info("Creating an item");
        selectNewItem();
        ContactListItem contactListItem = new ContactListItem(drone);
        contactListItem.fillItemFields(data);
        contactListItem.clickSave();
        waitUntilAlert();
        return new ContactList(drone).render();
    }

    public DataListPage editItem(String title, String data)
    {
        logger.info("Editing the " + title + " item");
        try
        {
            clickEditItem(title);
            ContactListItem contactListItem = new ContactListItem(drone);
            contactListItem.editAnItem(data);
            return drone.getCurrentPage().render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("The operation has timed out" + te);
        }
    }

    /**
     * Method to verify whether edit item link is available
     *
     * @param itemName
     * @return boolean
     */
    public boolean isEditDisplayed(String itemName)
    {
        boolean isDisplayed;
        WebElement theItem = locateItemActions(itemName);
        try
        {
            isDisplayed = theItem.findElement(EDIT_LINK).isEnabled();
        }
        catch (NoSuchElementException nse)
        {
            isDisplayed = false;
        }
        return isDisplayed;
    }
}
