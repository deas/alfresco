/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.blogIntegration.ui;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.alfresco.module.blogIntegration.BlogIntegrationImplementation;
import org.alfresco.module.blogIntegration.BlogIntegrationService;
import org.alfresco.web.app.servlet.FacesHelper;
import org.alfresco.web.bean.generator.BaseComponentGenerator;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.ui.repo.component.property.PropertySheetItem;
import org.alfresco.web.ui.repo.component.property.UIPropertySheet;

/**
 * Blog selector generator.
 * 
 * @author Roy Wetherall
 */
public class BlogSelectorGenerator extends BaseComponentGenerator
{
    /** Node */
    protected Node node;
    
    /** Blog integration service */
    BlogIntegrationService blogIntegrationService;
    
    /**
     * Set the blog integration service
     * 
     * @param blogIntegrationService    the blog integration service
     */
    public void setBlogIntegrationService(BlogIntegrationService blogIntegrationService)
    {
        this.blogIntegrationService = blogIntegrationService;
    }
    
    /**
     * @see org.alfresco.web.bean.generator.IComponentGenerator#generate(javax.faces.context.FacesContext, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public UIComponent generate(FacesContext context, String id)
    {
        UIComponent component = context.getApplication().createComponent(UISelectOne.COMPONENT_TYPE);
        FacesHelper.setupComponentId(context, component, id);        
  
        // create the list of choices
        UISelectItems itemsComponent = (UISelectItems)context.getApplication().createComponent("javax.faces.SelectItems");
  
        itemsComponent.setValue(getBlogItems());
   
        // add the items as a child component
        component.getChildren().add(itemsComponent);
           
       return component;
    }
    
    /**
     * Gets the items to put in the drop down control using the blog integration service.
     * 
     * @return  SelectItem[]    array of select items
     */
    protected SelectItem[] getBlogItems()
    {       
        List<BlogIntegrationImplementation> blogs = this.blogIntegrationService.getBlogIntegrationImplementations();
        SelectItem[] items = new SelectItem[blogs.size()];
        int index = 0;
        for (BlogIntegrationImplementation blog : blogs)
        {
            items[index] = new SelectItem(blog.getName(), blog.getDisplayName());
            index ++;
        }
        
        return items;
    }
    
    /**
     * @see org.alfresco.web.bean.generator.BaseComponentGenerator#createComponent(javax.faces.context.FacesContext, org.alfresco.web.ui.repo.component.property.UIPropertySheet, org.alfresco.web.ui.repo.component.property.PropertySheetItem)
     */
    @Override
    protected UIComponent createComponent(FacesContext context, UIPropertySheet propertySheet, PropertySheetItem item) {
       
       this.node = propertySheet.getNode();
        
       return super.createComponent(context, propertySheet, item);
    }
}
