package org.alfresco.share.util;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.site.wiki.WikiPageList;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Antonik
 */
public class WikiUtils
{

    private static Log logger = LogFactory.getLog(ShareUserSharedFilesPage.class);

    /**
     * navigate to the Wiki Page. User must be logged in to the Share
     * 
     * @param driver -
     *            WebDrone Instance
     * @param siteName
     * @return WikiPage
     */
    public static WikiPage openWikiPage(WebDrone driver, String siteName)
    {
        WikiPage wikiPage = ShareUser.openSiteDashboard(driver, siteName).render().getSiteNav().selectSiteWikiPage().render();
        logger.info("Opened Wiki page");

        return wikiPage;

    }

    /**
     * Method to create new Wiki Page. User must be logged in to the Share.
     * 
     * @param driver -
     *            WebDrone Instance
     * @param siteName
     * @param wikiTitle
     * @param text
     * @param tag
     * @return WikiPage
     */
    public static WikiPage createWikiPage(WebDrone driver, String siteName, String wikiTitle, String text, String tag)
    {
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(driver, siteName).render();
        WikiPage wikiPage = siteDashboardPage.getSiteNav().selectSiteWikiPage().render();
        List<String> wikiText = new ArrayList<>();
        wikiText.add(text);
        List<String> wikiTag = new ArrayList<>();
        wikiTag.add(tag);
        return wikiPage.createWikiPage(wikiTitle, wikiText, wikiTag).render();
    }

    /**
     * Method to return tag name from Details Page of wiki. User must be logged in to the Share.
     * 
     * @param driver -
     *            WebDrone Instance
     * @param siteName
     * @param wikiTitle
     * @return String - tag name
     */
    public static String getWikiTag(WebDrone driver, String siteName, String wikiTitle)
    {
        WikiPage wikiPage = WikiUtils.openWikiPage(driver, siteName);
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();
        wikiPage = ShareUser.getCurrentPage(driver).render();
        wikiPage = wikiPageList.getWikiPageDirectoryInfo(wikiTitle.replace("_", " ")).clickDetails();
        return wikiPage.getTagName();
    }

}
