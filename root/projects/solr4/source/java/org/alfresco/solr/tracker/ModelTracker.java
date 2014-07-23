
package org.alfresco.solr.tracker;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.service.cmr.dictionary.ModelDefinition.XMLBindingType;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.BoundedDeque;
import org.alfresco.solr.InformationServer;
import org.alfresco.solr.adapters.IOpenBitSet;
import org.alfresco.solr.client.AclChangeSet;
import org.alfresco.solr.client.AclChangeSets;
import org.alfresco.solr.client.AlfrescoModel;
import org.alfresco.solr.client.AlfrescoModelDiff;
import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.solr.client.Transaction;
import org.alfresco.solr.client.Transactions;
import org.json.JSONException;
import org.quartz.Scheduler;

public class ModelTracker extends AbstractTracker implements Tracker
{

    private Set<StoreRef> indexedStores = new HashSet<StoreRef>();
    private Set<StoreRef> ignoredStores = new HashSet<StoreRef>();
    private Set<String> indexedTenants = new HashSet<String>();
    private Set<String> ignoredTenants = new HashSet<String>();
    private Set<QName> indexedDataTypes = new HashSet<QName>();
    private Set<QName> ignoredDataTypes = new HashSet<QName>();
    private Set<QName> indexedTypes = new HashSet<QName>();
    private Set<QName> ignoredTypes = new HashSet<QName>();
    private Set<QName> indexedAspects = new HashSet<QName>();
    private Set<QName> ignoredAspects = new HashSet<QName>();
    private Set<String> indexedFields = new HashSet<String>();
    private Set<String> ignoredFields = new HashSet<String>();

    private ReentrantReadWriteLock modelLock = new ReentrantReadWriteLock();
    boolean hasModels = false;

    public ModelTracker(SolrTrackerScheduler scheduler, String id, Properties p, SOLRAPIClient client, 
                String coreName, InformationServer informationServer)
    {
        super(scheduler, id, p, client, coreName, informationServer);
    }
    

    /**
     * Default constructor, for testing.
     */
    ModelTracker()
    {
        // Testing purposes only
    }

    @Override
    public void doTrack() throws AuthenticationException, IOException, JSONException
    {
        // Is the InformationServer ready to update
        int registeredSearcherCount = this.infoSrv.getRegisteredSearcherCount();
        if (registeredSearcherCount >= getMaxLiveSearchers())
        {
            log.info(".... skipping tracking registered searcher count = " + registeredSearcherCount);
            return;
        }

        checkShutdown();
        trackModels(false);
    }

    @Override
    public IndexHealthReport checkIndex(Long fromTx, Long toTx, Long fromAclTx, Long toAclTx, Long fromTime, Long toTime) throws IOException, AuthenticationException, JSONException
    {
        IndexHealthReport indexHealthReport = new IndexHealthReport(infoSrv);
        Long minTxId = null;
        Long minAclTxId = null;

        // DB TX Count
        IOpenBitSet txIdsInDb = infoSrv.getOpenBitSetInstance();
        long maxTxId = 0;
        indexHealthReport.setDbTransactionCount(txIdsInDb.cardinality());
        IOpenBitSet aclTxIdsInDb = infoSrv.getOpenBitSetInstance();
        long maxAclTxId = 0;

        indexHealthReport.setDbAclTransactionCount(aclTxIdsInDb.cardinality());

        // Index TX Count
        return this.infoSrv.checkIndexTransactions(indexHealthReport, minTxId, minAclTxId, txIdsInDb, maxTxId, 
                    aclTxIdsInDb, maxAclTxId);
    }

    public void trackModels(boolean onlyFirstTime) throws AuthenticationException, IOException, JSONException
    {
        boolean requiresWriteLock = false;
        modelLock.readLock().lock();
        try
        {
            if (hasModels)
            {
                if (onlyFirstTime)
                {
                    return;
                }
                else
                {
                    requiresWriteLock = false;
                }
            }
            else
            {
                requiresWriteLock = true;
            }
        }
        finally
        {
            modelLock.readLock().unlock();
        }

        if (requiresWriteLock)
        {
            modelLock.writeLock().lock();
            try
            {
                if (hasModels)
                {
                    if (onlyFirstTime) { return; }
                }

                trackModelsImpl();
                hasModels = true;
            }
            finally
            {
                modelLock.writeLock().unlock();
            }
        }
        else
        {
            trackModelsImpl();
        }
    }

