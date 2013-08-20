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
package org.alfresco.solr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 * @author Andy
 */
public class SolrDocumentLoaderTest extends TestCase
{
    public void testDeepPath() throws SolrServerException, IOException
    {
        SolrServer solr = getRemoteServer();

        solr.deleteByQuery("*:*");
        solr.commit(true, true);
        solr.optimize(true, true);
        
        long start = System.nanoTime();

        SolrInputDocument acl0 = createTestAcl(0, new String[] { "andy", "GROUP_MONKEY", "ROLE_ADMIN", "bob" });
        solr.add(acl0);

        SolrInputDocument root = createTestPathRoot(0);
        solr.add(root);
        SolrInputDocument rootAux = createTestPathRootAux(0, 0);
        solr.add(rootAux);
        
        String path = "";
        path = addDeepPair(solr, 1, path, "{http://www.alfresco.org/model/content/1.0}one");
        path = addDeepPair(solr, 2, path, "{http://www.alfresco.org/model/content/1.0}two");
        path = addDeepPair(solr, 3, path, "{http://www.alfresco.org/model/content/1.0}three");
        path = addDeepPair(solr, 4, path, "{http://www.alfresco.org/model/content/1.0}four");
        path = addDeepPair(solr, 5, path, "{http://www.alfresco.org/model/content/1.0}five");
        path = addDeepPair(solr, 6, path, "{http://www.alfresco.org/model/content/1.0}six");
        path = addDeepPair(solr, 7, path, "{http://www.alfresco.org/model/content/1.0}seven");
        path = addDeepPair(solr, 8, path, "{http://www.alfresco.org/model/content/1.0}eight");
        path = addDeepPair(solr, 9, path, "{http://www.alfresco.org/model/content/1.0}nine");
        path = addDeepPair(solr, 10, path, "{http://www.alfresco.org/model/content/1.0}ten");
        path = addDeepPair(solr, 11, path, "{http://www.alfresco.org/model/content/1.0}eleven");
        path = addDeepPair(solr, 12, path, "{http://www.alfresco.org/model/content/1.0}twelve");
        path = addDeepPair(solr, 13, path, "{http://www.alfresco.org/model/content/1.0}thirteen");
        path = addDeepPair(solr, 14, path, "{http://www.alfresco.org/model/content/1.0}fourteen");
        path = addDeepPair(solr, 15, path, "{http://www.alfresco.org/model/content/1.0}fifteen");
        path = addDeepPair(solr, 16, path, "{http://www.alfresco.org/model/content/1.0}sixteen");
        path = addDeepPair(solr, 17, path, "{http://www.alfresco.org/model/content/1.0}seventeen");
        path = addDeepPair(solr, 18, path, "{http://www.alfresco.org/model/content/1.0}eighteeen");
        path = addDeepPair(solr, 19, path, "{http://www.alfresco.org/model/content/1.0}nineteen");
        path = addDeepPair(solr, 20, path, "{http://www.alfresco.org/model/content/1.0}twenty");
        path = addDeepPair(solr, 21, path, "{http://www.alfresco.org/model/content/1.0}twenty-one");
        path = addDeepPair(solr, 22, path, "{http://www.alfresco.org/model/content/1.0}twenty-two");
        path = addDeepPair(solr, 23, path, "{http://www.alfresco.org/model/content/1.0}twenty-three");
        path = addDeepPair(solr, 24, path, "{http://www.alfresco.org/model/content/1.0}twenty-four");
        path = addDeepPair(solr, 25, path, "{http://www.alfresco.org/model/content/1.0}twenty-five");
        path = addDeepPair(solr, 26, path, "{http://www.alfresco.org/model/content/1.0}twenty-six");
        path = addDeepPair(solr, 27, path, "{http://www.alfresco.org/model/content/1.0}twenty-seven");
        path = addDeepPair(solr, 28, path, "{http://www.alfresco.org/model/content/1.0}twenty-eight");
        path = addDeepPair(solr, 29, path, "{http://www.alfresco.org/model/content/1.0}twenty-nine");
        path = addDeepPair(solr, 30, path, "{http://www.alfresco.org/model/content/1.0}thirty");
        
        solr.commit(true, true);

        solr.optimize(true, true);

        long end = System.nanoTime();

        SolrQuery query = new SolrQuery("*:*");
        QueryResponse response = solr.query(query);

        assertEquals(63, response.getResults().getNumFound());
        
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"//*//*//cm:thirty\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"//*//*//cm:twenty-nine\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"//*//*//cm:twenty-eight\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"//*//*//cm:twenty-seven\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"//*//*//cm:twenty-six\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"//*//*//cm:twenty-five\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"//*//*//cm:twenty-four\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"//*//*//cm:twenty-three\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"//*//*//cm:twenty-two\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"//*//*//cm:twenty-one\"}")).getResults().getNumFound());
     
        
        
      
    }
    
