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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.Path.AttributeElement;
import org.alfresco.service.cmr.repository.Path.ChildAssocElement;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.repository.datatype.TypeConversionException;
import org.alfresco.service.cmr.repository.datatype.TypeConverter;
import org.alfresco.service.cmr.repository.datatype.TypeConverter.Converter;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.Pair;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
    private DictionaryService dictionaryService;

    public SOLRAPIClient(DictionaryService dictionaryService, NamespaceDAO namespaceDAO, String alfrescoURL, String username, String password)
    {
        super(alfrescoURL + (alfrescoURL.endsWith("/") ? "" : "/"), username, password);
        this.dictionaryService = dictionaryService;
        this.deserializer = new SOLRDeserializer(namespaceDAO);
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
        
        Reader reader = new BufferedReader(new InputStreamReader(response.getContentAsStream()));
        JSONObject json = new JSONObject(new JSONTokener(reader));

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

//        if(logger.isDebugEnabled())
//        {
//            logger.debug(response.getContentAsString());
//        }

        Reader reader = new BufferedReader(new InputStreamReader(response.getContentAsStream()));
        JSONObject json = new JSONObject(new JSONTokener(reader));
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
    
    private PropertyValue getSinglePropertyValue(DataTypeDefinition dataType, Object value) throws JSONException
    {
        PropertyValue ret = null;
        QName dataTypeName = dataType.getName();

        if(value == null || value == JSONObject.NULL)
        {
            ret = null;
        }
        else if(dataTypeName.equals(DataTypeDefinition.MLTEXT))
        {
            JSONArray a = (JSONArray)value;
            Map<Locale, String> mlValues = new HashMap<Locale, String>(a.length());

            for(int k = 0; k < a.length(); k++)
            {
                JSONObject pair = a.getJSONObject(k);
                Locale locale = deserializer.deserializeValue(Locale.class, pair.getString("locale"));
                String mlValue = pair.getString("value");
                mlValues.put(locale, mlValue);
            }

            ret = new MLTextPropertyValue(mlValues);
        }
        else if(dataTypeName.equals(DataTypeDefinition.CONTENT))
        {
            JSONObject o = (JSONObject)value;
            long contentId = o.getLong("contentId");
            
            String localeStr = o.has("locale") ? o.getString("locale") : null;
            Locale locale = (o.has("locale") ? deserializer.deserializeValue(Locale.class, localeStr) : null);

            Long size = o.has("size") ? o.getLong("size") : null;

            String encoding = o.has("encoding") ? o.getString("encoding") : null;
            String mimetype = o.has("mimetype") ? o.getString("mimetype") : null;

            ret = new ContentPropertyValue(locale, size, contentId, encoding, mimetype);
        }
        else
        {
            ret = new StringPropertyValue((String)value);
        }
        
        return ret;
    }

    private PropertyValue getPropertyValue(PropertyDefinition propertyDef, Object value) throws JSONException
    {
        PropertyValue ret = null;

        if(value == null || value == JSONObject.NULL)
        {
            ret = null;
        }
        else if(propertyDef == null)
        {
            // assume a string
            ret = new StringPropertyValue((String)value);
        }
        else
        {
            DataTypeDefinition dataType = propertyDef.getDataType();
            
            boolean isMulti = propertyDef.isMultiValued();

            if(isMulti)
            {
                if(!(value instanceof JSONArray))
                {
                    throw new IllegalArgumentException("Expected json array, got " + value.getClass().getName());
                }

                MultiPropertyValue multi = new MultiPropertyValue();
                JSONArray array = (JSONArray)value;
                for(int j = 0; j < array.length(); j++)
                {
                    multi.addValue(getSinglePropertyValue(dataType, array.get(j)));
                }
    
                ret = multi;
            }
            else
            {
                ret = getSinglePropertyValue(dataType, value);
            }
        }
        
        return ret;
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

        Reader reader = new BufferedReader(new InputStreamReader(response.getContentAsStream()));
        // TODO - replace with streaming-based solution e.g. SimpleJSON ContentHandler
        JSONObject json = new JSONObject(new JSONTokener(reader));

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
                List<Pair<String, QName>> paths = new ArrayList<Pair<String, QName>>(jsonPaths.length());
                for(int j = 0; j < jsonPaths.length(); j++)
                {
                    JSONObject path = new JSONObject(jsonPaths.getString(j));
                    String pathValue = path.getString("path");
                    QName qname = path.has("qname") ? deserializer.deserializeValue(QName.class, path.getString("qname")) : null;
                    paths.add(new Pair<String, QName>(pathValue, qname));
                }
                metaData.setPaths(paths);
            }

            if(jsonNodeInfo.has("properties"))
            {
                JSONObject jsonProperties = jsonNodeInfo.getJSONObject("properties");
                Map<QName, PropertyValue> properties = new HashMap<QName, PropertyValue>(jsonProperties.length());
                @SuppressWarnings("rawtypes")
                Iterator propKeysIterator = jsonProperties.keys();
                while(propKeysIterator.hasNext())
                {
                    String propName = (String)propKeysIterator.next();
                    QName propQName = deserializer.deserializeValue(QName.class, propName);
                    Object propValueObj = jsonProperties.opt(propName);

                    // check the expected property type to determine how to process the value
                    PropertyDefinition propertyDef = dictionaryService.getProperty(propQName);
//                    if(propertyDef == null)
//                    {
//                        // TODO which exception here?
//                        throw new IllegalArgumentException("Could not find property definition for property " + propName);
//                    }
                    
                    properties.put(propQName, getPropertyValue(propertyDef, propValueObj));
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
    @SuppressWarnings("rawtypes")
    private class SOLRTypeConverter
    {
        /**
         * Default Type Converter
         */
        private TypeConverter instance = new TypeConverter();
        private NamespaceDAO namespaceDAO;
        
        @SuppressWarnings("unchecked")
        SOLRTypeConverter(NamespaceDAO namespaceDAO)
        {
            this.namespaceDAO = namespaceDAO;

            // add all default converters to this converter
            // TODO find a better way of doing this
            Map<Class<?>, Map<Class<?>, Converter<?,?>>> converters = DefaultTypeConverter.INSTANCE.getConverters();
            for(Class source : converters.keySet())
            {
                Map<Class<?>, Converter<?,?>> converters1 = converters.get(source);
                for(Class dest : converters1.keySet())
                {
                    Converter<?,?> converter = converters1.get(dest);
                    instance.addConverter(source, dest, converter);
                }
            }
            
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
    
    public static enum STATUS
    {
        NOT_MODIFIED, OK, NO_TRANSFORM, NO_CONTENT, UNKNOWN, TRANSFORM_FAILED, GENERAL_FAILURE;
        
        public static STATUS getStatus(String statusStr)
        {
            if(statusStr.equals("ok"))
            {
                return OK;
            }
            else if(statusStr.equals("transformFailed"))
            {
                return TRANSFORM_FAILED;
            }
            else if(statusStr.equals("noTransform"))
            {
                return NO_TRANSFORM;
            }
            else if(statusStr.equals("noContent"))
            {
                return NO_CONTENT;
            }
            else
            {
                return UNKNOWN;
            }
        }
    }
    
/*    public static enum TRANSFORM_STATUS
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
    }*/
    
    public static class GetTextContentResponse extends SOLRResponse
    {
        private InputStream content;
        private STATUS status;
        private String transformException;
        private String transformStatusStr;

        public GetTextContentResponse(Response response) throws IOException
        {
            super(response);

            this.content = response.getContentAsStream();
            this.transformStatusStr = response.getHeader("XAlfresco-transformStatus");
            this.transformException = response.getHeader("XAlfresco-transformException");
            setStatus();
        }

        public InputStream getContent()
        {
            return content;
        }

        public STATUS getStatus()
        {
            return status;
        }
        
        private void setStatus()
        {
            int status = response.getStatus();
            if(status == HttpStatus.SC_NOT_MODIFIED)
            {
                this.status = STATUS.NOT_MODIFIED;
            }
            else if(status == HttpStatus.SC_INTERNAL_SERVER_ERROR)
            {
                this.status = STATUS.GENERAL_FAILURE;
            }
            else if(status == HttpStatus.SC_OK)
            {
                this.status = STATUS.OK;
            }
            else if(status == HttpStatus.SC_NO_CONTENT)
            {
                if(transformStatusStr == null)
                {
                    this.status = STATUS.UNKNOWN;
                }
                else
                {
                    if(transformStatusStr.equals("noTransform"))
                    {
                        this.status = STATUS.NO_TRANSFORM;
                    }
                    else if(transformStatusStr.equals("transformFailed"))
                    {
                        this.status = STATUS.TRANSFORM_FAILED;
                    }
                    else
                    {
                        this.status = STATUS.UNKNOWN;
                    }
                }
            }
        }

        public String getTransformException()
        {
            return transformException;
        }
        
        public int getContentLength()
        {
            return response.getContentLength();
        }
        
        public Long getRequestDuration()
        {
            return response.getRequestDuration();
        }
    }
}
