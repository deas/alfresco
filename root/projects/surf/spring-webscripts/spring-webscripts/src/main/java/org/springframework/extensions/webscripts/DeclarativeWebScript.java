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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Script/template driven based implementation of an Web Script
 *
 * @author davidc
 */
public class DeclarativeWebScript extends AbstractWebScript 
{
    // Logger
    private static final Log logger = LogFactory.getLog(DeclarativeWebScript.class);  
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScript#execute(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    final public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        // retrieve requested format
        String format = req.getFormat();

        try
        {
            // establish mimetype from format
            String mimetype = getContainer().getFormatRegistry().getMimeType(req.getAgent(), format);
            if (mimetype == null)
            {
                throw new WebScriptException("Web Script format '" + format + "' is not registered");
            }
            
            // construct model for script / template
            Status status = new Status();
            Cache cache = new Cache(getDescription().getRequiredCache());
            Map<String, Object> model = executeImpl(req, status, cache);
            if (model == null)
            {
                model = new HashMap<String, Object>(8, 1.0f);
            }
            model.put("status", status);
            model.put("cache", cache);
            
            try
            {
                // execute script if it exists
                ScriptDetails script = getExecuteScript(req.getContentType());
                if (script != null)
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Executing script " + script.getContent().getPathDescription());
                    
                    Map<String, Object> scriptModel = createScriptParameters(req, res, script, model);
                    
                    // add return model allowing script to add items to template model
                    Map<String, Object> returnModel = new HashMap<String, Object>(8, 1.0f);
                    scriptModel.put("model", returnModel);
                    executeScript(script.getContent(), scriptModel);
                    mergeScriptModelIntoTemplateModel(script.getContent(), returnModel, model);
                }
        
                // create model for template rendering
                Map<String, Object> templateModel = createTemplateParameters(req, res, model);
                
                // is a redirect to a status specific template required?
                if (status.getRedirect())
                {
                    sendStatus(req, res, status, cache, format, templateModel);
                }
                else
                {
                    // render output
                    int statusCode = status.getCode();
                    if (statusCode != HttpServletResponse.SC_OK && !req.forceSuccessStatus())
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Force success status header in response: " + req.forceSuccessStatus());
                            logger.debug("Setting status " + statusCode);
                        }
                        res.setStatus(statusCode);
                    }
                    
                    // apply location
                    String location = status.getLocation();
                    if (location != null && location.length() > 0)
                    {
                        if (logger.isDebugEnabled())
                            logger.debug("Setting location to " + location);
                        res.setHeader(WebScriptResponse.HEADER_LOCATION, location);
                    }
    
                    // apply cache
                    res.setCache(cache);
                    
                    String callback = null;
                    if (getContainer().allowCallbacks())
                    {
                        callback = req.getJSONCallback();
                    }
                    if (format.equals(WebScriptResponse.JSON_FORMAT) && callback != null)
                    {
                        if (logger.isDebugEnabled())
                            logger.debug("Rendering JSON callback response: content type=" + Format.JAVASCRIPT.mimetype() + ", status=" + statusCode + ", callback=" + callback);
                        
                        // NOTE: special case for wrapping JSON results in a javascript function callback
                        res.setContentType(Format.JAVASCRIPT.mimetype() + ";charset=UTF-8");
                        res.getWriter().write((callback + "("));
                    }
                    else
                    {
                        if (logger.isDebugEnabled())
                            logger.debug("Rendering response: content type=" + mimetype + ", status=" + statusCode);
    
                        res.setContentType(mimetype + ";charset=UTF-8");
                    }
                
                    // render response according to requested format
                    renderFormatTemplate(format, templateModel, res.getWriter());
                    
