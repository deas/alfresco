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

package org.alfresco.deployment;

import java.io.Serializable;


/**
 * All useful information about a file.
 * @author britt
 */
public class FileDescriptor implements Serializable, Comparable<FileDescriptor>
{
    private static final long serialVersionUID = 8082252882518483993L;

    /**
     * The name of the file.
     */
    private String fName;

    /**
     * The type of the file.
     */
    private FileType fType;

    /**
     * The GUID of the file.
     */
    private String fGUID;

    /**
     * Create a new one.
     * @param name
     * @param type
     * @param guid
     */
    public FileDescriptor(String name, FileType type, String guid)
    {
        fName = name;
        fType = type;
        fGUID = guid;
    }

    public String getName()
    {
        return fName;
    }

    public FileType getType()
    {
        return fType;
    }

    public String getGUID()
    {
        return fGUID;
    }

    public void setGuid(String guid)
    {
        fGUID = guid;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof FileDescriptor))
        {
            return false;
        }
        FileDescriptor other = (FileDescriptor)obj;
        return fName.equalsIgnoreCase(other.fName);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return fName.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append(fName);
        builder.append(':');
        builder.append(fType.toString());
        builder.append(':');
        builder.append(fGUID);
        builder.append(']');
        return builder.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(FileDescriptor o)
    {
        return fName.compareToIgnoreCase(o.fName);
    }
}
