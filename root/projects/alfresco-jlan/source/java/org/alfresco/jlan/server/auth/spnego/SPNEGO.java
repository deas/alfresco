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

package org.alfresco.jlan.server.auth.spnego;

import java.io.IOException;

import org.alfresco.jlan.server.auth.asn.DER;


/**
 * SPNEGO Class
 * 
 * <p>
 * Contains SPNEGO constants
 *
 * @author gkspencer
 */
public class SPNEGO {

  // Message types

  public static final int NegTokenInit = 0;
  public static final int NegTokenTarg = 1;

  // NegTokenInit context flags

  public static final int ContextDelete = 0;
  public static final int ContextMutual = 1;
  public static final int ContextReplay = 2;
  public static final int ContextSequence = 3;
  public static final int ContextAnon = 4;
  public static final int ContextConf = 5;
  public static final int ContextInteg = 6;

  // NegTokenTarg result codes

  public static final int AcceptCompleted = 0;
  public static final int AcceptIncomplete = 1;
  public static final int Reject = 2;

  /**
   * Return a result code as a string
   * 
   * @param res int
   * @return String
   */
  public static String asResultString(int res) {

    String resStr = null;

    switch (res) {
      case AcceptCompleted:
        resStr = "AcceptCompleted";
        break;
      case AcceptIncomplete:
        resStr = "AcceptIncomplete";
        break;
      case Reject:
        resStr = "Reject";
        break;
      default:
        resStr = "" + res;
        break;
    }

    return resStr;
  }

  /**
   * Determine the SPNEGO token type
   * 
   * @param buf byte[]
   * @param off int
   * @param len int
   * @return int
   * @exception IOException
   */
  public static int checkTokenType(byte[] buf, int off, int len)
    throws IOException {

    // Check the initial byte of the buffer
    
    if ( DER.isApplicationSpecific( buf[ off]))
      return NegTokenInit;
    else if ( DER.isTagged( buf[ off]))
      return NegTokenTarg;
    else
      return -1;
  }
}
