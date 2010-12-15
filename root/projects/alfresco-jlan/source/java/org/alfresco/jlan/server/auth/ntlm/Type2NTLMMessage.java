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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.jlan.util.HexDump;

/**
 * Type 2 NTLM Message Class
 *
 * @author gkspencer
 */
public class Type2NTLMMessage extends NTLMMessage implements Serializable
{
  // Minimal type 2 message length
    
  public static final int MinimalMessageLength = 32;
  
  // Type 2 field offsets
  
  public static final int OffsetTarget                = 12;
  public static final int OffsetFlags                 = 20;
  public static final int OffsetChallenge             = 24;
  public static final int OffsetContext               = 32;
  public static final int OffsetTargetInfo            = 40;   // optional
  
  /**
   * Default constructor
   */
  public Type2NTLMMessage() {
    super();
  }
  
  /**
   * Class constructor
   * 
   * @param buf byte[]
   */
  public Type2NTLMMessage(byte[] buf) {
    super(buf, 0, buf.length);
  }
  
  /**
   * Class constructor
   * 
   * @param buf byte[]
   * @param offset int
   * @param len int
   */
  public Type2NTLMMessage(byte[] buf, int offset, int len) {
    super(buf, offset, len);
  }

  /**
   * Return the flags value
   * 
   * @return int
   */
  public int getFlags() {
    return getIntValue(OffsetFlags);
  }
  
  /**
   * Check if the target name has been set
   * 
   * @return boolean
   */
  public final boolean hasTarget() {
    return hasFlag(NTLM.FlagRequestTarget);
  }
  
  /**
   * Return the target name
   * 
   * @return String
   */
  public final String getTarget() {
    return getStringValue(OffsetTarget, hasFlag(NTLM.FlagNegotiateUnicode));
  }
  
  /**
   * Return the challenge
   * 
   * @return byte[]
   */
  public final byte[] getChallenge() {
    return getRawBytes(OffsetChallenge, 8);
  }
  
  /**
   * Check if the optional context field is present
   * 
   * @return boolean
   */
  public final boolean hasContext() {
    return hasFlag(NTLM.FlagLocalCall);
  }
  
  /**
   * Return the context values
   * 
   * @return int[]
   */
  public final int[] getContext() {
    if ( hasContext() == false)
      return null;
    
    int[] ctx = new int[2];
    
    ctx[0] = getIntValue(OffsetContext);
    ctx[1] = getIntValue(OffsetContext + 4);
    
    return ctx;
  }
  
  /**
   * Check if target information is present
   * 
   * @return boolean
   */
  public final boolean hasTargetInformation() {
    return hasFlag(NTLM.FlagTargetInfo);
  }
  
  /**
   * Return the target information
   * 
   * @return List
   */
  public final List getTargetInformation() {
    if ( hasTargetInformation() == false)
      return null;
    
    // Get the target information block length and offset
    
    int tLen = getStringLength(OffsetTargetInfo);
    int tOff = getStringOffset(OffsetTargetInfo);
    
    List tList = new ArrayList();
    if ( tLen == 0)
      return tList;
    
    // Unpack the target information structures
    
    int typ = -1;
    int slen = -1;
    String name = null;
    
    while ( typ != 0)
    {
      // Unpack the details for the current target
      
      typ  = getShortValue(tOff);
      slen = getShortValue(tOff + 2);
      
      if ( slen > 0)
        name = getRawString(tOff + 4, slen/2, true);
      else
        name = null;
      
      // Add the details to the list
      
      if ( typ != 0)
        tList.add( new TargetInfo(typ, name));
      
      // Update the data offset
      
      tOff += slen + 4;
    }
    
    // Return the target list
    
    return tList;
  }
  
  /**
   * Build a type 2 message
   * 
   * @param flags int
   * @param target String
   * @param challenge byte[]
   * @param ctx byte[]
   * @param tList List
   */
  public final void buildType2(int flags, String target, byte[] challenge, int[] ctx, List tList) {

    // Initialize the header/flags
      
    initializeHeader(NTLM.Type2, flags);

    // Determine if strings are ASCII or Unicode
    
    boolean isUni = hasFlag(NTLM.FlagNegotiateUnicode);
    
    int strOff = OffsetTargetInfo;
    if ( tList != null)
      strOff += 8;
    
    // Pack the target name
    
    strOff = setStringValue(OffsetTarget, target, strOff, isUni);
    
    // Pack the challenge and context
    
    if ( challenge != null)
      setRawBytes(OffsetChallenge, challenge);
    else
      zeroBytes(OffsetChallenge, 8);
    
    if ( ctx != null)
      setRawInts(OffsetContext, ctx);
    else
      zeroBytes(OffsetContext, 8);
    
    // Pack the target information, if specified
    
    if ( tList != null)
    {
      // Clear the target information length and set the data offset
      
      setIntValue(OffsetTargetInfo, 0);
      setIntValue(OffsetTargetInfo+4, strOff);
      
      int startOff = strOff;
      
      // Pack the target information structures
      
      for ( int i = 0; i < tList.size(); i++)
      {
        // Get the target information
        
        TargetInfo tInfo = (TargetInfo) tList.get( i);
        
        // Pack the target information structure
        
        setShortValue(strOff, tInfo.isType());
        
        int tLen = tInfo.getName().length();
        if ( isUni)
          tLen *= 2;
        setShortValue(strOff+2, tLen);
        strOff = setRawString(strOff+4, tInfo.getName(), isUni);
      }
      
      // Add the list terminator
      
      zeroBytes(strOff, 4);
      strOff += 4;
      
      // Set the target information block length
      
      setShortValue(OffsetTargetInfo, strOff - startOff);
      setShortValue(OffsetTargetInfo+2, strOff - startOff);
    }
    
    // Set the message length
    
    setLength(strOff);
  }
  
  /**
   * Set the message flags
   * 
   * @param flags int
   */
  protected void setFlags(int flags) {
    setIntValue( OffsetFlags, flags);
  }
  
  /**
   * Return the type 2 message as a string
   * 
   * @return String
   */
  public String toString() {
    StringBuffer str = new StringBuffer();
    
    str.append("[Type2:0x");
    str.append(Integer.toHexString(getFlags()));
    str.append(",Target:");
    str.append(getTarget());
    str.append(",Ch:");
    str.append(HexDump.hexString(getChallenge()));
    
    if ( hasTargetInformation())
    {
        List targets = getTargetInformation();
        
        str.append(",TargInf:");
        for ( int i = 0; i < targets.size(); i++) {
          TargetInfo target = (TargetInfo) targets.get( i);
          str.append(target);
        }
    }
    str.append("]");
    
    return str.toString();
  }
}
