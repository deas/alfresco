/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.repo.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.NamespaceDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.LockHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Default implementation of the Dictionary.
 *  
 * @author David Caruana, janv
 *
 */
public class DictionaryDAOImpl implements DictionaryDAO
{
    /**
     * Lock objects
     */
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();
    
    // Namespace Data Access
    private NamespaceDAO namespaceDAO;

    // Tenant Service
    private TenantService tenantService;
    
    // Internal cache (clusterable)
    private SimpleCache<String, DictionaryRegistry> dictionaryRegistryCache;

    // used to reset the cache
    private ThreadLocal<DictionaryRegistry> dictionaryRegistryThreadLocal = new ThreadLocal<DictionaryRegistry>();
    private ThreadLocal<DictionaryRegistry> defaultDictionaryRegistryThreadLocal = new ThreadLocal<DictionaryRegistry>();

    // Static list of registered dictionary listeners
    private List<DictionaryListener> dictionaryListeners = new ArrayList<DictionaryListener>();

    // Logger
    private static Log logger = LogFactory.getLog(DictionaryDAO.class);

    private String defaultAnalyserResourceBundleName;
    
    private ClassLoader resourceClassLoader;

    // Try lock timeout (MNT-11371)
    private long tryLockTimeout;

    // inject dependencies
    
    public void setTenantService(TenantService tenantService)
    {
        this.tenantService = tenantService;
    }

    public void setTryLockTimeout(long tryLockTimeout)
    {
        this.tryLockTimeout = tryLockTimeout;
    }

    public void setDictionaryRegistryCache(SimpleCache<String, DictionaryRegistry> dictionaryRegistryCache)
    {
        this.dictionaryRegistryCache = dictionaryRegistryCache;
        // We are reading straight through to a shared cache - make sure it starts empty to avoid weird behaviour in
        // multi-context test suites that don't properly reset ehcache
        if (dictionaryRegistryCache.get(TenantService.DEFAULT_DOMAIN) != null)
        {
            dictionaryRegistryCache.clear();
        }
    }
    
    @Override 
    public String getDefaultAnalyserResourceBundleName()
    {
        return defaultAnalyserResourceBundleName;
    }

    public void setDefaultAnalyserResourceBundleName(String defaultAnalyserResourceBundleName)
    {
        this.defaultAnalyserResourceBundleName = defaultAnalyserResourceBundleName;
    }

    /**
     * Construct
     * 
     * @param namespaceDAO  namespace data access
     */
    public DictionaryDAOImpl(NamespaceDAO namespaceDAO)
    {
        this.namespaceDAO = namespaceDAO;
        this.namespaceDAO.registerDictionary(this);
        
    }
    
    /**
     * Register with the Dictionary
     */
    public void register(DictionaryListener dictionaryListener)
    {
        if (! dictionaryListeners.contains(dictionaryListener))
        {
            dictionaryListeners.add(dictionaryListener);
        }
    }
    
    /**
     * Initialise the Dictionary & Namespaces
     */
    public void init()
    {
        // Only init if we don't already have a registry for this domain. Use reset to reinit.
        getDictionaryRegistry(tenantService.getCurrentUserDomain(), true);
    }
    
    /**
     * Destroy the Dictionary & Namespaces
     */
    public void destroy()
    {
        String tenantDomain = tenantService.getCurrentUserDomain();
        
        removeDictionaryRegistry(tenantDomain);
        
        namespaceDAO.destroy();
        
        // notify registered listeners that dictionary has been destroyed
        for (DictionaryListener dictionaryDeployer : dictionaryListeners)
        {
            dictionaryDeployer.afterDictionaryDestroy();
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Dictionary destroyed");
        }
    }
    
    /**
     * Reset the Dictionary & Namespaces
     */      
    public void reset()
    {
        if (logger.isDebugEnabled()) 
        {
            logger.debug("Resetting dictionary ...");
        }
        
        destroy();
        init();
        
        if (logger.isDebugEnabled()) 
        {
            logger.debug("... resetting dictionary completed");
        }
    }
    
