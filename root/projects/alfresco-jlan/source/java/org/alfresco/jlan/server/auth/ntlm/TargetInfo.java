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

package org.alfresco.jlan.server.auth.ntlm;

/**
 * Target Information Class
 * 
 * <p>Contains the target information from an NTLM message.
 *
 * @author gkspencer
 */
public class TargetInfo
{
  // Target type and name
  
  private int m_type;
  private String m_name;
  
  /**
   * Class constructor
   * 
   * @param type int
   * @param name String
   */
  public TargetInfo(int type, String name) {
    m_type = type;
    m_name = name;
  }
  
  /**
   * Return the target type
   * 
   * @return int
   */
  public final int isType() {
    return m_type;
  }
  
  /**
   * Return the target name
   * 
   * @return String
   */
  public final String getName() {
    return m_name;
  }
  
  /**
   * Return the target information as a string
   * 
   * @return String
   */
  public String toString() {
    StringBuffer str = new StringBuffer();
    
    str.append("[");
    str.append(getTypeAsString(isType()));
    str.append(":");
    str.append(getName());
    str.append("]");
    
    return str.toString();
  }
  
  /**
   * Return the target type as a string
   * 
   * @param typ int
   * @return String
   */
  public final static String getTypeAsString(int typ)
  {
    String typStr = null;
    
    switch ( typ) {
    case NTLM.TargetServer:
      typStr = "Server";
      break;
    case NTLM.TargetDomain:
      typStr = "Domain";
      break;
    case NTLM.TargetFullDNS:
      typStr = "DNS";
      break;
    case NTLM.TargetDNSDomain:
      typStr = "DNS Domain";
      break;
    default:
      typStr = "Unknown 0x" + Integer.toHexString(typ);
      break;
    }
    
    return typStr;
  }
}
