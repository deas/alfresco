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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.TransientAlfrescoDocument;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.api.TransientDocument;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

public class CMISClientTest extends TestCase
{
    private Session session;

    @Override
    protected void setUp() throws Exception
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
        session = f.getRepositories(parameter).get(0).createSession();
    }

    public void testCreateUpdateDeleteDocument()
    {
        String descriptionValue1 = "Beschreibung";
        String descriptionValue2 = "My Description";

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, "test1");
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document,P:cm:titled");
        properties.put("cm:description", descriptionValue1);

        Document doc = session.getRootFolder().createDocument(properties, null, null);

        Property<String> descriptionProperty = doc.getProperty("cm:description");
        assertNotNull(descriptionProperty);
        assertEquals(descriptionValue1, descriptionProperty.getFirstValue());

        assertTrue(doc instanceof AlfrescoDocument);
        AlfrescoDocument alfDoc = (AlfrescoDocument) doc;

        assertTrue(alfDoc.hasAspect("P:cm:titled"));
        assertFalse(alfDoc.hasAspect("P:cm:taggable"));
        assertEquals(1, alfDoc.getAspects().size());

        ObjectType titledAspectType = alfDoc.findAspect("cm:description");
        assertNotNull(titledAspectType);
        assertEquals("P:cm:titled", titledAspectType.getId());
        assertTrue(alfDoc.hasAspect(titledAspectType));

        // update
        properties.clear();
        properties.put(PropertyIds.NAME, "test2");
        properties.put("cm:description", descriptionValue2);

        doc.updateProperties(properties);

        descriptionProperty = doc.getProperty("cm:description");
        assertNotNull(descriptionProperty);
        assertEquals(descriptionValue2, descriptionProperty.getFirstValue());

        // add aspect
        alfDoc.addAspect("P:cm:taggable");

        assertTrue(alfDoc.hasAspect("P:cm:titled"));
        assertTrue(alfDoc.hasAspect(session.getTypeDefinition("P:cm:titled")));
        assertTrue(alfDoc.hasAspect("P:cm:taggable"));
        assertTrue(alfDoc.hasAspect(session.getTypeDefinition("P:cm:taggable")));
        assertEquals(2, alfDoc.getAspects().size());

        // remove aspect
        alfDoc.removeAspect("P:cm:titled");

        assertFalse(alfDoc.hasAspect("P:cm:titled"));
        assertTrue(alfDoc.hasAspect("P:cm:taggable"));
        assertEquals(1, alfDoc.getAspects().size());

        assertNull(doc.getProperty("cm:description"));

        // add it again
        alfDoc.addAspect(titledAspectType);

        assertTrue(alfDoc.hasAspect(titledAspectType));
        assertNotNull(doc.getProperty("cm:description"));

        // remove it again
        alfDoc.removeAspect(titledAspectType);

        assertFalse(alfDoc.hasAspect(titledAspectType));
        assertNull(doc.getProperty("cm:description"));

        // delete
        alfDoc.delete(true);
    }

    public void testTransientDocument()
    {
        String descriptionValue1 = "Beschreibung";
        String descriptionValue2 = "My Description";
        String authorValue = "Mr JUnit Test";

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, "test1");
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document,P:cm:titled,P:app:inlineeditable");
        properties.put("cm:description", descriptionValue1);
        properties.put("app:editInline", true);

        Document doc = session.getRootFolder().createDocument(properties, null, null);

        // get transient document
        TransientDocument tDoc = doc.getTransientDocument();

        Property<String> descriptionProperty = tDoc.getProperty("cm:description");
        assertNotNull(descriptionProperty);
        assertEquals(descriptionValue1, descriptionProperty.getFirstValue());

        TransientAlfrescoDocument taDoc = (TransientAlfrescoDocument) tDoc;
        taDoc.addAspect("P:cm:author");

        tDoc.setPropertyValue("cm:description", descriptionValue2);
        tDoc.setPropertyValue("app:editInline", false);
        tDoc.setPropertyValue("cm:author", authorValue);

        // save and reload
        ObjectId id = tDoc.save();
        Document doc2 = (Document) session.getObject(id);
        doc2.refresh();
        TransientDocument tDoc2 = doc2.getTransientDocument();

        descriptionProperty = tDoc2.getProperty("cm:description");
        assertNotNull(descriptionProperty);
        assertEquals(descriptionValue2, descriptionProperty.getFirstValue());

        Property<String> authorProperty = tDoc2.getProperty("cm:author");
        assertNotNull(authorProperty);
        assertEquals(authorValue, authorProperty.getFirstValue());

        TransientAlfrescoDocument taDoc2 = (TransientAlfrescoDocument) tDoc2;

        assertTrue(taDoc2.hasAspect("P:cm:titled"));
        taDoc2.removeAspect("P:cm:titled");
        assertFalse(taDoc2.hasAspect("P:cm:titled"));

        // save and reload
        taDoc2.save();
        Document doc3 = (Document) session.getObject(id);
        doc3.refresh();

        assertNull(doc3.getProperty("cm:description"));

        // delete
        doc2.delete(true);
    }

    public void testEXIFAspect()
    {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, "exif.test");
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");

        AlfrescoDocument doc = (AlfrescoDocument) session.getRootFolder().createDocument(properties, null, null);

        doc.addAspect("P:exif:exif");

        BigDecimal xResolution = new BigDecimal("1234567890.123456789");
        BigDecimal yResolution = new BigDecimal("0.000000000000000001");
        GregorianCalendar dateTimeOriginal = new GregorianCalendar(2011, 01, 01, 10, 12, 30);
        dateTimeOriginal.setTimeZone(TimeZone.getTimeZone("GMT"));

        boolean flash = true;
        int pixelXDimension = 1024;
        int pixelYDimension = 512;

        properties = new HashMap<String, Object>();
        properties.put("exif:xResolution", xResolution);
        properties.put("exif:yResolution", yResolution);
        properties.put("exif:dateTimeOriginal", dateTimeOriginal);
        properties.put("exif:flash", flash);
        properties.put("exif:pixelXDimension", pixelXDimension);
        properties.put("exif:pixelYDimension", pixelYDimension);

        doc.updateProperties(properties);

        doc.refresh();

        assertEquals(dateTimeOriginal.getTimeInMillis(),
                ((GregorianCalendar) doc.getPropertyValue("exif:dateTimeOriginal")).getTimeInMillis());
        assertEquals(flash, doc.getPropertyValue("exif:flash"));
        assertEquals(BigInteger.valueOf(pixelXDimension), doc.getPropertyValue("exif:pixelXDimension"));
        assertEquals(BigInteger.valueOf(pixelYDimension), doc.getPropertyValue("exif:pixelYDimension"));

        // delete
        doc.delete(true);
    }

    public void testCheckIn()
    {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, "checkin.test");
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document, P:cm:titled");
        properties.put("cm:description", "desc1");

        AlfrescoDocument doc = (AlfrescoDocument) session.getRootFolder().createDocument(properties, null, null);

        ObjectId pwcId = doc.checkOut();
        assertNotNull(pwcId);

        AlfrescoDocument pwc = (AlfrescoDocument) session.getObject(pwcId);
        assertNotNull(pwc);

        assertEquals("desc1", pwc.getPropertyValue("cm:description"));

        properties = new HashMap<String, Object>();
        properties.put("cm:description", "desc2");

        ObjectId newDocId = pwc.checkIn(true, properties, null, null);
        assertNotNull(newDocId);

        AlfrescoDocument newDoc = (AlfrescoDocument) session.getObject(newDocId);
        newDoc.refresh();
        assertNotNull(newDoc);

        assertEquals("desc2", newDoc.getPropertyValue("cm:description"));

        // delete
        newDoc.delete(true);
    }
}
