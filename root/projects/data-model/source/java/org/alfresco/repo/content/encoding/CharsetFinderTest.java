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
package org.alfresco.repo.content.encoding;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import junit.framework.TestCase;

import org.alfresco.encoding.CharactersetFinder;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.util.DataModelTestApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * @see CharsetFinderTest
 * @see CharactersetFinder
 * 
 * @author Derek Hulley
 */
public class CharsetFinderTest extends TestCase
{
    private static ApplicationContext ctx = DataModelTestApplicationContextHelper.getApplicationContext();
    
    private ContentCharsetFinder charsetFinder;
    
    @Override
    public void setUp() throws Exception
    {
        charsetFinder = (ContentCharsetFinder) ctx.getBean("charset.finder");
    }
    
    public void testPlainText() throws Exception
    {
        String test = "The quick brown fox jumps over the lazy dog" +
                      "\n\nLe renard brun rapide saute par-dessus le chien paresseux" +
                      "\n\nDer schnelle braune Fuchs springt über den faulen Hund\n\n" +
                      "براون وكس السريع يقفز فوق الكلب كسالي";
        InputStream is = new BufferedInputStream(new ByteArrayInputStream(test.getBytes("UTF-8")));
        Charset charset = charsetFinder.getCharset(is, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertNotNull(charset);
        
    }
}
