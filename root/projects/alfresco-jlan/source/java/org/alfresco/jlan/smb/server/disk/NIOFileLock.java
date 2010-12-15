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

package org.alfresco.jlan.smb.server.disk;

import org.alfresco.jlan.locking.FileLock;

/**
 * NIO File Lock Class
 * 
 * <p>Extends the base file lock class to hold the NIO file lock object.
 *
 * @author gkspencer
 */
public class NIOFileLock extends FileLock {

  //	NIO file lock held on the open file
  
  java.nio.channels.FileLock m_lock;
  
  /**
   * Class constructor
   *
   * @param offset long
   * @param len long
   * @param pid int
   */
  public NIOFileLock(long offset, long len, int pid) {
    super ( offset, len, pid);
  }
  
  /**
   * Get the NIO lock
   * 
   * @return java.io.channels.FileLock
   */
  public final java.nio.channels.FileLock getNIOLock() {
    return m_lock;
  }
  
  /**
   * Set the NIO lock
   *
   * @param lock java.nio.channels.FileLock
   */
  public final void setNIOLock( java.nio.channels.FileLock lock) {
    m_lock = lock;
  }
}
