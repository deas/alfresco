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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;


/**
 * Encapsulates the execution of a single Web Script.
 *
 * Sub-classes of WebScriptRuntime maintain the execution environment e.g. servlet
 * request & response.
 * 
 * A new instance of WebScriptRuntime is required for each invocation.
 * 
 * @author davidc
 */
public abstract class AbstractRuntime implements Runtime
{
    // Logger
    protected static final Log logger = LogFactory.getLog(AbstractRuntime.class);

    /** Component Dependencies */
    protected RuntimeContainer container;
    protected WebScriptSession session;

    /**
     * Construct
     * 
     * @param container  web script context
     */
    public AbstractRuntime(RuntimeContainer container)
    {
        this.container = container;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Runtime#getContainer()
     */
    public Container getContainer()
    {
        return container;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Runtime#getSession()
     */
    public WebScriptSession getSession()
    {
        if (session == null)
        {
            session = new RuntimeSession(createSessionFactory());
        }
        return session;
    }
    
    private URLModelFactory urlModelFactory = null;
    
    public void setURLModelFactory(URLModelFactory urlModelFactory)
    {
        this.urlModelFactory = urlModelFactory;
    }
    
    private URLModel createURLModel(WebScriptRequest request)
    {
        URLModel urlModel = null;
        if (this.urlModelFactory == null)
        {
            urlModel = new DefaultURLModel(request);
        }
        else
        {
            urlModel = this.urlModelFactory.createURLModel(request);
        }
        return urlModel;
    }
    /**
     * Execute the Web Script encapsulated by this Web Script Runtime
     */
    final public void executeScript()
    {
        final boolean debug = logger.isDebugEnabled();
        long startRuntime = 0L;
        if (debug) startRuntime = System.nanoTime();

        final String method = getScriptMethod();
        String scriptUrl = null;
        Match match = null;

        try
        {
            // extract script url
            scriptUrl = getScriptUrl();
            if (scriptUrl == null || scriptUrl.length() == 0)
            {
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, "Script URL not specified");
            }

            if (debug)
                logger.debug("(Runtime=" + getName() + ", Container=" + container.getName() + ") Processing script url ("  + method + ") " + scriptUrl);

            WebScriptRequest scriptReq = null;
            WebScriptResponse scriptRes = null;
            Authenticator auth = null;
            
            RequiredAuthentication containerRequiredAuth = container.getRequiredAuthentication();
            
            if (!containerRequiredAuth.equals(RequiredAuthentication.none))
            {
                // Create initial request & response
                scriptReq = createRequest(null);
                scriptRes = createResponse();
                auth = createAuthenticator();
                
                if (debug)
                    logger.debug("(Runtime=" + getName() + ", Container=" + container.getName() + ") Container requires pre-auth: "+containerRequiredAuth);
                
                boolean preAuth = true;
                
                if (auth != null && auth.emptyCredentials())
                {
                    // check default (unauthenticated) domain
                    match = container.getRegistry().findWebScript(method, scriptUrl);
                    if ((match != null) && (match.getWebScript().getDescription().getRequiredAuthentication().equals(RequiredAuthentication.none)))
                    {
                        preAuth = false;
                    }
                }
                
                if (preAuth && (!container.authenticate(auth, containerRequiredAuth)))
                {
                    return; // return response (eg. prompt for un/pw if status is 401 or redirect)
                }
            }
            
            if (match == null)
            {
                match = container.getRegistry().findWebScript(method, scriptUrl);
            }
            
            if (match == null || match.getKind() == Match.Kind.URI)
            {
                if (match == null)
                {
                    String msg = "Script url " + scriptUrl + " does not map to a Web Script.";
                    if (debug) logger.debug(msg);
                    throw new WebScriptException(HttpServletResponse.SC_NOT_FOUND, msg);
                }
                else
                {
                    String msg = "Script url " + scriptUrl + " does not support the method " + method;
                    if (debug) logger.debug(msg);
                    throw new WebScriptException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
                }
            }

            // create web script request & response
            scriptReq = createRequest(match);
            scriptRes = createResponse();
            
            if (auth == null)
            {
                // not pre-authenticated
                auth = createAuthenticator();
            }
            
            if (debug) logger.debug("Agent: " + scriptReq.getAgent());

            long startScript = System.nanoTime();
            final WebScript script = match.getWebScript();
            final Description description = script.getDescription();
            
            try
            {
                if (debug)
                {
                    String reqFormat = scriptReq.getFormat();
                    String format = (reqFormat == null || reqFormat.length() == 0) ? "[undefined]" : reqFormat;
                    Description desc = scriptReq.getServiceMatch().getWebScript().getDescription();
                    logger.debug("Invoking Web Script " + description.getId() + " (format " + format + ", style: " + desc.getFormatStyle() + ", default: " + desc.getDefaultFormat() + ")");
                }

                executeScript(scriptReq, scriptRes, auth);
            }
            finally
            {
                if (debug)
                {
                    long endScript = System.nanoTime();
                    logger.debug("Web Script " + description.getId() + " executed in " + (endScript - startScript)/1000000f + "ms");
                }
            }
        }
        catch (Throwable e)
        {
            if (beforeProcessError(match, e))
            {
                if (e instanceof WebScriptException && (((WebScriptException)e).getStatus() == HttpServletResponse.SC_NOT_FOUND || 
                                                        ((WebScriptException)e).getStatus() == HttpServletResponse.SC_UNAUTHORIZED))
                {
                    // debug level output for "missing" WebScripts and API URLs entered incorrectly
                    String errorCode = ((WebScriptException)e).getStatus() == HttpServletResponse.SC_NOT_FOUND ? "NOT FOUND" : "UNAUTHORIZED";
                    logger.debug("Webscript did not execute. (" + errorCode + "): " + e.getMessage());
                }
            	// log error on server so its not swallowed and lost
                else if (logger.isErrorEnabled())
                {
                    logger.error("Exception from executeScript - redirecting to status template error: " + e.getMessage(), e);
                }
                
                // setup context
                WebScriptRequest req = createRequest(match);
                WebScriptResponse res = createResponse();
                String format = req.getFormat();
    
                // extract status code, if specified
                int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                StatusTemplate statusTemplate = null;
                Map<String, Object> statusModel = null;
                if (e instanceof WebScriptException)
                {
                    WebScriptException we = (WebScriptException)e;
                    statusCode = we.getStatus();
                    statusTemplate = we.getStatusTemplate();
                    statusModel = we.getStatusModel();
                }
    
                // retrieve status template for response rendering
                if (statusTemplate == null)
                {
                    // locate status template
                    // NOTE: search order...
                    //   1) root located <status>.ftl
                    //   2) root located <format>.status.ftl
                    //   3) root located status.ftl
                    statusTemplate = getStatusCodeTemplate(statusCode);
                    
                    String validTemplatePath = container.getTemplateProcessorRegistry().findValidTemplatePath(statusTemplate.getPath());
                    if (validTemplatePath == null)
                    {
                        if (format != null && format.length() > 0)
                        {
                            // if we have a format try and get the format specific status template
                            statusTemplate = getFormatStatusTemplate(format);
                            validTemplatePath = container.getTemplateProcessorRegistry().findValidTemplatePath(statusTemplate.getPath());
                        }
                        
                        // if we don't have a valid template path get the default status template
                        if (validTemplatePath == null)
                        {
                            statusTemplate = getStatusTemplate();
                            validTemplatePath = container.getTemplateProcessorRegistry().findValidTemplatePath(statusTemplate.getPath());
                        }
                        
                        // throw error if a status template could not be found
                        if (validTemplatePath == null)
                        {
                            throw new WebScriptException("Failed to find status template " + statusTemplate.getPath() + " (format: " + statusTemplate.getFormat() + ")");
                        }
                    }                
                }
    
                // create basic model for all information known at this point, if one hasn't been pre-provided
                if (statusModel == null || statusModel.equals(Collections.EMPTY_MAP))
                {
                    statusModel = new HashMap<String, Object>(8, 1.0f);
                    statusModel.putAll(container.getTemplateParameters());
                    statusModel.put("url", createURLModel(req));
                    if (match != null && match.getWebScript() != null)
                    {
                        statusModel.put("webscript", match.getWebScript().getDescription());  
                    }
                }
    
                // add status to model
                Status status = new Status();
                status.setCode(statusCode);
                status.setMessage(e.getMessage() != null ? e.getMessage() : e.toString());
                status.setException(e);
                statusModel.put("status", status);
    
                // render output
                String mimetype = container.getFormatRegistry().getMimeType(req.getAgent(), statusTemplate.getFormat());
                if (mimetype == null)
                {
                    throw new WebScriptException("Web Script format '" + statusTemplate.getFormat() + "' is not registered");
                }
                
                if (debug)
                {
                    logger.debug("Force success status header in response: " + req.forceSuccessStatus());
                    logger.debug("Sending status " + statusCode + " (Template: " + statusTemplate.getPath() + ")");
                    logger.debug("Rendering response: content type=" + mimetype);
                }
    
                Cache cache = new Cache();
                cache.setNeverCache(true);
                res.setCache(cache);
                res.setStatus(req.forceSuccessStatus() ? HttpServletResponse.SC_OK : statusCode);
                res.setContentType(mimetype + ";charset=UTF-8");
                try
                {
                    String validTemplatePath = container.getTemplateProcessorRegistry().findValidTemplatePath(statusTemplate.getPath());                
                    TemplateProcessor statusProcessor = container.getTemplateProcessorRegistry().getTemplateProcessor(validTemplatePath);
                    statusProcessor.process(validTemplatePath, statusModel, res.getWriter());
                }
                catch (Exception e1)
                {
                    logger.error("Internal error", e1);
                    throw new WebScriptException("Internal error", e1);
                }
            }
        }
        finally
        {
            if (debug)
            {
                long endRuntime = System.nanoTime();
                logger.debug("Processed script url ("  + method + ") " + scriptUrl + " in " + (endRuntime - startRuntime)/1000000f + "ms");
            }
        }
    }
    
    /**
     * Before processing an error exception - hook point to allow additional processing
     * of the exception based on the runtime. This allows runtime to handle errors themselves
     * if required - for example silently ignoring missing webscripts. 
     * 
     * @param match WebScript that was processed and caused the error
     * @param e Exception that occured during webscript processing
     * 
     * @return true to continue default error processing, false to assume handling is complete
     */
    protected boolean beforeProcessError(Match match, Throwable e)
    {
        // default implementation simply continues default processing
        return true;
    }

    /**
     * Execute script given the specified context
     * 
     * @param scriptReq
     * @param scriptRes
     * @param auth
     * 
     * @throws IOException
     */
    protected void executeScript(WebScriptRequest scriptReq, WebScriptResponse scriptRes, Authenticator auth)
        throws IOException
    {
        container.executeScript(scriptReq, scriptRes, auth);
    }

    /**
     * Get code specific Status Template path
     * 
     * @param statusCode
     * @return  path
     */
    protected StatusTemplate getStatusCodeTemplate(int statusCode)
    {
        return new StatusTemplate("/" + statusCode + ".ftl", WebScriptResponse.HTML_FORMAT);
    }
    
    /**
     * Get format Status Template path
     * 
     * @param format
     * @return  path
     */
    protected StatusTemplate getFormatStatusTemplate(String format)
    {
        return new StatusTemplate("/" + format + ".status.ftl", format);
    }
    
    /**
     * Get Status Template path
     * 
     * @return  path
     */
    protected StatusTemplate getStatusTemplate()
    {
        return new StatusTemplate("/status.ftl", WebScriptResponse.HTML_FORMAT);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Runtime#getScriptParameters()
     */
    public Map<String, Object> getScriptParameters()
    {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("session", getSession());
        
        ScriptParameterFactoryRegistry registry = container.getScriptParameterFactoryRegistry();
        if (registry != null)
        {
            Collection<ScriptParameterFactory> factories = registry.getScriptParameterFactories();
            if (factories != null)
            {
                for (ScriptParameterFactory factory : factories)
                {
                    parameters.putAll(factory.getParameters(this));
                }
            }
        }
        
        return parameters;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Runtime#getTemplateParameters()
     */
    public Map<String, Object> getTemplateParameters()
    {
        return Collections.emptyMap();
    }
    
    /**
     * Get the Web Script Method  e.g. get, post
     * 
     * @return  web script method
     */
    protected abstract String getScriptMethod();

    /**
     * Get the Web Script Url
     * 
     * @return  web script url
     */
    protected abstract String getScriptUrl();
    
    /**
     * Create a Web Script Request
     * 
     * @param match  web script matching the script method and url
     * @return  web script request
     */
    protected abstract WebScriptRequest createRequest(Match match);
    
    /**
     * Create a Web Script Response
     * 
     * @return  web script response
     */
    protected abstract WebScriptResponse createResponse();
    
    /**
     * Create a Web Script Authenticator
     * 
     * @return  web script authenticator
     */
    protected abstract Authenticator createAuthenticator();
    
    /**
     * Create a Web Script Session
     */
    protected abstract WebScriptSessionFactory createSessionFactory();

    /**
     * Helper to retrieve real (last) Web Script Request in a stack of wrapped Web Script requests
     * 
     * @param request
     * @return
     */
    protected static WebScriptRequest getRealWebScriptRequest(WebScriptRequest request)
    {
        WebScriptRequest real = request;
        while(real instanceof WrappingWebScriptRequest)
        {
            real = ((WrappingWebScriptRequest)real).getNext();
        }
        return real;
    }

    /**
     * Helper to retrieve real (last) Web Script Response in a stack of wrapped Web Script responses
     * 
     * @param response
     * @return
     */
    protected static WebScriptResponse getRealWebScriptResponse(WebScriptResponse response)
    {
        WebScriptResponse real = response;
        while(real instanceof WrappingWebScriptResponse)
        {
            real = ((WrappingWebScriptResponse)real).getNext();
        }
        return real;
    }

    /**
     * Session whose values are namespaced
     */
    private static class RuntimeSession implements WebScriptSession
    {
        private static final WebScriptSession NOOP_WEBSCRIPTSESSION = new NOOPWebScriptSession();
        private WebScriptSessionFactory sessionFactory;
        private WebScriptSession session;
        
        public RuntimeSession(WebScriptSessionFactory sessionFactory)
        {
            this.sessionFactory = sessionFactory;
        }

        public String getId()
        {
            return getSession().getId();
        }
        
        public Object getValue(String name)
        {
            return getSession().getValue(name);
        }

        public void removeValue(String name)
        {
            getSession().removeValue(name);
        }

        public void setValue(String name, Object value)
        {
            getSession().setValue(name, value);
        }
        
        private WebScriptSession getSession()
        {
            if (session == null && sessionFactory != null)
            {
                session = sessionFactory.createSession();
            }
            if (session == null)
            {
                session = NOOP_WEBSCRIPTSESSION;
            }
            return session;
        }
        
        /**
         * No-op implementation of WebScriptSession
         */
        private static class NOOPWebScriptSession implements WebScriptSession
        {
            public String getId()
            {
                return null;
            }
            
            public Object getValue(String name)
            {
                return null;
            }

            public void removeValue(String name)
            {
            }

            public void setValue(String name, Object value)
            {
            }
        }
    }
    
}
