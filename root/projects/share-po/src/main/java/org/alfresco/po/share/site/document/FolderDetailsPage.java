/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class supports the Folder Details page and extends the
 * DetailsPage for common functionalities.
 * 
 * @author Naved Shah
 * @since 1.7.0
 */
public class FolderDetailsPage extends DetailsPage
{

    private final Log logger = LogFactory.getLog(this.getClass());
    private static final String FOLDER_TAGS = "div.folder-tags";
    private static final String CSS_PANEL = "div.panel-body";
    private static final String NODE_PATH = "div.node-path";
    private static final String DOWNLOAD_TITLE = "Download as Zip";
    private static final String VIEW_ON_GOOGLE_MAPS = "//span[text()='View on Google Maps']";
    private static final String FOLDER_ACTION_SECTION = "div[class='folder-actions folder-details-panel']";
    private static final String PROPERTIES_INFO = "//div[contains(@class, 'metadata-header')]/descendant::div[@class='viewmode-field']/span[@class='viewmode-label']";

    private final By changeTypeLink;

    /**
     * Constructor
     */
    public FolderDetailsPage(WebDrone drone)
    {
        super(drone);
        switch (alfrescoVersion)
        {
            case Enterprise41:
                changeTypeLink = By.cssSelector("div.document-change-type a");
                break;

            default:
                changeTypeLink = By.cssSelector("div#onActionChangeType a");
                break;
        }
        PERMISSION_SETTINGS_PANEL_CSS = ".folder-permissions";
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized FolderDetailsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized FolderDetailsPage render(long time)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verifies if the page has rendered completely by checking the page load is
     * complete and in addition it will observe key HTML elements have rendered.
     * Add subfolder using sumbol @>@
     * 
     * @param timer Max time to wait
     * @return {@link DocumentDetailsPage}
     */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized FolderDetailsPage render(RenderTime timer)
    {
        elementRender(timer, RenderElement.getVisibleRenderElement(By.cssSelector("div.node-header>div.node-info>h1.thin.dark")));
        return this;
    }

    /**
     * Selects the <View Details> link on the select data row on DocumentLibrary
     * Page. Only available for content type = Folder.
     * 
     * @return {@link DocumentLibraryPage} response
     */
    public boolean isCorrectPath(String folderName)
    {
        boolean isPathCorrect = true;
        try
        {
            WebElement pathElement = drone.findAndWait(By.cssSelector(NODE_PATH));
            String pathToBeVerify = "Documents" + " > " + folderName;
            if (!pathToBeVerify.equals(pathElement.getText()))
            {
                isPathCorrect = false;
            }
        }
        catch (TimeoutException noSuchEleExc)
        {
            logger.error("Element : " + NODE_PATH + " does not exist");
        }
        return isPathCorrect;
    }

    /**
     * Verifies Tag Panel is present in the page.
     * 
     * @return
     */
    public boolean isTagPanelPresent()
    {
        String tagText;
        String tagValue;
        try
        {
            tagText = drone.findAndWait(By.cssSelector(FOLDER_TAGS)).getText();
            tagValue = drone.findAndWait(By.cssSelector(CSS_PANEL)).getText();
            if (tagText.contains("Tags") && !tagValue.isEmpty())
            {
                return true;
            }

        }
        catch (TimeoutException noSuchEleExcep)
        {
            logger.error("Element :" + FOLDER_TAGS + " OR " + CSS_PANEL + " does not exist.");
        }
        throw new PageException("Tag pane is not present!");
    }

    /**
     * Verify that Folder Action panel displays on the details page
     *
     * @return boolean
     */
    public boolean isFolderActionsPresent()
    {
        try
        {
            return drone.findAndWait(By.cssSelector(FOLDER_ACTION_SECTION)).isDisplayed();
        }
        catch (TimeoutException e)
        {
            logger.error("Element : " + FOLDER_ACTION_SECTION + " does not exist");
        }
        return false;
    }

    /**
     * Selects the <Download as zip> link on the select data row on
     * DocumentLibrary Page. Only available for content type = Folder.
     * 
     * @return {@link DocumentLibraryPage} response
     */
    public FolderDetailsPage selectDownloadFolderAsZip(String type)
    {
        if (AlfrescoVersion.Enterprise41.equals(alfrescoVersion) || alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("Option Download Folder as Zip is not available for this version of Alfresco");
        }
        try
        {
            WebElement menuOption = drone.findAndWait(By.cssSelector("div." + type + "-download>a"));
            menuOption.click();
            // Assumes driver capability settings to save file in a specific
            // location when
            // <Download> option is selected via Browser
            return new FolderDetailsPage(drone);
        }
        catch (TimeoutException toe)
        {

        }
        throw new PageException("Download option is not present!");
    }

    /**
     * Selects the Change Type link from FOlder actions.
     * 
     * @return {@link ChangeTypePage} response
     */
    public ChangeTypePage selectChangeType()
    {
        if (alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("Operation available only for Enterprise version");
        }
        drone.findAndWait(changeTypeLink).click();
        return new ChangeTypePage(drone);
    }

    /**
     * @param comment
     * @return
     */
    public boolean isCommentAddedAndRemoved(String comment)
    {
        if (addComment(comment) instanceof FolderDetailsPage)
        {
            if (removeComment(comment) instanceof FolderDetailsPage)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if donwload zip is displayed.
     * 
     * @return true if visible
     */
    public boolean isDownloadAsZipAtTopRight()
    {
        try
        {
            String value = drone.findAndWaitForElements(By.cssSelector(".action-link")).get(0).getAttribute("title");
            if (DOWNLOAD_TITLE.equals(value))
            {
                return true;
            }
        }
        catch (TimeoutException toe)
        {
        }
        return false;
    }

    /**
     * Get Folder Action List
     * 
     * @return List<String>
     */
    public List<String> getFolderActionList()
    {
        List<String> actionNames = new ArrayList<String>();
        String text = null;

        List<WebElement> actions = drone.findAndWaitForElements(By.xpath("//div[contains(@id, 'default-actionSet')]/div/a/span"));

        for (WebElement action : actions)
        {
            text = action.getText();

            if (text != null)
            {
                actionNames.add(text);
            }
        }
        return actionNames;
    }

    public boolean isViewOnGoogleMapsLinkVisible()
    {
        try
        {
            return drone.find(By.xpath(VIEW_ON_GOOGLE_MAPS)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to return Edit icons on Details page
     *
     * @return List <WebElement>
     */

    public List<WebElement> getEditControls()
    {
        try
        {
            List<WebElement> elements = drone.findAndWaitForElements(By.cssSelector("a.edit"));
            return elements;
        }
        catch (TimeoutException e)
        {
            return Collections.emptyList();
        }
    }

    /**
     *  Method checks if the author of the folder modifying is displayed at a Folder details page
     * @param drone  Webcrone instance
     * @param userName string name of the modifier
     * @return
     */
    public static boolean IsModifierDisplayed(WebDrone drone, String userName)
    {

        try
        {
            WebElement modifiedBy = drone.findAndWait(By.xpath("//span[contains(@class,'item-modifier')]"));
            return modifiedBy.isDisplayed() && modifiedBy.getText().contains(userName);
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }

    }

    public FolderRulesPage selectManageRules()
    {
        try
        {
            drone.findAndWait(By.cssSelector(".folder-manage-rules>a")).click();
            return new FolderRulesPage(drone);
        }
        catch(TimeoutException te)
        {
            throw new PageOperationException("Manage Rules link is not displayed at a page");
        }
    }

    /**
     * Method for verify all labels that Properties section must contain on the details page
     *
     * @return boolean
     */
    public boolean isPropertiesLabelsPresent()
    {
        boolean present = false;
        try
        {
            List<WebElement> labels = drone.findAll(By.xpath(PROPERTIES_INFO));

            if(labels.size() != 3)
                throw new Exception("Properties section doesn't contain full information");

            for(WebElement label : labels)
            {
                present = false;
                for(String labelName : new String[]{"Name:", "Title:", "Description:"})
                {
                    if(label.getText().equals(labelName))
                        present = true;
                }
                if (!present)
                    return false;

            }

        }
        catch (StaleElementReferenceException sere)
        {
            logger.error("Element :" + PROPERTIES_INFO + " does not exist", sere);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Element :" + PROPERTIES_INFO + " does not exist", nse);
        } catch (Exception e) {
            logger.error("Properties section doesn't contain full information");
        }
        return present;
    }
}