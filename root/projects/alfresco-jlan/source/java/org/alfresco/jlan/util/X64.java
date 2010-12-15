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

package org.alfresco.jlan.util;

/**
 * X64 Class
 * 
 * <p>
 * Check if the platform is a 64bit operating system.
 *
 * @author gkspencer
 */
public class X64 {
  
  /**
   * Check if we are running on a Windows 64bit system
   * 
   * @return boolean
   */
  public static boolean isWindows64() {

    // Check for Windows

    String prop = System.getProperty("os.name");
    if (prop == null || prop.startsWith("Windows") == false)
      return false;

    // Check the OS architecture

    prop = System.getProperty("os.arch");
    if (prop != null && prop.equalsIgnoreCase("amd64"))
      return true;

    // Check the VM name

    prop = System.getProperty("java.vm.name");
    if (prop != null && prop.indexOf("64-Bit") != -1)
      return true;

    // Check the data model

    prop = System.getProperty("sun.arch.data.model");
    if (prop != null && prop.equals("64"))
      return true;

    // Not 64 bit Windows

    return false;
  }
}
