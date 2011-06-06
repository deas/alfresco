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

package org.alfresco.repo.bm;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.repo.RepoJettyStartTest;
import org.alfresco.repo.RepoJettyStopTest;
import org.alfresco.repo.bm.cmis.RepoBenchmarkCMISSystemTest;
import org.alfresco.repo.bm.webdav.RepoBenchmarkWebDAVSystemTest;

/**
 * RepoBM system test suite (runs with embedded jetty)
 * 
 * @author janv
 */
public class RepoBenchmarkSystemTestSuite extends TestSuite
{
    /**
     * Creates the test suite
     * 
     * @return  the test suite
     */
    public static Test suite() throws Exception
    {
        // statically started here (since OpenCMIS sessions are started in constructor)
        RepoJettyStartTest.startJetty();
        
        TestSuite suite = new TestSuite();
        
		// note: currently assumes test data has been imported (via ImportDataTest.java => "ant test-repository-bm")
		
        // the following test rely on running repo
        suite.addTestSuite(RepoBenchmarkWebDAVSystemTest.class);
        suite.addTestSuite(RepoBenchmarkCMISSystemTest.class);
        
        suite.addTestSuite(RepoJettyStopTest.class);
        
        return suite;
    }
}
