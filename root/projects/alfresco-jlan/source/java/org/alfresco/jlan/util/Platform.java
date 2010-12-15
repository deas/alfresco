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
 * Platform Class
 * 
 * <p>Determine the platform type that we are runnng on.
 * 
 * @author gkspencer
 */
public class Platform {

  // Platform types

  public enum Type {
    Unchecked, Unknown, WINDOWS, LINUX, SOLARIS, MACOSX, AIX
  };

  // Platform type we are running on

  private static Type _platformType = Type.Unchecked;

  /**
   * Determine the platform type
   * 
   * @return Type
   */
  public static final Type isPlatformType() {

    // Check if the type has been set

    if (_platformType == Type.Unchecked) {
      
      // Get the operating system type

      String osName = System.getProperty("os.name");

      if (osName.startsWith("Windows"))
        _platformType = Type.WINDOWS;
      else if (osName.equalsIgnoreCase("Linux"))
        _platformType = Type.LINUX;
      else if (osName.startsWith("Mac OS X") || osName.equals( "Darwin"))
        _platformType = Type.MACOSX;
      else if (osName.startsWith("Solaris") || osName.startsWith("SunOS"))
        _platformType = Type.SOLARIS;
      else if (osName.startsWith("AIX"))
      	_platformType = Type.AIX;
    }

    // Return the current platform type

    return _platformType;
  }
}
