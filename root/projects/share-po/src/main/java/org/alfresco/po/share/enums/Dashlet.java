package org.alfresco.po.share.enums;

/**
 * This enums used to describe the Dashlets.
 * 
 * @author cbairaajoni
 * @since v1.0
 */
public enum Dashlet
{
    SITE_CALENDAR("Site Calendar"),
    SITE_MEMBERS("Site Members"),
    SITE_CONTENT("Site Content"),
    IMAGE_PREVIEW("Image Preview"),
    SITE_ACTIVITIES("Site Activities"),
    SITE_DATA_LISTS("Site Data Lists"),
    SITE_LINKS("Site Links"),
    SITE_NOTICE("Site Notice"),
    SITE_PROFILE("Site Profile"),
    WIKI("Wiki"),
    ALFRESCO_ADDONS_RSS_FEED("Alfresco Add-ons RSS Feed"),
    MY_DISCUSSIONS("My Discussions"),
    RSS_FEED("RSS Feed"),
    SAVED_SEARCH("Saved Search"),
    SITE_SEARCH("Site Search"),
    WEB_VIEW("Web View");

    private String dashletName;

    private Dashlet(String dashlet)
    {
        dashletName = dashlet;
    }

    public String getDashletName()
    {
        return dashletName;
    }
}