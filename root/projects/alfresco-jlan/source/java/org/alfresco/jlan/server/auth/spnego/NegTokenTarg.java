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

import org.alfresco.jlan.server.auth.asn.DERBuffer;
import org.alfresco.jlan.server.auth.asn.DEREnumerated;
import org.alfresco.jlan.server.auth.asn.DERObject;
import org.alfresco.jlan.server.auth.asn.DEROctetString;
import org.alfresco.jlan.server.auth.asn.DEROid;
import org.alfresco.jlan.server.auth.asn.DERSequence;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

/**
 * NegTokenTarg Class
 * 
 * <p>
 * Contains the details of an SPNEGO NegTokenTarg blob for use with CIFS.
 *
 * @author gkspencer
 */
public class NegTokenTarg {

  // Result code

  private int m_result = -1;

  // Supported mechanism

  private Oid m_supportedMech;

  // Response token

  private byte[] m_responseToken;

  /**
   * Class constructor for decoding
   */
  public NegTokenTarg() {
  }

  /**
   * Class constructor
   * 
   * @param result int
   * @param mech Oid
   * @param response byte[]
   */
  public NegTokenTarg(int result, Oid mech, byte[] response) {

    m_result = result;
    m_supportedMech = mech;
    m_responseToken = response;
  }

  /**
   * Return the result
   * 
   * @return int
   */
  public final int getResult() {
    return m_result;
  }

  /**
   * Return the supported mech type Oid
   * 
   * @return Oid
   */
  public final Oid getSupportedMech() {
    return m_supportedMech;
  }

  /**
   * Determine if there is a valid response token
   * 
   * @return boolean
   */
  public final boolean hasResponseToken() {
    return m_responseToken != null ? true : false;
  }

  /**
   * Return the response token
   * 
   * @return byte[]
   */
  public final byte[] getResponseToken() {
    return m_responseToken;
  }

  /**
   * Decode an SPNEGO NegTokenTarg blob
   * 
   * @param buf byte[]
   * @param off int
   * @param len int
   * @exception IOException
   */
  public void decode(byte[] buf, int off, int len)
    throws IOException {
	  
    // Create a DER buffer to decode the blob
    
    DERBuffer derBuf = new DERBuffer(buf, off, len);
    
    // Get the first object from the blob
    
    DERObject derObj = derBuf.unpackObject();
    
    if ( derObj instanceof DERSequence) {
      
      // Access the sequence
      
      DERSequence derSeq = (DERSequence) derObj;
      
      // Get the status
      
      derObj = derSeq.getTaggedObject( 0);
      if ( derObj != null) {
      
	      if ( derObj instanceof DEREnumerated == false)
	        throw new IOException( "Invalid status object");
	      
	      DEREnumerated derEnum = (DEREnumerated) derObj;
	      m_result = derEnum.getValue();
      }
      
      // Get the supportedMech (optional)
      
      derObj = derSeq.getTaggedObject( 1);
      if ( derObj != null) {
        
        // Check the object type
        
        if ( derObj instanceof DEROid == false)
          throw new IOException( "Invalid supportedMech object");
        
        DEROid derMech = (DEROid) derObj;
        try {
          m_supportedMech = new Oid( derMech.getOid());
        }
        catch (GSSException ex) {
          throw new IOException( "Bad supportedMech OID");
        }
      }
      else
        m_supportedMech = null;
      
      // Get the responseToken (optional)
      
      derObj = derSeq.getTaggedObject( 2);
      if ( derObj != null) {
        
        // Check the object type
        
        if ( derObj instanceof DEROctetString == false)
          throw new IOException( "Invalid responseToken object");
        
        DEROctetString derResp = (DEROctetString) derObj;
        m_responseToken = derResp.getValue();
      }
      else
        m_responseToken = null;
      
      // Get the mecListMIC (optional)
      
      derObj = derSeq.getTaggedObject( 3);
      if ( derObj != null) {
        
        // Check the object type
        
        if ( derObj instanceof DEROctetString == false)
          throw new IOException( "Invalid mecListMIC object");
        
        DEROctetString derMec = (DEROctetString) derObj;
      }
    }
    else
      throw new IOException( "Bad format in security blob");
  }

  /**
   * Encode an SPNEGO NegTokenTarg blob
   * 
   * @return byte[]
   * @exception IOException
   */
  public byte[] encode()
    throws IOException {

    // Build the sequence of tagged objects
	    
    DERSequence derSeq = new DERSequence();
    derSeq.setTagNo( 1);
	    
    // Add the result
    
    DEREnumerated derEnum = new DEREnumerated( m_result);
    derEnum.setTagNo( 0);
    derSeq.addObject( derEnum);
    
    // Pack the supportedMech, if valid
    
    if ( m_supportedMech != null) {
      DEROid derOid = new DEROid( m_supportedMech.toString());
      derOid.setTagNo( 1);
      derSeq.addObject( derOid);
    }
    
    // Pack the response token, if valid
    
    if ( m_responseToken != null) {
      DEROctetString derResp = new DEROctetString( m_responseToken);
      derResp.setTagNo( 2);
      derSeq.addObject( derResp);
    }
    
    // Pack the objects
    
    DERBuffer derBuf = new DERBuffer();
    derBuf.packObject( derSeq);
    
    // Return the packed negTokenInit blob
    
    return derBuf.getBytes();
  }

  /**
   * Return the NegtokenTarg object as a string
   * 
   * @return String
   */
  public String toString() {

    StringBuffer str = new StringBuffer();

    str.append("[NegtokenTarg result=");
    str.append(SPNEGO.asResultString(getResult()));

    str.append(" oid=");
    str.append(getSupportedMech());

    str.append(" response=");
    if (hasResponseToken()) {
      str.append(getResponseToken().length);
      str.append(" bytes");
    } else
      str.append("null");
    str.append("]");

    return str.toString();
  }
}
