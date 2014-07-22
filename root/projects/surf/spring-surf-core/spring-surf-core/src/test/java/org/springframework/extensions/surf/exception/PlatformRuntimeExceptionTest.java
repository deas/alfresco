/**
¯ * Copyright (C) 2005-2009 Alfresco Software Limited.
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

package org.springframework.extensions.surf.exception;

import java.util.Locale;

import junit.framework.TestCase;

import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Spring Web Scripts runtime exception test
 * 
 * @author Roy Wetherall
 */
public class PlatformRuntimeExceptionTest extends TestCase
{
	private static final String BASE_RESOURCE_NAME = "org.springframework.extensions.surf.util.testMessages";
    private static final String PARAM_VALUE = "television";
    private static final String MSG_PARAMS = "msg_params";
    private static final String MSG_ERROR = "msg_error";
    private static final String VALUE_ERROR = "This is an error message. \n  This is on a new line.";
    private static final String VALUE_FR_ERROR = "C'est un message d'erreur. \n  C'est sur une nouvelle ligne.";
    private static final String VALUE_PARAMS = "What no " + PARAM_VALUE + "?";
    private static final String VALUE_FR_PARAMS = "Que non " + PARAM_VALUE + "?";
    private static final String NON_I18NED_MSG = "This is a non i18ned error message.";
   
    @Override
    protected void setUp() throws Exception
    {
        // Re-set the current locale to be the default
        Locale.setDefault(Locale.ENGLISH);
        I18NUtil.setLocale(Locale.getDefault());
    }
    
    public void testI18NBehaviour()
    {
        // Register the bundle
        I18NUtil.registerResourceBundle(BASE_RESOURCE_NAME);
        
        PlatformRuntimeException exception1 = new PlatformRuntimeException(MSG_PARAMS, new Object[]{PARAM_VALUE});
        assertTrue(exception1.getMessage().contains(VALUE_PARAMS));
        PlatformRuntimeException exception3 = new PlatformRuntimeException(MSG_ERROR);
        assertTrue(exception3.getMessage().contains(VALUE_ERROR));
        
        // Change the locale and re-test
        I18NUtil.setLocale(new Locale("fr", "FR"));
        
        PlatformRuntimeException exception2 = new PlatformRuntimeException(MSG_PARAMS, new Object[]{PARAM_VALUE});
        assertTrue(exception2.getMessage().contains(VALUE_FR_PARAMS));   
        PlatformRuntimeException exception4 = new PlatformRuntimeException(MSG_ERROR);
        assertTrue(exception4.getMessage().contains(VALUE_FR_ERROR));  
        
        PlatformRuntimeException exception5 = new PlatformRuntimeException(NON_I18NED_MSG);
        assertTrue(exception5.getMessage().contains(NON_I18NED_MSG));
    }
    
    public void testMakeRuntimeException()
    {
        Throwable e = new RuntimeException("sfsfs");
        RuntimeException ee = PlatformRuntimeException.makeRuntimeException(e, "Test");
        assertTrue("Exception should not have been changed", ee == e);
        
        e = new Exception();
        ee = PlatformRuntimeException.makeRuntimeException(e, "Test");
        assertTrue("Expected an PlatformRuntimeException instance", ee instanceof PlatformRuntimeException);
    }
}
