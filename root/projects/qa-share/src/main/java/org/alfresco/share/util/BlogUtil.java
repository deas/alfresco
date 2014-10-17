package org.alfresco.share.util;

import org.alfresco.po.thirdparty.wordpress.WordPressMainPage;
import org.alfresco.po.thirdparty.wordpress.WordPressSignInPage;
import org.alfresco.po.thirdparty.wordpress.WordPressUserPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.NoSuchElementException;

/**
 * Util class to manager Blog Publishing
 *
 * @author Marina.Nenadovets
 *
 */
public class BlogUtil extends AbstractUtils
{
    /**
     * Method to verify whether post is published to WordPress
     *
     * @param drone
     * @param postTitle
     * @param blogUrl
     * @param userName
     * @param userPassword
     * @return boolean
     */
    public static boolean isPostPublishedToExternalWordpressBlog(WebDrone drone, String postTitle, String blogUrl, String userName, String userPassword)
    {
        String currentUrl = drone.getCurrentUrl();
        try
        {
            //Navigate to Sign In page
            drone.navigateTo(String.format("https://%s.wordpress.com", blogUrl));
            WordPressUserPage wordPressUserPage = new WordPressUserPage(drone);
            if(drone.getTitle().equalsIgnoreCase("WordPress.com - Get a Free Website and Blog Here"))
            {
                WordPressMainPage wordPressMainPage = new WordPressMainPage(drone).render();
                WordPressSignInPage wordPressSignInPage = wordPressMainPage.clickLogIn().render();
                wordPressUserPage = wordPressSignInPage.login(userName, userPassword);
                drone.navigateTo(String.format("https://%s.wordpress.com", userName));
            }

            return wordPressUserPage.isPostPresent(postTitle);
        }
        catch (NoSuchElementException ex)
        {
            throw new NoSuchElementException("Element not found", ex);
        }
        finally
        {
            //Navigate to previous page
            drone.navigateTo(currentUrl);
            drone.waitForPageLoad(5);
        }
    }

    /**
     * Method to verify whether post is removed from WordPress
     * @param drone
     * @param postTitle
     * @param blogUrl
     * @param userName
     * @param userPassword
     * @return boolean
     */
    public static boolean isPostRemovedFromExternalWordpressBlog(WebDrone drone, String postTitle, String blogUrl, String userName, String userPassword)
    {
        String currentUrl = drone.getCurrentUrl();
        try
        {
            //Navigate to Sign In page
            drone.navigateTo(String.format("https://%s.wordpress.com", blogUrl));
            WordPressUserPage wordPressUserPage = new WordPressUserPage(drone);
            if(drone.getTitle().equalsIgnoreCase("WordPress.com - Get a Free Website and Blog Here"))
            {
                WordPressMainPage wordPressMainPage = new WordPressMainPage(drone).render();
                WordPressSignInPage wordPressSignInPage = wordPressMainPage.clickLogIn().render();
                wordPressUserPage = wordPressSignInPage.login(userName, userPassword);
            }
            return wordPressUserPage.isPostRemoved(postTitle);

        }
        catch (NoSuchElementException ex)
        {
            throw new NoSuchElementException("Element not found", ex);
        }
        finally
        {
            //Navigate to previous page.
            drone.navigateTo(currentUrl);
        }
    }
}
