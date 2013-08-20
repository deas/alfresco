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

package org.alfresco.jlan.sample;

import org.alfresco.jlan.debug.Debug;
import org.alfresco.jlan.server.filesys.DiskDeviceContext;
import org.alfresco.jlan.server.filesys.cache.FileState;
import org.alfresco.jlan.server.filesys.loader.FileProcessor;
import org.alfresco.jlan.server.filesys.loader.FileSegment;


/**
 * Test File Processor Class
 *
 * @author gkspencer
 */
public class TestFileProcessor implements FileProcessor {

	/**
	 * Process a cached file just before it is to be stored.
	 * 
	 * @param context
	 * @param state
	 * @param segment
	 */
	public void processStoredFile(DiskDeviceContext context, FileState state, FileSegment segment) {
		try {
			Debug.println("## TestFileProcessor Storing file=" + state.getPath() + ", fid=" + state.getFileId() + ", cache=" + segment.getTemporaryFile());
		}
		catch (Exception ex) {
		}
	}

	/**
	 * Process a cached file just after being loaded.
	 *
	 * @param context
	 * @param state
	 * @param segment
	 */
	public void processLoadedFile(DiskDeviceContext context, FileState state, FileSegment segment) {
		try {
			Debug.println("## TestFileProcessor Loaded file=" + state.getPath() + ", fid=" + state.getFileId() + ", cache=" + segment.getTemporaryFile());
		}
		catch (Exception ex) {
		}
	}

}
