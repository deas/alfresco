package org.alfresco.po.share.site.document;

import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

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

        public int getCurrentPageCount()
        {
                WebElement currentPageNumberElem = getFormElement().findElement(CURRENT_PAGE_SPAN);
                return Integer.valueOf(currentPageNumberElem.getText());
        }

        public HtmlPage clickNext()
        {
                getFormElement().findElement(NEXT_PAGE_LINK).click();
                return drone.getCurrentPage().render();
        }

        public HtmlPage clickPrevious()
        {
                getFormElement().findElement(PREVIOUS_PAGE_LINK).click();
                return drone.getCurrentPage().render();
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
                                paginationLink.click();
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
}
