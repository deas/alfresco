package org.alfresco.po.share.site.document;

import org.alfresco.po.share.UserProfilePage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class TableViewFileDirectoryInfo extends SimpleDetailTableView
{
    
    private static final String CREATOR = "td.yui-dt-col-cmcreator>div>span>a";
    private static final String CREATED = "td.yui-dt-col-cmcreated>div>span";
    private static final String MODIFIER = "td.yui-dt-col-cmmodifier>div>span>a";
    private static final String MODIFIED = "td.yui-dt-col-modified>div>span";

    public TableViewFileDirectoryInfo(String nodeRef, WebElement webElement, WebDrone drone)
    {
        super(nodeRef, webElement, drone);
        
        FILENAME_IDENTIFIER = "td.yui-dt-col-name span>a";
        TITLE = "td.yui-dt-col-cmtitle>div>span";
        FILE_DESC_IDENTIFIER = "td.yui-dt-col-cmdescription>div>span";
        MORE_ACTIONS = drone.getElement("more.actions");
        CONTENT_ACTIONS = "td.yui-dt-col-actions";
        rowElementXPath = "../../..";
        resolveStaleness();
    }

    public String getCreator()
    {
        try
        {
            return findAndWait(By.cssSelector(CREATOR)).getText();
        }
        catch (TimeoutException te) { }
        throw new PageOperationException("Unable to find content column creator");
    }

    public UserProfilePage selectCreator()
    {
        WebElement creatorLink = findElement(By.cssSelector(CREATOR));
        creatorLink.click();
        return new UserProfilePage(getDrone());
    }

    public String getCreated()
    {
        try
        {
            return findAndWait(By.cssSelector(CREATED)).getText();
        }
        catch (TimeoutException te) { }
        throw new PageOperationException("Unable to find content column created");
    }
    
    public String getModifier()
    {
        try
        {
            return findAndWait(By.cssSelector(MODIFIER)).getText();
        }
        catch (TimeoutException te) { }
        throw new PageOperationException("Unable to find content column modifier");
    }

    public UserProfilePage selectModifier()
    {
        WebElement creatorLink = findElement(By.cssSelector(MODIFIER));
        creatorLink.click();
        return new UserProfilePage(getDrone());
    }

    public String getModified()
    {
        try
        {
            return findAndWait(By.cssSelector(MODIFIED)).getText();
        }
        catch (TimeoutException te) { }
        throw new PageOperationException("Unable to find content column created");
    }
}
