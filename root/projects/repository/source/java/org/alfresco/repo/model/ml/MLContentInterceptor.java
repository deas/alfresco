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
package org.alfresco.repo.model.ml;

import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.ml.MultilingualContentService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Replaces content readers according to the empty translation status.
 * 
 * @see ContentService#getReader(NodeRef, QName)
 * @see FileFolderService#getReader(NodeRef)
 * @since 2.1
 * @author Derek Hulley
 */
public class MLContentInterceptor implements MethodInterceptor
{
    private static Log logger = LogFactory.getLog(MLContentInterceptor.class);
    
    /** Direct access to the NodeService */
    private NodeService nodeService;
    /** Direct access to the ContentService */
    private ContentService contentService;
    /** Direct access to the ML Content Service */
    private MultilingualContentService multilingualContentService;
    
    public void setNodeService(NodeService bean)
    {
        this.nodeService = bean;
    }
    
    public void setContentService(ContentService directContentService)
    {
        this.contentService = directContentService;
    }

    public void setMultilingualContentService(MultilingualContentService directMultilingualContentService)
    {
        this.multilingualContentService = directMultilingualContentService;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        String methodName = invocation.getMethod().getName();

        Object ret = null;
        
        if (methodName.equals("getReader"))
        {
            Object[] args = invocation.getArguments();
            
            NodeRef nodeRef = (NodeRef) args[0];
            
            // Shortcut it if the node is not an empty translation
            Set<QName> aspects = nodeService.getAspects(nodeRef);
            if (!aspects.contains(ContentModel.ASPECT_MULTILINGUAL_DOCUMENT) ||
                !aspects.contains(ContentModel.ASPECT_MULTILINGUAL_EMPTY_TRANSLATION))
            {
                // It is not a ML document or we expect that there is no translation
                return invocation.proceed();
            }
            
            // Get the content property required
            QName propertyQName = null;
            if (args.length == 1)
            {
                // Assume that the default cm:content is required
                propertyQName = ContentModel.PROP_CONTENT;
            }
            else
            {
                // The request is specific
                propertyQName = (QName) args[1];
            }
            // Get the pivot translation
            NodeRef pivotNodeRef = multilingualContentService.getPivotTranslation(nodeRef);
            if (pivotNodeRef == null)
            {
                // This is technically possible
                ret = invocation.proceed();
            }
            else
            {
                // Get the reader from that
                ContentReader pivotContentReader = contentService.getReader(pivotNodeRef, propertyQName);
                // Done
                if (logger.isDebugEnabled())
                {
                    logger.debug(
                            "Converted reader for empty translation: \n" +
                            "   Empty Translation: " + nodeRef + "\n" +
                            "   Pivot Translation: " + pivotNodeRef + "\n" +
                            "   Reader:            " + pivotContentReader);
                }
                ret = pivotContentReader;
            }
        }
        else
        {
            ret = invocation.proceed();
        }
        // done
        return ret;
    }
}
