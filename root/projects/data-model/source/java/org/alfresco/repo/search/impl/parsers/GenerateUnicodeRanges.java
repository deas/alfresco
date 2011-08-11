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
package org.alfresco.repo.search.impl.parsers;


/**
 * @author Andy
 */
public class GenerateUnicodeRanges
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {

        int  start = 0;
        int  last = 0;
        for (int i = 0; i < 0xFFFF; i++)
        {
            //if (Character.isSpaceChar(i))
            switch(Character.getType(i))
            {
            case Character.LOWERCASE_LETTER:  // V1
            case Character.MODIFIER_LETTER:   // V1
            case Character.OTHER_LETTER:      // V1
            case Character.TITLECASE_LETTER:  // V1
            case Character.UPPERCASE_LETTER:  // V1
            case Character.COMBINING_SPACING_MARK:
            case Character.ENCLOSING_MARK:
            case Character.NON_SPACING_MARK:
            case Character.DECIMAL_DIGIT_NUMBER:  // V1
            case Character.LETTER_NUMBER:
            case Character.OTHER_NUMBER:
            case Character.CURRENCY_SYMBOL:
            case Character.OTHER_SYMBOL:
                if (last == 0)
                {
                    start = i;
                }

                if ((last + 1 == i) || (start == i))
                {
                    last = i;
                }
                else
                {
                    if (start > 0)
                    {
                        if (start == (i - 1))
                        {
                            System.out.println(String.format("        | '\\u%04x'", Integer.valueOf(start)));
                        }
                        else
                        {
                            System.out.println(String.format("        | '\\u%04x'..'\\u%04x'", Integer.valueOf(start), Integer.valueOf(last)));
                        }
                        start = i;
                        last = i;
                    }
                }
                break;
            case Character.CONTROL:
            case Character.FORMAT:
            case Character.PRIVATE_USE:
            case Character.SURROGATE:
            case Character.CONNECTOR_PUNCTUATION:
            case Character.DASH_PUNCTUATION:
            case Character.END_PUNCTUATION:
            case Character.FINAL_QUOTE_PUNCTUATION:
            case Character.INITIAL_QUOTE_PUNCTUATION:
            case Character.OTHER_PUNCTUATION:
            case Character.START_PUNCTUATION:
            case Character.MODIFIER_SYMBOL:
            case Character.MATH_SYMBOL:
            case Character.LINE_SEPARATOR:
            case Character.PARAGRAPH_SEPARATOR:
            case Character.SPACE_SEPARATOR:
                if (start > 0)
                {
                    if (start == (i - 1))
                    {
                        System.out.println(String.format("        | '\\u%04x'", Integer.valueOf(start)));
                    }
                    else
                    {
                        System.out.println(String.format("        | '\\u%04x'..'\\u%04x'", Integer.valueOf(start), Integer.valueOf(last)));
                    }
                    start = 0;
                    last = 0;
                }
                break;
            }

        }

    }
}
