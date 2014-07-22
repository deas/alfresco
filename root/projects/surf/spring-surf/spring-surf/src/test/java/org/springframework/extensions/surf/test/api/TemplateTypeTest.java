/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
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

package org.springframework.extensions.surf.test.api;

import org.testng.annotations.Test;
import org.springframework.extensions.surf.test.TestCaseSetup;
import org.springframework.extensions.surf.types.TemplateType;

/**
 * Tests the Surf API directly using mock objects
 * 
 * @author muzquiano
 */
public class TemplateTypeTest
{
	@Test
    public void testCRUD() throws Exception
    {
    	TemplateType templateType1 = TestCaseSetup.getObjectService().newTemplateType();
    	TestCaseSetup.getObjectService().saveObject(templateType1);
    	TemplateType templateType2 = TestCaseSetup.getObjectService().newTemplateType("templateType2");
    	TestCaseSetup.getObjectService().saveObject(templateType2);
    }
}
