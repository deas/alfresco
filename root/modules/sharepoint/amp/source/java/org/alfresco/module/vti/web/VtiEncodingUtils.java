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

package org.alfresco.module.vti.web;

import java.util.HashMap;
import java.util.Map;

/**
* <p>VtiEncodingUtils is used for encoding strings to specific FrontPage extension format.</p>   
* 
* @author Stas Sokolovsky
*/
public class VtiEncodingUtils
{
    
    private static Map<Character, String> encodingMap = new HashMap<Character, String>();
    
    /**
     * <p>Encode string to specific FrontPage extension format. </p> 
     *
     * @param original original string 
     */
    public static String encode(String original)
    {
        String result = original;
        try
        {
            String transformedString = new String(original.getBytes("UTF-8"), "ISO-8859-1");
            StringBuffer resultBuffer = new StringBuffer();
            
            for (int i = 0; i < transformedString.length(); i++)
            {
                String specialCharacter = null;
                if ((specialCharacter = encodingMap.get(transformedString.charAt(i))) != null) {
                    resultBuffer.append(specialCharacter);
                } else if ((int)transformedString.charAt(i) < 128) {
                    resultBuffer.append(Character.valueOf(transformedString.charAt(i)));
                } else {
                    addCharacter(transformedString.charAt(i), resultBuffer);
                }
            }
            result = resultBuffer.toString();
        }
        catch (Exception e)
        {
            // ignore
        }
        return result;
    }

    private static void addCharacter(char character, StringBuffer resultBuffer)
    {
        resultBuffer.append("&#");
        resultBuffer.append((int) (character));
        resultBuffer.append(';');
    }

    static
    {
        encodingMap.put('=', "&#61;");
        encodingMap.put('{', "&#123;");
        encodingMap.put('}', "&#125;");
        encodingMap.put('&', "&#38;");
        encodingMap.put(';', "&#59;");
        encodingMap.put('\'', "&#39;");
    }    
      
}
