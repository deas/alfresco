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
package org.alfresco.web.forms.xforms;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Vector;

import junit.framework.AssertionFailedError;

import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.util.BaseTest;
import org.alfresco.util.XMLUtil;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chiba.xml.events.DOMEventNames;
import org.chiba.xml.events.XFormsEventNames;
import org.chiba.xml.events.XMLEvent;
import org.chiba.xml.ns.NamespaceConstants;
import org.chiba.xml.xforms.ChibaBean;
import org.chiba.xml.xforms.config.Config;
import org.chiba.xml.xforms.exception.XFormsException;
import org.springframework.extensions.config.source.ClassPathConfigSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.xml.sax.SAXException;

/**
 * JUnit tests to exercise the the schema to xforms converter
 * 
 * @author ariel backenroth
 */
public class Schema2XFormsTest 
   extends BaseTest
{

   private final static Log LOGGER = LogFactory.getLog(Schema2XFormsTest.class);

   public void testOneStringTestWithEmptyInstanceDocument()
      throws Exception
   {
      final Document schemaDocument = this.loadTestResourceDocument("xforms/unit-tests/one-string-test.xsd");
      final Document xformsDocument = Schema2XFormsTest.buildXForm(null, schemaDocument, "one-string-test");
      this.runXForm(xformsDocument);
      final JXPathContext xpathContext = JXPathContext.newContext(xformsDocument);
      Pointer pointer = xpathContext.getPointer("//*[@id='input_0']");
      assertNotNull(pointer);
      String s = ((Element)pointer.getNode()).getAttributeNS(NamespaceConstants.XFORMS_NS, "bind");
      assertNotNull(s);
      pointer = xpathContext.getPointer("//*[@id='" + s + "']");
      assertNotNull(pointer);
      assertEquals("true()", ((Element)pointer.getNode()).getAttributeNS(NamespaceConstants.XFORMS_NS, "required"));
      pointer = xpathContext.getPointer("//" + NamespaceConstants.XFORMS_PREFIX + ":instance[@id='instance_0']/one-string-test/string");
      assertNotNull(pointer);
      assertEquals("default-value", ((Element)pointer.getNode()).getTextContent());
   }

   public void testOneStringTestWithInstanceDocument()
      throws Exception
   {
      final Document instanceDocument = XMLUtil.parse("<one-string-test><string>test</string></one-string-test>");
      final Document schemaDocument = this.loadTestResourceDocument("xforms/unit-tests/one-string-test.xsd");
      final Document xformsDocument = Schema2XFormsTest.buildXForm(instanceDocument, schemaDocument, "one-string-test");
      this.runXForm(xformsDocument);
      final JXPathContext xpathContext = JXPathContext.newContext(xformsDocument);
      Pointer pointer = xpathContext.getPointer("//*[@id='input_0']");
      assertNotNull(pointer);
      String s = ((Element)pointer.getNode()).getAttributeNS(NamespaceConstants.XFORMS_NS, "bind");
      pointer = xpathContext.getPointer("//*[@id='" + s + "']");
      assertNotNull(pointer);
      assertEquals("true()", ((Element)pointer.getNode()).getAttributeNS(NamespaceConstants.XFORMS_NS, "required"));
      pointer = xpathContext.getPointer("//" + NamespaceConstants.XFORMS_PREFIX + ":instance[@id='instance_0']/one-string-test/string");
      assertNotNull(pointer);
      assertEquals("test", ((Element)pointer.getNode()).getTextContent());
   }

   public void testNumbers()
      throws Exception
   {
      final Document schemaDocument = this.loadTestResourceDocument("xforms/unit-tests/number-test.xsd");
      final Document xformsDocument = Schema2XFormsTest.buildXForm(null, schemaDocument, "number-test");
      System.err.println("generated xform " + XMLUtil.toString(xformsDocument));
      final Element[] repeatedNumbers = Schema2XFormsTest.resolveXFormsControl(xformsDocument, "/number-test/repeated_numbers");
      final ChibaBean chibaBean = this.runXForm(xformsDocument);
      try
      {
         chibaBean.dispatch(repeatedNumbers[0].getAttribute("id") + "-insert_before", DOMEventNames.ACTIVATE); 
         fail("expected to reproduce WCM-778");
      }
      catch (XFormsException bindingIssue)
      {
         // tracked as WCM-778
      }
   }

   public void testRepeatConstraintsTest()
      throws Exception
   {
      final Document schemaDocument = this.loadTestResourceDocument("xforms/unit-tests/repeat-constraints-test.xsd");
      final Document xformsDocument = Schema2XFormsTest.buildXForm(null, schemaDocument, "repeat-constraints-test");
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/one-to-inf", 
                                               new SchemaUtil.Occurrence(1, SchemaUtil.Occurrence.UNBOUNDED));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/zero-to-inf", 
                                               new SchemaUtil.Occurrence(0, SchemaUtil.Occurrence.UNBOUNDED));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/one-to-five", 
                                               new SchemaUtil.Occurrence(1, 5));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/three-to-five", 
                                               new SchemaUtil.Occurrence(3, 5));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/zero-to-five", 
                                               new SchemaUtil.Occurrence(0, 5));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/referenced-string", 
                                               new SchemaUtil.Occurrence(1, SchemaUtil.Occurrence.UNBOUNDED));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/nested-outer-zero-to-inf", 
                                               new SchemaUtil.Occurrence(0, SchemaUtil.Occurrence.UNBOUNDED));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/nested-outer-zero-to-inf/nested-zero-to-inf-inner-zero-to-inf", 
                                               new SchemaUtil.Occurrence(0, SchemaUtil.Occurrence.UNBOUNDED));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/nested-outer-zero-to-inf/nested-zero-to-inf-inner-one-to-inf", 
                                               new SchemaUtil.Occurrence(1, SchemaUtil.Occurrence.UNBOUNDED));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/nested-outer-one-to-inf", 
                                               new SchemaUtil.Occurrence(1, SchemaUtil.Occurrence.UNBOUNDED));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/nested-outer-one-to-inf/nested-one-to-inf-inner-zero-to-inf", 
                                               new SchemaUtil.Occurrence(0, SchemaUtil.Occurrence.UNBOUNDED));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/nested-outer-one-to-inf/nested-one-to-inf-inner-one-to-inf", 
                                               new SchemaUtil.Occurrence(1, SchemaUtil.Occurrence.UNBOUNDED));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/nested-outer-three-to-five", 
                                               new SchemaUtil.Occurrence(3, 5));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/nested-outer-three-to-five/nested-three-to-five-inner-zero-to-inf", 
                                               new SchemaUtil.Occurrence(0, SchemaUtil.Occurrence.UNBOUNDED));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/nested-outer-three-to-five/nested-three-to-five-inner-one-to-inf", 
                                               new SchemaUtil.Occurrence(1, SchemaUtil.Occurrence.UNBOUNDED));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/nested-outer-outer-three-to-inf",
                                               new SchemaUtil.Occurrence(3, SchemaUtil.Occurrence.UNBOUNDED));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/nested-outer-outer-three-to-inf/nested-outer-inner-five-to-inf",
                                               new SchemaUtil.Occurrence(5, SchemaUtil.Occurrence.UNBOUNDED));
      Schema2XFormsTest.assertRepeatProperties(xformsDocument, 
                                               "/repeat-constraints-test/nested-outer-outer-three-to-inf/nested-outer-inner-five-to-inf/nested-inner-inner-seven-to-inf",
                                               new SchemaUtil.Occurrence(7, SchemaUtil.Occurrence.UNBOUNDED));
      this.runXForm(xformsDocument);
   }

   public void testRootElementWithExtension()
      throws Exception
   {
      final Document schemaDocument = this.loadTestResourceDocument("xforms/unit-tests/root-element-with-extension-test.xsd");
      Document xformsDocument = Schema2XFormsTest.buildXForm(null, schemaDocument, "without-extension-test");
      this.runXForm(xformsDocument);
      assertEquals(3, xformsDocument.getElementsByTagNameNS(NamespaceConstants.XFORMS_NS, "input").getLength());
      
      try
      {
         xformsDocument = Schema2XFormsTest.buildXForm(null, schemaDocument, "with-extension-test");
         fail("expected failure creating xform with root element with-extension-test in schema " + XMLUtil.toString(schemaDocument));
      }
      catch (FormBuilderException fbe)
      {
         LOGGER.debug("got expected exception " + fbe.getMessage());
      }
   }

   public void testSwitch()
      throws Exception
   {
      final Document schemaDocument = this.loadTestResourceDocument("xforms/unit-tests/switch-test.xsd");
      final Document xformsDocument = Schema2XFormsTest.buildXForm(null, schemaDocument, "switch-test");
      this.runXForm(xformsDocument);
//      assertEquals(3, xformsDocument.getElementsByTagNameNS(NamespaceConstants.XFORMS_NS, "input").getLength());
//      
//      try
//      {
//         xformsDocument = Schema2XFormsTest.buildXForm(null, schemaDocument, "with-extension-test");
//         fail("expected failure creating xform with root element with-extension-test in schema " + XMLUtil.toString(schemaDocument));
//      }
//      catch (FormBuilderException fbe)
//      {
//      }
   }

   public void testDerivedType()
      throws Exception
   {
      final Document schemaDocument = this.loadTestResourceDocument("xforms/unit-tests/derived-type-test.xsd");
      final Document xformsDocument = Schema2XFormsTest.buildXForm(null, schemaDocument, "derived-type-test");
      this.runXForm(xformsDocument);
      LOGGER.debug("generated xforms " + XMLUtil.toString(xformsDocument));
      assertBindProperties(xformsDocument,
                           "/derived-type-test/raw-normalized-string", 
                           "normalizedString",
                           "normalizedString");
      assertControlProperties(xformsDocument,
                              "/derived-type-test/raw-normalized-string", 
                              NamespaceConstants.XFORMS_PREFIX + ":input");
      assertBindProperties(xformsDocument,
                           "/derived-type-test/non-empty-normalized-string", 
                           "non-empty-normalized-string-type",
                           "normalizedString");
      assertControlProperties(xformsDocument,
                              "/derived-type-test/non-empty-normalized-string", 
                              NamespaceConstants.XFORMS_PREFIX + ":input");
      assertBindProperties(xformsDocument,
                           "/derived-type-test/raw-string", 
                           "string",
                           "string");
      assertControlProperties(xformsDocument,
                              "/derived-type-test/raw-string", 
                              NamespaceConstants.XFORMS_PREFIX + ":textarea");
      assertBindProperties(xformsDocument,
                           "/derived-type-test/non-empty-string", 
                           "non-empty-string-type",
                           "string");
      assertControlProperties(xformsDocument,
                              "/derived-type-test/non-empty-string", 
                              NamespaceConstants.XFORMS_PREFIX + ":textarea");
      assertBindProperties(xformsDocument,
                           "/derived-type-test/raw-any-uri", 
                           "anyURI",
                           "anyURI");
      assertControlProperties(xformsDocument,
                              "/derived-type-test/raw-any-uri", 
                              NamespaceConstants.XFORMS_PREFIX + ":upload");
      assertBindProperties(xformsDocument,
                           "/derived-type-test/non-empty-any-uri", 
                           "non-empty-any-uri-type",
                           "anyURI");
      assertControlProperties(xformsDocument,
                              "/derived-type-test/non-empty-any-uri", 
                              NamespaceConstants.XFORMS_PREFIX + ":upload");
      assertBindProperties(xformsDocument,
                           "/derived-type-test/raw-decimal", 
                           "decimal",
                           "decimal");
      assertControlProperties(xformsDocument,
                              "/derived-type-test/raw-decimal", 
                              NamespaceConstants.XFORMS_PREFIX + ":input");
      try
      {
         assertBindProperties(xformsDocument, 
                              "/derived-type-test/non-zero-decimal", 
                              "non-zero-decimal-type",
                              "decimal");
         fail("expected union type non-zero-decimal to fail");
      }
      catch (AssertionFailedError ignore)
      {
      }
      assertControlProperties(xformsDocument,
                              "/derived-type-test/non-zero-decimal", 
                              NamespaceConstants.XFORMS_PREFIX + ":input");
      assertBindProperties(xformsDocument, 
                           "/derived-type-test/raw-positive-integer", 
                           "positiveInteger",
                           "positiveInteger");
      Element control = assertControlProperties(xformsDocument,
                                                "/derived-type-test/raw-positive-integer", 
                                                NamespaceConstants.XFORMS_PREFIX + ":input");
      assertEquals(0, Integer.parseInt(control.getAttributeNS(NamespaceService.ALFRESCO_URI, "fractionDigits")));

      assertBindProperties(xformsDocument, 
                           "/derived-type-test/one-to-ten-positive-integer", 
                           "one-to-ten-positive-integer-type",
                           "positiveInteger");
      control = assertControlProperties(xformsDocument,
                                        "/derived-type-test/one-to-ten-positive-integer", 
                                        NamespaceConstants.XFORMS_PREFIX + ":range");
      assertEquals(1, Integer.parseInt(control.getAttributeNS(NamespaceConstants.XFORMS_NS, "start")));
      assertEquals(10, Integer.parseInt(control.getAttributeNS(NamespaceConstants.XFORMS_NS, "end")));
      assertEquals(0, Integer.parseInt(control.getAttributeNS(NamespaceService.ALFRESCO_URI, "fractionDigits")));

      assertBindProperties(xformsDocument, 
                           "/derived-type-test/raw-boolean", 
                           "boolean",
                           "boolean");
      assertControlProperties(xformsDocument,
                              "/derived-type-test/raw-boolean", 
                              NamespaceConstants.XFORMS_PREFIX + ":select1");
      assertBindProperties(xformsDocument, 
                           "/derived-type-test/always-true-boolean", 
                           "always-true-boolean-type",
                           "boolean");
      assertControlProperties(xformsDocument,
                              "/derived-type-test/always-true-boolean", 
                              NamespaceConstants.XFORMS_PREFIX + ":select1");
      try
      {
         assertBindProperties(xformsDocument, 
                              "/derived-type-test/raw-any-type", 
                              "anyType",
                              "anyType");
         fail("expected unexpected behavior for anyType");
      }
      catch (AssertionFailedError ignore)
      {
      }
      assertControlProperties(xformsDocument,
                              "/derived-type-test/raw-any-type", 
                              NamespaceConstants.XFORMS_PREFIX + ":textarea");
   }

   public void testRecursive()
      throws Exception
   {
      final Document schemaDocument = this.loadTestResourceDocument("xforms/unit-tests/recursive-test.xsd");
      Document xformsDocument = Schema2XFormsTest.buildXForm(null, schemaDocument, "non-recursive-test");
      this.runXForm(xformsDocument);
      try
      {
         xformsDocument = Schema2XFormsTest.buildXForm(null, schemaDocument, "recursive-test");
         fail("expected failure creating xform with recursive element definition root element recursive-test in schema " + XMLUtil.toString(schemaDocument));
      }
      catch (FormBuilderException fbe)
      {
         LOGGER.debug("got expected exception " + fbe.getMessage());
      }
      try
      {
         xformsDocument = Schema2XFormsTest.buildXForm(null, schemaDocument, "nested-recursive-test");
         fail("expected failure creating xform with recursive element definition root element nested-recursive-test in schema " + XMLUtil.toString(schemaDocument));
      }
      catch (FormBuilderException fbe)
      {
         LOGGER.debug("got expected exception " + fbe.getMessage());
      }
   }

   public void testAnnotation()
      throws Exception
   {
      final Document schemaDocument = this.loadTestResourceDocument("xforms/unit-tests/annotation-test.xsd");
      Document xformsDocument = Schema2XFormsTest.buildXForm(null, schemaDocument, "annotation-test");
      this.runXForm(xformsDocument);
      System.err.println("generated xform " + XMLUtil.toString(xformsDocument));
      Element control = assertControlProperties(xformsDocument,
                                                "/annotation-test/upload_in_root",
                                                NamespaceConstants.XFORMS_PREFIX + ":upload");
      assertEquals("upload_in_root", control.getAttributeNS(NamespaceConstants.XFORMS_NS, "appearance"));
      control = assertControlProperties(xformsDocument,
                                        "/annotation-test/string_in_root",
                                        NamespaceConstants.XFORMS_PREFIX + ":textarea");
      assertEquals("string_in_root", control.getAttributeNS(NamespaceConstants.XFORMS_NS, "appearance"));

      control = assertControlProperties(xformsDocument,
                                        "/annotation-test/struct_1/upload_in_base",
                                        NamespaceConstants.XFORMS_PREFIX + ":upload");
      assertEquals("upload_in_base", control.getAttributeNS(NamespaceConstants.XFORMS_NS, "appearance"));
      control = assertControlProperties(xformsDocument,
                                        "/annotation-test/struct_1/string_in_base",
                                        NamespaceConstants.XFORMS_PREFIX + ":textarea");
      assertEquals("string_in_base", control.getAttributeNS(NamespaceConstants.XFORMS_NS, "appearance"));

      control = assertControlProperties(xformsDocument,
                                        "/annotation-test/struct_1/upload_in_struct",
                                        NamespaceConstants.XFORMS_PREFIX + ":upload");
      assertEquals("upload_in_struct", control.getAttributeNS(NamespaceConstants.XFORMS_NS, "appearance"));
      control = assertControlProperties(xformsDocument,
                                        "/annotation-test/struct_1/string_in_struct",
                                        NamespaceConstants.XFORMS_PREFIX + ":textarea");
      assertEquals("string_in_struct", control.getAttributeNS(NamespaceConstants.XFORMS_NS, "appearance"));
   }

   public void testConstraint()
      throws Exception
   {
      final Document schemaDocument = this.loadTestResourceDocument("xforms/unit-tests/constraint-test.xsd");
      Document xformsDocument = Schema2XFormsTest.buildXForm(null, schemaDocument, "constraint-test");
      final ChibaBean chibaBean = this.runXForm(xformsDocument);
      final LinkedList<XMLEvent> events = new LinkedList<XMLEvent>();
      final EventListener el = new EventListener()
      {
         public void handleEvent(final Event e)
         {
            events.add((XMLEvent)e);
         }
      };
      ((EventTarget)chibaBean.getXMLContainer().getDocumentElement()).addEventListener(XFormsEventNames.VALID, el, true);
      ((EventTarget)chibaBean.getXMLContainer().getDocumentElement()).addEventListener(XFormsEventNames.INVALID, el, true);
      ((EventTarget)chibaBean.getXMLContainer().getDocumentElement()).addEventListener(XFormsEventNames.SUBMIT_DONE, el, true);
      ((EventTarget)chibaBean.getXMLContainer().getDocumentElement()).addEventListener(XFormsEventNames.SUBMIT_ERROR, el, true);

      Element e = Schema2XFormsTest.resolveXFormsControl(xformsDocument, "/constraint-test/zip-pattern")[0];
      chibaBean.updateControlValue(e.getAttribute("id"), "not a zip");
      assertEquals(1, events.size());
      assertEquals(XFormsEventNames.INVALID, events.get(0).getType());
      events.clear();

      chibaBean.updateControlValue(e.getAttribute("id"), "94110");
      assertEquals(1, events.size());
      assertEquals(XFormsEventNames.VALID, events.get(0).getType());
      events.clear();

      e = Schema2XFormsTest.resolveXFormsControl(xformsDocument, "/constraint-test/email-pattern")[0];
      chibaBean.updateControlValue(e.getAttribute("id"), "iamnotanemailaddress");
      assertEquals(1, events.size());
      assertEquals(XFormsEventNames.INVALID, events.get(0).getType());
      events.clear();

      chibaBean.updateControlValue(e.getAttribute("id"), "ariel.backenroth@alfresco.org");
      assertEquals(1, events.size());
      assertEquals(XFormsEventNames.VALID, events.get(0).getType());
      events.clear();

      Element[] controls = Schema2XFormsTest.resolveXFormsControl(xformsDocument, "/constraint-test/repeated-zip-pattern/.");
      assertEquals(3 /* 2 actual + prototype */, controls.length);
      Element[] repeat = Schema2XFormsTest.resolveXFormsControl(xformsDocument, "/constraint-test/repeated-zip-pattern");
      assertEquals(4 /* 1 repeat + 3 triggers */, repeat.length);
      
      final Element[] bindForRepeat = Schema2XFormsTest.resolveBind(xformsDocument, "/constraint-test/repeated-zip-pattern");
      assertEquals(bindForRepeat[bindForRepeat.length - 1].getAttribute("id"), repeat[0].getAttributeNS(NamespaceConstants.XFORMS_NS, "bind"));
      for (int i = 1; i <= Integer.parseInt(bindForRepeat[bindForRepeat.length - 1].getAttributeNS(NamespaceConstants.XFORMS_NS, "minOccurs")); i++)
      {
         chibaBean.updateRepeatIndex(repeat[0].getAttribute("id"), i);
         chibaBean.updateControlValue(controls[controls.length - 1].getAttribute("id"), "notavalidzip");
      }
      //     assertEquals("unexpected events " + events, controls.length, events.size());
      for (final Event event : events)
      {
         assertEquals(XFormsEventNames.INVALID, event.getType());
      }
      events.clear();

      chibaBean.dispatch("submit", DOMEventNames.ACTIVATE);
      assertEquals(1, events.size());
      assertEquals(XFormsEventNames.SUBMIT_ERROR, events.get(0).getType());
      events.clear();

      for (final Element c : controls)
      {
         chibaBean.updateControlValue(c.getAttribute("id"), "07666");
      }
//      assertEquals("unexpected events " + events, controls.length, events.size());
      for (final Event event : events)
      {
         assertEquals(XFormsEventNames.VALID, event.getType());
      }
      events.clear();

      chibaBean.dispatch("submit", DOMEventNames.ACTIVATE);
      assertEquals(1, events.size());
      assertEquals(XFormsEventNames.SUBMIT_DONE, events.get(0).getType());
   }

   private static void assertRepeatProperties(final Document xformsDocument, 
                                              final String nodeset, 
                                              final SchemaUtil.Occurrence o)
   {
      final Element[] bindElements = Schema2XFormsTest.resolveBind(xformsDocument, nodeset);
      assertNotNull("unable to resolve bind for nodeset " + nodeset, bindElements);
      assertFalse("unable to resolve bind for nodeset " + nodeset, 0 == bindElements.length);
      final Element nodesetBindElement = bindElements[bindElements.length - 1];
      assertEquals("unexpected minimum value for nodeset " + nodeset,
                   o.minimum, 
                   Integer.parseInt(nodesetBindElement.getAttributeNS(NamespaceConstants.XFORMS_NS, "minOccurs")));
      if (o.isUnbounded())
      {
         assertEquals("unexpected maximum value for nodeset " + nodeset,
                      "unbounded", 
                      nodesetBindElement.getAttributeNS(NamespaceConstants.XFORMS_NS, "maxOccurs"));
      }
      else
      {
         assertEquals("unexpected maximum value for nodeset " + nodeset,
                      o.maximum, 
                      Integer.parseInt(nodesetBindElement.getAttributeNS(NamespaceConstants.XFORMS_NS, "maxOccurs")));
      }
      assertEquals("unexpected required value for nodeset " + nodeset,
                   (o.minimum != 0 && nodesetBindElement.hasAttributeNS(NamespaceConstants.XFORMS_NS, "type")) + "()",
                   nodesetBindElement.getAttributeNS(NamespaceConstants.XFORMS_NS, "required"));

      JXPathContext xpathContext = JXPathContext.newContext(xformsDocument);
      String xpath = "//*[@" + NamespaceConstants.XFORMS_PREFIX + ":bind='" + nodesetBindElement.getAttribute("id") + "']";
      assertEquals(4, xpathContext.selectNodes(xpath).size());
      xpath = ("//" + NamespaceConstants.XFORMS_PREFIX + 
               ":repeat[@" + NamespaceConstants.XFORMS_PREFIX + 
               ":bind='" + nodesetBindElement.getAttribute("id") + "']");
      assertEquals(1, xpathContext.selectNodes(xpath).size());
      xpath = ("//" + NamespaceConstants.XFORMS_PREFIX + 
               ":trigger[@" + NamespaceConstants.XFORMS_PREFIX + 
               ":bind='" + nodesetBindElement.getAttribute("id") + "']");
      assertEquals(3, xpathContext.selectNodes(xpath).size());

      int nestingFactor = 1;
      for (int i = 0; i < bindElements.length - 1; i++)
      {
         final SchemaUtil.Occurrence parentO = Schema2XFormsTest.occuranceFromBind(bindElements[i]);
         if (parentO.isRepeated())
         {
            nestingFactor = nestingFactor * (1 + parentO.minimum);
         }
      }
      final Pointer instance0 = xpathContext.getPointer("//" + NamespaceConstants.XFORMS_PREFIX + ":instance[@id='instance_0']");
      assertNotNull(instance0);
      assertNotNull(instance0.getNode());
      xpathContext = xpathContext.getRelativeContext(instance0);
      xpath = nodeset.substring(1);
      assertEquals("unexpected result for instance nodeset " + xpath + " in " + instance0.getNode(), 
                   nestingFactor * (o.minimum + 1), 
                   xpathContext.selectNodes(xpath).size());
      xpath = nodeset.substring(1) + "[@" + NamespaceService.ALFRESCO_PREFIX + ":prototype='true']";
      assertEquals("unexpected result for instance prototype nodeset " + nodeset + " in " + instance0.getNode(), 
                   nestingFactor, 
                   xpathContext.selectNodes(xpath).size());
   }

   private static Element assertBindProperties(final Document xformsDocument,
                                               final String nodeset,
                                               final String schemaType,
                                               final String builtInType)
   {
      final Element[] binds = Schema2XFormsTest.resolveBind(xformsDocument, nodeset);
      assertEquals("unexpected type for nodeset " + nodeset,
                   schemaType, 
                   binds[binds.length - 1].getAttributeNS(NamespaceConstants.XFORMS_NS, "type"));
      assertEquals("unexpected built in type for nodeset " + nodeset,
                   builtInType, 
                   binds[binds.length - 1].getAttributeNS(NamespaceService.ALFRESCO_URI, "builtInType"));
      return binds[binds.length - 1];
   }

   private static Element assertControlProperties(final Document xformsDocument,
                                                  final String nodeset,
                                                  final String controlType)
   {
      final Element[] controls = Schema2XFormsTest.resolveXFormsControl(xformsDocument, nodeset);
      assertEquals("unexpected xforms control for " + nodeset,
                   controlType,
                   controls[controls.length - 1].getNodeName());
      return controls[controls.length - 1];
   }

   /**
    * Returns the resolved bind and all parents binds for the nodeset.
    */
   private static Element[] resolveBind(final Document xformsDocument, final String nodeset)
   {
      JXPathContext xpathContext = JXPathContext.newContext(xformsDocument);
      assertNotNull(nodeset);
      assertEquals('/', nodeset.charAt(0));
      final String rootNodePath = nodeset.replaceFirst("(\\/[^\\/]+).*", "$1");
      assertNotNull(rootNodePath);
      String xpath = ("//" + NamespaceConstants.XFORMS_PREFIX + 
                      ":bind[@" + NamespaceConstants.XFORMS_PREFIX + 
                      ":nodeset='" + rootNodePath + "']");
      Pointer pointer = xpathContext.getPointer(xpath);
      assertNotNull("unable to resolve xpath for root node " + xpath, pointer);
      assertNotNull("unable to resolve xpath for root node " + xpath, pointer.getNode());
      if (nodeset.equals(rootNodePath))
      {
         return new Element[] { (Element)pointer.getNode() };
      }
      xpathContext = xpathContext.getRelativeContext(pointer);
      // substring the path to the next slash and split it
      final LinkedList<Element> result = new LinkedList<Element>();
      result.add((Element)pointer.getNode());
      for (String p : nodeset.substring(rootNodePath.length() + 1).split("/"))
      {
         xpath = NamespaceConstants.XFORMS_PREFIX + ":bind[starts-with(@" + NamespaceConstants.XFORMS_PREFIX + ":nodeset, '" + p + "')]";
         pointer = xpathContext.getPointer(xpath);
         assertNotNull("unable to resolve path " + xpath + 
                       " on bind with nodeset " + result.getLast().getAttributeNS(NamespaceConstants.XFORMS_NS, "nodeset"),
                       pointer);
         assertNotNull("unable to resolve path " + xpath + 
                       " on bind with nodeset " + result.getLast().getAttributeNS(NamespaceConstants.XFORMS_NS, "nodeset"),
                       pointer.getNode());
         xpathContext = xpathContext.getRelativeContext(pointer);
         result.add((Element)pointer.getNode());
      }
      return (Element[])result.toArray(new Element[result.size()]);
   }

   private static Element[] resolveXFormsControl(final Document xformsDocument,
                                                 final String nodeset)
   {
      final Element[] binds = Schema2XFormsTest.resolveBind(xformsDocument, nodeset);
      assertNotNull(binds);
      assertFalse(binds.length == 0);
      final String bindId = binds[binds.length - 1].getAttribute("id");
      
      final JXPathContext xpathContext = JXPathContext.newContext(xformsDocument);
      String xpath = "//*[@" + NamespaceConstants.XFORMS_PREFIX + ":bind='" + bindId + "']";
      return (Element[])xpathContext.selectNodes(xpath).toArray(new Element[0]);
   }

   private Document loadTestResourceDocument(final String path)
      throws IOException, SAXException
   {
      ClassPathConfigSource source = new ClassPathConfigSource(path);
      return XMLUtil.parse(source.getInputStream(path));
   }

   private ChibaBean runXForm(final Document xformsDocument)
      throws Exception
   {
      final ChibaBean chibaBean = new ChibaBean();
      chibaBean.setXMLContainer(xformsDocument);
      chibaBean.init();
      return chibaBean;
   }

   private static Document buildXForm(final Document instanceDocument,
                                      final Document schemaDocument,
                                      final String rootElementName)
      throws FormBuilderException
   {
      final Schema2XForms s2xf = new Schema2XForms("/test_action",
                                                   Schema2XForms.SubmitMethod.POST,
                                                   "echo://fake.base.url", true);
      return s2xf.buildXForm(instanceDocument, 
                             schemaDocument, 
                             rootElementName, 
                             new ResourceBundle()
                             {
                                public Object handleGetObject(final String key)
                                {
                                   if (key == null)
                                   {
                                      throw new NullPointerException();
                                   }
                                   return null;
                                }
                                
                                public Enumeration<String> getKeys()
                                {
                                   return new Vector<String>().elements();
                                }
                             }).getFirst();
   }

   private static SchemaUtil.Occurrence occuranceFromBind(final Element bindElement)
   {
      return new SchemaUtil.Occurrence(bindElement.hasAttributeNS(NamespaceConstants.XFORMS_NS, "minOccurs")
                                       ? Integer.parseInt(bindElement.getAttributeNS(NamespaceConstants.XFORMS_NS, "minOccurs"))
                                       : 1,
                                       bindElement.hasAttributeNS(NamespaceConstants.XFORMS_NS, "maxOccurs")
                                       ? ("unbounded".equals(bindElement.getAttributeNS(NamespaceConstants.XFORMS_NS, "maxOccurs"))
                                          ? SchemaUtil.Occurrence.UNBOUNDED
                                          : Integer.parseInt(bindElement.getAttributeNS(NamespaceConstants.XFORMS_NS, "maxOccurs")))
                                       : 1);
   }
}
