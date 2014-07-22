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

package org.springframework.extensions.surf.util;

/**
 * UTF-8 URL character encoder. Based on an optimized and improved version
 * of the w3 Consortium URLUTF8Encoder class.
 * 
 * @author kevinr
 */
public final class URLEncoder
{
    private final static String[] hex = {
        "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07",
        "%08", "%09", "%0a", "%0b", "%0c", "%0d", "%0e", "%0f",
        "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
        "%18", "%19", "%1a", "%1b", "%1c", "%1d", "%1e", "%1f",
        "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27",
        "%28", "%29", "%2a", "%2b", "%2c", "%2d", "%2e", "%2f",
        "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37",
        "%38", "%39", "%3a", "%3b", "%3c", "%3d", "%3e", "%3f",
        "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47",
        "%48", "%49", "%4a", "%4b", "%4c", "%4d", "%4e", "%4f",
        "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57",
        "%58", "%59", "%5a", "%5b", "%5c", "%5d", "%5e", "%5f",
        "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67",
        "%68", "%69", "%6a", "%6b", "%6c", "%6d", "%6e", "%6f",
        "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77",
        "%78", "%79", "%7a", "%7b", "%7c", "%7d", "%7e", "%7f",
        "%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87",
        "%88", "%89", "%8a", "%8b", "%8c", "%8d", "%8e", "%8f",
        "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97",
        "%98", "%99", "%9a", "%9b", "%9c", "%9d", "%9e", "%9f",
        "%a0", "%a1", "%a2", "%a3", "%a4", "%a5", "%a6", "%a7",
        "%a8", "%a9", "%aa", "%ab", "%ac", "%ad", "%ae", "%af",
        "%b0", "%b1", "%b2", "%b3", "%b4", "%b5", "%b6", "%b7",
        "%b8", "%b9", "%ba", "%bb", "%bc", "%bd", "%be", "%bf",
        "%c0", "%c1", "%c2", "%c3", "%c4", "%c5", "%c6", "%c7",
        "%c8", "%c9", "%ca", "%cb", "%cc", "%cd", "%ce", "%cf",
        "%d0", "%d1", "%d2", "%d3", "%d4", "%d5", "%d6", "%d7",
        "%d8", "%d9", "%da", "%db", "%dc", "%dd", "%de", "%df",
        "%e0", "%e1", "%e2", "%e3", "%e4", "%e5", "%e6", "%e7",
        "%e8", "%e9", "%ea", "%eb", "%ec", "%ed", "%ee", "%ef",
        "%f0", "%f1", "%f2", "%f3", "%f4", "%f5", "%f6", "%f7",
        "%f8", "%f9", "%fa", "%fb", "%fc", "%fd", "%fe", "%ff"
    };

