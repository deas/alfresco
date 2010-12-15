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

import javax.faces.context.FacesContext;

import org.alfresco.model.ContentModel;
import org.alfresco.module.blogIntegration.BlogIntegrationModel;
import org.alfresco.module.blogIntegration.BlogIntegrationService;
import org.alfresco.module.blogIntegration.BlogIntegrationServiceImpl;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.web.action.evaluator.BaseActionEvaluator;
import org.alfresco.web.bean.repository.Node;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;


/**
 * Post blog evaluator
 * 
 * @author Roy Wetherall
 */
public class PostBlogEvaluator extends BaseActionEvaluator implements BlogIntegrationModel
{
    /**
     * @see org.alfresco.web.action.ActionEvaluator#evaluate(org.alfresco.web.bean.repository.Node)
     */
    public boolean evaluate(Node node)
    {
        boolean result = false;
        
        // Get the conten service and the blog integration service
        WebApplicationContext applicationContext = FacesContextUtils.getRequiredWebApplicationContext(FacesContext.getCurrentInstance());
        ContentService contentService = (ContentService)applicationContext.getBean("ContentService");
        BlogIntegrationService blogIntegrationService = (BlogIntegrationService)applicationContext.getBean("BlogIntegrationService");
                
        // Check the mimetype of the content 
        ContentReader contentReader = contentService.getReader(node.getNodeRef(), ContentModel.PROP_CONTENT);
        if (contentReader != null)
        {
            String mimetype = contentReader.getMimetype();
            if (node.hasAspect(ASPECT_BLOG_POST) == false &&
                BlogIntegrationServiceImpl.supportedMimetypes.contains(mimetype) == true &&
                blogIntegrationService.getBlogDetails(node.getNodeRef()).size() != 0)
            {
                result = true;
            }
        }
        return result;
    }
}
