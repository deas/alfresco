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
package org.alfresco.repo.dictionary;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.cache.MemoryCache;
import org.alfresco.repo.dictionary.DictionaryDAOImpl.DictionaryRegistry;
import org.alfresco.repo.dictionary.NamespaceDAOImpl.NamespaceRegistry;
import org.alfresco.repo.tenant.SingleTServiceImpl;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.namespace.QName;

public class DiffModelTest extends TestCase
{
    public static final String MODEL1_XML = 
        "<model name=\"test1:model1\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <description>Another description</description>" +
        "   <author>Alfresco</author>" +
        "   <published>2007-08-01</published>" +
        "   <version>1.0</version>" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test1/1.0\" prefix=\"test1\"/>" +
        "   </namespaces>" +

        "   <types>" +
       
        "      <type name=\"test1:type1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop1\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop2\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </type>" +
        
        "      <type name=\"test1:type2\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 2</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop3\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop4\">" +
        "              <type>d:int</type>" +
        "           </property>" +          
        "        </properties>" +
        "      </type>" +
        
        "      <type name=\"test1:type3\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 3</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop5\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop6\">" +
        "              <type>d:int</type>" +
        "           </property>" +          
        "        </properties>" +
        "      </type>" +
        
        "   </types>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test1:aspect1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop9\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop10\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </aspect>" +
        
        "      <aspect name=\"test1:aspect2\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 2</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop11\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop12\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </aspect>" +
        
        "      <aspect name=\"test1:aspect3\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 3</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop13\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop14\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </aspect>" +
              
        "   </aspects>" +        
        
        "</model>";
    
    public static final String MODEL1_UPDATE1_XML = 
        "<model name=\"test1:model1\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <description>Another description</description>" +
        "   <author>Alfresco</author>" +
        "   <published>2007-08-01</published>" +
        "   <version>1.0</version>" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test1/1.0\" prefix=\"test1\"/>" +
        "   </namespaces>" +

        "   <types>" +
       
        "      <type name=\"test1:type1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop1\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop2\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </type>" +
        
        "      <type name=\"test1:type3\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 3</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop5\">" +
        "              <type>d:text</type>" +
        "           </property>" +   
        "        </properties>" +
        "      </type>" +
        
        "      <type name=\"test1:type4\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 4</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop7\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop8\">" +
        "              <type>d:int</type>" +
        "           </property>" +          
        "        </properties>" +
        "      </type>" +
        
        "   </types>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test1:aspect1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop9\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop10\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </aspect>" +
        
        "      <aspect name=\"test1:aspect3\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 3</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop13\">" +
        "              <type>d:int</type>" +
        "           </property>" + 
        "           <property name=\"test1:prop14\">" +
        "              <type>d:int</type>" +
        "           </property>" + 
        "        </properties>" +
        "      </aspect>" +
        
        "      <aspect name=\"test1:aspect4\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 4</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop15\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop16\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </aspect>" +    
        
        "   </aspects>" +        
        
        "</model>";
    
    public static final String MODEL2_XML = 
        "<model name=\"test1:model2\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <description>Another description</description>" +
        "   <author>Alfresco</author>" +
        "   <published>2007-08-01</published>" +
        "   <version>1.0</version>" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test1/1.0\" prefix=\"test1\"/>" +
        "   </namespaces>" +

        "   <types>" +
       
        "      <type name=\"test1:type1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop1\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop2\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </type>" +
        
        "   </types>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test1:aspect1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop9\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop10\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </aspect>" +
        
        "   </aspects>" +
        
        "</model>";
    
    public static final String MODEL2_EXTRA_PROPERTIES_XML = 
        "<model name=\"test1:model2\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <description>Another description</description>" +
        "   <author>Alfresco</author>" +
        "   <published>2007-08-01</published>" +
        "   <version>1.0</version>" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test1/1.0\" prefix=\"test1\"/>" +
        "   </namespaces>" +

        "   <types>" +
       
        "      <type name=\"test1:type1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop1\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop2\">" +
        "              <type>d:int</type>" +
        "           </property>" +       
        "           <property name=\"test1:prop3\">" +
        "              <type>d:date</type>" +
        "           </property>" +   
        "        </properties>" +
        "      </type>" +
        
        "   </types>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test1:aspect1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop11\">" +
        "              <type>d:boolean</type>" +
        "           </property>" +         
        "           <property name=\"test1:prop9\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop10\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </aspect>" +
        
        "   </aspects>" +
        
        "</model>";
    
