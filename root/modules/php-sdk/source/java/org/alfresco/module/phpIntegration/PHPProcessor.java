/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

package org.alfresco.module.phpIntegration;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.phpIntegration.lib.Node;
import org.alfresco.module.phpIntegration.lib.NodeFactory;
import org.alfresco.module.phpIntegration.lib.Repository;
import org.alfresco.module.phpIntegration.lib.ScriptObject;
import org.alfresco.module.phpIntegration.lib.Session;
import org.alfresco.module.phpIntegration.lib.Store;
import org.alfresco.processor.ProcessorExtension;
import org.alfresco.repo.jscript.ClasspathScriptLocation;
import org.alfresco.repo.processor.BaseProcessor;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.ScriptLocation;
import org.alfresco.service.cmr.repository.ScriptProcessor;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.TemplateProcessor;
import org.alfresco.service.namespace.QName;

import com.caucho.quercus.Quercus;
import com.caucho.quercus.env.ArrayValue;
import com.caucho.quercus.env.ArrayValueImpl;
import com.caucho.quercus.env.DoubleValue;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.JavaValue;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.StringInputStream;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.module.QuercusModule;
import com.caucho.quercus.page.QuercusPage;
import com.caucho.quercus.program.JavaClassDef;
import com.caucho.util.CharBuffer;
import com.caucho.vfs.ReadStream;
import com.caucho.vfs.StringWriter;
import com.caucho.vfs.VfsStream;
import com.caucho.vfs.WriteStream;

/**
 * 
 * @author Roy Wetherall
 */
public class PHPProcessor extends BaseProcessor implements TemplateProcessor, ScriptProcessor
{
    /** Keys to well known global values */
    public static final String GLOBAL_REPOSITORY = "_ALF_REPOSITORY";
    public static final String GLOBAL_SESSION = "_ALF_SESSION";
    public static final String GLOBAL_MODEL = "_ALF_MODEL";
    
    /** Key to value found in $_SERVER indicating that the Alfresco API is available */
    public static final String ALF_AVAILABLE = "ALF_AVAILABLE";
    
    /** Key used to store a reference to the service registry in the Quercus engine */
    public static final String KEY_SERVICE_REGISTRY = "ServiceRegistry";
    public static final String KEY_NODE_FACTORY = "NodeFactory";
    
    /** Quercus engine */
    private Quercus quercus; 
    
    /** Node Factory */
    private NodeFactory nodeFactory;
    
    /**
     * Constructor
     */
    public PHPProcessor()
    {
    	// Create the quercus engine
    	this.quercus = new Quercus();
    	
    	// Set the ALF_AVAILABLE server value to help with script reuse
    	this.quercus.setServerEnv(PHPProcessor.ALF_AVAILABLE, "true");
    }
        
    public void setNodeFactory(NodeFactory nodeFactory)
    {
        this.nodeFactory = nodeFactory;
    }
    
    /**
     * @see org.alfresco.repo.processor.BaseProcessor#register()
     */
    @Override
    public void register()
    {
        // Add the service registry as a special value
        this.quercus.setSpecial(KEY_SERVICE_REGISTRY, this.services);
        this.quercus.setSpecial(KEY_NODE_FACTORY, this.nodeFactory);
        
        // Call super class
        super.register();
    }
    
    /**
     * @see org.alfresco.service.cmr.repository.Processor#registerProcessorExtension(org.alfresco.service.cmr.repository.ProcessorExtension)
     */
    @SuppressWarnings("unchecked")
	@Override
    public void registerProcessorExtension(ProcessorExtension processorExtension)
    {
        // Call the base implementation
        super.registerProcessorExtension(processorExtension);
        
        // Deal with adding the extension to the quercus engine
        if (processorExtension instanceof PHPMethodExtension)
        {
            this.quercus.addModule((QuercusModule)processorExtension);    
            ((PHPMethodExtension)processorExtension).initialiseModule(this.quercus.findModule(processorExtension.getClass().getName()));
        }
        else if (processorExtension instanceof PHPObjectExtension)
        {
            try
            {
                Class clazz = Class.forName(((PHPObjectExtension)processorExtension).getExtensionClass());
                this.quercus.addJavaClass(processorExtension.getExtensionName(), clazz);
            }
            catch (ClassNotFoundException exception)
            {
                throw new PHPProcessorException("PHP Object Extension class '" + ((PHPObjectExtension)processorExtension).getExtensionClass() + "' could not be found.", exception);
            }
        }
        
    }
    
