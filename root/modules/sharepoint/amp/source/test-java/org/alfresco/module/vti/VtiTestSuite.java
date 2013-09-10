/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.module.vti;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite to run all the run all the tests in the Sharepoint module.
 * 
 * @author Matt Ward
 */
@RunWith(Suite.class)
@SuiteClasses({
    org.alfresco.module.vti.handler.alfresco.HandlerTestSuite.class,
    org.alfresco.module.vti.web.ws.VtiWebWSPackageTestSuite.class,
    org.alfresco.module.vti.web.VtiRequestDispatcherTest.class
})
public class VtiTestSuite
{
    // See @SuiteClasses
}
