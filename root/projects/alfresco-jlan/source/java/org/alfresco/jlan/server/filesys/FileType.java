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

package org.alfresco.jlan.server.filesys;

/**
 * File Type Class
 * 
 * <p>File type constants.
 *
 * @author gkspencer
 */
public class FileType {

  // File types
  
  public static final int RegularFile   = 1;
  public static final int Directory     = 2;
  public static final int SymbolicLink  = 3;
  public static final int HardLink      = 4;
  public static final int Device        = 5;
  
  /**
   * Return a file type as a string
   * 
   * @param typ int
   * @return String
   */
  public final static String asString(int typ) {
    
    String typStr = "Unknown";
    
    switch ( typ) {
      case RegularFile:
        typStr = "File";
        break;
      case Directory:
        typStr = "Directory";
        break;
      case SymbolicLink:
        typStr = "SymbolicLink";
        break;
      case HardLink:
        typStr = "HardLink";
        break;
      case Device:
        typStr = "Device";
        break;
    }
    
    return typStr;
  }
}
