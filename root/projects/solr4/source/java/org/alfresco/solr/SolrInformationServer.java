package org.alfresco.solr;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;

import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.adapters.IOpenBitSet;
import org.alfresco.solr.adapters.ISimpleOrderedMap;
import org.alfresco.solr.client.AclChangeSet;
import org.alfresco.solr.client.AclReaders;
import org.alfresco.solr.client.AlfrescoModel;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.client.Transaction;
import org.alfresco.solr.tracker.IndexHealthReport;
import org.alfresco.solr.tracker.Tracker;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.json.JSONException;

/**
 * This is the Solr4 implementation of the information server (index).
 * @author Ahmed Owian
 */
public class SolrInformationServer implements InformationServer
{

    private AlfrescoCoreAdminHandler adminHandler;
    private SolrCore core;
    private TrackerState trackerState = new TrackerState();
    private AlfrescoSolrDataModel dataModel;
    private String alfrescoVersion;
    private int authorityCacheSize;
    private int filterCacheSize;
    private int pathCacheSize;
    private boolean transformContent = true;
    private long lag;
    private long holeRetention;

    public SolrInformationServer(AlfrescoCoreAdminHandler adminHandler, SolrCore core)
    {
        this.adminHandler = adminHandler;
        this.core = core;

        Properties p = core.getResourceLoader().getCoreProperties();
        alfrescoVersion = p.getProperty("alfresco.version", "4.2.2");
        authorityCacheSize = Integer.parseInt(p.getProperty("solr.authorityCache.size", "64"));
        filterCacheSize = Integer.parseInt(p.getProperty("solr.filterCache.size", "64"));
        pathCacheSize = Integer.parseInt(p.getProperty("solr.pathCache.size", "64"));
        transformContent = Boolean.parseBoolean(p.getProperty("alfresco.index.transformContent", "true"));
        lag = Integer.parseInt(p.getProperty("alfresco.lag", "1000"));
        holeRetention = Integer.parseInt(p.getProperty("alfresco.hole.retention", "3600000"));
        
        dataModel = AlfrescoSolrDataModel.getInstance();
    }

    public String getAlfrescoVersion()
    {
        return this.alfrescoVersion;
    }
    
    @Override
    public void afterInitModels()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public AclReport checkAclInIndex(Long arg0, AclReport arg1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void checkCache() throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public IndexHealthReport checkIndexTransactions(IndexHealthReport arg0, Long arg1, Long arg2, IOpenBitSet arg3,
                long arg4, IOpenBitSet arg5, long arg6) throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NodeReport checkNodeCommon(NodeReport arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void commit() throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteByAclChangeSetId(Long arg0) throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteByAclId(Long arg0) throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteByNodeId(Long arg0) throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteByTransactionId(Long arg0) throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public List<AlfrescoModel> getAlfrescoModels()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<Entry<String, Object>> getCoreStats() throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DictionaryComponent getDictionaryService(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getDocSetSize(String arg0, String arg1) throws IOException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Set<Long> getErrorDocIds() throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getHoleRetention()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public M2Model getM2Model(QName arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Set<String>> getModelErrors()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NamespaceDAO getNamespaceDAO()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IOpenBitSet getOpenBitSetInstance()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRegisteredSearcherCount()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <T> ISimpleOrderedMap<T> getSimpleOrderedMapInstance()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tracker getTracker()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TrackerState getTrackerInitialState() throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TrackerState getTrackerState()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long indexAcl(List<AclReaders> arg0, boolean arg1) throws IOException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void indexAclTransaction(AclChangeSet arg0, boolean arg1) throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void indexNode(Node arg0, boolean arg1) throws IOException, AuthenticationException, JSONException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void indexTransaction(Transaction arg0, boolean arg1) throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isInIndex(String arg0, long arg1) throws IOException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean putModel(M2Model arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void rollback() throws IOException
    {
        // TODO Auto-generated method stub

    }

}
