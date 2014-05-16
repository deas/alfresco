package org.alfresco.share.util;

import org.alfresco.po.thirdparty.flickr.FlickrUserPage;
import org.alfresco.po.thirdparty.flickr.YahooSignInPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.NoSuchElementException;

/**
 * @author Aliaksei.Boole
 */
public class PublishUtil
{

    /**
     * Method checks the published picture in Flickr profile exist. Login in Yahoo if necessary.
     *
     * @param drone
     * @param fileName
     * @param userName
     * @param userPassword
     * @return
     * @throws InterruptedException
     */
    public static boolean isContentUploadedToFlickrChannel(WebDrone drone, String fileName, String userName, String userPassword) throws InterruptedException
    {
        String detailsPageUrl = drone.getCurrentUrl();
        try
        {
            // navigate to sign in page
            drone.navigateTo("http://www.flickr.com/signin/");
            if (drone.getTitle().endsWith("Yahoo"))
            {
                YahooSignInPage yahooSignInPage = new YahooSignInPage(drone).render();
                yahooSignInPage.login(userName, userPassword);
            }
            FlickrUserPage flickrUserPage = new FlickrUserPage(drone).render();
            return flickrUserPage.isFileUpload(fileName);

        }
        catch (NoSuchElementException ex)
        {
            throw new NoSuchElementException("Element not found", ex);
        }
        finally
        {
            //Navigate to previous page.
            drone.navigateTo(detailsPageUrl);
        }
    }

}
