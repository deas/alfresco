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

package org.alfresco.jlan.test.cluster;

/**
 * Exception Test Result Class
 * 
 * <p>Test result implementation for tests that complete with an exception. An exception does not
 * necessarily mean a failure as some exceptions may be expected depending on the test.
 *
 * @author gkspencer
 */
public class ExceptionTestResult extends TestResult {

	// Test result
	
	private Exception m_exception;
	
	/**
	 * Class constructor
	 * 
	 * @param ex Exception
	 */
	public ExceptionTestResult( Exception ex) {
		m_exception = ex;
	}
	
	/**
	 * Class constructor
	 * 
	 * @param ex Exception
	 * @param comment String
	 */
	public ExceptionTestResult( Exception ex, String comment) {
		super( comment);
		
		m_exception = ex;
	}
	
	/**
	 * Return the test result
	 * 
	 * @return Object
	 */
	public Object getResult() {
		return m_exception;
	}

	/**
	 * Default to indicate that an exception is not a success, override to filter
	 * particular exception types
	 * 
	 * @return boolean
	 */
	public boolean isSuccess() {
		return false;
	}

	/**
	 * Default to indicate that an exception is a failure, override to filter
	 * particular exception types
	 * 
	 * @return boolean
	 */
	public boolean isFailure() {
		return true;
	}

	/**
	 * Default to indicate that an exception is not a warning, override to filter
	 * particular exception types
	 * 
	 * @return boolean
	 */
	public boolean isWarning() {
		return false;
	}
	
	/**
	 * Return the test result as a string
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append( "[Exception id=");
		str.append( getRunId());
		str.append( ",result=");
		str.append( m_exception.getMessage());
		
		if ( hasComment()) {
			str.append( ",comment=");
			str.append( getComment());
		}
		str.append( "]");
		
		return str.toString();
	}
}
