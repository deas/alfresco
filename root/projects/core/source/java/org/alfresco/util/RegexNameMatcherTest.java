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
package org.alfresco.util;

import java.util.ArrayList;

import junit.framework.TestCase;

public class RegexNameMatcherTest extends TestCase {

	public RegexNameMatcherTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRegexNameMatcher() {
		
		RegexNameMatcher matcher = new RegexNameMatcher();
		
		ArrayList<String>patterns = new ArrayList<String>();
		patterns.add("[A-Za-z0-9:/_]*ROOT/myapp/dir1/*");
		matcher.setPatterns(patterns);
		
		boolean ret = matcher.matches("ROOT/myapp/dir1");
		assertTrue(ret);
		
		boolean ret2 = matcher.matches("xxROOT/myapp/dir1");
		assertTrue(ret2);
		
		boolean ret3 = matcher.matches("test20text:/www/avm_webapps/ROOT/myapp/dir1");
		assertTrue(ret3);
		
		boolean ret4 = matcher.matches("test20text:/www/avm_webapps/ROOT/myapp/dir11");
		assertTrue(!ret4);
		
		boolean ret5 = matcher.matches("test20text:/www/avm_webapps/ROOT/myapp/dir666");
		assertTrue(!ret5);
		
		boolean ret6 = matcher.matches("test20text:/www/avm_webapps/ROOT/zzz/dir1");
		assertTrue(!ret6);
			
	}
	
	public void testRegexGif() {
		RegexNameMatcher matcher = new RegexNameMatcher();
		
		ArrayList<String>patterns = new ArrayList<String>();
		patterns.add(".*.jpg");
		matcher.setPatterns(patterns);
		
		boolean ret = matcher.matches("ROOT/myapp/file.jpg");
		assertTrue(ret);
		
		boolean ret2 = matcher.matches("xxROOT/myapp/dir1.gif");
		assertFalse(ret2);
				
	}
	public void testRegexGifAndJpg() {
		RegexNameMatcher matcher = new RegexNameMatcher();
		
		ArrayList<String>patterns = new ArrayList<String>();
		patterns.add(".*.jpg$|.*.gif$");
		matcher.setPatterns(patterns);
		
		boolean ret = matcher.matches("ROOT/myapp/file.jpg");
		assertTrue(ret);
		
		boolean ret2 = matcher.matches("xxROOT/myapp/dir1.gif");
		assertTrue(ret2);
		
		boolean ret3 = matcher.matches("xxROOT/myapp/dir7.png");
		assertFalse(ret3);
		
		boolean ret4 = matcher.matches("xxROOT/myapp/dir7.png.old");
		assertFalse(ret3);
				
	}

}
