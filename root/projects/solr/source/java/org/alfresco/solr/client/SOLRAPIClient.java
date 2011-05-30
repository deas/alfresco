/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.solr.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.Path.AttributeElement;
import org.alfresco.service.cmr.repository.Path.ChildAssocElement;
import org.alfresco.service.cmr.repository.datatype.TypeConversionException;
import org.alfresco.service.cmr.repository.datatype.TypeConverter;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// TODO error handling
// TODO get text content transform status handling
public class SOLRAPIClient extends AlfrescoHttpClient
{
    private static final Log logger = LogFactory.getLog(SOLRAPIClient.class);
    private static final String GET_TRANSACTIONS_URL = "api/solr/transactions";
    private static final String GET_METADATA_URL = "api/solr/metadata";
    private static final String GET_NODES_URL = "api/solr/nodes";
    private static final String GET_CONTENT = "api/solr/textContent";

    private SOLRDeserializer deserializer;

    public SOLRAPIClient(NamespaceDAO namespaceDAO, String alfrescoURL, String username, String password)
    {
        super(alfrescoURL + (alfrescoURL.endsWith("/") ? "" : "/"), username, password);
        deserializer = new SOLRDeserializer(namespaceDAO);
    }

    public List<Transaction> getTransactions(Long fromCommitTime, Long minTxnId, int maxResults) throws IOException, JSONException
    {
        setupHttpClient();

        StringBuilder url = new StringBuilder(this.url);
        url.append(GET_TRANSACTIONS_URL);
        StringBuilder args = new StringBuilder();
        if(fromCommitTime != null)
        {
            args.append("?");
            args.append("fromCommitTime");
            args.append("=");
            args.append(fromCommitTime);            
        }
        if(minTxnId != null)
        {
            if(args.length() == 0)
            {
                args.append("?");
            }
            else
            {
                args.append("&");
            }
            args.append("minTxnId");
            args.append("=");
            args.append(minTxnId);            
        }
        if(maxResults != 0 && maxResults != Integer.MAX_VALUE)
        {
            if(args.length() == 0)
            {
                args.append("?");
            }
            else
            {
                args.append("&");
            }
            args.append("maxResults");
            args.append("=");
            args.append(maxResults);            
        }
        url.append(args);
        
        GetRequest req = new GetRequest(url.toString());
        Response response = sendRequest(req, username);

        if(response.getStatus() != HttpStatus.SC_OK)
        {
            throw new AlfrescoRuntimeException("GetTransactions return status is " + response.getStatus());
        }

        if(logger.isDebugEnabled())
        {
            logger.debug(response.getContentAsString());
        }
        
        JSONObject json = new JSONObject(response.getContentAsString());

        JSONArray jsonTransactions = json.getJSONArray("transactions");
        int numTxns = jsonTransactions.length();
        List<Transaction> transactions = new ArrayList<Transaction>(numTxns);
        for(int i = 0; i < numTxns; i++)
        {
            JSONObject solrTxn = jsonTransactions.getJSONObject(i);
            Transaction txn = new Transaction();
            txn.setId(solrTxn.getLong("id"));
            txn.setCommitTimeMs(solrTxn.getLong("commitTimeMs"));
            txn.setUpdates(solrTxn.getLong("updates"));
            txn.setDeletes(solrTxn.getLong("deletes"));
            transactions.add(txn);
        }

        return transactions;
    }
    
