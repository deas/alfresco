/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.extensions.surf.exception.ConnectorProviderException;
import org.springframework.extensions.surf.exception.RemoteConfigException;
import org.springframework.extensions.surf.exception.WebScriptsPlatformException;
import org.springframework.extensions.surf.util.Pair;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.ConnectorProvider;
import org.springframework.extensions.webscripts.connector.ConnectorProviderImpl;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;

import freemarker.cache.TemplateLoader;

/**
 * Store implementation that queries and retrieves documents from a remote HTTP endpoint.
 * <p>
 * The endpoint is assumed to support a WebScript Remote Store implementation (such as
 * AVMRemoteStore) that mirrors the required Store API. 
 * 
 * @author Kevin Roast
 */
public class RemoteStore extends AbstractStore
{
	private static Log logger = LogFactory.getLog(RemoteStore.class);
    
    public static final String DEFAULT_API = "/remotestore";
    public static final String DEFAULT_ENDPOINT_ID = "alfresco";
	
    private static final String API_LISTPATTERN = "listpattern";
    private static final String API_LISTALL = "listall";
    private static final String API_GET = "get";
    private static final String API_CREATE = "create";
    private static final String API_CREATE_MULTI = "createmulti";
    private static final String API_DELETE = "delete";
    private static final String API_UPDATE = "update";
    private static final String API_LASTMODIFIED = "lastmodified";
    private static final String API_HAS = "has";
    
    private ConnectorService connectorService;
    private ConnectorProvider connectorProvider;
    
    private String storeId;
    private String endpoint;    
    private String path;
    private String api;
    private String webappId;
    private String webappPathPrefix;
        
    /**
     * @param service   The ConnectorService bean
     */
    public void setConnectorService(ConnectorService service)
    {
        this.connectorService = service;
    }
    
    /**
     * Gets the connector service.
     * 
     * @return the connector service
     */
    public ConnectorService getConnectorService()
    {
    	return this.connectorService;
    }
        
    /**
     * Sets the connector provider
     */
    public void setConnectorProvider(ConnectorProvider connectorProvider)
    {
    	this.connectorProvider = connectorProvider;
    }
    
    /**
     * @return the connector provider
     */
    public ConnectorProvider getConnectorProvider()
    {
    	return this.connectorProvider;
    }
    
    /**
     * @param api the WebScript API path to set for the remote store i.e. "/remotestore"
     */
    public void setApi(String api)
    {
    	this.api = api;
    }
    
    /**
     * Gets the api.
     * 
     * @return the api
     */
    public String getApi()
    {
    	return this.api;
    }
        
    /**
     * Sets the base path to send to the remote store
     * 
     * @param path 
     */
    public void setPath(String path)
    {
    	this.path = path;
    }
            
    /**
     * @param endpoint the endpoint ID to use when calling the remote API
     */
    public void setEndpoint(String endpoint)
    {
    	this.endpoint = endpoint;
    }
    
    /**
     * Gets the endpoint.
     * 
     * @return the endpoint
     */
    public String getEndpoint()
    {
    	return this.endpoint;
    }
    
    /**
     * Sets the store's web application id to bind to within the designated store
     * This is meaningful for WCM Web Project stores.
     * 
     * @param webappId
     */
    public void setWebappId(String webappId)
    {
    	this.webappId = webappId;
    }
    
    public String getWebappPathPrefix()
    {
        return this.webappPathPrefix;
    }
    
    public void setWebappPathPrefix(String webappPathPrefix)
    {
        this.webappPathPrefix = webappPathPrefix;
    }
    
    /**
     * Gets the store's web application id binding
     * This is meaningful for WCM Web Project stores.
     * 
     * @return
     */
    public String getWebappId()
    {
    	String value = this.webappId;
    	
    	if (value == null && this.getPreviewContext() != null)
    	{
    		value = getPreviewContext().getWebappId();
    	}
    	
    	return value;
    }
        
    /**
     * Allows for specification of default or fallback store id to use
     * when binding to a remote store.  This can be overridden by providing
     * an implementation of a RemoteStoreContextProvider in the Spring
     * Bean configuration.
     * 
     * @param storeId   the default store id
     */
    public void setStoreId(String storeId)
    {
    	this.storeId = storeId;
    }
    
    /**
     * Gets the store id.
     * 
     * @return the store id
     */
    public String getStoreId()
    {
    	String value = this.storeId;
    	
    	if (value == null && getPreviewContext() != null)
    	{
    		value = getPreviewContext().getStoreId();
    	}
    	
    	return value;
    }
    
