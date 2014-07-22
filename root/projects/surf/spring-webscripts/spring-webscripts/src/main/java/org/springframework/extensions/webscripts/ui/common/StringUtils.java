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

package org.springframework.extensions.webscripts.ui.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserException;

/**
 * Class containing misc helper methods for managing Strings.
 * 
 * NOTE: Extracted from org.alfresco.web.ui.common.Utils;
 * 
 * @author Kevin Roast
 */
public class StringUtils
{
    private static final Log logger = LogFactory.getLog(StringUtils.class);

    private static final String DOCTYPE = "!DOCTYPE";
    private static final String HTML = "html";

    private static final String ATTR_STYLE = "STYLE";
    private static final String ATTR_SRC = "SRC";
    private static final String ATTR_DYNSRC = "DYNSRC";
    private static final String ATTR_LOWSRC = "LOWSRC";
    private static final String ATTR_HREF = "HREF";
    private static final String ATTR_BACKGROUND = "BACKGROUND";
    private static final String ATTR_ON_PREFIX = "ON";
    private static final String ATTR_FORMACTION = "FORMACTION";
    private static final String ATTR_FORMMETHOD = "FORMMETHOD";
    private static final String ATTR_ACTION = "ACTION";
    
    /** default list - NOTE: see spring-webscripts-application-context.xml */
    /** JavaScript event handler attributes starting with "on" are always removed */
    protected static Set<String> attrBlackList = new HashSet<String>();
    static
    {
        attrBlackList.add(ATTR_STYLE);
    }
    
    /** default list - NOTE: see spring-webscripts-application-context.xml */
    protected static Set<String> attrGreyList = new HashSet<String>();
    static
    {
        attrGreyList.add(ATTR_SRC);
        attrGreyList.add(ATTR_DYNSRC);
        attrGreyList.add(ATTR_LOWSRC);
        attrGreyList.add(ATTR_HREF);
        attrGreyList.add(ATTR_BACKGROUND);
        attrGreyList.add(ATTR_FORMACTION);
        attrGreyList.add(ATTR_FORMMETHOD);
        attrGreyList.add(ATTR_ACTION);
    }
    
    /** default list - NOTE: see spring-webscripts-application-context.xml */
    protected static Set<String> tagWhiteList = new HashSet<String>(64);
    static
    {
        tagWhiteList.add("!DOCTYPE");
        tagWhiteList.add("HTML");
        tagWhiteList.add("HEAD");
        tagWhiteList.add("BODY");
        tagWhiteList.add("META");
        tagWhiteList.add("BASE");
        tagWhiteList.add("TITLE");
        tagWhiteList.add("LINK");
        tagWhiteList.add("CENTER");
        tagWhiteList.add("EM");
        tagWhiteList.add("STRONG");
        tagWhiteList.add("SUP");
        tagWhiteList.add("SUB");
        tagWhiteList.add("P");
        tagWhiteList.add("B");
        tagWhiteList.add("I");
        tagWhiteList.add("U");
        tagWhiteList.add("BR");
        tagWhiteList.add("UL");
        tagWhiteList.add("OL");
        tagWhiteList.add("LI");
        tagWhiteList.add("H1");
        tagWhiteList.add("H2");
        tagWhiteList.add("H3");
        tagWhiteList.add("H4");
        tagWhiteList.add("H5");
        tagWhiteList.add("H6");
        tagWhiteList.add("SPAN");
        tagWhiteList.add("DIV");
        tagWhiteList.add("A");
        tagWhiteList.add("IMG");
        tagWhiteList.add("FONT");
        tagWhiteList.add("TABLE");
        tagWhiteList.add("THEAD");
        tagWhiteList.add("TBODY");
        tagWhiteList.add("TR");
        tagWhiteList.add("TH");
        tagWhiteList.add("TD");
        tagWhiteList.add("HR");
        tagWhiteList.add("DT");
        tagWhiteList.add("DL");
        tagWhiteList.add("DT");
        tagWhiteList.add("PRE");
        tagWhiteList.add("BLOCKQUOTE");
        tagWhiteList.add("BUTTON");
        tagWhiteList.add("CODE");
        tagWhiteList.add("FORM");
        tagWhiteList.add("OPTION");
        tagWhiteList.add("SELECT");
        tagWhiteList.add("TEXTAREA");
    }
    