    public List<Node> getNodes(GetNodesParameters parameters, int maxResults) throws IOException, JSONException
    {
        setupHttpClient();

        StringBuilder url = new StringBuilder(this.url);
        url.append(GET_NODES_URL);

        JSONObject body = new JSONObject();
        
        if(parameters.getTransactionIds() != null)
        {
            JSONArray jsonTxnIds = new JSONArray();
            for(Long txnId : parameters.getTransactionIds())
            {
                jsonTxnIds.put(txnId);
            }
            body.put("txnIds", jsonTxnIds);
        }
    
        if(parameters.getFromNodeId() != null)
        {
            body.put("fromNodeId", parameters.getFromNodeId());
        }
        if(parameters.getToNodeId() != null)
        {
            body.put("toNodeId", parameters.getToNodeId());
        }
        if(parameters.getExcludeAspects() != null)
        {
            JSONArray jsonExcludeAspects = new JSONArray();
            for(QName excludeAspect : parameters.getExcludeAspects())
            {
                jsonExcludeAspects.put(excludeAspect.toString());
            }
            body.put("excludeAspects", jsonExcludeAspects);
        }
        if(parameters.getIncludeAspects() != null)
        {
            JSONArray jsonIncludeAspects = new JSONArray();
            for(QName includeAspect : parameters.getIncludeAspects())
            {
                jsonIncludeAspects.put(includeAspect.toString());
            }
            body.put("includeAspects", jsonIncludeAspects);
        }

        if(parameters.getStoreProtocol() != null)
        {
            body.put("storeProtocol", parameters.getStoreProtocol());
        }

        if(parameters.getStoreIdentifier() != null)
        {
            body.put("storeIdentifier", parameters.getStoreIdentifier());
        }
        
        body.put("maxResults", maxResults);

        PostRequest req = new PostRequest(url.toString(), body.toString(), "application/json");
 
        Response response = sendRequest(req, username);

        if(response.getStatus() != HttpStatus.SC_OK)
        {
            throw new AlfrescoRuntimeException("GetNodes return status is " + response.getStatus());
        }

        if(logger.isDebugEnabled())
        {
            logger.debug(response.getContentAsString());
        }
        //System.out.println("getNodes: " + response.getContentAsString());
        JSONObject json = new JSONObject(response.getContentAsString());
        json.write(new PrintWriter(System.out));

        JSONArray jsonNodes = json.getJSONArray("nodes");
        List<Node> nodes = new ArrayList<Node>(jsonNodes.length());
        for(int i = 0; i < jsonNodes.length(); i++)
        {
            JSONObject jsonNodeInfo = jsonNodes.getJSONObject(i);
            Node nodeInfo = new Node();
            if(jsonNodeInfo.has("id"))
            {
                nodeInfo.setId(jsonNodeInfo.getLong("id"));
            }
            
            if(jsonNodeInfo.has("txnId"))
            {
                nodeInfo.setTxnId(jsonNodeInfo.getLong("txnId"));
            }

            if(jsonNodeInfo.has("status"))
            {
                Node.STATUS status;
                String statusStr = jsonNodeInfo.getString("status");
                if(statusStr.equals("u"))
                {
                    status = Node.STATUS.UPDATED;
                }
                else if(statusStr.equals("d"))
                {
                    status = Node.STATUS.DELETED;
                }
                else
                {
                    status = Node.STATUS.UNKNOWN;
                }
                nodeInfo.setStatus(status);
            }
            
            nodes.add(nodeInfo);
        }

        return nodes;
    }
    
