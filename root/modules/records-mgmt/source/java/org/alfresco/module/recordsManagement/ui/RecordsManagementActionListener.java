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
package org.alfresco.module.recordsManagement.ui;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.app.servlet.FacesHelper;
import org.alfresco.web.bean.BrowseBean;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.component.UIActionLink;


/**
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementActionListener 
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
    
    public void executeScript(ActionEvent event)
    {
        // Get the script to be executed
        UIActionLink link = (UIActionLink)event.getComponent();
        Map<String, String> params = link.getParameterMap();
        
        String id = params.get("id");
        NodeRef documentNodeRef = new NodeRef(Repository.getStoreRef(), id);
        
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("document", new ScriptNode(documentNodeRef, this.services));
        
        // Add the parameters to the model passed to the script
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            model.put(entry.getKey(), entry.getValue());
        }
        
        // Determine whether the script is a node reference of a path
        String script = params.get("script");
        NodeRef nodeRef = new NodeRef(script);
        this.services.getScriptService().executeScript(nodeRef, ContentModel.PROP_CONTENT, model);      
        
        FacesContext context = FacesContext.getCurrentInstance();
        BrowseBean browseBean = (BrowseBean)FacesHelper.getManagedBean(context, "BrowseBean");
        String actionLocation = params.get("actionLocation");
        
        if (actionLocation.equals("document-details") == true)
        {
            browseBean.getDocument().reset();
            UIComponent comp = context.getViewRoot().findComponent("dialog:dialog-body:document-props");
            comp.getChildren().clear();
        }
        else if (actionLocation.equals("folder-details") == true)
        {
            browseBean.getActionSpace().reset();
            UIComponent comp = context.getViewRoot().findComponent("dialog:dialog-body:space-props");
            comp.getChildren().clear();
        }  
    }
}
