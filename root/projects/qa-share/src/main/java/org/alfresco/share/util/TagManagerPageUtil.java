package org.alfresco.share.util;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.adminconsole.TagManagerPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author  Olga Antonik
 */
public class TagManagerPageUtil
{

    /**
     * Open Tag Manager page from any share page.
     * NOT FOR CLOUD!
     *
     * @param drone
     * @return TagManagerPage
     */
    public static TagManagerPage openTagManagerPage(WebDrone drone)
    {
        try
        {
            ShareUser.login(drone, "admin", "admin");
            SharePage page = drone.getCurrentPage().render();
            return page.getNav().getTagManagerPage().render();
        }
        catch (PageRenderTimeException e)
        {
            throw new PageOperationException("Tag Manager Page does not render in time. May be you trying use method not as administrator OR it's bug.", e);
        }
    }

    /**
     * execute search. Work if you are admin.
     *
     * @param drone
     * @param tagName
     * @return TagManagerPage
     */
    public static TagManagerPage openTagMangerAndFindTag(WebDrone drone, String tagName)
    {
        TagManagerPage tagManagerPage = openTagManagerPage(drone).render();
        tagManagerPage.searchTag(tagName);

        return tagManagerPage.render();
    }

    /**
     * execute search. Work if you are admin. Tag Manager page is opened
     *
     * @param drone
     * @param tagName
     * @return TagManagerPage
     */
    public static TagManagerPage findTag(WebDrone drone, String tagName)
    {
        TagManagerPage tagManagerPage = drone.getCurrentPage().render();
        checkNotNull(tagName);
        tagManagerPage.fillSearchField(tagName);
        tagManagerPage.clickSearchButton();

        return tagManagerPage.render();
    }

    /**
     * execute search and verify that tag was found. Work if you are admin. Tag Manager page is opened
     *
     * @param drone
     * @param tagName
     * @return boolean
     */
    public static boolean findTagAndVerify(WebDrone drone, String tagName)
    {
        TagManagerPage tagManagerPage = findTag(drone, tagName);
        return tagManagerPage.isInResults(tagName);
    }

    /**
     * execute edit tag and verify that tag was changed. Work if you are admin. Tag Manager page is opened
     *
     * @param drone
     * @param tagName
     * @param newTagName
     * @return boolean
     */
    public static boolean editTagAndVerify(WebDrone drone, String tagName, String newTagName) throws InterruptedException {
        TagManagerPage tagManagerPage = drone.getCurrentPage().render();

        tagManagerPage.editTag(tagName, newTagName);
        tagManagerPage = openTagMangerAndFindTag(drone, newTagName).render();

        return tagManagerPage.isInResults(newTagName);

    }

    /**
     * execute deletion and verify that tag was deleted. Work if you are admin. Tag Manager page is opened
     *
     * @param drone
     * @param tagName
     * @return boolean
     */
    public static boolean deleteTagAndVerify(WebDrone drone, String tagName)
    {

        TagManagerPage tagManagerPage = openTagMangerAndFindTag(drone, tagName).render();

        tagManagerPage.deleteTag(tagName);
        tagManagerPage = openTagManagerPage(drone).render();
        checkNotNull(tagName);
        tagManagerPage.fillSearchField(tagName);
        tagManagerPage.clickSearchButton();

        return !tagManagerPage.isInResults(tagName);

    }


}
