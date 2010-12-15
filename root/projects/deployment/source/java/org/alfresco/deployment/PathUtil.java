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

import java.io.File;

public class PathUtil 
{
	    /**
	     * Get a File object for the given path in this target.
	     * @param path
	     * @return
	     */
	   public static File getFileForPath(String rootDir, String path)
	   {
	        return new File(rootDir + normalizePath(path));
	    }
	    private static final String fgSeparatorReplacement;
	
	    static
	    {
	    	fgSeparatorReplacement = File.separator.equals("/") ? "/" : "\\\\";
	    }
	
	    /**
	     * Utility to normalize a path to platform specific form.
	     * @param path
	     * @return
	     */
	    private static String normalizePath(String path)
	    {
	        path = path.replaceAll("/+", fgSeparatorReplacement);
	        path = path.replace("/$", "");
	        if (!path.startsWith(File.separator))
	        {
	            path = File.separator + path;
	        }
	        return path;
	    }
}

