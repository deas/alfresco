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

package org.springframework.extensions.webscripts.annotation.samples;

import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.annotation.ScriptParameter;

/**
 * Sample class with Script* annotations
 * 
 * @author drq
 *
 */
@ScriptClass 
(
        help="Sample JavaScript root object that returns a list of messages",
        code="var msgs = Sample.getMessages(3);\nfor each( var msg in msgs) {\nvar msgStr = msg.msg;\n}",
        types=
        {
                ScriptClassType.JavaScriptRootObject,
                ScriptClassType.TemplateRootObject
        }
)
public class Sample 
{
    @ScriptMethod 
    (
            help="Returns given number of hello world messages",
            output="the array of hello world messages",
            code="var msgs = Sample.getMessages(3);\nfor each( var msg in msgs) {\nvar msgStr = msg.msg;\n}"
    )
    public SampleMessage [] getMessages ( @ScriptParameter(name="numberOfMsg",help="Number of messages") int numberOfMsg)
    {
        SampleMessage [] messages = new SampleMessage[numberOfMsg];

        for (SampleMessage msg : messages)
        {
            msg.setMsg("Hello World");
        }

        return messages;
    }
}