    /**
     * Store path calculation
     * 
     * If we have a store context, then we can check to see if a base path should be inserted ahead of the
     * path that we believe we're directing to.
     * 
     * Use case - consider writing a file /alfresco/site-data/components/component.xml
     * 
     * If we're writing to sitestore, then the file is stored relative to the store root.
     * 
     * In the case of a WCM web project, however, we may want to persist to one of several web applications.
     * If we have a webappId (retrieved from the context), then we prepend: /WEB-INF/classes
     * 
     * The new location is: /WEB-INF/classes/alfresco/site-data/components/component.xml
     * 
     * This allows us to operate against both straight up AVM stores as well as WCM Web Project AVM stores.
     * 
     * @return
     */
    public String getStorePath()
    {
    	String value = this.path;
    	
    	// if we have a webapp id to which we are binding, we will use
    	// the WCM webapp path prefix to "move" our starting point
    	// to the appropriate place in the avm store
    	if (this.getStoreId() != null && this.getWebappId() != null)
    	{
			value = this.getWebappPathPrefix();
			if (value != null)
			{
    			if (!value.endsWith("/"))
    			{
    				value += "/";
    			}
    			if (this.path.startsWith("/"))
    			{
    				value += this.path.substring(1);
    			}
    			else
    			{
    				value += this.path;
    			}
			}
    	}
    	
    	if (value == null)
    	{
    	    value = "";
    	}
    	
    	return value;
    }
        
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#init()
     */
    public void init()
    {
        if (this.connectorService == null)
        {
            throw new IllegalArgumentException("ConnectorService reference is mandatory for RemoteStore.");
        }
        if (this.getEndpoint() == null || getEndpoint().length() == 0)
        {
            throw new IllegalArgumentException("Endpoint ID is mandatory for RemoteStore.");
        }
        if (this.getApi() == null || this.getApi().length() == 0)
        {
            throw new IllegalArgumentException("API name is mandatory for RemoteStore.");
        }
        if (this.getStorePath() == null)
        {
            throw new IllegalArgumentException("Path prefix is mandatory for RemoteStore.");
        }
    	
        if (logger.isDebugEnabled())
        {
            logger.debug("RemoteStore initialised with endpoint id '" + this.getEndpoint() + "' API path '" +
                         this.getApi() + "' path prefix '" + this.getStorePath() + "'.");
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#isSecure()
     */
    public boolean isSecure()
    {
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#exists()
     */
    public boolean exists()
    {
        // always return true - even if a remote store appears to be down we cannot
        // assume this is always the case and must retry until it is restored
        return true;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#hasDocument(java.lang.String)
     */
    public boolean hasDocument(String documentPath) throws IOException
    {
        boolean hasDocument = false;
        
        Response res = callGet(buildEncodeCall(API_HAS, documentPath));
        if (Status.STATUS_OK == res.getStatus().getCode())
        {
            hasDocument = Boolean.parseBoolean(res.getResponse());
        }
        else
        {
            throw new IOException("Unable to test document path: " + documentPath +
                    " in remote store: " + this.getEndpoint() +
                    " due to error: " + res.getStatus().getCode() + " " + res.getStatus().getMessage());
        }
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.hasDocument() " + documentPath + " = " + hasDocument);
        
        return hasDocument;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#lastModified(java.lang.String)
     */
    public long lastModified(String documentPath) throws IOException
    {
        Response res = callGet(buildEncodeCall(API_LASTMODIFIED, documentPath));
        if (Status.STATUS_OK == res.getStatus().getCode())
        {
            try
            {
                long lastMod = Long.parseLong(res.getResponse());
                
                if (logger.isDebugEnabled())
                    logger.debug("RemoteStore.lastModified() " + documentPath + " = " + lastMod);
                
                return lastMod;
            }
            catch (NumberFormatException ne)
            {
                throw new IOException("Failed to process lastModified response: " + ne.getMessage());
            }
        }
        else
        {
            throw new IOException("Unable to get lastModified date of document path: " + documentPath +
                    " in remote store: " + this.getEndpoint() +
                    " due to error: " + res.getStatus().getCode() + " " + res.getStatus().getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#updateDocument(java.lang.String, java.lang.String)
     */
    public void updateDocument(String documentPath, String content) throws IOException
    {
        ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
        Response res = callPost(buildEncodeCall(API_UPDATE, documentPath), in);
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.updateDocument() " + documentPath + " = " + res.getStatus().getCode());
        
        if (Status.STATUS_OK != res.getStatus().getCode())
        {
            throw new IOException("Unable to update document path: " + documentPath +
                    " in remote store: " + this.getEndpoint() +
                    " due to error: " + res.getStatus().getCode() + " " + res.getStatus().getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#removeDocument(java.lang.String)
     */
    public boolean removeDocument(String documentPath) throws IOException
    {
        Response res = callDelete(buildEncodeCall(API_DELETE, documentPath));
        
        boolean removed = (Status.STATUS_OK == res.getStatus().getCode());
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.removeDocument() " + documentPath + " = " + res.getStatus().getCode() + " (removed = "+removed+")");
        
        return removed;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#createDocument(java.lang.String, java.lang.String)
     */
    public void createDocument(String documentPath, String content) throws IOException
    {
        ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
        Response res = callPost(buildEncodeCall(API_CREATE, documentPath), in);
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.createDocument() " + documentPath + " = " + res.getStatus().getCode());
        
        if (Status.STATUS_OK != res.getStatus().getCode())
        {
            throw new IOException("Unable to create document path: " + documentPath +
                    " in remote store: " + this.getEndpoint() +
                    " due to error: " + res.getStatus().getCode() + " " + res.getStatus().getMessage());
        }
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#createDocuments(java.util.List)
     */
    @Override
    public void createDocuments(List<Pair<String, Document>> pathContents) throws IOException
    {
        Document master = DocumentHelper.createDocument();
        Element docEl = master.addElement("master");
        for (Pair<String, Document> pathContent : pathContents)
        {
            Element document = docEl.addElement("document");
            final String storePath = getStorePath();
            document.addAttribute("path", (storePath.equals("/") ? storePath : '/' + storePath) + pathContent.getFirst());
            document.add(pathContent.getSecond().getRootElement().createCopy());
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setSuppressDeclaration(false);
        XMLWriter xmlWriter = new XMLWriter(out, format);
        xmlWriter.write(master);
        xmlWriter.flush();
        out.close();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Response res = callPost(buildEncodeCall(API_CREATE_MULTI, ""), in);
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.createDocuments() " + pathContents.size() + " = " + res.getStatus().getCode());
        
        if (Status.STATUS_OK != res.getStatus().getCode())
        {
            throw new IOException("Unable to create documents in remote store: " + this.getEndpoint()
                    + " due to error: " + res.getStatus().getCode() + " " + res.getStatus().getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getDocument(java.lang.String)
     */
    public InputStream getDocument(String documentPath) throws IOException
    {
        return getDocumentResponse(documentPath).getResponseStream();
    }
    
    private Response getDocumentResponse(String path)
        throws IOException
    {
        Response res = callGet(buildEncodeCall(API_GET, path));
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.getDocument() " + path + " = " + res.getStatus().getCode());
        
        if (Status.STATUS_OK == res.getStatus().getCode())
        {
            return res;
        }
        else
        {
            throw new IOException("Unable to retrieve document path: " + path +
                    " in remote store: " + this.getEndpoint() +
                    " due to error: " + res.getStatus().getCode() + " " + res.getStatus().getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getAllDocumentPaths()
     */
    public String[] getAllDocumentPaths()
    {
        Response res = callGet(buildEncodeCall(API_LISTALL, ""));
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.getAllDocumentPaths() " + res.getStatus().getCode());
        
        if (Status.STATUS_OK == res.getStatus().getCode())
        {
            // convert to an array of store root-relative paths
            List<String> list = new ArrayList<String>(128);
            StringTokenizer t = new StringTokenizer(res.getResponse(), "\n");
            while (t.hasMoreTokens())
            {
                list.add(t.nextToken());
            }            
            String[] paths = list.toArray(new String[list.size()]);
            
            // truncate paths so that they are relative to the store path
            // and invariant of the type of AVM store (WCM or plain)
            convertToRelativePaths(paths);
            
            return paths;
        }
        else
        {
            return new String[0];
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getDocumentPaths(java.lang.String, boolean, java.lang.String)
     */
    public String[] getDocumentPaths(String path, boolean includeSubPaths, String documentPattern)
    {
        // Combine path and document pattern - then extract file pattern
        // as the RemoteStore only expects the file name pattern and the rest is considered path
        String fullPath = path + documentPattern;
        int lastSeparator = fullPath.lastIndexOf('/');
        if (lastSeparator != -1)
        {
            path = fullPath.substring(0, lastSeparator);
            documentPattern = fullPath.substring(lastSeparator + 1);
        }
        Map<String, String> args = new HashMap<String, String>(1, 1.0f);
        args.put("m", documentPattern);
        Response res = callGet(buildEncodeCall(API_LISTPATTERN, path, args));
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.getDocumentPaths() " + path + " subpaths: " + includeSubPaths +
                         " pattern: " + documentPattern + " = " + res.getStatus().getCode() + " " + res.getStatus().getMessage());
        
        if (Status.STATUS_OK == res.getStatus().getCode())
        {
            // convert to an array of store root-relative paths
            List<String> list = new ArrayList<String>(128);
            StringTokenizer t = new StringTokenizer(res.getResponse(), "\n");
            while (t.hasMoreTokens())
            {
                list.add(t.nextToken());
            }            
            String[] paths = list.toArray(new String[list.size()]);
            
            // truncate paths so that they are relative to the store path
            // and invariant of the type of AVM store (WCM or plain)
            convertToRelativePaths(paths);
            
            return paths;
        }
        else
        {
            return new String[0];
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getDescriptionDocumentPaths()
     */
    public String[] getDescriptionDocumentPaths()
    {
        return getDocumentPaths("", true, DESC_PATH_PATTERN);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getScriptDocumentPaths(org.alfresco.web.scripts.WebScript)
     */
    public String[] getScriptDocumentPaths(WebScript script)
    {
        String scriptPaths = script.getDescription().getId() + ".*";
        return getDocumentPaths("", false, scriptPaths);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getScriptLoader()
     */
    public ScriptLoader getScriptLoader()
    {
        return new RemoteStoreScriptLoader();
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getTemplateLoader()
     */
    public TemplateLoader getTemplateLoader()
    {
        return new RemoteStoreTemplateLoader();
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getBasePath()
     */
    public String getBasePath()
    {
    	return getStorePath();
    }


    /**
     * Helper to build and encode a remote store call
     * 
     * @param method        Remote store method name
     * @param documentPath  Document path to encode
     * 
     * @return encoded URL to execute
     */
    private String buildEncodeCall(String method, String documentPath)
    {
        return buildEncodeCall(method, documentPath, null);
    }
    
    /**
     * Helper to build and encode a remote store call
     * 
     * @param method        Remote store method name
     * @param documentPath  Document path to encode, can be empty but not null
     * @param args          Args map to apply to URL call, can be null or empty
     * 
     * @return encoded URL to execute
     */
    private String buildEncodeCall(String method, String documentPath, Map<String, String> args)
    {
        StringBuilder buf = new StringBuilder(128);
        
        buf.append(this.getApi());
        buf.append('/');
        buf.append(method);
        
        // encode store path into url
        String fullPath = this.getStorePath() + "/" + documentPath;        
        for (StringTokenizer t = new StringTokenizer(fullPath, "/"); t.hasMoreTokens(); /**/)
        {
            buf.append('/').append(URLEncoder.encode(t.nextToken()));
        }
        
        // Append in the store id
        String storeId = this.getStoreId();
        if (storeId != null)
        {
    		if (args == null)
    		{
    			args = new HashMap<String, String>(1, 1.0f);
    		}
   			args.put("s", storeId);
        }
        
        // Append in the webapp id (if applicable)
        String webappId = this.getWebappId();
        if (webappId != null)
        {
    		if (args == null)
    		{
    			args = new HashMap<String, String>(1, 1.0f);
    		}
   			args.put("w", webappId);        	
        }
                
        // append in any request parameters
        if (args != null && args.size() != 0)
        {
            buf.append('?');
            int count = 0;
            for (Map.Entry<String, String> entry : args.entrySet())
            {
                if (count++ != 0)
                {
                    buf.append('&');
                }
                buf.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue()));
            }
        }
        
        return buf.toString();
    }

    protected String buildUri(String uri)
    {
        return  uri;
    }
    
    /**
     * Perform a POST call to the given URI with the supplied input.
     */
    protected Response callPost(String uri, InputStream in)
    {
        try
        {
            Connector con = getConnector();
            return con.call(buildUri(uri), null, in);
        }
        catch (ConnectorProviderException cpe)
        {
            throw new WebScriptsPlatformException("Unable to find config for remote store.", cpe);
        }
    }

    /**
     * Perform a GET call to the given URI.
     */
    protected Response callGet(String uri)
    {
        try
        {
            Connector con = getConnector();
            return con.call(buildUri(uri));
        }
        catch (ConnectorProviderException cpe)
        {
            throw new WebScriptsPlatformException("Unable to find config for remote store.", cpe);
        }
    }
    
    /**
     * Perform a DELETE call to the given URI.
     */
    protected Response callDelete(String uri)
    {
        try
        {
            Connector con = getConnector();
            ConnectorContext context = new ConnectorContext(HttpMethod.DELETE, null, null);
            return con.call(buildUri(uri), context);
        }
        catch (ConnectorProviderException cpe)
        {
            throw new WebScriptsPlatformException("Unable to find config for remote store.", cpe);
        }
    }

    /**
     * Get a Connector for access to the endpoint. If a connector has been bound to the
     * current thread then use it, else retrieve a transient connector instance from the
     * ConnectorService.
     * 
     * @return Connector
     * 
     * @throws RemoteConfigException
     */
    protected Connector getConnector() throws ConnectorProviderException
    {
    	Connector conn = null;
    	
    	// use a default connector provider if none injected
    	if (connectorProvider == null)
    	{
    	    connectorProvider = new ConnectorProviderImpl();
    	}

    	// provision connector
   		conn = getConnectorProvider().provide(this.getEndpoint());
        
        return conn; 
    }
    
    
    /**
     * Remote Store implementation of a Script Loader
     * 
     * @author Kevin Roast
     */
    protected class RemoteStoreScriptLoader implements ScriptLoader
    {
        /**
         * @see org.springframework.extensions.webscripts.ScriptLoader#getScript(java.lang.String)
         */
        public ScriptContent getScript(String path)
        {
            ScriptContent sc = null;
            try
            {
                if (hasDocument(path))
                {
                    sc = new RemoteScriptContent(path);
                }
            }
            catch (IOException e)
            {
                throw new WebScriptException("Error locating script " + path, e);                
            }
            return sc;
        }
    }
    
    
    /**
     * Remote Store implementation of a Template Loader
     * 
     * @author Kevin Roast
     */
    private class RemoteStoreTemplateLoader implements TemplateLoader
    {
        /**
         * @see freemarker.cache.TemplateLoader#closeTemplateSource(java.lang.Object)
         */
        public void closeTemplateSource(Object templateSource) throws IOException
        {
            // nothing to do - we return a reader to fully retrieved in-memory data
        }

        /**
         * @see freemarker.cache.TemplateLoader#findTemplateSource(java.lang.String)
         */
        public Object findTemplateSource(String name) throws IOException
        {
            RemoteStoreTemplateSource source = null;
            if (hasDocument(name))
            {
                source = new RemoteStoreTemplateSource(name);
            }
            return source;
        }

        /**
         * @see freemarker.cache.TemplateLoader#getLastModified(java.lang.Object)
         */
        public long getLastModified(Object templateSource)
        {
            return ((RemoteStoreTemplateSource)templateSource).lastModified();
        }

        /**
         * @see freemarker.cache.TemplateLoader#getReader(java.lang.Object, java.lang.String)
         */
        public Reader getReader(Object templateSource, String encoding) throws IOException
        {
            return ((RemoteStoreTemplateSource)templateSource).getReader(encoding);
        }
    }
    
    
    /**
     * Template Source - loads from a Remote Store.
     * 
     * TODO: implement caching of remotely loaded template content?
     * 
     * @author Kevin Roast
     */
    private class RemoteStoreTemplateSource
    {
        private String templatePath;
        
        private RemoteStoreTemplateSource(String path)
        {
            this.templatePath = path;
        }
        
        private long lastModified()
        {
            try
            {
                return RemoteStore.this.lastModified(templatePath);
            }
            catch (IOException e)
            {
                return -1;
            }
        }
        
        private Reader getReader(String encoding)
            throws IOException
        {
            Response res = getDocumentResponse(templatePath);
            if (encoding == null || encoding.equals(res.getEncoding()))
            {
                return new StringReader(res.getResponse());
            }
            else
            {
                return new InputStreamReader(res.getResponseStream(), encoding);
            }
        }
    }
    
    
    /**
     * Script Content - loads from a Remote Store.
     * 
     * TODO: implement caching of remotely loaded script content?
     * 
     * @author Kevin Roast
     */
    private class RemoteScriptContent implements ScriptContent
    {
        private String scriptPath;
        
        /**
         * Constructor
         * 
         * @param path  Path to remote script content
         */
        private RemoteScriptContent(String path)
        {
            this.scriptPath = path;
        }
        
        /**
         * @see org.springframework.extensions.webscripts.ScriptContent#getPath()
         */
        public String getPath()
        {
            return getStorePath() + '/' + this.scriptPath;
        }

        /**
         * @see org.springframework.extensions.webscripts.ScriptContent#getPathDescription()
         */
        public String getPathDescription()
        {
            return getStorePath() + '/' + this.scriptPath + " loaded from endpoint: " + getEndpoint();
        }
        
        /**
         * @see org.springframework.extensions.webscripts.ScriptContent#getInputStream()
         */
        public InputStream getInputStream()
        {
            try
            {
                return getDocumentResponse(scriptPath).getResponseStream();
            }
            catch (IOException e)
            {
                throw new WebScriptsPlatformException("Unable to load script: " + scriptPath, e);
            }
        }

        /**
         * @see org.springframework.extensions.webscripts.ScriptContent#getReader()
         */
        public Reader getReader()
        {
            try
            {
                Response res = getDocumentResponse(scriptPath);
                if (res.getEncoding() != null)
                {
                    return new InputStreamReader(res.getResponseStream(), res.getEncoding());
                }
                else
                {
                    return new InputStreamReader(res.getResponseStream());
                }
            }
            catch (IOException e)
            {
                throw new WebScriptsPlatformException("Unable to load script: " + scriptPath, e);
            }
        }
        
        /**
         * @see org.springframework.extensions.webscripts.ScriptContent#isCachable()
         */
        public boolean isCachable()
        {
            return false;
        }
        
        /**
         * @see org.springframework.extensions.webscripts.ScriptContent#isSecure()
         */
        public boolean isSecure()
        {
            return false;
        }
    }
    
    /**
     * Converts an array of strings containing the full store root-relative
     * paths to AVM objects to an array of strings containing paths that are
     * relative to the "web application root".
     * 
     * For a standard AVM store, the root reference continues to be the
     * root of the store.
     * 
     * For a WCM store, the root reference becomes the web application root
     * directory - i.e. www/avm_webapps/ROOT
     * 
     * Next, this method uses the getStorePath() method to compute the
     * starting point for relative references.  The getStorePath() method
     * could return a fixed starting location (i.e. /alfresco) - this is
     * specified via the setPath() method.
     * 
     * In addition, if the setWebappPathPrefix() method is used, one can
     * more finely control how paths will vary for WCM stores.
     * 
     * Consider the case where the store path is set to "/alfresco" and
     * the webappPathPrefix is set to "/WEB-INF/classes".
     * 
     * For a standard AVM store, the relative root becomes:
     *    /alfresco
     *    
     * For a WCM store, the relative root becomes:
     *    www/avm_webapps/ROOT/WEB-INF/classes/alfresco
     *  
     * Note that the webappPathPrefix is only applied for WCM stores.
     * 
     * @param fullPaths
     */
    private void convertToRelativePaths(String[] fullPaths)
    {
        // incoming paths are full avm paths
        //
        // this might be: www/avm_webapps/<webappId>/<path> in the case of a full WCM store
        // or it might be: /<path> in the case of an plain AVM store (sitestore)
        //
        // we now want to truncate these to be relative to the storePath which was provided
        
        // determine the truncation string
        String truncationString = "";
        if (this.getStoreId() != null && this.getWebappId() != null)
        {
            // if we have a web app id, then we're looking at a WCM store
            truncationString = "www/avm_webapps/" + this.getWebappId();
            if (this.getStorePath() != null && this.getStorePath().length() > 0)
            {
                truncationString += this.getStorePath();
            }
        }
        
        // if we have a valid truncation string
        if (truncationString != null && truncationString.length() > 0)
        {
            // walk the files and convert paths so as to be relative to store path root
            for (int i = 0; i < fullPaths.length; i++)
            {
                // the full avm path to the object
                // if this is a WCM store, this will be the full path starting from www/avm_webapps...etc
                String fullPath = fullPaths[i];
                
                int x = fullPath.indexOf(truncationString);
                if (x != -1)
                {
                    fullPath = fullPath.substring(x + truncationString.length());
                }
                
                if (!fullPath.startsWith("/"))
                {
                    fullPath = "/" + fullPath;
                }
                
                fullPaths[i] = fullPath;
            }
        }
    }
}
