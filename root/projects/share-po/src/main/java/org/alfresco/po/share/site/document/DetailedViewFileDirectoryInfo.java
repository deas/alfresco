/**
 * 
 */
package org.alfresco.po.share.site.document;

import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.WebElement;

/**
 * @author cbairaajoni
 *
 */
public class DetailedViewFileDirectoryInfo extends SimpleDetailTableView
{

    public DetailedViewFileDirectoryInfo(String nodeRef, WebElement webElement, WebDrone drone)
    {
        super(nodeRef, webElement, drone);
        rowElementXPath = "../../..";
        resolveStaleness();
    }

}
