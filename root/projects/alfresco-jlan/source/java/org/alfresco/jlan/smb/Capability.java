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

package org.alfresco.jlan.smb;

/**
 * SMB Capabilities Class
 * 
 * <p>Contains the capability flags for the client/server during a session setup.
 *
 * @author gkspencer
 */
public class Capability {

	//	Capabilities
	
	public static final int RawMode						= 0x00000001;
	public static final int MpxMode						= 0x00000002;
	public static final int Unicode						= 0x00000004;
	public static final int LargeFiles				= 0x00000008;
	public static final int NTSMBs						= 0x00000010;
	public static final int RemoteAPIs				= 0x00000020;
	public static final int NTStatus					= 0x00000040;
	public static final int Level2Oplocks			= 0x00000080;
	public static final int LockAndRead				= 0x00000100;
	public static final int NTFind						= 0x00000200;
	public static final int DFS								= 0x00001000;
	public static final int InfoPassthru			= 0x00002000;
	public static final int LargeRead 				= 0x00004000;
	public static final int LargeWrite				= 0x00008000;
	public static final int UnixExtensions		= 0x00800000;
	public static final int BulkTransfer			= 0x20000000;
	public static final int CompressedData		=	0x40000000;
	public static final int ExtendedSecurity	=	0x80000000;
}
