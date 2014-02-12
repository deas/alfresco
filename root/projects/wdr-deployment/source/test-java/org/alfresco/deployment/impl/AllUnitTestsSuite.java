package org.alfresco.deployment.impl;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.deployment.impl.dmr.RootLocatorImplTest;
import org.alfresco.deployment.impl.dmr.StoreNameMapperImplTest;

/**
 * All wdr project UNIT test classes should be added to this test suite.
 */
public class AllUnitTestsSuite extends TestSuite
{
    /**
     * Creates the test suite
     *
     * @return  the test suite
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(RootLocatorImplTest.class);
        suite.addTestSuite(StoreNameMapperImplTest.class);
        return suite;
    }
 
}