    private String addDeepPair(SolrServer solr, int dbid, String path, String qname) throws SolrServerException, IOException
    {
        StringBuilder builder = new StringBuilder(path);
        builder.append("/").append(qname);
        String newPath = builder.toString();
        
        solr.add(createTestPathDocument(dbid, asArray(""+(dbid-1)), asArray(qname), repeatAsArray(dbid, false,  "{http://www.alfresco.org/model/system/1.0}children"),
                "{http://www.alfresco.org/model/content/1.0}folder", asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(dbid)));
        solr.add(createTestPathDocumentAux(dbid, repeatAsArray(dbid, true, newPath), asArray(getDeepParents(dbid)), 0));
        
        return newPath;
    }
    
   
    private String[] repeatAsArray(int dbid, boolean prefix, String string)
    {
        String[] array = new String[dbid];
        for(int i = 0; i < dbid; i++)
        {
            if(prefix)
            {
               array[i] = "/{http://www.alfresco.org/model/content/1.0}repeat-"+i+string;
            }
            else
            {
                array[i] = string;
            }
        }
        return array;
    }

    private String[] getDeepParents(int dbid)
    {
        String[] parents = new String[dbid];
        for(int i = 0; i < dbid; i++)
        {
            parents[i] = ""+i;
        }
        return parents;
    }
    
    
    public void testPath() throws SolrServerException, IOException
    {
        SolrServer solr = getRemoteServer();

        solr.deleteByQuery("*:*");
        solr.commit(true, true);
        solr.optimize(true, true);

        long start = System.nanoTime();

        SolrInputDocument acl0 = createTestAcl(0, new String[] { "andy", "GROUP_MONKEY", "ROLE_ADMIN", "bob" });
        solr.add(acl0);

        SolrInputDocument root = createTestPathRoot(0);
        solr.add(root);
        SolrInputDocument rootAux = createTestPathRootAux(0, 0);
        solr.add(rootAux);

        solr.add(createTestPathDocument(1, asArray("0"), asArray("{http://www.alfresco.org/model/content/1.0}one"), asArray("{http://www.alfresco.org/model/system/1.0}children"),
                "{http://www.alfresco.org/model/content/1.0}folder", asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(1)));
        solr.add(createTestPathDocumentAux(1, asArray("/{http://www.alfresco.org/model/content/1.0}one"), asArray("0"), 0));

        solr.add(createTestPathDocument(2, asArray("0"), asArray("{http://www.alfresco.org/model/content/1.0}two"), asArray("{http://www.alfresco.org/model/system/1.0}children"),
                "{http://www.alfresco.org/model/content/1.0}folder", asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(2)));
        solr.add(createTestPathDocumentAux(2, asArray("/{http://www.alfresco.org/model/content/1.0}two"), asArray("0"), 0));

        solr.add(createTestPathDocument(3, asArray("0"), asArray("{http://www.alfresco.org/model/content/1.0}three"),
                asArray("{http://www.alfresco.org/model/system/1.0}children"), "{http://www.alfresco.org/model/content/1.0}folder",
                asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(3)));
        solr.add(createTestPathDocumentAux(3, asArray("/{http://www.alfresco.org/model/content/1.0}three"), asArray("0"), 0));

        solr.add(createTestPathDocument(4, asArray("0"), asArray("{http://www.alfresco.org/model/content/1.0}four"), asArray("{http://www.alfresco.org/model/system/1.0}children"),
                "{http://www.alfresco.org/model/content/1.0}folder", asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(4)));
        solr.add(createTestPathDocumentAux(4, asArray("/{http://www.alfresco.org/model/content/1.0}four"), asArray("0"), 0));

        solr.add(createTestPathDocument(5, asArray("1"), asArray("{http://www.alfresco.org/model/content/1.0}five"),
                asArray("{http://www.alfresco.org/model/content/1.0}contains"), "{http://www.alfresco.org/model/content/1.0}folder",
                asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(5)));
        solr.add(createTestPathDocumentAux(5, asArray("/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}five"), asArray("0", "1"), 0));

        solr.add(createTestPathDocument(6, asArray("1"), asArray("{http://www.alfresco.org/model/content/1.0}six"), asArray("{http://www.alfresco.org/model/content/1.0}contains"),
                "{http://www.alfresco.org/model/content/1.0}folder", asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(6)));
        solr.add(createTestPathDocumentAux(6, asArray("/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}six"), asArray("0", "1"), 0));

        solr.add(createTestPathDocument(7, asArray("2"), asArray("{http://www.alfresco.org/model/content/1.0}seven"),
                asArray("{http://www.alfresco.org/model/content/1.0}contains"), "{http://www.alfresco.org/model/content/1.0}folder",
                asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(7)));
        solr.add(createTestPathDocumentAux(7, asArray("/{http://www.alfresco.org/model/content/1.0}two/{http://www.alfresco.org/model/content/1.0}seven"), asArray("0", "2"), 0));

        solr.add(createTestPathDocument(8, asArray("2", "0", "1"), asArray("{http://www.alfresco.org/model/content/1.0}eight-2",
                "{http://www.alfresco.org/model/content/1.0}eight-0", "{http://www.alfresco.org/model/content/1.0}eight-1"),
                asArray("{http://www.alfresco.org/model/content/1.0}contains", "{http://www.alfresco.org/model/system/1.0}children",
                        "{http://www.alfresco.org/model/content/1.0}contains"), "{http://www.alfresco.org/model/content/1.0}folder",
                asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(8)));
        solr.add(createTestPathDocumentAux(8, asArray("/{http://www.alfresco.org/model/content/1.0}two/{http://www.alfresco.org/model/content/1.0}eight-2",
                "/{http://www.alfresco.org/model/content/1.0}eight-0", "/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}eight-1"),
                asArray("0", "1", "2"), 0));

        solr.add(createTestPathDocument(9, asArray("5"), asArray("{http://www.alfresco.org/model/content/1.0}nine"),
                asArray("{http://www.alfresco.org/model/content/1.0}contains"), "{http://www.alfresco.org/model/content/1.0}folder",
                asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(9)));
        solr.add(createTestPathDocumentAux(9,
                asArray("/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}five/{http://www.alfresco.org/model/content/1.0}nine"),
                asArray("0", "1", "5"), 0));

        solr.add(createTestPathDocument(10, asArray("5"), asArray("{http://www.alfresco.org/model/content/1.0}ten"),
                asArray("{http://www.alfresco.org/model/content/1.0}contains"), "{http://www.alfresco.org/model/content/1.0}folder",
                asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(10)));
        solr.add(createTestPathDocumentAux(10,
                asArray("/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}five/{http://www.alfresco.org/model/content/1.0}ten"), asArray(
                        "0", "1", "5"), 0));

        solr.add(createTestPathDocument(11, asArray("5"), asArray("{http://www.alfresco.org/model/content/1.0}eleven"),
                asArray("{http://www.alfresco.org/model/content/1.0}contains"), "{http://www.alfresco.org/model/content/1.0}folder",
                asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(11)));
        solr.add(createTestPathDocumentAux(11,
                asArray("/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}five/{http://www.alfresco.org/model/content/1.0}eleven"),
                asArray("0", "1", "5"), 0));

        solr.add(createTestPathDocument(12, asArray("5"), asArray("{http://www.alfresco.org/model/content/1.0}twelve"),
                asArray("{http://www.alfresco.org/model/content/1.0}contains"), "{http://www.alfresco.org/model/content/1.0}folder",
                asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(12)));
        solr.add(createTestPathDocumentAux(12,
                asArray("/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}five/{http://www.alfresco.org/model/content/1.0}twelve"),
                asArray("0", "1", "5"), 0));

        solr.add(createTestPathDocument(13, asArray("12", "2"), asArray("{http://www.alfresco.org/model/content/1.0}thirteen", "{http://www.alfresco.org/model/content/1.0}link"),
                asArray("{http://www.alfresco.org/model/content/1.0}contains", "{http://www.alfresco.org/model/content/1.0}contains"),
                "{http://www.alfresco.org/model/content/1.0}folder", asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(13)));
        solr
                .add(createTestPathDocumentAux(
                        13,
                        asArray(
                                "/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}five/{http://www.alfresco.org/model/content/1.0}twelve/{http://www.alfresco.org/model/content/1.0}thirteen",
                                "/{http://www.alfresco.org/model/content/1.0}two/{http://www.alfresco.org/model/content/1.0}link"), asArray("0", "1", "5", "12", "2"), 0));

        solr.add(createTestPathDocument(14, asArray("13", "1", "2", "5", "6", "12", "13"), asArray("{http://www.alfresco.org/model/content/1.0}fourteen",
                "{http://www.alfresco.org/model/content/1.0}common", "{http://www.alfresco.org/model/content/1.0}common", "{http://www.alfresco.org/model/content/1.0}common",
                "{http://www.alfresco.org/model/content/1.0}common", "{http://www.alfresco.org/model/content/1.0}common", "{http://www.alfresco.org/model/content/1.0}common"),
                asArray("{http://www.alfresco.org/model/content/1.0}contains", "{http://www.alfresco.org/model/content/1.0}contains",
                        "{http://www.alfresco.org/model/content/1.0}contains", "{http://www.alfresco.org/model/content/1.0}contains",
                        "{http://www.alfresco.org/model/content/1.0}contains", "{http://www.alfresco.org/model/content/1.0}contains",
                        "{http://www.alfresco.org/model/content/1.0}contains"), "{http://www.alfresco.org/model/content/1.0}folder",
                asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(14)));
        solr
                .add(createTestPathDocumentAux(
                        14,
                        asArray(
                                "/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}five/{http://www.alfresco.org/model/content/1.0}twelve/{http://www.alfresco.org/model/content/1.0}thirteen/{http://www.alfresco.org/model/content/1.0}fourteen",
                                "/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}common",
                                "/{http://www.alfresco.org/model/content/1.0}two/{http://www.alfresco.org/model/content/1.0}common",
                                "/{http://www.alfresco.org/model/content/1.0}two/{http://www.alfresco.org/model/content/1.0}link/{http://www.alfresco.org/model/content/1.0}fourteen",
                                "/{http://www.alfresco.org/model/content/1.0}two/{http://www.alfresco.org/model/content/1.0}link/{http://www.alfresco.org/model/content/1.0}common",
                                "/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}five/{http://www.alfresco.org/model/content/1.0}common",
                                "/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}six/{http://www.alfresco.org/model/content/1.0}common",
                                "/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}five/{http://www.alfresco.org/model/content/1.0}twelve/{http://www.alfresco.org/model/content/1.0}common",
                                "/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}five/{http://www.alfresco.org/model/content/1.0}twelve/{http://www.alfresco.org/model/content/1.0}thirteen/{http://www.alfresco.org/model/content/1.0}common"),
                        asArray("0", "1", "5", "12", "13", "2", "6"), 0));

        solr.add(createTestPathDocument(15, asArray("13"), asArray("{http://www.alfresco.org/model/content/1.0}fifteen"),
                asArray("{http://www.alfresco.org/model/content/1.0}contains"), "{http://www.alfresco.org/model/content/1.0}folder",
                asArray("{http://www.alfresco.org/model/content/1.0}auditable"), createProperties(15)));
        solr
                .add(createTestPathDocumentAux(
                        15,
                        asArray(
                                "/{http://www.alfresco.org/model/content/1.0}one/{http://www.alfresco.org/model/content/1.0}five/{http://www.alfresco.org/model/content/1.0}twelve/{http://www.alfresco.org/model/content/1.0}thirteen/{http://www.alfresco.org/model/content/1.0}fifteen",
                                "/{http://www.alfresco.org/model/content/1.0}two/{http://www.alfresco.org/model/content/1.0}link/{http://www.alfresco.org/model/content/1.0}fifteen"),
                        asArray("0", "1", "5", "12", "13"), 0));

        solr.commit(true, true);

        solr.optimize(true, true);

        long end = System.nanoTime();

        SolrQuery query = new SolrQuery("*:*");
        QueryResponse response = solr.query(query);

        assertEquals(33, response.getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/.\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:two\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:three\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:four\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:eight-0\"}")).getResults().getNumFound());
        assertEquals(0, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:five\"}")).getResults().getNumFound());
        assertEquals(0, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:one\"}")).getResults().getNumFound());
        assertEquals(0, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:two\"}")).getResults().getNumFound());
        assertEquals(0, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:two/cm:one\"}")).getResults().getNumFound());
        assertEquals(0, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:two/cm:two\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:five\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:six\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:two/cm:seven\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:eight-1\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:two/cm:eight-2\"}")).getResults().getNumFound());
        assertEquals(0, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:eight-2\"}")).getResults().getNumFound());
        assertEquals(0, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:two/cm:eight-1\"}")).getResults().getNumFound());
        assertEquals(0, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:two/cm:eight-0\"}")).getResults().getNumFound());
        assertEquals(0, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:eight-0\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:five/cm:nine\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:five/cm:ten\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:five/cm:eleven\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:five/cm:twelve\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:five/cm:twelve/cm:thirteen\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:five/cm:twelve/cm:thirteen/cm:fourteen\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:five/cm:twelve/cm:thirteen/cm:common\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:five/cm:twelve/cm:common\"}")).getResults().getNumFound());
        assertEquals(5, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:*\"}")).getResults().getNumFound());
        assertEquals(5, solr.query(new SolrQuery("{!afts v=PATH:\"/*\"}")).getResults().getNumFound());
        assertEquals(6, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:*/cm:*\"}")).getResults().getNumFound());
        assertEquals(6, solr.query(new SolrQuery("{!afts v=PATH:\"/*/*\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:*/cm:five\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/*/cm:five\"}")).getResults().getNumFound());
        assertEquals(6, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:*/cm:*/cm:*\"}")).getResults().getNumFound());
        assertEquals(6, solr.query(new SolrQuery("{!afts v=PATH:\"/*/*/*\"}")).getResults().getNumFound());
        assertEquals(4, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/*\"}")).getResults().getNumFound());
        assertEquals(4, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:*\"}")).getResults().getNumFound());
        assertEquals(5, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:*/cm:five/cm:*\"}")).getResults().getNumFound());
        assertEquals(5, solr.query(new SolrQuery("{!afts v=PATH:\"/*/cm:five/*\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:*/cm:nine\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/*/cm:nine\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/*/cm:five\"}")).getResults().getNumFound());
        assertEquals(16, solr.query(new SolrQuery("{!afts v=PATH:\"//.\"}")).getResults().getNumFound());
        assertEquals(15, solr.query(new SolrQuery("{!afts v=PATH:\"//*\"}")).getResults().getNumFound());
        assertEquals(15, solr.query(new SolrQuery("{!afts v=PATH:\"//*/.\"}")).getResults().getNumFound());
        assertEquals(15, solr.query(new SolrQuery("{!afts v=PATH:\"//*/./.\"}")).getResults().getNumFound());
        assertEquals(15, solr.query(new SolrQuery("{!afts v=PATH:\"//./*\"}")).getResults().getNumFound());
        assertEquals(15, solr.query(new SolrQuery("{!afts v=PATH:\"//././*/././.\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"//cm:common\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one//cm:common\"}")).getResults().getNumFound());
        assertEquals(7, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:five//*\"}")).getResults().getNumFound());
        assertEquals(8, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one/cm:five//.\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one//cm:five/cm:nine\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one//cm:thirteen/cm:fourteen\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one//cm:thirteen/cm:fourteen//.\"}")).getResults().getNumFound());
        assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"/cm:one//cm:thirteen/cm:fourteen//.//.\"}")).getResults().getNumFound());

        testAllPathsTo(solr, "", false, "cm:one");
        testAllPathsTo(solr, "", false, "cm:two");
        testAllPathsTo(solr, "", false, "cm:three");
        testAllPathsTo(solr, "", false, "cm:four");
        testAllPathsTo(solr, "", false, "cm:eight-0");
        testAllPathsTo(solr, "", false, "cm:one", "cm:five");
        testAllPathsTo(solr, "", false, "cm:one", "cm:six");
        testAllPathsTo(solr, "", false, "cm:one", "cm:eight-1");
        testAllPathsTo(solr, "", false, "cm:one", "cm:common");
        testAllPathsTo(solr, "", false, "cm:two", "cm:seven");
        testAllPathsTo(solr, "", false, "cm:two", "cm:eight-2");
        testAllPathsTo(solr, "", false, "cm:two", "cm:link");
        testAllPathsTo(solr, "", false, "cm:two", "cm:common");
        testAllPathsTo(solr, "", false, "cm:one", "cm:five", "cm:nine");
        testAllPathsTo(solr, "", false, "cm:one", "cm:five", "cm:ten");
        testAllPathsTo(solr, "", false, "cm:one", "cm:five", "cm:eleven");
        testAllPathsTo(solr, "", false, "cm:one", "cm:five", "cm:twelve");
        testAllPathsTo(solr, "", false, "cm:one", "cm:five", "cm:common");
        testAllPathsTo(solr, "", false, "cm:one", "cm:six", "cm:common");
        testAllPathsTo(solr, "", false, "cm:one", "cm:five", "cm:twelve", "cm:thirteen");
        testAllPathsTo(solr, "", false, "cm:one", "cm:five", "cm:twelve", "cm:common");
        testAllPathsTo(solr, "", false, "cm:one", "cm:five", "cm:twelve", "cm:thirteen", "cm:fourteen");
        testAllPathsTo(solr, "", false, "cm:one", "cm:five", "cm:twelve", "cm:thirteen", "cm:fifteen");
        testAllPathsTo(solr, "", false, "cm:one", "cm:five", "cm:twelve", "cm:thirteen", "cm:common");
        testAllPathsTo(solr, "", false, "cm:two", "cm:link", "cm:fourteen");
        testAllPathsTo(solr, "", false, "cm:two", "cm:link", "cm:fifteen");
        testAllPathsTo(solr, "", false, "cm:two", "cm:link", "cm:common");

        System.out.println("Time " + ((end - start) / 1000000000.0));

    }

    private void testAllPathsTo(SolrServer solr, String path, boolean nextDescendantsOnly, String... parts) throws SolrServerException
    {
        if (parts.length > 1)
        {
            String[] tail = new String[parts.length - 1];
            System.arraycopy(parts, 1, tail, 0, tail.length);
            testAllPathsTo(solr, path, true, tail);
            if (!nextDescendantsOnly)
            {
                testAllPathsTo(solr, path + "/" + parts[0], false, tail);
                testAllPathsTo(solr, path + "/*", false, tail);
            }

            testAllPathsTo(solr, path + "//" + parts[0], false, tail);
            testAllPathsTo(solr, path + "//*", false, tail);
        }
        else
        {
            if (!nextDescendantsOnly)
            {
                System.out.println("{!afts v=PATH:\"" + path + "/" + parts[0] + "\"}");
                assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"" + path + "/" + parts[0] + "\"}")).getResults().getNumFound());
                System.out.println("{!afts v=PATH:\"" + path + "/" + parts[0] + "/.\"}");
                assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"" + path + "/" + parts[0] + "/.\"}")).getResults().getNumFound());
            }
            System.out.println("{!afts v=PATH:\"" + path + "//" + parts[0] + "\"}");
            assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"" + path + "//" + parts[0] + "\"}")).getResults().getNumFound());
            System.out.println("{!afts v=PATH:\"" + path + "//" + parts[0] + "/.\"}");
            assertEquals(1, solr.query(new SolrQuery("{!afts v=PATH:\"" + path + "//" + parts[0] + "/.\"}")).getResults().getNumFound());
        }

    }

    private String[] asArray(String... string)
    {
        return string;
    }

    private Map<QName, String> createProperties(int doc)
    {
        HashMap<QName, String> properties = new HashMap<QName, String>();
        properties.put(ContentModel.PROP_LOCALE, "en");
        properties.put(ContentModel.PROP_TITLE, "Document Number " + doc);
        properties.put(ContentModel.PROP_NAME, "Doc " + doc);
        properties.put(ContentModel.PROP_CREATED, "2010-07-21T10:52:00.000Z");
        properties.put(ContentModel.PROP_CREATOR, "Andy");
        properties.put(ContentModel.PROP_MODIFIED, "2010-07-22T10:52:00.000Z");
        properties.put(ContentModel.PROP_MODIFIER, "Bob");

        return properties;
    }

    public void testBulkLoad() throws SolrServerException, IOException
    {
        int FOLDERS = 10000;
        int LEAVES = 100;

        SolrServer solr = getRemoteServer();

        solr.deleteByQuery("*:*");
        solr.commit(true, true);
        solr.optimize(true, true);

        int dbid = 0;

        SolrInputDocument root = createRootDocument(dbid);
        solr.add(root);

        SolrInputDocument rootPath = createRootPathDocument(dbid++);
        solr.add(rootPath);

        solr.commit(true, true);
        solr.optimize(true, true);

        SolrQuery query = new SolrQuery("*:*");
        QueryResponse response = solr.query(query);

        assert (2 == response.getResults().size());

        long start = System.nanoTime();

        for (int i = 0; i < FOLDERS; i++)
        {
            Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
            docs.add(createContainerDocument(dbid, i));
            docs.add(createContainerPathDocument(dbid++, i));
            for (int j = 0; j < LEAVES; j++)
            {
                docs.add(createLeafDocument(dbid, i, j));
                docs.add(createLeafPathDocument(dbid++, i, j));
            }
            solr.add(docs);
        }

        solr.commit(true, true);

        solr.optimize(true, true);

        long end = System.nanoTime();

        query = new SolrQuery("*:*");
        response = solr.query(query);

        System.out.println("Time " + ((end - start) / 1000000000.0));

        assert ((FOLDERS * (LEAVES + 1) + 2) == response.getResults().size());

        System.out.println("Done " + (FOLDERS * (LEAVES + 1) + 2));
    }

    public SolrInputDocument createLeafDocument(int dbid, int i, int j)
    {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("ISCATEGORY", "F");
        doc.addField("PARENT", "ID-F-" + i);
        doc.addField("QNAME", "{http://www.alfresco.org/model/content/1.0}leaf-" + j);
        doc.addField("PRIMARYPARENT", "ID-F-" + i);
        doc.addField("ASSOCTYPEQNAME", "{http://www.alfresco.org/model/content/1.0}contains");
        doc.addField("PRIMARYASSOCTYPEQNAME", "{http://www.alfresco.org/model/content/1.0}contains");
        doc.addField("ANCESTOR", "ID-F-" + i);
        doc.addField("FTSSTATUS", "Clean");
        doc.addField("ID", "ID-L-" + i + "-" + j);
        doc.addField("TX", "TX-1");
        doc.addField("ISROOT", "F");
        doc.addField("ISNODE", "T");
        doc.addField("DBID", dbid);
        doc.addField("TYPE", "{http://www.alfresco.org/model/content/1.0}folder");
        doc.addField("ASPECT", "{http://www.alfresco.org/model/content/1.0}auditable");
        doc.addField("@{http://www.alfresco.org/model/system/1.0}locale", "en");
        doc.addField("@{http://www.alfresco.org/model/content/1.0}title", "Leaf " + i + " " + j);
        doc.addField("@{http://www.alfresco.org/model/content/1.0}name", "Folder " + i + " " + j);
        doc.addField("@{http://www.alfresco.org/model/content/1.0}created", "2010-07-21T10:52:00.000Z");
        doc.addField("@{http://www.alfresco.org/model/content/1.0}creator", "Andy");
        doc.addField("@{http://www.alfresco.org/model/content/1.0}modified", "2010-07-21T10:52:00.000Z");
        doc.addField("@{http://www.alfresco.org/model/content/1.0}modifier", "Andy");
        return doc;
    }

    public SolrInputDocument createLeafPathDocument(int dbid, int i, int j)
    {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("ID", "ID-L-" + i + "-" + j + "-Path");
        doc.addField("TX", "TX-1");
        doc.addField("DBID", "" + dbid);
        QName first = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "folder_" + i);
        QName second = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "leaf_" + j);
        doc.addField("PATH", "/" + ISO9075.getXPathName(first) + "/" + ISO9075.getXPathName(second));
        return doc;
    }

    public SolrInputDocument createContainerDocument(int dbid, int i)
    {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("ISCATEGORY", "F");
        doc.addField("PARENT", "Root");
        doc.addField("QNAME", "{http://www.alfresco.org/model/content/1.0}folder-" + i);
        doc.addField("PRIMARYPARENT", "Root");
        doc.addField("ASSOCTYPEQNAME", "{http://www.alfresco.org/model/system/1.0}children");
        doc.addField("PRIMARYASSOCTYPEQNAME", "{http://www.alfresco.org/model/system/1.0}children");
        doc.addField("ANCESTOR", "Root");
        doc.addField("FTSSTATUS", "Clean");
        doc.addField("ID", "ID-F-" + i);
        doc.addField("TX", "TX-1");
        doc.addField("ISROOT", "T");
        doc.addField("ISNODE", "T");
        doc.addField("DBID", "" + dbid);
        doc.addField("TYPE", "{http://www.alfresco.org/model/content/1.0}folder");
        doc.addField("ASPECT", "{http://www.alfresco.org/model/content/1.0}auditable");
        doc.addField("@{http://www.alfresco.org/model/system/1.0}locale", "en");
        doc.addField("@{http://www.alfresco.org/model/content/1.0}title", "Folder " + i);
        doc.addField("@{http://www.alfresco.org/model/content/1.0}name", "Folder " + i);
        doc.addField("@{http://www.alfresco.org/model/content/1.0}created", "2010-07-21T10:52:00.000Z");
        doc.addField("@{http://www.alfresco.org/model/content/1.0}creator", "Andy");
        doc.addField("@{http://www.alfresco.org/model/content/1.0}modified", "2010-07-21T10:52:00.000Z");
        doc.addField("@{http://www.alfresco.org/model/content/1.0}modifier", "Andy");
        return doc;
    }

    public SolrInputDocument createContainerPathDocument(int dbid, int i)
    {
        SolrInputDocument doc = new SolrInputDocument();

        doc.addField("ID", "ID-F-" + i + "-Path");
        doc.addField("TX", "TX-1");
        doc.addField("DBID", "" + dbid);
        QName first = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "folder_" + i);
        doc.addField("PATH", "/" + ISO9075.getXPathName(first));
        return doc;
    }

    public SolrInputDocument createRootDocument(int dbid)
    {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("ISCATEGORY", "F");
        doc.addField("FTSSTATUS", "Clean");
        doc.addField("ID", "Root");
        doc.addField("TX", "TX-1");
        doc.addField("ISROOT", "T");
        doc.addField("ISNODE", "T");
        doc.addField("DBID", "" + dbid);
        return doc;
    }

    public SolrInputDocument createRootPathDocument(int dbid)
    {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("PATH", "/");
        doc.addField("ID", "Root-Path");
        doc.addField("TX", "TX-1");
        doc.addField("DBID", "" + dbid);
        return doc;
    }

    public SolrServer getRemoteServer() throws MalformedURLException
    {
        CommonsHttpSolrServer solr = new CommonsHttpSolrServer("http://localhost:8080/solr/test");
        solr.setRequestWriter(new BinaryRequestWriter());
        return solr;
    }

    public SolrInputDocument createTestPathRoot(int dbid)
    {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("ISCATEGORY", "F");
        doc.addField("FTSSTATUS", "Clean");
        doc.addField("ID", "Root");
        doc.addField("TX", "TX-1");
        doc.addField("ISROOT", "T");
        doc.addField("ISNODE", "T");
        doc.addField("DBID", "" + dbid);
        return doc;
    }

    public SolrInputDocument createTestPathRootAux(int dbid, int acl)
    {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("ID", "Root-Path");
        doc.addField("TX", "TX-1");
        doc.addField("DBID", "" + dbid);
        doc.addField("PATH", "/");
        // doc.addField("ACL", ""+acl);
        return doc;
    }

    public SolrInputDocument createTestAcl(int acl, String[] readers)
    {
        SolrInputDocument doc = new SolrInputDocument();
        // doc.addField("ACL", ""+acl);
        doc.addField("TX", "TX-1");
        doc.addField("ID", "ACL-" + acl);
        for (String reader : readers)
        {
            // doc.addField("READER", reader);
        }
        return doc;
    }

    public SolrInputDocument createTestPathDocument(int dbid, String[] parents, String[] qnames, String[] assocTypeQnames, String type, String[] aspects,
            Map<QName, String> properties)
    {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("ISCATEGORY", "F");
        for (String parent : parents)
        {
            doc.addField("PARENT", parent);
        }
        for (String qname : qnames)
        {
            doc.addField("QNAME", qname);
        }
        doc.addField("PRIMARYPARENT", parents[0]);
        for (String assocTypeQname : assocTypeQnames)
        {
            doc.addField("ASSOCTYPEQNAME", assocTypeQname);
        }
        doc.addField("PRIMARYASSOCTYPEQNAME", assocTypeQnames[0]);
        doc.addField("FTSSTATUS", "Clean");
        doc.addField("ID", "ID-" + dbid);
        doc.addField("TX", "TX-1");
        doc.addField("ISROOT", "F");
        doc.addField("ISNODE", "T");
        doc.addField("DBID", "" + dbid);
        doc.addField("TYPE", type);
        for (String aspect : aspects)
        {
            doc.addField("ASPECT", aspect);
        }
        for (QName qname : properties.keySet())
        {
            doc.addField("@" + qname, properties.get(qname));
        }
        return doc;
    }

    public SolrInputDocument createTestPathDocumentAux(int dbid, String[] paths, String[] ancestors, int acl)
    {
        SolrInputDocument doc = new SolrInputDocument();
        for (String ancestor : ancestors)
        {
            doc.addField("ANCESTOR", ancestor);
        }
        doc.addField("ID", "ID-" + dbid + "-Path");
        doc.addField("TX", "TX-1");
        doc.addField("DBID", "" + dbid);
        for (String path : paths)
        {
            doc.addField("PATH", path);
        }
        // doc.addField("ACL", ""+acl);
        return doc;
    }
}
