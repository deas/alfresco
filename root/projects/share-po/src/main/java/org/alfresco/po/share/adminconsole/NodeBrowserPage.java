package org.alfresco.po.share.adminconsole;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;
import static com.google.common.base.Preconditions.*;

/**
 * @author Aliaksei Boole
 */
public class NodeBrowserPage extends SharePage
{
    private final static By TITLE_LABEL = By.cssSelector(".title>label");
    private final static By SEARCH_BUTTON = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-search-button-button");
    private final static By SEARCH_TEXT_FIELD = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-search-text");
    private final static By SEARCH_BAR = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-search-bar");

    private final static String SEARCH_RESULT_BASE_CSS = "tr[class~='yui-dt-rec']";
    private final static By SEARCH_RESULT = By.cssSelector(SEARCH_RESULT_BASE_CSS);
    private final static By RESULT_NAME = By.cssSelector(SEARCH_RESULT_BASE_CSS + "> td[class~='yui-dt-col-nodeRef'] > div > a[href='#']");

    private final static By SEARCH_QUERY_TYPE_BUTTON = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-lang-menu-button-button");
    private final static By STORE_TYPE_BUTTON = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-store-menu-button-button");
        private final static By VISIBLE_DROPDOWN_SELECT = By.cssSelector("div[class~='visible']> div > ul >li[class~='yuimenuitem'] > a[class~='yuimenuitemlabel']");

    private final static String REGEXP_PATTERN = ".*%s.*";

    public NodeBrowserPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public NodeBrowserPage render(RenderTime renderTime)
    {
                elementRender(renderTime,
                        getVisibleRenderElement(SEARCH_BUTTON),
                        getVisibleRenderElement(SEARCH_TEXT_FIELD),
                        getVisibleRenderElement(SEARCH_QUERY_TYPE_BUTTON),
                        getVisibleRenderElement(STORE_TYPE_BUTTON),
                        getVisibleRenderElement(TITLE_LABEL));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NodeBrowserPage render(long l)
    {
        checkArgument(l > 0);
        return render(new RenderTime(l));
    }

    @SuppressWarnings("unchecked")
    @Override
    public NodeBrowserPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public void fillQueryField(String query)
    {
        fillField(SEARCH_TEXT_FIELD, query);
    }

    public void clickSearchButton()
    {
        click(SEARCH_BUTTON);
    }

    public void selectStore(String text)
    {
        select(STORE_TYPE_BUTTON, text);
    }

    public void selectQueryType(String text)
    {
        select(SEARCH_QUERY_TYPE_BUTTON, text);
    }

    public boolean isSearchResults()
    {
        List<WebElement> results;
        try
        {
            results = drone.findAndWaitForElements(SEARCH_RESULT, 5000);
        }
        catch (TimeoutException e)
        {
            results = Collections.emptyList();
        }
        return results.size() > 0;
    }

    public int getSearchResultsCount()
    {
        return isSearchResults() ? drone.findAndWaitForElements(SEARCH_RESULT, 5000).size() : 0;
    }

    public boolean isOnSearchBar(String regExp)
    {
        checkNotNull(regExp);
        WebElement searchBar = drone.findAndWait(SEARCH_BAR);
        String text = searchBar.getText();
        return text.matches(formatRegExp(regExp));
    }

    public boolean isInResults(String regExp)
    {
        checkNotNull(regExp);
        String fRegExp = formatRegExp(regExp);
        List<WebElement> resultsName = drone.findAndWaitForElements(RESULT_NAME, 5000);
        for (WebElement resultName : resultsName)
        {
            String name = resultName.getText();
            if (name.matches(fRegExp))
            {
                return true;
            }
        }
        return false;
    }

    private String formatRegExp(String regExp)
    {
        return String.format(REGEXP_PATTERN, regExp);
    }

    private void select(By buttonLocator, String text)
    {
        checkNotNull(text);
        click(buttonLocator);
        List<WebElement> options = drone.findAndWaitForElements(VISIBLE_DROPDOWN_SELECT);
        for (WebElement option : options)
        {
            if (text.equals(option.getText()))
            {
                option.click();
                return;
            }
        }
    }

    private void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = drone.findAndWait(selector);
        inputField.clear();
        if (text != null)
        {
            inputField.sendKeys(text);
        }
    }
}
