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

package org.alfresco.jlan.smb.nt;

import java.util.Vector;

import org.alfresco.jlan.util.DataBuffer;
import org.alfresco.jlan.util.DataPacker;

/**
 * Access Control List Class
 *
 * @author gkspencer
 */
public class ACL {

	//	List of access control entries
	
	private Vector<ACE> m_aceList;
	
	//	Revision
	
	private int m_revision = 2;
	
	/**
	 * Default constructor
	 */
	public ACL() {
	}

	/**
	 * Class constructor
	 * 
	 * @param ace ACE
	 */
	public ACL(ACE ace) {
		addACE(ace);
	}
	
	/**
	 * Return the revision
	 * 
	 * @return int
	 */
	public final int getRevision() {
		return m_revision;
	}
		
	/**
	 * Return the count of access control entries
	 * 
	 * @return int
	 */
	public final int numberOfEntries() {
		return m_aceList == null ? 0 : m_aceList.size();
	}
	
	/**
	 * Add an access control entry (ACE) to the ACL
	 * 
	 * @param ace ACE
	 */
	public final void addACE(ACE ace) {
		
		//	Check if the list is allocated
		
		if ( m_aceList == null)
			m_aceList = new Vector<ACE>();
			
		//	Add the ACE to the end of the list
		
		m_aceList.addElement(ace);
	}

	/**
	 * Return the required access control entry from the ACL
	 * 
	 * @param idx int
	 * @return ACE
	 */
	public final ACE getACE(int idx) {
		if ( m_aceList == null || idx >=m_aceList.size())
			return null;
		return m_aceList.elementAt(idx);
	}
		
	/**
	 * Delete an access control entry from the ACL
	 * 
	 * @param ace ACE
	 */
	public final void deleteACE(ACE ace) {
		if ( m_aceList != null)
			m_aceList.removeElement(ace);
	}
	
	/**
	 * Delete an access control entry from the ACL
	 * 
	 * @param idx int
	 */
	public final void deleteACE(int idx) {
		if ( m_aceList != null && m_aceList.size() > idx && idx >= 0)
			m_aceList.removeElementAt(idx);
	}
	
	/**
	 * Delete all access control entries from the ACL
	 */
	public final void deleteAllACEs() {
		if ( m_aceList != null) {
			m_aceList.removeAllElements();
			m_aceList = null;
		}
	}
	
	/**
	 * Load the access control list from the specified buffer
	 * 
	 * @param buf byte[]
	 * @param off int
	 * @return int
	 * @exception LoadException
	 */
	public final int loadACL(byte[] buf, int off)
		throws LoadException {
		
		//	Get the ACL revision, ACL size (in bytes) and number of access control entries
		
		m_revision = DataPacker.getIntelShort(buf, off);

		int siz    = DataPacker.getIntelShort(buf, off + 2);
		int aceCnt = DataPacker.getIntelInt(buf, off + 4);

		//	Check if there are any access control entries
		
		if ( aceCnt == 0) {
			m_aceList = null;
			return off + siz;
		}
				
		//	Clear the current ACE list
		
		if ( m_aceList != null)
			m_aceList.removeAllElements();
		else
			m_aceList = new Vector<ACE>();
			
		//	Load the ACE list
		
		int acePos = off + 8;
		
		for ( int i = 0; i < aceCnt; i++) {
			
			//	Create a new access control entry and load it
			
			ACE curAce = new ACE();
			acePos = curAce.loadACE(buf, acePos);
			
			//	Add the entry to the ACLs list
			
			addACE(curAce);
		}
		
		//	Return the new buffer position
		
		return acePos;
	}

	/**
	 * Load the access control list from the specified buffer
	 * 
	 * @param buf DataBuffer
	 * @return int
	 * @exception LoadException
	 */
	public final int loadACL( DataBuffer buf)
		throws LoadException {
		
		//	Get the ACL revision, ACL size (in bytes) and number of access control entries

		int startPos = buf.getPosition();
		
		m_revision = buf.getShort();

		int siz    = buf.getShort();
		int aceCnt = buf.getShort();

		//	Check if there are any access control entries
		
		if ( aceCnt == 0) {
			m_aceList = null;
			return startPos + siz;
		}
				
		//	Clear the current ACE list
		
		if ( m_aceList != null)
			m_aceList.removeAllElements();
		else
			m_aceList = new Vector<ACE>();
			
		//	Load the ACE list
		
		buf.setPosition( startPos + 8);
		
		for ( int i = 0; i < aceCnt; i++) {
			
			//	Create a new access control entry and load it
			
			ACE curAce = new ACE();
			curAce.loadACE( buf);
			
			//	Add the entry to the ACLs list
			
			addACE(curAce);
		}
		
		//	Return the new buffer position
		
		return buf.getPosition();
	}

	/**
	 * Save the access control list to the specified buffer
	 * 
	 * @param buf byte[]
	 * @param off int
	 * @return int
	 * @exception SaveException
	 */
	public final int saveACL(byte[] buf, int off)
		throws SaveException {

		//	Pack the ACL

		int startPos = off;
				
		DataPacker.putIntelShort(m_revision, buf, off);
		DataPacker.putIntelInt(m_aceList != null ? m_aceList.size() : 0, buf, off + 4);

		//	Pack the access control entries, if any

		int endPos = off + 8;
				
		if ( m_aceList != null && m_aceList.size() > 0) {
				
			//	Pack the ACE list
			
			for ( int i = 0; i < m_aceList.size(); i++) {
				
				//	Get the current ACE and pack into the buffer
				
				ACE curAce = getACE(i);
				endPos = curAce.saveACE(buf, endPos);
			}
		}
		
		//	Set the ACL size and return the end offset
		
		DataPacker.putIntelShort(endPos - startPos, buf, off + 2);
		return endPos;
	}
	
	/**
	 * Save the access control list to the specified buffer
	 * 
	 * @param buf DataBuffer
	 * @param off int
	 * @return int
	 * @exception SaveException
	 */
	public final int saveACL( DataBuffer buf)
		throws SaveException {

		//	Pack the ACL

		int startPos = buf.getPosition();

		buf.putShort( m_revision);
		buf.putShort( 0);
		buf.putInt( m_aceList != null ? m_aceList.size() : 0);

		//	Pack the access control entries, if any

		if ( m_aceList != null && m_aceList.size() > 0) {
				
			//	Pack the ACE list
			
			for ( int i = 0; i < m_aceList.size(); i++) {
				
				//	Get the current ACE and pack into the buffer
				
				ACE curAce = getACE(i);
				curAce.saveACE( buf);
			}
		}
		
		//	Set the ACL size and return the end offset

		int endPos = buf.getPosition();
		
		buf.setPosition( startPos + 2);
		buf.putShort( endPos - startPos);
		buf.setPosition( endPos);

		return endPos;
	}
	
	/**
	 * Return the ACL as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer str = new StringBuffer();
		
		str.append("[");
		str.append(numberOfEntries());
		str.append(":");
		
		for ( int i = 0; i < numberOfEntries(); i++) {
			
			//	Get the current ACE and add to the string
			
			ACE curAce = getACE(i);
			str.append(curAce.toString());
			str.append(",");
		}
		
		return str.toString();
	}
}