    public static final String MODEL3_XML = 
        "<model name=\"test1:model3\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <description>Another description</description>" +
        "   <author>Alfresco</author>" +
        "   <published>2007-08-01</published>" +
        "   <version>1.0</version>" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test1/1.0\" prefix=\"test1\"/>" +
        "   </namespaces>" +

        "   <types>" +
       
        "      <type name=\"test1:type1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop1\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop2\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </type>" +
        
        "   </types>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test1:aspect1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop9\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop10\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </aspect>" +
        
        "   </aspects>" +
        
        "</model>";
    
    public static final String MODEL3_EXTRA_TYPES_AND_ASPECTS_XML = 
        "<model name=\"test1:model3\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <description>Another description</description>" +
        "   <author>Alfresco</author>" +
        "   <published>2007-08-01</published>" +
        "   <version>1.0</version>" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test1/1.0\" prefix=\"test1\"/>" +
        "   </namespaces>" +

        "   <types>" +
       
        "      <type name=\"test1:type1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop1\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop2\">" +
        "              <type>d:int</type>" +
        "           </property>" +       
        "        </properties>" +
        "      </type>" +
        
        "      <type name=\"test1:type2\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 2</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop3\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop4\">" +
        "              <type>d:int</type>" +
        "           </property>" +          
        "        </properties>" +
        "      </type>" +
        
        "   </types>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test1:aspect1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 1</description>" +
        "        <properties>" +     
        "           <property name=\"test1:prop9\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop10\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </aspect>" +
        
        "      <aspect name=\"test1:aspect2\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 2</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop11\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop12\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </aspect>" +
        
        "   </aspects>" +
        
        "</model>";
    
    public static final String MODEL4_XML = 
        "<model name=\"test1:model4\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <description>Another description</description>" +
        "   <author>Alfresco</author>" +
        "   <published>2007-08-01</published>" +
        "   <version>1.0</version>" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test1/1.0\" prefix=\"test1\"/>" +
        "   </namespaces>" +

        "   <types>" +
       
        "      <type name=\"test1:type1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop1\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "        </properties>" +
        "      </type>" +
        
        "   </types>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test1:aspect1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop9\">" +
        "              <type>d:text</type>" +
        "           </property>" +     
        "        </properties>" +
        "      </aspect>" +
        
        "   </aspects>" +
        
        "</model>";
    
    public static final String MODEL4_EXTRA_DEFAULT_ASPECT_XML = 
        "<model name=\"test1:model4\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <description>Another description</description>" +
        "   <author>Alfresco</author>" +
        "   <published>2007-08-01</published>" +
        "   <version>1.0</version>" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test1/1.0\" prefix=\"test1\"/>" +
        "   </namespaces>" +

        "   <types>" +
       
        "      <type name=\"test1:type1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop1\">" +
        "              <type>d:text</type>" +
        "           </property>" +  
        "        </properties>" +
        "        <mandatory-aspects>" +
        "           <aspect>test1:aspect1</aspect>" +
        "        </mandatory-aspects>" +
        "      </type>" +
        
        "   </types>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test1:aspect1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 1</description>" +
        "        <properties>" +     
        "           <property name=\"test1:prop9\">" +
        "              <type>d:text</type>" +
        "           </property>" +     
        "        </properties>" +
        "      </aspect>" +
  
        "   </aspects>" +
        
        "</model>";
    
    public static final String MODEL5_XML = 
        "<model name=\"test1:model5\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <description>Another description</description>" +
        "   <author>Alfresco</author>" +
        "   <published>2007-08-01</published>" +
        "   <version>1.0</version>" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test1/1.0\" prefix=\"test1\"/>" +
        "   </namespaces>" +

        "   <types>" +
       
        "      <type name=\"test1:type1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop1\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "        </properties>" +
        "      </type>" +
        
        "      <type name=\"test1:type2\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 2</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop3\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop4\">" +
        "              <type>d:int</type>" +
        "           </property>" +          
        "        </properties>" +
        "      </type>" +
        
        "   </types>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test1:aspect1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop9\">" +
        "              <type>d:text</type>" +
        "           </property>" +     
        "        </properties>" +
        "      </aspect>" +
        
        "      <aspect name=\"test1:aspect2\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 2</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop11\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop12\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </aspect>" +
        
        "   </aspects>" +
        
        "</model>";
    
