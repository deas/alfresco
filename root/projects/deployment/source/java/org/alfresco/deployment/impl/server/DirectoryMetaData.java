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

package org.alfresco.deployment.impl.server;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import org.alfresco.deployment.FileDescriptor;

/**
 * This represents the metadata of a single directory.
 * It is stored on disk to keep track of deployment status
 * for deployment targets.
 * @author britt
 */
public class DirectoryMetaData implements Serializable
{
    private static final long serialVersionUID = 8464208313551153234L;

    private SortedSet<FileDescriptor> fElements;
    
    public DirectoryMetaData()
    {
        fElements = new TreeSet<FileDescriptor>();
    }
    
    /**
     * Add an entry.
     * @param file
     */
    public void add(FileDescriptor file)
    {
        fElements.add(file);
    }
    
    /**
     * Remove an entry.
     * @param file
     */
    public void remove(FileDescriptor file)
    {
        fElements.remove(file);
    }
    
    /**
     * Get the listing.
     * @return
     */
    public SortedSet<FileDescriptor> getListing()
    {
        return fElements;
    }
}
