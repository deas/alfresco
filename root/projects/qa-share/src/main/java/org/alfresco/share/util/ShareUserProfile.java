package org.alfresco.share.util;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.document.SyncInfoPage.ButtonType;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.user.TrashCanDeleteConfirmationPage;
import org.alfresco.po.share.user.TrashCanEmptyConfirmationPage;
import org.alfresco.po.share.user.TrashCanItem;
import org.alfresco.po.share.user.TrashCanPage;
import org.alfresco.po.share.user.TrashCanValues;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ShareUserProfile extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(ShareUserProfile.class);

    public ShareUserProfile()
    {
        if (logger.isTraceEnabled())
        {
            logger.debug(this.getClass().getSimpleName() + " instantiated");
        }
    }
    
    /**
     * Deletes the specified trashcan item if found
     * @param WebDrone
     * @ param String itemName to be deleted
     * @return TrashCanPage
     */
    public static  TrashCanPage deleteTrashCanItem(WebDrone driver, String itemName)
    {    
        TrashCanItem trashCanItem = getTrashCanItem(driver, itemName);
        TrashCanDeleteConfirmationPage trashDeletePage = trashCanItem.selectTrashCanAction(TrashCanValues.DELETE).render();
        return trashDeletePage.clickOkButton().render();
    }
    
    /**
     * Recovers the specified trashcan item if found
     * @param WebDrone
     * @ param String itemName to be recovered
     * @return TrashCanPage
     */    
    public static HtmlPage recoverTrashCanItem(WebDrone drone, String itemName)
    {
        TrashCanItem trashCanItem = getTrashCanItem(drone, itemName);
        return trashCanItem.selectTrashCanAction(TrashCanValues.RECOVER).render();
    }
    
    
    /**
     * Empty TrashCan.
     * @param drone
     * @return
     */
    public static TrashCanPage emptyTrashCan(WebDrone drone, ButtonType buttonType)
    {
      TrashCanEmptyConfirmationPage trashCanEmptyPage = ((TrashCanPage)getSharePage(drone)).render().selectEmpty().render();
      if(ButtonType.CANCEL.equals(buttonType))
      {
          return trashCanEmptyPage.clickCancelButton().render();
      }
      else
      {
          return trashCanEmptyPage.clickOkButton().render();
      }
      
    }
    
    /**
     * Perform a trashcan search on itemName. Return the list of all trashcan items with matching name.
     * @param drone
     * @param itemName String to search for
     * @return List<String>
     */
    public static List<String> getTrashCanItems(WebDrone drone, String itemName)
    {           
        List<String> namesOfFiles = new ArrayList<String>();
        
        TrashCanPage trashcanPage = (TrashCanPage) getSharePage(drone);
        trashcanPage.render();
        
        trashcanPage = trashcanPage.itemSearch(itemName).render();

        for (TrashCanItem item : trashcanPage.getTrashCanItems())
        {
            namesOfFiles.add(item.getFileName());
        }
        return namesOfFiles;
    }
    
    /**
     * Check TrashCan Item is present in list or not. 
     * @param drone
     * @param contentName
     * @return
     */
    public static boolean isTrashCanItemPresent(WebDrone drone, String contentName)
    {
        try
        {
            // TODO: Naved: Suggested Use getTrashCanItems(drone, contentName).contains(contentName)
            return contentName.equalsIgnoreCase(getTrashCanItem(drone, contentName).getFileName()) ? true : false;
        }
        catch(ShareException args)
        {
            logger.error("Not available trash can item :"+contentName);
            return false;
        }
    }
    
    /**
     * Retrieve TrashCanItem based on its name input. Assumes User is on TrashCanPage
     * @param drone
     * @param contentName
     * @return
     */
    public static TrashCanItem getTrashCanItem(WebDrone drone, String contentName)
    {
        List<TrashCanItem> trashCanItems = new ArrayList<TrashCanItem>();
        TrashCanPage trashCan = (TrashCanPage)getSharePage(drone).render();
        trashCanItems.addAll(trashCan.getTrashCanItems());
      
        TrashCanItem item = getItemPresentInThePage(contentName, trashCanItems);
        
        if(item != null)
        {
        	return item; 
        }
        
        while(trashCan.hasNextPage())
        {
            trashCan = trashCan.selectNextPage().render();
            //trashCanItems.addAll(trashCan.getTrashCanItems());
            item = getItemPresentInThePage(contentName, trashCan.getTrashCanItems());
            
            if(item != null)
            {
            	return item; 
            }
        }        

        throw new ShareException("Incorrect content :"+ contentName);
        
    }
    
    
    /**
     * @param contentName
     * @param trashCanItems
     * @return
     */
    private static TrashCanItem getItemPresentInThePage(String contentName, List<TrashCanItem> trashCanItems)
    {
    
    	for (TrashCanItem trashCanItem : trashCanItems)
        {
            if(contentName.equalsIgnoreCase(trashCanItem.getFileName()))
            {
                return trashCanItem;
            }
        }
		return null;
    }
    
    /**
     * @param drone
     * @return
     */
    public static TrashCanPage navigateToTrashCan(WebDrone drone)
    {
        SharePage sharePage = getSharePage(drone);
    
        if( sharePage instanceof TrashCanPage)
        {
                return ((TrashCanPage) sharePage).render();
        }       
        else
        {
                 MyProfilePage  myProfilePage = sharePage.getNav().selectMyProfile().render();
                 return myProfilePage.getProfileNav().selectTrashCan().render();
        }
    }
}