    /** default value - NOTE: see spring-webscripts-application-context.xml */
    protected static boolean overrideDocType = true;

    /**
     * @param tags      Set of safe HTML tags
     */
    public void setTagWhiteList(Set<String> tags)
    {
        StringUtils.tagWhiteList = tags;
    }
    
    /**
     * @param attributes    Set of HTML attributes to consider for sanitisation
     */
    public void setAttributeGreyList(Set<String> attributes)
    {
        StringUtils.attrGreyList = attributes;
    }
    
    /**
     * @param attributes    Set of HTML attributes to be removed
     */
    public void setAttributeBlackList(Set<String> attributes)
    {
        StringUtils.attrBlackList = attributes;
    }

    /**
     * @param overrideDocType    Decides if legacy html !DOCTYPE instructions shall be transformed to the default mode
     */
    public void setOverrideDocType(boolean overrideDocType)
    {
        StringUtils.overrideDocType = overrideDocType;
    }

    /**
     * Encodes the given string, so that it can be used within an HTML page.
     * 
     * @param string     the String to convert
     */
    public static String encode(final String string)
    {
        if (string == null)
        {
            return "";
        }
        
        StringBuilder sb = null;      //create on demand
        String enc;
        char c;
        for (int i = 0; i < string.length(); i++)
        {
            enc = null;
            c = string.charAt(i);
            switch (c)
            {
                case '"': enc = "&quot;"; break;    //"
                case '&': enc = "&amp;"; break;     //&
                case '<': enc = "&lt;"; break;      //<
                case '>': enc = "&gt;"; break;      //>
                
                case '\u20AC': enc = "&euro;";  break;
                case '\u00AB': enc = "&laquo;"; break;
                case '\u00BB': enc = "&raquo;"; break;
                case '\u00A0': enc = "&nbsp;"; break;
                
                default:
                    if (((int)c) >= 0x80)
                    {
                        //encode all non basic latin characters
                        enc = "&#" + ((int)c) + ";";
                    }
                break;
            }
            
            if (enc != null)
            {
                if (sb == null)
                {
                    String soFar = string.substring(0, i);
                    sb = new StringBuilder(i + 16);
                    sb.append(soFar);
                }
                sb.append(enc);
            }
            else
            {
                if (sb != null)
                {
                    sb.append(c);
                }
            }
        }
        
        if (sb == null)
        {
            return string;
        }
        else
        {
            return sb.toString();
        }
    }

    /**
     * Crop a label within a SPAN element, using ellipses '...' at the end of label and
     * and encode the result for HTML output. A SPAN will only be generated if the label
     * is beyond the default setting of 32 characters in length.
     * 
     * @param text       to crop and encode
     * 
     * @return encoded and cropped resulting label HTML
     */
    public static String cropEncode(String text)
    {
        return cropEncode(text, 32);
    }

    /**
     * Crop a label within a SPAN element, using ellipses '...' at the end of label and
     * and encode the result for HTML output. A SPAN will only be generated if the label
     * is beyond the specified number of characters in length.
     * 
     * @param text       to crop and encode
     * @param length     length of string to crop too
     * 
     * @return encoded and cropped resulting label HTML
     */
    public static String cropEncode(String text, int length)
    {
        if (text.length() > length)
        {
            String label = text.substring(0, length - 3) + "...";
            StringBuilder buf = new StringBuilder(length + 32 + text.length());
            buf.append("<span title=\"")
               .append(StringUtils.encode(text))
               .append("\">")
               .append(StringUtils.encode(label))
               .append("</span>");
            return buf.toString();
        }
        else
        {
            return StringUtils.encode(text);
        }
    }

    /**
     * Encode a string to the %AB hex style JavaScript compatible notation.
     * Used to encode a string to a value that can be safely inserted into an HTML page and
     * then decoded (and probably eval()ed) using the unescape() JavaScript method.
     * 
     * @param s      string to encode
     * 
     * @return %AB hex style encoded string
     */
    public static String encodeJavascript(String s)
    {
        StringBuilder buf = new StringBuilder(s.length() * 3);
        for (int i=0; i<s.length(); i++)
        {
            char c = s.charAt(i);
            int iChar = (int)c;
            buf.append('%');
            buf.append(Integer.toHexString(iChar));
        }
        return buf.toString();
    }