    /**
     * Encode a string to the UTF-8-in-URL proposal. There are some changes
     * from the standard, this is what happens:
     *
     * <ul>
     * <li><p>The ASCII characters 'a' through 'z', 'A' through 'Z',
     *        and '0' through '9' remain the same.
     *
     * <li><p>The URI reserved characters ; , / ? : @ & = + $ only remain the same
     *        if the parameter reserveUriChars is true 
     * 
     * <li><p>The unreserved characters - _ . ! ~ * ( ) remain the same.
     *
     * <li><p>The unreserved character ' is converted into "%27" - it is NOT left unencoded!
     *
     * <li><p>The space character ' ' is converted into "%20" - NOT a plus sign!
     *
     * <li><p>All other ASCII characters are converted into the
     *        3-character string "%xy", where xy is
     *        the two-digit hexadecimal representation of the character
     *        code
     *
     * <li><p>All non-ASCII characters are encoded in two steps: first
     *        to a sequence of 2 or 3 bytes, using the UTF-8 algorithm;
     *        secondly each of these bytes is encoded as "%xx".
     * </ul>
     * 
     * @param s     The non-null string to be encoded
     * @param reserveUriChars  true => uri reserved characters are not encoded
     * 
     * @return The encoded string
     */
    private static String encode(final String s, final boolean reserveUriChars)
    {
        StringBuilder sb = null;      //create on demand
        char ch;
        final int len = s.length();
        for (int i = 0; i < len; i++)
        {
            ch = s.charAt(i);
            
            if (('A' <= ch && ch <= 'Z') ||             // 'A'..'Z'
                ('a' <= ch && ch <= 'z') ||             // 'a'..'z'
                ('0' <= ch && ch <= '9') ||             // '0'..'9'
                ch == '-' || ch == '_' ||               // unreserved
                ch == '.' || ch == '!' ||
                ch == '~' || ch == '*' ||
                ch == '(' || ch == ')')
            {
                if (sb != null)
                {
                    sb.append(ch);
                }
            }
            else if (reserveUriChars &&                 // uri reserved
                     (ch == ';' || ch == ',' ||
                      ch == '/' || ch == '?' ||
                      ch == ':' || ch == '@' ||
                      ch == '&' || ch == '=' ||
                      ch == '+' || ch == '$'))
            {
                if (sb != null)
                {
                    sb.append(ch);
                }
            }
            else if ((int)ch <= 0x007f)                 // other ASCII including single quote ' and space
            {
                if (sb == null)
                {
                    final String soFar = s.substring(0, i);
                    sb = new StringBuilder(len + 16);
                    sb.append(soFar);
                }
                sb.append(hex[ch]);
            }
            else if ((int)ch <= 0x07FF)                 // non-ASCII <= 0x7FF
            {
                if (sb == null)
                {
                    final String soFar = s.substring(0, i);
                    sb = new StringBuilder(len + 16);
                    sb.append(soFar);
                }
                sb.append(hex[0xc0 | (ch >> 6)]);
                sb.append(hex[0x80 | (ch & 0x3F)]);
            }
            else                                        // 0x7FF < ch <= 0xFFFF
            {
                if (sb == null)
                {
                    final String soFar = s.substring(0, i);
                    sb = new StringBuilder(len + 16);
                    sb.append(soFar);
                }
                sb.append(hex[0xe0 | (ch >> 12)]);
                sb.append(hex[0x80 | ((ch >> 6) & 0x3F)]);
                sb.append(hex[0x80 | (ch & 0x3F)]);
            }
        }
        return (sb != null ? sb.toString() : s);
    }
    
    /**
     * @See org.springframework.extensions.surf.util.URLEncoder.encodeUriComponent(s)
     */
    public static String encode(String s)
    {
        return encode(s, false);
    }

    /**
     * Encode a string to the UTF-8-in-URL proposal. There are some changes
     * from the standard, this is what happens:
     *
     * <ul>
     * <li><p>The ASCII characters 'a' through 'z', 'A' through 'Z',
     *        and '0' through '9' remain the same.
     *
     * <li><p>The unreserved characters - _ . ! ~ * ( ) remain the same.
     *
     * <li><p>The unreserved character ' is converted into "%27" - it is NOT left unencoded!
     *
     * <li><p>The space character ' ' is converted into "%20" - NOT a plus sign!
     *
     * <li><p>All other ASCII characters are converted into the
     *        3-character string "%xy", where xy is
     *        the two-digit hexadecimal representation of the character
     *        code
     *
     * <li><p>All non-ASCII characters are encoded in two steps: first
     *        to a sequence of 2 or 3 bytes, using the UTF-8 algorithm;
     *        secondly each of these bytes is encoded as "%xx".
     * </ul>
     *
     * @param s     The string to be encoded
     * @return The encoded string
     */
    public static String encodeUriComponent(String s)
    {
        return encode(s, false);
    }

    /**
     * Encode a string to the UTF-8-in-URL proposal. There are some changes
     * from the standard, this is what happens:
     *
     * <ul>
     * <li><p>The ASCII characters 'a' through 'z', 'A' through 'Z',
     *        and '0' through '9' remain the same.
     *
     * <li><p>The URI reserved characters ; , / ? : @ & = + $ remain the same.
     * 
     * <li><p>The unreserved characters - _ . ! ~ * ( ) remain the same.
     *
     * <li><p>The unreserved character ' is converted into "%27" - it is NOT left unencoded!
     *
     * <li><p>The space character ' ' is converted into "%20" - NOT a plus sign!
     *
     * <li><p>All other ASCII characters are converted into the
     *        3-character string "%xy", where xy is
     *        the two-digit hexadecimal representation of the character
     *        code
     *
     * <li><p>All non-ASCII characters are encoded in two steps: first
     *        to a sequence of 2 or 3 bytes, using the UTF-8 algorithm;
     *        secondly each of these bytes is encoded as "%xx".
     * </ul>
     *
     * @param s     The string to be encoded
     * @return The encoded string
     */
    public static String encodeUri(String s)
    {
        return encode(s, true);
    }
}
