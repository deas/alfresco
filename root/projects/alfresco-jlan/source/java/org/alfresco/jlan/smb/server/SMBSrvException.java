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

import org.alfresco.jlan.smb.SMBErrorText;
import org.alfresco.jlan.smb.SMBStatus;

/**
 * SMB exception class
 *
 * <p>This class holds the detail of an SMB network error. The SMB error class and
 *  error code are available to give extra detail about the error condition.
 *
 * @author gkspencer
 */
public class SMBSrvException extends Exception {

  //  SMB error class

  protected int m_errorclass;

  //  SMB error code

  protected int m_errorcode;

  // NT 32-bit error code
  
  protected int m_NTerror = -1;
  
  /**
   * Construct an SMB exception with the specified error class/error code.
   */
  public SMBSrvException(int errclass, int errcode) {
    super(SMBErrorText.ErrorString(errclass, errcode));
    m_errorclass = errclass;
    m_errorcode = errcode;
  }
  
  /**
   * Construct an SMB exception with the specified error class/error code and
   * additional text error message.
   */
  public SMBSrvException(int errclass, int errcode, String msg) {
    super(msg);
    m_errorclass = errclass;
    m_errorcode = errcode;
  }
  
  /**
   * Construct an SMB exception using the error class/error code in the SMB packet
   */
  protected SMBSrvException(SMBSrvPacket pkt) {
    super(SMBErrorText.ErrorString(pkt.getErrorClass(), pkt.getErrorCode()));
    m_errorclass = pkt.getErrorClass();
    m_errorcode = pkt.getErrorCode();
  }

  /**
   * Construct an SMB exception with the specified error class/error code.
   * 
   * @param nterror int
   * @param errclass int
   * @param errcode int
   */
  public SMBSrvException(int nterror, int errclass, int errcode)
  {
    super(SMBErrorText.ErrorString(errclass, errcode));
    m_errorclass = errclass;
    m_errorcode = errcode;
    m_NTerror = nterror;
  }
  
  /**
   *  Return the error class for this SMB exception.
   *
   * @return    SMB error class.
   */
  public int getErrorClass() {
    return m_errorclass;
  }
  
  /**
   *  Return the error code for this SMB exception
   *
   * @return    SMB error code
   */
  public int getErrorCode() {
    return m_errorcode;
  }

  /**
   * Check if the NT error code has been set
   * 
   * @return boolean
   */
  public final boolean hasNTErrorCode() {
    return m_NTerror != -1 ? true : false;
  }
  
  /**
   * Return the NT error code
   * 
   * @return int
   */
  public int getNTErrorCode() {
    return m_NTerror;
  }
  
  /**
   * Return the error text for the SMB exception
   * 
   * @return Error text string.
   */
  public String getErrorText() {
    if ( getNTErrorCode() != 0)
      return SMBErrorText.ErrorString(SMBStatus.NTErr, getNTErrorCode());
    return SMBErrorText.ErrorString(m_errorclass, m_errorcode);
  }
}
