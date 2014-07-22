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
import org.springframework.extensions.webscripts.annotation.ScriptMethodType;
import org.springframework.extensions.webscripts.annotation.ScriptParameter;

/**
 * Sample class with Script* annotations
 * 
 * @author drq
 *
 */
@ScriptClass 
(
        help="Sample JavaScript APIs for managing messages",
        code="//sample code goes here.",
        types=
        {
                ScriptClassType.JavaScriptAPI
        }
)
public class SampleMessage
{

    /**
     * 
     */
    private String msg;

    /**
     * 
     */
    public SampleMessage()
    {
    }
    /**
     * @param msg
     */
    public SampleMessage(String msg)
    {
        this.msg = msg;
    }

    /**
     * @return the msg
     */
    @ScriptMethod (help="Returns message",
            output="the message",
            code="//sample code goes here.")
            public String getMsg()
    {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    /**
     * @param regex
     * @return
     */
    @ScriptMethod 
    (
            help="Splits this message around matches of the given regular expression.",
            output="the array of strings computed by splitting this string around matches of the given regular expression",
            code="//sample code goes here.",
            type=ScriptMethodType.READ
    )
    public String [] splitMsg(@ScriptParameter(name="regex",help="the delimiting regular expression") String regex)
    {
        return this.msg.split(regex);
    }
}
