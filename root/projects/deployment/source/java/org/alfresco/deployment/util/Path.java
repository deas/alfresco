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

package org.alfresco.deployment.util;

import java.io.File;

/**
 * A Class that represents a deployment path.
 * @author britt
 */
public class Path
{
    private String[] fComponents;

    public Path(String path)
    {
        path = path.replaceAll("^(/|\\\\)+", "").replaceAll("(/|\\\\)+$", "");
        fComponents = path.split("(/|\\\\)+");
        if (fComponents.length == 1 && fComponents[0].equals(""))
        {
            fComponents = new String[0];
        }
    }

    public Path(String[] components)
    {
        fComponents = components;
    }

    /**
     * Get the number of components in the path.
     * @return
     */
    public int size()
    {
        return fComponents.length;
    }

    /**
     * Get the indexth component.
     * @param index
     * @return
     */
    public String get(int index)
    {
        return fComponents[index];
    }

    /**
     * Get the parent Path of this Path.
     * @return
     */
    public Path getParent()
    {
        if (fComponents.length == 0)
        {
            return null;
        }
        String[] result = new String[fComponents.length - 1];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = fComponents[i];
        }
        return new Path(result);
    }

    /**
     * Get the last component of the Path. Don't call on the root Path.
     * @return
     */
    public String getBaseName()
    {
        return fComponents[fComponents.length - 1];
    }

    /**
     * Get the Path that is this Path extended by one component.
     * @param name
     * @return
     */
    public Path extend(String name)
    {
        String[] result = new String[fComponents.length + 1];
        for (int i = 0; i < fComponents.length; i++)
        {
            result[i] = fComponents[i];
        }
        result[fComponents.length] = name;
        return new Path(result);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if (fComponents.length == 0)
        {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fComponents.length - 1; i++)
        {
            builder.append(fComponents[i]);
            builder.append(File.separatorChar);
        }
        builder.append(fComponents[fComponents.length - 1]);
        return builder.toString();
    }
}
