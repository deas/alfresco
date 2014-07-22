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
package org.alfresco.repo;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.repo.web.scripts.activities.SiteActivitySystemTest;
import org.alfresco.solr.client.SOLRAPIClientTest;

/**
 * Run suite of miscellaneous system tests (against embedded jetty)
 * 
 * @author janv
 */
public class MiscSystemTestSuite extends TestSuite
{
    /**
     * Creates the test suite
     * 
     * @return  the test suite
     */
    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();
        
        // start (embedded) Jetty
        suite.addTestSuite(RepoJettyStartTest.class);
        
        // the following tests rely on running repo
        
        // site activities
        suite.addTestSuite(SiteActivitySystemTest.class);
        
        // SOLR
        suite.addTestSuite(SOLRAPIClientTest.class);
        
        // stop (embedded) Jetty
        suite.addTestSuite(RepoJettyStopTest.class);
        
        return suite;
    }
}
