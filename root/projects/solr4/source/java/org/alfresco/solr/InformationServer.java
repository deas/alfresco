/*
 * Copyright (C) 2014 Alfresco Software Limited.
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
package org.alfresco.solr;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AlfrescoSolrDataModel.TenantAclIdDbId;
import org.alfresco.solr.adapters.IOpenBitSet;
import org.alfresco.solr.adapters.ISimpleOrderedMap;
import org.alfresco.solr.client.AclChangeSet;
import org.alfresco.solr.client.AclReaders;
import org.alfresco.solr.client.AlfrescoModel;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.client.Transaction;
import org.alfresco.solr.tracker.IndexHealthReport;
import org.alfresco.solr.tracker.TrackerStats;
import org.apache.solr.common.util.NamedList;
import org.json.JSONException;

/**
 * This is the interface to the information server, whether it be Solr or some other search server.
 * @author Ahmed Owian
 *
 */
public interface InformationServer
{
    public static final String PROP_PREFIX_PARENT_TYPE = "alfresco.metadata.ignore.datatype.";

    void rollback() throws IOException;

    void commit() throws IOException;

    void indexAclTransaction(AclChangeSet changeSet, boolean overwrite) throws IOException;

    void indexTransaction(Transaction info, boolean overwrite) throws IOException;

    void deleteByTransactionId(Long transactionId) throws IOException;

    void deleteByAclChangeSetId(Long aclChangeSetId) throws IOException;

    void deleteByAclId(Long aclId) throws IOException;

    void deleteByNodeId(Long nodeId) throws IOException;

    void indexNode(Node node, boolean overwrite) throws IOException, AuthenticationException, JSONException;
    
    void indexNodes(List<Node> nodes, boolean overwrite) throws IOException, AuthenticationException, JSONException;

    long indexAcl(List<AclReaders> aclReaderList, boolean overwrite) throws IOException;

    TrackerState getTrackerInitialState();

    int getTxDocsSize(String targetTxId, String targetTxCommitTime) throws IOException;

    int getRegisteredSearcherCount();

    boolean isInIndex(String ids) throws IOException;

    Set<Long> getErrorDocIds() throws IOException;

    Iterable<Map.Entry<String, Object>> getCoreStats() throws IOException;

    TrackerStats getTrackerStats();

    Map<String, Set<String>> getModelErrors();

    IOpenBitSet getOpenBitSetInstance();

    <T> ISimpleOrderedMap<T> getSimpleOrderedMapInstance();

    DictionaryComponent getDictionaryService(String alternativeDictionary);

    NamespaceDAO getNamespaceDAO();

    List<AlfrescoModel> getAlfrescoModels();

    void afterInitModels();

    boolean putModel(M2Model model);

    M2Model getM2Model(QName modelQName);

    long getHoleRetention();

    AclReport checkAclInIndex(Long aclid, AclReport aclReport);

    IndexHealthReport reportIndexTransactions(Long minTxId, IOpenBitSet txIdsInDb, long maxTxId) throws IOException;

    List<TenantAclIdDbId> getDocsWithUncleanContent(int start, int rows) throws IOException;

    void updateContentToIndexAndCache(long dbId, String tenant) throws Exception;

    void addCommonNodeReportInfo(NodeReport nodeReport);

    void addFTSStatusCounts(NamedList<Object> ihr);

    IndexHealthReport reportAclTransactionsInIndex(Long minAclTxId, IOpenBitSet aclTxIdsInDb, long maxAclTxId);

    int getAclTxDocsSize(String aclTxId, String aclTxCommitTime) throws IOException;
    
    AclChangeSet getMaxAclChangeSetIdAndCommitTimeInIndex();
    
    Transaction getMaxTransactionIdAndCommitTimeInIndex();
}
