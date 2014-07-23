/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.solr.content;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests {@link SolrContentUrlBuilder}
 * 
 * @author Derek Hulley
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrContentUrlBuilderTest
{
    @Test
    public void tenantOnly()
    {
        String url1 = SolrContentUrlBuilder
                .start()
                .add(SolrContentUrlBuilder.KEY_TENANT, "bob")
                .get();
        String url1Check = SolrContentUrlBuilder
                .start()
                .add(SolrContentUrlBuilder.KEY_TENANT, "bob")
                .get();
        Assert.assertEquals(url1, url1Check);
        String url2 = SolrContentUrlBuilder
                .start()
                .add(SolrContentUrlBuilder.KEY_TENANT, "jake")
                .get();
        Assert.assertNotEquals(url1, url2);
        
        Assert.assertTrue("Incorrect URL format.", url1.startsWith("solr://bob/"));
    }
}
