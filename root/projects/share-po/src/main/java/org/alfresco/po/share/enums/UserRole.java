package org.alfresco.po.share.enums;

import org.openqa.selenium.By;

/**
 * This enums used to describe the user roles.
 * 
 * @author cbairaajoni
 * @since v1.0
 */
public enum UserRole
{
    MANAGER         ("Manager"          , null), 
    EDITOR          ("Editor"           , By.cssSelector("div.bd li:nth-of-type(1)")), 
    CONSUMER        ("Consumer"         , By.cssSelector("div.bd li:nth-of-type(2)")), 
    COLLABORATOR    ("Collaborator"     , By.cssSelector("div.bd li:nth-of-type(3)")), 
    COORDINATOR     ("Coordinator"      , By.cssSelector("div.bd li:nth-of-type(4)")), 
    CONTRIBUTOR     ("Contributor"      , By.cssSelector("div.bd li:nth-of-type(5)")),
    SITECONSUMER    ("Site Consumer"    , By.cssSelector("div.bd li:nth-of-type(6)")), 
    SITECONTRIBUTOR ("Site Contributor" , By.cssSelector("div.bd li:nth-of-type(7)")),
    SITEMANAGER     ("Site Manager"     , By.cssSelector("div.bd li:nth-of-type(8)")), 
    SITECOLLABORATOR("Site Collaborator", By.cssSelector("div.bd li:nth-of-type(9)")); 
    

    private String roleName;
    By accessType;

    public By getAccessType()
    {
        return accessType;
    }

    private UserRole(String role, By accessType)
    {
        this.accessType = accessType;
        roleName = role;
    }
 
    private UserRole(String role)
    {
        roleName = role;
    }
    
    public String getRoleName() {
        return roleName;
    }
}