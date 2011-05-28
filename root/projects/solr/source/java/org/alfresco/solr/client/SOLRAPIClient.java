package org.alfresco.solr.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.SOLRDeserializer;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SOLRAPIClient
{
    private static final Log logger = LogFactory.getLog(SOLRAPIClient.class);
    private static final String GET_TRANSACTIONS_URL = "api/solr/transactions";
    private static final String GET_METADATA_URL = "api/solr/metadata";
    private static final String GET_NODES_URL = "api/solr/nodes";

    private String alfrescoURL;
    private String username;
    private String password;

    // Remote Server access
    private HttpClient httpClient = null;

    private DictionaryService dictionaryService;
    private SOLRDeserializer deserializer;

    public SOLRAPIClient(DictionaryService dictionaryService, String alfrescoURL, String username, String password)
    {
        this.alfrescoURL = alfrescoURL + (alfrescoURL.endsWith("/") ? "" : "/");;
        this.username = username;
        this.password = password;
        this.dictionaryService = dictionaryService;
        deserializer = new SOLRDeserializer(dictionaryService);
    }
    
    private void setupHttpClient()
    {
        httpClient = new HttpClient();
        httpClient.getParams().setBooleanParameter(HttpClientParams.PREEMPTIVE_AUTHENTICATION, true);
        httpClient.getState().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), new UsernamePasswordCredentials(username, password));
    }

    public List<Transaction> getTransactions(Long fromCommitTime, Long minTxnId, int maxResults) throws IOException, JSONException
    {
        setupHttpClient();

        StringBuilder url = new StringBuilder(alfrescoURL);
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
        long startTime = System.currentTimeMillis();
        Response response = sendRequest(req, Status.STATUS_OK, username);
        long endTime = System.currentTimeMillis();

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
        
        logger.debug("Got " + jsonTransactions.length() + " txns in " + (endTime - startTime) + " ms");
        
        return transactions;
    }
    
    public List<Node> getNodes(GetNodesParameters parameters, int maxResults) throws IOException, JSONException
    {
        setupHttpClient();

        StringBuilder url = new StringBuilder(alfrescoURL);
        url.append(GET_NODES_URL);

        StringWriter body = new StringWriter();
        JSONWriter jsonOut = new JSONWriter(body);
        
        jsonOut.startObject();
        {
            if(parameters.getTransactionIds() != null)
            {
                jsonOut.startValue("txnIds");
                {
                    jsonOut.startArray();
                    {
                        for(Long txnId : parameters.getTransactionIds())
                        {
                            jsonOut.writeValue(txnId);
                        }

                    }
                    jsonOut.endArray();
                }
                jsonOut.endValue();
            }
        
            if(parameters.getFromNodeId() != null)
            {
                jsonOut.writeValue("fromNodeId", parameters.getFromNodeId());
            }
            if(parameters.getToNodeId() != null)
            {
                jsonOut.writeValue("toNodeId", parameters.getToNodeId());
            }
            if(parameters.getExcludeAspects() != null)
            {
                jsonOut.startValue("excludeAspects");
                {
                    jsonOut.startArray();
                    {
                        for(QName excludeAspect : parameters.getExcludeAspects())
                        {
                            jsonOut.writeValue(excludeAspect.toString());
                        }
                    }
                    jsonOut.endArray();
                }
                jsonOut.endValue();
            }
            if(parameters.getIncludeAspects() != null)
            {
                jsonOut.startValue("includeAspects");
                {
                    jsonOut.startArray();
                    {
                        for(QName includeAspect : parameters.getIncludeAspects())
                        {
                            jsonOut.writeValue(includeAspect.toString());
                        }
                    }
                    jsonOut.endArray();
                }
                jsonOut.endValue();
            }

            if(parameters.getStoreProtocol() != null)
            {
                jsonOut.writeValue("storeProtocol", parameters.getStoreProtocol());
            }

            if(parameters.getStoreIdentifier() != null)
            {
                jsonOut.writeValue("storeIdentifier", parameters.getStoreIdentifier());
            }
            
            jsonOut.writeValue("maxResults", maxResults);
        }
        jsonOut.endObject();

        PostRequest req = new PostRequest(url.toString(), body.toString(), "application/json");
 
        long startTime = System.currentTimeMillis();
        Response response = sendRequest(req, Status.STATUS_OK, username);
        long endTime = System.currentTimeMillis();
        
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

        logger.debug("Got " + jsonNodes.length() + " nodes in " + (endTime - startTime) + " ms");

        return nodes;
    }
    
    public List<NodeMetaData> getNodesMetaData(List<Long> nodeIds, int maxResults, int numMetaDataNodes) throws IOException, JSONException
    {
        setupHttpClient();

        StringBuilder url = new StringBuilder(alfrescoURL);
        url.append(GET_METADATA_URL);

        StringWriter body = new StringWriter();
        JSONWriter jsonOut = new JSONWriter(body);
        
        jsonOut.startObject();
        {
            if(nodeIds != null && nodeIds.size() > 0)
            {
                jsonOut.startValue("nodeIds");
                {
                    jsonOut.startArray();
                    for(Long nodeId : nodeIds)
                    {
                        jsonOut.writeValue(nodeId);
                    }
                    jsonOut.endArray();
                }
                jsonOut.endValue();
            }

            jsonOut.writeValue("maxResults", maxResults);
        }
        jsonOut.endObject();

        PostRequest req = new PostRequest(url.toString(), body.toString(), "application/json");
        long startTime = System.currentTimeMillis();
        Response response = sendRequest(req, Status.STATUS_OK, username);
        long endTime = System.currentTimeMillis();
        
        if(logger.isDebugEnabled())
        {
            logger.debug("nodesMetaData = " + response.getContentAsString());
        }

        JSONObject json = new JSONObject(response.getContentAsString());

        JSONArray jsonNodes = json.getJSONArray("nodes");

        logger.debug("Got metadata for " + jsonNodes.length() + " nodes in " + (endTime - startTime) + " ms");
        
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

            if(jsonNodeInfo.has("properties"))
            {
                JSONObject jsonProperties = jsonNodeInfo.getJSONObject("properties");
                Map<QName, Serializable> properties = new HashMap<QName, Serializable>(jsonProperties.length());
                @SuppressWarnings("rawtypes")
                Iterator propKeysIterator = jsonProperties.keys();
                while(propKeysIterator.hasNext())
                {
                    String propName = (String)propKeysIterator.next();
                    QName propQName = deserializer.deserializeValue(QName.class, propName);

                    // TODO need to call get* method depending on type
                    Object propValue = jsonProperties.get(propName);
                    properties.put(propQName, deserializer.deserialize(propQName, propValue));
                }
                metaData.setProperties(properties);
            }
            nodes.add(metaData);
        }
        return nodes;
    }
    
    /**
     * Send Request to Test Web Script Server (as admin)
     * 
     * @param req
     * @param expectedStatus
     * @return response
     * @throws IOException
     */
    protected Response sendRequest(Request req, int expectedStatus)
        throws IOException
    {
        return sendRequest(req, expectedStatus, null);
    }
    
    /**
     * Send Request
     * 
     * @param req
     * @param expectedStatus
     * @param asUser
     * @return response
     * @throws IOException
     */
    protected Response sendRequest(Request req, int expectedStatus, String asUser)
        throws IOException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("");
            logger.debug("* Request: " + req.getMethod() + " " + req.getFullUri() + (req.getBody() == null ? "" : "\n" + new String(req.getBody(), "UTF-8")));
        }

        Response res = sendRemoteRequest(req, expectedStatus);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("");
            logger.debug("* Response: " + res.getStatus() + " " + req.getMethod() + " " + req.getFullUri() + "\n" + res.getContentAsString());
        }
        
