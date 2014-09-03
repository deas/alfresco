package org.alfresco.po.share.search;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;


//Incomplete
public class CreateNewFilterPopUpPage extends SharePage {

	
	private static final By FILTER_ID = By.cssSelector("div[class$=InputContainer] input[name=filterID]");
    private static final By DISPLAY_NAME = By.cssSelector("div[class$=InputContainer] input[name=displayName]");
    // Need the unique css selector 
    private static final By SELECTED_DROPDOWN_ELEMENT = By.cssSelector("");
    private static final By PROPERTY_DROPDOWN_ITEMS = By.cssSelector("td[id^='dijit_MenuItem'][id$='text']");
    private static final By CREATE_FILTER_POPUP = By.cssSelector("div[id^='uniqName_1']");
    private static Log logger = LogFactory.getLog(CreateNewFilterPopUpPage.class);
	
    public CreateNewFilterPopUpPage(WebDrone drone)
    {
        super(drone);
    }
    
    @SuppressWarnings("unchecked")
    public CreateNewFilterPopUpPage render(RenderTime timer)
    {
        RenderElement actionMessage = getActionMessageElement(ElementState.INVISIBLE);
        elementRender(timer, getVisibleRenderElement(CREATE_FILTER_POPUP),
                 actionMessage);
        return this;
    }

    
     /** (non-Javadoc)
     * 
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFilterPopUpPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFilterPopUpPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
     /** Send Filter ID in facetedSearchConfig Page
     * 
     * @return*/     
    
    public void sendFilterID(String filterID)
    {
        try
        {
          drone.findAndWait(FILTER_ID).sendKeys(filterID);
        } catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: FilterID" + toe.getMessage());
        }

    }   
    
     /* Send dispaly name in facetedSearchConfig Page
     * 
     * @return*/
     
    
    public void sendDisplayName(String displayName)
    {
        try
        {
          drone.findAndWait(DISPLAY_NAME).sendKeys(displayName);
        } catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: display name" + toe.getMessage());
        }

    }
    
    /**
     * Get the String value of selected property type
     * 
     * @return String value of select property type
     */
	protected String getSelectedPropertyType() 
	{

		try 
		{
			WebElement selected = drone.find(SELECTED_DROPDOWN_ELEMENT);
			return selected.getText();
		} 
		catch (NoSuchElementException e) 
		{
			if (logger.isTraceEnabled()) 
			{
				logger.trace("Unable to select the property");
			}
		}
		throw new PageOperationException("Unable to select the property : ");
	}
    
    /**
     * Selects a property type from the drop down by matching
     * the option displayed with the propertyType input.
     * 
     * @param mimeType String identifier as seen on the drop down
     */
    public CreateNewFilterPopUpPage selectPropertyType(final PropertyType propertyType)
    {

        List<WebElement> dropDown;
		try {
			if (!isPropertyDisplayed(propertyType))
			{
			    throw new UnsupportedOperationException("This operation is not supported");
			}
			dropDown = drone.findAll(PROPERTY_DROPDOWN_ITEMS);
			for(WebElement dropdown:dropDown )
			{
				if(dropdown.getText().equals(propertyType))
				{
					if(dropdown.getText().equals(getSelectedPropertyType()))
					{
						return this;
					}
					dropdown.click();
					return this;
				}
			}
		} catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the property");
            }
        }
		throw new PageOperationException("Unable to select the property : " );       
       
    }

    
    /**
     * Helper method to return true if Share Dialogue is displayed
     * 
     * @return boolean <tt>true</tt> is Share Dialogue is displayed
     */
    public boolean isPropertyDisplayed(final PropertyType propertyType)
    {
        try
        {
            String dialogue = getSelectedProperty();
            
                if (dialogue.equals(propertyType))
                {                	
            	return true;            	
                }
             }
        catch (NoSuchElementException nse)
        {
        	throw new PageOperationException("unable to locate the poperty element");
        }
        return false;
    }

    /**
     * Helper method to return selected property
     * 
     * @return WebElement
     */
    public String getSelectedProperty()
    {
        try
        {
            WebElement propertyElement = drone.find(SELECTED_DROPDOWN_ELEMENT);

            return propertyElement.getText();
        }
        catch (NoSuchElementException nse)
        {
        	throw new PageOperationException("unable to get the selected property");
        }
    }

}
