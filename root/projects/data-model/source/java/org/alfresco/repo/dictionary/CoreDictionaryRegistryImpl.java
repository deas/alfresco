package org.alfresco.repo.dictionary;

import java.util.Map;

import org.alfresco.service.namespace.QName;

/**
 * Core dictionary registry (holding core models initialised at bootstrap).
 * 
 * @author sglover
 *
 */
public class CoreDictionaryRegistryImpl extends AbstractDictionaryRegistry
{
    public CoreDictionaryRegistryImpl(DictionaryDAO dictionaryDAO)
    {
    	super(dictionaryDAO);
    }

    @Override
    public String getTenantDomain()
    {
        return null;
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
            logger.info("Init core dictionary: model count = "+(models != null ? models.size() : 0)
            		+" in "+(System.currentTimeMillis()-startTime)+" msecs ["+Thread.currentThread()+"]");
        }
    }

	@Override
	public void remove()
	{
		for(DictionaryListener listener : dictionaryDAO.getDictionaryListeners())
		{
			listener.afterDictionaryDestroy();
		}
	}

	@Override
    protected QName putModelImpl(CompiledModel model)
	{
		// TODO disallow model overrides for the core dictionary

//		if(compiledModels.get(model.getModelDefinition().getName()) != null)
//		{
//			throw new AlfrescoRuntimeException("Cannot override existing model " + model.getModelDefinition().getName());
//		}
//
//		for(M2Namespace namespace : model.getM2Model().getNamespaces())
//		{
//			if(uriToModels.get(namespace.getUri()) != null)
//			{
//				throw new AlfrescoRuntimeException("Cannot override existing namespace " + namespace.getUri());
//			}
//		}

		QName qname = super.putModelImpl(model);

//		if(dictionaryDAO.isContextRefreshed())
//		{
//			for(DictionaryListener listener : dictionaryDAO.getDictionaryListeners())
//			{
//				if(listener instanceof ExtendedDictionaryListener)
//				{
//					((ExtendedDictionaryListener)listener).coreModelAdded(model);
//				}
//			}
//		}

        return qname;
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