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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Unit tests for {@link CollectionUtils}.
 * 
 * @author Neil Mc Erlean
 * @since TODO
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
}
