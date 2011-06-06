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

package org.alfresco.repo.web.scripts.activities;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.repo.RepoJettyStartTest;
import org.alfresco.repo.RepoJettyStopTest;

/**
 * Site Activities system test suite (runs with embedded jetty)
 * 
 * @author janv
 */
public class SiteActivitySystemTestSuite extends TestSuite
{
    /**
     * Creates the test suite
     * 
     * @return  the test suite
     */
    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();
        
        RepoJettyStartTest.startJetty(); 
        
        // the following test rely on running repo
        suite.addTestSuite(SiteActivitySystemTest.class);
        
        suite.addTestSuite(RepoJettyStopTest.class);
        
        return suite;
    }
}
