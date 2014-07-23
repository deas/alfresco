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

package org.alfresco.solr.tracker;

import java.io.IOException;
import java.util.List;

import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AclReport;
import org.alfresco.solr.NodeReport;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.client.NodeMetaData;
import org.alfresco.solr.client.NodeMetaDataParameters;
import org.alfresco.solr.client.SOLRAPIClient.GetTextContentResponse;
import org.json.JSONException;

public interface Tracker
{

    int getMaxLiveSearchers();

    void updateIndex();

    void indexAclChangeSets() throws AuthenticationException, IOException, JSONException;

    void indexAcls() throws AuthenticationException, IOException, JSONException;

    void reindexAclChangeSets() throws AuthenticationException, IOException, JSONException;

    void reindexAcls() throws AuthenticationException, IOException, JSONException;

    void purgeAclChangeSets() throws AuthenticationException, IOException, JSONException;

    void purgeAcls() throws AuthenticationException, IOException, JSONException;

    void addTransactionToReindex(Long transactionToReindex);

    void addTransactionToIndex(Long transactionToIndex);

    void addTransactionToPurge(Long transactionToPurge);

    void addNodeToReindex(Long nodeToReindex);

    void addNodeToIndex(Long nodeToIndex);

    void addNodeToPurge(Long nodeToPurge);

    void addAclChangeSetToReindex(Long aclChangeSetToReindex);

    void addAclChangeSetToIndex(Long aclChangeSetToIndex);

    void addAclChangeSetToPurge(Long aclChangeSetToPurge);

    void addAclToReindex(Long aclToReindex);

    void addAclToIndex(Long aclToIndex);

    void addAclToPurge(Long aclToPurge);

    void trackRepository() throws IOException, AuthenticationException, JSONException;

    IndexHealthReport checkIndex(Long fromTx, Long toTx, Long fromAclTx, Long toAclTx, Long fromTime,
                Long toTime) throws AuthenticationException, IOException, JSONException;

    NodeReport checkNode(Node node);

    NodeReport checkNode(Long dbid);

    List<Node> getFullNodesForDbTransaction(Long txid);

    List<Long> getAclsForDbAclTransaction(Long acltxid);

    AclReport checkAcl(Long aclid);

    void close();

    void trackModels(boolean onlyFirstTime) throws AuthenticationException, IOException, JSONException;

    void ensureFirstModelSync();

    void setShutdown(boolean shutdown);

    List<NodeMetaData> getNodesMetaData(NodeMetaDataParameters params, int maxResults)
                throws AuthenticationException, IOException, JSONException;

    GetTextContentResponse getTextContent(Long nodeId, QName propertyQName, Long modifiedSince)
                throws AuthenticationException, IOException;

    boolean canAddContentPropertyToDoc();

    TrackerStats getTrackerStats();

    String getAlfrescoVersion();

}