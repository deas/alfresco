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

import java.io.IOException;

import org.alfresco.deployment.impl.server.Deployment;
import org.alfresco.service.cmr.model.FileInfo;

/**
 *
 *
 * @author Mark Rogers
 */
public class DMDeployment extends Deployment 
{
    /**
     * 
     */
    private static final long serialVersionUID = 1072135017772640386L;
    private FileInfo rootNode;
    
    public DMDeployment(String ticket, String targetName, String storeName,
            int version, FileInfo rootNode) throws IOException
            
    {
        super(ticket, targetName, storeName, version);
        this.setRootNode(rootNode);
    }

    public void setRootNode(FileInfo rootNode)
    {
        this.rootNode = rootNode;
    }

    public FileInfo getRootNode()
    {
        return rootNode;
    }
    

}
