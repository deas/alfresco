package org.alfresco.po.share.util;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;

/**
 * The Class TestUtils.
 */
public class TestUtils
{

    /**
     * Is a WebElement usable?
     * 
     * @param element the WebElement
     * @return boolean
     */
    public static boolean usableElement(WebElement element)
    {
        if (element != null && element.isDisplayed() && element.isEnabled() 
                && !StringUtils.contains(element.getAttribute("class"), "dijitDisabled")
                && !StringUtils.contains(element.getAttribute("aria-disabled"), "true"))
        {
            return true;
        }
        return false;
    }
}