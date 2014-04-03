package org.alfresco.po.share.site.document;

import com.google.common.base.Predicate;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * @author Aliaksei Boole
 */
public class PaginationForm extends HtmlElement
{
    private final By FORM_XPATH;
    private final static By NEXT_PAGE_LINK = By.xpath(".//a[@title='Next Page']");
    private final static By PREVIOUS_PAGE_LINK = By.xpath(".//a[@title='Previous Page']");
    private final static By PAGES_SELECT_LINKS = By.xpath(".//span[@class='yui-pg-pages']/*");
    private final static By PAGE_INFO_LABEL = By.xpath(".//span[@class='yui-pg-current']");
    private final static By CURRENT_PAGE_SPAN = By.xpath(".//span[@class='yui-pg-pages']/span");

    public PaginationForm(WebDrone drone, By formXpath)
    {
        super(drone);
        FORM_XPATH = formXpath;
    }

    private WebElement getFormElement()
    {
        return drone.findAndWait(FORM_XPATH);
    }

    public int getCurrentPageNumber()
    {
        WebElement currentPageNumberElem = getFormElement().findElement(CURRENT_PAGE_SPAN);
        return Integer.valueOf(currentPageNumberElem.getText());
    }

    public HtmlPage clickNext()
    {
        int beforePageNumber = getCurrentPageNumber();
        getFormElement().findElement(NEXT_PAGE_LINK).click();
        waitUntilPageNumberChanged(beforePageNumber);
        return drone.getCurrentPage().render();
    }

    public HtmlPage clickPrevious()
    {
        int beforePageNumber = getCurrentPageNumber();
        getFormElement().findElement(PREVIOUS_PAGE_LINK).click();
        waitUntilPageNumberChanged(beforePageNumber);
        return drone.getCurrentPage().render();
    }

    public boolean isPreviousButtonEnable()
    {
        try
        {
            getFormElement().findElement(PREVIOUS_PAGE_LINK);
            return true;
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    public boolean isNextButtonEnable()
    {
        try
        {

            getFormElement().findElement(NEXT_PAGE_LINK);
            return true;
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    public List<WebElement> getPaginationLinks()
    {
        return getFormElement().findElements(PAGES_SELECT_LINKS);
    }

    public HtmlPage clickOnPaginationPage(int linkNumber)
    {
        List<WebElement> paginationLinks = getPaginationLinks();
        for (WebElement paginationLink : paginationLinks)
        {
            int currentLinkNumber = Integer.valueOf(paginationLink.getText());
            if (currentLinkNumber == linkNumber)
            {
                int beforePageNumber = getCurrentPageNumber();
                paginationLink.click();
                waitUntilPageNumberChanged(beforePageNumber);
                break;
            }
        }
        return drone.getCurrentPage().render();
    }

    public String getPaginationInfo()
    {
        return getFormElement().findElement(PAGE_INFO_LABEL).getText();
    }

    public boolean isDisplay()
    {
        try
        {
            return drone.findAndWait(FORM_XPATH, 2000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    private void waitUntilPageNumberChanged(int beforePageNumber)
    {
        WebDriverWait wait = new WebDriverWait(((WebDroneImpl) drone).getDriver(), 5);
        wait.until(paginationPageChanged(beforePageNumber));
    }

    private Predicate<WebDriver> paginationPageChanged(final int beforePageNumber)
    {
        return new Predicate<WebDriver>()
        {
            @Override
            public boolean apply(WebDriver driver)
            {
                WebElement currentPageIndicator = driver.findElement(FORM_XPATH).findElement(CURRENT_PAGE_SPAN);
                return Integer.valueOf(currentPageIndicator.getText()) != beforePageNumber;
            }
        };
    }
}
