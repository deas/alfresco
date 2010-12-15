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

import org.alfresco.deployment.FileType;
import org.alfresco.deployment.impl.server.DeployedFile;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * 
 *
 * @author Mark Rogers
 */
public class DMDeployedFile extends DeployedFile
{
    /**
     * 
     */
    private static final long serialVersionUID = -5032978596418068099L;

    private NodeRef destNodeRef;
    
    private String encoding;
    
    private String mimeType;
    
    public DMDeployedFile(FileType type, 
            String preLocation, 
            String path,
            String guid, 
            boolean create,
            boolean file,
            NodeRef destNodeRef,
            String encoding,
            String mimeType)
    {
        super(type, preLocation, path, guid, create, file);
        this.destNodeRef = destNodeRef;
        this.encoding = encoding;
        this.mimeType = mimeType;    
    }

    
    public DMDeployedFile(FileType type, 
            String preLocation, 
            String path,
            String guid, 
            boolean create,
            boolean file)
    {
        super(type, preLocation, path, guid, create, file);
        // TODO Auto-generated constructor stub
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setDestNodeRef(NodeRef destNodeRef)
    {
        this.destNodeRef = destNodeRef;
    }

    public NodeRef getDestNodeRef()
    {
        return destNodeRef;
    }

}