    public static final String MODEL5_EXTRA_ASSOCIATIONS_XML =
        "<model name=\"test1:model5\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <description>Another description</description>" +
        "   <author>Alfresco</author>" +
        "   <published>2007-08-01</published>" +
        "   <version>1.0</version>" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test1/1.0\" prefix=\"test1\"/>" +
        "   </namespaces>" +

        "   <types>" +
       
        "      <type name=\"test1:type1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop1\">" +
        "              <type>d:text</type>" +
        "           </property>" +  
        "        </properties>" +
        "        <associations>" +
        "           <child-association name=\"test1:assoc1\">" +
        "               <source>" +
        "                   <mandatory>false</mandatory>" +
        "                   <many>false</many>" +
        "               </source>" +
        "               <target>" +
        "                   <class>test1:type2</class>" +
        "                   <mandatory>false</mandatory>" +
        "                   <many>false</many>" +
        "               </target>" +
        "           </child-association>" +
        "        </associations>" +
        "      </type>" +
        
        "      <type name=\"test1:type2\">" +
        "        <title>Base</title>" +
        "        <description>The Base Type 2</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop3\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop4\">" +
        "              <type>d:int</type>" +
        "           </property>" +          
        "        </properties>" +
        "      </type>" +
        
        "   </types>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test1:aspect1\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 1</description>" +
        "        <properties>" +     
        "           <property name=\"test1:prop9\">" +
        "              <type>d:text</type>" +
        "           </property>" +     
        "        </properties>" +
        "        <associations>" +
        "           <association name=\"test1:assoc2\">" +
        "               <source>" +
        "                   <role>test1:role1</role>" +
        "                   <mandatory>false</mandatory>" +
        "                   <many>true</many>" +
        "               </source>" +
        "               <target>" +
        "                   <class>test1:aspect2</class>" +
        "                   <role>test1:role2</role>" +
        "                   <mandatory>false</mandatory>" +
        "                   <many>true</many>" +
        "               </target>" +
        "           </association>" +
        "        </associations>" +
        "      </aspect>" +
  
        "      <aspect name=\"test1:aspect2\">" +
        "        <title>Base</title>" +
        "        <description>The Base Aspect 2</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop11\">" +
        "              <type>d:text</type>" +
        "           </property>" +
        "           <property name=\"test1:prop12\">" +
        "              <type>d:int</type>" +
        "           </property>" +        
        "        </properties>" +
        "      </aspect>" +
        
        "   </aspects>" +
        
        "</model>";
    
    public static final String MODEL6_XML = 
        "<model name=\"test1:model6\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <description>Another description</description>" +
        "   <author>Alfresco</author>" +
        "   <published>2007-08-01</published>" +
        "   <version>1.0</version>" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test1/1.0\" prefix=\"test1\"/>" +
        "   </namespaces>" +
        
        "   <types>" +
        
        "      <type name=\"test1:type1\">" +
        "        <title>Type1 Title</title>" +
        "        <description>Type1 Description</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop1\">" +
        "              <title>Prop1 Title</title>" +
        "              <description>Prop1 Description</description>" +
        "              <type>d:text</type>" +
        "           </property>" +
        "        </properties>" +
        "      </type>" +
        
        "   </types>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test1:aspect1\">" +
        "        <title>Aspect1 Title</title>" +
        "        <description>Aspect1 Description</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop9\">" +
        "              <title>Prop9 Title</title>" +
        "              <description>Prop9 Description</description>" +
        "              <type>d:text</type>" +
        "           </property>" +
        "        </properties>" +
        "      </aspect>" +
        
        "   </aspects>" +
        
        "</model>";
    
    public static final String MODEL6_UPDATE1_XML = 
        "<model name=\"test1:model6\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <description>Another description - UPDATE1</description>" +
        "   <author>Alfresco - UPDATE1</author>" +
        "   <published>2009-08-01</published>" +
        "   <version>2.0</version>" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test1/1.0\" prefix=\"test1\"/>" +
        "   </namespaces>" +
        
        "   <types>" +
        
        "      <type name=\"test1:type1\">" +
        "        <title>Type1 Title - UPDATE1</title>" +
        "        <description>Type1 Description - UPDATE1</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop1\">" +
        "              <title>Prop1 Title - UPDATE1</title>" +
        "              <description>Prop1 Description - UPDATE1</description>" +
        "              <type>d:text</type>" +
        "           </property>" +
        "        </properties>" +
        "      </type>" +
        
