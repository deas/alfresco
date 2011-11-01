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
package org.alfresco.module.vti.handler.alfresco.v3;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoVersionsServiceHandler;
import org.alfresco.module.vti.metadata.model.DocumentVersionBean;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionType;

/**
 * Alfresco implementation of VersionsServiceHandler and AbstractAlfrescoVersionsServiceHandler
 * 
 * @author PavelYur
 */
public class AlfrescoVersionsServiceHandler extends AbstractAlfrescoVersionsServiceHandler
{
    /**
     * Do a "SharePoint Delete All Versions", which isn't the same
     *  as a normal Alfresco delete version history
     * @see org.alfresco.module.vti.handler.VersionsServiceHandler#deleteAllVersions(java.lang.String)
     */
    public List<DocumentVersionBean> deleteAllVersions(String fileName) throws FileNotFoundException
    {
       FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(fileName);
       
       // Asking for a non existent file is valid for listing
       if(documentFileInfo == null)
       {
          throw new FileNotFoundException(fileName);
       }

       // Check it's a valid file
       assertDocument(documentFileInfo);
       
       // We need to identify the versions to keep
       // The SharePoint spec requires us to keep:
       //   * The current version
       //   * The most recent major version ("published version")
       // Depending on the state, this means keeping 1 or 2 versions
       VersionHistory history = versionService.getVersionHistory(documentFileInfo.getNodeRef());
       List<Version> toKeep = new ArrayList<Version>();
       
       Version current = history.getHeadVersion();
       toKeep.add(current);
       if (current.getVersionType() != VersionType.MAJOR)
       {
          // Find the last major version
          // (Versions are returned most recent first)
          for (Version v : history.getAllVersions())
          {
             if (v.getVersionType() == VersionType.MAJOR)
             {
                toKeep.add(v);
                break;
             }
          }
       }
       
       if (logger.isDebugEnabled())
          logger.debug("Deleteing all versions except " + toKeep);

       // Zap all the versions except the 1 or 2 to keep
       for (Version v : history.getAllVersions())
       {
          if (! toKeep.contains(v))
          {
             versionService.deleteVersion(documentFileInfo.getNodeRef(), v);
          }
       }

       // Return the new details on the file, which now has
       //  a much much smaller version history
       return getVersions(documentFileInfo);
    }
}
