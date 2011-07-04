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
 * Boolean Test Result Class
 * 
 * <p>Test result implementation for simple true/false status returns.
 *
 * @author gkspencer
 */
public class BooleanTestResult extends TestResult {

	// Test result
	
	private boolean m_result;
	
	/**
	 * Class contstructor
	 * 
	 * @param result boolean
	 */
	public BooleanTestResult( boolean result) {
		m_result = result;
	}
	
	/**
	 * Class contstructor
	 * 
	 * @param result boolean
	 * @param comment String
	 */
	public BooleanTestResult( boolean result, String comment) {
		super ( comment);
		
		m_result = result;
	}
	
	/**
	 * Return the test result
	 * 
	 * @return Object
	 */
	public Object getResult() {
		return new Boolean( m_result);
	}

	/**
	 * Determine if the result indicates a success
	 * 
	 * @return boolean
	 */
	public boolean isSuccess() {
		return m_result;
	}

	/**
	 * Determine if the result indicates a failure
	 * 
	 * @return boolean
	 */
	public boolean isFailure() {
		return m_result == false ? true : false;
	}

	/**
	 * Determine if the result indicates a warning
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
		
		str.append( "[Boolean id=");
		str.append( getRunId());
		str.append( ",result=");
		str.append( m_result);
		
		if ( hasComment()) {
			str.append( ",comment=");
			str.append( getComment());
		}
		str.append( "]");
		
		return str.toString();
	}
}
