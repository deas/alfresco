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
 * Test Result Interface
 *
 * <p>Holds the result from the run of a test.
 * 
 * @author gkspencer
 */
public abstract class TestResult {

	// Test thread id
	
	private String m_id;
	
	// Additional result comment
	
	private String m_comment;
	
	/**
	 * Default constructor
	 */
	public TestResult() {
		m_id = Thread.currentThread().getName();
	}
	
	/**
	 * Class constructor
	 * 
	 * @param comment String
	 */
	public TestResult( String comment) {
		m_id = Thread.currentThread().getName();
		m_comment = comment;
	}
	
	/**
	 * Class constructor
	 * 
	 * @param id String
	 * @param comment String
	 */
	public TestResult( String id, String comment) {
		m_id = id;
		m_comment = comment;
	}
	
	/**
	 * Return the test run/thread id
	 * 
	 * @return String
	 */
	public final String getRunId() {
		return m_id;
	}

	/**
	 * Check if there is additional result/comment information available
	 * 
	 * @return boolean
	 */
	public final boolean hasComment() {
		return m_comment != null ? true : false;
	}
	
	/**
	 * Return the additional info/comment
	 * 
	 * @return String
	 */
	public final String getComment() {
		return m_comment;
	}
	
	/**
	 * Set the result comment
	 * 
	 * @param comment String
	 */
	public final void setComment( String comment) {
		m_comment = comment;
	}
	
	/**
	 * Return the test result
	 * 
	 * @return Object
	 */
	public abstract Object getResult();
	
	/**
	 * Determine if the result indicates a success
	 * 
	 * @return boolean
	 */
	public boolean isSuccess() {
		return false;
	}
	
	/**
	 * Determine if the result indicates a failure
	 * 
	 * @return boolean
	 */
	public boolean isFailure() {
		return true;
	}
	
	/**
	 * Determine if the result indicates a warning
	 * 
	 * @return boolean
	 */
	public boolean isWarning() {
		return false;
	}
}
