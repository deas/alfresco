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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.extensions.webscripts.annotation.samples.SampleMessage;

/**
 * @author drq
 *
 */
public class WebscriptAnnotation2Test extends TestCase
{
    private Set<BeanDefinition> components;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        ClassPathScanningCandidateComponentProvider provider =
            new ClassPathScanningCandidateComponentProvider(true);
        String basePackage = "org.springframework.extensions.webscripts.annotation";

        provider.addIncludeFilter(new AnnotationTypeFilter(ScriptClass.class));

        components = provider.findCandidateComponents(basePackage);
    }
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testAnnotatedJavaScriptAPIClass() throws Exception
    {
        boolean foundSampleMessageBean = false;
        
        for (BeanDefinition component : components)
        {
            if (component.getBeanClassName().equals(SampleMessage.class.getName()))
            {
                foundSampleMessageBean = true;
                
                Class<SampleMessage> beanClass = (Class<SampleMessage>) Class.forName(component.getBeanClassName());
                
                // validate WSClass annotation
                ScriptClass wsc = beanClass.getAnnotation(ScriptClass.class);
                assertNotNull(wsc);
                assertNotNull(wsc.help());
                assertNotNull(wsc.code());
                Set<ScriptClassType> types = new HashSet<ScriptClassType>(Arrays.asList(wsc.types()));
                assertTrue(types.contains(ScriptClassType.JavaScriptAPI));

                // validate WSMethod annotation
                Method m = beanClass.getMethod("splitMsg",java.lang.String.class);
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
                assertNotNull(wsp.name());
            }
        }
        assertTrue(foundSampleMessageBean);
    }
}
