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

package org.alfresco.jlan.server.auth;

import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.core.SharedDevice;

/**
 * <p>Default authenticator class.
 *
 * <p>The default authenticator implementation enables user level security mode and allows
 * any user to connect to the server.
 *
 * @author gkspencer
 */
public class DefaultAuthenticator extends CifsAuthenticator {

  /**
   * Class constructor
   */
  public DefaultAuthenticator() {
    setAccessMode(USER_MODE);
  }

  /**
   * Allow any user to access the server
   *
   * @param client   Client details.
   * @param share    Shared device the user is connecting to.
   * @param pwd      Share level password.
   * @param sess     Server session
   * @return int
   */
  public int authenticateShareConnect(ClientInfo client, SharedDevice share, String pwd, SrvSession sess) {
    return Writeable;
  }

  /**
   * Allow any user to access the server.
   *
   * @param client   Client details.
   * @param sess		 Server session
   * @param alg			 Encryption algorithm
   * @return int
   */
  public int authenticateUser(ClientInfo client, SrvSession sess, int alg) {
    return AUTH_ALLOW;
  }

  /**
   * The default authenticator does not use encrypted passwords.
   *
   * @param sess SrvSession
   * @return byte[]
   */
  public byte[] getChallengeKey(SrvSession sess) {
    return null;
  }
}
