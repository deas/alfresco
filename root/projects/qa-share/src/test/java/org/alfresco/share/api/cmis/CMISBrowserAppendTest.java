/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.api.cmis;

import org.alfresco.share.enums.CMISBinding;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Class to include: Tests for CMIS Append for Browser binding
 * 
 * @author Abhijeet Bharade
 * 
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne", "MyAlfresco" })
public class CMISBrowserAppendTest extends CMISAppendTest
{

    private static Log logger = LogFactory.getLog(CMISBrowserAppendTest.class);

    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        try
        {
            super.beforeClass();
            binding = CMISBinding.BROWSER11;

            testName = this.getClass().getSimpleName();

            createTestData(testName);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    @Test
    public void AONE_14489() throws Exception
    {
        String thisTestName = getTestName();
        createDocTest(thisTestName);
    }

    @Test
    public void AONE_14490() throws Exception
    {
        appendTest(drone, getTestName(), false, fileName);

    }

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14491() throws Exception
    {
        appendTest(drone, getTestName(), true, fileName);
    }

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14492() throws Exception
    {
        appendSeveralChunksTest(drone, getFileName(getTestName()));
    }

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14493() throws Exception
    {
        String file150mbName = "150MB_file"; // 150+150+150 = 450 MB;
        try
        {
            try
            {
                RandomAccessFile f = new RandomAccessFile(DATA_FOLDER + file150mbName, "rw");
                f.setLength(150000000);
            }
            catch (Exception e)
            {
                logger.error("Test File don't created.");
            }
            appendLargeChunksTest(drone, file150mbName);
        }
        finally
        {
            File f = new File(DATA_FOLDER + file150mbName);
            f.delete();
        }
    }
}
