package org.alfresco.po.share.site.datalist.items;

import org.alfresco.po.share.site.datalist.lists.ContactList;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

/**
 * Page object to reflect Contact list item
 *
 * @author Marina.Nenadovets
 */
public class ContactListItem extends AbstractItem
{
    public ContactListItem(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ContactListItem render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ContactListItem render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ContactListItem render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for editing an item
     *
     * @param oldTitle
     * @param newTitle
     */
    public void editAnItem (String oldTitle, String newTitle)
    {
        ContactList contactList = new ContactList(drone);
        contactList.clickEditItem(oldTitle);
        fillItemFields(newTitle);
        clickSave();
        waitUntilAlert();
    }
}
