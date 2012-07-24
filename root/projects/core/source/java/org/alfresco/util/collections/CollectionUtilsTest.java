/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.util.collections;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Unit tests for {@link CollectionUtils}.
 * 
 * @author Neil Mc Erlean
 */
public class CollectionUtilsTest
{
    @Test public void varArgsAsSet()
    {
        Set<String> expectedSet = new HashSet<String>();
        expectedSet.add("Larry");
        expectedSet.add("Curly");
        expectedSet.add("Moe");
        
        assertEquals(expectedSet, CollectionUtils.asSet(String.class, "Larry", "Curly", "Moe"));
    }
    
    @Test public void collectionFiltering() throws Exception
    {
        Map<String, Integer> nerdsBirthdays = new HashMap<String, Integer>();
        nerdsBirthdays.put("Alan Turing",           1912);
        nerdsBirthdays.put("Charles Babbage",       1791);
        nerdsBirthdays.put("Matthew Smith",         1966);
        nerdsBirthdays.put("Paul Dirac",            1902);
        nerdsBirthdays.put("Robert Boyle",          1627);
        nerdsBirthdays.put("Robert Hooke",          1635);
        nerdsBirthdays.put("J. Robert Oppenheimer", 1904);
        
        Function<String, Boolean> johnFilter = new KeySubstringFilter("John");
        assertEquals(0, CollectionUtils.filterKeys(nerdsBirthdays, johnFilter).size());
        
        Function<String, Boolean> robertFilter = new KeySubstringFilter("Robert");
        assertEquals(3, CollectionUtils.filterKeys(nerdsBirthdays, robertFilter).size());
    }
    
    private static final class KeySubstringFilter implements Function<String, Boolean>
    {
        private final String substring;
        public KeySubstringFilter(String substring) { this.substring = substring; }
        @Override public Boolean apply(String value)
        {
            return value.contains(substring);
        }
    }
}
