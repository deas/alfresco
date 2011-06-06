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
package org.alfresco.webservice.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Web service system tests suite
 * 
 * @author Roy Wetherall
 */
public class WebServiceSuiteSystemTest extends TestSuite
{
    public static Test suite() 
    {
        TestSuite suite = new TestSuite();
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
        return suite;
    }
}
