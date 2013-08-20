/*
 * Copyright (C) 2006-2010 Alfresco Software Limited.
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

package org.alfresco.jlan.sample;

import java.io.IOException;

import org.alfresco.jlan.ftp.FTPRequest;
import org.alfresco.jlan.ftp.FTPSiteInterface;
import org.alfresco.jlan.ftp.FTPSrvSession;
import org.alfresco.jlan.server.config.ServerConfiguration;
import org.springframework.extensions.config.ConfigElement;


/**
 * Test FTP Site Interface Class
 * 
 * <p>Implements the FTPSiteInterface to accept custom SITE commands.
 *
 * @author gkspencer
 */
public class TestFTPSiteInterface implements FTPSiteInterface {

  /**
   * Initialize the FTP site interface
   * 
   * @param config ServerConfiguration
   * @param params ConfigElement
   */
  public void initializeSiteInterface(ServerConfiguration config, ConfigElement params) {
  }

  /**
   * Process the FTP SITE command
   * 
   * @param sess FTPSrvSession
   * @param req FTPRequest
   */
  public void processFTPSiteCommand(FTPSrvSession sess, FTPRequest req)
    throws IOException {

    // DEBUG
    
    if ( sess.hasDebug( FTPSrvSession.DBG_INFO))
      sess.debugPrintln( "SITE command " + req.getArgument());
    
    // Echo the user request
    
    sess.sendFTPResponse( 200, "Site request : " + req.getArgument());
  }
}
