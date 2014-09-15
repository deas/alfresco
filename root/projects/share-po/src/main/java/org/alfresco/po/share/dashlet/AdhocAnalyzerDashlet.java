package org.alfresco.po.share.dashlet;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class AdhocAnalyzerDashlet extends AbstractDashlet implements Dashlet
{

    private static Log logger = LogFactory.getLog(AdhocAnalyzerDashlet.class);
    
    private static final String DASHLET = "div[id*='DASHLET']";
    private static final String DASHLET_TITLE = "div[class='alfresco-dashlets-Dashlet--title title']";
    private static final String DASHLET_OPEN_DROPDOWN = "//span[text()='Open...']";
    private static final String DASHLET_MESSAGE = "//div[text()='Click the menu to display an analysis you have previously created.']";
    protected final static String THERE_ARE_NO_ANALYSES = "//td[text()='(There are no analyses)']";
    
    protected AdhocAnalyzerDashlet(WebDrone drone)
    {
        super(drone, By.cssSelector(DASHLET));
    }

    @SuppressWarnings("unchecked")
    public AdhocAnalyzerDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdhocAnalyzerDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    public AdhocAnalyzerDashlet render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(By.cssSelector(DASHLET)));
        return this;
    }

    /**
     * Checks if Title is displayed in a dashlet header
     * 
     * @return
     */
    public boolean isTitleDisplayed()
    {
        try
        {
            WebElement dashletTitle = drone.find(By.cssSelector(DASHLET_TITLE));
            return dashletTitle.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Title in Adhoc Analyzer dashlet header " + nse);
            throw new PageException("Unable to find Title in Adhoc Analyzer dashlet header.", nse);
        }
    } 


    /**
     * Checks if Open... is displayed in Adhoc Analyzer dashlet
     * 
     * @return
     */
    public boolean isOpenDisplayed()
    {
        try
        {
            WebElement openDropDown = drone.find(By.xpath(DASHLET_OPEN_DROPDOWN));
            return openDropDown.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Open... in Adhoc Analyzer dashlet " + nse);
            throw new PageException("Unable to find Open... in Adhoc Analyzer dashlet.", nse);
        }
    } 


    /**
     * Checks if "Click the menu to display an analysis you have previously created."
     * is displayed in Adhoc Analyzer dashlet
     * 
     * @return
     */
    public boolean isDashletMessageDisplayed()
    {
        try
        {
            WebElement dashletMessage = drone.find(By.xpath(DASHLET_MESSAGE));
            return dashletMessage.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No 'Click the menu to display an analysis you have previously created.' message in Adhoc Analyzer dashlet " + nse);
            throw new PageException("Unable to find 'Click the menu to display an analysis you have previously created.' in Adhoc Analyzer dashlet.", nse);
        }
    } 
    
    /**
     * Checks if (There are no analyses) message is displayed
     * 
     * @return
     */
    public boolean isThereAreNoAnalysesDisplayed()
    {
        try
        {
            WebElement noAnalyses = drone.find(By.xpath(THERE_ARE_NO_ANALYSES));
            return noAnalyses.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No (There are no analyses) message " + nse);
            throw new PageException("Unable to find (There are no analyses) message.", nse);
        }
    }
    

    /**
     * Clicks on Open... dropdown in Adhoc Analyzer dashlet
     */
    public void clickOnOpenDropdown()
    {
        try
        {
            WebElement openDropDown = drone.findAndWait(By.xpath(DASHLET_OPEN_DROPDOWN));
            openDropDown.click();
        }
        catch (TimeoutException te)
        {
            logger.error("Not able to click on Open... in Adhoc Analyzer dashlet " + te);
        }
    }  
    
    /**
     * Clicks on the report in Open... dropdown 
     * 
     * @param reportName
     */
    public void clickOnExistingReport(String reportName)
    {
        try
        {
            WebElement report = drone.findAndWait(By.xpath(String.format("//td[text()='%s']", reportName)));
            report.click();
        }
        catch (TimeoutException te)
        {
            logger.error(String.format("Not able to click on %s report", reportName), te);
        }       
    }
    
    /**
     * Returns dashlet title
     * 
     * @return
     */
    public String getTitle()
    {
        try
        {
            WebElement dashletTitle = drone.find(By.cssSelector(DASHLET_TITLE));
            return dashletTitle.getText();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Title in Adhoc Analyzer dashlet header " + nse);
            throw new PageException("Unable to find Title in Adhoc Analyzer dashlet header.", nse);
        }
    } 
    
}