    /**
     * Executes a PHP script using the quercus engine.
     * 
     * @param script    the script as a string
     * @param out       the writer to direct the output of the PHP to.  This can be null if no output is required.
     * @param model     the context model for the script
     * @return          the return result of the executed PHP
     */
    private Object executeScript(String script, Writer out, Map<String, Object> model)
    {
        return executeScript(new StringInputStream(script), out, model);
    }
    
    /**
     * Executes the PHP script using the quercus engine.
     * 
     * @param is        the input stream containing the PHP to execute
     * @param out       the writer to direct the output of the PHP to.  This can be null if no output is required.
     * @param model     the context model for the script
     * @return Object   the return result of the executed PHP
     */
    private Object executeScript(InputStream is, Writer out, Map<String, Object> model)
    {
        try
        {
            // Create the string writer
            StringWriter writer = new StringWriter(new CharBuffer(1024));
            writer.openWrite();
            
            // Parse the page
            VfsStream stream = new VfsStream(is, null);        
            QuercusPage page = this.quercus.parse(new ReadStream(stream));
            
            // Execute the page
            WriteStream ws = new WriteStream(writer);
            Env env = new Env(this.quercus, page, ws, null, null);   
            env.start();
            
            // Add the repository and session as a global variables
            Repository repository = new Repository(env, "");
            env.setGlobalValue(GLOBAL_REPOSITORY, convertToValue(env, null, repository));
            Session session = repository.createSession("");
            env.setGlobalValue(GLOBAL_SESSION, convertToValue(env, null, session));
            
            // Map the contents of the passed model into global variables
            if (model != null)
            {
               env.setGlobalValue(GLOBAL_MODEL, convertToValue(env, session, model));
            }
            
            // Execute the page
            Value value = page.executeTop(env);
            
            // Make sure we flush because otherwise the result does not get written
            ws.flush();
           
            // Write to output
            String result = ((StringWriter)ws.getSource()).getString();            
            if (out != null)
            {
                out.write(result);
            }            
            
            // Return the result
            return value.toJavaObject();
        }
        catch (Exception exception)
        {
            throw new ScriptException("Error executing script.", exception);
        }
    }
    
