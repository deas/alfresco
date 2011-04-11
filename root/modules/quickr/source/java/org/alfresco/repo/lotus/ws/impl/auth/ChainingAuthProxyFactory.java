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
package org.alfresco.repo.lotus.ws.impl.auth;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

/**
 * This allows certain functions of a chained authentication to be targeted to specific members of the chain.
 * 
 * @author PavelYur
 *
 */
public class ChainingAuthProxyFactory extends ProxyFactoryBean
{
    
    private static final long serialVersionUID = 2466381225815597287L;

    /** The source application context bean factory. */
    private BeanFactory beanFactory;

    /** An optional bean names to look up in the source application contexts. */
    private List<String> sourceBeanNames;

    /** An optional 'fallback' object for when a suitable one could not be found in the chain *. */
    private Object defaultTarget;

    /**
     * Instantiates a new chaining subsystem proxy factory.
     */
    public ChainingAuthProxyFactory()
    {
        addAdvisor(new DefaultPointcutAdvisor(new MethodInterceptor()
        {
            public Object invoke(MethodInvocation mi) throws Throwable
            {
                Method method = mi.getMethod();
                
                boolean authenticated = false;
                
                try
                {
                    for (String sourceBeanName : sourceBeanNames)
                    {                        
                        try
                        {
                            Object bean = beanFactory.getBean(sourceBeanName);

                            // Ignore inactive beans
                            if ((bean instanceof Authenticator) && ((Authenticator) bean).isActive())
                            {
                                authenticated = (Boolean) method.invoke(bean, mi.getArguments());
                                
                                if (authenticated)
                                {
                                    return authenticated;
                                }
                            }
                        }
                        catch (BeansException e)
                        {
                            // Ignore and continue
                        }                        
                    }

                    // Fall back to the default object if we have one
                    if (!authenticated && defaultTarget != null && method.getDeclaringClass().isAssignableFrom(defaultTarget.getClass()))
                    {
                        return method.invoke(defaultTarget, mi.getArguments());
                    }

                    // If this is the isActive() method, we can handle it ourselves!
                    if (method.equals(Authenticator.class.getMethod("isActive")))
                    {
                        return Boolean.FALSE;
                    }

                    // Otherwise, something has gone wrong with wiring!
                    throw new RuntimeException("Don't know where to route call to method " + method);
                }
                catch (InvocationTargetException e)
                {
                    // Unwrap invocation target exceptions
                    throw e.getTargetException();
                }
            }
        }));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.aop.framework.AdvisedSupport#setInterfaces(java.lang.Class[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setInterfaces(Class[] interfaces)
    {
        super.setInterfaces(interfaces);
        // Make it possible to export the object via JMX
        setTargetClass(getObjectType());
    }
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory)
    {     
        super.setBeanFactory(beanFactory);
        this.beanFactory = beanFactory;
    }

    /**
     * Sets an optional bean names to target all calls to in the source application context. If not set, default
     * bean is used.
     * 
     * @param sourceBeanNames
     *            the sourceBeanNames to set
     */
    public void setSourceBeanNames(List<String> sourceBeanNames)
    {
        this.sourceBeanNames = sourceBeanNames;
    }

    /**
     * Sets the default target for method calls, when a suitable target cannot be found in the application context
     * chain.
     * 
     * @param defaultTarget
     *            the defaultTarget to set
     */
    public void setDefaultTarget(Object defaultTarget)
    {
        this.defaultTarget = defaultTarget;
    }
}
