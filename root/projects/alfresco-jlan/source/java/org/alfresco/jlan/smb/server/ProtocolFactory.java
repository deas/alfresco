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

package org.alfresco.jlan.smb.server;

import org.alfresco.jlan.smb.Dialect;

/**
 * SMB Protocol Factory Class.
 *
 * <p>The protocol factory class generates protocol handlers for SMB dialects.
 *
 * @author gkspencer
 */
class ProtocolFactory {

  /**
   * ProtocolFactory constructor comment.
   */
  public ProtocolFactory() {
    super();
  }
  
  /**
   * Return a protocol handler for the specified SMB dialect type, or null if there
   * is no appropriate protocol handler.
   *
   * @return ProtocolHandler
   * @param dialect int
   */
  protected static ProtocolHandler getHandler(int dialect) {

    //  Determine the SMB dialect type

    ProtocolHandler handler = null;

    switch (dialect) {

      //  Core dialect

      case Dialect.Core :
      case Dialect.CorePlus :
        handler = new CoreProtocolHandler();
        break;

        //  LanMan dialect

      case Dialect.DOSLanMan1 :
      case Dialect.DOSLanMan2 :
      case Dialect.LanMan1 :
      case Dialect.LanMan2 :
      case Dialect.LanMan2_1 :
        handler = new LanManProtocolHandler();
        break;

        //  NT dialect

      case Dialect.NT :
      	handler = new NTProtocolHandler();
        break;
    }

    //  Return the protocol handler

    return handler;
  }
}
