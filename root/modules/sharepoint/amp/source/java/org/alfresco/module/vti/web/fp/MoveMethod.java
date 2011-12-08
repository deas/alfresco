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
package org.alfresco.module.vti.web.fp;

import javax.servlet.http.HttpServletRequestWrapper;

import org.alfresco.repo.webdav.WebDAVServerException;

/**
 * Implements the WebDAV MOVE method with VTI specific behaviours
 */
public class MoveMethod extends org.alfresco.repo.webdav.MoveMethod
{
    private String alfrescoContext;

    public MoveMethod(String alfrescoContext)
    {
        this.alfrescoContext = alfrescoContext;
    }

    /**
     * Alters the request to include the servlet path (needed for 
     *  building the destination path), then executes as usual
     */
    @Override
    public void execute() throws WebDAVServerException {
       // Wrap the request to include the servlet path
       m_request = new HttpServletRequestWrapper(m_request) {
          public String getServletPath()
          {
              return alfrescoContext.equals("") ? "/" : alfrescoContext;
          }
       };

       // Now have the move executed as normal
       super.execute();
    }

   /**
     * Returns the path, excluding the Servlet Context (if present)
     * @see org.alfresco.repo.webdav.WebDAVMethod#getPath()
     */
    @Override
    protected String getPath()
    {
       return AbstractMethod.getPathWithoutContext(alfrescoContext, m_request);
    }
}
