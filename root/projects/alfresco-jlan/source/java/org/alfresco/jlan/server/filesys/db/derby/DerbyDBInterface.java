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

package org.alfresco.jlan.server.filesys.db.derby;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.config.InvalidConfigurationException;
import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.server.filesys.FileExistsException;
import org.alfresco.jlan.server.filesys.FileInfo;
import org.alfresco.jlan.server.filesys.FileName;
import org.alfresco.jlan.server.filesys.FileOpenParams;
import org.alfresco.jlan.server.filesys.FileStatus;
import org.alfresco.jlan.server.filesys.db.DBDataDetails;
import org.alfresco.jlan.server.filesys.db.DBDataDetailsList;
import org.alfresco.jlan.server.filesys.db.DBDataInterface;
import org.alfresco.jlan.server.filesys.db.DBDeviceContext;
import org.alfresco.jlan.server.filesys.db.DBException;
import org.alfresco.jlan.server.filesys.db.DBFileInfo;
import org.alfresco.jlan.server.filesys.db.DBInterface;
import org.alfresco.jlan.server.filesys.db.DBObjectIdInterface;
import org.alfresco.jlan.server.filesys.db.DBQueueInterface;
import org.alfresco.jlan.server.filesys.db.DBSearchContext;
import org.alfresco.jlan.server.filesys.db.JdbcDBInterface;
import org.alfresco.jlan.server.filesys.db.RetentionDetails;
import org.alfresco.jlan.server.filesys.loader.CachedFileInfo;
import org.alfresco.jlan.server.filesys.loader.FileRequest;
import org.alfresco.jlan.server.filesys.loader.FileRequestQueue;
import org.alfresco.jlan.server.filesys.loader.FileSegment;
import org.alfresco.jlan.server.filesys.loader.FileSegmentInfo;
import org.alfresco.jlan.server.filesys.loader.MultipleFileRequest;
import org.alfresco.jlan.server.filesys.loader.SingleFileRequest;
import org.alfresco.jlan.smb.server.ntfs.StreamInfo;
import org.alfresco.jlan.smb.server.ntfs.StreamInfoList;
import org.alfresco.jlan.util.MemorySize;
import org.alfresco.jlan.util.WildCard;
import org.alfresco.jlan.util.db.DBConnectionPool;
import org.springframework.extensions.config.ConfigElement;

/**
 * Derby Base Database Interface Class
 * 
 * <p>
 * Derby (formerly Cloudscape) specific implementation of the database interface used by the
 * database filesystem driver (DBDiskDriver).
 * 
 * @author gkspencer
 */
public class DerbyDBInterface extends JdbcDBInterface implements DBQueueInterface, DBDataInterface, DBObjectIdInterface {

	// Memory buffer maximum size

	public final static long MaxMemoryBuffer = MemorySize.MEGABYTE / 2; // 512Kb

	// Blob reading buffer size

	public static final int BlobReadBuffer = 32768;

	// Lock file name, used to check if server shutdown was clean or not

	public final static String LockFileName = "DerbyLoader.lock";

	// Database connection and prepared statement used to write file requests to the queue tables

	private Connection m_dbConn;
	private PreparedStatement m_reqStmt;
	private PreparedStatement m_tranStmt;

	/**
	 * Default constructor
	 */
	public DerbyDBInterface() {
		super();
	}

	/**
	 * Return the database interface name
	 * 
	 * @return String
	 */
	public String getDBInterfaceName() {
		return "Derby";
	}

	/**
	 * Get the supported database features mask
	 * 
	 * @return int
	 */
	protected int getSupportedFeatures() {

		// Determine the available database interface features

		return FeatureNTFS + FeatureRetention + FeatureSymLinks + FeatureQueue + FeatureData + FeatureJarData + FeatureObjectId;
	}

	/**
	 * Initialize the database interface
	 * 
	 * @param dbCtx DBDeviceContext
	 * @param params ConfigElement
	 * @exception InvalidConfigurationException
	 */
	public void initializeDatabase(DBDeviceContext dbCtx, ConfigElement params)
		throws InvalidConfigurationException {

		// Set the JDBC driver class, must be set before the connection pool is created

		setDriverName("org.apache.derby.jdbc.EmbeddedDriver");

		// Call the base class to do the main initialization

		super.initializeDatabase(dbCtx, params);

		// Create the database connection pool

		try {
			createConnectionPool();
		}
		catch (Exception ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Error creating connection pool, " + ex.toString());

			// Rethrow the exception

			throw new InvalidConfigurationException("Failed to create connection pool, " + ex.getMessage());
		}
		// Check if the file system table exists

		Connection conn = null;

		try {

			// Open a connection to the database

			conn = getConnection();

			DatabaseMetaData dbMeta = conn.getMetaData();
			ResultSet rs = dbMeta.getTables(null, null, null, null);

			boolean foundStruct = false;
			boolean foundStream = false;
			boolean foundRetain = false;
			boolean foundQueue = false;
			boolean foundTrans = false;
			boolean foundData = false;
			boolean foundJarData = false;
			boolean foundObjId = false;

			while (rs.next()) {

				// Get the table name

				String tblName = rs.getString("TABLE_NAME");

				// Check if we found the filesystem structure or streams table

				if ( tblName.equalsIgnoreCase(getFileSysTableName()))
					foundStruct = true;
				else if ( hasStreamsTableName() && tblName.equalsIgnoreCase(getStreamsTableName()))
					foundStream = true;
				else if ( hasRetentionTableName() && tblName.equalsIgnoreCase(getRetentionTableName()))
					foundRetain = true;
				else if ( hasDataTableName() && tblName.equalsIgnoreCase(getDataTableName()))
					foundData = true;
				else if ( hasJarDataTableName() && tblName.equalsIgnoreCase(getJarDataTableName()))
					foundJarData = true;
				else if ( hasQueueTableName() && tblName.equalsIgnoreCase(getQueueTableName()))
					foundQueue = true;
				else if ( hasTransactionTableName() && tblName.equalsIgnoreCase(getTransactionTableName()))
					foundTrans = true;
				else if ( hasObjectIdTableName() && tblName.equalsIgnoreCase(getObjectIdTableName()))
					foundObjId = true;
			}

			// Check if the file system structure table should be created

			if ( foundStruct == false) {

				// Create the file system structure table

				Statement stmt = conn.createStatement();

				stmt.execute("CREATE TABLE "
								+ getFileSysTableName()
								+ "(FileId INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, DirId INTEGER, FileName VARCHAR(255),"
								+ "FileSize INTEGER, CreateDate TIMESTAMP, ModifyDate TIMESTAMP, AccessDate TIMESTAMP, ChangeDate TIMESTAMP, ReadOnlyFile CHAR,"
								+ "ArchivedFile CHAR, DirectoryFile CHAR, SystemFile CHAR, HiddenFile CHAR, OwnerUid INTEGER, OwnerGid INTEGER,"
								+ "FileMode INTEGER, IsDeleted CHAR DEFAULT 'N')");

				// Create various indexes

				stmt.execute("CREATE INDEX IFileDirId ON " + getFileSysTableName() + "(FileName,DirId)");
				stmt.execute("CREATE INDEX IDirId ON " + getFileSysTableName() + "(DirId)");
				stmt.execute("CREATE INDEX IDir ON " + getFileSysTableName() + "(DirId,DirectoryFile)");
				stmt.execute("CREATE INDEX IFileDirIdDir ON " + getFileSysTableName() + "(FileName,DirId,DirectoryFile)");

				stmt.close();

				// DEBUG

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("[Derby] Created table " + getFileSysTableName());
			}

			// Check if the file streams table should be created

			if ( isNTFSEnabled() && foundStream == false && getStreamsTableName() != null) {

				// Create the file streams table

				Statement stmt = conn.createStatement();

				stmt.execute("CREATE TABLE "
								+ getStreamsTableName()
								+ " (StreamId INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, FileId INTEGER NOT NULL, StreamName VARCHAR(255) NOT NULL, StreamSize INTEGER,"
								+ "CreateDate TIMESTAMP, ModifyDate TIMESTAMP, AccessDate TIMESTAMP)");

				// Create various indexes

				stmt.execute("CREATE INDEX IFileId ON " + getStreamsTableName() + "(FileId)");

				stmt.close();

				// DEBUG

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("[Derby] Created table " + getStreamsTableName());
			}

			// Check if the retention table should be created

			if ( isRetentionEnabled() && foundRetain == false && getRetentionTableName() != null) {

				// Create the retention period data table

				Statement stmt = conn.createStatement();

				stmt.execute("CREATE TABLE " + getRetentionTableName() + " (FileId INTEGER NOT NULL PRIMARY KEY,"
						+ "StartDate TIMESTAMP, EndDate TIMESTAMP, PurgeFlag CHAR DEFAULT '0')");
				stmt.close();

				// DEBUG

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("[Derby] Created table " + getRetentionTableName());
			}

			// Check if the file loader queue table should be created

			if ( isQueueEnabled() && foundQueue == false && getQueueTableName() != null) {

				// Create the request queue data table

				Statement stmt = conn.createStatement();

				stmt.execute("CREATE TABLE "
								+ getQueueTableName()
								+ " (FileId INTEGER NOT NULL, StreamId INTEGER NOT NULL, ReqType SMALLINT,"
								+ "SeqNo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, TempFile VARCHAR(512), VirtualPath VARCHAR(512), QueuedAt TIMESTAMP)");

				stmt.execute("CREATE INDEX IQFileId ON " + getQueueTableName() + "(FileId)");
				stmt.execute("CREATE INDEX IQFileIdType ON " + getQueueTableName() + "(FileId, ReqType)");

				stmt.close();

				// DEBUG

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("[Derby] Created table " + getQueueTableName());
			}

			// Check if the file loader transaction queue table should be created

			if ( isQueueEnabled() && foundTrans == false && getTransactionTableName() != null) {

				// Create the transaction request queue data table

				Statement stmt = conn.createStatement();

				stmt.execute("CREATE TABLE "
								+ getTransactionTableName()
								+ " (FileId INTEGER NOT NULL, StreamId INTEGER NOT NULL,"
								+ "TranId INTEGER NOT NULL, ReqType SMALLINT, TempFile VARCHAR(512), VirtualPath VARCHAR(512), QueuedAt TIMESTAMP,"
								+ "PRIMARY KEY (FileId,StreamId,TranId))");

				stmt.close();

				// DEBUG

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("[Derby] Created table " + getTransactionTableName());
			}

			// Check if the file data table should be created

			if ( isDataEnabled() && foundData == false && hasDataTableName()) {

				// Create the file data table

				Statement stmt = conn.createStatement();

				stmt.execute("CREATE TABLE "
								+ getDataTableName()
								+ " (FileId INTEGER NOT NULL, StreamId INTEGER NOT NULL, FragNo INTEGER, FragLen INTEGER, Data BLOB (512K), JarFile CHAR DEFAULT '0', JarId INTEGER)");

				stmt.execute("CREATE INDEX IDataFileStreamId ON " + getDataTableName() + "(FileId,StreamId)");
				stmt.execute("CREATE INDEX IDataFileId ON " + getDataTableName() + "(FileId)");
				stmt.execute("CREATE INDEX IDataFileIdFrag ON " + getDataTableName() + "(FileId,FragNo)");

				stmt.close();

				// DEBUG

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("[Derby] Created table " + getDataTableName());
			}

			// Check if the Jar file data table should be created

			if ( isJarDataEnabled() && foundJarData == false && hasJarDataTableName()) {

				// Create the Jar file data table

				Statement stmt = conn.createStatement();

				stmt.execute("CREATE TABLE " + getJarDataTableName()
						+ " (JarId INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, Data BLOB (512K))");

				stmt.close();

				// DEBUG

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("[Derby] Created table " + getJarDataTableName());
			}

			// Check if the file id/object id mapping table should be created

			if ( isObjectIdEnabled() && foundObjId == false && hasObjectIdTableName()) {

				// Create the file id/object id mapping table

				Statement stmt = conn.createStatement();

				stmt.execute("CREATE TABLE " + getObjectIdTableName()
						+ " (FileId INTEGER NOT NULL, StreamId INTEGER NOT NULL, ObjectId VARCHAR(128), PRIMARY KEY (FileId,StreamId))");

				stmt.close();

				// DEBUG

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("[Derby] Created table " + getObjectIdTableName());
			}
		}
		catch (Exception ex) {
			Debug.println("[Derby] Error: " + ex.toString());
		}
		finally {

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}
	}

