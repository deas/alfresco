/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.cmis.client.test;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

public class Test
{
    public static Session createSession()
    {
        SessionFactory f = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();

        // user credentials
        parameter.put(SessionParameter.USER, "admin");
        parameter.put(SessionParameter.PASSWORD, "admin");

        // connection settings
        parameter.put(SessionParameter.ATOMPUB_URL, "http://localhost:8080/alfresco/service/cmis");
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");

        // create session
        return f.getRepositories(parameter).get(0).createSession();
    }

    public static void printAspects(AlfrescoDocument alfDoc)
    {
        System.out.println("------------------------------------------");
        System.out.println("Aspects:");
        System.out.println("------------------------------------------");
        for (ObjectType aspect : alfDoc.getAspects())
        {
            System.out.println(aspect.getId());
        }
    }

    public static void printProperties(Document doc)
    {
        System.out.println("------------------------------------------");
        System.out.println("Properties:");
        System.out.println("------------------------------------------");
        for (Property<?> prop : doc.getProperties())
        {
            System.out.println(prop.getId() + ": " + prop.getValuesAsString());
        }
    }

    public static void main(String[] args)
    {
        Session session = createSession();

        // create document
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, "test1");
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document,P:cm:titled");
        properties.put("cm:description", "Beschreibung");

        Document doc = session.getRootFolder().createDocument(properties, null, null);

        printProperties(doc);

        // update properties
        properties.clear();
        properties.put(PropertyIds.NAME, "test2");
        properties.put("cm:description", "My Description");

        doc.updateProperties(properties);

        printProperties(doc);

        // get aspects
        AlfrescoDocument alfDoc = (AlfrescoDocument) doc;

        printAspects(alfDoc);

        // add aspect
        alfDoc.addAspect("P:cm:taggable");

        printAspects(alfDoc);

        // remove aspect
        alfDoc.removeAspect("P:cm:titled");

        printAspects(alfDoc);

        printProperties(doc);

        alfDoc.delete(true);
    }
}
