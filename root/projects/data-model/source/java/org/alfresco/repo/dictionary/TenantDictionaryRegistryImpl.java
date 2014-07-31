package org.alfresco.repo.dictionary;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.QName;

/**
 * Tenant-specific dictionary registry.
 * 
 * @author sglover
 *
 */
public class TenantDictionaryRegistryImpl extends AbstractDictionaryRegistry
{
    private String tenantDomain;

    public TenantDictionaryRegistryImpl(DictionaryDAO dictionaryDAO, String tenantDomain)
    {
    	super(dictionaryDAO);
        this.tenantDomain = tenantDomain;
    }

    public String getTenantDomain()
    {
        return tenantDomain;
    }

    private DictionaryRegistry getParent()
    {
    	return dictionaryDAO.getDictionaryRegistry("");
    }

	@Override
	public QName putModelImpl(CompiledModel model)
	{
		if(getParent().modelExists(model.getModelDefinition().getName()))
		{
			throw new IllegalArgumentException("Cannot change core model: " + model);
		}
		QName ret = super.putModelImpl(model);

		return ret;
	}

	@Override
	public Map<QName, CompiledModel> getCompiledModels(boolean includeInherited)
	{
		Map<QName, CompiledModel> ret = new HashMap<QName, CompiledModel>();
		ret.putAll(super.getCompiledModels(includeInherited));
		if(includeInherited)
		{
			ret.putAll(getParent().getCompiledModels(includeInherited));
		}
		return ret;
	}

	@Override
    public List<CompiledModel> getModelsForUri(String uri)
    {
    	List<CompiledModel> models = new LinkedList<>();
    	List<CompiledModel> parentModels = getParent().getModelsForUri(uri);
    	models.addAll(parentModels);
    	models.addAll(getModelsForUriImpl(uri));
        return models;
    }

	@Override
	public CompiledModel getModel(QName name)
	{
		CompiledModel model = getModelImpl(name);
		if(model == null)
		{
			// try parent
			model = getParent().getModel(name);
		}
		return model;
	}

	@Override
	public void removeModel(QName modelName)
	{
		CompiledModel model = removeModelImpl(modelName);
		if(model == null)
		{
			// try parent
			getParent().removeModel(modelName);
		}
	}
	
	@Override
    public AspectDefinition getAspect(QName aspectName)
    {
		AspectDefinition aspect = getAspectImpl(aspectName);
		if(aspect == null)
		{
			// try parent
			aspect = getParent().getAspect(aspectName);
		}

		return aspect;
    }

    @Override
    public boolean isModelInherited(QName modelName)
    {
    	return (getParent().getModel(modelName) != null);
    }

	@Override
    public Map<String, String> getPrefixesCache()
    {
    	Map<String, String> prefixesCache = new HashMap<String, String>();

    	Map<String, String> parentPrefixes = getParent().getPrefixesCache();
    	prefixesCache.putAll(parentPrefixes);
    	prefixesCache.putAll(getPrefixesCacheImpl());

        return prefixesCache;
    }

	@Override
    public List<String> getUrisCache()
    {
		List<String> urisCache = new LinkedList<String>();

    	List<String> parentUris = getParent().getUrisCache();
    	urisCache.addAll(parentUris);
    	urisCache.addAll(getUrisCacheImpl());

        return urisCache;
    }

    @Override
    public Collection<String> getPrefixes(String URI)
    {
    	Collection<String> prefixes = getParent().getPrefixes(URI);
    	prefixes.addAll(getPrefixesImpl(URI));
    	return prefixes;
    }

    @Override
    public void addURI(String uri)
    {
    	if(getParent().hasURI(uri))
    	{
    		throw new NamespaceException("URI " + uri + " has already been defined");
    	}
    	addURIImpl(uri);
    }
    
    @Override
    public void addPrefix(String prefix, String uri)
    {
    	if(getParent().hasPrefix(prefix))
    	{
    		throw new NamespaceException("Prefix " + prefix + " has already been defined");
    	}
    	addPrefixImpl(prefix, uri);
    }

    @Override
    public void removeURI(String uri)
    {
        if(!removeURIImpl(uri))
        {
        	// try parent
        	getParent().removeURI(uri);
        }
    }
    
    @Override
    public void removePrefix(String prefix)
    {
        if(!removePrefixImpl(prefix))
        {
        	// try parent
        	getParent().removePrefix(prefix);
        }
    }

    @Override
    protected void initImpl()
    {
    	long startTime = System.currentTimeMillis();

        // populate the dictionary based on registered sources (only for core registry)
        for (DictionaryListener dictionaryDeployer : dictionaryDAO.getDictionaryListeners())
        {
            dictionaryDeployer.onDictionaryInit();
        }

        // notify registered listeners that dictionary has been initialised (population is complete)
        for (DictionaryListener dictionaryListener : dictionaryDAO.getDictionaryListeners())
        {
            dictionaryListener.afterDictionaryInit();
        }

        // Done
        if (logger.isInfoEnabled())
        {
        	Map<QName, CompiledModel> models = getCompiledModels(false);
            logger.info("Init Tenant Dictionary: model count = "+(models != null ? models.size() : 0)
            		+" in "+(System.currentTimeMillis()-startTime)+" msecs ["+Thread.currentThread()+"]");
        }
    }

    @Override
    public TypeDefinition getType(QName typeName)
    {
    	TypeDefinition type = getTypeImpl(typeName);
    	if(type == null)
    	{
    		// try parent
    		type = getParent().getType(typeName);
    	}
    	return type;
    }

	@Override 
	public void removeImpl()
	{
		for(DictionaryListener listener : dictionaryDAO.getDictionaryListeners())
		{
			listener.afterDictionaryDestroy();
		}
	}
}