    /**
     * Strip unsafe HTML tags from a string - only leaves most basic formatting tags
     * and encodes the remaining characters.
     * 
     * @param s HTML string to strip tags from
     * 
     * @return safe string
     */
    public static String stripUnsafeHTMLTags(String s)
    {
        return stripUnsafeHTMLTags(s, true);
    }
    
    /**
     * Strip unsafe HTML tags from a string - only leaves most basic formatting tags
     * and optionally encodes or strips the remaining characters.
     * 
     * @param s         HTML string to strip tags from
     * @param encode    if true then encode remaining html data
     * 
     * @return safe string
     */
    public static String stripUnsafeHTMLTags(String s, boolean encode)
    {
        return stripUnsafeHTMLTags(s, encode, false);
    }

    /**
     * Strip unsafe HTML tags from a string that represent an entire hml doc - only leaves most basic formatting tags
     * and optionally encodes or strips the remaining characters.
     *
     * @param doc       HTML string representing an entire hml doc to strip tags from
     * @param encode    if true then encode remaining html data
     *
     * @return safe string
     */
    public static String stripUnsafeHTMLDocument(String doc, boolean encode)
    {
        return stripUnsafeHTMLTags(doc, encode, overrideDocType);
    }

    /**
     * Strip unsafe HTML tags from a string - only leaves most basic formatting tags
     * and optionally encodes or strips the remaining characters.
     *
     * @param s         HTML string to strip tags from
     * @param encode    if true then encode remaining html data
     * @param overrideDocumentType if true a doctype enforcing the latest browser rendition mode will used
     *
     * @return safe string
     */
    public static String stripUnsafeHTMLTags(String s, boolean encode, boolean overrideDocumentType)
    {
        String result = s;
        int strippedLength = result.length();
        
        // perform a multi-pass strip until the length of the result stays fixed
        try
        {
            final int MaxPasses = 3;  // Maximum number of passes over the content
            for (int i=0; i<MaxPasses; i++)
            {
                strippedLength = result.length();
                
                StringBuilder buf = new StringBuilder(result.length());
                
                Parser parser = Parser.createParser(result, "UTF-8");
                PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
                parser.setNodeFactory(factory);
                NodeIterator itr = parser.elements();
                processNodes(buf, itr, false, overrideDocumentType);
                
                result = buf.toString();
                
                if (strippedLength == result.length()) break;
            }
            // avoid attempts to add attack vectors that wrap in partial attacks hoping that the stripping
            // process will effectively reconstruct attack elements as other elements are removed.
            if (strippedLength != result.length())
            {
                return "";
            }
            
            // final text element encoding pass if required 
            if (encode)
            {
                StringBuilder buf = new StringBuilder(result.length());
                
                Parser parser = Parser.createParser(result, "UTF-8");
                PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
                parser.setNodeFactory(factory);
                NodeIterator itr = parser.elements();
                processNodes(buf, itr, true);
                
                result = buf.toString();
            }
        }
        catch (ParserException e)
        {
            // return the only safe value if this occurs
            return "";
        }
        
        return result;
    }

    /**
     * Recursively process HTML nodes to strip unsafe HTML.
     *
     * @param buf       Buffer to write to
     * @param itr       Node iterator to process
     * @param encode    True to HTML encode characters within text nodes
     *
     * @throws ParserException
     */
    private static void processNodes(StringBuilder buf, NodeIterator itr, boolean encode)
            throws ParserException
    {
        processNodes(buf, itr, encode, false);
    }

