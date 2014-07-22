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

import java.io.StringReader;

import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.annotation.ScriptParameter;
import org.springframework.extensions.webscripts.ui.common.StringUtils;
import org.xml.sax.InputSource;

import freemarker.ext.dom.NodeModel;

/**
 * Collection of script utility methods for working with strings etc.
 * This class is immutable.
 * 
 * @author Kevin Roast
 */
@ScriptClass 
(
        help="Collection of script utility methods for working with strings etc.",
        code="//JavaScript Sample Code\nmodel.biohtml = stringUtils.replaceLineBreaks(bio);\n\n//Freemarker Template Sample Code\n${stringUtils.stripUnsafeHTML(item.node.content)?substring(0, contentLength)}",
        types=
        {
                ScriptClassType.JavaScriptRootObject,
                ScriptClassType.TemplateRootObject
        }
)
public class ScriptableUtils
{
    @ScriptMethod
    (
            help="Strips encode unsafe HTML tags from the input string",
            output="Processed string"
    )
    public String stripEncodeUnsafeHTML(@ScriptParameter(help="Input string") String s)
    {
        return StringUtils.stripUnsafeHTMLTags(s, true);
    }
    
    @ScriptMethod
    (
            help="Strips unsafe HTML tags from the input string",
            output="Processed string"
    )
    public String stripUnsafeHTML(@ScriptParameter(help="Input string") String s)
    {
        return StringUtils.stripUnsafeHTMLTags(s, false);
    }
    
    @ScriptMethod
    (
            help="Replaces line breaks in the input string",
            output="Processed string"
    )
    public String replaceLineBreaks(@ScriptParameter(help="Input string") String s)
    {
        return StringUtils.replaceLineBreaks(s, true);
    }
    
    @ScriptMethod
    (
            help="Encodes input HTML string",
            output="Processed string"
    )
    public String encodeHTML(@ScriptParameter(help="Input string") String s)
    {
        return StringUtils.encode(s);
    }
    
    @ScriptMethod
    (
            help="Encodes input JavaScript string",
            output="Processed string"
    )
    public String encodeJavaScript(@ScriptParameter(help="Input string") String s)
    {
        return StringUtils.encodeJavascript(s);
    }
    
    @ScriptMethod
    (
            help="Encodes input string using URLEncoder",
            output="Processed string"
    )
    public String urlEncode(@ScriptParameter(help="Input string") String s)
    {
        return URLEncoder.encode(s);
    }

    @ScriptMethod
    (
            help="Encode input URL string",
            output="Processed string"
    )
    public String urlEncodeComponent(@ScriptParameter(help="Input string") String s)
    {
        return URLEncoder.encodeUri(s);
    }

    @ScriptMethod
    (
            help="Decodes input URL string",
            output="Processed string"
    )
    public String urlDecode(@ScriptParameter(help="Input string") String s)
    {
        return URLDecoder.decode(s);
    }
    
    /**
     * Converts an xml string to a freemarker node model
     * 
     * @param xml
     * 
     * @return freemarker node model
     */
    @ScriptMethod
    (
            help="Converts an xml string to a freemarker node model",
            output="Freemarker node model"
    )
    public NodeModel parseXMLNodeModel(@ScriptParameter(help="Input XML string") String xml)
    {
        try
        {
            return NodeModel.parse(new InputSource(new StringReader(xml)));
        }
        catch (Throwable err)
        {
            err.printStackTrace();
            return null;
        }
    }
    
}
