/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.wcm.client.util;

import junit.framework.TestCase;


public class QuerySanitizerTest extends TestCase
{
    public void testSanitizer()
    {
        assertEquals("lhfusohfjkb7489374_-", QuerySanitizer.sanitize("lhfusohfjkb7489374_-"));
        assertEquals("    ", QuerySanitizer.sanitize("\\/!\""));
        assertEquals("                           ", QuerySanitizer.sanitize("\"'%?*()$^<>/{}[]#~@.,|\\+!:;"));
    }
}
