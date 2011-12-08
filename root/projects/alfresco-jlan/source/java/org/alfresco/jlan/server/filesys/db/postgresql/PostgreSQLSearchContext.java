/*
 * Copyright (C) 2006-2011 Alfresco Software Limited.
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

package org.alfresco.jlan.server.filesys.db.postgresql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.server.filesys.FileInfo;
import org.alfresco.jlan.server.filesys.FileType;
import org.alfresco.jlan.server.filesys.db.DBSearchContext;
import org.alfresco.jlan.util.WildCard;

/**
 * PostgreSQL Database Search Context Class
 * 
 * @author gkspencer
 */
public class PostgreSQLSearchContext extends DBSearchContext {

	/**
	 * Class constructor
	 * 
	 * @param rs
	 *            ResultSet
	 * @param filter
	 *            WildCard
	 */
	protected PostgreSQLSearchContext(ResultSet rs, WildCard filter) {
		super(rs, filter);
	}

	/**
	 * Return the next file from the search, or return false if there are no
	 * more files
	 * 
	 * @param info
	 *            FileInfo
	 * @return boolean
	 */
	public boolean nextFileInfo(FileInfo info) {

		// Get the next file from the search

		try {

			// Return the next file details or loop until a match is found if a
			// complex wildcard filter
			// has been specified

			while (m_rs.next()) {

				// Get the file name for the next file

				info.setFileId(m_rs.getInt("FileId"));
				info.setFileName(m_rs.getString("FileName"));
				info.setSize(m_rs.getLong("FileSize"));

				long createDate = m_rs.getLong("CreateDate");
				if (createDate != 0L)
					info.setCreationDateTime(createDate);
				else
					info.setCreationDateTime(System.currentTimeMillis());

				long modifyDate = m_rs.getLong("ModifyDate");
				if (modifyDate != 0L)
					info.setModifyDateTime(modifyDate);
				else
					info.setModifyDateTime(System.currentTimeMillis());

				long accessDate = m_rs.getLong("AccessDate");
				if (accessDate != 0L)
					info.setAccessDateTime(accessDate);

				// Build the file attributes flags

				int attr = 0;

				if (m_rs.getBoolean("ReadOnly") == true)
					attr += FileAttribute.ReadOnly;

				if (m_rs.getBoolean("SystemFile") == true)
					attr += FileAttribute.System;

				if (m_rs.getBoolean("Hidden") == true)
					attr += FileAttribute.Hidden;

				if (m_rs.getBoolean("Directory") == true) {
					attr += FileAttribute.Directory;
					info.setFileType(FileType.Directory);
				} else
					info.setFileType(FileType.RegularFile);

				if (m_rs.getBoolean("Archived") == true)
					attr += FileAttribute.Archive;

				// Check if files should be marked as offline

				if (hasMarkAsOffline()) {
					if (getOfflineFileSize() == 0
							|| info.getSize() >= getOfflineFileSize())
						attr += FileAttribute.NTOffline;
				}

				info.setFileAttributes(attr);

				// Get the group/owner id

				info.setGid(m_rs.getInt("Gid"));
				info.setUid(m_rs.getInt("Uid"));

				info.setMode(m_rs.getInt("Mode"));

				// Check if the file is a symbolic link

				if (m_rs.getBoolean("IsSymLink") == true)
					info.setFileType(FileType.SymbolicLink);

				// Check if there is a complex wildcard filter

				if (m_filter == null
						|| m_filter.matchesPattern(info.getFileName()) == true)
					return true;
			}
		} 
		catch (SQLException ex) {
		}

		// No more files

		closeSearch();
		return false;
	}

	/**
	 * Return the file name of the next file in the active search. Returns null
	 * if the search is complete.
	 * 
	 * @return String
	 */
	public String nextFileName() {

		// Get the next file from the search

		try {

			// Return the next file details or loop until a match is found if a
			// complex wildcard filter
			// has been specified

			String fileName = null;

			while (m_rs.next()) {

				// Get the file name for the next file

				fileName = m_rs.getString("FileName");

				// Check if there is a complex wildcard filter

				if (m_filter == null
						|| m_filter.matchesPattern(fileName) == true)
					return fileName;
			}
		} 
		catch (SQLException ex) {
		}

		// No more files

		return null;
	}

	/**
	 * Close the search
	 */
	public void closeSearch() {

		// Check if the resultset is valid, if so then close it

		if ( m_rs != null) {
			
			try {

				// Close the associated statement

				Statement stmt = m_rs.getStatement();
				if (stmt != null)
					stmt.close();
				m_rs.close();
			} 
			catch (Exception ex) {
			}
			
			m_rs = null;
		}

		// Call the base class

		super.closeSearch();
	}
}
