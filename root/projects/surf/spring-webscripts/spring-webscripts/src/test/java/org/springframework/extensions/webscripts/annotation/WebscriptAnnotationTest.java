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

package org.springframework.extensions.webscripts.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.extensions.webscripts.AbstractRuntimeContainer;
import org.springframework.extensions.webscripts.AbstractWebScriptServerTest;
import org.springframework.extensions.webscripts.ClassPathStore;
import org.springframework.extensions.webscripts.Registry;

/**
 * @author drq
 *
 */
public class WebscriptAnnotationTest  extends AbstractWebScriptServerTest 
{
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.AbstractWebScriptServerTest#setUp()
     */
    public void setUp() throws ServletException
    {
        super.setUp();
        // manually init our classpath store
        getClassPathStore().init();
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.AbstractWebScriptServerTest#getConfigLocations()
     */
    public ArrayList<String> getConfigLocations()
    {
        ArrayList<String> list = super.getConfigLocations();

        list.add("classpath:org/springframework/extensions/webscripts/stores/spring-webscripts-stores-context.xml");
        list.add("classpath:org/springframework/extensions/webscripts/annotation/spring-webscripts-annotation-test-context.xml");

        return list;
    }

    /**
     * @return
     */
    public ClassPathStore getClassPathStore()
    {
        return (ClassPathStore) getTestServer().getApplicationContext().getBean("webscripts.store.test");
    }

    /**
     * @return
     */
    public AbstractRuntimeContainer getWebscriptContainer() {
        return (AbstractRuntimeContainer) (getTestServer().getApplicationContext().getBean("webscripts.container.test"));
    }

    /**
     * @return
     */
    public Registry getWebscriptRegistry() {
        return getWebscriptContainer().getRegistry();
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testAnnotatedJavaScriptRootObject() throws Exception
    {
        // locate JavaScript root object
        Map<String, Object> scriptObjs = getWebscriptContainer().getScriptParameters();
        assertNotNull(scriptObjs);
        Object sampleObj = scriptObjs.get("sample");
        assertNotNull(sampleObj);
        assertTrue(sampleObj instanceof org.springframework.extensions.webscripts.annotation.samples.Sample);
        
        // validate WSClass annotation
        ScriptClass wsc = sampleObj.getClass().getAnnotation(ScriptClass.class);
        assertNotNull(wsc);
        assertNotNull(wsc.help());
        assertNotNull(wsc.code());
        
        Set<ScriptClassType> types = new HashSet<ScriptClassType>(Arrays.asList(wsc.types()));
        assertTrue(types.contains(ScriptClassType.JavaScriptRootObject));
        assertTrue(types.contains(ScriptClassType.TemplateRootObject));
        
        
        // validate WSMethod annotation
        Class [] args = {Integer.TYPE};
        Method m = sampleObj.getClass().getMethod("getMessages",args);
        assertNotNull(m);
        ScriptMethod wsm = m.getAnnotation(ScriptMethod.class);
        assertNotNull(wsm);
        assertNotNull(wsm.code());
        assertNotNull(wsm.help());
        assertNotNull(wsm.output());
        assertTrue(wsm.type().equals(ScriptMethodType.READ));
        
        // validate WSParameter annotation
        Annotation[][] wsps = m.getParameterAnnotations();
        assertNotNull(wsps);
        Annotation[] wsps1 = wsps[0];
        assertNotNull(wsps1);
        Object wspObj = wsps1[0];
        assertTrue(wspObj instanceof org.springframework.extensions.webscripts.annotation.ScriptParameter);
        ScriptParameter wsp = (ScriptParameter) wspObj;
        assertNotNull(wsp.help());
    }
    
}
