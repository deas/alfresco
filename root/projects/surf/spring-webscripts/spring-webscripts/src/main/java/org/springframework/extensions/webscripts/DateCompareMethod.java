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

package org.springframework.extensions.webscripts;

import java.util.Date;
import java.util.List;

import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

/**
 * @author Kevin Roast
 * 
 * Custom FreeMarker Template language method.
 * <p>
 * Perform a test to see how two dates compare, optionally offset by a specified number of milliseconds.
 * <p>
 * Usage: 
 *    dateCompare(dateA, dateB) - 1 if dateA if greater than dateB
 *    dateCompare(dateA, dateB, millis) - 1 if dateA is greater than dateB by at least millis, else 0
 *    dateCompare(dateA, dateB, millis, test) - same as above, but the 'test' variable is one of the
 *          following strings ">", "<", "==" - greater than, less than or equal - as the test to perform.
 */
@ScriptClass 
(
        help="Perform a test to see how two dates compare, optionally offset by a specified number of\nmilliseconds.\n\nUsage: \n  dateCompare(dateA, dateB) - 1 if dateA if greater than dateB\n  dateCompare(dateA, dateB, millis) - 1 if dateA is greater than dateB by at least millis, else 0\n  dateCompare(dateA, dateB, millis, test) - same as above, but the 'test' variable is one of the\n  following strings \">\", \"<\", \"==\" - greater than, less than or equal - as the test to perform.",
        code="<#if (dateCompare(child.properties[\"cm:modified\"], date, 1000*60*60*24*7) == 1)>\n</#if>",
        types=
        {
                ScriptClassType.TemplateRootObject
        }
)
public class DateCompareMethod implements TemplateMethodModelEx
{
    /**
     * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
     */
    public Object exec(List args) throws TemplateModelException
    {
        int result = 0;
        
        if (args.size() >= 2)
        {
            Object arg0 = args.get(0);
            Object arg1 = args.get(1);
            long diff = 0;
            if (args.size() == 3)
            {
               Object arg2 = args.get(2);
               if (arg2 instanceof TemplateNumberModel)
               {
                  Number number = ((TemplateNumberModel)arg2).getAsNumber();
                  diff = number.longValue();
               }
            }
            String test = null;
            if (args.size() == 4)
            {
                Object arg3 = args.get(3);
                if (arg3 instanceof TemplateScalarModel)
                {
                    test = ((TemplateScalarModel)arg3).getAsString();
                }
            }
            if (arg0 instanceof TemplateDateModel && arg1 instanceof TemplateDateModel)
            {
                Date dateA = (Date)(((TemplateDateModel)arg0).getAsDate()).clone();
                Date dateB = (Date)(((TemplateDateModel)arg1).getAsDate()).clone();
                switch (((TemplateDateModel)arg0).getDateType())
                {
                    case TemplateDateModel.DATE:
                        // clear time part
                        dateA.setHours(0);
                        dateA.setMinutes(0);
                        dateA.setSeconds(0);
                        dateA = new Date(dateA.getTime() / 1000L * 1000L);
                        break;
                    case TemplateDateModel.TIME:
                        // clear date part
                        dateA.setDate(1);
                        dateA.setMonth(0);
                        dateA.setYear(0);
                        break;
                }
                switch (((TemplateDateModel)arg1).getDateType())
                {
                    case TemplateDateModel.DATE:
                        // clear time part
                        dateB.setHours(0);
                        dateB.setMinutes(0);
                        dateB.setSeconds(0);
                        dateB = new Date(dateB.getTime() / 1000L * 1000L);
                        break;
                    case TemplateDateModel.TIME:
                        // clear date part
                        dateB.setDate(1);
                        dateB.setMonth(0);
                        dateB.setYear(0);
                        break;
                }
                if (test == null || test.equals(">"))
                {
                    if (dateA.getTime() > (dateB.getTime() - diff)) 
                    {
                       result = 1;
                    }
                }
                else if (test.equals("<"))
                {
                    if (dateA.getTime() < (dateB.getTime() - diff)) 
                    {
                       result = 1;
                    }
                }
                else if (test.equals("=="))
                {
                    if (dateA.getTime() == (dateB.getTime() - diff)) 
                    {
                       result = 1;
                    }
                }
            }
        }
        
        return result;
    }
}
