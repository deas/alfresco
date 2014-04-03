//package org.alfresco.po.share.site.document;
//
//import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;
//
//import org.alfresco.webdrone.HtmlPage;
//import org.alfresco.webdrone.RenderTime;
//import org.alfresco.webdrone.WebDrone;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//
//public class CreateHtmlContentPage extends CreatePlainTextContentPage
//{
//    protected static final By CONTENT = By.cssSelector("textarea[id$='default_prop_cm_content']");
//
//    public CreateHtmlContentPage(WebDrone drone)
//    {
//        super(drone);
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public CreatePlainTextContentPage render(RenderTime timer)
//    {
//        elementRender(timer, getVisibleRenderElement(NAME), getVisibleRenderElement(TITLE), getVisibleRenderElement(DESCRIPTION), getVisibleRenderElement(CONTENT), getVisibleRenderElement(SUBMIT_BUTTON), getVisibleRenderElement(CANCEL_BUTTON));
//        return this;
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public CreatePlainTextContentPage render()
//    {
//        return render(new RenderTime(maxPageLoadingTime));
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public CreatePlainTextContentPage render(long time)
//    {
//        return render(new RenderTime(time));
//    }
//
//    protected void createContent(ContentDetails details) 
//    {
//        if(details.getName() != null)
//        {
//            WebElement nameElement = drone.find(NAME);
//            nameElement.clear();
//            nameElement.sendKeys(details.getName());
//        }
//        
//        if(details.getTitle() != null)
//        {
//            WebElement titleElement = drone.find(TITLE);
//            titleElement.clear();
//            titleElement.sendKeys(details.getTitle());
//        }
//        
//        if(details.getDescription() != null)
//        {
//            WebElement descriptionElement = drone.find(DESCRIPTION);
//            descriptionElement.clear();
//            descriptionElement.sendKeys(details.getDescription());
//        }
//        
//        if(details.getContent() != null)
//        {
//            WebElement contentElement = drone.find(CONTENT);
//            contentElement.clear();
//            contentElement.sendKeys(details.getContent());
//        }
//    }
//}
