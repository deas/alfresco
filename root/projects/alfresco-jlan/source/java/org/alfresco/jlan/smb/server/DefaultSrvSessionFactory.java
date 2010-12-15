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
 * Default Server Session Factory Class
 * 
 * @author gkspencer
 */
public class DefaultSrvSessionFactory implements SrvSessionFactory {

	/**
	 * Create a server session object
	 * 
	 * @param handler PacketHandler
	 * @param server SMBServer
	 * @param sessId int
	 * @return SMBSrvSession
	 */
	public SMBSrvSession createSession(PacketHandler handler, SMBServer server, int sessId) {

		// Create a new SMB session

		SMBSrvSession sess = new SMBSrvSession(handler, server);

		sess.setSessionId(sessId);
		sess.setUniqueId(handler.getShortName() + sess.getSessionId());
		sess.setDebugPrefix("[" + handler.getShortName() + sessId + "] ");

		// Add the session to the active session list

		server.addSession(sess);

		// Return the new session

		return sess;
	}
}
