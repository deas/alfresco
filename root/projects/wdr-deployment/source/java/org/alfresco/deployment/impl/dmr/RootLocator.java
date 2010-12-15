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
package org.alfresco.deployment.impl.dmr;

/**
 * Root Locator for mapping AVM Web Projects onto
 * DM paths.   
 * 
 * The root mapper specifies where in a DM repository the project should go.
 * 
 * For example it may say all projects go in "app:company_home" or there may be a 
 * more complex mapping.
 * 
 *
 * @author Mark Rogers
 */
public interface RootLocator
{
    
    /**
     * Get The x-path pattern for the root of the deployment
     * This part of the path must exist prior to the first deployment
     */
     String getRootQuery(String projectName);
}