    /**
     * Recursively process HTML nodes to strip unsafe HTML.
     *
     * @param buf       Buffer to write to
     * @param itr       Node iterator to process
     * @param encode    True to HTML encode characters within text nodes
     * @param overrideDocTypePass True if the docType shall be overriden
     *                           (shall only be set to true when iterating over root elements)
     *
     * @throws ParserException
     */
    private static void processNodes(StringBuilder buf, NodeIterator itr, boolean encode, boolean overrideDocTypePass)
            throws ParserException
    {
        boolean firstNode = true;
        while (itr.hasMoreNodes())
        {
            Node node = itr.nextNode();
            if (node instanceof Tag)
            {
                // get the tag and process it and its attributes
                Tag tag = (Tag)node;
                
                // get the tag name - automatically converted to upper case
                String tagname = tag.getTagName();

                if (firstNode)
                {
                    firstNode = false;
                    if (overrideDocTypePass)
                    {
                        // Make sure we make the browser go into standards mode
                        buf.append('<').append(DOCTYPE).append(' ').append(HTML).append('>');
                        if (tagname.equals(DOCTYPE))
                        {
                            // The first tag was DOCTYPE, lets make sure we don't add it in the processing below
                            continue;
                        }
                    }
                }

                // only allow a whitelist of safe tags i.e. remove SCRIPT etc.
                if (tagWhiteList.contains(tagname))
                {
                    // process each attribute name - removing:
                    // all "on*" javascript event handlers
                    // all "style" attributes - as could contain 'expression' javascript for IE
                    Vector<Attribute> attrs = tag.getAttributesEx();
                    
                    // tag attributes contain the tag name at a minimum
                    if (attrs.size() > 1)
                    {
                        buf.append('<').append(tag.getRawTagName());
                        for (Attribute attr : attrs)
                        {
                            String name = attr.getName();
                            if (name != null)
                            {
                                String nameUpper = name.toUpperCase();
                                if (!tagname.equals(nameUpper))   // ignore tag name itself
                                {
                                    // strip any non-alpha character from attribute name - can be used to form XSS attacks
                                    // i.e. allow onclick= by using /onclick= or "onclick=
                                    String safeName = nameUpper.replaceAll("[^A-Z_]", "");

                                    // found a tag attribute for output
                                    // test for known attributes to remove
                                    if (!safeName.startsWith(ATTR_ON_PREFIX) && !attrBlackList.contains(safeName))
                                    {
                                        String value = attr.getRawValue();
                                        // sanitise src and href attributes
                                        if (attrGreyList.contains(safeName))
                                        {
                                            // test the attribute value for XSS - the procedure is:
                                            // . first trim the string to remove whitespace at front (hides attack)
                                            // . test for encoded characters at start of attribute (hides javascript)
                                            // . test for direct javascript: attack
                                            if (attr.getValue() != null)
                                            {
                                                String test = attr.getValue().trim();
                                                if (test.length() > 2)
                                                {
                                                    // handle that html encoder doesn't know about grave accent
                                                    if (test.startsWith("`"))
                                                    {
                                                        test = test.substring(1);
                                                    }
                                                    // all encoded attacks start with &# sequence - assume to be an attack
                                                    // there are no valid protocols starting with "J" - assume attack
                                                    // the "background" attribute is also vulnerable - no web colors start with "J"
                                                    // on IE6 "vbscript" can be used - no colors or protocols start with "VB"
                                                    if (test.startsWith("&#") ||
                                                        test.substring(0, 1).toUpperCase().charAt(0) == 'J' ||
                                                        test.substring(0, 2).toUpperCase().startsWith("VB"))
                                                    {
                                                        value = "\"\"";
                                                    }
                                                }
                                            }
                                        }
                                        buf.append(' ').append(name);
                                        if (value != null)
                                        {
                                            buf.append('=').append(value);
                                        }
                                    }
                                }
                            }
                        }

                        // close the tag after attribute output and before child output
                        buf.append('>');
                        
                        // process children if they exist, else end tag will be processed in next iteration
                        if (tag.getChildren() != null)
                        {
                            processNodes(buf, tag.getChildren().elements(), encode, false);
                            buf.append(tag.getEndTag().toHtml());
                        }
                    }
                    else
                    {
                        // process children if they exist - or output end tag if not empty
                        if (tag.getChildren() != null)
                        {
                            buf.append('<').append(tag.getRawTagName()).append('>');
                            processNodes(buf, tag.getChildren().elements(), encode, false);
                            buf.append(tag.getEndTag().toHtml());
                        }
                        else
                        {
                            buf.append(tag.toHtml());
                        }
                    }
                }
            }
            else if (node instanceof Text)
            {
                if (firstNode)
                {
                    firstNode = false;
                    if (overrideDocTypePass)
                    {
                        // Make sure we make the browser go into standards mode
                        buf.append('<').append(DOCTYPE).append(' ').append(HTML).append('>');
                    }
                }
                
                String txt = ((Text)node).toPlainTextString();
                // MNT-10958 - work around a bug in the HTML Parser which does not correctly
                // detect <% as start of TAG - which can used to insert XSS code!
                // For example: <%<script>alert('XSS');//<%</script>
                buf.append(encode || txt.contains("<%") ? encode(txt): txt);
            }
        }
    }