    public List<NodeMetaData> getNodesMetaData(NodeMetaDataParameters params, int maxResults) throws IOException, JSONException
    {
        setupHttpClient();

        List<Long> nodeIds = params.getNodeIds();
        
        StringBuilder url = new StringBuilder(this.url);
        url.append(GET_METADATA_URL);

        JSONObject body = new JSONObject();
        if(nodeIds != null && nodeIds.size() > 0)
        {
            JSONArray jsonNodeIds = new JSONArray();
            for(Long nodeId : nodeIds)
            {
                jsonNodeIds.put(nodeId);
            }
            body.put("nodeIds", jsonNodeIds);

        }
        if(params.getFromNodeId() != null)
        {
            body.put("fromNodeId", params.getFromNodeId());
        }
        if(params.getToNodeId() != null)
        {
            body.put("toNodeId", params.getToNodeId());
        }
        
        // only need to set in cases where we don't want them in the response
        // because they default to true
        if(!params.isIncludeAclId())
        {
            body.put("includeAclId", params.isIncludeAclId());
        }
        if(!params.isIncludeAspects())
        {
            body.put("includeAspects", params.isIncludeAspects());
        }
        if(!params.isIncludeProperties())
        {
            body.put("includeProperties", params.isIncludeProperties());
        }
        if(!params.isIncludeAssociations())
        {
            body.put("includeAssociations", params.isIncludeAssociations());
        }
        if(!params.isIncludePaths())
        {
            body.put("includePaths", params.isIncludePaths());
        }
        if(!params.isIncludeOwner())
        {
            body.put("includeOwner", params.isIncludeOwner());
        }
        if(!params.isIncludeNodeRef())
        {
            body.put("includeNodeRef", params.isIncludeNodeRef());
        }

        body.put("maxResults", maxResults);

        PostRequest req = new PostRequest(url.toString(), body.toString(), "application/json");
        Response response = sendRequest(req, username);
        
        if(response.getStatus() != HttpStatus.SC_OK)
        {
            throw new AlfrescoRuntimeException("GetNodeMetaData return status is " + response.getStatus());
        }
        
        if(logger.isDebugEnabled())
        {
            logger.debug("nodesMetaData = " + response.getContentAsString());
        }

        String text = response.getContentAsString();
        JSONObject json = new JSONObject(text);

        JSONArray jsonNodes = json.getJSONArray("nodes");
        
        List<NodeMetaData> nodes = new ArrayList<NodeMetaData>(jsonNodes.length());
        for(int i = 0; i < jsonNodes.length(); i++)
        {
            JSONObject jsonNodeInfo = jsonNodes.getJSONObject(i);
            NodeMetaData metaData = new NodeMetaData();

            if(jsonNodeInfo.has("id"))
            {
                metaData.setId(jsonNodeInfo.getLong("id"));
            }
            
            if(jsonNodeInfo.has("aclId"))
            {
                metaData.setAclId(jsonNodeInfo.getLong("aclId"));
            }

            if(jsonNodeInfo.has("nodeRef"))
            {
                metaData.setNodeRef(new NodeRef(jsonNodeInfo.getString("nodeRef")));
            }
            
            if(jsonNodeInfo.has("type"))
            {
                metaData.setType(deserializer.deserializeValue(QName.class, jsonNodeInfo.getString("type")));
            }
            
            if(jsonNodeInfo.has("aspects"))
            {
                JSONArray jsonAspects = jsonNodeInfo.getJSONArray("aspects");
                Set<QName> aspects = new HashSet<QName>(jsonAspects.length());
                for(int j = 0; j < jsonAspects.length(); j++)
                {
                    String jsonAspect = (String)jsonAspects.get(j);
                    aspects.add(deserializer.deserializeValue(QName.class, jsonAspect));
                }
                metaData.setAspects(aspects);
            }

            if(jsonNodeInfo.has("paths"))
            {
                JSONArray jsonPaths = jsonNodeInfo.getJSONArray("paths");
                List<String> paths = new ArrayList<String>(jsonPaths.length());
                for(int j = 0; j < jsonPaths.length(); j++)
                {
                    String path = jsonPaths.getString(j);
                    paths.add(path);
                }
                metaData.setPaths(paths);
            }

            if(jsonNodeInfo.has("properties"))
            {
                JSONObject jsonProperties = jsonNodeInfo.getJSONObject("properties");
                Map<QName, String> properties = new HashMap<QName, String>(jsonProperties.length());
                @SuppressWarnings("rawtypes")
                Iterator propKeysIterator = jsonProperties.keys();
                while(propKeysIterator.hasNext())
                {
                    String propName = (String)propKeysIterator.next();
                    QName propQName = deserializer.deserializeValue(QName.class, propName);
                    Object propValueObj = jsonProperties.get(propName);
                    // property value is either a JSONArray (for multi properties) or a String
                    if(propValueObj instanceof JSONArray)
                    {
                        JSONArray array = (JSONArray)propValueObj;
                        for(int j = 0; j < array.length(); j++)
                        {
                            String propValue = array.getString(j);
                            properties.put(propQName, propValue);
                        }
                    }
                    else
                    {
                        properties.put(propQName, (String)propValueObj);
                    }
                }
                metaData.setProperties(properties);
            }
            nodes.add(metaData);
        }

        return nodes;
    }
    
