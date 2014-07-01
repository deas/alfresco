package org.alfresco.po.share.enums;

/**
 * This enums used to describe the Dashlets.
 * 
 * @author cbairaajoni
 * @since v1.0
 */
public enum Dashlets
{
    ALFRESCO_ADDONS_RSS_FEED("Alfresco Add-ons RSS Feed"),
    DATA_LISTS("Data Lists"),
    IMAGE_PREVIEW("Image Preview"),
    MY_ACTIVITIES("My Activities"),
    MY_DOCUMENTS("My Documents"),
    MY_DISCUSSIONS("My Discussions"),
    MY_SITES("My Sites"),
    MY_TASKS("My Tasks"),
    RSS_FEED("RSS Feed"),
    SAVED_SEARCH("Saved Search"),
    SITE_ACTIVITIES("Site Activities"),
    SITE_CALENDAR("Site Calendar"),
    SITE_CONTENT("Site Content"),
    SITE_DATA_LISTS("Site Data Lists"),
    SITE_LINKS("Site Links"),
    SITE_NOTICE("Site Notice"),
    SITE_MEMBERS("Site Members"),
    SITE_PROFILE("Site Profile"),
    SITE_SEARCH("Site Search"),
    SITE_CONTENT_REPORT("Site Content Report"),
    TOP_SITE_CONTRIBUTOR_REPORT("Top Site Contributor Rep..."),
    WEB_VIEW("Web View"),
    WELCOME_SITE("Welcome Site"),
    WIKI("Wiki");

    private String dashletName;

    private Dashlets(String dashlet)
    {
        dashletName = dashlet;
    }

    public String getDashletName()
    {
        return dashletName;
    }
}