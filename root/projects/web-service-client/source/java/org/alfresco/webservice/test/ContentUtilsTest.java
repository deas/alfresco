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
package org.alfresco.webservice.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import junit.framework.TestCase;

import org.alfresco.webservice.util.ContentUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author Dmitry Velichkevich
 */
public class ContentUtilsTest extends TestCase
{
    private static final String MD5_DIGEST_ALGORITHM = "MD5";

    private static final String TEMPORARY_DESTINATION_PATH = "./destination.tmp";
    private static final String BIG_TEST_FILE = "org/alfresco/webservice/test/resources/big-content.pdf";

    private InputStream inputStream;

    @Override
    protected void setUp() throws Exception
    {
        inputStream = getClass().getClassLoader().getResourceAsStream(BIG_TEST_FILE);
    }

    @Override
    protected void tearDown() throws Exception
    {
        inputStream.close();
    }

    public void testInputStreamToByteArrayConversion() throws Exception
    {
        File tempDestination = new File(TEMPORARY_DESTINATION_PATH);
        tempDestination.deleteOnExit();
        OutputStream destination = new FileOutputStream(tempDestination);
        MessageDigest digest = MessageDigest.getInstance(MD5_DIGEST_ALGORITHM);
        ContentUtils.copy(new DigestInputStream(getClass().getClassLoader().getResourceAsStream(BIG_TEST_FILE), digest), destination);
        String expectedMD5Sum = new String(Hex.encodeHex(digest.digest()));

        String actualMD5Sum = DigestUtils.md5Hex(ContentUtils.convertToByteArray(inputStream));
        assertEquals(expectedMD5Sum, actualMD5Sum);
    }
}
