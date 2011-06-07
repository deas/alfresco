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
package org.alfresco.module.org_alfresco_module_dod5015.action.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.model.RenditionModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RMDispositionActionExecuterAbstractBase;
import org.alfresco.module.org_alfresco_module_dod5015.model.DOD5015Model;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.InitializingBean;

/**
 * Destroy action
 * 
 * @author Roy Wetherall
 */
public class DestroyAction extends RMDispositionActionExecuterAbstractBase implements
        ContentServicePolicies.OnContentUpdatePolicy, InitializingBean
{
    private PolicyComponent policyComponent;
    private boolean ghostingEnabled = true;

    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    public void setGhostingEnabled(boolean ghostingEnabled)
    {
        this.ghostingEnabled = ghostingEnabled;
    }

    @Override
    protected void executeRecordFolderLevelDisposition(Action action, NodeRef recordFolder)
    {
        if (ghostingEnabled)
        {
            nodeService.addAspect(recordFolder, DOD5015Model.ASPECT_GHOSTED, Collections.<QName, Serializable> emptyMap());  
        }
        else
        {
            nodeService.deleteNode(recordFolder);
        }
        
        List<NodeRef> records = this.recordsManagementService.getRecords(recordFolder);
        for (NodeRef record : records)
        {
            executeRecordLevelDisposition(action, record);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void executeRecordLevelDisposition(Action action, NodeRef record)
    {
        // Do ghosting, if it is enabled
        if (this.ghostingEnabled)
        {
            // First purge (synchronously) all content properties
            Set<QName> props = this.nodeService.getProperties(record).keySet();
            props.retainAll(this.dictionaryService.getAllProperties(DataTypeDefinition.CONTENT));
            for (QName prop : props)
            {
                this.nodeService.removeProperty(record, prop);
            }

            // Remove the renditioned aspect (and its properties and associations) if it is present.
            //
            // From Alfresco 3.3 it is the rn:renditioned aspect which defines the
            // child-association being considered in this method.
            // Note also that the cm:thumbnailed aspect extends the rn:renditioned aspect.
            //
            // We want to remove the rn:renditioned aspect, but due to the possibility
            // that there is Alfresco 3.2-era data with the cm:thumbnailed aspect
            // applied, we must consider removing it too.
            if (this.nodeService.hasAspect(record, RenditionModel.ASPECT_RENDITIONED)
                    || this.nodeService.hasAspect(record, ContentModel.ASPECT_THUMBNAILED))
            {
                // Add the ghosted aspect to all the renditioned children, so that they will not be archived when the
                // renditioned aspect is removed
                Set<QName> childAssocTypes = this.dictionaryService.getAspect(RenditionModel.ASPECT_RENDITIONED)
                        .getChildAssociations().keySet();
                for (ChildAssociationRef child : this.nodeService.getChildAssocs(record))
                {
                    if (childAssocTypes.contains(child.getTypeQName()))
                    {
                        this.nodeService.addAspect(child.getChildRef(), DOD5015Model.ASPECT_GHOSTED, Collections
                                .<QName, Serializable> emptyMap());
                    }
                }
                if (this.nodeService.hasAspect(record, RenditionModel.ASPECT_RENDITIONED))
                {
                    this.nodeService.removeAspect(record, RenditionModel.ASPECT_RENDITIONED);
                }
                if (this.nodeService.hasAspect(record, ContentModel.ASPECT_THUMBNAILED))
                {
                    this.nodeService.removeAspect(record, ContentModel.ASPECT_THUMBNAILED);
                }
            }
            
            // Finally, add the ghosted aspect (TODO: Any properties?)
            this.nodeService.addAspect(record, DOD5015Model.ASPECT_GHOSTED, Collections.<QName, Serializable> emptyMap());          
        }
        else
        {
            this.nodeService.deleteNode(record);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.alfresco.repo.content.ContentServicePolicies.OnContentUpdatePolicy#onContentUpdate(org.alfresco.service.cmr
     * .repository.NodeRef, boolean)
     */
    public void onContentUpdate(NodeRef nodeRef, boolean newContent)
    {
        throw new AlfrescoRuntimeException("Update of content properties not allowed when node has "
                + DOD5015Model.ASPECT_GHOSTED + " aspect.");
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception
    {
        // Register interest in the onContentUpdate policy
        policyComponent.bindClassBehaviour(ContentServicePolicies.ON_CONTENT_UPDATE,
                DOD5015Model.ASPECT_GHOSTED, new JavaBehaviour(this, "onContentUpdate"));
    }
}
