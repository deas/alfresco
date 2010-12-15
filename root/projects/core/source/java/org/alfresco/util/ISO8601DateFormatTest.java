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
package org.alfresco.util;

import java.util.Date;

import junit.framework.TestCase;

public class ISO8601DateFormatTest extends TestCase
{
    public void testConversion()
    {
        String test = "2005-09-16T17:01:03.456+01:00";
        // convert to a date
        Date date = ISO8601DateFormat.parse(test);
        // get the string form
        String strDate = ISO8601DateFormat.format(date);
        // convert back to a date from the converted string
        Date dateAfter = ISO8601DateFormat.parse(strDate);
        // make sure the date objects match, test this instead of the
        // string as the string form will be different in different
        // locales
        assertEquals(date, dateAfter);
    }
}
