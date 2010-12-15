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

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.alfresco.module.blogIntegration.BlogIntegrationModel;
import org.alfresco.module.blogIntegration.BlogIntegrationRuntimeException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.servlet.FacesHelper;
import org.alfresco.web.bean.BrowseBean;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.component.UIActionLink;


/**
 * Blog details action listener
 * 
 * @author Roy Wetherall
 */
public class BlogDetailsActionListener implements BlogIntegrationModel
{
    /** The service registry */
    private ServiceRegistry services;
    
    /**
     * Set the service registry
     * 
     * @param services  the service registry
     */
    public void setServiceRegistry(ServiceRegistry services) 
    {
        this.services = services;
    }
    
    /**
     * Listener's execute method
     */
    public void executeScript(ActionEvent event)
    {
        // Get the script to be executed
        UIActionLink link = (UIActionLink)event.getComponent();
        Map<String, String> params = link.getParameterMap();
        
        String action = params.get("action");
        
        String id = params.get("id");
        NodeRef documentNodeRef = new NodeRef(Repository.getStoreRef(), id);        
        if ("add".equals(action) == true)
        {
            this.services.getNodeService().addAspect(documentNodeRef, ASPECT_BLOG_DETAILS, null);
        }
        else if ("remove".equals(action) == true)
        {
            this.services.getNodeService().removeAspect(documentNodeRef, ASPECT_BLOG_DETAILS);
        }
        else
        {
            throw new BlogIntegrationRuntimeException("Invalid action has been specified '" + action + "'");
        }
        
        FacesContext context = FacesContext.getCurrentInstance();
        BrowseBean browseBean = (BrowseBean)FacesHelper.getManagedBean(context, "BrowseBean");        
        browseBean.getActionSpace().reset();
        UIComponent comp = context.getViewRoot().findComponent("dialog:dialog-body:document-props");
        comp.getChildren().clear();
    }
}
