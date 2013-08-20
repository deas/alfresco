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
package org.alfresco.solr;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link DualPivotQuickSort}.
 * 
 * Provides tests methods for {@link DualPivotQuickSort.sort} and {@link Arrays.sort}, for comparing their time. 
 *
 * @author Alex Miller
 */
public class DualPivotQuickSortTest
{
    private static final int DATA_SET_SIZE = 10000000;
    
    private static final int NULL_PERCENTAGE = 1;
    
    private static final Random RANDOM = new Random();

    private static class TestComparator implements Comparator<Long> 
    {

        @Override
        public int compare(Long o1, Long o2)
        {
            if (o2 == null)
            {
                if (o1 == null)
                {
                    return 0;
                }
                else
                {
                    return -1;
                }
            }
            else
            {
                if (o1 == null)
                {
                    return 1;
                }
                else 
                {
                    long diff = o1 - o2;
                    if (o1.longValue() == o2.longValue())
                    {
                        return 0;
                    }
                    else
                    {
                        if (o1.longValue() < o2.longValue())
                        {
                            return -1;
                        }
                        else
                        {
                            return 1;
                        }
                    }
                }                
            }
        }
    }
    
    @Test
    public void testDualPivotQuickSort()
    {
        Long[] testData = getTestData();
        
        long startTime = System.currentTimeMillis();
        
        DualPivotQuickSort.sort(testData, new TestComparator());
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("DualPivotQuickSort.sort completed in " + (endTime - startTime) + "ms");

        assertSorted(testData);
    }
    
    @Test
    public void testArraysSort()
    {
        Long[] testData = getTestData();
        
        long startTime = System.currentTimeMillis();
        
        Arrays.sort(testData, new TestComparator());
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("Arrays.sort completed in " + (endTime - startTime) + "ms");
        
        assertSorted(testData);
    }
    
    private Long[] getTestData() 
    {
        Long[] testData = new Long[DATA_SET_SIZE];
        for (int i = 0 ; i < testData.length ; i++) 
        {
            int j = RANDOM.nextInt(100);
            if (j <= NULL_PERCENTAGE)
            {
                testData[i] = null;
            }
            else
            {
                testData[i] = (long)RANDOM.nextInt(100000);
            }
        }
        return testData;
    }
    
    private void assertSorted(Long[] testData)
    {
        boolean inNulls = false;
        for (int i = 1 ; i < testData.length ; i ++)
        {
            if (testData[i] == null)
            {
                if (inNulls)
                {
                    Assert.assertNull(testData[i-1]);
                }
                else
                {
                    inNulls = true;
                }
            }
            else
            {
                Assert.assertTrue(testData[i].longValue() >= testData[i-1].longValue());
            }
        }
    }
}