    /**
     * Replace one string instance with another within the specified string
     * 
     * @param str
     * @param repl
     * @param with
     * 
     * @return replaced string
     */
    public static String replace(String str, String repl, String with)
    {
        if (str == null)
        {
            return null;
        }
        
        int lastindex = 0;
        int pos = str.indexOf(repl);

        // If no replacement needed, return the original string
        // and save StringBuffer allocation/char copying
        if (pos < 0)
        {
            return str;
        }

        int len = repl.length();
        int lendiff = with.length() - repl.length();
        StringBuilder out = new StringBuilder((lendiff <= 0) ? str.length() : (str.length() + (lendiff << 3)));
        for (; pos >= 0; pos = str.indexOf(repl, lastindex = pos + len))
        {
            out.append(str.substring(lastindex, pos)).append(with);
        }

        return out.append(str.substring(lastindex, str.length())).toString();
    }

    /**
     * Remove all occurances of a String from a String
     * 
     * @param str     String to remove occurances from
     * @param match   The string to remove
     * 
     * @return new String with occurances of the match removed
     */
    public static String remove(String str, String match)
    {
        int lastindex = 0;
        int pos = str.indexOf(match);

        // If no replacement needed, return the original string
        // and save StringBuffer allocation/char copying
        if (pos < 0)
        {
            return str;
        }

        int len = match.length();
        StringBuilder out = new StringBuilder(str.length());
        for (; pos >= 0; pos = str.indexOf(match, lastindex = pos + len))
        {
            out.append(str.substring(lastindex, pos));
        }

        return out.append(str.substring(lastindex, str.length())).toString();
    }

    /**
     * Replaces carriage returns and line breaks with the &lt;br&gt; tag.
     * 
     * @param str The string to be parsed
     * @return The string with line breaks removed
     */
    public static String replaceLineBreaks(String str, boolean xhtml)
    {
        String replaced = null;

        if (str != null)
        {
            try
            {
                StringBuilder parsedContent = new StringBuilder(str.length() + 32);
                BufferedReader reader = new BufferedReader(new StringReader(str));
                String line = reader.readLine();
                while (line != null)
                {
                    parsedContent.append(line);
                    line = reader.readLine();
                    if (line != null)
                    {
                        parsedContent.append(xhtml ? "<br/>" : "<br>");
                    }
                }

                replaced = parsedContent.toString();
            }
            catch (IOException ioe)
            {
                if (logger.isWarnEnabled())
                {
                    logger.warn("Failed to replace line breaks in string: " + str);
                }
            }
        }

        return replaced;
    }
    
    /**
     * Join an array of values into a String value
     * 
     * @param value non-null array of objects - toString() of each value is used
     * 
     * @return concatenated string value
     */
    public static String join(final Object[] value)
    {
        return join(value, null);
    }
    
    /**
     * Join an array of values into a String value using supplied delimiter between each.
     * 
     * @param value non-null array of objects - toString() of each value is used
     * @param delim delimiter value to apply between each value - null indicates no delimiter
     * 
     * @return concatenated string value
     */
    public static String join(final Object[] value, final String delim)
    {
        final StringBuilder buf = new StringBuilder(value.length << 4);
        for (int i=0; i<value.length; i++)
        {
            if (i != 0 && delim != null)
            {
                buf.append(delim);
            }
            buf.append(value[i] != null ? value[i].toString() : "");
        }
        return buf.toString();
    }
}
