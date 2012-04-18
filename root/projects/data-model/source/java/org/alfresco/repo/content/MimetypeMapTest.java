/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.repo.content;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.util.DataModelTestApplicationContextHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.config.ConfigDeployment;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.ConfigSource;
import org.springframework.extensions.config.xml.XMLConfigService;

/**
 * @see org.alfresco.repo.content.MimetypeMap
 * @see org.alfresco.repo.content.MimetypeMapContentTest
 * 
 * @author Derek Hulley
 */
public class MimetypeMapTest extends TestCase
{
    private static ApplicationContext ctx = DataModelTestApplicationContextHelper.getApplicationContext();
    
    private MimetypeService mimetypeService;
    private ConfigService configService;
    
    @Override
    public void setUp() throws Exception
    {
        mimetypeService =  (MimetypeService)ctx.getBean("mimetypeService");
        configService = ((MimetypeMap)mimetypeService).getConfigService();
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        ((MimetypeMap)mimetypeService).setConfigService(configService);
        ((MimetypeMap)mimetypeService).init();
    }

    public void testExtensions() throws Exception
    {
        Map<String, String> extensionsByMimetype = mimetypeService.getExtensionsByMimetype();
        Map<String, String> mimetypesByExtension = mimetypeService.getMimetypesByExtension();
        
        // plain text
        assertEquals("txt", extensionsByMimetype.get("text/plain"));
        assertEquals("text/plain", mimetypesByExtension.get("txt"));
        assertEquals("text/plain", mimetypesByExtension.get("java"));
        
        // other text forms
        assertEquals("text/csv", mimetypesByExtension.get("csv"));
        assertEquals("text/html", mimetypesByExtension.get("html"));
        
        // JPEG
        assertEquals("jpg", extensionsByMimetype.get("image/jpeg"));
        assertEquals("image/jpeg", mimetypesByExtension.get("jpg"));
        assertEquals("image/jpeg", mimetypesByExtension.get("jpeg"));
        assertEquals("image/jpeg", mimetypesByExtension.get("jpe"));
        
        // MS Word
        assertEquals("doc", extensionsByMimetype.get("application/msword"));
        assertEquals("application/msword", mimetypesByExtension.get("doc"));
        
        // Star Office
        assertEquals("sds", extensionsByMimetype.get("application/vnd.stardivision.chart"));
    }
    
    public void testIsText() throws Exception
    {
        assertTrue(mimetypeService.isText(MimetypeMap.MIMETYPE_HTML));
    }
    
    public void testGetContentCharsetFinder() throws Exception
    {
        assertNotNull("No charset finder", mimetypeService.getContentCharsetFinder());
    }

    public void testMimetypeFromExtension() throws Exception
    {
        // test known mimetype
        assertEquals("application/msword", mimetypeService.getMimetype("doc"));
        // test case insensitivity
        assertEquals("application/msword", mimetypeService.getMimetype("DOC"));
        
        // test fallback for unknown and missing
        assertEquals(MimetypeMap.MIMETYPE_BINARY, mimetypeService.getMimetype(null));
        assertEquals(MimetypeMap.MIMETYPE_BINARY, mimetypeService.getMimetype("unknownext"));
    }
 
    /**
     * Tests guessing the mimetype from a filename.
     * 
     * Note - The test for checking by filename + content are in the repo project
     * @see org.alfresco.repo.content.MimetypeMapContentTest
     */
    public void testGuessMimetypeForFilename() throws Exception
    {
        assertEquals("application/msword", mimetypeService.guessMimetype("something.doc"));
        assertEquals("application/msword", mimetypeService.guessMimetype("SOMETHING.DOC"));
        assertEquals(MimetypeMap.MIMETYPE_BINARY, mimetypeService.guessMimetype("noextension"));
        assertEquals(MimetypeMap.MIMETYPE_BINARY, mimetypeService.guessMimetype("file.unknownext"));
        
        // Without a content reader, the behaviour is the same
        assertEquals("application/msword", mimetypeService.guessMimetype("something.doc", (ContentReader)null));
        assertEquals("application/msword", mimetypeService.guessMimetype("SOMETHING.DOC", (ContentReader)null));
        assertEquals(MimetypeMap.MIMETYPE_BINARY, mimetypeService.guessMimetype("noextension", (ContentReader)null));
        assertEquals(MimetypeMap.MIMETYPE_BINARY, mimetypeService.guessMimetype("file.unknownext", (ContentReader)null));
    }
    