    public GetTextContentResponse getTextContent(Long nodeId, QName propertyName, Long modifiedSince) throws IOException
    {
        setupHttpClient();

        StringBuilder url = new StringBuilder(this.url);
        url.append(GET_CONTENT);
        if(nodeId != null)
        {
            url.append("/");
            url.append(String.valueOf(nodeId));
        }
        if(propertyName != null)
        {
            if(url.charAt(url.length() - 1) != '/')
            {
                url.append("/");
            }
            // TODO encode
            URLCodec encoder = new URLCodec();
            url.append(encoder.encode(propertyName.toString(), "UTF-8"));
        }
        
        GetRequest req = new GetRequest(url.toString());
        Map<String, String> headers = new HashMap<String, String>(2);
        
        if(modifiedSince != null)
        {
            headers.put("If-Modified-Since", String.valueOf(DateUtil.formatDate(new Date(modifiedSince))));
        }
//        headers.put("If-None-Match",  String entityTag);
        req.setHeaders(headers);
        
        Response response = sendRequest(req, username);
        
        if(response.getStatus() != Status.STATUS_NOT_MODIFIED && response.getStatus() != Status.STATUS_NO_CONTENT && response.getStatus() != Status.STATUS_OK)
        {
            throw new AlfrescoRuntimeException("GetNodeMetaData return status is " + response.getStatus());
        }

        return new GetTextContentResponse(response);
    }
    
    /*
     * type conversions from serialized JSON values to SOLR-consumable objects 
     */
    private class SOLRTypeConverter
    {
        /**
         * Default Type Converter
         */
        private TypeConverter instance = new TypeConverter();
        private NamespaceDAO namespaceDAO;
        
        SOLRTypeConverter(NamespaceDAO namespaceDAO)
        {
            this.namespaceDAO = namespaceDAO;

            // dates
            instance.addConverter(String.class, Date.class, new TypeConverter.Converter<String, Date>()
            {
                public Date convert(String source)
                {
                    try
                    {
                        Date date = ISO8601DateFormat.parse(source);
                        return date;
                    }
                    catch (Exception e)
                    {
                        throw new TypeConversionException("Failed to convert date " + source + " to string", e);
                    }
                }
            });
                    
            // node refs        
            instance.addConverter(String.class, NodeRef.class, new TypeConverter.Converter<String, NodeRef>()
            {
                public NodeRef convert(String source)
                {
                    return new NodeRef(source);
                }
            });
            
            // paths
            instance.addConverter(String.class, AttributeElement.class, new TypeConverter.Converter<String, AttributeElement>()
            {
                public AttributeElement convert(String source)
                {
                    return new Path.AttributeElement(source);
                }
            });
            
            instance.addConverter(String.class, ChildAssocElement.class, new TypeConverter.Converter<String, ChildAssocElement>()
            {
                public ChildAssocElement convert(String source)
                {
                    return new Path.ChildAssocElement(instance.convert(ChildAssociationRef.class, source));
                }
            });
            
            instance.addConverter(String.class, Path.DescendentOrSelfElement.class, new TypeConverter.Converter<String, Path.DescendentOrSelfElement>()
            {
                public Path.DescendentOrSelfElement convert(String source)
                {
                    return new Path.DescendentOrSelfElement();
                }
            });
            
            instance.addConverter(String.class, Path.ParentElement.class, new TypeConverter.Converter<String, Path.ParentElement>()
            {
                public Path.ParentElement convert(String source)
                {
                    return new Path.ParentElement();
                }
            });
            
            instance.addConverter(String.class, Path.SelfElement.class, new TypeConverter.Converter<String, Path.SelfElement>()
            {
                public Path.SelfElement convert(String source)
                {
                    return new Path.SelfElement();
                }
            });
            
            // associations
            instance.addConverter(String.class, ChildAssociationRef.class, new TypeConverter.Converter<String, ChildAssociationRef>()
            {
                public ChildAssociationRef convert(String source)
                {
                    return new ChildAssociationRef(source);
                }
            });

            instance.addConverter(String.class, AssociationRef.class, new TypeConverter.Converter<String, AssociationRef>()
            {
                public AssociationRef convert(String source)
                {
                    return new AssociationRef(source);
                }
            });
            
            // qnames
            instance.addConverter(String.class, QName.class, new TypeConverter.Converter<String, QName>()
            {
                public QName convert(String source)
                {
                    return QName.createQName(source, SOLRTypeConverter.this.namespaceDAO);
                }
            });
            
            instance.addConverter(String.class, MLText.class, new TypeConverter.Converter<String, MLText>()
            {
                public MLText convert(String source)
                {
                    return new MLText(source);
                }
            });
        }
        
