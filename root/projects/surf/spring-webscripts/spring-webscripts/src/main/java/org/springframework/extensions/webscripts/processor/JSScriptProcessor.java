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

package org.springframework.extensions.webscripts.processor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.WrappedException;
import org.springframework.extensions.surf.core.scripts.ScriptException;
import org.springframework.extensions.surf.core.scripts.ScriptResourceHelper;
import org.springframework.extensions.surf.core.scripts.ScriptResourceLoader;
import org.springframework.extensions.surf.exception.WebScriptsPlatformException;
import org.springframework.extensions.webscripts.NativeMap;
import org.springframework.extensions.webscripts.ScriptContent;
import org.springframework.extensions.webscripts.ScriptValueConverter;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.util.FileCopyUtils;


/**
 * JS Script Processor for Surf Web Framework
 * 
 * @author davidc
 * @author kevinr
 */
public class JSScriptProcessor extends AbstractScriptProcessor implements ScriptResourceLoader
{
    private static final Log logger = LogFactory.getLog(JSScriptProcessor.class);
    private static WrapFactory wrapFactory = new PresentationWrapFactory(); 
    
    private static final String PATH_CLASSPATH = "classpath:";
    
    /** Pre initialized secure scope object. */
    private Scriptable secureScope;
    
    /** Pre initialized non secure scope object. */
    private Scriptable nonSecureScope;
    
    /** Flag to enable or disable runtime script compliation */
    private boolean compile = true;
    
    /** Flag to enable the sharing of sealed root scopes between scripts executions */
    private boolean shareSealedScopes = true;
    
    /** Cache of runtime compiled script instances */
    private Map<String, Script> scriptCache = new ConcurrentHashMap<String, Script>(256);
    
    
    /**
     * @param compile   the compile flag to set
     */
    public void setCompile(boolean compile)
    {
        this.compile = compile;
    }
    