    public void ensureFirstModelSync()
    {
        try
        {
            trackModels(true);
        }
        catch (Throwable t)
        {
            log.error("Model tracking failed", t);
        }

    }

    /**
     * Tracks models.  Reflects changes and updates on disk copy
     * @throws AuthenticationException
     * @throws IOException
     * @throws JSONException
     */
    private void trackModelsImpl() throws AuthenticationException, IOException, JSONException
    {
        long start = System.nanoTime();

        List<AlfrescoModelDiff> modelDiffs = client.getModelsDiff(this.infoSrv.getAlfrescoModels());
        HashMap<String, M2Model> modelMap = new HashMap<String, M2Model>();

        for (AlfrescoModelDiff modelDiff : modelDiffs)
        {
            switch (modelDiff.getType())
            {
                case CHANGED:
                    AlfrescoModel changedModel = client.getModel(modelDiff.getModelName());
                    for (M2Namespace namespace : changedModel.getModel().getNamespaces())
                    {
                        modelMap.put(namespace.getUri(), changedModel.getModel());
                    }
                    break;
                case NEW:
                    AlfrescoModel newModel = client.getModel(modelDiff.getModelName());
                    for (M2Namespace namespace : newModel.getModel().getNamespaces())
                    {
                        modelMap.put(namespace.getUri(), newModel.getModel());
                    }
                    break;
                case REMOVED:
                    // At the moment we do not unload models - I can see no side effects ....
                    // However search is used to check for references to indexed properties or types
                    // This will be partially broken anyway due to eventual consistency
                    // A model should only be unloaded if there are no data dependencies
                    // Should have been on the de-lucene list.
                    break;
            }
        }

        HashSet<String> loadedModels = new HashSet<String>();
        for (M2Model model : modelMap.values())
        {
            loadModel(modelMap, loadedModels, model);
        }
        if (loadedModels.size() > 0)
        {
            this.infoSrv.afterInitModels();
        }

        File alfrescoModelDir = new File(id, "alfrescoModels");
        if (!alfrescoModelDir.exists())
        {
            alfrescoModelDir.mkdir();
        }
        for (AlfrescoModelDiff modelDiff : modelDiffs)
        {
            switch (modelDiff.getType())
            {
                case CHANGED:
                    removeMatchingModels(alfrescoModelDir, modelDiff.getModelName());
                    M2Model changedModel = this.infoSrv.getM2Model(modelDiff.getModelName());
                    File changedFile = new File(alfrescoModelDir, getModelFileName(changedModel));
                    FileOutputStream cos = new FileOutputStream(changedFile);
                    changedModel.toXML(cos);
                    cos.flush();
                    cos.close();
                    break;
                case NEW:
                    M2Model newModel = this.infoSrv.getM2Model(modelDiff.getModelName());
                    // add on file
                    File newFile = new File(alfrescoModelDir, getModelFileName(newModel));
                    FileOutputStream nos = new FileOutputStream(newFile);
                    newModel.toXML(nos);
                    nos.flush();
                    nos.close();
                    break;
                case REMOVED:
                    removeMatchingModels(alfrescoModelDir, modelDiff.getModelName());
                    break;
            }
        }

        long end = System.nanoTime();

        trackerStats.addModelTime(end - start);

        if (true == runPostModelLoadInit)
        {
            for (Object key : props.keySet())
            {
                String stringKey = (String) key;
                if (stringKey.startsWith("alfresco.index.store"))
                {
                    StoreRef store = new StoreRef(props.getProperty(stringKey));
                    indexedStores.add(store);
                }
                if (stringKey.startsWith("alfresco.ignore.store"))
                {
                    StoreRef store = new StoreRef(props.getProperty(stringKey));
                    ignoredStores.add(store);
                }
                if (stringKey.startsWith("alfresco.index.tenant"))
                {
                    indexedTenants.add(props.getProperty(stringKey));
                }
                if (stringKey.startsWith("alfresco.ignore.tenant"))
                {
                    ignoredTenants.add(props.getProperty(stringKey));
                }
                if (stringKey.startsWith("alfresco.index.datatype"))
                {
                    QName qname = expandQName(props.getProperty(stringKey));
                    indexedDataTypes.add(qname);
                }
                if (stringKey.startsWith("alfresco.ignore.datatype"))
                {
                    QName qname = expandQName(props.getProperty(stringKey));
                    ignoredDataTypes.add(qname);
                }
                if (stringKey.startsWith("alfresco.index.type"))
                {
                    QName qname = expandQName(props.getProperty(stringKey));
                    indexedTypes.add(qname);
                }
                if (stringKey.startsWith("alfresco.ignore.type"))
                {
                    QName qname = expandQName(props.getProperty(stringKey));
                    ignoredTypes.add(qname);
                }
                if (stringKey.startsWith("alfresco.index.aspect"))
                {
                    QName qname = expandQName(props.getProperty(stringKey));
                    indexedAspects.add(qname);
                }
                if (stringKey.startsWith("alfresco.ignore.aspect"))
                {
                    QName qname = expandQName(props.getProperty(stringKey));
                    ignoredAspects.add(qname);
                }
                if (stringKey.startsWith("alfresco.index.field"))
                {
                    String name = expandName(props.getProperty(stringKey));
                    indexedFields.add(name);
                }
                if (stringKey.startsWith("alfresco.ignore.field"))
                {
                    String name = expandName(props.getProperty(stringKey));
                    ignoredFields.add(name);
                }
            }
            runPostModelLoadInit = false;
        }
    }