        public final <T> T convert(Class<T> c, Object value)
        {
            return instance.convert(c, value);
        }
    }
    
    /*
     * Deserializes JSON values from the remote API into objects consumable by SOLR
     */
    private class SOLRDeserializer
    {
        private SOLRTypeConverter typeConverter;

        public SOLRDeserializer(NamespaceDAO namespaceDAO)
        {
            typeConverter = new SOLRTypeConverter(namespaceDAO);
        }
        
        public <T> T deserializeValue(Class<T> targetClass, Object value) throws JSONException
        {
            return typeConverter.convert(targetClass, value);
        }
    }
    
    private static class SOLRResponse
    {
        protected Response response;
        
        public SOLRResponse(Response response)
        {
            super();
            this.response = response;
        }
        
        public Response getResponse()
        {
            return response;
        }
    }

    public static class GetTransactionsResponse extends SOLRResponse
    {
        private List<Transaction> txns;

        public GetTransactionsResponse(Response response, List<Transaction> txns)
        {
            super(response);
            this.txns = txns;
        }

        public List<Transaction> getTransaction()
        {
            return txns;
        }
    }
    
    public static class GetNodesResponse extends SOLRResponse
    {
        private List<Node> nodes;

        public GetNodesResponse(Response response, List<Node> nodes)
        {
            super(response);
            this.nodes = nodes;
        }

        public List<Node> getNodes()
        {
            return nodes;
        }
    }
    
    public static class GetNodesMetaDataResponse extends SOLRResponse
    {
        private List<NodeMetaData> nodes;

        public GetNodesMetaDataResponse(Response response, List<NodeMetaData> nodes)
        {
            super(response);
            this.nodes = nodes;
        }

        public List<NodeMetaData> getNodes()
        {
            return nodes;
        }
    }
    
    public static enum TRANSFORM_STATUS
    {
        OK, FAILED, NO_TRANSFORM, UNKNOWN;
        
        public static TRANSFORM_STATUS getStatus(String statusStr)
        {
            if(statusStr.equals("ok"))
            {
                return OK;
            }
            else if(statusStr.equals("failed"))
            {
                return FAILED;
            }
            else if(statusStr.equals("noTransform"))
            {
                return NO_TRANSFORM;
            }
            else
            {
                return UNKNOWN;
            }
        }
    }
    
    public static class GetTextContentResponse extends SOLRResponse
    {
        private InputStream content;
        private TRANSFORM_STATUS transformStatus;
        private String transformException;

        public GetTextContentResponse(Response response) throws IOException
        {
            super(response);
            this.content = response.getContentAsStream();
            String transformStatusStr = response.getHeader("XAlfresco-transformStatus");
            if(transformStatusStr != null)
            {
                this.transformStatus = TRANSFORM_STATUS.getStatus(transformStatusStr);
            }
            else
            {
                this.transformStatus = null;
            }
            this.transformException = response.getHeader("XAlfresco-transformException");
        }

        public InputStream getContent()
        {
            return content;
        }

        public TRANSFORM_STATUS getTransformStatus()
        {
            return transformStatus;
        }

        public String getTransformException()
        {
            return transformException;
        }
        
        public int getContentLength()
        {
            return response.getContentLength();
        }
        
        public int getStatus()
        {
            return response.getStatus();
        }
        
        public Long getRequestDuration()
        {
            return response.getRequestDuration();
        }
    }
}
