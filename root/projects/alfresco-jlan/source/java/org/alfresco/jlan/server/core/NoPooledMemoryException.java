/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.jlan.server.core;

import java.io.IOException;

/**
 * No Pooled Memory Exception Class
 * 
 * <p>Indicates that no buffers are available in the global memory pool or per protocol pool.
 * 
 * @author gkspencer
 */
public class NoPooledMemoryException extends IOException {

	private static final long serialVersionUID = 6852939454477894406L;

	/**
	   * Default constructor
	   */
	  public NoPooledMemoryException() {
	    super();
	  }

	  /**
	   * Class constructor
	   *
	   * @param s String
	   */
	  public NoPooledMemoryException(String s) {
	    super(s);
	  }
}
