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
package org.alfresco.module.org_alfresco_module_dod5015;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.BeforeCreateReference;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.BeforeRemoveReference;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.OnCreateReference;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementPolicies.OnRemoveReference;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMListOfValuesConstraint;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMListOfValuesConstraint.MatchLogic;
import org.alfresco.module.org_alfresco_module_dod5015.model.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.model.RecordsManagementCustomModel;
import org.alfresco.module.org_alfresco_module_dod5015.model.RecordsManagementModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.DictionaryRepositoryBootstrap;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Association;
import org.alfresco.repo.dictionary.M2ChildAssociation;
import org.alfresco.repo.dictionary.M2ClassAssociation;
import org.alfresco.repo.dictionary.M2Constraint;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.repo.dictionary.M2Property;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.ParameterCheck;

/**
 * Records Management AdminService Implementation.
 * 
 * @author Neil McErlean, janv
 */
public class RecordsManagementAdminServiceImpl implements RecordsManagementAdminService, 
														  RecordsManagementCustomModel,
														  NodeServicePolicies.OnAddAspectPolicy,
														  NodeServicePolicies.OnRemoveAspectPolicy,
														  NodeServicePolicies.OnCreateNodePolicy
{
    /** Logger */
    private static Log logger = LogFactory.getLog(RecordsManagementAdminServiceImpl.class);
    
    /** Constants */
    public static final String RMC_CUSTOM_ASSOCS = RecordsManagementCustomModel.RM_CUSTOM_PREFIX + ":customAssocs";    
    private static final String CUSTOM_CONSTRAINT_TYPE = org.alfresco.module.org_alfresco_module_dod5015.caveat.RMListOfValuesConstraint.class.getName();    
    private static final NodeRef RM_CUSTOM_MODEL_NODE_REF = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "records_management_custom_model");    
    private static final String PARAM_ALLOWED_VALUES = "allowedValues";
    private static final String PARAM_CASE_SENSITIVE = "caseSensitive";
    private static final String PARAM_MATCH_LOGIC = "matchLogic";    
    public static final String RMA_RECORD = "rma:record";    
    private static final String SOURCE_TARGET_ID_SEPARATOR = "__";
    
    /** Dictionary service */
    private DictionaryService dictionaryService;
    
    /** Namespace service */ 
    private NamespaceService namespaceService;
    
    /** Node service */
    private NodeService nodeService;
    
    /** Content service */
    private ContentService contentService;

    /** Dictionary repository bootstrap */
    private DictionaryRepositoryBootstrap dictonaryRepositoryBootstrap;

    /** Policy component */
    private PolicyComponent policyComponent;
    
    /** Policy delegates */
    private ClassPolicyDelegate<BeforeCreateReference> beforeCreateReferenceDelegate;
    private ClassPolicyDelegate<OnCreateReference> onCreateReferenceDelegate;    
    private ClassPolicyDelegate<BeforeRemoveReference> beforeRemoveReferenceDelegate;
    private ClassPolicyDelegate<OnRemoveReference> onRemoveReferenceDelegate;
    
    /** List of types that can be customisable */
    private List<QName> pendingCustomisableTypes;
    private Map<QName, QName> customisableTypes;
    
    /**
     * @param dictionaryService     the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
		this.dictionaryService = dictionaryService;
	}

    /**
     * @param namespaceService      the namespace service
     */
	public void setNamespaceService(NamespaceService namespaceService)
	{
		this.namespaceService = namespaceService;
	}

	/**
	 * @param nodeService      the node service
	 */
	public void setNodeService(NodeService nodeService)
	{
		this.nodeService = nodeService;
	}
	
	/**
	 * @param contentService	the content service
	 */
	public void setContentService(ContentService contentService)
	{
	    this.contentService = contentService;
	}
	
	/**
	 * @param policyComponent  the policy component
	 */
	public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * Bootstrap for standard (non-RMC) dynamic models
     * 
     * @param dictonaryRepositoryBootstrap	dictionary repository bootstrap
     */
	public void setDictionaryRepositoryBootstrap(DictionaryRepositoryBootstrap dictonaryRepositoryBootstrap)
    {
        this.dictonaryRepositoryBootstrap = dictonaryRepositoryBootstrap;
    }    
    
	/**
	 * Initialisation method
	 */
	public void init()
    {
        // Register the various policies
        beforeCreateReferenceDelegate = policyComponent.registerClassPolicy(BeforeCreateReference.class);
        onCreateReferenceDelegate = policyComponent.registerClassPolicy(OnCreateReference.class);
        beforeRemoveReferenceDelegate = policyComponent.registerClassPolicy(BeforeRemoveReference.class);
        onRemoveReferenceDelegate = policyComponent.registerClassPolicy(OnRemoveReference.class);
    }
	
    protected void invokeBeforeCreateReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference)
    {
        // get qnames to invoke against
        Set<QName> qnames = RecordsManagementPoliciesUtil.getTypeAndAspectQNames(nodeService, fromNodeRef);
        // execute policy for node type and aspects
        BeforeCreateReference policy = beforeCreateReferenceDelegate.get(qnames);
        policy.beforeCreateReference(fromNodeRef, toNodeRef, reference);
    }
    
    protected void invokeOnCreateReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference)
    {
        // get qnames to invoke against
        Set<QName> qnames = RecordsManagementPoliciesUtil.getTypeAndAspectQNames(nodeService, fromNodeRef);
        // execute policy for node type and aspects
        OnCreateReference policy = onCreateReferenceDelegate.get(qnames);
        policy.onCreateReference(fromNodeRef, toNodeRef, reference);
    }
    
    protected void invokeBeforeRemoveReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference)
    {
        // get qnames to invoke against
        Set<QName> qnames = RecordsManagementPoliciesUtil.getTypeAndAspectQNames(nodeService, fromNodeRef);
        // execute policy for node type and aspects
        BeforeRemoveReference policy = beforeRemoveReferenceDelegate.get(qnames);
        policy.beforeRemoveReference(fromNodeRef, toNodeRef, reference);
    }
    
    protected void invokeOnRemoveReference(NodeRef fromNodeRef, NodeRef toNodeRef, QName reference)
    {
        // get qnames to invoke against
        Set<QName> qnames = RecordsManagementPoliciesUtil.getTypeAndAspectQNames(nodeService, fromNodeRef);
        // execute policy for node type and aspects
        OnRemoveReference policy = onRemoveReferenceDelegate.get(qnames);
        policy.onRemoveReference(fromNodeRef, toNodeRef, reference);
    }

    @Override
    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        if (nodeService.exists(nodeRef) == true &&
            isCustomisable(aspectTypeQName) == true)
        {
            QName customPropertyAspect = getCustomAspect(aspectTypeQName);
            nodeService.addAspect(nodeRef, customPropertyAspect, null);
        }
    }

    @Override
    public void onRemoveAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        if (nodeService.exists(nodeRef) == true &&
            isCustomisable(aspectTypeQName) == true)
        {
            QName customPropertyAspect = getCustomAspect(aspectTypeQName);
            nodeService.removeAspect(nodeRef, customPropertyAspect);  
        }
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        NodeRef nodeRef = childAssocRef.getChildRef();
        QName type = nodeService.getType(nodeRef);
        while (type != null && ContentModel.TYPE_CMOBJECT.equals(type) == false)
        {
            if (isCustomisable(type) == true)
            {
                QName customPropertyAspect = getCustomAspect(type);
                nodeService.addAspect(nodeRef, customPropertyAspect, null);  
            }
            
            TypeDefinition def = dictionaryService.getType(type);
            if (def != null)
            {
                type = def.getParentName();
            }
            else
            {
                type = null;
            }
        }        
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#initialiseCustomModel()
     */
    public void initialiseCustomModel()
    {        
        // Bind class behaviours
        policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnAddAspectPolicy.QNAME, 
                this, 
                new JavaBehaviour(this, "onAddAspect", NotificationFrequency.FIRST_EVENT));
        policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnRemoveAspectPolicy.QNAME, 
                this, 
                new JavaBehaviour(this, "onRemoveAspect", NotificationFrequency.FIRST_EVENT));
        policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnCreateNodePolicy.QNAME, 
                this, 
                new JavaBehaviour(this, "onCreateNode", NotificationFrequency.FIRST_EVENT));
        
    	// Make sure each customisable type has a corresponding aspect 
    	for (QName customisableType : pendingCustomisableTypes) 
    	{
    		QName customAspect = getCustomAspect(customisableType);
    		if (dictionaryService.getAspect(customAspect) == null)
    		{
	    		NodeRef modelRef = getCustomModelRef(customAspect.getNamespaceURI());
		        M2Model model = readCustomContentModel(modelRef);
		        try
		        {
		        	// Create the new aspect to hold the custom properties
		        	M2Aspect aspect = model.createAspect(customAspect.toPrefixString(namespaceService));
		        	aspect.setDescription(customisableType.toPrefixString(namespaceService));
		        }
		        finally
		        {
		        	writeCustomContentModel(modelRef, model);
		        }
    		}
		}
    }
    
    /**
     * @param customisableTypes		list of string representations of the type qnames that are customisable
     */
    public void setCustomisableTypes(List<String> customisableTypes)
    {
    	pendingCustomisableTypes = new ArrayList<QName>();
    	for (String customisableType : customisableTypes) 
    	{
    		pendingCustomisableTypes.add(QName.createQName(customisableType, namespaceService));
		}
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#getCustomisable()
     */
    public Set<QName> getCustomisable()
    {
    	return getCustomisableMap().keySet();    	
    }
    
    /**
     * 
     * @return
     */
    private Map<QName, QName> getCustomisableMap()
    {
    	if (customisableTypes == null)
    	{
    		customisableTypes = new HashMap<QName, QName>(7);
	    	Collection<QName> aspects = dictionaryService.getAspects(RM_CUSTOM_MODEL);
	    	for (QName aspect : aspects) 
	    	{
	    		AspectDefinition aspectDef = dictionaryService.getAspect(aspect);
	    		String name = aspectDef.getName().getLocalName();
	    		if (name.endsWith("Properties") == true)
	    		{
	    			QName type = null;
	    			String prefixString = aspectDef.getDescription();
	    			if (prefixString == null)
	    			{
	    				// Backward compatibility from previous RM custom models
	    				if ("customRecordProperties".equals(name) == true)
	    				{
	    					type = RecordsManagementModel.ASPECT_RECORD;
	    				}
	    				else if ("customRecordFolderProperties".equals(name) == true)
	    				{
	    					type = RecordsManagementModel.TYPE_RECORD_FOLDER;
	    				}
	    				else if ("customRecordSeriesProperties".equals(name) == true)
	    				{
	    					type = DOD5015Model.TYPE_RECORD_SERIES;
	    				}
	    				else if ("customRecordCategoryProperties".equals(name) == true)
	    				{
	    					type = DOD5015Model.TYPE_RECORD_CATEGORY;
	    				}
	    			}
	    			else
	    			{
	    				type = QName.createQName(prefixString, namespaceService);
	    			}
	    			customisableTypes.put(type, aspect);
	    		}
			}
    	}
    	return customisableTypes;    	
    }
    


    /**
     * Gets the QName of the custom aspect given the customisable type QName
     * 
     * @param customisableType	
     * @return
     */
    private QName getCustomAspect(QName customisableType)
    {
    	Map<QName, QName> map = getCustomisableMap();
    	QName result = map.get(customisableType);
    	if (result == null)
    	{
    		String localName = customisableType.toPrefixString(namespaceService).replace(":", "");
    		localName = MessageFormat.format("{0}CustomProperties", localName);
    	    result = QName.createQName(RM_CUSTOM_URI, localName);
    	}
    	return result;
    }
    
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#isCustomisable(org.alfresco.service.namespace.QName)
     */
    @Override
    public boolean isCustomisable(QName type) 
    {
    	ParameterCheck.mandatory("type", type);    	
    	return getCustomisable().contains(type);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#makeCustomisable(org.alfresco.service.namespace.QName)
     */
    @Override
    public void makeCustomisable(QName type) 
    {
    	ParameterCheck.mandatory("type", type);
    	
    	if (customisableTypes == null)
    	{
    		// Add the type to the pending list
    		pendingCustomisableTypes.add(type);
    	}
    	else
    	{    	
	    	QName customAspect = getCustomAspect(type);
			if (dictionaryService.getAspect(customAspect) == null)
			{
		    	NodeRef modelRef = getCustomModelRef(customAspect.getNamespaceURI());
		        M2Model model = readCustomContentModel(modelRef);
		        try
		        {
		        	// Create the new aspect to hold the custom properties
		        	M2Aspect aspect = model.createAspect(customAspect.toPrefixString(namespaceService));
		        	aspect.setDescription(type.toPrefixString(namespaceService));
		        }
		        finally
		        {
		        	writeCustomContentModel(modelRef, model);
		        }
		        customisableTypes.put(type, customAspect);
			}
    	}
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#unmakeCustomisable(org.alfresco.service.namespace.QName)
     */
    @Override
    public void unmakeCustomisable(QName type) 
    {
    	ParameterCheck.mandatory("type", type);
    	
    	if (customisableTypes == null)
    	{
    		throw new AlfrescoRuntimeException("Service not initialised.");
    	}
    	
    	QName customAspect = getCustomAspect(type);
		if (dictionaryService.getAspect(customAspect) != null)
		{
			// TODO need to confirm that the custom properties are not being used!
			
	    	NodeRef modelRef = getCustomModelRef(customAspect.getNamespaceURI());
	        M2Model model = readCustomContentModel(modelRef);
	        try
	        {
	        	// Create the new aspect to hold the custom properties
	        	model.removeAspect(customAspect.toPrefixString(namespaceService));
	        }
	        finally
	        {
	        	writeCustomContentModel(modelRef, model);
	        }
	        customisableTypes.remove(type);
		}
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#existsCustomProperty(org.alfresco.service.namespace.QName)
     */
    @Override
    public boolean existsCustomProperty(QName propertyName) 
    {
    	ParameterCheck.mandatory("propertyName", propertyName);
    	
    	boolean result = false;
    	if (RM_CUSTOM_URI.equals(propertyName.getNamespaceURI()) == true &&
    	    dictionaryService.getProperty(propertyName) != null)
    	{
    		result = true;
    	}
    	return result;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#getCustomPropertyDefinitions()
     */
    public Map<QName, PropertyDefinition> getCustomPropertyDefinitions()
    {
    	Map<QName, PropertyDefinition> result = new HashMap<QName, PropertyDefinition>();
    	for (QName customisableType : getCustomisable()) 
    	{
    		result.putAll(getCustomPropertyDefinitions(customisableType));
		}
        return result;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#getCustomPropertyDefinitions(org.alfresco.module.org_alfresco_module_dod5015.CustomisableRmElement)
     */
    public Map<QName, PropertyDefinition> getCustomPropertyDefinitions(QName customisableType)
    {
		QName relevantAspectQName = getCustomAspect(customisableType);
        AspectDefinition aspectDefn = dictionaryService.getAspect(relevantAspectQName);
        Map<QName, PropertyDefinition> propDefns = aspectDefn.getProperties();

        return propDefns;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#addCustomPropertyDefinition(org.alfresco.service.namespace.QName, org.alfresco.service.namespace.QName, java.lang.String, org.alfresco.service.namespace.QName, java.lang.String, java.lang.String)
     */
    public QName addCustomPropertyDefinition(QName propId, QName aspectName, String label, QName dataType, String title, String description)
    {
        return addCustomPropertyDefinition(propId, aspectName, label, dataType, title, description, null, false, false, false, null);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#addCustomPropertyDefinition(org.alfresco.service.namespace.QName, org.alfresco.service.namespace.QName, java.lang.String, org.alfresco.service.namespace.QName, java.lang.String, java.lang.String, java.lang.String, boolean, boolean, boolean, org.alfresco.service.namespace.QName)
     */
    public QName addCustomPropertyDefinition(QName propId, QName aspectName, String label, QName dataType, String title, String description, String defaultValue, boolean multiValued, boolean mandatory, boolean isProtected, QName lovConstraint)
    {
    	if (isCustomisable(aspectName) == false)
    	{
    		throw new AlfrescoRuntimeException("The type/aspect " + aspectName.toPrefixString(namespaceService) + " is not customisable.");
    	}
    	
        // title parameter is currently ignored. Intentionally.
        if (propId == null)
        {
            // Generate a propId
            propId = this.generateQNameFor(label);
        }
        
        ParameterCheck.mandatory("aspectName", aspectName);
        ParameterCheck.mandatory("label", label);
        ParameterCheck.mandatory("dataType", dataType);
        
        NodeRef modelRef = getCustomModelRef(propId.getNamespaceURI());
        M2Model deserializedModel = readCustomContentModel(modelRef);
        
        QName customAspect = getCustomAspect(aspectName);
        M2Aspect customPropsAspect = deserializedModel.getAspect(customAspect.toPrefixString(namespaceService));
        
        if (customPropsAspect == null)
        {
            throw new AlfrescoRuntimeException("Unknown custom aspect: " + customAspect + " for aspect " + aspectName.toPrefixString(namespaceService));
        }
        
        String propIdAsString = propId.toPrefixString(namespaceService);
        M2Property customProp = customPropsAspect.getProperty(propIdAsString);
        if (customProp != null)
        {
            throw new AlfrescoRuntimeException("Property already exists: " + propIdAsString);
        }
        
        M2Property newProp = customPropsAspect.createProperty(propIdAsString);
        newProp.setName(propIdAsString);
        newProp.setType(dataType.toPrefixString(namespaceService));
        
        // Note that the title is used to store the RM 'label'.
        newProp.setTitle(label);
        newProp.setDescription(description);
        newProp.setDefaultValue(defaultValue);
        
        newProp.setMandatory(mandatory);
        newProp.setProtected(isProtected);
        newProp.setMultiValued(multiValued);
        
        newProp.setIndexed(true);
        newProp.setIndexedAtomically(true);
        newProp.setStoredInIndex(false);
        newProp.setIndexTokenisationMode(IndexTokenisationMode.FALSE);
        
        if (lovConstraint != null)
        {
            if (! dataType.equals(DataTypeDefinition.TEXT))
            {
                throw new AlfrescoRuntimeException("Cannot apply constraint '"+lovConstraint+"' to property '"+propIdAsString+"' with datatype '"+dataType+"' (expected: dataType = TEXT)");
            }
            
            String lovConstraintQNameAsString = lovConstraint.toPrefixString(namespaceService);
            newProp.addConstraintRef(lovConstraintQNameAsString);
        }
        
        writeCustomContentModel(modelRef, deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("addCustomPropertyDefinition: "+label+
                    "=" + propIdAsString + " to aspect: "+aspectName);
        }
        
        return propId;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#setCustomPropertyDefinitionLabel(org.alfresco.service.namespace.QName, java.lang.String)
     */
    public QName setCustomPropertyDefinitionLabel(QName propQName, String newLabel)
    {
        ParameterCheck.mandatory("propQName", propQName);
        
        PropertyDefinition propDefn = dictionaryService.getProperty(propQName);
        if (propDefn == null)
        {
            throw new AlfrescoRuntimeException("DictionaryService does not contain property definition " + propQName);
        }
        
        if (newLabel == null) return propQName;
        
        NodeRef modelRef = getCustomModelRef(propQName.getNamespaceURI());
        M2Model deserializedModel = readCustomContentModel(modelRef);
        
        M2Property targetProperty = findProperty(propQName, deserializedModel);

        targetProperty.setTitle(newLabel);
        writeCustomContentModel(modelRef, deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("setCustomPropertyDefinitionLabel: "+propQName+
                    "=" + newLabel);
        }
        
        return propQName;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#setCustomPropertyDefinitionConstraint(org.alfresco.service.namespace.QName, org.alfresco.service.namespace.QName)
     */
    public QName setCustomPropertyDefinitionConstraint(QName propQName, QName newLovConstraint)
    {
        ParameterCheck.mandatory("propQName", propQName);
        
        PropertyDefinition propDefn = dictionaryService.getProperty(propQName);
        if (propDefn == null)
        {
            throw new AlfrescoRuntimeException("DictionaryService does not contain property definition " + propQName);
        }
        
        NodeRef modelRef = getCustomModelRef(propQName.getNamespaceURI());
        M2Model deserializedModel = readCustomContentModel(modelRef);
        
        M2Property targetProp = findProperty(propQName, deserializedModel);
        String dataType = targetProp.getType();

        if (! dataType.equals(DataTypeDefinition.TEXT.toPrefixString(namespaceService)))
        {
            throw new AlfrescoRuntimeException("Cannot apply constraint '"+newLovConstraint+
                    "' to property '" + targetProp.getName() + "' with datatype '" + dataType + "' (expected: dataType = TEXT)");
        }
        String lovConstraintQNameAsString = newLovConstraint.toPrefixString(namespaceService);
        
        // Add the constraint - if it isn't already there.
        String refOfExistingConstraint = null;
        
        for (M2Constraint c : targetProp.getConstraints())
        {
            // There should only be one constraint.
            refOfExistingConstraint = c.getRef();
            break;
        }
        if (refOfExistingConstraint != null)
        {
            targetProp.removeConstraintRef(refOfExistingConstraint);
        }
        targetProp.addConstraintRef(lovConstraintQNameAsString);
        
        writeCustomContentModel(modelRef, deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("addCustomPropertyDefinitionConstraint: "+lovConstraintQNameAsString);
        }
        
        return propQName;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#removeCustomPropertyDefinitionConstraints(org.alfresco.service.namespace.QName)
     */
    public QName removeCustomPropertyDefinitionConstraints(QName propQName)
    {
        ParameterCheck.mandatory("propQName", propQName);
        
        PropertyDefinition propDefn = dictionaryService.getProperty(propQName);
        if (propDefn == null)
        {
            throw new AlfrescoRuntimeException("DictionaryService does not contain property definition " + propQName);
        }
        
        NodeRef modelRef = getCustomModelRef(propQName.getNamespaceURI());
        M2Model deserializedModel = readCustomContentModel(modelRef);
        
        M2Property targetProperty = findProperty(propQName, deserializedModel);
        
        // Need to count backwards to remove constraints
        for (int i = targetProperty.getConstraints().size() - 1; i >= 0; i--) {
            String ref = targetProperty.getConstraints().get(i).getRef();
            targetProperty.removeConstraintRef(ref);
        }
        
        writeCustomContentModel(modelRef, deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("removeCustomPropertyDefinitionConstraints: "+propQName);
        }
        
        return propQName;
    }

    /**
     * 
     * @param propQName
     * @param deserializedModel
     * @return
     */
    private M2Property findProperty(QName propQName, M2Model deserializedModel)
    {
        List<M2Aspect> aspects = deserializedModel.getAspects();
        // Search through the aspects looking for the custom property
        for (M2Aspect aspect : aspects)
        {
            for (M2Property prop : aspect.getProperties())
            {
                if (propQName.toPrefixString(namespaceService).equals(prop.getName()))
                {
                    return prop;
                }
            }
        }
        throw new AlfrescoRuntimeException("Custom model does not contain property definition " + propQName);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#removeCustomPropertyDefinition(org.alfresco.service.namespace.QName)
     */
    public void removeCustomPropertyDefinition(QName propQName)
    {
        ParameterCheck.mandatory("propQName", propQName);
        
        NodeRef modelRef = getCustomModelRef(propQName.getNamespaceURI());
        M2Model deserializedModel = readCustomContentModel(modelRef);
        
        String propQNameAsString = propQName.toPrefixString(namespaceService);
        
        String aspectName = null;
        
        boolean found = false;
        
        // Need to select the correct aspect in the customModel from which we'll
        // attempt to delete the property definition.
        for (QName customisableType : getCustomisable())
        {
        	aspectName = getCustomAspect(customisableType).toPrefixString(namespaceService);
            M2Aspect customPropsAspect = deserializedModel.getAspect(aspectName);
            
            if (customPropsAspect == null)
            {
                throw new AlfrescoRuntimeException("Unknown aspect: "+aspectName);
            }
            
            M2Property prop = customPropsAspect.getProperty(propQNameAsString);
            if (prop != null)
            {
                if (logger.isDebugEnabled())
                {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Attempting to delete custom property: ");
                    msg.append(propQNameAsString);
                    logger.debug(msg.toString());
                }
                
                found = true;
                customPropsAspect.removeProperty(propQNameAsString);
                break;
            }
        }
        
        if (found == false)
        {
            throw new AlfrescoRuntimeException("Could not find property to delete: "+propQNameAsString);
        }
        
        writeCustomContentModel(modelRef, deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("deleteCustomPropertyDefinition: "+propQNameAsString+" from aspect: "+aspectName);
        }
    }

	/**
	 * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#getCustomReferenceDefinitions()
	 */
    public Map<QName, AssociationDefinition> getCustomReferenceDefinitions()
    {
		QName relevantAspectQName = QName.createQName(RMC_CUSTOM_ASSOCS, namespaceService);
        AspectDefinition aspectDefn = dictionaryService.getAspect(relevantAspectQName);
        Map<QName, AssociationDefinition> assocDefns = aspectDefn.getAssociations();
        
        return assocDefns;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService#addCustomReference(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
     */
	public void addCustomReference(NodeRef fromNode, NodeRef toNode, QName refId)
	{
	    // Check that a definition for the reference type exists.
		Map<QName, AssociationDefinition> availableAssocs = this.getCustomReferenceDefinitions();

		AssociationDefinition assocDef = availableAssocs.get(refId);
		if (assocDef == null)
		{
			throw new IllegalArgumentException("No such custom reference: " + refId);
		}

		// Check if an instance of this reference type already exists in the same direction.
		boolean associationAlreadyExists = false;
        if (assocDef.isChild())
        {
            List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(fromNode, assocDef.getName(), assocDef.getName());
            for (ChildAssociationRef chAssRef : childAssocs)
            {
                if (chAssRef.getChildRef().equals(toNode))
                {
                    associationAlreadyExists = true;
                }
            }
        }
        else
        {
            List<AssociationRef> assocs = nodeService.getTargetAssocs(fromNode, assocDef.getName());
            for (AssociationRef assRef : assocs)
            {
                if (assRef.getTargetRef().equals(toNode))
                {
                    associationAlreadyExists = true;
                }
            }
        }
        if (associationAlreadyExists)
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Association '").append(refId).append("' already exists from ")
                .append(fromNode).append(" to ").append(toNode);
            throw new AlfrescoRuntimeException(msg.toString());
        }

		// Invoke before create reference policy
		invokeBeforeCreateReference(fromNode, toNode, refId);
		
		if (assocDef.isChild())
		{
			this.nodeService.addChild(fromNode, toNode, refId, refId);
		}
		else
		{
			this.nodeService.createAssociation(fromNode, toNode, refId);
		}
		
		// Invoke on create reference policy
        invokeOnCreateReference(fromNode, toNode, refId);
	}

	public void removeCustomReference(NodeRef fromNode, NodeRef toNode, QName assocId) 
	{
		Map<QName, AssociationDefinition> availableAssocs = this.getCustomReferenceDefinitions();

		AssociationDefinition assocDef = availableAssocs.get(assocId);
		if (assocDef == null)
		{
			throw new IllegalArgumentException("No such custom reference: " + assocId);
		}
		
		invokeBeforeRemoveReference(fromNode, toNode, assocId);

		if (assocDef.isChild())
		{
		    // TODO:  Ask for a more efficient method such as
		    //        nodeService.removeChildAssociation(fromNode, toNode, chRef.getTypeQName(), null);

			List<ChildAssociationRef> children = nodeService.getChildAssocs(fromNode);
			for (ChildAssociationRef chRef : children)
			{
				if (assocId.equals(chRef.getTypeQName()) && chRef.getChildRef().equals(toNode))
				{
					nodeService.removeChildAssociation(chRef);
				}
			}
		}
		else
		{
			nodeService.removeAssociation(fromNode, toNode, assocId);
		}
		
		invokeOnRemoveReference(fromNode, toNode, assocId);
	}

	public List<AssociationRef> getCustomReferencesFrom(NodeRef node)
	{
    	List<AssociationRef> retrievedAssocs = nodeService.getTargetAssocs(node, RegexQNamePattern.MATCH_ALL);
    	return retrievedAssocs;
	}

	public List<ChildAssociationRef> getCustomChildReferences(NodeRef node)
	{
    	List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(node);
    	return childAssocs;
	}
	
    public List<AssociationRef> getCustomReferencesTo(NodeRef node)
    {
        List<AssociationRef> retrievedAssocs = nodeService.getSourceAssocs(node, RegexQNamePattern.MATCH_ALL);
        return retrievedAssocs;
    }

    public List<ChildAssociationRef> getCustomParentReferences(NodeRef node)
    {
        List<ChildAssociationRef> result = nodeService.getParentAssocs(node);
        return result;
    }
    
    // note: currently RMC custom assocs only
    public QName addCustomAssocDefinition(String label)
    {
        ParameterCheck.mandatoryString("label", label);
        
        NodeRef modelRef = getCustomModelRef(""); // defaults to RM_CUSTOM_URI
        M2Model deserializedModel = readCustomContentModel(modelRef);
        
        String aspectName = RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS;
        
        M2Aspect customAssocsAspect = deserializedModel.getAspect(aspectName);
        
        if (customAssocsAspect == null)
        {
            throw new AlfrescoRuntimeException("Unknown aspect: "+aspectName);
        }

        // If this label is already taken...
        if (getQNameForClientId(label) != null)
        {
            throw new IllegalArgumentException("Reference label already in use: " + label);
        }
        
        QName generatedQName = this.generateQNameFor(label);
        String generatedShortQName = generatedQName.toPrefixString(namespaceService);
        
        M2ClassAssociation customAssoc = customAssocsAspect.getAssociation(generatedShortQName);
        if (customAssoc != null)
        {
            throw new AlfrescoRuntimeException("Assoc already exists: "+generatedShortQName);
        }
        
        M2Association newAssoc = customAssocsAspect.createAssociation(generatedShortQName);
        newAssoc.setSourceMandatory(false);
        newAssoc.setTargetMandatory(false);

        // MOB-1573
        newAssoc.setSourceMany(true);
        newAssoc.setTargetMany(true);

        // The label is stored in the title.
        newAssoc.setTitle(label);
        
        // TODO Could be the customAssocs aspect
        newAssoc.setTargetClassName(RecordsManagementAdminServiceImpl.RMA_RECORD);
        
        writeCustomContentModel(modelRef, deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("addCustomAssocDefinition: ("+label+")");
        }
        
        return generatedQName;
    }
    
    // note: currently RMC custom assocs only
    public QName addCustomChildAssocDefinition(String source, String target)
    {
        ParameterCheck.mandatoryString("source", source);
        ParameterCheck.mandatoryString("target", target);
        
        NodeRef modelRef = getCustomModelRef(""); // defaults to RM_CUSTOM_URI
        M2Model deserializedModel = readCustomContentModel(modelRef);
        
        String aspectName = RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS;
        
        M2Aspect customAssocsAspect = deserializedModel.getAspect(aspectName);
        
        if (customAssocsAspect == null)
        {
            throw new AlfrescoRuntimeException("Unknown aspect: "+aspectName);
        }

        String compoundID = this.getCompoundIdFor(source, target);
        if (getQNameForClientId(compoundID) != null)
        {
            throw new IllegalArgumentException("Reference label already in use: " + compoundID);
        }
        
        M2ClassAssociation customAssoc = customAssocsAspect.getAssociation(compoundID);
        if (customAssoc != null)
        {
            throw new AlfrescoRuntimeException("ChildAssoc already exists: "+compoundID);
        }
        QName generatedQName = this.generateQNameFor(compoundID);
        
        M2ChildAssociation newAssoc = customAssocsAspect.createChildAssociation(generatedQName.toPrefixString(namespaceService));
        newAssoc.setSourceMandatory(false);
        newAssoc.setTargetMandatory(false);
        
        // MOB-1573
        newAssoc.setSourceMany(true);
        newAssoc.setTargetMany(true);

        // source and target are stored in title.
        newAssoc.setTitle(compoundID);
        
        // TODO Could be the custom assocs aspect
        newAssoc.setTargetClassName(RecordsManagementAdminServiceImpl.RMA_RECORD);
        
        writeCustomContentModel(modelRef, deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("addCustomChildAssocDefinition: ("+source+","+target+")");
        }
        
        return generatedQName;
    }
    
    // note: currently RMC custom assocs only
    public QName updateCustomChildAssocDefinition(QName refQName, String newSource, String newTarget)
    {
        String compoundId = getCompoundIdFor(newSource, newTarget);
        return persistUpdatedAssocTitle(refQName, compoundId);
    }
    
    // note: currently RMC custom assocs only
    public QName updateCustomAssocDefinition(QName refQName, String newLabel)
    {
        return persistUpdatedAssocTitle(refQName, newLabel);
    }

    /**
     * This method writes the specified String into the association's title property.
     * For RM custom properties and references, Title is used to store the identifier.
     */
    // note: currently RMC custom assocs only
    private QName persistUpdatedAssocTitle(QName refQName, String newTitle)
    {
        ParameterCheck.mandatory("refQName", refQName);
        
        AssociationDefinition assocDefn = dictionaryService.getAssociation(refQName);
        if (assocDefn == null)
        {
            throw new AlfrescoRuntimeException("DictionaryService does not contain association definition " + refQName);
        }
        
        NodeRef modelRef = getCustomModelRef(""); // defaults to RM_CUSTOM_URI
        M2Model deserializedModel = readCustomContentModel(modelRef);
        
        M2Aspect customAssocsAspect = deserializedModel.getAspect(RMC_CUSTOM_ASSOCS);
        
        for (M2ClassAssociation assoc : customAssocsAspect.getAssociations())
        {
            if (refQName.toPrefixString(namespaceService).equals(assoc.getName()))
            {
                if (newTitle != null)
                {
                    assoc.setTitle(newTitle);
                }
            }
        }
        writeCustomContentModel(modelRef, deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("persistUpdatedAssocTitle: "+refQName+
                    "=" + newTitle + " to aspect: " + RMC_CUSTOM_ASSOCS);
        }
        
        return refQName;
    }
    
    public void addCustomConstraintDefinition(QName constraintName, String title, boolean caseSensitive, List<String> allowedValues, MatchLogic matchLogic) 
    {
        ParameterCheck.mandatory("constraintName", constraintName);
        ParameterCheck.mandatoryString("title", title);
        ParameterCheck.mandatory("allowedValues", allowedValues);
        
        NodeRef modelRef = getCustomModelRef(constraintName.getNamespaceURI());
        M2Model deserializedModel = readCustomContentModel(modelRef);
        
        String constraintNameAsPrefixString = constraintName.toPrefixString(namespaceService);
        
        M2Constraint customConstraint = deserializedModel.getConstraint(constraintNameAsPrefixString);
        if (customConstraint != null)
        {
            throw new AlfrescoRuntimeException("Constraint already exists: "+constraintNameAsPrefixString);
        }
        
        M2Constraint newCon = deserializedModel.createConstraint(constraintNameAsPrefixString, CUSTOM_CONSTRAINT_TYPE);
        
        newCon.setTitle(title);
        newCon.createParameter(PARAM_ALLOWED_VALUES, allowedValues);
        newCon.createParameter(PARAM_CASE_SENSITIVE, caseSensitive ? "true" : "false");
        newCon.createParameter(PARAM_MATCH_LOGIC, matchLogic.toString());
        
        writeCustomContentModel(modelRef, deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("addCustomConstraintDefinition: "+constraintNameAsPrefixString+" (valueCnt: "+allowedValues.size()+")");
        }
    }
    
    /*
    public void addCustomConstraintDefinition(QName constraintName, String description, Map<String, Object> parameters) 
    {
        // TODO Auto-generated method stub
    }
    */
    
    public void changeCustomConstraintValues(QName constraintName, List<String> newAllowedValues)
    {
        ParameterCheck.mandatory("constraintName", constraintName);
        ParameterCheck.mandatory("newAllowedValues", newAllowedValues);
        
        NodeRef modelRef = getCustomModelRef(constraintName.getNamespaceURI());
        M2Model deserializedModel = readCustomContentModel(modelRef);
        
        String constraintNameAsPrefixString = constraintName.toPrefixString(namespaceService);
        
        M2Constraint customConstraint = deserializedModel.getConstraint(constraintNameAsPrefixString);
        if (customConstraint == null)
        {
            throw new AlfrescoRuntimeException("Unknown constraint: "+constraintNameAsPrefixString);
        }
        
        String type = customConstraint.getType();
        if ((type == null) || (! type.equals(CUSTOM_CONSTRAINT_TYPE)))
        {
            throw new AlfrescoRuntimeException("Unexpected type '"+type+"' for constraint: "+constraintNameAsPrefixString+" (expected '"+CUSTOM_CONSTRAINT_TYPE+"')");
        }
        
        customConstraint.removeParameter(PARAM_ALLOWED_VALUES);
        customConstraint.createParameter(PARAM_ALLOWED_VALUES, newAllowedValues);
        
        writeCustomContentModel(modelRef, deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("changeCustomConstraintValues: "+constraintNameAsPrefixString+" (valueCnt: "+newAllowedValues.size()+")");
        }
    }
    
    public void changeCustomConstraintTitle(QName constraintName, String title)
    {
        ParameterCheck.mandatory("constraintName", constraintName);
        ParameterCheck.mandatoryString("title", title);
        
        NodeRef modelRef = getCustomModelRef(constraintName.getNamespaceURI());
        M2Model deserializedModel = readCustomContentModel(modelRef);
        
        String constraintNameAsPrefixString = constraintName.toPrefixString(namespaceService);
        
        M2Constraint customConstraint = deserializedModel.getConstraint(constraintNameAsPrefixString);
        if (customConstraint == null)
        {
            throw new AlfrescoRuntimeException("Unknown constraint: "+constraintNameAsPrefixString);
        }
        
        String type = customConstraint.getType();
        if ((type == null) || (! type.equals(CUSTOM_CONSTRAINT_TYPE)))
        {
            throw new AlfrescoRuntimeException("Unexpected type '"+type+"' for constraint: "+constraintNameAsPrefixString+" (expected '"+CUSTOM_CONSTRAINT_TYPE+"')");
        }
        
        customConstraint.setTitle(title);
        
        writeCustomContentModel(modelRef, deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("changeCustomConstraintTitle: "+constraintNameAsPrefixString+" (title: "+title+")");
        }
    }
    
    public List<ConstraintDefinition> getCustomConstraintDefinitions(QName modelQName) 
    {
        Collection<ConstraintDefinition> conDefs = dictionaryService.getConstraints(modelQName, true);
        
        for (ConstraintDefinition conDef : conDefs)
        {
            Constraint con = conDef.getConstraint();
            if (! (con instanceof RMListOfValuesConstraint))
            {
                conDefs.remove(conDef);
            }
        }
        
        return new ArrayList<ConstraintDefinition>(conDefs);
    }
    
    public void removeCustomConstraintDefinition(QName constraintName) 
    {
        ParameterCheck.mandatory("constraintName", constraintName);
        
        NodeRef modelRef = getCustomModelRef(constraintName.getNamespaceURI());
        M2Model deserializedModel = readCustomContentModel(modelRef);
        
        String constraintNameAsPrefixString = constraintName.toPrefixString(namespaceService);
        
        M2Constraint customConstraint = deserializedModel.getConstraint(constraintNameAsPrefixString);
        if (customConstraint == null)
        {
            throw new AlfrescoRuntimeException("Constraint does not exist: "+constraintNameAsPrefixString);
        }
        
        deserializedModel.removeConstraint(constraintNameAsPrefixString);
        
        writeCustomContentModel(modelRef, deserializedModel);
        
        if (logger.isInfoEnabled())
        {
            logger.info("deleteCustomConstraintDefinition: "+constraintNameAsPrefixString);
        }
    }
    
    private NodeRef getCustomModelRef(String uri)
    {
        if ((uri.equals("")) || (uri.equals(RecordsManagementModel.RM_CUSTOM_URI)))
        {
            // note: short-cut for "rmc" currently assumes that RM custom model does not define additional namespaces
            return RM_CUSTOM_MODEL_NODE_REF;
        }
        else
        {
            // ALF-5875
            List<NodeRef> modelRefs = dictonaryRepositoryBootstrap.getModelRefs();
            
            for (NodeRef modelRef : modelRefs)
            {
                try
                {
                    M2Model model = readCustomContentModel(modelRef);
                    
                    for (M2Namespace namespace : model.getNamespaces())
                    {
                        if (namespace.getUri().equals(uri))
                        {
                            return modelRef;
                        }
                    }
                } 
                catch (DictionaryException de)
                {
                    logger.warn("readCustomContentModel: skip model ("+modelRef+") whilst searching for uri ("+uri+"): "+de);
                }
            }
            
            throw new AlfrescoRuntimeException("Model not found for uri: "+uri);
        }
    }
    
    private M2Model readCustomContentModel(NodeRef modelNodeRef)
    {
        ContentReader reader = this.contentService.getReader(modelNodeRef,
                                                             ContentModel.TYPE_CONTENT);
        
        if (reader.exists() == false) {throw new AlfrescoRuntimeException("RM CustomModel has no content.");}
        
        InputStream contentIn = null;
        M2Model deserializedModel = null;
        try
        {
            contentIn = reader.getContentInputStream();
            deserializedModel = M2Model.createModel(contentIn);
        }
        finally
        {
            try
            {
                if (contentIn != null) contentIn.close();
            }
            catch (IOException ignored)
            {
                // Intentionally empty.`
            }
        }
        return deserializedModel;
    }
    
    private void writeCustomContentModel(NodeRef modelRef, M2Model deserializedModel)
    {
        ContentWriter writer = this.contentService.getWriter(modelRef, ContentModel.TYPE_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_XML);
        writer.setEncoding("UTF-8");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        deserializedModel.toXML(baos);
        
        String updatedModelXml;
        try
        {
            updatedModelXml = baos.toString("UTF-8");
            writer.putContent(updatedModelXml);
            // putContent closes all resources.
            // so we don't have to.
        } catch (UnsupportedEncodingException uex)
        {
            throw new AlfrescoRuntimeException("Exception when writing custom model xml.", uex);
        }
    }

    
    public QName getQNameForClientId(String localName)
    {
        //TODO 1. After certification. This implementation currently does not support reference,
        // property, constraints definitions with the same names, which is technically allowed by Alfresco.

        //TODO 2. Note the implicit assumption here that all custom references will have
        // unique titles. This is, in fact, not guaranteed.
        
        QName propertyResult = null;
        for (QName qn : getCustomPropertyDefinitions().keySet())
        {
            if (localName != null && localName.equals(qn.getLocalName()))
            {
                propertyResult = qn;
            }
        }
        
        if (propertyResult != null)
        {
            return propertyResult;
        }
        
        QName referenceResult = null;
        for (QName refQn : getCustomReferenceDefinitions().keySet())
        {
            if (localName != null && localName.equals(refQn.getLocalName()))
            {
                referenceResult = refQn;
            }
        }
        
        // TODO Handle the case where both are not null
        return referenceResult;
    }

    private QName generateQNameFor(String clientId)
    {
        if (getQNameForClientId(clientId) != null)
        {
            // TODO log it's already taken. What to do?
            throw new IllegalArgumentException("clientId already in use: " + clientId);
        }
        
        String newGUID = GUID.generate();
        QName newQName = QName.createQName(RM_CUSTOM_PREFIX, newGUID, namespaceService);
        
        return newQName;
    }
   
    public String[] splitSourceTargetId(String sourceTargetId)
    {
        if (!sourceTargetId.contains(SOURCE_TARGET_ID_SEPARATOR))
        {
            throw new IllegalArgumentException("Illegal sourceTargetId: " + sourceTargetId);
        }
        return sourceTargetId.split(SOURCE_TARGET_ID_SEPARATOR);
    }
    
    public String getCompoundIdFor(String sourceId, String targetId)
    {
        ParameterCheck.mandatoryString("sourceId", sourceId);
        ParameterCheck.mandatoryString("targetId", targetId);
        
        if (sourceId.contains(SOURCE_TARGET_ID_SEPARATOR))
        {
            throw new IllegalArgumentException("sourceId cannot contain '" + SOURCE_TARGET_ID_SEPARATOR
                    + "': " + sourceId);
        }
        StringBuilder result = new StringBuilder();
        result.append(sourceId)
            .append(SOURCE_TARGET_ID_SEPARATOR)
            .append(targetId);
        return result.toString();
    }
}
