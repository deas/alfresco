package org.alfresco.po.share.site.document;

import org.alfresco.po.share.enums.Encoder;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

public class CreateHtmlContentPage extends CreatePlainTextContentPage
{
    private static final String TINYMCE_CONTENT = "template_x002e_create-content_x002e_create-content_x0023_default_prop_cm_content_ifr";

    public CreateHtmlContentPage(WebDrone drone)
    {
        super(drone);
        CONTENT = By.cssSelector("iframe[id$='default_prop_cm_content_ifr']");
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateHtmlContentPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateHtmlContentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateHtmlContentPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @Override
    protected void createContentField(ContentDetails details)
    {
        if (details.getContent() != null)
        {
            TinyMceEditor tinyMCEEditor = new TinyMceEditor(drone);
            tinyMCEEditor.setTinyMce(TINYMCE_CONTENT);

            tinyMCEEditor.setText(details.getContent(), Encoder.ENCODER_HTML);
        }
    }
}