    QName expandQName(String qName)
    {
        String expandedQName = qName;
        if (qName.startsWith("@"))
        {
            return expandQName(qName.substring(1));
        }
        else if (qName.startsWith("{"))
        {
            expandedQName = expandQNameImpl(qName);
        }
        else if (qName.contains(":"))
        {
            expandedQName = expandQNameImpl(qName);
        }
        // else if (AlfrescoSolrDataModel.nonDictionaryFields.get(qName) == null)
        // {
        // expandedQName = expandQNameImpl(qName);
        // }
        return QName.createQName(expandedQName);

    }

    String expandQNameImpl(String q)
    {
        String eq = q;
        // Check for any prefixes and expand to the full uri
        if (q.charAt(0) != '{')
        {
            int colonPosition = q.indexOf(':');
            if (colonPosition == -1)
            {
                // use the default namespace
                eq = "{" + this.infoSrv.getNamespaceDAO().getNamespaceURI("") + "}" + q;
            }
            else
            {
                // find the prefix
                eq = "{" + this.infoSrv.getNamespaceDAO().getNamespaceURI(q.substring(0, colonPosition)) + "}"
                            + q.substring(colonPosition + 1);
            }
        }
        return eq;
    }

    String expandName(String qName)
    {
        String expandedQName = qName;
        if (qName.startsWith("@"))
        {
            return expandName(qName.substring(1));
        }
        else if (qName.startsWith("{"))
        {
            expandedQName = expandQNameImpl(qName);
        }
        else if (qName.contains(":"))
        {
            expandedQName = expandQNameImpl(qName);
        }
        // else if (AlfrescoSolrDataModel.nonDictionaryFields.get(qName) == null)
        // {
        // expandedQName = expandQNameImpl(qName);
        // }
        return expandedQName;

    }

    /**
     * @param alfrescoModelDir
     * @param modelName
     */
    private void removeMatchingModels(File alfrescoModelDir, QName modelName)
    {

        final String prefix = modelName.toPrefixString(this.infoSrv.getNamespaceDAO()).replace(":", ".") + ".";
        final String postFix = ".xml";

        File[] toDelete = alfrescoModelDir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                if (pathname.isDirectory()) { return false; }
                String name = pathname.getName();
                if (false == name.endsWith(postFix)) { return false; }
                if (false == name.startsWith(prefix)) { return false; }
                // check is number between
                String checksum = name.substring(prefix.length(), name.length() - postFix.length());
                try
                {
                    Long.parseLong(checksum);
                    return true;
                }
                catch (NumberFormatException nfe)
                {
                    return false;
                }
            }
        });

        if (toDelete != null)
        {
            for (File file : toDelete)
            {
                file.delete();
            }
        }
    }

    private void loadModel(Map<String, M2Model> modelMap, HashSet<String> loadedModels, M2Model model)
    {
        String modelName = model.getName();
        if (loadedModels.contains(modelName) == false)
        {
            for (M2Namespace importNamespace : model.getImports())
            {
                M2Model importedModel = modelMap.get(importNamespace.getUri());
                if (importedModel != null)
                {

                    // Ensure that the imported model is loaded first
                    loadModel(modelMap, loadedModels, importedModel);
                }
            }

            if (this.infoSrv.putModel(model))
            {
                loadedModels.add(modelName);
            }
            log.info("Loading model " + model.getName());
        }
    }

    private String getModelFileName(M2Model model)
    {
        return model.getName().replace(":", ".") + "." + model.getChecksum(XMLBindingType.DEFAULT) + ".xml";
    }
}
