/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.opencmis.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.opencmis.mapping.CMISMapping;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.DictionaryListener;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CmisExtensionElementImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * Common CMIS Dictionary Support including registry of Types.
 * 
 * @author davidc
 * @author florian.mueller
 */
public abstract class CMISAbstractDictionaryService extends AbstractLifecycleBean implements CMISDictionaryService,
        DictionaryListener
{
    // Logger
    protected static final Log logger = LogFactory.getLog(CMISAbstractDictionaryService.class);

    public static final String ALFRESCO_EXTENSION_NAMESPACE = "http://www.alfresco.org";
    public static final String MANDATORY_ASPECTS = "mandatoryAspects";
    public static final String MANDATORY_ASPECT = "mandatoryAspect";

    // service dependencies
    private DictionaryDAO dictionaryDAO;
    protected DictionaryService dictionaryService;
    protected CMISMapping cmisMapping;
    protected PropertyAccessorMapping accessorMapping;
    protected PropertyLuceneBuilderMapping luceneBuilderMapping;

    /**
     * Set the mapping service
     * 
     * @param cmisMapping
     */
    public void setCmisMapping(CMISMapping cmisMapping)
    {
        this.cmisMapping = cmisMapping;
    }

    /**
     * Set the property accessor mapping service
     * 
     * @param accessor mapping
     */
    public void setPropertyAccessorMapping(PropertyAccessorMapping accessorMapping)
    {
        this.accessorMapping = accessorMapping;
    }

    /**
     * Set the property lucene mapping service
     * 
     * @param lucene mapping
     */
    public void setPropertyLuceneBuilderMapping(PropertyLuceneBuilderMapping luceneBuilderMapping)
    {
        this.luceneBuilderMapping = luceneBuilderMapping;
    }

    /**
     * Set the dictionary Service
     * 
     * @param dictionaryService
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Set the dictionary DAO
     * 
     * @param dictionaryDAO
     */
    public void setDictionaryDAO(DictionaryDAO dictionaryDAO)
    {
        this.dictionaryDAO = dictionaryDAO;
    }

    public void setSingletonCache(SimpleCache<String, DictionaryRegistry> singletonCache)
    {
        this.singletonCache = singletonCache;
    }
    
    private final ReentrantReadWriteLock registryLock = new ReentrantReadWriteLock();
    private final WriteLock registryWriteLock = registryLock.writeLock();
    private final ReadLock registryReadLock = registryLock.readLock();
    
    // note: cache is tenant-aware (if using TransctionalCache impl)
    private SimpleCache<String, DictionaryRegistry> singletonCache; // eg. for openCmisDictionaryRegistry
    private final String KEY_OPENCMIS_DICTIONARY_REGISTRY = "key.openCmisDictionaryRegistry";
    
    protected String key_opencmis_dictionary_registry = null;
    
    /**
     * CMIS Dictionary registry
     * 
     * Index of CMIS Type Definitions
     */
    public class DictionaryRegistry
    {
        // Type Definitions Index
        Map<QName, TypeDefinitionWrapper> typeDefsByQName = new HashMap<QName, TypeDefinitionWrapper>();
        Map<QName, TypeDefinitionWrapper> assocDefsByQName = new HashMap<QName, TypeDefinitionWrapper>();

        Map<String, AbstractTypeDefinitionWrapper> typeDefsByTypeId = new HashMap<String, AbstractTypeDefinitionWrapper>();
        Map<String, TypeDefinitionWrapper> typeDefsByQueryName = new HashMap<String, TypeDefinitionWrapper>();
        List<TypeDefinitionWrapper> baseTypes = new ArrayList<TypeDefinitionWrapper>();

        Map<String, PropertyDefinitionWrapper> propDefbyPropId = new HashMap<String, PropertyDefinitionWrapper>();
        Map<String, PropertyDefinitionWrapper> propDefbyQueryName = new HashMap<String, PropertyDefinitionWrapper>();

        /**
         * Register type definition.
         * 
         * @param typeDef
         */
        public void registerTypeDefinition(AbstractTypeDefinitionWrapper typeDef)
        {
            AbstractTypeDefinitionWrapper existingTypeDef = typeDefsByTypeId.get(typeDef.getTypeId());
            if (existingTypeDef != null)
            {
                throw new AlfrescoRuntimeException("Type " + typeDef.getTypeId() + " already registered");
            }

            typeDefsByTypeId.put(typeDef.getTypeId(), typeDef);
            QName typeQName = typeDef.getAlfrescoName();
            if (typeQName != null)
            {
                if ((typeDef instanceof RelationshipTypeDefintionWrapper) && !typeDef.isBaseType())
                {
                    assocDefsByQName.put(typeQName, typeDef);
                } else
                {
                    typeDefsByQName.put(typeQName, typeDef);
                }
            }

            typeDefsByQueryName.put(typeDef.getTypeDefinition(false).getQueryName(), typeDef);

            if (logger.isDebugEnabled())
            {
                logger.debug("Registered type " + typeDef.getTypeId() + " (scope=" + typeDef.getBaseTypeId() + ")");
                logger.debug(" QName: " + typeDef.getAlfrescoName());
                logger.debug(" Table: " + typeDef.getTypeDefinition(false).getQueryName());
                logger.debug(" Action Evaluators: " + typeDef.getActionEvaluators().size());
            }
        }

        /**
         * Register property definitions.
         * 
         * @param typeDef
         */
        public void registerPropertyDefinitions(AbstractTypeDefinitionWrapper typeDef)
        {
            for (PropertyDefinitionWrapper propDef : typeDef.getProperties())
            {
                if (propDef.getPropertyDefinition().isInherited())
                {
                    continue;
                }

                propDefbyPropId.put(propDef.getPropertyId(), propDef);
                propDefbyQueryName.put(propDef.getPropertyDefinition().getQueryName(), propDef);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("DictionaryRegistry[");
            builder.append("Types=").append(typeDefsByTypeId.size()).append(", ");
            builder.append("Base Types=").append(baseTypes.size()).append(", ");
            builder.append("]");
            return builder.toString();
        }
    }
    
    protected DictionaryRegistry getRegistry()
    {
        return getRegistryImpl();
    }
    
    protected DictionaryRegistry getRegistryImpl()
    {
        DictionaryRegistry registry = null;

        registryReadLock.lock();
        try
        {
            // Avoid NPE due to null cache key.
            if (key_opencmis_dictionary_registry != null)
            {
                registry = singletonCache.get(key_opencmis_dictionary_registry);
            }
        }
        finally
        {
            registryReadLock.unlock();
        }

        if (registry == null)
        {
            init();
            registry = singletonCache.get(key_opencmis_dictionary_registry);
        }
        return registry;
    }
    
    public TypeDefinitionWrapper findType(String typeId)
    {
        return getRegistry().typeDefsByTypeId.get(typeId);
    }
    
    public boolean isExcluded(QName qname)
    {
        return cmisMapping.isExcluded(qname);
    }

    public TypeDefinitionWrapper findTypeForClass(QName clazz, BaseTypeId... matchingScopes)
    {
        // searching for relationship
        boolean scopeByRelationship = false;
        for (BaseTypeId scope : matchingScopes)
        {
            if (scope == BaseTypeId.CMIS_RELATIONSHIP)
            {
                scopeByRelationship = true;
                break;
            }
        }

        // locate type in registry
        clazz = cmisMapping.getCmisType(clazz);
        TypeDefinitionWrapper typeDef = null;
        if (scopeByRelationship)
        {
            typeDef = getRegistry().assocDefsByQName.get(clazz);
        } else
        {
            typeDef = getRegistry().typeDefsByQName.get(clazz);
            if (typeDef == null)
            {
                typeDef = getRegistry().assocDefsByQName.get(clazz);
            }
        }

        // ensure matches one of provided matching scopes
        TypeDefinitionWrapper matchingTypeDef = (matchingScopes.length == 0) ? typeDef : null;
        if (typeDef != null)
        {
            for (BaseTypeId scope : matchingScopes)
            {
                if (typeDef.getBaseTypeId() == scope)
                {
                    matchingTypeDef = typeDef;
                    break;
                }
            }
        }

        return matchingTypeDef;
    }

    public TypeDefinitionWrapper findNodeType(QName clazz)
    {
        return getRegistry().typeDefsByQName.get(cmisMapping.getCmisType(clazz));
    }

    public TypeDefinitionWrapper findAssocType(QName clazz)
    {
        return getRegistry().assocDefsByQName.get(cmisMapping.getCmisType(clazz));
    }

    public TypeDefinitionWrapper findTypeByQueryName(String queryName)
    {
        // ISO 9075 name look up should be lower case.
        return getRegistry().typeDefsByQueryName.get(ISO9075.lowerCaseEncodedSQL(queryName));
    }

    public QName getAlfrescoClass(QName name)
    {
        return cmisMapping.getAlfrescoClass(name);
    }

    public PropertyDefinitionWrapper findProperty(String propId)
    {
        return getRegistry().propDefbyPropId.get(propId);
    }

    @Override
    public PropertyDefinitionWrapper findPropertyByQueryName(String queryName)
    {
        return getRegistry().propDefbyQueryName.get(ISO9075.lowerCaseEncodedSQL(queryName));
    }

    public List<TypeDefinitionWrapper> getBaseTypes()
    {
        return Collections.unmodifiableList(getRegistry().baseTypes);
    }

    public List<TypeDefinitionWrapper> getAllTypes()
    {
        return Collections.unmodifiableList(new ArrayList<TypeDefinitionWrapper>(getRegistry().typeDefsByTypeId
                .values()));
    }

    public PropertyType findDataType(QName dataType)
    {
        return cmisMapping.getDataType(dataType);
    }

    public QName findAlfrescoDataType(PropertyType propertyType)
    {
        return cmisMapping.getAlfrescoDataType(propertyType);
    }

    /**
     * Factory for creating CMIS Definitions
     * 
     * @param registry
     */
    abstract protected void createDefinitions(DictionaryRegistry registry);

    private void addTypeExtensions(DictionaryRegistry registry, TypeDefinitionWrapper td)
    {
        QName classQName = td.getAlfrescoClass();
        ClassDefinition classDef = dictionaryService.getClass(classQName);
        if(classDef != null)
        {
	        // add mandatory/default aspects
	        List<AspectDefinition> defaultAspects = classDef.getDefaultAspects(true);
	        if(defaultAspects != null && defaultAspects.size() > 0)
	        {
		        List<CmisExtensionElement> mandatoryAspectsExtensions = new ArrayList<CmisExtensionElement>();
		        for(AspectDefinition aspectDef : defaultAspects)
		        {
		        	QName aspectQName = aspectDef.getName();
		        	
		        	TypeDefinitionWrapper aspectType = registry.typeDefsByQName.get(cmisMapping.getCmisType(aspectQName));
		            if (aspectType == null)
		            {
		                continue;
		            }
	
		        	mandatoryAspectsExtensions.add(new CmisExtensionElementImpl(ALFRESCO_EXTENSION_NAMESPACE, MANDATORY_ASPECT, null, aspectType.getTypeId()));
		        }
	
	            if(!mandatoryAspectsExtensions.isEmpty())
	            {
	                td.getTypeDefinition(true).setExtensions(
	                        Collections.singletonList((CmisExtensionElement) new CmisExtensionElementImpl(
	                                ALFRESCO_EXTENSION_NAMESPACE, MANDATORY_ASPECTS, null, mandatoryAspectsExtensions)));
	            }
	        }
        }
    }

    /**
     * Dictionary Initialization - creates a new registry
     */
    protected void init()
    {
        registryWriteLock.lock();
        try
        {
        	this.key_opencmis_dictionary_registry = KEY_OPENCMIS_DICTIONARY_REGISTRY + "." + cmisMapping.getCmisVersion().toString();
            DictionaryRegistry registry = new DictionaryRegistry();
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Creating type definitions...");
            }
            
            // phase 1: construct type definitions and link them together
            createDefinitions(registry);
            for (AbstractTypeDefinitionWrapper objectTypeDef : registry.typeDefsByTypeId.values())
            {
                objectTypeDef.connectParentAndSubTypes(cmisMapping, registry, dictionaryService);
            }
            
            // phase 2: register base types and inherit property definitions
            for (AbstractTypeDefinitionWrapper typeDef : registry.typeDefsByTypeId.values())
            {
                if (typeDef.getTypeDefinition(false).getParentTypeId() == null)
                {
                    registry.baseTypes.add(typeDef);
                    typeDef.resolveInheritance(cmisMapping, registry, dictionaryService);
                }
            }
            
            // phase 3: register properties
            for (AbstractTypeDefinitionWrapper typeDef : registry.typeDefsByTypeId.values())
            {
                registry.registerPropertyDefinitions(typeDef);
            }
            
            // phase 4: assert valid
            for (AbstractTypeDefinitionWrapper typeDef : registry.typeDefsByTypeId.values())
            {
                typeDef.assertComplete();
                
                addTypeExtensions(registry, typeDef);
            }
            
            // publish new registry
            singletonCache.put(key_opencmis_dictionary_registry, registry);
            
            if (logger.isInfoEnabled())
                logger.info("Initialized CMIS Dictionary. Types:" + registry.typeDefsByTypeId.size() + ", Base Types:"
                        + registry.baseTypes.size());
        }
        finally
        {
            registryWriteLock.unlock();
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.dictionary.DictionaryListener#onInit()
     */
    public void onDictionaryInit()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.dictionary.DictionaryListener#afterInit()
     */
    public void afterDictionaryInit()
    {
        init();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.alfresco.repo.dictionary.DictionaryListener#afterDictionaryDestroy()
     */
    public void afterDictionaryDestroy()
    {
        registryWriteLock.lock();
        try
        {
            singletonCache.remove(key_opencmis_dictionary_registry);
        }
        finally
        {
            registryWriteLock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.extensions.surf.util.AbstractLifecycleBean#onBootstrap
     * (org.springframework.context.ApplicationEvent)
     */
    protected void onBootstrap(ApplicationEvent event)
    {
        afterDictionaryInit();

        // TODO revisit (for KS and/or 1.1)
        if (dictionaryDAO != null)
        {
            dictionaryDAO.register(this);
        }
        else
        {
            logger.error("DictionaryDAO is null - hence CMIS Dictionary not registered for updates");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.extensions.surf.util.AbstractLifecycleBean#onShutdown
     * (org.springframework.context.ApplicationEvent)
     */
    protected void onShutdown(ApplicationEvent event)
    {
    }
    
    public String getCmisTypeId(QName classQName)
    {
        return cmisMapping.getCmisTypeId(classQName);
    }

}
