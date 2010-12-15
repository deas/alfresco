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
 * File Sharing Mode Class
 * 
 * <p>Defines sharing mode constants used when opening a file via the CIFSDiskSession.NTCreate() method.
 *
 * @author gkspencer
 */
public class SharingMode {

	//	File sharing mode constants
	
	public final static int NOSHARING = 0x0000;
	public final static int READ	  = 0x0001;
	public final static int WRITE	  = 0x0002;
	public final static int DELETE	  = 0x0004;
	
	public final static int READWRITE = READ + WRITE;
}
