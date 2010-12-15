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

/**
 * <p>Contains the various states that an SMB server session will go through during the session
 * lifetime.
 *
 * @author gkspencer
 */
public class SMBSrvSessionState  {

	// NetBIOS session has been closed.

	public static final int NBHANGUP = 5;

	// NetBIOS session request state.

	public static final int NBSESSREQ = 0;

	// SMB session closed down.

	public static final int SMBCLOSED = 4;

	// Negotiate SMB dialect.

	public static final int SMBNEGOTIATE = 1;

	// SMB session is initialized, ready to receive/handle standard SMB requests.

	public static final int SMBSESSION = 3;

	// SMB session setup.

	public static final int SMBSESSSETUP = 2;

  //	State name strings

  private static final String _stateName [] = {
	  																						"NBSESSREQ",
	  																						"SMBNEGOTIATE",
	  																						"SMBSESSSETUP",
	  																						"SMBSESSION",
	  																						"SMBCLOSED",
	  																						"NBHANGUP"
  };
  
	/**
	 * Return the specified SMB state as a string.
	 */
	public static String getStateAsString( int state ) {
	  if ( state < _stateName.length)
	    return _stateName [ state];
	  return "[UNKNOWN]";
	}
}