                    if (format.equals(WebScriptResponse.JSON_FORMAT) && callback != null)
                    {
                        // NOTE: special case for wrapping JSON results in a javascript function callback
                        res.getWriter().write(")");
                    }
                }
            }
            finally
            {
                // perform any necessary cleanup
                executeFinallyImpl(req, status, cache, model);
            }
        }
        catch(Throwable e)
        {
            if (logger.isDebugEnabled())
            {
                StringWriter stack = new StringWriter();
                e.printStackTrace(new PrintWriter(stack));
                logger.debug("Caught exception; decorating with appropriate status template : " + stack.toString());
            }

            throw createStatusException(e, req, res);
        }
    }
    
    /**
     * Merge script generated model into template-ready model
     * 
     * @param scriptContent    script content
     * @param scriptModel      script model
     * @param templateModel    template model
     */
    final private void mergeScriptModelIntoTemplateModel(ScriptContent scriptContent, Map<String, Object> scriptModel, Map<String, Object> templateModel)
    {
        // determine script processor
        ScriptProcessor scriptProcessor = getContainer().getScriptProcessorRegistry().getScriptProcessor(scriptContent);        
        if (scriptProcessor != null)
        {
            for (Map.Entry<String, Object> entry : scriptModel.entrySet())
            {
                // retrieve script model value
                Object value = entry.getValue();
                Object templateValue = scriptProcessor.unwrapValue(value);
                templateModel.put(entry.getKey(), templateValue);
            }
        }
    }

    /**
     * Execute custom Java logic
     * 
     * @param req  Web Script request
     * @param status Web Script status
     * @return  custom service model
     * @deprecated
     */
    protected Map<String, Object> executeImpl(WebScriptRequest req, WebScriptStatus status)
    {
        return null;
    }

    /**
     * Execute custom Java logic
     * 
     * @param req  Web Script request
     * @param status Web Script status
     * @return  custom service model
     * @deprecated
     */
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        return executeImpl(req, new WebScriptStatus(status));
    }

    /**
     * Execute custom Java logic
     * 
     * @param  req  Web Script request
     * @param  status Web Script status
     * @param  cache  Web Script cache
     * @return  custom service model
     */
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // NOTE: Redirect to those web scripts implemented before cache support and v2.9
        return executeImpl(req, status);
    }

    /**
     * Execute custom Java logic to clean up any resources
     *  
     * @param req  Web Script request
     * @param status  Web Script status
     * @param cache  Web Script cache
     * @param model  model
     */
    protected void executeFinallyImpl(WebScriptRequest req, Status status, Cache cache, Map<String, Object> model)
    {
    }
    
    
    /**
     * Render a template (of given format) to the Web Script Response
     * 
     * @param format  template format (null, default format)  
     * @param model  data model to render
     * @param writer  where to output
     */
    final protected void renderFormatTemplate(String format, Map<String, Object> model, Writer writer)
    {
        format = (format == null) ? "" : format;

        String templatePath = getDescription().getId() + "." + format;

        if (logger.isDebugEnabled())
            logger.debug("Rendering template '" + templatePath + "'");

        renderTemplate(templatePath, model, writer);
    }
    
    /**
     * Get map of template parameters that are available with given request.
     * This method is for FreeMarker Editor Extension plugin of Surf Dev Tools.
     * 
     * @param req webscript request
     * @param res webscript response
     * @return
     * @throws IOException
     */
    public  Map<String, Object> getTemplateModel(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
     // construct model for script / template
        Status status = new Status();
        Cache cache = new Cache(getDescription().getRequiredCache());
        Map<String, Object> model = new HashMap<String, Object>(8, 1.0f);
        
        model.put("status", status);
        model.put("cache", cache);
        
        // execute script if it exists
        ScriptDetails script = getExecuteScript(req.getContentType());
        if (script != null)
        {                    
            Map<String, Object> scriptModel = createScriptParameters(req, res, script, model);                    
            // add return model allowing script to add items to template model
            Map<String, Object> returnModel = new HashMap<String, Object>(8, 1.0f);
            scriptModel.put("model", returnModel);
            executeScript(script.getContent(), scriptModel);
            mergeScriptModelIntoTemplateModel(script.getContent(), returnModel, model);
        }
        // create model for template rendering
        return createTemplateParameters(req, res, model);
    }

}
