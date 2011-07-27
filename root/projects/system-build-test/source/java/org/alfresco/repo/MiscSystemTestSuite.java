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

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.repo.cmis.rest.test.CmisRelationshipSystemTest;
import org.alfresco.repo.web.scripts.activities.SiteActivitySystemTest;
import org.alfresco.webservice.test.AccessControlServiceSystemTest;
import org.alfresco.webservice.test.ActionServiceSystemTest;
import org.alfresco.webservice.test.AdministrationServiceSystemTest;
import org.alfresco.webservice.test.AuthenticationServiceSystemTest;
import org.alfresco.webservice.test.AuthoringServiceSystemTest;
import org.alfresco.webservice.test.ClassificationServiceSystemTest;
import org.alfresco.webservice.test.ContentServiceSystemTest;
import org.alfresco.webservice.test.DictionaryServiceSystemTest;
import org.alfresco.webservice.test.RepositoryServiceSystemTest;
import org.alfresco.webservice.test.TimeoutSystemTest;

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
        
        // web services
        suite.addTestSuite(AuthenticationServiceSystemTest.class);
        suite.addTestSuite(AuthoringServiceSystemTest.class);
        suite.addTestSuite(ClassificationServiceSystemTest.class);
        suite.addTestSuite(ContentServiceSystemTest.class);
        suite.addTestSuite(RepositoryServiceSystemTest.class);
        suite.addTestSuite(ActionServiceSystemTest.class);
        suite.addTestSuite(AdministrationServiceSystemTest.class);
        suite.addTestSuite(AccessControlServiceSystemTest.class);
        suite.addTestSuite(DictionaryServiceSystemTest.class);
        suite.addTestSuite(TimeoutSystemTest.class);
        
        // cmis
        suite.addTest(new JUnit4TestAdapter(CmisRelationshipSystemTest.class));
        
        // stop (embedded) Jetty
        suite.addTestSuite(RepoJettyStopTest.class);
        
        return suite;
    }
}