        "   </types>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test1:aspect1\">" +
        "        <title>Aspect1 Title</title>" +
        "        <description>Aspect1 Description</description>" +
        "        <properties>" +
        "           <property name=\"test1:prop9\">" +
        "              <title>Prop9 Title - UPDATE1</title>" +
        "              <description>Prop9 Description - UPDATE1</description>" +
        "              <type>d:text</type>" +
        "           </property>" +
        "        </properties>" +
        "      </aspect>" +
        
        "   </aspects>" +
        
        "</model>";
    
    
    public static final String MODEL7_XML = 
        "<model name=\"test7:model7\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test7/1.0\" prefix=\"test7\"/>" +
        "   </namespaces>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test7:aspectA\">" +
        "      </aspect>" +
        
        "      <aspect name=\"test7:aspectB\">" +
        "         <mandatory-aspects> " +
        "            <aspect>test7:aspectA</aspect> " +
        "         </mandatory-aspects> " +
        "      </aspect>" +
        
        "   </aspects>" +
        
        "</model>";
    
    public static final String MODEL7_EXTRA_PROPERTIES_MANDATORY_ASPECTS_XML = 
        "<model name=\"test7:model7\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">" +
        
        "   <imports>" +
        "      <import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\"/>" +
        "   </imports>" +
        
        "   <namespaces>" +
        "      <namespace uri=\"http://www.alfresco.org/model/test7/1.0\" prefix=\"test7\"/>" +
        "   </namespaces>" +
        
        "   <aspects>" +
        
        "      <aspect name=\"test7:aspectA\">" +
        "         <properties> " +
        "            <property name=\"test7:propA1\"> " +
        "               <title>Prop A1</title> " +
        "               <type>d:text</type> " +
        "            </property> " +
        "         </properties> " +
        "      </aspect>" +
        
        "      <aspect name=\"test7:aspectB\">" +
        "         <mandatory-aspects> " +
        "            <aspect>test7:aspectA</aspect> " +
        "         </mandatory-aspects> " +
        "      </aspect>" +
        
        "   </aspects>" +
        
        "</model>";
    
    private DictionaryDAOImpl dictionaryDAO;

    /**
     * Setup
     */
    protected void setUp() throws Exception
    {
    	// Initialise the Dictionary
        TenantService tenantService = new SingleTServiceImpl();
        
        NamespaceDAOImpl namespaceDAO = new NamespaceDAOImpl();
        namespaceDAO.setTenantService(tenantService);
        
        initNamespaceCaches(namespaceDAO);
        
        dictionaryDAO = new DictionaryDAOImpl(namespaceDAO);
        dictionaryDAO.setTenantService(tenantService);
        
        initDictionaryCaches(dictionaryDAO);
        
        
        // include Alfresco dictionary model
        List<String> bootstrapModels = new ArrayList<String>();
        bootstrapModels.add("alfresco/model/dictionaryModel.xml");

        DictionaryBootstrap bootstrap = new DictionaryBootstrap();
        bootstrap.setModels(bootstrapModels);
        bootstrap.setDictionaryDAO(dictionaryDAO);
        bootstrap.bootstrap();
    }

    private void initDictionaryCaches(DictionaryDAOImpl dictionaryDAO)
    {
        dictionaryDAO.setDictionaryRegistryCache(new MemoryCache<String, DictionaryRegistry>());
    }
    
    private void initNamespaceCaches(NamespaceDAOImpl namespaceDAO)
    {
        namespaceDAO.setNamespaceRegistryCache(new MemoryCache<String, NamespaceRegistry>());
    }
    