    // load dictionary (models and namespaces)
    private DictionaryRegistry initDictionary(final String tenantDomain, final boolean keepNamespaceLocal)
    {
        long startTime = System.currentTimeMillis();
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Init Dictionary: ["+Thread.currentThread()+"] "+(tenantDomain.equals(TenantService.DEFAULT_DOMAIN) ? "" : " (Tenant: "+tenantDomain+")"));
        }
        LockHelper.tryLock(writeLock, tryLockTimeout, "putting dictionary registry into cache in 'DictionaryDAOImpl.initDictionary()'");
        try
        {
            DictionaryRegistry result = AuthenticationUtil.runAs(new RunAsWork<DictionaryRegistry>()
            {
                public DictionaryRegistry doWork()
                {
                    try
                    {
                        DictionaryRegistry dictionaryRegistry = initDictionaryRegistry(tenantDomain);
                        
                        if (dictionaryRegistry == null)
                        {
                            // unexpected
                            throw new AlfrescoRuntimeException("Failed to init dictionaryRegistry " + tenantDomain);
                        }
                        
                        dictionaryRegistryCache.put(tenantDomain, dictionaryRegistry);
                        return dictionaryRegistry;
                    }
                    finally
                    {
                        if (dictionaryRegistryCache.get(tenantDomain) != null)
                        {
                            removeDataDictionaryLocal(tenantDomain);
                            if (!keepNamespaceLocal)
                            {
                                namespaceDAO.clearNamespaceLocal();
                            }
                        }
                    }
                }
            }, tenantService.getDomainUser(AuthenticationUtil.getSystemUserName(), tenantDomain));
            // Done
            if (logger.isInfoEnabled())
            {
                logger.info("Init Dictionary: model count = "+(getModels() != null ? getModels().size() : 0) +" in "+(System.currentTimeMillis()-startTime)+" msecs ["+Thread.currentThread()+"] "+(tenantDomain.equals(TenantService.DEFAULT_DOMAIN) ? "" : " (Tenant: "+tenantDomain+")"));
            }
            return result;
        }
        finally
        {
            writeLock.unlock();
        }
    }
    
    private DictionaryRegistry initDictionaryRegistry(String tenantDomain)
    {
        // create threadlocal, if needed
        DictionaryRegistry dictionaryRegistry = createDataDictionaryLocal(tenantDomain);
        
        dictionaryRegistry.setCompiledModels(new HashMap<QName,CompiledModel>());
        dictionaryRegistry.setUriToModels(new HashMap<String, List<CompiledModel>>());
        
        if (logger.isTraceEnabled()) 
        {
            logger.trace("Empty dictionary initialised: "+dictionaryRegistry+" - "+defaultDictionaryRegistryThreadLocal+" ["+Thread.currentThread()+"]");
        }
        
        // initialise empty namespaces
        namespaceDAO.init();
        
        // populate the dictionary based on registered sources
        for (DictionaryListener dictionaryDeployer : dictionaryListeners)
        {
            dictionaryDeployer.onDictionaryInit();
        }
        
        // notify registered listeners that dictionary has been initialised (population is complete)
        for (DictionaryListener dictionaryListener : dictionaryListeners)
        {
            dictionaryListener.afterDictionaryInit();
        }
        
        // called last
        namespaceDAO.afterDictionaryInit();
        
        return dictionaryRegistry;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.DictionaryDAO#putModel(org.alfresco.repo.dictionary.impl.M2Model)
     */
    public QName putModel(M2Model model)
    {
        return putModelImpl(model, true);
    }
    
    @Override
    public QName putModelIgnoringConstraints(M2Model model)
    {
        return putModelImpl(model, false);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.DictionaryDAO#putModel(org.alfresco.repo.dictionary.impl.M2Model)
     */
    public QName putModelImpl(M2Model model, boolean enableConstraintClassLoading)
    {
        String tenantDomain = tenantService.getCurrentUserDomain();
        
        // Compile model definition
        CompiledModel compiledModel = model.compile(this, namespaceDAO, enableConstraintClassLoading);
        QName modelName = compiledModel.getModelDefinition().getName();
        
        // Remove namespace definitions for previous model, if it exists
        CompiledModel previousVersion = getCompiledModels(tenantDomain).get(modelName);
        if (previousVersion != null)
        {
            for (M2Namespace namespace : previousVersion.getM2Model().getNamespaces())
            {
                namespaceDAO.removePrefix(namespace.getPrefix());
                namespaceDAO.removeURI(namespace.getUri());
                unmapUriToModel(namespace.getUri(), previousVersion, tenantDomain);
            }
            for (M2Namespace importNamespace : previousVersion.getM2Model().getImports())
            {
            	unmapUriToModel(importNamespace.getUri(), previousVersion, tenantDomain);
            }
        }
        
        // Create namespace definitions for new model
        for (M2Namespace namespace : model.getNamespaces())
        {
            namespaceDAO.addURI(namespace.getUri());
            namespaceDAO.addPrefix(namespace.getPrefix(), namespace.getUri());
            mapUriToModel(namespace.getUri(), compiledModel, tenantDomain);
        }
        for (M2Namespace importNamespace : model.getImports())
        {
        	mapUriToModel(importNamespace.getUri(), compiledModel, tenantDomain);
        }
        
        // Publish new Model Definition
        getCompiledModels(tenantDomain).put(modelName, compiledModel);
        
        if (logger.isTraceEnabled())
        {
            logger.trace("Registered model: " + modelName.toPrefixString(namespaceDAO));
            for (M2Namespace namespace : model.getNamespaces())
            {
                logger.trace("Registered namespace: '" + namespace.getUri() + "' (prefix '" + namespace.getPrefix() + "')");
            }
        }
        
        return modelName;
    }
    
    /**
     * @see org.alfresco.repo.dictionary.DictionaryDAO#removeModel(org.alfresco.service.namespace.QName)
     */
    public void removeModel(QName modelName)
    {
        String tenantDomain = tenantService.getCurrentUserDomain();
        
        CompiledModel compiledModel = getCompiledModels(tenantDomain).get(modelName);
        if (compiledModel != null)
        {
            // Remove the namespaces from the namespace service
            M2Model model = compiledModel.getM2Model();            
            for (M2Namespace namespace : model.getNamespaces())
            {
                namespaceDAO.removePrefix(namespace.getPrefix());
                namespaceDAO.removeURI(namespace.getUri());
                unmapUriToModel(namespace.getUri(), compiledModel, tenantDomain);
            }
            
            // Remove the model from the list
            getCompiledModels(tenantDomain).remove(modelName);
        }
    }
    
    /**
     * Map Namespace URI to Model
     * 
     * @param uri   namespace uri
     * @param model   model
     * @param tenantDomain
     */
    private void mapUriToModel(String uri, CompiledModel model, String tenantDomain)
    {
    	List<CompiledModel> models = getUriToModels(tenantDomain).get(uri);
    	if (models == null)
    	{
    		models = new ArrayList<CompiledModel>();
    		getUriToModels(tenantDomain).put(uri, models);
    	}
    	if (!models.contains(model))
    	{
    		models.add(model);
    	}
    }
    
    /**
     * Unmap Namespace URI from Model
     * 
     * @param uri  namespace uri
     * @param model   model
     * @param tenantDomain
     */
    private void unmapUriToModel(String uri, CompiledModel model, String tenantDomain)
    {
    	List<CompiledModel> models = getUriToModels(tenantDomain).get(uri);
    	if (models != null)
    	{
    		models.remove(model);
    	}
    }

    
    /**
     * Get Models mapped to Namespace Uri
     * 
     * @param uri   namespace uri
     * @return   mapped models 
     */
    private List<CompiledModel> getModelsForUri(String uri)
    {
        String tenantDomain = tenantService.getCurrentUserDomain();
        if (! tenantDomain.equals(TenantService.DEFAULT_DOMAIN))
        {
            // get non-tenant models (if any)
            List<CompiledModel> models = getUriToModels(TenantService.DEFAULT_DOMAIN).get(uri);
            
            List<CompiledModel> filteredModels = new ArrayList<CompiledModel>();
            if (models != null)
            {
                filteredModels.addAll(models);
            }
    
            // get tenant models (if any)
            List<CompiledModel> tenantModels = getUriToModels(tenantDomain).get(uri);
            if (tenantModels != null)
            {
                if (models != null)
                {
                    // check to see if tenant model overrides a non-tenant model
                    for (CompiledModel tenantModel : tenantModels)
                    {
                        for (CompiledModel model : models)
                        {
                            if (tenantModel.getM2Model().getName().equals(model.getM2Model().getName()))
                            {
                                filteredModels.remove(model);
                            }
                        }
                    }
                }
                filteredModels.addAll(tenantModels);
                models = filteredModels;
            }
            
            if (models == null)
            {
                models = Collections.emptyList();
            }
            return models;
        }
        
        List<CompiledModel> models = getUriToModels(TenantService.DEFAULT_DOMAIN).get(uri);
        if (models == null)
        {
            models = Collections.emptyList(); 
        }
        return models;
    }
    
    /**
     * @param modelName  the model name
     * @return the compiled model of the given name
     */
    public CompiledModel getCompiledModel(QName modelName)
    {
        String tenantDomain = tenantService.getCurrentUserDomain();
        if (! tenantDomain.equals(TenantService.DEFAULT_DOMAIN))
        {
            // get tenant-specific model (if any)
            CompiledModel model = getCompiledModels(tenantDomain).get(modelName);
            if (model != null)
            {
                return model;
            }
            // else drop down to check for shared (core/system) models ...
        }

        // get non-tenant model (if any)
        CompiledModel model = getCompiledModels(TenantService.DEFAULT_DOMAIN).get(modelName);
        if (model == null)
        {
            throw new DictionaryException("d_dictionary.model.err.no_model", modelName);
        }
        return model;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.ModelQuery#getPropertyType(org.alfresco.repo.ref.QName)
     */
    public DataTypeDefinition getDataType(QName typeName)
    {
    	if (typeName != null) {
	        List<CompiledModel> models = getModelsForUri(typeName.getNamespaceURI());
	        for (CompiledModel model : models)
	        {
	        	DataTypeDefinition dataType = model.getDataType(typeName);
	        	if (dataType != null)
	        	{
	        		return dataType;
	        	}
	        }
    	}
        return null;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public DataTypeDefinition getDataType(Class javaClass)
    {
        String tenantDomain = tenantService.getCurrentUserDomain();
        if (! tenantDomain.equals(TenantService.DEFAULT_DOMAIN))
        {
            // get tenant models (if any)                
            for (CompiledModel model : getCompiledModels(tenantDomain).values())
            { 
                DataTypeDefinition dataTypeDef = model.getDataType(javaClass);
                if (dataTypeDef != null)
                {
                    return dataTypeDef;
                }
            }          
        
            // get non-tenant models (if any)
            for (CompiledModel model : getCompiledModels(TenantService.DEFAULT_DOMAIN).values())
            {    
                DataTypeDefinition dataTypeDef = model.getDataType(javaClass);
                if (dataTypeDef != null)
                {
                    return dataTypeDef;
                }
            }
        
            return null;
        }
        else
        {
            for (CompiledModel model : getCompiledModels(TenantService.DEFAULT_DOMAIN).values())
            {    
                DataTypeDefinition dataTypeDef = model.getDataType(javaClass);
                if (dataTypeDef != null)
                {
                    return dataTypeDef;
                }
            }
        }
        return null;
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.DictionaryDAO#getPropertyTypes(org.alfresco.repo.ref.QName)
     */
    public Collection<DataTypeDefinition> getDataTypes(QName modelName)
    {
        CompiledModel model = getCompiledModel(modelName);
        return model.getDataTypes();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.ModelQuery#getType(org.alfresco.repo.ref.QName)
     */
    public TypeDefinition getType(QName typeName)
    {
        if (typeName != null) {
            List<CompiledModel> models = getModelsForUri(typeName.getNamespaceURI());
            for (CompiledModel model : models)
            {
                TypeDefinition type = model.getType(typeName);
                if (type != null)
                {
                    return type;
                }
            }
            
            if (logger.isWarnEnabled())
            {
                logger.warn("Type not found: "+typeName);
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.DictionaryDAO#getSubTypes(org.alfresco.service.namespace.QName, boolean)
     */
    public Collection<QName> getSubTypes(QName superType, boolean follow)
    {
    	// note: could be optimised further, if compiled into the model
    	
        // Get all types (with parent type) for all models
        Map<QName, QName> allTypesAndParents = new HashMap<QName, QName>(); // name, parent
        
        for (CompiledModel model : getCompiledModels().values())
        {
        	for (TypeDefinition type : model.getTypes())
        	{
        		allTypesAndParents.put(type.getName(), type.getParentName());
        	}
        }
        
        // Get sub types
    	HashSet<QName> subTypes = new HashSet<QName>();
        for (QName type : allTypesAndParents.keySet())
        {
        	if (follow)
        	{   
        		// all sub types
        		QName current = type;
	            while ((current != null) && !current.equals(superType))
	            {
	            	current = allTypesAndParents.get(current); // get parent
	            }
	            if (current != null)
	            {
	            	subTypes.add(type);
	            }
        	}
        	else
        	{
        		// immediate sub types only
        	    QName typesSuperType = allTypesAndParents.get(type);
        		if (typesSuperType != null && typesSuperType.equals(superType))
        		{
        			subTypes.add(type);
        		}
        	}

        }
        return subTypes;
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.ModelQuery#getAspect(org.alfresco.repo.ref.QName)
     */
    public AspectDefinition getAspect(QName aspectName)
    {
        if (aspectName != null) {
            List<CompiledModel> models = getModelsForUri(aspectName.getNamespaceURI());
            for (CompiledModel model : models)
            {
                AspectDefinition aspect = model.getAspect(aspectName);
                if (aspect != null)
                {
                    return aspect;
                }
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.DictionaryDAO#getSubAspects(org.alfresco.service.namespace.QName, boolean)
     */
    public Collection<QName> getSubAspects(QName superAspect, boolean follow)
    {
    	// note: could be optimised further, if compiled into the model
    	
        // Get all aspects (with parent aspect) for all models   
        Map<QName, QName> allAspectsAndParents = new HashMap<QName, QName>(); // name, parent
        
        for (CompiledModel model : getCompiledModels().values())
        {
        	for (AspectDefinition aspect : model.getAspects())
        	{
        		allAspectsAndParents.put(aspect.getName(), aspect.getParentName());
        	}
        }
   	
        // Get sub aspects
    	HashSet<QName> subAspects = new HashSet<QName>();
        for (QName aspect : allAspectsAndParents.keySet())
        {
        	if (follow)
        	{
        		// all sub aspects
	        	QName current = aspect;
	            while ((current != null) && !current.equals(superAspect))
	            {
	            	current = allAspectsAndParents.get(current); // get parent
	            }
	            if (current != null)
	            {
	            	subAspects.add(aspect);
	            }
	    	}
	    	else
	    	{
	    		// immediate sub aspects only
	    	    QName typesSuperAspect = allAspectsAndParents.get(aspect);
	    		if (typesSuperAspect != null && typesSuperAspect.equals(superAspect))
	    		{
	    			subAspects.add(aspect);
	    		}
	    	}        	
        }
        return subAspects;
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.ModelQuery#getClass(org.alfresco.repo.ref.QName)
     */
    public ClassDefinition getClass(QName className)
    {
        List<CompiledModel> models = getModelsForUri(className.getNamespaceURI());

        for (CompiledModel model : models)
        {
        	ClassDefinition classDef = model.getClass(className);
        	if (classDef != null)
        	{
        		return classDef;
        	}
        }
        return null;
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.ModelQuery#getProperty(org.alfresco.repo.ref.QName)
     */
    public PropertyDefinition getProperty(QName propertyName)
    {
        List<CompiledModel> models = getModelsForUri(propertyName.getNamespaceURI());
        for (CompiledModel model : models)
        {
        	PropertyDefinition propDef = model.getProperty(propertyName);
        	if (propDef != null)
        	{
        		return propDef;
        	}
        }
        return null;
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ModelQuery#getConstraint(org.alfresco.service.namespace.QName)
     */
    public ConstraintDefinition getConstraint(QName constraintQName)
    {
        List<CompiledModel> models = getModelsForUri(constraintQName.getNamespaceURI());
        for (CompiledModel model : models)
        {
        	ConstraintDefinition constraintDef = model.getConstraint(constraintQName);
        	if (constraintDef != null)
        	{
        		return constraintDef;
        	}
        }
        return null;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.ModelQuery#getAssociation(org.alfresco.repo.ref.QName)
     */
    public AssociationDefinition getAssociation(QName assocName)
    {
        List<CompiledModel> models = getModelsForUri(assocName.getNamespaceURI());
        for (CompiledModel model : models)
        {
        	AssociationDefinition assocDef = model.getAssociation(assocName);
        	if (assocDef != null)
        	{
        		return assocDef;
        	}
        }
        return null;
    }

    public Collection<AssociationDefinition> getAssociations(QName modelName)
    {
        CompiledModel model = getCompiledModel(modelName);
        return model.getAssociations();
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.DictionaryDAO#getModels()
     */
    public Collection<QName> getModels()
    {
        // get all models - including inherited models, if applicable
        return getCompiledModels().keySet();
    }
    
    // MT-specific
    public boolean isModelInherited(QName modelName)
    {    
        String tenantDomain = tenantService.getCurrentUserDomain();
        if (! tenantDomain.equals(TenantService.DEFAULT_DOMAIN))
        {
            // get tenant-specific model (if any)
            CompiledModel model = getCompiledModels(tenantDomain).get(modelName);
            if (model != null)
            {
                return false;
            }
            // else drop down to check for shared (core/system) models ...
        }

        // get non-tenant model (if any)
        CompiledModel model = getCompiledModels(TenantService.DEFAULT_DOMAIN).get(modelName);
        if (model == null)
        {
            throw new DictionaryException("d_dictionary.model.err.no_model", modelName);
        }
        
        if (! tenantDomain.equals(TenantService.DEFAULT_DOMAIN))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private Map<QName,CompiledModel> getCompiledModels() 
    {
        String tenantDomain = tenantService.getCurrentUserDomain();
        if (! tenantDomain.equals(TenantService.DEFAULT_DOMAIN))
        {
            // return all tenant-specific models and all inherited (non-overridden) models
            Map<QName, CompiledModel> filteredModels = new HashMap<QName, CompiledModel>();
            
            // get tenant models (if any)
            Map<QName,CompiledModel> tenantModels = getCompiledModels(tenantDomain);
            
            // get non-tenant models - these will include core/system models and any additional custom models (which are implicitly available to all tenants)
            Map<QName,CompiledModel> nontenantModels = getCompiledModels(TenantService.DEFAULT_DOMAIN);

            // check for overrides
            filteredModels.putAll(nontenantModels);
     
            for (QName tenantModel : tenantModels.keySet())
            {
                for (QName nontenantModel : nontenantModels.keySet())
                {
                    if (tenantModel.equals(nontenantModel))
                    {
                        // override
                        filteredModels.remove(nontenantModel);
                        break;
                    }
                }
            }

            filteredModels.putAll(tenantModels);
            return filteredModels;
        }
        else
        {
            // return all (non-tenant) models
            return getCompiledModels(TenantService.DEFAULT_DOMAIN);
        } 
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.DictionaryDAO#getModel(org.alfresco.repo.ref.QName)
     */
    public ModelDefinition getModel(QName name)
    {
        CompiledModel model = getCompiledModel(name);
        return model.getModelDefinition();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.DictionaryDAO#getTypes(org.alfresco.repo.ref.QName)
     */
    public Collection<TypeDefinition> getTypes(QName modelName)
    {
        CompiledModel model = getCompiledModel(modelName);
        return model.getTypes();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.DictionaryDAO#getAspects(org.alfresco.repo.ref.QName)
     */
    public Collection<AspectDefinition> getAspects(QName modelName)
    {
        CompiledModel model = getCompiledModel(modelName);
        return model.getAspects();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.impl.DictionaryDAO#getAnonymousType(org.alfresco.repo.ref.QName, java.util.Collection)
     */
    public TypeDefinition getAnonymousType(QName type, Collection<QName> aspects)
    {
        TypeDefinition typeDef = getType(type);
        if (typeDef == null)
        {
            throw new DictionaryException("d_dictionary.model.err.type_not_found", type);
        }
        Collection<AspectDefinition> aspectDefs = new ArrayList<AspectDefinition>();
        if (aspects != null)
        {
            for (QName aspect : aspects)
            {
                AspectDefinition aspectDef = getAspect(aspect);
                if (aspectDef == null)
                {
                    throw new DictionaryException("d_dictionary.model.err.aspect_not_found", aspect);
                }
                aspectDefs.add(aspectDef);
            }
        }
        return new M2AnonymousTypeDefinition(typeDef, aspectDefs);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.dictionary.DictionaryDAO#getProperties(org.alfresco.service.namespace.QName)
     */
    public Collection<PropertyDefinition> getProperties(QName modelName)
    {
        CompiledModel model = getCompiledModel(modelName);
        return model.getProperties();
    }
    
    @Override
    public Collection<PropertyDefinition> getProperties(QName modelName, QName dataType)
    {
        HashSet<PropertyDefinition> properties = new HashSet<PropertyDefinition>();

        Collection<PropertyDefinition> props = getProperties(modelName);
        for(PropertyDefinition prop : props)
        {
            if((dataType == null) ||   prop.getDataType().getName().equals(dataType))
            {
                properties.add(prop);
            }
        }
        return properties;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.dictionary.DictionaryDAO#getProperties(org.alfresco.service.namespace.QName, org.alfresco.service.namespace.QName)
     */
    public Collection<PropertyDefinition> getPropertiesOfDataType(QName dataType)
    {
    	Collection<PropertyDefinition> properties = new HashSet<PropertyDefinition>();

    	Collection<QName> modelNames = getModels();
    	for(QName modelName : modelNames)
    	{
    		properties.addAll(getProperties(modelName, dataType));
    	}

    	return properties;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.dictionary.DictionaryDAO#getNamespaces(org.alfresco.service.namespace.QName)
     */
    public Collection<NamespaceDefinition> getNamespaces(QName modelName)
    {
        CompiledModel model = getCompiledModel(modelName);
        ModelDefinition modelDef = model.getModelDefinition();
        return modelDef.getNamespaces();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.DictionaryDAO#getConstraints(org.alfresco.service.namespace.QName)
     */
    public Collection<ConstraintDefinition> getConstraints(QName modelName)
    {
        return getConstraints(modelName, false);
    }
    
    public Collection<ConstraintDefinition> getConstraints(QName modelName, boolean referenceableDefsOnly)
    {
        CompiledModel model = getCompiledModel(modelName);
        if (referenceableDefsOnly)
        {
            return getReferenceableConstraintDefs(model);
        }
        else
        {
            return model.getConstraints();
        }
    }
    
    private Collection<ConstraintDefinition> getReferenceableConstraintDefs(CompiledModel model)
    {
        Collection<ConstraintDefinition> conDefs = model.getConstraints();
        Collection<PropertyDefinition> propDefs = model.getProperties();
        for (PropertyDefinition propDef : propDefs)
        {
            for (ConstraintDefinition conDef : propDef.getConstraints())
            {
                conDefs.remove(conDef);
            }
        }
        
        return conDefs;
    }
    
    // re-entrant (eg. via reset)
    private DictionaryRegistry getDictionaryRegistry(String tenantDomain)
    {
        return getDictionaryRegistry(tenantDomain, false, false);
    }

    // re-entrant (eg. via reset)
    private DictionaryRegistry getDictionaryRegistry(String tenantDomain, boolean init)
    {
        return getDictionaryRegistry(tenantDomain, init, true);
    }

    // re-entrant (eg. via reset)
    private DictionaryRegistry getDictionaryRegistry(String tenantDomain, boolean init, boolean keepNamespaceLocal)
    {
        DictionaryRegistry dictionaryRegistry = null;
        
        // check threadlocal first - return if set
        dictionaryRegistry = getDictionaryRegistryLocal(tenantDomain);
        if (dictionaryRegistry != null)
        {
            return dictionaryRegistry; // return local dictionaryRegistry
        }
        LockHelper.tryLock(readLock, tryLockTimeout, "getting dictionary registry from cache in 'DictionaryDAOImpl.getDictionaryRegistry()'");
        try
        {
            // check cache second - return if set
            dictionaryRegistry = dictionaryRegistryCache.get(tenantDomain);
            
            if (dictionaryRegistry != null)
            {
                if (init)
                {
                    // Reset thread cache whilst we have this lock
                    namespaceDAO.afterDictionaryInit();
                }
                return dictionaryRegistry; // return cached config
            }
        }
        finally
        {
            readLock.unlock();
        }
        
        // Double check cache with write lock
        LockHelper.tryLock(writeLock, tryLockTimeout, "getting dictionary registry from cache in 'DictionaryDAOImpl.getDictionaryRegistry()'");
        try
        {
            dictionaryRegistry = dictionaryRegistryCache.get(tenantDomain);
            
            if (dictionaryRegistry != null)
            {
                if (init)
                {
                    // Reset thread cache whilst we have this lock
                    namespaceDAO.afterDictionaryInit();
                }
                return dictionaryRegistry; // return cached config
            }

            if (logger.isTraceEnabled())
            {
                logger.trace("getDictionaryRegistry: not in cache (or threadlocal) - re-init ["+Thread.currentThread().getId()+"]"+(tenantDomain.equals(TenantService.DEFAULT_DOMAIN) ? "" : " (Tenant: "+tenantDomain+")"));
            }
            
            // reset caches - may have been invalidated (e.g. in a cluster)
            dictionaryRegistry = initDictionary(tenantDomain, keepNamespaceLocal);
        }
        finally
        {
            writeLock.unlock();
        }

        
        if (dictionaryRegistry == null)
        {     
            // unexpected
            throw new AlfrescoRuntimeException("Failed to get dictionaryRegistry " + tenantDomain);
        }
        
        return dictionaryRegistry;
    }
    
    // create threadlocal
    private DictionaryRegistry createDataDictionaryLocal(String tenantDomain)
    {
       // create threadlocal, if needed
        DictionaryRegistry dictionaryRegistry = getDictionaryRegistryLocal(tenantDomain);
        if (dictionaryRegistry == null)
        {
            dictionaryRegistry = new DictionaryRegistry(tenantDomain);
            
            if (tenantDomain.equals(TenantService.DEFAULT_DOMAIN))
            {
                defaultDictionaryRegistryThreadLocal.set(dictionaryRegistry);
            }
            else
            {
                dictionaryRegistryThreadLocal.set(dictionaryRegistry);
            }
        }
        
        return dictionaryRegistry;
    }
    
    // get threadlocal 
    private DictionaryRegistry getDictionaryRegistryLocal(String tenantDomain)
    {
        DictionaryRegistry dictionaryRegistry = null;
        
        if (tenantDomain.equals(TenantService.DEFAULT_DOMAIN))
        {
            dictionaryRegistry = this.defaultDictionaryRegistryThreadLocal.get();
        }
        else
        {
            dictionaryRegistry = this.dictionaryRegistryThreadLocal.get();
        }
        
        // check to see if domain switched
        if ((dictionaryRegistry != null) && (tenantDomain.equals(dictionaryRegistry.getTenantDomain())))
        {
            return dictionaryRegistry; // return threadlocal, if set
        }   
        
        return null;
    }
    
    // remove threadlocal
    private void removeDataDictionaryLocal(String tenantDomain)      
    {
        if (tenantDomain.equals(TenantService.DEFAULT_DOMAIN))
        {
            defaultDictionaryRegistryThreadLocal.set(null); // it's in the cache, clear the threadlocal
        }
        else
        {
            dictionaryRegistryThreadLocal.set(null); // it's in the cache, clear the threadlocal
        }
    }
    
    private void removeDictionaryRegistry(String tenantDomain)
    {
        LockHelper.tryLock(writeLock, tryLockTimeout, "removing dictionary registry from cache in 'DictionaryDAOImpl.removeDictionaryRegistry()'");
        try
        {
            if (dictionaryRegistryCache.get(tenantDomain) != null)
            {
                dictionaryRegistryCache.remove(tenantDomain);
            }
            
            removeDataDictionaryLocal(tenantDomain);
        }
        finally
        {
            writeLock.unlock();
        }
    }
    
    /**
     * Get compiledModels from the cache (in the context of the given tenant domain)
     * 
     * @param tenantDomain
     */
    private Map<QName,CompiledModel> getCompiledModels(String tenantDomain)
    {
        if ((! AuthenticationUtil.isMtEnabled()) || (! tenantDomain.equals(TenantService.DEFAULT_DOMAIN)))
        {
            return getDictionaryRegistry(tenantDomain).getCompiledModels();
        }
        else
        {
            // ALF-6029
            return TenantUtil.runAsSystemTenant(new TenantRunAsWork<Map<QName,CompiledModel>>()
            {
                public Map<QName,CompiledModel> doWork() throws Exception
                {
                    return getDictionaryRegistry(TenantService.DEFAULT_DOMAIN).getCompiledModels();
                }
            }, TenantService.DEFAULT_DOMAIN);
        }
    }
    
    /**
     * Get uriToModels from the cache (in the context of the given tenant domain)
     * 
     * @param tenantDomain
     */
    private Map<String, List<CompiledModel>> getUriToModels(String tenantDomain)
    {
        if ((! AuthenticationUtil.isMtEnabled()) || (! tenantDomain.equals(TenantService.DEFAULT_DOMAIN)))
        {
            return getDictionaryRegistry(tenantDomain).getUriToModels();
        }
        else
        {
            // ALF-6029
            return TenantUtil.runAsSystemTenant(new TenantRunAsWork<Map<String, List<CompiledModel>>>()
            {
                public Map<String, List<CompiledModel>> doWork() throws Exception
                {
                    return getDictionaryRegistry(TenantService.DEFAULT_DOMAIN).getUriToModels();
                }
            }, TenantService.DEFAULT_DOMAIN);
        }
    }
    
    
    /**
     * Return diffs between input model and model in the Dictionary.
     * 
     * If the input model does not exist in the Dictionary then no diffs will be returned.
     * 
     * @param model
     * @return model diffs (if any)
     */
    public List<M2ModelDiff> diffModel(M2Model model)
    {
        return diffModel(model, true);
    }
    
    public List<M2ModelDiff> diffModelIgnoringConstraints(M2Model model)
    {
        return diffModel(model, false);
    }
    
    /**
     * Return diffs between input model and model in the Dictionary.
     * 
     * If the input model does not exist in the Dictionary then no diffs will be returned.
     * 
     * @param model
     * @return model diffs (if any)
     */
    public List<M2ModelDiff> diffModel(M2Model model, boolean enableConstraintClassLoading)
    {
        // Compile model definition
        CompiledModel compiledModel = model.compile(this, namespaceDAO, enableConstraintClassLoading);
        QName modelName = compiledModel.getModelDefinition().getName();
        
        CompiledModel previousVersion = null;
        try 
        { 
            previousVersion = getCompiledModel(modelName); 
        } 
        catch (DictionaryException e) 
        {
            logger.warn(e);
        } // ignore missing model

        if (previousVersion == null)
        {
            return new ArrayList<M2ModelDiff>(0);
        }
        else
        {
            return diffModel(previousVersion, compiledModel);
        }
    }
    
    /**
     * Return diffs between two compiled models.
     * 
     * note:
     * - checks classes (types & aspects) for incremental updates
     * - checks properties for incremental updates, but does not include the diffs
     * - checks assocs & child assocs for incremental updates, but does not include the diffs
     * - incremental updates include changes in title/description, property default value, etc
     * - ignores changes in model definition except name (ie. title, description, author, published date, version are treated as an incremental update)
     * 
     * TODO
     * - imports
     * - namespace
     * - datatypes
     * - constraints (including property constraints - references and inline)
     * 
     * @param model
     * @return model diffs (if any)
     */
    /* package */ List<M2ModelDiff> diffModel(CompiledModel previousVersion, CompiledModel model)
    {
        List<M2ModelDiff> M2ModelDiffs = new ArrayList<M2ModelDiff>();
        
        if (previousVersion != null)
        { 
            Collection<TypeDefinition> previousTypes = previousVersion.getTypes();
            Collection<AspectDefinition> previousAspects = previousVersion.getAspects();
            Collection<ConstraintDefinition> previousConDefs = getReferenceableConstraintDefs(previousVersion);
            
            if (model == null)
            {
                // delete model
                for (TypeDefinition previousType : previousTypes)
                {
                    M2ModelDiffs.add(new M2ModelDiff(previousType.getName(), M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_DELETED));
                }
                for (AspectDefinition previousAspect : previousAspects)
                {
                    M2ModelDiffs.add(new M2ModelDiff(previousAspect.getName(), M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_DELETED));
                }
                for (ConstraintDefinition previousConDef : previousConDefs)
                {
                    M2ModelDiffs.add(new M2ModelDiff(previousConDef.getName(), M2ModelDiff.TYPE_CONSTRAINT, M2ModelDiff.DIFF_DELETED));
                }
            }
            else
            {
                // update model
                Collection<TypeDefinition> types = model.getTypes();
                Collection<AspectDefinition> aspects = model.getAspects();
                Collection<ConstraintDefinition> conDefs = getReferenceableConstraintDefs(model);
                
                if (previousTypes.size() != 0)
                {
                    M2ModelDiffs.addAll(M2ClassDefinition.diffClassLists(new ArrayList<ClassDefinition>(previousTypes), new ArrayList<ClassDefinition>(types), M2ModelDiff.TYPE_TYPE));
                }
                else
                {
                    for (TypeDefinition type : types)
                    {
                        M2ModelDiffs.add(new M2ModelDiff(type.getName(), M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_CREATED));
                    }
                }
                
                if (previousAspects.size() != 0)
                {
                    M2ModelDiffs.addAll(M2ClassDefinition.diffClassLists(new ArrayList<ClassDefinition>(previousAspects), new ArrayList<ClassDefinition>(aspects), M2ModelDiff.TYPE_ASPECT));
                }
                else
                {
                    for (AspectDefinition aspect : aspects)
                    {
                        M2ModelDiffs.add(new M2ModelDiff(aspect.getName(), M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_CREATED));
                    }
                }
                
                if (previousConDefs.size() != 0)
                {
                    M2ModelDiffs.addAll(M2ConstraintDefinition.diffConstraintLists(new ArrayList<ConstraintDefinition>(previousConDefs), new ArrayList<ConstraintDefinition>(conDefs)));
                }
                else
                {
                    for (ConstraintDefinition conDef : conDefs)
                    {
                        M2ModelDiffs.add(new M2ModelDiff(conDef.getName(), M2ModelDiff.TYPE_CONSTRAINT, M2ModelDiff.DIFF_CREATED));
                    }
                }
            }
        }
        else
        {
            if (model != null)
            {
                // new model
                Collection<TypeDefinition> types = model.getTypes();
                Collection<AspectDefinition> aspects = model.getAspects();
                
                for (TypeDefinition type : types)
                {
                    M2ModelDiffs.add(new M2ModelDiff(type.getName(), M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_CREATED));
                }
                           
                for (AspectDefinition aspect : aspects)
                {
                    M2ModelDiffs.add(new M2ModelDiff(aspect.getName(), M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_CREATED));
                }  
            }
            else 
            {
                // nothing to diff
            }
        }
        
        return M2ModelDiffs;
    }
    

   
    
   
    public class DictionaryRegistry
    {
        private Map<String, List<CompiledModel>> uriToModels = new HashMap<String, List<CompiledModel>>(0);
        private Map<QName,CompiledModel> compiledModels = new HashMap<QName,CompiledModel>(0);
        
        private String tenantDomain;
        
        public DictionaryRegistry(String tenantDomain)
        {
            this.tenantDomain = tenantDomain;
        }
        
        public String getTenantDomain()
        {
            return tenantDomain;
        }
        
        public Map<String, List<CompiledModel>> getUriToModels()
        {
            return uriToModels;
        }
        public void setUriToModels(Map<String, List<CompiledModel>> uriToModels)
        {
            this.uriToModels = uriToModels;
        }
        public Map<QName, CompiledModel> getCompiledModels()
        {
            return compiledModels;
        }
        public void setCompiledModels(Map<QName, CompiledModel> compiledModels)
        {
            this.compiledModels = compiledModels;
        }
    }

    @Override
    public ClassLoader getResourceClassLoader()
    {
        return resourceClassLoader;
    }

    @Override
    public void setResourceClassLoader(ClassLoader resourceClassLoader)
    {
        this.resourceClassLoader = resourceClassLoader;
    }

}
