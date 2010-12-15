/*
 * Copyright (C) 2009-2010 Alfresco Software Limited.
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
 * Name Mapper for mapping AVM Web Projects onto
 * DM paths.   
 * 
 * avm web project are of the form
 * 
 *    StoreName:/www/ROOT/file1
 * 
 * This interface turns the StoreName into a name to be added to a DM repo.
 *
 * @author Mark Rogers
 */
public interface StoreNameMapper
{    
    /**
     * Returns the project name based on the AVM store name
     * 
     * A folder with the project name will be created by the DM 
     * Deployment Engine, in the rootFolder if it does not exist.
     * 
     * @param the authoring storeName;
     * 
     * @return the mapped project name. 
     */
    String mapProjectName(String storeName);
}
