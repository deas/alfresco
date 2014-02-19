/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
/**
 * 
 */
package org.alfresco.webdrone.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Annotation can be used one off data setup before test suites run.
 *
 * @author Shan Nagarajan
 * @since  1.7
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface DataSetup
{
    
    /**
     * Whether methods on this class/method are enabled.
     */
    public boolean enabled() default true;
    
    /**
     * The list of groups this method belongs to.
     */
    public DataGroup[] groups() default {};
    
    /**
     * The Test Link Id for the {@link DataSetup} Method.
     * 
     * @return The Test Link Id.
     */
    public String testLinkId() default "";
    
}