	/**
	 * Check if a file/folder exists
	 * 
	 * @param dirId int
	 * @param fname String
	 * @return FileStatus.NotExist, FileStatus.FileExists or FileStatus.DirectoryExists
	 * @throws DBException
	 */
	public int fileExists(int dirId, String fname)
		throws DBException {

		// Check if the file exists, and whether it is a file or folder

		int sts = FileStatus.NotExist;

		Connection conn = null;
		Statement stmt = null;

		try {

			// Get a connection to the database, create a statement for the database lookup

			conn = getConnection();
			stmt = conn.createStatement();

			String sql = "SELECT FileName,DirectoryFile FROM " + getFileSysTableName() + " WHERE DirId = " + dirId
					+ " AND FileName = '" + checkNameForSpecialChars(fname) + "'";

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] File exists SQL: " + sql);

			// Search for the file/folder

			ResultSet rs = stmt.executeQuery(sql);

			// Check if a file record exists

			if ( rs.next()) {

				// Check if the record is for a file or folder

				if ( rs.getBoolean("DirectoryFile") == true)
					sts = FileStatus.DirectoryExists;
				else
					sts = FileStatus.FileExists;
			}

			// Close the result set

			rs.close();
		}
		catch (Exception ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] File exists error " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the status

		return sts;
	}

	/**
	 * Create a file record for a new file or folder
	 * 
	 * @param fname String
	 * @param dirId int
	 * @param params FileOpenParams
	 * @param retain boolean
	 * @return int
	 * @exception DBException
	 * @exception FileExistsException
	 */
	public int createFileRecord(String fname, int dirId, FileOpenParams params, boolean retain)
		throws DBException, FileExistsException {

		// Create a new file record for a file/folder and return a unique file id

		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement stmt = null;

		int fileId = -1;

		try {

			// Get a database connection

			conn = getConnection();

			// Check if the file already exists in the database

			String qsql = "SELECT FileName FROM " + getFileSysTableName() + " WHERE FileName = ? AND DirId = ?";

			stmt = conn.prepareStatement( qsql);
			stmt.setString( 1, fname);
			stmt.setInt( 2, dirId);

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Create file SQL: " + qsql);

			// Check if the file/folder already exists

			ResultSet rs = stmt.executeQuery();
			if ( rs.next())
				throw new FileExistsException();
			stmt.close();

			// Check if a file or folder record should be created

			boolean dirRec = params.isDirectory();

			// Get a statement

			Timestamp timeNow = new Timestamp(System.currentTimeMillis());

			pstmt = conn.prepareStatement("INSERT INTO " + getFileSysTableName()
					+ "(FileName,CreateDate,ModifyDate,AccessDate,DirId,DirectoryFile,ReadOnlyFile,"
					+ "ArchivedFile,SystemFile,HiddenFile,FileSize,OwnerGid,OwnerUid,FileMode)"
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, fname);
			pstmt.setTimestamp(2, timeNow);
			pstmt.setTimestamp(3, timeNow);
			pstmt.setTimestamp(4, timeNow);
			pstmt.setInt(5, dirId);
			pstmt.setBoolean(6, dirRec);
			pstmt.setBoolean(7, FileAttribute.hasAttribute(params.getAttributes(), FileAttribute.ReadOnly));
			pstmt.setBoolean(8, FileAttribute.hasAttribute(params.getAttributes(), FileAttribute.Archive));
			pstmt.setBoolean(9, FileAttribute.hasAttribute(params.getAttributes(), FileAttribute.System));
			pstmt.setBoolean(10, FileAttribute.hasAttribute(params.getAttributes(), FileAttribute.Hidden));
			pstmt.setInt(11, 0);

			pstmt.setInt(12, params.hasGid() ? params.getGid() : 0);
			pstmt.setInt(13, params.hasUid() ? params.getUid() : 0);
			pstmt.setInt(14, params.hasMode() ? params.getMode() : 0);

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Create file SQL: " + pstmt.toString());

			// Create an entry for the new file

			if ( pstmt.executeUpdate() > 0) {

				// Get the last insert id

				stmt = conn.prepareStatement( "SELECT FileId FROM " + getFileSysTableName() + " WHERE FileName = ? AND DirId = ?");
				stmt.setString( 1, fname);
				stmt.setInt( 2, dirId);
				
				ResultSet rs2 = stmt.executeQuery();

				if ( rs2.next())
					fileId = rs2.getInt(1);

				// Check if the returned file id is valid

				if ( fileId == -1)
					throw new DBException("Failed to get file id for " + fname);

				// If retention is enabled then create a retention record for the new file/folder

				if ( retain == true && isRetentionEnabled()) {

					// Create a retention record for the new file/directory

					Timestamp startDate = new Timestamp(System.currentTimeMillis());
					Timestamp endDate = new Timestamp(startDate.getTime() + getRetentionPeriod());

					String rSql = "INSERT INTO " + getRetentionTableName() + " (FileId,StartDate,EndDate) VALUES (" + fileId
							+ ",'" + startDate.toString() + "','" + endDate.toString() + "')";

					// DEBUG

					if ( Debug.EnableInfo && hasSQLDebug())
						Debug.println("[Derby] Add retention record SQL: " + rSql);

					// Add the retention record for the file/folder

					stmt.executeUpdate(rSql);
				}

				// DEBUG

				if ( Debug.EnableInfo && hasDebug())
					Debug.println("[Derby] Created file name=" + fname + ", dirId=" + dirId + ", fileId=" + fileId);
			}
		}
		catch (Exception ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Create file record error " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Close the prepared statement

			if ( pstmt != null) {
				try {
					pstmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Close the query statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the allocated file id

		return fileId;
	}

	/**
	 * Create a stream record for a new file stream
	 * 
	 * @param sname String
	 * @param fid int
	 * @return int
	 * @exception DBException
	 */
	public int createStreamRecord(String sname, int fid)
		throws DBException {

		// Create a new file stream attached to the specified file

		Connection conn = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;

		int streamId = -1;

		try {

			// Get a database connection

			conn = getConnection();

			// Get a statement

			Timestamp timeNow = new Timestamp(System.currentTimeMillis());

			stmt = conn.prepareStatement("INSERT INTO " + getStreamsTableName()
					+ "(FileId,StreamName,CreateDate,ModifyDate,AccessDate,StreamSize) VALUES (?,?,?,?,?,?)");
			stmt.setInt(1, fid);
			stmt.setString(2, sname);
			stmt.setTimestamp(3, timeNow);
			stmt.setTimestamp(4, timeNow);
			stmt.setTimestamp(5, timeNow);
			stmt.setInt(6, 0);

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Create stream SQL: " + stmt.toString());

			// Create an entry for the new stream

			if ( stmt.executeUpdate() > 0) {

				// Get the stream id for the newly created stream

				stmt2 = conn.prepareStatement( "SELECT StreamId FROM " + getStreamsTableName() + " WHERE FileId = ? AND StreamName = ?");
				stmt2.setInt( 1, fid);
				stmt2.setString( 2, sname);

				ResultSet rs2 = stmt2.executeQuery();

				if ( rs2.next())
					streamId = rs2.getInt(1);
				rs2.close();
			}
		}
		catch (Exception ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Create file stream error " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Close the statements

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			if ( stmt2 != null) {
				try {
					stmt2.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the allocated stream id

		return streamId;
	}

	/**
	 * Delete a file or folder record
	 * 
	 * @param dirId int
	 * @param fid int
	 * @param markOnly boolean
	 * @exception DBException
	 */
	public void deleteFileRecord(int dirId, int fid, boolean markOnly)
		throws DBException {

		// Delete a file record from the database, or mark the file record as deleted

		Connection conn = null;
		Statement stmt = null;

		try {

			// Get a connection to the database

			conn = getConnection();

			// Delete the file entry from the database

			stmt = conn.createStatement();
			String sql = null;

			if ( markOnly == true)
				sql = "UPDATE " + getFileSysTableName() + " SET IsDeleted = 'Y' WHERE FileId = " + fid;
			else
				sql = "DELETE FROM " + getFileSysTableName() + " WHERE FileId = " + fid;

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Delete file SQL: " + sql);

			// Delete the file/folder, or mark as deleted

			stmt.executeUpdate(sql);
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Delete file error " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}
	}

	/**
	 * Delete a file stream record
	 * 
	 * @param fid int
	 * @param stid int
	 * @param markOnly boolean
	 * @exception DBException
	 */
	public void deleteStreamRecord(int fid, int stid, boolean markOnly)
		throws DBException {

		// Delete a file stream from the database, or mark the stream as deleted

		Connection conn = null;
		Statement stmt = null;

		try {

			// Get a database connection

			conn = getConnection();

			// Get a statement

			stmt = conn.createStatement();
			String sql = "DELETE FROM " + getStreamsTableName() + " WHERE FileId = " + fid + " AND StreamId = " + stid;

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Delete stream SQL: " + sql);

			// Delete the stream record

			stmt.executeUpdate(sql);
		}
		catch (Exception ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Delete stream error: " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}
	}

	/**
	 * Set file information for a file or folder
	 * 
	 * @param dirId int
	 * @param fid int
	 * @param finfo FileInfo
	 * @exception DBException
	 */
	public void setFileInformation(int dirId, int fid, FileInfo finfo)
		throws DBException {

		// Set file information fields

		Connection conn = null;
		Statement stmt = null;

		try {

			// Get a connection to the database

			conn = getConnection();

			// Build the SQL statement to update the file information settings

			StringBuffer sql = new StringBuffer(256);
			sql.append("UPDATE ");
			sql.append(getFileSysTableName());
			sql.append(" SET ");

			// Check if the file attributes have been updated

			if ( finfo.hasSetFlag(FileInfo.SetAttributes)) {

				// Update the basic file attributes

				sql.append("ReadOnlyFile = ");
				sql.append(finfo.isReadOnly() ? "'1'" : "'0'");

				sql.append(", ArchivedFile =");
				sql.append(finfo.isArchived() ? "'1'" : "'0'");

				sql.append(", SystemFile = ");
				sql.append(finfo.isSystem() ? "'1'" : "'0'");

				sql.append(", HiddenFile = ");
				sql.append(finfo.isHidden() ? "'1'" : "'0'");
				sql.append(",");
			}

			// Check if the file size should be set

			if ( finfo.hasSetFlag(FileInfo.SetFileSize)) {

				// Update the file size

				sql.append("FileSize = ");
				sql.append(finfo.getSize());
				sql.append(",");
			}

			// Merge the group id, user id and mode into the in-memory file information

			if ( finfo.hasSetFlag(FileInfo.SetGid)) {

				// Update the group id

				sql.append("OwnerGid = ");
				sql.append(finfo.getGid());
				sql.append(",");
			}

			if ( finfo.hasSetFlag(FileInfo.SetUid)) {

				// Update the user id

				sql.append("OwnerUid = ");
				sql.append(finfo.getUid());
				sql.append(",");
			}

			if ( finfo.hasSetFlag(FileInfo.SetMode)) {

				// Update the mode

				sql.append("FileMode = ");
				sql.append(finfo.getMode());
				sql.append(",");
			}

			// Check if the access date/time has been set

			if ( finfo.hasSetFlag(FileInfo.SetAccessDate)) {

				// Add the SQL to update the access date/time

				sql.append(" AccessDate = TIMESTAMP('");
				sql.append(new Timestamp(finfo.getAccessDateTime()));
				sql.append("'),");
			}

			// Check if the modify date/time has been set

			if ( finfo.hasSetFlag(FileInfo.SetModifyDate)) {

				// Add the SQL to update the modify date/time

				sql.append(" ModifyDate = TIMESTAMP('");
				sql.append(new Timestamp(finfo.getModifyDateTime()));
				sql.append("'),");
			}

			// Check if the inode change date/time has been set

			if ( finfo.hasSetFlag(FileInfo.SetChangeDate)) {

				// Add the SQL to update the change date/time

				sql.append(" ChangeDate = TIMESTAMP('");
				sql.append(new Timestamp(finfo.getChangeDateTime()));
				sql.append("')");
			}

			// Trim any trailing comma

			if ( sql.charAt(sql.length() - 1) == ',')
				sql.setLength(sql.length() - 1);

			// Complete the SQL request string

			sql.append(" WHERE FileId = ");
			sql.append(fid);
			sql.append("");

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Set file info SQL: " + sql.toString());

			// Create the SQL statement

			stmt = conn.createStatement();
			stmt.executeUpdate(sql.toString());
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Set file information error " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}
	}

	/**
	 * Set information for a file stream
	 * 
	 * @param dirId int
	 * @param fid int
	 * @param stid int
	 * @param sinfo StreamInfo
	 * @exception DBException
	 */
	public void setStreamInformation(int dirId, int fid, int stid, StreamInfo sinfo)
		throws DBException {

		// Set file stream information fields

		Connection conn = null;
		Statement stmt = null;

		try {

			// Get a connection to the database

			conn = getConnection();

			// Build the SQL statement to update the file information settings

			StringBuffer sql = new StringBuffer(256);
			sql.append("UPDATE ");
			sql.append(getStreamsTableName());
			sql.append(" SET ");

			// Check if the access date/time has been set

			if ( sinfo.hasSetFlag(StreamInfo.SetAccessDate)) {

				// Add the SQL to update the access date/time

				sql.append(" AccessDate = TIMESTAMP('");
				sql.append(new Timestamp(sinfo.getAccessDateTime()));
				sql.append("'),");
			}

			// Check if the modify date/time has been set

			if ( sinfo.hasSetFlag(StreamInfo.SetModifyDate)) {

				// Add the SQL to update the modify date/time

				sql.append(" ModifyDate = TIMESTAMP('");
				sql.append(new Timestamp(sinfo.getModifyDateTime()));
				sql.append("'),");
			}

			// Check if the stream size should be updated

			if ( sinfo.hasSetFlag(StreamInfo.SetStreamSize)) {

				// Update the stream size

				sql.append(" StreamSize = ");
				sql.append(sinfo.getSize());
			}

			// Trim any trailing comma

			if ( sql.charAt(sql.length() - 1) == ',')
				sql.setLength(sql.length() - 1);

			// Complete the SQL request string

			sql.append(" WHERE FileId = ");
			sql.append(fid);
			sql.append(" AND StreamId = ");
			sql.append(stid);
			sql.append("");

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Set stream info SQL: " + sql.toString());

			// Create the SQL statement

			stmt = conn.createStatement();
			stmt.executeUpdate(sql.toString());
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Set stream information error " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}
	}

	/**
	 * Get the id for a file/folder, or -1 if the file/folder does not exist.
	 * 
	 * @param dirId int
	 * @param fname String
	 * @param dirOnly boolean
	 * @param caseLess boolean
	 * @return int
	 * @throws DBException
	 */
	public int getFileId(int dirId, String fname, boolean dirOnly, boolean caseLess)
		throws DBException {

		// Get the file id for a file/folder

		int fileId = -1;

		Connection conn = null;
		PreparedStatement stmt = null;

		try {

			// Get a connection to the database, create a statement for the database lookup

			conn = getConnection();

			// Build the SQL for the file lookup

			StringBuffer sql = new StringBuffer(128);

			sql.append("SELECT FileId FROM ");
			sql.append(getFileSysTableName());
			sql.append(" WHERE DirId = ? AND ");

			// Check if the search is for a directory only

			if ( dirOnly == true) {

				// Search for a directory record

				sql.append(" DirectoryFile = '1' AND ");
			}

			// Check if the file name search should be caseless

			if ( caseLess == true) {

				// Perform a caseless search

				sql.append(" UPPER(FileName) = ?");
				fname = fname.toUpperCase();
			}
			else {

				// Perform a case sensitive search

				sql.append(" FileName = ?");
			}

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Get file id SQL: " + sql.toString());

			// Use a prepared statement 
			
			stmt = conn.prepareStatement( sql.toString());
			stmt.setInt(1, dirId);
			stmt.setString(2, fname);
			
			// Run the database search

			ResultSet rs = stmt.executeQuery();

			// Check if a file record exists

			if ( rs.next()) {

				// Get the unique file id for the file or folder

				fileId = rs.getInt("FileId");
			}

			// Close the result set

			rs.close();
		}
		catch (Exception ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Get file id error " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the file id, or -1 if not found

		return fileId;
	}

	/**
	 * Get information for a file or folder
	 * 
	 * @param dirId int
	 * @param fid int
	 * @param infoLevel int
	 * @return FileInfo
	 * @exception DBException
	 */
	public DBFileInfo getFileInformation(int dirId, int fid, int infoLevel)
		throws DBException {

		// Create a SQL select for the required file information

		StringBuffer sql = new StringBuffer(128);

		sql.append("SELECT ");

		// Select fields according to the required information level

		switch (infoLevel) {

			// File name only

			case DBInterface.FileNameOnly:
				sql.append("FileName");
				break;

			// File ids and name

			case DBInterface.FileIds:
				sql.append("FileName,FileId,DirId");
				break;

			// All file information

			case DBInterface.FileAll:
				sql.append("*");
				break;

			// Unknown information level

			default:
				throw new DBException("Invalid information level, " + infoLevel);
		}

		sql.append(" FROM ");
		sql.append(getFileSysTableName());
		sql.append(" WHERE FileId = ");
		sql.append(fid);

		// DEBUG

		if ( Debug.EnableInfo && hasSQLDebug())
			Debug.println("[Derby] Get file info SQL: " + sql.toString());

		// Load the file record

		Connection conn = null;
		Statement stmt = null;

		DBFileInfo finfo = null;

		try {

			// Get a connection to the database

			conn = getConnection();
			stmt = conn.createStatement();

			// Load the file record

			ResultSet rs = stmt.executeQuery(sql.toString());

			if ( rs != null && rs.next()) {

				// Create the file informaiton object

				finfo = new DBFileInfo();
				finfo.setFileId(fid);

				// Load the file information

				switch (infoLevel) {

					// File name only

					case DBInterface.FileNameOnly:
						finfo.setFileName(rs.getString("FileName"));
						break;

					// File ids and name

					case DBInterface.FileIds:
						finfo.setFileName(rs.getString("FileName"));
						finfo.setDirectoryId(rs.getInt("DirId"));
						break;

					// All file information

					case DBInterface.FileAll:
						finfo.setFileName(rs.getString("FileName"));
						finfo.setSize(rs.getLong("FileSize"));
						finfo.setAllocationSize(finfo.getSize());
						finfo.setDirectoryId(rs.getInt("DirId"));

						// Load the various file date/times

						Timestamp ts = rs.getTimestamp("CreateDate");
						finfo.setCreationDateTime(ts != null ? ts.getTime() : 0L);

						ts = rs.getTimestamp("ModifyDate");
						finfo.setModifyDateTime(ts != null ? ts.getTime() : 0L);

						ts = rs.getTimestamp("AccessDate");
						finfo.setAccessDateTime(ts != null ? ts.getTime() : 0L);

						ts = rs.getTimestamp("ChangeDate");
						finfo.setChangeDateTime(ts != null ? ts.getTime() : 0L);

						// Build the file attributes flags

						int attr = 0;

						if ( rs.getBoolean("ReadOnlyFile") == true)
							attr += FileAttribute.ReadOnly;

						if ( rs.getBoolean("SystemFile") == true)
							attr += FileAttribute.System;

						if ( rs.getBoolean("HiddenFile") == true)
							attr += FileAttribute.Hidden;

						if ( rs.getBoolean("DirectoryFile") == true)
							attr += FileAttribute.Directory;

						if ( rs.getBoolean("ArchivedFile") == true)
							attr += FileAttribute.Archive;

						finfo.setFileAttributes(attr);

						// Get the group/owner id

						finfo.setGid(rs.getInt("OwnerGid"));
						finfo.setUid(rs.getInt("OwnerUid"));

						finfo.setMode(rs.getInt("FileMode"));
						break;
				}
			}
		}
		catch (Exception ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Get file information error " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the file information

		return finfo;
	}

	/**
	 * Get information for a file stream
	 * 
	 * @param fid int
	 * @param stid int
	 * @param infoLevel int
	 * @return StreamInfo
	 * @exception DBException
	 */
	public StreamInfo getStreamInformation(int fid, int stid, int infoLevel)
		throws DBException {

		// Create a SQL select for the required stream information

		StringBuffer sql = new StringBuffer(128);

		sql.append("SELECT ");

		// Select fields according to the required information level

		switch (infoLevel) {

			// Stream name only.
			//
			// Also used if ids are requested as we already have the ids

			case DBInterface.StreamNameOnly:
			case DBInterface.StreamIds:
				sql.append("StreamName");
				break;

			// All file information

			case DBInterface.StreamAll:
				sql.append("*");
				break;

			// Unknown information level

			default:
				throw new DBException("Invalid information level, " + infoLevel);
		}

		sql.append(" FROM ");
		sql.append(getStreamsTableName());
		sql.append(" WHERE FileId = ");
		sql.append(fid);
		sql.append(" AND StreamId = ");
		sql.append(stid);

		// DEBUG

		if ( Debug.EnableInfo && hasSQLDebug())
			Debug.println("[Derby] Get stream info SQL: " + sql.toString());

		// Load the stream record

		Connection conn = null;
		Statement stmt = null;

		StreamInfo sinfo = null;

		try {

			// Get a connection to the database

			conn = getConnection();
			stmt = conn.createStatement();

			// Load the stream record

			ResultSet rs = stmt.executeQuery(sql.toString());

			if ( rs != null && rs.next()) {

				// Create the stream informaiton object

				sinfo = new StreamInfo("", fid, stid);

				// Load the file information

				switch (infoLevel) {

					// Stream name only (or name and ids)

					case DBInterface.StreamNameOnly:
					case DBInterface.StreamIds:
						sinfo.setName(rs.getString("StreamName"));
						break;

					// All stream information

					case DBInterface.FileAll:
						sinfo.setName(rs.getString("StreamName"));
						sinfo.setSize(rs.getLong("StreamSize"));

						// Load the various file date/times

						Timestamp ts = rs.getTimestamp("CreateDate");
						sinfo.setCreationDateTime(ts != null ? ts.getTime() : 0L);

						ts = rs.getTimestamp("ModifyDate");
						sinfo.setModifyDateTime(ts != null ? ts.getTime() : 0L);

						ts = rs.getTimestamp("AccessDate");
						sinfo.setAccessDateTime(ts != null ? ts.getTime() : 0L);
						break;
				}
			}
		}
		catch (Exception ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Get stream information error " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the stream information

		return sinfo;
	}

	/**
	 * Return the list of streams for the specified file
	 * 
	 * @param fid int
	 * @param infoLevel int
	 * @return StreamInfoList
	 * @exception DBException
	 */
	public StreamInfoList getStreamsList(int fid, int infoLevel)
		throws DBException {

		// Create a SQL select for the required stream information

		StringBuffer sql = new StringBuffer(128);

		sql.append("SELECT ");

		// Select fields according to the required information level

		switch (infoLevel) {

			// Stream name only.

			case DBInterface.StreamNameOnly:
				sql.append("StreamName");
				break;

			// Stream name and ids

			case DBInterface.StreamIds:
				sql.append("StreamName,FileId,StreamId");
				break;

			// All file information

			case DBInterface.StreamAll:
				sql.append("*");
				break;

			// Unknown information level

			default:
				throw new DBException("Invalid information level, " + infoLevel);
		}

		sql.append(" FROM ");
		sql.append(getStreamsTableName());
		sql.append(" WHERE FileId = ");
		sql.append(fid);

		// DEBUG

		if ( Debug.EnableInfo && hasSQLDebug())
			Debug.println("[Derby] Get stream list SQL: " + sql.toString());

		// Load the stream record

		Connection conn = null;
		Statement stmt = null;

		StreamInfoList sList = null;

		try {

			// Get a connection to the database

			conn = getConnection();
			stmt = conn.createStatement();

			// Load the stream records

			ResultSet rs = stmt.executeQuery(sql.toString());
			sList = new StreamInfoList();

			while (rs.next()) {

				// Create the stream informaiton object

				StreamInfo sinfo = new StreamInfo("", fid, -1);

				// Load the file information

				switch (infoLevel) {

					// Stream name only

					case DBInterface.StreamNameOnly:
						sinfo.setName(rs.getString("StreamName"));
						break;

					// Stream name and id

					case DBInterface.StreamIds:
						sinfo.setName(rs.getString("StreamName"));
						sinfo.setStreamId(rs.getInt("StreamId"));
						break;

					// All stream information

					case DBInterface.FileAll:
						sinfo.setName(rs.getString("StreamName"));
						sinfo.setStreamId(rs.getInt("StreamId"));
						sinfo.setSize(rs.getLong("StreamSize"));

						// Load the various file date/times

						Timestamp ts = rs.getTimestamp("CreateDate");
						sinfo.setCreationDateTime(ts != null ? ts.getTime() : 0L);

						ts = rs.getTimestamp("ModifyDate");
						sinfo.setModifyDateTime(ts != null ? ts.getTime() : 0L);

						ts = rs.getTimestamp("AccessDate");
						sinfo.setAccessDateTime(ts != null ? ts.getTime() : 0L);
						break;
				}

				// Add the stream information to the list

				sList.addStream(sinfo);
			}
		}
		catch (Exception ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Get stream list error " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the streams list

		return sList;
	}

	/**
	 * Rename a file or folder, may also change the parent directory.
	 * 
	 * @param dirId int
	 * @param fid int
	 * @param newName String
	 * @param newDir int
	 * @exception DBException
	 * @exception FileNotFoundException
	 */
	public void renameFileRecord(int dirId, int fid, String newName, int newDir)
		throws DBException, FileNotFoundException {

		// Rename a file/folder

		Connection conn = null;
		PreparedStatement stmt = null;

		try {

			// Get a connection to the database

			conn = getConnection();

			// Update the file record

			String sql = "UPDATE " + getFileSysTableName() + " SET FileName = ?, DirId = ?, ChangeDate = ? WHERE FileId = ?";

			stmt = conn.prepareStatement( sql);
			stmt.setString( 1, newName);
			stmt.setInt( 2, newDir);
			stmt.setTimestamp( 3, new Timestamp(System.currentTimeMillis()));
			stmt.setInt( 4, fid);

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Rename SQL: " + sql);

			// Rename the file/folder

			if ( stmt.executeUpdate() == 0) {

				// Original file not found

				throw new FileNotFoundException("" + fid);
			}
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Rename file error " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}
	}

	/**
	 * Rename a file stream
	 * 
	 * @param dirId int
	 * @param fid int
	 * @param stid int
	 * @param newName
	 * @exception DBException
	 */
	public void renameStreamRecord(int dirId, int fid, int stid, String newName)
		throws DBException {
		// TODO Auto-generated method stub

	}

	/**
	 * Return the retention period expiry date/time for the specified file, or zero if the
	 * file/folder is not under retention.
	 * 
	 * @param dirId int
	 * @param fid int
	 * @return RetentionDetails
	 * @exception DBException
	 */
	public RetentionDetails getFileRetentionDetails(int dirId, int fid)
		throws DBException {

		// Check if retention is enabled

		if ( isRetentionEnabled() == false)
			return null;

		// Get the retention record for the file/folder

		Connection conn = null;
		Statement stmt = null;

		RetentionDetails retDetails = null;

		try {

			// Get a connection to the database

			conn = getConnection();
			stmt = conn.createStatement();

			// Get the retention record, if any

			retDetails = getRetentionExpiryDateTime(conn, stmt, fid);
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Get retention error " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the retention details

		return retDetails;
	}

	/**
	 * Start a directory search
	 * 
	 * @param dirId int
	 * @param searchPath String
	 * @param attrib int
	 * @param infoLevel int
	 * @param maxRecords int
	 * @return DBSearchContext
	 * @exception DBException
	 */
	public DBSearchContext startSearch(int dirId, String searchPath, int attrib, int infoLevel, int maxRecords)
		throws DBException {

		// Search for files/folders in the specified folder

		StringBuffer sql = new StringBuffer(128);
		sql.append("SELECT * FROM ");
		sql.append(getFileSysTableName());
		sql.append(" WHERE DirId = ? AND IsDeleted = 'N'");

		// Split the search path

		String[] paths = FileName.splitPath(searchPath);

		// Check if the file name contains wildcard characters

		WildCard wildCard = null;
		String searchStr = null;
		
		if ( WildCard.containsWildcards(searchPath)) {

			// For the '*.*' and '*' wildcards the SELECT will already return all files/directories
			// that are attached to the parent directory. For 'name.*' and '*.ext' type wildcards we can use the LIKE clause
			// to filter the required records, for more complex wildcards we will post-process the search using the
			// WildCard class to match the file names.

			if ( searchPath.endsWith("\\*.*") == false && searchPath.endsWith("\\*") == false) {

				// Use a wildcard match either before or after the search string
				
				sql.append(" AND FileName LIKE(?)");
				
				// Create a wildcard search pattern

				wildCard = new WildCard(paths[1], true);

				// Check for a 'name.*' type wildcard

				if ( wildCard.isType() == WildCard.WILDCARD_EXT) {

					// Set the search string

					searchStr = wildCard.getMatchPart() + "%";
					
					// Clear the wildcard object, we do not want it to filter the search results

					wildCard = null;
				}
				else if ( wildCard.isType() == WildCard.WILDCARD_NAME) {

					// Set the search string

					searchStr = "%" + wildCard.getMatchPart();

					// Clear the wildcard object, we do not want it to filter the search results

					wildCard = null;
				}
			}
		}
		else {

			// Search for a specific file/directory

			sql.append(" AND FileName = ?");
			searchStr = paths[1];
		}

		// Return directories first

		sql.append(" ORDER BY DirectoryFile DESC");
		
		// Start the search

		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement stmt = null;

		try {

			// Get a connection to the database

			conn = getConnection();
			stmt = conn.prepareStatement( sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			stmt.setInt( 1, dirId);
			if ( searchStr != null)
				stmt.setString( 2, searchStr);

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Start search SQL: " + sql.toString());

			// Start the folder search

			rs = stmt.executeQuery();
		}
		catch (Exception ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Start search error " + ex.getMessage());

			// Rethrow the exception

			throw new DBException(ex.toString());
		}
		finally {

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Create the search context, and return

		return new DerbySearchContext(rs, wildCard);
	}

	/**
	 * Return the used file space, or -1 if not supported.
	 * 
	 * @return long
	 */
	public long getUsedFileSpace() {

		// Calculate the total used file space

		Connection conn = null;
		Statement stmt = null;

		long usedSpace = -1L;

		try {

			// Get a database connection and statement

			conn = getConnection();
			stmt = conn.createStatement();

			String sql = "SELECT SUM(CAST(FileSize as BIGINT)) FROM " + getFileSysTableName();

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Get filespace SQL: " + sql);

			// Calculate the currently used disk space

			ResultSet rs = stmt.executeQuery(sql);

			if ( rs.next())
				usedSpace = rs.getLong(1);
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println("[Derby] Get used file space error " + ex.getMessage());
		}
		finally {

			// Close the prepared statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the used file space

		return usedSpace;
	}

	/**
	 * Shutdown the database interface, release resources.
	 * 
	 * @param context DBDeviceContext
	 */
	public void shutdownDatabase(DBDeviceContext context) {

		// Call the base class

		super.shutdownDatabase(context);
	}

	/**
	 * Get the retention expiry date/time for a file/folder
	 * 
	 * @param conn Connection
	 * @param stmt Statement
	 * @param fid int
	 * @return RetentionDetails
	 * @exception SQLException
	 */
	private final RetentionDetails getRetentionExpiryDateTime(Connection conn, Statement stmt, int fid)
		throws SQLException {

		// Get the retention expiry date/time for the specified file/folder

		RetentionDetails retDetails = null;
		String sql = "SELECT StartDate,EndDate FROM " + getRetentionTableName() + " WHERE FileId = " + fid;

		// DEBUG

		if ( Debug.EnableInfo && hasSQLDebug())
			Debug.println("[Derby] Get retention expiry SQL: " + sql);

		// Get the retention record, if any

		ResultSet rs = stmt.executeQuery(sql);

		if ( rs.next()) {

			// Get the retention expiry date

			Timestamp startDate = rs.getTimestamp("StartDate");
			Timestamp endDate = rs.getTimestamp("EndDate");

			retDetails = new RetentionDetails(fid, startDate != null ? startDate.getTime() : -1L, endDate.getTime());
		}

		// Return the retention details

		return retDetails;
	}

	/**
	 * Determine if the specified file/folder is still within an active retention period
	 * 
	 * @param conn Connection
	 * @param stmt Statement
	 * @param fid int
	 * @return boolean
	 * @exception SQLException
	 */
	private final boolean fileHasActiveRetention(Connection conn, Statement stmt, int fid)
		throws SQLException {

		// Check if retention is enabled

		if ( isRetentionEnabled() == false)
			return false;

		// Check if the file/folder is within the retention period

		RetentionDetails retDetails = getRetentionExpiryDateTime(conn, stmt, fid);
		if ( retDetails == null)
			return false;

		// Check if the file/folder is within the retention period

		return retDetails.isWithinRetentionPeriod(System.currentTimeMillis());
	}

	// ***** DBQueueInteface Methods *****

	/**
	 * Queue a file request.
	 * 
	 * @param req FileRequest
	 * @exception DBException
	 */
	public void queueFileRequest(FileRequest req)
		throws DBException {

		// Make sure the associated file state stays in memory for a short time, if the queue is
		// small the request may get processed soon.

		if ( req instanceof SingleFileRequest) {

			// Get the request details

			SingleFileRequest fileReq = (SingleFileRequest) req;

			try {

				// Check if the file request queue database connection is valid

				if ( m_dbConn == null || m_dbConn.isClosed() || m_reqStmt == null || m_reqStmt.getConnection().isClosed())
					createQueueStatements();

				// Check if the request is part of a transaction, or a standalone request

				if ( fileReq.isTransaction() == false) {

					// Write the file request record

					int recCnt = 0;

					synchronized (m_reqStmt) {

						// Write the file request to the queue database

						m_reqStmt.clearParameters();

						m_reqStmt.setInt(1, fileReq.getFileId());
						m_reqStmt.setInt(2, fileReq.getStreamId());
						m_reqStmt.setInt(3, fileReq.isType());
						m_reqStmt.setString(4, fileReq.getTemporaryFile());
						m_reqStmt.setString(5, fileReq.getVirtualPath());

						recCnt = m_reqStmt.executeUpdate();

						// Retrieve the allocated sequence number

						if ( recCnt > 0) {

							// Get the last insert id

							Statement stmt = m_dbConn.createStatement();
							ResultSet rs2 = stmt.executeQuery("VALUES IDENTITY_VAL_LOCAL()");

							if ( rs2.next())
								fileReq.setSequenceNumber(rs2.getInt(1));
						}
					}
				}
				else {

					// Check if the transaction prepared statement is valid, we may have lost the
					// connection to the database.

					if ( m_tranStmt == null || m_tranStmt.getConnection().isClosed())
						createQueueStatements();

					// Write the transaction file request to the database

					synchronized (m_tranStmt) {

						// Write the request record to the database

						m_tranStmt.clearParameters();

						m_tranStmt.setInt(1, fileReq.getFileId());
						m_tranStmt.setInt(2, fileReq.getStreamId());
						m_tranStmt.setInt(3, fileReq.isType());
						m_tranStmt.setInt(4, fileReq.getTransactionId());
						m_tranStmt.setString(5, fileReq.getTemporaryFile());
						m_tranStmt.setString(6, fileReq.getVirtualPath());

						m_tranStmt.executeUpdate();
					}
				}
			}
			catch (SQLException ex) {

				// If the request is a save then add to a pending queue to retry when the database
				// is back online

				if ( fileReq.isType() == FileRequest.SAVE || fileReq.isType() == FileRequest.TRANSSAVE)
					queueOfflineSaveRequest(fileReq);

				// DEBUG

				if ( Debug.EnableError && hasDebug())
					Debug.println(ex);

				// Rethrow the exception

				throw new DBException(ex.getMessage());
			}
		}
	}

	/**
	 * Perform a queue cleanup deleting temporary cache files that do not have an associated save or
	 * transaction request.
	 * 
	 * @param tempDir File
	 * @param tempDirPrefix String
	 * @param tempFilePrefix String
	 * @param jarFilePrefix String
	 * @return FileRequestQueue
	 * @throws DBException
	 */
	public FileRequestQueue performQueueCleanup(File tempDir, String tempDirPrefix, String tempFilePrefix, String jarFilePrefix)
		throws DBException {

		// Get a connection to the database

		Connection conn = null;
		PreparedStatement stmt = null;
		Statement delStmt = null;

		int reqCnt = 0;

		try {

			// Get a connection to the database

			conn = getConnection(DBConnectionPool.PermanentLease);

			// Delete all load requests from the queue

			delStmt = conn.createStatement();
			delStmt.executeUpdate("DELETE FROM " + getQueueTableName() + " WHERE ReqType = " + FileRequest.LOAD);

			// Create a statement to check if a temporary file is part of a save request

			stmt = conn.prepareStatement("SELECT FileId,SeqNo FROM " + getQueueTableName() + " WHERE TempFile = ?");

			// Scan all files/sub-directories within the temporary area looking for files that have
			// been saved but not
			// deleted due to a server shutdown or crash.

			File[] tempFiles = tempDir.listFiles();

			if ( tempFiles != null && tempFiles.length > 0) {

				// Scan the file loader sub-directories for temporary files

				for (int i = 0; i < tempFiles.length; i++) {

					// Get the current file/sub-directory

					File curFile = tempFiles[i];

					if ( curFile.isDirectory() && curFile.getName().startsWith(tempDirPrefix)) {

						// Check if the sub-directory has any loader temporary files

						File[] subFiles = curFile.listFiles();

						if ( subFiles != null && subFiles.length > 0) {

							// Check each file to see if it has a pending save request in the file
							// request database

							for (int j = 0; j < subFiles.length; j++) {

								// Get the current file from the list

								File ldrFile = subFiles[j];

								if ( ldrFile.isFile() && ldrFile.getName().startsWith(tempFilePrefix)) {

									try {

										// Get the file details from the file system table

										stmt.clearParameters();
										stmt.setString(1, ldrFile.getAbsolutePath());

										ResultSet rs = stmt.executeQuery();

										if ( rs.next()) {

											// Update the recovery file count

											reqCnt++;
										}
										else {

											// There is no pending save for the temporary file,
											// delete it

											ldrFile.delete();

											// DEBUG

											if ( Debug.EnableInfo && hasDebug())
												Debug.println("[Derby] Deleted unqueued file " + ldrFile.getName());
										}
									}
									catch (SQLException ex) {
										Debug.println(ex);
									}
								}
								else {

									// DEBUG

									if ( Debug.EnableInfo && hasDebug())
										Debug.println("[Derby] Deleted temporary file " + ldrFile.getName());

									// Delete the temporary file

									ldrFile.delete();
								}
							}
						}
					}
				}
			}
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println(ex);
		}
		finally {

			// Close the delete statment

			if ( delStmt != null) {
				try {
					delStmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Close the prepared statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// DEBUG

		if ( Debug.EnableInfo && hasDebug())
			Debug.println("[Derby] Cleanup recovered " + reqCnt + " file saves from previous run");

		// Return the recovered file save requests
		//
		// TODO: Implement recovery logic

		return null;
	}

	/**
	 * Check if the specified temporary file has a queued request.
	 * 
	 * @param tempFile String
	 * @param lastFile boolean
	 * @return boolean
	 * @exception DBException
	 */
	public boolean hasQueuedRequest(String tempFile, boolean lastFile)
		throws DBException {

		Connection conn = null;
		Statement stmt = null;

		boolean queued = false;

		try {

			// Get a connection to the database

			conn = getConnection();
			stmt = conn.createStatement();

			String sql = "SELECT FileId FROM " + getQueueTableName() + " WHERE TempFile = '" + tempFile + "'";

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Has queued req SQL: " + sql);

			// Check if there is a queued request using the temporary file

			ResultSet rs = stmt.executeQuery(sql);
			if ( rs.next())
				queued = true;
			else {

				// Check if there is a transaction using the temporary file

				sql = "SELECT FileId FROM " + getTransactionTableName() + " WHERE TempFile = '" + tempFile + "'";

				// DEBUG

				if ( Debug.EnableInfo && hasSQLDebug())
					Debug.println("[Derby] Has queued req SQL: " + sql);

				// Check the transaction table

				rs = stmt.executeQuery(sql);
				if ( rs.next())
					queued = true;
			}
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println(ex);

			// Rethrow the exception

			throw new DBException(ex.getMessage());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the queued status

		return queued;
	}

	/**
	 * Delete a file request from the pending queue.
	 * 
	 * @param fileReq FileRequest
	 * @exception DBException
	 */
	public void deleteFileRequest(FileRequest fileReq)
		throws DBException {

		Connection conn = null;
		Statement stmt = null;

		try {

			// Get a connection to the database

			conn = getConnection();
			stmt = conn.createStatement();

			// Delete the file request queue entry from the request table or multiple records from
			// the
			// transaction table

			if ( fileReq instanceof SingleFileRequest) {

				// Get the single file request details

				SingleFileRequest singleReq = (SingleFileRequest) fileReq;

				// Delete the request record

				stmt.executeUpdate("DELETE FROM " + getQueueTableName() + " WHERE SeqNo = " + singleReq.getSequenceNumber());
			}
			else {

				// Delete the transaction records

				stmt.executeUpdate("DELETE FROM " + getTransactionTableName() + " WHERE TranId = " + fileReq.getTransactionId());
			}
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println(ex);

			// Rethrow the exception

			throw new DBException(ex.getMessage());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}
	}

	/**
	 * Load a block of file request from the database into the specified queue.
	 * 
	 * @param fromSeqNo int
	 * @param reqType int
	 * @param reqQueue FileRequestQueue
	 * @param recLimit int
	 * @return int
	 * @exception DBException
	 */
	public int loadFileRequests(int fromSeqNo, int reqType, FileRequestQueue reqQueue, int recLimit)
		throws DBException {

		// Load a block of file requests from the loader queue

		Connection conn = null;
		Statement stmt = null;

		int recCnt = 0;

		try {

			// Get a connection to the database

			conn = getConnection();
			stmt = conn.createStatement();

			// Build the SQL to load the queue records

			String sql = "SELECT * FROM " + getQueueTableName() + " WHERE SeqNo > " + fromSeqNo + " AND ReqType = " + reqType
					+ " ORDER BY SeqNo";

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Load file requests - " + sql);

			// Get a block of file request records

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {

				// Get the file request details

				int fid = rs.getInt("FileId");
				int stid = rs.getInt("StreamId");
				int reqTyp = rs.getInt("ReqType");
				int seqNo = rs.getInt("SeqNo");
				String tempPath = rs.getString("TempFile");
				String virtPath = rs.getString("VirtualPath");

				// Recreate the file request for the in-memory queue

				SingleFileRequest fileReq = new SingleFileRequest(reqTyp, fid, stid, tempPath, virtPath, seqNo, null);

				// Add the request to the callers queue

				reqQueue.addRequest(fileReq);

				// Update the count of loaded requests

				recCnt++;
			}
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println(ex);

			// Rethrow the exception

			throw new DBException(ex.getMessage());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the count of file requests loaded

		return recCnt;
	}

	/**
	 * Load a transaction request from the queue.
	 * 
	 * @param tranReq MultiplFileRequest
	 * @return MultipleFileRequest
	 * @exception DBException
	 */
	public MultipleFileRequest loadTransactionRequest(MultipleFileRequest tranReq)
		throws DBException {

		// Load a transaction request from the transaction loader queue

		Connection conn = null;
		Statement stmt = null;

		try {

			// Get a connection to the database

			conn = getConnection();
			stmt = conn.createStatement();

			String sql = "SELECT * FROM " + getTransactionTableName() + " WHERE TranId = " + tranReq.getTransactionId();

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Load trans request - " + sql);

			// Get the block of file request records for the current transaction

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {

				// Get the file request details

				int fid = rs.getInt("FileId");
				int stid = rs.getInt("StreamId");
				String tempPath = rs.getString("TempFile");
				String virtPath = rs.getString("VirtualPath");

				// Create the cached file information and add to the request

				CachedFileInfo finfo = new CachedFileInfo(fid, stid, tempPath, virtPath);
				tranReq.addFileInfo(finfo);
			}
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println(ex);

			// Rethrow the exception

			throw new DBException(ex.getMessage());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the updated file request

		return tranReq;
	}

	/**
	 * Create the prepared statements used by the file request queueing database
	 * 
	 * @exception SQLException
	 */
	protected final void createQueueStatements()
		throws SQLException {

		// Check if the database connection is valid

		if ( m_dbConn != null) {

			// Close the existing statements

			if ( m_reqStmt != null)
				m_reqStmt.close();

			if ( m_tranStmt != null)
				m_tranStmt.close();

			// Release the current database connection

			releaseConnection(m_dbConn);
			m_dbConn = null;

		}

		if ( m_dbConn == null)
			m_dbConn = getConnection(DBConnectionPool.PermanentLease);

		// Create the prepared statements for accessing the file request queue database

		m_reqStmt = m_dbConn.prepareStatement("INSERT INTO " + getQueueTableName()
				+ "(FileId,StreamId,ReqType,TempFile,VirtualPath) VALUES (?,?,?,?,?)");

		// Create the prepared statements for accessing the transaction request queue database

		m_tranStmt = m_dbConn.prepareStatement("INSERT INTO " + getTransactionTableName()
				+ "(FileId,StreamId,ReqType,TranId,TempFile,VirtualPath) VALUES (?,?,?,?,?,?)");
	}

	// ***** DBDataInterface Methods *****

	/**
	 * Return the file data details for the specified file or stream.
	 * 
	 * @param fileId
	 * @param streamId
	 * @return DBDataDetails
	 * @throws DBException
	 */
	public DBDataDetails getFileDataDetails(int fileId, int streamId)
		throws DBException {

		// Load the file details from the data table

		Connection conn = null;
		Statement stmt = null;

		DBDataDetails dbDetails = null;

		try {

			// Get a connection to the database

			conn = getConnection();
			stmt = conn.createStatement();

			String sql = "SELECT * FROM " + getDataTableName() + " WHERE FileId = " + fileId + " AND StreamId = " + streamId
					+ " AND FragNo = 1";

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Get file data details SQL: " + sql);

			// Load the file details

			ResultSet rs = stmt.executeQuery(sql);

			if ( rs.next()) {

				// Create the file details

				dbDetails = new DBDataDetails(fileId, streamId);

				if ( rs.getBoolean("JarFile") == true)
					dbDetails.setJarId(rs.getInt("JarId"));
			}

			// DEBUG

			if ( Debug.EnableInfo && hasDebug())
				Debug.println("[Derby] Get file data details " + dbDetails);
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println(ex);

			// Rethrow the exception

			throw new DBException(ex.getMessage());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// If the file details are not valid throw an exception

		if ( dbDetails == null)
			throw new DBException("Failed to load file details for " + fileId + ":" + streamId);

		// Return the file data details

		return dbDetails;
	}

	/**
	 * Return the maximum data fragment size supported
	 * 
	 * @return long
	 */
	public long getMaximumFragmentSize() {
		return 20 * MemorySize.MEGABYTE;
	}

	/**
	 * Load file data from the database into a temporary/local file
	 * 
	 * @param fileId int
	 * @param streamId int
	 * @param fileSeg FileSegment
	 * @throws DBException
	 * @throws IOException
	 */
	public void loadFileData(int fileId, int streamId, FileSegment fileSeg)
		throws DBException, IOException {

		// Open the temporary file

		FileOutputStream fileOut = new FileOutputStream(fileSeg.getTemporaryFile());

		// Update the segment status

		fileSeg.setStatus(FileSegmentInfo.Loading);

		// DEBUG

		long startTime = 0L;

		if ( Debug.EnableInfo && hasDebug())
			startTime = System.currentTimeMillis();

		// Load the file data fragments

		Connection conn = null;
		Statement stmt = null;

		try {

			// Get a connection to the database, create a statement

			conn = getConnection();
			stmt = conn.createStatement();

			String sql = "SELECT * FROM " + getDataTableName() + " WHERE FileId = " + fileId + " AND StreamId = " + streamId
					+ " ORDER BY FragNo";

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Load file data SQL: " + sql);

			// Find the data fragments for the file, check if the file is stored in a Jar

			ResultSet rs = stmt.executeQuery(sql);

			// Load the file data from the main file record(s)

			byte[] inbuf = null;
			int fragNo = -1;
			int fragSize = -1;

			long totLen = 0L;

			while (rs.next()) {

				// Access the file data

				fragNo = rs.getInt("FragNo");
				fragSize = rs.getInt("FragLen");

				// We must access the BLOB input stream directly, do not get the Blob and call
				// getBinaryStream() as it
				// does not work in Cloudscape/Derby

				InputStream dataFrag = rs.getBinaryStream("Data");

				// Allocate the read buffer, if not already allocated

				if ( inbuf == null)
					inbuf = new byte[BlobReadBuffer];

				// Read the data from the database record and write to the output file

				int rdLen = dataFrag.read(inbuf);

				while (rdLen > 0) {

					// Write a block of data to the temporary file segment

					fileOut.write(inbuf, 0, rdLen);
					totLen += rdLen;

					// Read another block of data

					rdLen = dataFrag.read(inbuf);
				}

				// Signal to waiting threads that data is available

				fileSeg.setReadableLength(totLen);
				fileSeg.signalDataAvailable();

				// Renew the lease on the database connection

				getConnectionPool().renewLease(conn);
			}

			// Close the resultset

			rs.close();

			// DEBUG

			if ( Debug.EnableInfo && hasDebug()) {
				long endTime = System.currentTimeMillis();
				Debug.println("[Derby] Loaded fid=" + fileId + ", stream=" + streamId + ", frags=" + fragNo + ", time="
						+ (endTime - startTime) + "ms");
			}
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println(ex);

			// Rethrow the exception

			throw new DBException(ex.getMessage());
		}
		finally {

			// Check if a statement was allocated

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);

			// Close the output file

			if ( fileOut != null) {
				try {
					fileOut.close();
				}
				catch (Exception ex) {
					Debug.println(ex);
				}
			}
		}

		// Signal that the file data is available

		fileSeg.signalDataAvailable();
	}

	/**
	 * Load Jar file data from the database into a temporary file
	 * 
	 * @param jarId int
	 * @param jarSeg FileSegment
	 * @throws DBException
	 * @throws IOException
	 */
	public void loadJarData(int jarId, FileSegment jarSeg)
		throws DBException, IOException {

		// Load the Jar file data

		Connection conn = null;
		Statement stmt = null;

		FileOutputStream outJar = null;

		try {

			// Get a connection to the database, create a statement

			conn = getConnection();
			stmt = conn.createStatement();

			String sql = "SELECT * FROM " + getJarDataTableName() + " WHERE JarId = " + jarId;

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Load Jar data SQL: " + sql);

			// Create the temporary Jar file

			outJar = new FileOutputStream(jarSeg.getTemporaryFile());

			// Get the Jar data record

			ResultSet rs = stmt.executeQuery(sql);

			if ( rs.next()) {

				// Access the Jar file data

				InputStream dataFrag = rs.getBinaryStream("Data");

				// Allocate the read buffer

				byte[] inbuf = new byte[BlobReadBuffer];

				// Read the Jar data from the database record and write to the output file

				int rdLen = dataFrag.read(inbuf);
				long totLen = 0L;

				while (rdLen > 0) {

					// Write a block of data to the temporary file segment

					outJar.write(inbuf, 0, rdLen);
					totLen += rdLen;

					// Read another block of data

					rdLen = dataFrag.read(inbuf);
				}
			}

			// Close the output Jar file

			outJar.close();

			// Set the Jar file segment status to indicate that the data has been loaded

			jarSeg.setStatus(FileSegmentInfo.Available, false);
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println(ex);

			// Rethrow the exception

			throw new DBException(ex.getMessage());
		}
		finally {

			// Check if a statement was allocated

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);

			// Close the output file

			if ( outJar != null) {
				try {
					outJar.close();
				}
				catch (Exception ex) {
					Debug.println(ex);
				}
			}
		}
	}

	/**
	 * Save the file data from the temporary/local file to the database
	 * 
	 * @param fileId int
	 * @param streamId int
	 * @param fileSeg FileSegment
	 * @return int
	 * @throws DBException
	 * @throws IOException
	 */
	public int saveFileData(int fileId, int streamId, FileSegment fileSeg)
		throws DBException, IOException {

		// Determine if we can use an in memory buffer to copy the file fragments

		boolean useMem = false;
		byte[] memBuf = null;

		if ( getDataFragmentSize() <= MaxMemoryBuffer) {

			// Use a memory buffer to copy the file data fragments

			useMem = true;
			memBuf = new byte[(int) getDataFragmentSize()];
		}

		// Get the temporary file size

		File tempFile = new File(fileSeg.getTemporaryFile());

		// Save the file data

		Connection conn = null;
		Statement delStmt = null;
		PreparedStatement stmt = null;
		int fragNo = 1;

		FileInputStream inFile = null;

		try {

			// Open the temporary file

			inFile = new FileInputStream(tempFile);

			// Get a connection to the database

			conn = getConnection();
			delStmt = conn.createStatement();

			String sql = "DELETE FROM " + getDataTableName() + " WHERE FileId = " + fileId + " AND StreamId = " + streamId;

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Save file data SQL: " + sql);

			// Delete any existing file data records for this file

			int recCnt = delStmt.executeUpdate(sql);
			delStmt.close();
			delStmt = null;

			// Turn off auto-commit whilst we save the data

			conn.setAutoCommit(false);

			// Add the file data to the database

			stmt = conn.prepareStatement("INSERT INTO " + getDataTableName()
					+ " (FileId,StreamId,FragNo,FragLen,Data,JarFile,JarId) VALUES (?,?,?,?,?,'0',0)");

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Save file data SQL: " + stmt.toString());

			long saveSize = tempFile.length();

			while (saveSize > 0) {

				// Determine the current fragment size to store

				long fragSize = Math.min(saveSize, getDataFragmentSize());

				// Determine if the data fragment should be copied to a memory buffer or a seperate
				// temporary file

				InputStream fragStream = null;

				if ( saveSize == fragSize) {

					// Just copy the data from the temporary file, only one fragment

					fragStream = inFile;
				}
				else if ( useMem == true) {

					// Copy a block of data to the memory buffer

					fragSize = inFile.read(memBuf);
					fragStream = new ByteArrayInputStream(memBuf);
				}
				else {

					// Need to create a temporary file and copy the fragment of data to it

					throw new DBException("File data copy not implemented yet");
				}

				// Store the current fragment

				stmt.clearParameters();
				stmt.setInt(1, fileId);
				stmt.setInt(2, streamId);
				stmt.setInt(3, fragNo++);
				stmt.setInt(4, (int) fragSize);
				stmt.setBinaryStream(5, fragStream, (int) fragSize);

				if ( stmt.executeUpdate() < 1 && hasDebug())
					Debug.println("## Derby Failed to update file data, fid=" + fileId + ", stream=" + streamId + ", fragNo="
							+ (fragNo - 1));

				conn.commit();

				// Update the remaining data size to be saved

				saveSize -= fragSize;

				// Renew the lease on the database connection so that it does not expire

				getConnectionPool().renewLease(conn);
			}
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println(ex);

			// Rethrow the exception

			throw new DBException(ex.getMessage());
		}
		finally {

			// Close the delete statement

			if ( delStmt != null) {
				try {
					delStmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Close the insert statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Turn auto-commit back on

			try {
				conn.setAutoCommit(true);
			}
			catch (Exception ex) {
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);

			// Close the input file

			if ( inFile != null) {
				try {
					inFile.close();
				}
				catch (Exception ex) {
				}
			}
		}

		// Return the number of data fragments used to save the file data

		return fragNo;
	}

	/**
	 * Save the file data from a Jar file to the database
	 * 
	 * @param jarPath String
	 * @param fileList DBDataDetailsList
	 * @return int
	 * @throws DBException
	 * @throws IOException
	 */
	public int saveJarData(String jarPath, DBDataDetailsList fileList)
		throws DBException, IOException {

		// Write the Jar file to the blob field in the Jar data table

		Connection conn = null;
		PreparedStatement istmt = null;
		Statement stmt = null;

		int jarId = -1;

		try {

			// Get a connection to the database

			conn = getConnection();

			// Open the Jar file

			File jarFile = new File(jarPath);
			FileInputStream inJar = new FileInputStream(jarFile);

			// Add the Jar file data to the database

			istmt = conn.prepareStatement("INSERT INTO " + getJarDataTableName() + " (Data) VALUES (?)");

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Save Jar data SQL: " + istmt.toString());

			// Set the Jar data field

			istmt.setBinaryStream(1, inJar, (int) jarFile.length());

			if ( istmt.executeUpdate() < 1 && hasDebug())
				Debug.println("## Derby Failed to store Jar data");

			// Get the unique jar id allocated to the new Jar record

			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("VALUES IDENTITY_VAL_LOCAL()");

			if ( rs.next())
				jarId = rs.getInt(1);

			// Update the jar id record for each file in the Jar

			for (int i = 0; i < fileList.numberOfFiles(); i++) {

				// Get the current file details

				DBDataDetails dbDetails = fileList.getFileAt(i);

				// Add the file data record(s) to the database

				stmt.executeUpdate("DELETE FROM " + getDataTableName() + " WHERE FileId = " + dbDetails.getFileId()
						+ " AND StreamId = " + dbDetails.getStreamId());
				stmt.executeUpdate("INSERT INTO " + getDataTableName() + " (FileId,StreamId,FragNo,JarId,JarFile) VALUES ("
						+ dbDetails.getFileId() + "," + dbDetails.getStreamId() + ", 1," + jarId + ",'1')");
			}
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println(ex);

			// Rethrow the exception

			throw new DBException(ex.getMessage());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException ex) {
				}
			}

			// Close the insert statement

			if ( istmt != null) {
				try {
					istmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the allocated Jar id

		return jarId;
	}

	/**
	 * Delete the file data for the specified file/stream
	 * 
	 * @param fileId int
	 * @param streamId int
	 * @throws DBException
	 * @throws IOException
	 */
	public void deleteFileData(int fileId, int streamId)
		throws DBException, IOException {

		// Delete the file data records for the file or stream

		Connection conn = null;
		Statement delStmt = null;

		try {

			// Get a connection to the database

			conn = getConnection();

			// Need to delete the existing data

			delStmt = conn.createStatement();
			String sql = null;

			// Check if the main file stream is being deleted, if so then delete all stream data too

			if ( streamId == 0)
				sql = "DELETE FROM " + getDataTableName() + " WHERE FileId = " + fileId;
			else
				sql = "DELETE FROM " + getDataTableName() + " WHERE FileId = " + fileId + " AND StreamId = " + streamId;

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Delete file data SQL: " + sql);

			// Delete the file data records

			int recCnt = delStmt.executeUpdate(sql);

			// Debug

			if ( Debug.EnableInfo && hasDebug() && recCnt > 0)
				Debug.println("[Derby] Deleted file data fid=" + fileId + ", stream=" + streamId + ", records=" + recCnt);
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableInfo && hasDebug())
				Debug.println(ex);
		}
		finally {

			// Close the delete statement

			if ( delStmt != null) {
				try {
					delStmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}
	}

	/**
	 * Delete the file data for the specified Jar file
	 * 
	 * @param jarId int
	 * @throws DBException
	 * @throws IOException
	 */
	public void deleteJarData(int jarId)
		throws DBException, IOException {

		// Delete the data records for the Jar file data

		Connection conn = null;
		Statement delStmt = null;

		try {

			// Get a connection to the database

			conn = getConnection();

			// Need to delete the existing data

			delStmt = conn.createStatement();
			String sql = "DELETE FROM " + getJarDataTableName() + " WHERE JarId = " + jarId;

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Delete Jar data SQL: " + sql);

			// Delete the Jar data records

			int recCnt = delStmt.executeUpdate(sql);

			// Debug

			if ( Debug.EnableInfo && hasDebug() && recCnt > 0)
				Debug.println("[Derby] Deleted Jar data jarId=" + jarId + ", records=" + recCnt);
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableInfo && hasDebug())
				Debug.println(ex);
		}
		finally {

			// Close the delete statement

			if ( delStmt != null) {
				try {
					delStmt.close();
				}
				catch (Exception ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}
	}

	// ***** DBObjectIdInterface Methods *****

	/**
	 * Create a file id to object id mapping
	 * 
	 * @param fileId int
	 * @param streamId int
	 * @param objectId String
	 * @exception DBException
	 */
	public void saveObjectId(int fileId, int streamId, String objectId)
		throws DBException {

		// Create a new file id/object id mapping record

		Connection conn = null;
		Statement stmt = null;

		try {

			// Get a connection to the database

			conn = getConnection();
			stmt = conn.createStatement();

			// Delete any current mapping record for the object

			String sql = "DELETE FROM " + getObjectIdTableName() + " WHERE FileId = " + fileId + " AND StreamId = " + streamId;

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Save object id SQL: " + sql);

			// Delete any current mapping record

			stmt.executeUpdate(sql);

			// Insert the new mapping record

			sql = "INSERT INTO " + getObjectIdTableName() + " (FileId,StreamId,ObjectID) VALUES(" + fileId + "," + streamId
					+ ",'" + objectId + "')";

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Save object id SQL: " + sql);

			// Create the mapping record

			if ( stmt.executeUpdate(sql) == 0)
				throw new DBException("Failed to add object id record, fid=" + fileId + ", objId=" + objectId);
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println(ex);

			// Rethrow the exception

			throw new DBException(ex.getMessage());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}
	}

	/**
	 * Load the object id for the specified file id
	 * 
	 * @param fileId int
	 * @param streamId int
	 * @return String
	 * @exception DBException
	 */
	public String loadObjectId(int fileId, int streamId)
		throws DBException {

		// Load the object id for the specified file id

		Connection conn = null;
		Statement stmt = null;

		String objectId = null;

		try {

			// Get a connection to the database

			conn = getConnection();
			stmt = conn.createStatement();

			String sql = "SELECT ObjectId FROM " + getObjectIdTableName() + " WHERE FileId = " + fileId + " AND StreamId = "
					+ streamId;

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Load object id SQL: " + sql);

			// Load the mapping record

			ResultSet rs = stmt.executeQuery(sql);

			if ( rs.next())
				objectId = rs.getString("ObjectId");
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println(ex);

			// Rethrow the exception

			throw new DBException(ex.getMessage());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}

		// Return the object id

		return objectId;
	}

	/**
	 * Delete a file id/object id mapping
	 * 
	 * @param fileId int
	 * @param streamId int
	 * @param objectId String
	 * @exception DBException
	 */
	public void deleteObjectId(int fileId, int streamId, String objectId)
		throws DBException {

		// Delete a file id/object id mapping record

		Connection conn = null;
		Statement stmt = null;

		try {

			// Get a connection to the database

			conn = getConnection();
			stmt = conn.createStatement();

			String sql = "DELETE FROM " + getObjectIdTableName() + " WHERE FileId = " + fileId + " AND StreamId = " + streamId;

			// DEBUG

			if ( Debug.EnableInfo && hasSQLDebug())
				Debug.println("[Derby] Delete object id SQL: " + sql);

			// Delete the mapping record

			stmt.executeUpdate(sql);
		}
		catch (SQLException ex) {

			// DEBUG

			if ( Debug.EnableError && hasDebug())
				Debug.println(ex);

			// Rethrow the exception

			throw new DBException(ex.getMessage());
		}
		finally {

			// Close the statement

			if ( stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException ex) {
				}
			}

			// Release the database connection

			if ( conn != null)
				releaseConnection(conn);
		}
	}

	/**
	 * Return the data for a symbolic link
	 * 
	 * @param dirId int
	 * @param fid int
	 * @return String
	 * @exception DBException
	 */
	public String readSymbolicLink(int dirId, int fid)
		throws DBException {

		throw new DBException("Symbolic links not supported");
	}

	/**
	 * Delete a symbolic link record
	 * 
	 * @param dirId int
	 * @param fid int
	 * @exception DBException
	 */
	public void deleteSymbolicLinkRecord(int dirId, int fid)
		throws DBException {

		throw new DBException("Symbolic links not supported");
	}
}
