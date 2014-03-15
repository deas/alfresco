package org.alfresco.po.share.admin;

import java.util.List;

import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Representation of an Action Set
 * 
 * @author Richard Smith
 */
public class ActionsSet
{

    /** Constants */
    private static final By CONTROL_ELEMENT = By.cssSelector("div.dijitMenuItem");
    private static final String MENU_ELEMENT_SUFFIX = "_dropdown";
    private static final String MENU_ELEMENT_SELECTOR_TEMPLATE = "div#?";
    private static final By MENU_ROWS = By.cssSelector("tr.dijitMenuItem");
    private static final By MENU_LABEL = By.cssSelector("td.dijitMenuItemLabel");
    private static final By DIALOG = By.cssSelector("div.alfresco-dialog-AlfDialog");
    private static final By DIALOG_BUTTONS = By.cssSelector("span.dijitButtonNode");

    private WebDrone drone;
    private WebElement control;
    private String menuId;

    /**
     * Instantiates a new actions set.
     * 
     * @param webDrone the web drone
     * @param element the element
     */
    public ActionsSet(WebDrone drone, WebElement element)
    {
        this.drone = drone;
        this.control = element.findElement(CONTROL_ELEMENT);
        // The dropdown menu has the same id as the control element with '_dropdown' appended
        this.menuId = this.control.getAttribute("id") + MENU_ELEMENT_SUFFIX;
    }

    /**
     * Click action by name.
     * 
     * @param actionName the action name
     */
    public void clickActionByName(String actionName)
    {
        // Click the control to open the menu
        control.click();

        // Compose the selector for the drop down menu
        String menuSelector = StringUtils.replace(MENU_ELEMENT_SELECTOR_TEMPLATE, "?", this.menuId);

        // Find the menu
        WebElement menu = this.drone.find(By.cssSelector(menuSelector));

        // If the menu is not null and is displayed and is enabled
        if (menu != null && menu.isDisplayed() && menu.isEnabled())
        {

            // Within the menu element find the MENU_ROWS
            List<WebElement> menuRows = menu.findElements(MENU_ROWS);

            // Iterate over the menuRows and click the control that matches the named action
            for (WebElement menuRow : menuRows)
            {
                if (actionName.equalsIgnoreCase(StringUtils.trim(menuRow.findElement(MENU_LABEL).getText())))
                {
                    menuRow.click();
                    break;
                }
            }
        }
    }

    /**
     * Click and dialog action by name.
     * 
     * @param actionName the action name
     * @param dialogButtonName the name of the dialog button to click
     */
    public void clickActionByNameAndDialogByButtonName(String actionName, String dialogButtonName)
    {
        // Click the action
        clickActionByName(actionName);

        // Find the dialog
        WebElement dialog = this.drone.find(DIALOG);

        if(dialog != null && dialog.isDisplayed() && dialog.isEnabled())
        {
            // Within the dialog find the buttons
            List<WebElement> dialogButtons = dialog.findElements(DIALOG_BUTTONS);
            
            // Iterate over the dialogButtons and click the button that matches the named dialog button name
            for (WebElement button : dialogButtons)
            {
                if (dialogButtonName.equalsIgnoreCase(StringUtils.trim(button.getText())))
                {
                    button.click();
                    break;
                }
            }
        }
    }
}