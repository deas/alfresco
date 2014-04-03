/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.site.SitePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * In Line Edit Page Object, Where user edit the content.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class InlineEditPage extends SitePage
{
    protected static final By NAME = By.cssSelector("input[id$='default_prop_cm_name']");
    protected static final By TITLE = By.cssSelector("input[id$='default_prop_cm_title']");
    protected static final By DESCRIPTION = By.cssSelector("textarea[id$='default_prop_cm_description']");
    protected static final By SUBMIT_BUTTON = By.cssSelector("button[id$='form-submit-button']");

    public InlineEditPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public InlineEditPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(NAME), getVisibleRenderElement(TITLE), getVisibleRenderElement(DESCRIPTION),
                getVisibleRenderElement(SUBMIT_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InlineEditPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public InlineEditPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Pass MimeType to get appropriate document page object.
     */
    public HtmlPage getInlineEditDocumentPage(MimeType mimeType)
    {
        switch (mimeType)
        {
            case HTML:
                return new EditHtmlDocumentPage(drone);
            default:
                return new EditTextDocumentPage(drone);
        }
    }
}
