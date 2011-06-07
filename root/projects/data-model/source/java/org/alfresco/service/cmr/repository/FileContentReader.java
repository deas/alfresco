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
package org.alfresco.service.cmr.repository;

import java.io.File;

/**
 * Extension to {@link ContentReader} for Readers which are able to
 *  make the backing file available to you.
 * 
 * @see org.alfresco.service.cmr.repository.ContentReader
 * 
 * @author Derek Hulley
 */
public interface FileContentReader extends ContentReader
{
    /**
     * Provides access to the underlying File that this
     *  Reader accesses.
     *  
     * @return Returns the file that this reader accesses
     */
    public File getFile();

    /**
     * @return Whether the file exists or not
     */
    public boolean exists();
}
