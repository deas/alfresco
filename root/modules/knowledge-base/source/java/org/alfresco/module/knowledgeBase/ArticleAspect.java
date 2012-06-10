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
package org.alfresco.module.knowledgeBase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.copy.CopyBehaviourCallback;
import org.alfresco.repo.copy.CopyDetails;
import org.alfresco.repo.copy.CopyServicePolicies;
import org.alfresco.repo.copy.DoNothingCopyBehaviourCallback;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.OwnableService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * Article aspect behaviour
 * 
 * @author Roy Wetherall
 */
public class ArticleAspect implements KbModel, 
                                      CopyServicePolicies.OnCopyNodePolicy,
                                      NodeServicePolicies.OnUpdatePropertiesPolicy,
                                      NodeServicePolicies.OnAddAspectPolicy,
                                      NodeServicePolicies.OnCreateChildAssociationPolicy,
                                      ContentServicePolicies.OnContentUpdatePolicy,
                                      NodeServicePolicies.OnCreateNodePolicy
{
    /** Policy component */
    private PolicyComponent policyComponent;
    
    /** Node service */
    private NodeService nodeService;
    
    /** Content service */
    private ContentService contentService;
    
    /** Action service */
    private ActionService actionService;
    
    /** Dictionary service */
    private DictionaryService dictionaryService;
    
    /** Permission service */
    private PermissionService permissionService;
    
    /** Ownable service */
    private OwnableService ownableService;
    
    /** Authentication service */
    private AuthenticationService authenticationService;
    
    /**
     * Sets the policy component
     * 
     * @param policyComponent   the policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * Set the node service 
     * 
     * @param nodeService   the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Set the content service
     * 
     * @param contentService    the content service
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    /**
     * Set the action service
     * 
     * @param actionService     the action service
     */
    public void setActionService(ActionService actionService)
    {
        this.actionService = actionService;
    }
    
    /**
     * Set the dictionary service
     * 
     * @param dictionaryService     the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    /**
     * Set the permission service
     * 
     * @param permissionService     the permission service
     */
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }
    
    /**
     * Set the ownable service
     * 
     * @param ownableService    the ownable service
     */
    public void setOwnableService(OwnableService ownableService)
    {
        this.ownableService = ownableService;
    }
    
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }
    
    /**
     * Initialisation method.  Registers the various behaviours for the aspect.
     */
    public void init()
    {

        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCopyNode"),
                ASPECT_ARTICLE,
                new JavaBehaviour(this, "onCopyNode"));
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
                ASPECT_ARTICLE,
                new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"),
                ASPECT_ARTICLE,
                new JavaBehaviour(this, "onAddAspect", NotificationFrequency.FIRST_EVENT));
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onContentUpdate"),
                ASPECT_ARTICLE,
                new JavaBehaviour(this, "onContentUpdate", NotificationFrequency.TRANSACTION_COMMIT)); 
        this.policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"),
                ContentModel.TYPE_FOLDER,
                ContentModel.ASSOC_CONTAINS,
                new JavaBehaviour(this, "onCreateChildAssociation", NotificationFrequency.FIRST_EVENT));
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                TYPE_KNOWLEDGE_BASE,
                new JavaBehaviour(this, "onCreateNode", NotificationFrequency.FIRST_EVENT));
    }
    
    
    /**
     * The article aspect should not be copied.
     * 
     * @see org.alfresco.repo.copy.CopyServicePolicies.OnCopyNodePolicy#getCopyCallback(org.alfresco.service.namespace.QName, org.alfresco.repo.copy.CopyDetails)
     */
    @Override
    public CopyBehaviourCallback getCopyCallback(QName classRef, CopyDetails copyDetails)
    {
        // Do nothing since we do not want to copy the article aspect
        return new DoNothingCopyBehaviourCallback();
    }

    /**
     * @see org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy#onUpdateProperties(org.alfresco.service.cmr.repository.NodeRef, java.util.Map, java.util.Map)
     */
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after)
    {
        if (this.nodeService.exists(nodeRef) == true && 
            this.nodeService.hasAspect(nodeRef, ASPECT_ARTICLE) == true &&
            this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY) == false)
        {
            String beforeStatus = (String)before.get(PROP_STATUS).toString();
            String afterStatus = (String)after.get(PROP_STATUS).toString();
            if (beforeStatus.equals(afterStatus) == false && STATUS_PUBLISHED.toString().equals(afterStatus) == true)
            {
                updatePublishedArticle(nodeRef);
            }
            
            String beforeVisibility = (String)before.get(PROP_VISIBILITY).toString();
            String afterVisibility = (String)after.get(PROP_VISIBILITY).toString();
            if (afterVisibility.equals(beforeVisibility) == false)
            {
                updateArticlePermissions(nodeRef);
            }
        }        
    }    
    
    /*
     * OnCreate KnowledgeBase node behaviour.
     * 
     * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy#onCreateNode(org.alfresco.service.cmr.repository.ChildAssociationRef)
     */
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        // Get the newly created knowledge base node reference
        NodeRef knowledgeBase = childAssocRef.getChildRef();
        
        // Set the permissions for the groups     
        this.permissionService.setPermission(knowledgeBase, GROUP_INTERNAL, PermissionService.COORDINATOR, true);
        this.permissionService.setPermission(knowledgeBase, GROUP_TIER_1, PermissionService.CONTRIBUTOR, true);  
    }

    /**
     * @see org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy#onAddAspect(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
     */
    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        if (this.nodeService.exists(nodeRef) == true)
        {
            NodeRef kb = getKnowledgeBase(nodeRef);
            
            // Get the article count
            Action counterAction = this.actionService.createAction("counter");
            this.actionService.executeAction(counterAction, kb);
            String id = this.nodeService.getProperty(kb, ContentModel.PROP_COUNTER).toString();
               
            // Set the kb id
            this.nodeService.setProperty(nodeRef, PROP_KB_ID, pad(id, 4));            

            // Give coordinator permissions to the internal group
            this.permissionService.setPermission(nodeRef, GROUP_INTERNAL, PermissionService.COORDINATOR, true);
            
            // Set the article permissions
            updateArticlePermissions(nodeRef);
        }        
    }
    
    /**
     * Pads a given string to a given length
     * 
     * @param  s        the string
     * @param  len      the required length
     * @return String   the padded string  
     */
    private String pad(String s, int len)
    {
       String result = s;
       for (int i=0; i<(len - s.length()); i++)
       {
           result = "0" + result;
       }
       return result;
    }

    /**
     * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy#onCreateChildAssociation(org.alfresco.service.cmr.repository.ChildAssociationRef, boolean)
     */
    public void onCreateChildAssociation(ChildAssociationRef childAssocRef, boolean isNewNode)
    {
        NodeRef nodeRef = childAssocRef.getChildRef();   
        if (this.nodeService.exists(nodeRef) == true && 
            this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY) == false)
        {
            QName className = this.nodeService.getType(nodeRef);
            if (this.dictionaryService.isSubClass(className, ContentModel.TYPE_CONTENT) == true)
            {
                NodeRef kb = getKnowledgeBase(nodeRef);
                
                if (kb != null)
                {   
                    // For some reason we need to set the owner by hand
                    String userName = this.authenticationService.getCurrentUserName();
                    this.ownableService.setOwner(nodeRef, userName);
                    
                    // Link the article to the relevant knowledge base
                    this.nodeService.createAssociation(nodeRef, kb, ASSOC_KNOWLEDGE_BASE);
                    
                    // Apply the article aspect
                    this.nodeService.addAspect(nodeRef, ASPECT_ARTICLE, null);                    
                }
            }
        }
    }

    /**
     *  @see org.alfresco.repo.content.ContentServicePolicies.OnContentUpdatePolicy#onContentUpdate(org.alfresco.service.cmr.repository.NodeRef, boolean)
     */
    public void onContentUpdate(NodeRef nodeRef, boolean newContent)
    {
        if (this.nodeService.exists(nodeRef) == true && 
            this.nodeService.hasAspect(nodeRef, ASPECT_ARTICLE) == true &&
            this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY) == false)
        {
            String status = this.nodeService.getProperty(nodeRef, PROP_STATUS).toString();
            if (STATUS_PUBLISHED.toString().equals(status) == true)
            {
                updatePublishedArticle(nodeRef);
            }            
        }        
    }
    
    /**
     * Updates the SWF of a published article
     * 
     * @param article   the article
     */
    private void updatePublishedArticle(NodeRef article)
    {
        // See if a rendition of the article already exists or not
        NodeRef rendition = null;
        List<ChildAssociationRef> children = this.nodeService.getChildAssocs(article, ASSOC_PUBLISHED, RegexQNamePattern.MATCH_ALL);
        if (children.size() == 1)
        {
            rendition = children.get(0).getChildRef();
        }
        
        if (rendition == null)
        {
            // Create the rendition
            String articleName = getRenditionName((String)this.nodeService.getProperty(article, ContentModel.PROP_NAME));
            Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
            props.put(ContentModel.PROP_NAME, articleName);                
            rendition = this.nodeService.createNode(
                                            article, 
                                            ASSOC_PUBLISHED, 
                                            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, articleName), 
                                            ContentModel.TYPE_CONTENT, 
                                            props).getChildRef();
        }
        
        // Transform the article
        ContentReader reader = this.contentService.getReader(article, ContentModel.PROP_CONTENT);
        if (reader != null)
        {
            ContentWriter writer = this.contentService.getWriter(rendition, ContentModel.PROP_CONTENT, true);
            writer.setMimetype(MimetypeMap.MIMETYPE_FLASH);
            writer.setEncoding("UTF-8");
            this.contentService.transform(reader, writer);
        }              
    }
    
    /**
     * Update the articles permissions based on the value of the visibility property
     * 
     * @param article   the article node reference
     */
    private void updateArticlePermissions(NodeRef article)
    {
        NodeRef visibility = (NodeRef)this.nodeService.getProperty(article, PROP_VISIBILITY);
        if (visibility.equals(VISIBILITY_INTERNAL) == true)
        {
            // Clear tier one and two
            this.permissionService.clearPermission(article, GROUP_TIER_1);
            this.permissionService.clearPermission(article, GROUP_TIER_2);
            
            // Ensure inherted permissions are removed    
            this.permissionService.setInheritParentPermissions(article, false);
        }
        else if (visibility.equals(VISIBILITY_TIER_1) == true)
        {
            // Clear tier two
            this.permissionService.clearPermission(article, GROUP_TIER_2);
            
            // Add tier one
            this.permissionService.setPermission(article, GROUP_TIER_1, PermissionService.CONSUMER, true);
            
            // Ensure inherted permissions are removed    
            this.permissionService.setInheritParentPermissions(article, false);
        }
        else if (visibility.equals(VISIBILITY_TIER_2) == true)
        {
            // Add tier one and two
            this.permissionService.setPermission(article, GROUP_TIER_1, PermissionService.CONSUMER, true);
            this.permissionService.setPermission(article, GROUP_TIER_2, PermissionService.CONSUMER, true);
            
            // Ensure inherted permissions are removed    
            this.permissionService.setInheritParentPermissions(article, false);
        }
        else if (visibility.equals(VISIBILITY_TIER_3) == true)
        {
            // Add tier one and two
            this.permissionService.setPermission(article, GROUP_TIER_1, PermissionService.CONSUMER, true);
            this.permissionService.setPermission(article, GROUP_TIER_2, PermissionService.CONSUMER, true);
            
            // Ensure inherted permissions are included  
            this.permissionService.setInheritParentPermissions(article, true);            
        }
    }
    
    /**
     * Get the name of the rendition
     * 
     * @param original  the origional article name
     * @return String   the name of the rendition
     */
    private String getRenditionName(String original)
    {
        // get the current extension
        int dotIndex = original.lastIndexOf('.');
        StringBuilder sb = new StringBuilder(original.length());
        if (dotIndex > -1)
        {
            // add the new extension
            sb.append(original.substring(0, dotIndex));            
            sb.append('.').append("swf");
        }
        else
        {
            // no extension so dont add a new one
            sb.append(original);
            sb.append('.').append("swf");
        }

        return sb.toString();
    }   
    
    /**
     * Get the knowledge base
     * 
     * @param nodeRef   the node reference
     * @return NodeRef  the related knowledge base reference, null if none
     */
    private NodeRef getKnowledgeBase(NodeRef nodeRef)
    {
       NodeRef result = null;
       
       if (this.nodeService.hasAspect(nodeRef, KbModel.ASPECT_ARTICLE) == true)
       {
           // Get the knowledge base node from the association
           List<AssociationRef> assocs = this.nodeService.getTargetAssocs(nodeRef, ASSOC_KNOWLEDGE_BASE);
           if (assocs.size() == 1)
           {
               result = assocs.get(0).getTargetRef();
           }
       }
       
       if (result == null)
       {
          result = findKnowledgeBase(nodeRef);
       }
       
       return result;
    }

    /**
     * Traverse up the node hierarchy to find the containing knowledge base, null if none
     * 
     * @param nodeRef   the node reference
     * @return NodeRef  the knowledge base node reference, null if none
     */
    private NodeRef findKnowledgeBase(NodeRef nodeRef)
    {
       NodeRef result = null;
       
       ChildAssociationRef parentAssocRef = this.nodeService.getPrimaryParent(nodeRef);
       
       if (parentAssocRef != null)
       {
           NodeRef parent = parentAssocRef.getParentRef();
           if (parent != null)
           {           
               if (TYPE_KNOWLEDGE_BASE.equals(this.nodeService.getType(parent)) == true)
               {
                   result = parent;
               }
               else
               {
                   result = findKnowledgeBase(parent);
               }
           }
       }
       
       return result;
    }
}
