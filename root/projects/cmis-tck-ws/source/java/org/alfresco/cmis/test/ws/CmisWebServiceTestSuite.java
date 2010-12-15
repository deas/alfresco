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
package org.alfresco.cmis.test.ws;

import java.io.PrintStream;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.ResultPrinter;
import junit.textui.TestRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CmisWebServiceTestSuite extends TestSuite
{
    private static Log LOGGER = LogFactory.getLog(CmisWebServiceTestSuite.class);

    private static int executed = 0;
    private static int failed = 0;
    private static long time = 0;
    private static boolean flag = true;

    private String[] testableServiceNames = new String[0];

    public void setTestableServiceNames(String[] testableServiceNames)
    {
        this.testableServiceNames = testableServiceNames;
    }

    public static void main(String[] args)
    {
        try
        {
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext("cmis-context.xml");
            CmisWebServiceTestSuite testsExecutor = (CmisWebServiceTestSuite) applicationContext.getBean("cmisTestsExecutor");
            if (null != testsExecutor)
            {
                testsExecutor.execute();
            }
            else
            {
                LOGGER.error("CMIS Tests Executor class was not found! Testing terminated...");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Can't run Tests. Cause error message: " + e.toString());
        }
    }

    public void execute()
    {
        printLicenseInfo();
        if ((null != testableServiceNames) && (testableServiceNames.length > 0))
        {
            TestRunner testRunner = new TestRunner();
            ResultPrinter printer = new CMISResultPrinter(System.out);
            testRunner.setPrinter(printer);
            for (String serviceName : testableServiceNames)
            {
                Class<?> testableClass = null;
                serviceName = serviceName.trim();
                try
                {
                    testableClass = Class.forName("org.alfresco.cmis.test.ws.Cmis" + serviceName + "Client");
                }
                catch (ClassNotFoundException e)
                {
                    // Doing nothing
                }
                if (null == testableClass)
                {
                    LOGGER.error("Test for '" + serviceName + "' was not found!");
                }
                else
                {
                    LOGGER.info("Testing: [" + serviceName + "]");
                    testRunner.doRun(new TestSuite(testableClass));
                }
            }
            printStatusInfo();
        }
        else
        {
            LOGGER.info("No one Testable Service was configured! Nothing to test");
        }
    }

    private void printLicenseInfo()
    {
        LOGGER.info("******************************************************************");
        LOGGER.info("*                                                                *");
        LOGGER.info("*                          CMIS TEST                             *");
        LOGGER.info("*       Copyright (C) 2005-2010 Alfresco Software Limited.       *");
        LOGGER.info("*                                                                *");
        LOGGER.info("******************************************************************");
        LOGGER.info("");
        LOGGER.info("Starting CMIS Services' Tests");
        LOGGER.info("");
    }

    private void printStatusInfo()
    {
        LOGGER.info("");
        LOGGER.info("Finished");
        LOGGER.info("Totally spent time: " + time + " ms");
        LOGGER.info("------------------------------------------------------");
        LOGGER.info("Totally passed: " + (executed - failed));
        LOGGER.info("Totally failed: " + failed);
        LOGGER.info("Totally executed: " + executed);
        LOGGER.info("------------------------------------------------------");
    }

    private static class CMISResultPrinter extends ResultPrinter
    {
        public CMISResultPrinter(PrintStream writer)
        {
            super(writer);
        }

        @Override
        public void addError(Test test, Throwable t)
        {
            LOGGER.info("----------------------------");
            LOGGER.info("   !!! Test failed !!!");
            LOGGER.info("Message: " + t.toString());
            flag = false;
        }

        @Override
        public void addFailure(Test test, AssertionFailedError t)
        {
            LOGGER.info("----------------------------");
            LOGGER.info("   !!! Test failed !!!");
            LOGGER.info("Message: " + t.toString());
            flag = false;
        }

        @Override
        public void startTest(Test test)
        {
            LOGGER.info("Executing test " + test);
            LOGGER.info("----------------------------");
            flag = true;
        }

        @Override
        public void endTest(Test test)
        {
            if (flag)
            {
                LOGGER.info("----------------------------");
                LOGGER.info("... test passed");
            }
            LOGGER.info("");
        }

        @Override
        protected void printFooter(TestResult result)
        {
            LOGGER.info("----------------------------");
            LOGGER.info("Passed:   " + (result.runCount() - result.failureCount() - result.errorCount()));
            LOGGER.info("Failed:   " + (result.failureCount() + result.errorCount()));
            LOGGER.info("Executed: " + result.runCount());
            LOGGER.info("----------------------------");
            LOGGER.info("");
            LOGGER.info("");
            executed += result.runCount();
            failed += (result.errorCount() + result.failureCount());
        }

        @Override
        protected void printHeader(long runTime)
        {
            LOGGER.info("");
            LOGGER.info("Test was finished. Execution summary: ");
            LOGGER.info("Spent time: " + runTime + " ms");
            time += runTime;
        }

        @Override
        protected void printErrors(TestResult result)
        {
        }

        @Override
        protected void printFailures(TestResult result)
        {
        }

        @Override
        protected void printDefectHeader(TestFailure failure, int count)
        {
            // LOGGER.info(count + ") " + failure.failedTest());
        }

        @Override
        protected void printDefectTrace(TestFailure failure)
        {
            // LOGGER.info(BaseTestRunner.getFilteredTrace(failure.trace()));
        }
    }
}
