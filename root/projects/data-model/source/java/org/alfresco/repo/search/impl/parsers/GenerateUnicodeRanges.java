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

import java.text.NumberFormat;

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

        char start = 0;
        char last = 0;
        for (char i = 0; i < 0xFFFF; i++)
        {
            //if (Character.isSpaceChar(i))
            if (Character.isLetterOrDigit(i))
            {
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
                    start = 0;
                    last = 0;
                }
            }

        }

    }

}