    /**
     * @param shareSealedScopes true to allow sharing of sealed scopes between script executions - set to
     * false to disable this feature and ensure that a new scope is created for each executed script.
     */
    public void setShareSealedScopes(boolean shareSealedScopes)
    {
        this.shareSealedScopes = shareSealedScopes;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.processor.Processor#getExtension()
     */
    public String getExtension()
    {
        return "js";
    }

    /* (non-Javadoc)
     * @see org.alfresco.processor.Processor#getName()
     */
    public String getName()
    {
        return "javascript";
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.processor.AbstractScriptProcessor#init()
     */
    public void init()
    {
        super.init();
        
        this.initProcessor();        
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptProcessor#findScript(java.lang.String)
     */
    public ScriptContent findScript(String path)
    {
        return getScriptLoader().getScript(path);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptProcessor#executeScript(java.lang.String, java.util.Map)
     */
    public Object executeScript(String path, Map<String, Object> model)
    {
        // locate script within web script stores
        ScriptContent scriptLocation = findScript(path);
        if (scriptLocation == null)
        {
            throw new WebScriptException("Unable to locate script " + path);
        }
        // execute script
        return executeScript(scriptLocation, model);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptProcessor#executeScript(org.alfresco.web.scripts.ScriptContent, java.util.Map)
     */
    public Object executeScript(ScriptContent location, Map<String, Object> model)
    {
        try
        {
            // test the cache for a pre-compiled script matching our path
            String path = location.getPath();
            Script script = null;
            if (this.compile && location.isCachable())
            {
                script = this.scriptCache.get(path);
            }
            if (script == null)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Resolving and compiling script path: " + path);
                
                // retrieve script content and resolve imports
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                FileCopyUtils.copy(location.getInputStream(), os);  // both streams are closed
                byte[] bytes = os.toByteArray();
                String source = new String(bytes, "UTF-8");
                source = ScriptResourceHelper.resolveScriptImports(source, this, logger);
                
                // compile the script and cache the result
                Context cx = Context.enter();
                try
                {
                    script = cx.compileString(source, path, 1, null);
                    
                    // We do not worry about more than one user thread compiling the same script.
                    // If more than one request thread compiles the same script and adds it to the
                    // cache that does not matter - the results will be the same. Therefore we
                    // rely on the ConcurrentHashMap impl to deal both with ensuring the safety of the
                    // underlying structure with asynchronous get/put operations and for fast
                    // multi-threaded access to the common cache.
                    if (this.compile && location.isCachable())
                    {
                        this.scriptCache.put(path, script);
                    }
                }
                finally
                {
                    Context.exit();
                }
            }
            
            return executeScriptImpl(script, model, location.isSecure());
        }
        catch (ScriptException se)
        {
            throw new WebScriptException("Failed to load script '" + location.toString() + "': " + se.getMessage(), se);
        }
        catch (Throwable e)
        {
            throw new WebScriptException("Failed to execute script '" + location.toString() + "': " + e.getMessage(), e);
        }
    }

    /**
     * Load a script content from the specific resource path.
     *  
     * @param resource      Script resource to load. Supports either classpath: prefix syntax or a
     *                      resource path within the webscript stores. 
     * 
     * @return the content from the resource, null if not recognised format
     * 
     * @throws ConfigServiceRuntimeException on any IO or ContentIO error
     */
    public String loadScriptResource(String resource)
    {
        if (resource.startsWith(PATH_CLASSPATH))
        {
            try
            {
                // load from classpath
                String scriptClasspath = resource.substring(PATH_CLASSPATH.length());
                InputStream stream = getClass().getClassLoader().getResource(scriptClasspath).openStream();
                if (stream == null)
                {
                    throw new ScriptException("Unable to load included script classpath resource: " + resource);
                }
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                FileCopyUtils.copy(stream, os);  // both streams are closed
                byte[] bytes = os.toByteArray();
                // create the string from the byte[] using encoding if necessary
                return new String(bytes, "UTF-8");
            }
            catch (IOException err)
            {
                throw new ScriptException("Unable to load included script classpath resource: " + resource);
            }
        }
        else
        {
            // locate script within web script stores
            ScriptContent scriptLocation = findScript(resource);
            if (scriptLocation == null)
            {
                throw new ScriptException("Unable to locate script " + resource);
            }
            try
            {   
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                FileCopyUtils.copy(scriptLocation.getInputStream(), os);  // both streams are closed
                byte[] bytes = os.toByteArray();
                return new String(bytes, "UTF-8");
            }
            catch (Throwable e)
            {
                throw new ScriptException(
                        "Failed to load script '" + scriptLocation.toString() + "': " + e.getMessage(), e);
            }
        }
    }

    /**
     * Execute the supplied script content.
     * 
     * @param script        The script to execute.
     * @param model         Data model containing objects to be added to the root scope.
     * @param secure        True if the script is considered secure and may access java.* libs directly
     * 
     * @return result of the script execution, can be null.
     * 
     * @throws ConfigServiceRuntimeException
     */
    private Object executeScriptImpl(Script script, Map<String, Object> model, boolean secure)
    {
        // execute script
        long startTime = 0;
        if (logger.isDebugEnabled())
        {
            startTime = System.nanoTime();
        }
        
        Context cx = Context.enter();
        cx.setOptimizationLevel(1);
        try
        {
            // Create a thread-specific scope from one of the shared scopes.
            // See http://www.mozilla.org/rhino/scopes.html
            cx.setWrapFactory(wrapFactory);
            Scriptable scope;
            if (this.shareSealedScopes)
            {
                Scriptable sharedScope = secure ? this.nonSecureScope : this.secureScope;
                scope = cx.newObject(sharedScope);
                scope.setPrototype(sharedScope);
                scope.setParentScope(null);
            }
            else
            {
                scope = initScope(cx, secure, false);
            }
            
            // there's always a model, if only to hold the extension objects
            if (model == null)
            {
                model = new HashMap<String, Object>();
            }
            
            // add the global scripts
            addProcessorModelExtensions(model);
            
            // insert supplied object model into root of the default scope
            for (String key : model.keySet())
            {
                Object obj = model.get(key);
                ScriptableObject.putProperty(scope, key, obj);
            }
            
            // execute the script and return the result
            Object result = script.exec(cx, scope);
            return result;
        }
        catch (WrappedException w)
        {
            Throwable err = w.getWrappedException();
            throw new WebScriptException(err.getMessage(), err);
        }
        catch (Throwable e)
        {
            throw new WebScriptException(e.getMessage(), e);
        }
        finally
        {
            Context.exit();

            if (logger.isDebugEnabled())
            {
                long endTime = System.nanoTime();
                logger.debug("Time to execute script: " + (endTime - startTime)/1000000f + "ms");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptProcessor#unwrapValue(java.lang.Object)
     */
    public Object unwrapValue(Object value)
    {
        return ScriptValueConverter.unwrapValue(value);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptProcessor#reset()
     */
    public void reset()
    {
        init();
        this.scriptCache.clear();
    }
    
    /**
     * Inits the processor.
     */
    protected void initProcessor()
    {
        // Initialize the secure scope
        Context cx = Context.enter();
        try
        {
            cx.setWrapFactory(wrapFactory);
            this.secureScope = initScope(cx, false, true);
        }
        finally
        {
            Context.exit();
        }
        
        // Initialize the non-secure scope
        cx = Context.enter();
        try
        {
            cx.setWrapFactory(wrapFactory);
            this.nonSecureScope = initScope(cx, true, true);
        }
        finally
        {
            Context.exit();
        }
    }
    
    /**
     * Initializes a scope for script execution. The easiest way to embed Rhino is just to create a new scope this
     * way whenever you need one. However, initStandardObjects() is an expensive method to call and it allocates a
     * fair amount of memory.
     * 
     * @param cx        the thread execution context
     * @param secure    Do we consider the script secure? When <code>false</code> this ensures the script may not
     *                  access insecure java.* libraries or import any other classes for direct access - only the
     *                  configured root host objects will be available to the script writer.
     * @param sealed    Should the scope be sealed, making it immutable? This should be <code>true</code> if a scope
     *                  is to be reused.
     * @return the scope object
     */
    protected Scriptable initScope(Context cx, boolean secure, boolean sealed)
    {
        Scriptable scope;
        if (secure)
        {
            // Initialise the non-secure scope
            // allow access to all libraries and objects, including the importer
            // @see http://www.mozilla.org/rhino/ScriptingJava.html
            scope = new ImporterTopLevel(cx, sealed);
        }
        else
        {
            // Initialise the secure scope
            scope = cx.initStandardObjects(null, sealed);
            // remove security issue related objects - this ensures the script may not access
            // unsecure java.* libraries or import any other classes for direct access - only
            // the configured root host objects will be available to the script writer
            scope.delete("Packages");
            scope.delete("getClass");
            scope.delete("java");
        }
        return scope;
    }


    /**
     * Wrap Factory for Rhino Script Engine
     * 
     * @author davidc
     */
    public static class PresentationWrapFactory extends WrapFactory
    {
        /* (non-Javadoc)
         * @see org.mozilla.javascript.WrapFactory#wrapAsJavaObject(org.mozilla.javascript.Context, org.mozilla.javascript.Scriptable, java.lang.Object, java.lang.Class)
         */
        public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class staticType)
        {
            if (javaObject instanceof Map)
            {
                return new NativeMap(scope, (Map)javaObject);
            }
            return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
        }
    }    
}