    public void testDeleteModel()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL1_XML.getBytes());

        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, null);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(6, modelDiffs.size());
        
        assertEquals(3, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_DELETED));
        assertEquals(3, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_DELETED));
    }
    
    @SuppressWarnings("unused")
    public void testNoExistingModelToDelete()
    {
        try
        {
            List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(null, null); 
            assertTrue("Should throw exeception that there is no previous version of the model to delete", true);
        }
        catch (AlfrescoRuntimeException e)
        {
            assertTrue("Wrong error message", e.getMessage().equals("Invalid arguments - no previous version of model to delete"));
        }
        
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL1_XML.getBytes());

        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        
        CompiledModel compiledModel = dictionaryDAO.getCompiledModel(modelName);
        
        try
        {
            List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(null, compiledModel);
            assertTrue("Should throw exeception that there is no previous version of the model to delete", true);
        }
        catch (AlfrescoRuntimeException e)
        {
            assertTrue("Wrong error message", e.getMessage().equals("Invalid arguments - no previous version of model to delete"));
        }
    }
    
    public void testNewModel()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL1_XML.getBytes());

        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(null, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(6, modelDiffs.size());
        
        assertEquals(3, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_CREATED));
        assertEquals(3, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_CREATED));
    }
    
    public void testNonIncUpdateModel()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL1_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);  
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(MODEL1_UPDATE1_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);       
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff M2ModelDiff : modelDiffs)
        {
            System.out.println(M2ModelDiff.toString());
        }   
        
        assertEquals(16, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_CREATED));
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(0, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UPDATED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_DELETED));
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_CREATED));
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(0, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UPDATED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_DELETED));
        
        assertEquals(0, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_CREATED));
        assertEquals(6, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UPDATED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_DELETED));
    }
    
    public void testIncUpdatePropertiesAdded()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL2_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(MODEL2_EXTRA_PROPERTIES_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(8, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(4, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_CREATED));
    }

    public void testIncUpdateTypesAndAspectsAdded()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL3_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);  
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(MODEL3_EXTRA_TYPES_AND_ASPECTS_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(8, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_CREATED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_CREATED));
        
        assertEquals(4, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
    }
    
    public void testIncUpdateAssociationsAdded()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL5_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(MODEL5_EXTRA_ASSOCIATIONS_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }
        
        assertEquals(12, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UPDATED_INC));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(6, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASSOCIATION, M2ModelDiff.DIFF_CREATED));
    }
    
    public void testIncUpdateTitleDescription()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL6_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(MODEL6_UPDATE1_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }
        
        assertEquals(4, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UPDATED_INC));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UPDATED_INC));
    }
    
    public void testNonIncUpdatePropertiesRemoved()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL2_EXTRA_PROPERTIES_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);  
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(MODEL2_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(8, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(4, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_DELETED));
    }
    
    public void testNonIncUpdateTypesAndAspectsRemoved()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL3_EXTRA_TYPES_AND_ASPECTS_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(MODEL3_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }
        
        assertEquals(8, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_DELETED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_DELETED));
        
        assertEquals(4, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
    }
    
    public void testNonIncUpdateDefaultAspectAdded()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL4_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(MODEL4_EXTRA_DEFAULT_ASPECT_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }
        
        assertEquals(4, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UPDATED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
    }
    
    public void testNonIncUpdateAssociationsRemoved()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL5_EXTRA_ASSOCIATIONS_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(MODEL5_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }
        
        assertEquals(12, modelDiffs.size());
        
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UPDATED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_TYPE, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(6, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_UNCHANGED));
        
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASSOCIATION, M2ModelDiff.DIFF_DELETED));
    }
    
    public void testIncUpdatePropertiesAddedToMandatoryAspect()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL7_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(MODEL7_EXTRA_PROPERTIES_MANDATORY_ASPECTS_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(3, modelDiffs.size());
        
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_CREATED));
    }
    
    public void testNonIncUpdatePropertiesRemovedFromMandatoryAspect()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(MODEL7_EXTRA_PROPERTIES_MANDATORY_ASPECTS_XML.getBytes());
        M2Model model = M2Model.createModel(byteArrayInputStream);
        QName modelName = dictionaryDAO.putModel(model);  
        CompiledModel previousVersion = dictionaryDAO.getCompiledModel(modelName);
        
        byteArrayInputStream = new ByteArrayInputStream(MODEL7_XML.getBytes());
        model = M2Model.createModel(byteArrayInputStream);
        modelName = dictionaryDAO.putModel(model);
        CompiledModel newVersion = dictionaryDAO.getCompiledModel(modelName);
        
        List<M2ModelDiff> modelDiffs = dictionaryDAO.diffModel(previousVersion, newVersion);
        
        for (M2ModelDiff modelDiff : modelDiffs)
        {
            System.out.println(modelDiff.toString());
        }   
        
        assertEquals(3, modelDiffs.size());
        
        assertEquals(2, countDiffs(modelDiffs, M2ModelDiff.TYPE_ASPECT, M2ModelDiff.DIFF_UNCHANGED));
        assertEquals(1, countDiffs(modelDiffs, M2ModelDiff.TYPE_PROPERTY, M2ModelDiff.DIFF_DELETED));
    }
    
    private int countDiffs(List<M2ModelDiff> M2ModelDiffs, String elementType, String diffType)
    {
        int count = 0;
        for (M2ModelDiff modelDiff : M2ModelDiffs)
        {
            if (modelDiff.getDiffType().equals(diffType) && modelDiff.getElementType().equals(elementType))
            {
                count++;
            }
        }
        return count;
    }
    
}

