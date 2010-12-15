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
package org.alfresco.cmis.test.ws;

/**
 * Base class to provide data to services clients
 * 
 * @author Mike Shavnev
 */
public class AbstractService
{

    private String name;
    private String parentElementName;
    private String namespace;
    private String path;

    public AbstractService(String name, String parentElementName, String namespace, String path)
    {
        this.name = name;
        this.parentElementName = parentElementName;
        this.namespace = namespace;
        this.path = path;
    }

    public String getName()
    {
        return name;
    }

    public String getNamespace()
    {
        return namespace;
    }

    public String getParentElementName()
    {
        return parentElementName;
    }

    public String getPath()
    {
        return path;
    }

    public String getWsdlUri()
    {
        return path + "?wsdl";
    }
}