//        if (expectedStatus > 0 && expectedStatus != res.getStatus())
//        {
//            fail("Status code " + res.getStatus() + " returned, but expected " + expectedStatus + " for " + req.getFullUri() + " (" + req.getMethod() + ")\n" + res.getContentAsString());
//        }
        
        return res;
    }

    /**
     * Get the server for the previously-supplied {@link #setCustomContext(String) custom context}
     */
//    protected TestWebScriptServer getServer()
//    {
//        if (customContext == null)
//        {
//            return TestWebScriptRepoServer.getTestServer();
//        }
//        else
//        {
//            return TestWebScriptRepoServer.getTestServer(customContext);
//        }
//    }
    
    /**
     * Send Local Request to Test Web Script Server
     * 
     * @param req
     * @param expectedStatus
     * @param asUser
     * @return response
     * @throws IOException
     */
//    protected Response sendLocalRequest(final Request req, final int expectedStatus, String asUser)
//        throws IOException
//    {
//        asUser = (asUser == null) ? defaultRunAs : asUser;
//        if (asUser == null)
//        {
//            return getServer().submitRequest(req.getMethod(), req.getFullUri(), req.getHeaders(), req.getBody(), req.getEncoding(), req.getType());
//        }
//        else
//        {
//            // send request in context of specified user
//            getServer();
//            return AuthenticationUtil.runAs(new RunAsWork<Response>()
//            {
//                @SuppressWarnings("synthetic-access")
//                public Response doWork() throws Exception
//                {
//                    return getServer().submitRequest(req.getMethod(), req.getFullUri(), req.getHeaders(), req.getBody(), req.getEncoding(), req.getType());
//                }
//            }, asUser);
//        }
//    }
    
    /**
     * Send Remote Request to stand-alone Web Script Server
     * 
     * @param req
     * @param expectedStatus
     * @param asUser
     * @return response
     * @throws IOException
     */
    protected Response sendRemoteRequest(Request req, int expectedStatus)
        throws IOException
    {
        String uri = req.getFullUri();
        if (!uri.startsWith("http"))
        {
            uri = alfrescoURL + uri;
        }
        
        // construct method
        HttpMethod httpMethod = null;
        String method = req.getMethod();
        if (method.equalsIgnoreCase("GET"))
        {
            GetMethod get = new GetMethod(req.getFullUri());
            httpMethod = get;
        }
        else if (method.equalsIgnoreCase("POST"))
        {
            PostMethod post = new PostMethod(req.getFullUri());
            post.setRequestEntity(new ByteArrayRequestEntity(req.getBody(), req.getType()));
            httpMethod = post;
        }
        else
        {
            throw new AlfrescoRuntimeException("Http Method " + method + " not supported");
        }
        if (req.getHeaders() != null)
        {
            for (Map.Entry<String, String> header : req.getHeaders().entrySet())
            {
                httpMethod.setRequestHeader(header.getKey(), header.getValue());
            }
        }

        // execute method
        httpClient.executeMethod(httpMethod);
        return new HttpMethodResponse(httpMethod);
    }
    
    /**
     * A Web Script Test Response
     */
    public interface Response
    {
        public byte[] getContentAsByteArray();
        
        public String getContentAsString()
            throws UnsupportedEncodingException;
        
        public String getHeader(String name);
        
        public String getContentType();
        
        public int getContentLength();
        
        public int getStatus();
    }
    
    public static class HttpMethodResponse implements Response
    {
        private HttpMethod method;

        public HttpMethodResponse(HttpMethod method)
        {
            this.method = method;
        }

        public byte[] getContentAsByteArray()
        {
            try
            {
                return method.getResponseBody();
            }
            catch (IOException e)
            {
                return null;
            }
        }

        public String getContentAsString() throws UnsupportedEncodingException
        {
            try
            {
                return method.getResponseBodyAsString();
            }
            catch (IOException e)
            {
                return null;
            }
        }

        public String getContentType()
        {
            return getHeader("Content-Type");
        }

        public int getContentLength()
        {
            try
            {
                return method.getResponseBody().length;
            }
            catch (IOException e)
            {
                return 0;
            }
        }

        public String getHeader(String name)
        {
            Header header = method.getResponseHeader(name);
            return (header != null) ? header.getValue() : null;
        }

        public int getStatus()
        {
            return method.getStatusCode();
        }

    }
    
    public static class Request
    {
        private String method;
        private String uri;
        private Map<String, String> args;
        private Map<String, String> headers;
        private byte[] body;
        private String encoding = "UTF-8";
        private String contentType;
        
        public Request(Request req)
        {
            this.method = req.method;
            this.uri= req.uri;
            this.args = req.args;
            this.headers = req.headers;
            this.body = req.body;
            this.encoding = req.encoding;
            this.contentType = req.contentType;
        }
        
        public Request(String method, String uri)
        {
            this.method = method;
            this.uri = uri;
        }
        
        public String getMethod()
        {
            return method;
        }
        
        public String getUri()
        {
            return uri;
        }
        
        public String getFullUri()
        {
            // calculate full uri
            String fullUri = uri == null ? "" : uri;
            if (args != null && args.size() > 0)
            {
                char prefix = (uri.indexOf('?') == -1) ? '?' : '&';
                for (Map.Entry<String, String> arg : args.entrySet())
                {
                    fullUri += prefix + arg.getKey() + "=" + (arg.getValue() == null ? "" : arg.getValue());
                    prefix = '&';
                }
            }
            
            return fullUri;
        }
        
        public Request setArgs(Map<String, String> args)
        {
            this.args = args;
            return this;
        }
        
        public Map<String, String> getArgs()
        {
            return args;
        }

        public Request setHeaders(Map<String, String> headers)
        {
            this.headers = headers;
            return this;
        }
        
        public Map<String, String> getHeaders()
        {
            return headers;
        }
        
        public Request setBody(byte[] body)
        {
            this.body = body;
            return this;
        }
        
        public byte[] getBody()
        {
            return body;
        }
        
        public Request setEncoding(String encoding)
        {
            this.encoding = encoding;
            return this;
        }
        
        public String getEncoding()
        {
            return encoding;
        }

        public Request setType(String contentType)
        {
            this.contentType = contentType;
            return this;
        }
        
        public String getType()
        {
            return contentType;
        }
    }
    
    /**
     * Test GET Request
     */
    public static class GetRequest extends Request
    {
        public GetRequest(String uri)
        {
            super("get", uri);
        }
    }

    /**
     * Test POST Request
     */
    public static class PostRequest extends Request
    {
        public PostRequest(String uri, String post, String contentType)
            throws UnsupportedEncodingException 
        {
            super("post", uri);
            setBody(getEncoding() == null ? post.getBytes() : post.getBytes(getEncoding()));
            setType(contentType);
        }

        public PostRequest(String uri, byte[] post, String contentType)
        {
            super("post", uri);
            setBody(post);
            setType(contentType);
        }
    }
}
