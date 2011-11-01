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
package org.alfresco.module.vti.web.ws;

import java.util.List;

import org.alfresco.module.vti.handler.VersionsServiceHandler;
import org.alfresco.module.vti.metadata.model.DocumentVersionBean;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.version.VersionDoesNotExistException;
import org.dom4j.Element;

/**
 * Class for handling DeleteVersion method from versions web service
 *
 * @author PavelYur
 */
public class DeleteVersionEndpoint extends AbstractVersionEndpoint
{
    public DeleteVersionEndpoint(VersionsServiceHandler handler)
    {
       super(handler);
    }
    
    @Override
    protected String getEndpointNamePrefix() 
    {
       return "DeleteVersion";
    }

    /**
     * Deletes specified version of the document
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
     */
    @Override
    protected List<DocumentVersionBean> executeVersionAction(
          VtiSoapRequest soapRequest, String dws, String fileName, Element fileVersion) throws Exception 
    {
       List<DocumentVersionBean> versions;
       try
       {
          versions = handler.deleteVersion(dws + "/" + fileName, fileVersion.getText());
       }
       catch(FileNotFoundException fnfe)
       {
          // The specification defines the exact code that must be
          //  returned in case of a file not being found
          long code = 0x80131600l;
          String message = "File not found: " + fnfe.getMessage();
          throw new VtiSoapException(message, code, fnfe);
       }
       catch(VersionDoesNotExistException vne)
       {
          // The specification defines the exact code that must be
          //  returned in case of the version not existing
          long code = 0x80131600l;
          String message = "No such version: " + vne.getMessage();
          throw new VtiSoapException(message, code, vne);
       }
       
       return versions;
    }
}