    @SuppressWarnings("unchecked")
	public static Value convertToValue(Env env, Session session, Object value)
    {
        Value result = null;
        
        if (value instanceof String)
        {
            result = StringValue.create(value);
        }
        else if (value instanceof Integer)
        {  
            // TODO could do with a IntegerValue value ...
            result = DoubleValue.create(((Integer)value).doubleValue());
        }
        // TODO anyother primative value types that need converting ... 
        else if (value instanceof StoreRef)
        {
            if (session != null)
            {
                StoreRef storeRef = (StoreRef)value;
                Store store = session.getStore(storeRef.getIdentifier(), storeRef.getProtocol());
                if (store != null)
                {
                   result = convertToValue(env, session, store);                    
                }
            }
            
            // TODO do we need to raise the fact that the session, store or node way have been null ?? 
        }
        else if (value instanceof NodeRef)
        {
            if (session != null)
            {
                NodeRef nodeRef = (NodeRef)value;
                Store store = session.getStore(nodeRef.getStoreRef().getIdentifier(), nodeRef.getStoreRef().getProtocol());
                if (store != null)
                {
                    Node node = session.getNode(store, nodeRef.getId());
                    if (node != null)
                    {
                        result = convertToValue(env, session, node);
                    }
                }
            }
            
            // TODO do we need to raise the fact that the session, store or node way have been null ?? 
        }
        else if (value instanceof Map)
        {
            Map map = (Map)value;
            ArrayValue arrayValue = new ArrayValueImpl(map.size());
            for (Object objKey : map.keySet())
            {
                if (objKey instanceof String)
                {
                    String key = (String)objKey;
                    Object objValue = map.get(key);
                    arrayValue.put(StringValue.create(key), convertToValue(env, session, objValue));
                }
            }
            result = arrayValue;
        }
        else if (value instanceof ScriptObject)
        {
            try
            {
                JavaClassDef def = env.getQuercus().getModuleContext().getJavaClassDefinition(((ScriptObject)value).getScriptObjectName());
                def.init();
                result = new JavaValue(env, value, def);
            }
            catch (Exception exception)
            {
                // For some reason the class definition could not be retrieved so set to null
                result = NullValue.NULL;
            }
        }
        else
        {
            try
            {
                JavaClassDef def = env.getQuercus().getModuleContext().getJavaClassDefinition(value.getClass().toString());
                def.init();
                result = new JavaValue(env, value, def);
            }
            catch (Exception exception)
            {
                // Failed to get the java class for the definition so set to null
                result = NullValue.NULL;
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getModel(Object model)
    {
        Map<String, Object> result = null;
        if (model != null && model instanceof Map)
        {
            result = (Map<String, Object>)model;
        }
        else
        {
            result = new HashMap<String, Object>(2);
        }
        return result;
    }
    
    /* =========== Template Processor Methods ============== */
    
    /**
     * @see org.alfresco.service.cmr.repository.TemplateProcessor#process(java.lang.String, java.lang.Object, java.io.Writer)
     */
    public void process(String template, Object model, Writer out)
    {
        InputStream is = null;
        if (template.indexOf(StoreRef.URI_FILLER) != -1)
        {
            NodeRef ref = new NodeRef(template);
            if (this.services.getNodeService().exists(ref) == true)
            {
                ContentReader contentReader = this.services.getContentService().getReader(ref, ContentModel.PROP_CONTENT);
                if (contentReader != null)
                {
                    is = contentReader.getContentInputStream();
                }
                else
                {
                    throw new AlfrescoRuntimeException("The script (" + template + ") has no content.");
                }
            }
            else
            {
                throw new AlfrescoRuntimeException("Invalid node reference passed to PHP template processor. (" + template + ")");
            }
        }
        else
        {
            is = getClass().getClassLoader().getResourceAsStream(template);
        }

        try
        {
            executeScript(is, out, getModel(model));
        }
        finally
        {
            try { is.close(); } catch (IOException e) {e.printStackTrace();};
        }
    }

    /**
     * @see org.alfresco.service.cmr.repository.TemplateProcessor#processString(java.lang.String, java.lang.Object, java.io.Writer)
     */
    public void processString(String template, Object model, Writer out)
    {
        executeScript(template, out, getModel(model));
    }    
    
    /* =========== Script Processor Methods ============== */
    
    /**
     * @see org.alfresco.service.cmr.repository.ScriptProcessor#execute(org.alfresco.service.cmr.repository.ScriptLocation, java.util.Map)
     */
    public Object execute(ScriptLocation location, Map<String, Object> model)
    {
        return executeScript(location.getInputStream(), null, model);
    }

    /**
     * @see org.alfresco.service.cmr.repository.ScriptProcessor#execute(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, java.util.Map)
     */
    public Object execute(NodeRef nodeRef, QName contentProp, Map<String, Object> model)
    {
        ContentReader contentReader = this.services.getContentService().getReader(nodeRef, contentProp);
        if (contentReader == null)
        {
            throw new PHPProcessorException("PHP script has no content. (nodeRef=" + nodeRef.toString() + "; contentProp=" + contentProp.toString());
        }
        return executeScript(contentReader.getContentInputStream(), null, model);
    }

    /**
     * @see org.alfresco.service.cmr.repository.ScriptProcessor#execute(java.lang.String, java.util.Map)
     */
    public Object execute(String location, Map<String, Object> model)
    {
        ScriptLocation scriptLocation = new ClasspathScriptLocation(location);
        return execute(scriptLocation, model);
    }

    /**
     * @see org.alfresco.service.cmr.repository.ScriptProcessor#executeString(java.lang.String, java.util.Map)
     */
    public Object executeString(String script, Map<String, Object> model)
    {
        return executeScript(script, null, model);
    }

    /**
     * @see org.alfresco.service.cmr.repository.ScriptProcessor#reset()
     */
    public void reset()
    {
    }
}
