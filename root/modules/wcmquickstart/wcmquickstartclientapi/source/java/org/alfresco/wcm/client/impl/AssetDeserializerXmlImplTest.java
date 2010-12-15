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
package org.alfresco.wcm.client.impl;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.TreeMap;

import junit.framework.TestCase;

public class AssetDeserializerXmlImplTest extends TestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    public void test1() throws Exception
    {
        AssetDeserializerXmlImpl deserializer = new AssetDeserializerXmlImpl();
        LinkedList<TreeMap<String,Serializable>> results = 
            deserializer.deserialize(new ByteArrayInputStream(sampleXml1.getBytes("UTF-8")));
        System.out.println(results);
        assertEquals(1, results.size());
    }

    private String sampleXml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><assets><asset id=\"workspace://SpacesStore/169bd7ad-5fbf-4ae5-afc2-be0623d13c62\" type=\"myapp:MyType\"><property name=\"myapp:textListProperty\"><list><value type=\"text\"><![CDATA[One]]></value><value type=\"text\"><![CDATA[Two]]></value><value type=\"text\"><![CDATA[Three]]></value><value type=\"text\"><![CDATA[Four]]></value><value type=\"text\"><![CDATA[Five]]></value><value type=\"text\"><![CDATA[Six]]></value></list></property><property name=\"myapp:noderefProperty\"><value type=\"id\">workspace://SpacesStore/c21d39da-545d-42cc-9de7-e314d81c760b</value></property><property name=\"myapp:textProperty\"><value type=\"text\"><![CDATA[The радиатор &lt;sat&gt; on the mat: first on one side; the on the other... –]]></value></property><property name=\"myapp:dateProperty\"><value type=\"time\">20101014-14:37:38.579+0100</value></property><property name=\"myapp:integerProperty\"><value type=\"integer\">678</value></property><property name=\"myapp:intListProperty\"><list><value type=\"integer\">1</value><value type=\"integer\">2</value><value type=\"integer\">3</value><value type=\"integer\">4</value><value type=\"integer\">5</value><value type=\"integer\">6</value></list></property><property name=\"myapp:doubleProperty\"><value type=\"number\">1.324352E14</value></property><property name=\"myapp:longProperty\"><value type=\"integer\">6737436288</value></property><property name=\"myapp:floatProperty\"><value type=\"number\">132.43524</value></property></asset></assets>";
}