    private static final String MIMETYPE_1A =
        "      <mimetype mimetype=\"mimetype1\" display=\"Mimetype ONE\">" +
        "        <extension display=\"Extension ONE\">ext1a</extension>" +
        "      </mimetype>";
    private static final String MIMETYPE_1B =
        "      <mimetype mimetype=\"mimetype1\" display=\"Mimetype ONE\">" +
        "        <extension display=\"Extension 1\">ext1a</extension>" +
        "      </mimetype>";
    private static final String MIMETYPE_1C =
        "      <mimetype mimetype=\"mimetype1\" display=\"Mimetype ONE\">" +
        "        <extension>ext1a</extension>" +
        "      </mimetype>";
    private static final String MIMETYPE_1D =
        "      <mimetype mimetype=\"mimetype1\" display=\"Mimetype 1\">" +
        "        <extension>ext1a</extension>" +
        "      </mimetype>";
    private static final String MIMETYPE_1E =
        "      <mimetype mimetype=\"mimetype1\" text=\"true\">" +
        "        <extension>ext1a</extension>" +
        "      </mimetype>";
    private static final String MIMETYPE_1F =
        "      <mimetype mimetype=\"mimetype1\" text=\"true\">" +
        "        <extension>ext1a</extension>" +
        "        <extension default=\"true\">ext1b</extension>" +
        "        <extension>ext1c</extension>" +
        "      </mimetype>";
    private static final String MIMETYPE_1G =
        "      <mimetype mimetype=\"mimetype1\" text=\"true\">" +
        "        <extension>ext1c</extension>" +
        "        <extension>ext1b</extension>" +
        "        <extension>ext1a</extension>" +
        "      </mimetype>";
    
    private static final String MIMETYPE_2A =
        "      <mimetype mimetype=\"mimetype2\" display=\"Mimetype TWO\" text=\"true\">" +
        "        <extension>ext2a</extension>" +
        "      </mimetype>";
    private static final String MIMETYPE_2B =
        "      <mimetype mimetype=\"mimetype2\" display=\"Mimetype TWO\">" +
        "        <extension>ext2a</extension>" +
        "      </mimetype>";

    private static final String MIMETYPE_3A =
        "      <mimetype mimetype=\"mimetype3\" display=\"Mimetype THREE\">" +
        "        <extension>ext3a</extension>" +
        "        <extension>ext3b</extension>" +
        "      </mimetype>";

    public void testNoDuplicates() throws Exception
    {
        setConfigService(
                MIMETYPE_1A+
                MIMETYPE_2A+
                MIMETYPE_3A);
        ((MimetypeMap)mimetypeService).init();
        
        assertFalse("mimetype1 should not be text", mimetypeService.isText("mimetype1"));
        assertEquals("ext1a", mimetypeService.getExtension("mimetype1"));
        assertEquals("mimetype1", mimetypeService.getMimetype("ext1a"));
        assertEquals("Mimetype ONE", mimetypeService.getDisplaysByMimetype().get("mimetype1"));
        assertEquals("Extension ONE", mimetypeService.getDisplaysByExtension().get("ext1a"));

        assertTrue("mimetype2 should be text", mimetypeService.isText("mimetype2"));
        assertEquals("mimetype2", mimetypeService.getMimetype("ext2a"));

        assertEquals("mimetype3", mimetypeService.getMimetype("ext3a"));
    }

    public void testDuplicates() throws Exception
    {
        setConfigService(
                MIMETYPE_1A+MIMETYPE_1B+MIMETYPE_1C+MIMETYPE_1D+MIMETYPE_1E+MIMETYPE_1F+ // Change all values
                MIMETYPE_2A+MIMETYPE_2B+ // duplicate removes isText
                MIMETYPE_3A+MIMETYPE_3A); // identical
        ((MimetypeMap)mimetypeService).init();
        
        assertTrue("mimetype1 should have be reset to text", mimetypeService.isText("mimetype1"));
        assertEquals("ext1b", mimetypeService.getExtension("mimetype1"));
        assertEquals("mimetype1", mimetypeService.getMimetype("ext1a"));
        assertEquals("mimetype1", mimetypeService.getMimetype("ext1b"));
        assertEquals("mimetype1", mimetypeService.getMimetype("ext1c"));
        assertEquals("Mimetype 1", mimetypeService.getDisplaysByMimetype().get("mimetype1"));
        assertEquals("Extension 1", mimetypeService.getDisplaysByExtension().get("ext1a"));

        assertFalse("mimetype2 should have be reset to not text", mimetypeService.isText("mimetype2"));
        assertEquals("mimetype2", mimetypeService.getMimetype("ext2a"));

        assertEquals("mimetype3", mimetypeService.getMimetype("ext3a"));
    }

    private void setConfigService(final String mimetypes)
    {
        ConfigSource configSource = new ConfigSource()
        {
            @Override
            public List<ConfigDeployment> getConfigDeployments()
            {
                String xml =
                    "<alfresco-config area=\"mimetype-map\">" +
                    "  <config evaluator=\"string-compare\" condition=\"Mimetype Map\">" +
                    "    <mimetypes>" +
                           mimetypes +
                    "    </mimetypes>" +
                    "  </config>" +
                    "</alfresco-config>";
                List<ConfigDeployment> configs = new ArrayList<ConfigDeployment>();
                configs.add(new ConfigDeployment("name", new ByteArrayInputStream(xml.getBytes())));
                return configs;
            }
        };

        ConfigService configService = new XMLConfigService(configSource);
        ((XMLConfigService) configService).initConfig();
        ((MimetypeMap)mimetypeService).setConfigService(configService);
    }
}
