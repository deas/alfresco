/*
 * Copyright ss(C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.wcm.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SectionFactoryTest extends BaseTest
{
    private final static Log log = LogFactory.getLog(SectionFactoryTest.class);

    public void testGetSections()
    {
        WebSite site = getWebSite();
        Section root = site.getRootSection();
        String rootId = root.getId();

        Section section = sectionFactory.getSectionFromPathSegments(rootId, new String[] { "news" });
        assertNotNull(section);
        // assertNotNull(section.getCollectionFolderId());

        Section bad = sectionFactory.getSectionFromPathSegments(rootId, new String[] { "news", "wooble" });
        assertNull(bad);

        Section exists2 = sectionFactory.getSection(section.getId());
        assertNotNull(exists2);
        // assertNotNull(exists2.getCollectionFolderId());

        log.debug(section.getProperties());
    }

}
