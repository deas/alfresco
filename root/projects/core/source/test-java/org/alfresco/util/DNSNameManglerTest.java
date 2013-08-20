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
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>. */

package org.alfresco.util;

import junit.framework.TestCase;

/**
 * Test the DNSNameMangler.
 * @author britt
 */
public class DNSNameManglerTest extends TestCase
{
    /**
     * Test it.
     */
    public void testIt()
    {
        try
        {
            String mangled = DNSNameMangler.MakeDNSName("website", "britt", "main");
            System.out.println(mangled);
            assertTrue(mangled.length() <= 59);
            mangled = DNSNameMangler.MakeDNSName("website", "Foodle Dee dOO", "main");
            System.out.println(mangled);
            assertTrue(mangled.length() <= 59);
            mangled = DNSNameMangler.MakeDNSName("website-thinkl$", "winky_froo", "orkle");
            System.out.println(mangled);
            assertTrue(mangled.length() <= 59);
            mangled = DNSNameMangler.MakeDNSName("fork", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxZZxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", "winkle");
            System.out.println(mangled);
            assertTrue(mangled.length() <= 59);
            mangled = DNSNameMangler.MakeDNSName("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", "Frederick", "preview");
            System.out.println(mangled);
            assertTrue(mangled.length() <= 59);
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            fail();
        }
    }
}
