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

package org.alfresco.jlan.server.locking;

import org.alfresco.jlan.locking.FileLock;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.filesys.NetworkFile;

/**
 * File Lock Listener Interface.
 *
 * <p>The file lock listener receives events when file locks are granted, released and denied.
 *
 * @author gkspencer
 */
public interface FileLockListener {

  /**
   * Lock has been granted on the specified file.
   *
   * @param sess SrvSession
   * @param file NetworkFile
   * @param lock FileLock
   */
  void lockGranted(SrvSession sess, NetworkFile file, FileLock lock);

  /**
   * Lock has been released on the specified file.
   *
   * @param sess SrvSession
   * @param file NetworkFile
   * @param lock FileLock
   */
  void lockReleased(SrvSession sess, NetworkFile file, FileLock lock);

  /**
   * Lock has been denied on the specified file.
   *
   * @param sess SrvSession
   * @param file NetworkFile
   * @param lock FileLock
   */
  void lockDenied(SrvSession sess, NetworkFile file, FileLock lock);
}
