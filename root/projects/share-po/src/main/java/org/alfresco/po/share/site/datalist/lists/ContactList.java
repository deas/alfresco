package org.alfresco.po.share.site.datalist.lists;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.site.datalist.AbstractDataList;
import org.alfresco.po.share.site.datalist.items.ContactListItem;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

/**
 * Page object to hold elements of Contact Data List
 *
 * @author Marina.Nenadovets
 */
public class ContactList extends AbstractDataList
{
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
        selectNewItem();
        ContactListItem contactListItem = new ContactListItem(drone);
        contactListItem.fillItemFields(data);
        contactListItem.clickSave();
        waitUntilAlert();
        return new ContactList(drone).render();
    }
}
