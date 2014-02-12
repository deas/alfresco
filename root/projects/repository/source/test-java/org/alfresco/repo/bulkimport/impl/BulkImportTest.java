/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.repo.bulkimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.evaluator.NoConditionEvaluator;
import org.alfresco.repo.action.executer.CopyActionExecuter;
import org.alfresco.repo.action.executer.MoveActionExecuter;
import org.alfresco.repo.bulkimport.BulkImportParameters;
import org.alfresco.repo.bulkimport.NodeImporter;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.test_category.BaseSpringTestsCategory;
import org.alfresco.test_category.OwnJVMTestsCategory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.util.ResourceUtils;

/**
 * @since 4.0
 */
@Category(OwnJVMTestsCategory.class)
public class BulkImportTest extends AbstractBulkImportTests
{
    private StreamingNodeImporterFactory streamingNodeImporterFactory;

    @BeforeClass
    public static void beforeTests()
    {
        startContext();
    }

    @Before
    public void setup() throws SystemException, NotSupportedException
    {
        super.setup();
        streamingNodeImporterFactory = (StreamingNodeImporterFactory)ctx.getBean("streamingNodeImporterFactory");
    }

    /**
     * For replaceExisting = true, the title must be taken from the metadata and not overridden by the actual filename.
     * 
     * @throws Throwable
     */
    @Test
    public void testMNT8470() throws Throwable
    {
        txn = transactionService.getUserTransaction();
        txn.begin();

        NodeRef folderNode = topLevelFolder.getNodeRef();

        try
        {
            NodeImporter nodeImporter = streamingNodeImporterFactory.getNodeImporter(ResourceUtils.getFile("classpath:bulkimport1"));
            BulkImportParameters bulkImportParameters = new BulkImportParameters();
            bulkImportParameters.setTarget(folderNode);
            bulkImportParameters.setReplaceExisting(true);
            bulkImportParameters.setDisableRulesService(true);
            bulkImportParameters.setBatchSize(40);
            bulkImporter.bulkImport(bulkImportParameters, nodeImporter);
        }
        catch(Throwable e)
        {
            fail(e.getMessage());
        }

        System.out.println(bulkImporter.getStatus());
        assertEquals(false, bulkImporter.getStatus().inProgress());
        
        List<FileInfo> folders = getFolders(folderNode, null);
        assertEquals(1, folders.size());
        FileInfo folder1 = folders.get(0);
        assertEquals("folder1", folder1.getName());
        // title should be taken from the metadata file
        assertEquals("", folder1.getProperties().get(ContentModel.PROP_TITLE));
    }

    @Test
    public void testCopyImportStriping() throws Throwable
    {
        txn = transactionService.getUserTransaction();
        txn.begin();

        NodeRef folderNode = topLevelFolder.getNodeRef();

        try
        {
            NodeImporter nodeImporter = streamingNodeImporterFactory.getNodeImporter(ResourceUtils.getFile("classpath:bulkimport"));
            BulkImportParameters bulkImportParameters = new BulkImportParameters();
            bulkImportParameters.setTarget(folderNode);
            bulkImportParameters.setReplaceExisting(true);
            bulkImportParameters.setDisableRulesService(true);
            bulkImportParameters.setBatchSize(40);
            bulkImporter.bulkImport(bulkImportParameters, nodeImporter);
        }
        catch(Throwable e)
        {
            fail(e.getMessage());
        }

        System.out.println(bulkImporter.getStatus());

        checkFiles(folderNode, null, 2, 9,
                new ExpectedFile[]
                {
                    new ExpectedFile("quickImg1.xls", MimetypeMap.MIMETYPE_EXCEL),
                    new ExpectedFile("quickImg1.doc", MimetypeMap.MIMETYPE_WORD),
                    new ExpectedFile("quick.txt", MimetypeMap.MIMETYPE_TEXT_PLAIN, "The quick brown fox jumps over the lazy dog"),
                },
                new ExpectedFolder[]
                {
                    new ExpectedFolder("folder1"),
                    new ExpectedFolder("folder2")
                });

        List<FileInfo> folders = getFolders(folderNode, "folder1");
        assertEquals("", 1, folders.size());
        NodeRef folder1 = folders.get(0).getNodeRef();
        checkFiles(folder1, null, 1, 0, null,
                new ExpectedFolder[]
                {
                    new ExpectedFolder("folder1.1")
                });

        folders = getFolders(folderNode, "folder2");
        assertEquals("", 1, folders.size());
        NodeRef folder2 = folders.get(0).getNodeRef();
        checkFiles(folder2, null, 1, 0,
                new ExpectedFile[]
                {
                },
                new ExpectedFolder[]
                {
                    new ExpectedFolder("folder2.1")
                });

        folders = getFolders(folder1, "folder1.1");
        assertEquals("", 1, folders.size());
        NodeRef folder1_1 = folders.get(0).getNodeRef();
        checkFiles(folder1_1, null, 2, 12,
                new ExpectedFile[]
                {
                    new ExpectedFile("quick.txt", MimetypeMap.MIMETYPE_TEXT_PLAIN, "The quick brown fox jumps over the lazy dog"),
                    new ExpectedFile("quick.sxw", MimetypeMap.MIMETYPE_OPENOFFICE1_WRITER),
                    new ExpectedFile("quick.tar", "application/x-gtar"),
                },
                new ExpectedFolder[]
                {
                    new ExpectedFolder("folder1.1.1"),
                    new ExpectedFolder("folder1.1.2")
                });

        folders = getFolders(folder2, "folder2.1");
        assertEquals("", 1, folders.size());
        NodeRef folder2_1 = folders.get(0).getNodeRef();

        checkFiles(folder2_1, null, 0, 17,
                new ExpectedFile[]
                {
                    new ExpectedFile("quick.png", MimetypeMap.MIMETYPE_IMAGE_PNG),
                    new ExpectedFile("quick.pdf", MimetypeMap.MIMETYPE_PDF),
                    new ExpectedFile("quick.odt", MimetypeMap.MIMETYPE_OPENDOCUMENT_TEXT),
                },
                new ExpectedFolder[]
                {
                });
    }

    protected Rule createCopyRule(NodeRef targetNode, boolean isAppliedToChildren)
    {
        Rule rule = new Rule();
        rule.setRuleType(RuleType.INBOUND);
        String title = "rule title " + System.currentTimeMillis();
        rule.setTitle(title);
        rule.setDescription(title);
        rule.applyToChildren(isAppliedToChildren);

        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put(MoveActionExecuter.PARAM_DESTINATION_FOLDER, targetNode);

        Action action = actionService.createAction(CopyActionExecuter.NAME, params);
        ActionCondition condition = actionService.createActionCondition(NoConditionEvaluator.NAME);
        action.addActionCondition(condition);
        rule.setAction(action);

        return rule;
    }
    
    @Test
    public void testImportWithRules() throws Throwable
    {
        NodeRef folderNode = topLevelFolder.getNodeRef();
        NodeImporter nodeImporter = null;

        txn = transactionService.getUserTransaction();
        txn.begin();

        NodeRef targetNode = fileFolderService.create(top, "target", ContentModel.TYPE_FOLDER).getNodeRef();

        // Create a rule on the node into which we're importing
        Rule newRule = createCopyRule(targetNode, false);
        this.ruleService.saveRule(folderNode, newRule);

        txn.commit();
        
        txn = transactionService.getUserTransaction();
        txn.begin();

        nodeImporter = streamingNodeImporterFactory.getNodeImporter(ResourceUtils.getFile("classpath:bulkimport"));

        BulkImportParameters bulkImportParameters = new BulkImportParameters();
        bulkImportParameters.setTarget(folderNode);
        bulkImportParameters.setReplaceExisting(true);
        bulkImportParameters.setDisableRulesService(false);
        bulkImportParameters.setBatchSize(40);
        bulkImporter.bulkImport(bulkImportParameters, nodeImporter);

        System.out.println(bulkImporter.getStatus());

        assertEquals("", 74, bulkImporter.getStatus().getNumberOfContentNodesCreated());

        checkFiles(folderNode, null, 2, 9, new ExpectedFile[] {
                new ExpectedFile("quickImg1.xls", MimetypeMap.MIMETYPE_EXCEL),
                new ExpectedFile("quickImg1.doc", MimetypeMap.MIMETYPE_WORD),
                new ExpectedFile("quick.txt", MimetypeMap.MIMETYPE_TEXT_PLAIN, "The quick brown fox jumps over the lazy dog"),
        },
        new ExpectedFolder[] {
                new ExpectedFolder("folder1"),
                new ExpectedFolder("folder2")
        });

        List<FileInfo> folders = getFolders(folderNode, "folder1");
        assertEquals("", 1, folders.size());
        NodeRef folder1 = folders.get(0).getNodeRef();
        checkFiles(folder1, null, 1, 0, null, new ExpectedFolder[] {
                new ExpectedFolder("folder1.1")
        });

        folders = getFolders(folderNode, "folder2");
        assertEquals("", 1, folders.size());
        NodeRef folder2 = folders.get(0).getNodeRef();
        checkFiles(folder2, null, 1, 0, new ExpectedFile[] {
        },
        new ExpectedFolder[] {
                new ExpectedFolder("folder2.1")
        });

        folders = getFolders(folder1, "folder1.1");
        assertEquals("", 1, folders.size());
        NodeRef folder1_1 = folders.get(0).getNodeRef();
        checkFiles(folder1_1, null, 2, 12, new ExpectedFile[] {
                new ExpectedFile("quick.txt", MimetypeMap.MIMETYPE_TEXT_PLAIN, "The quick brown fox jumps over the lazy dog"),
                new ExpectedFile("quick.sxw", MimetypeMap.MIMETYPE_OPENOFFICE1_WRITER),
                new ExpectedFile("quick.tar", "application/x-gtar"),
        },
        new ExpectedFolder[] {
                new ExpectedFolder("folder1.1.1"),
                new ExpectedFolder("folder1.1.2")
        });

        folders = getFolders(folder2, "folder2.1");
        assertEquals("", 1, folders.size());
        NodeRef folder2_1 = folders.get(0).getNodeRef();

        checkFiles(folder2_1, null, 0, 17, new ExpectedFile[] {
                new ExpectedFile("quick.png", MimetypeMap.MIMETYPE_IMAGE_PNG),
                new ExpectedFile("quick.pdf", MimetypeMap.MIMETYPE_PDF),
                new ExpectedFile("quick.odt", MimetypeMap.MIMETYPE_OPENDOCUMENT_TEXT),
        },
        new ExpectedFolder[] {
        });
    }

    /**
     * MNT-9076: Penultimate version cannot be accessed from Share when uploading using bulkimport
     *
     * @throws Throwable
     */
    @Test
    public void testMNT9076() throws Throwable
    {
        txn = transactionService.getUserTransaction();
        txn.begin();

        NodeRef folderNode = topLevelFolder.getNodeRef();

        try
        {
            NodeImporter nodeImporter = streamingNodeImporterFactory.getNodeImporter(ResourceUtils.getFile("classpath:bulkimport2"));
            BulkImportParameters bulkImportParameters = new BulkImportParameters();
            bulkImportParameters.setTarget(folderNode);
            bulkImportParameters.setReplaceExisting(true);
            bulkImportParameters.setDisableRulesService(true);
            bulkImportParameters.setBatchSize(40);
            bulkImporter.bulkImport(bulkImportParameters, nodeImporter);
        }
        catch(Throwable e)
        {
            fail(e.getMessage());
        }

        System.out.println(bulkImporter.getStatus());
        assertEquals(false, bulkImporter.getStatus().inProgress());

        List<FileInfo> files = getFiles(folderNode, null);
        assertEquals("One file is expected to be imported:", 1, files.size());
        FileInfo file = files.get(0);
        assertEquals("File name is not equal:", "fileWithVersions.txt", file.getName());

        NodeRef file0NodeRef = file.getNodeRef();
        assertTrue("Imported file should be versioned:", versionService.isVersioned(file0NodeRef));

        VersionHistory history = versionService.getVersionHistory(file0NodeRef);
        assertEquals("Imported file should have 4 versions:", 4, history.getAllVersions().size());
        Version[] versions = history.getAllVersions().toArray(new Version[4]);

        //compare the content of each version
        ContentReader contentReader;
        contentReader = this.contentService.getReader(versions[0].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is the final version of fileWithVersions.txt.", contentReader.getContentString());

        contentReader = this.contentService.getReader(versions[1].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is version 3 of fileWithVersions.txt.", contentReader.getContentString());

        contentReader = this.contentService.getReader(versions[2].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is version 2 of fileWithVersions.txt.", contentReader.getContentString());

        contentReader = this.contentService.getReader(versions[3].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is version 1 of fileWithVersions.txt.", contentReader.getContentString());
    }

    /**
     * MNT-9067: bulkimport "Replace existing files" option does not work when versioning is enabled
     *
     * @throws Throwable
     */
    @Test
    public void testMNT9067() throws Throwable
    {
        txn = transactionService.getUserTransaction();
        txn.begin();

        NodeRef folderNode = topLevelFolder.getNodeRef();

        //initial import
        try
        {
            NodeImporter nodeImporter = streamingNodeImporterFactory.getNodeImporter(ResourceUtils.getFile("classpath:bulkimport3/initial"));
            BulkImportParameters bulkImportParameters = new BulkImportParameters();
            bulkImportParameters.setTarget(folderNode);
            bulkImportParameters.setReplaceExisting(true);
            bulkImportParameters.setDisableRulesService(true);
            bulkImportParameters.setBatchSize(40);
            bulkImporter.bulkImport(bulkImportParameters, nodeImporter);
        }
        catch(Throwable e)
        {
            fail(e.getMessage());
        }

        txn.commit();
        txn = transactionService.getUserTransaction();
        txn.begin();

        System.out.println(bulkImporter.getStatus());
        assertEquals(false, bulkImporter.getStatus().inProgress());

        List<FileInfo> files = getFiles(folderNode, null);
        assertEquals("One file is expected to be imported:", 1, files.size());
        FileInfo file = files.get(0);
        assertEquals("File name is not equal:", "fileWithVersions.txt", file.getName());

        NodeRef fileNodeRef = file.getNodeRef();
        assertTrue("Imported file should be versioned:", versionService.isVersioned(fileNodeRef));

        VersionHistory history = versionService.getVersionHistory(fileNodeRef);
        assertEquals("Imported file should have 4 versions:", 4, history.getAllVersions().size());


        //replace versioned file with new versioned file
        try
        {
            NodeImporter nodeImporter = streamingNodeImporterFactory.getNodeImporter(ResourceUtils.getFile("classpath:bulkimport3/replace_with_versioned"));
            BulkImportParameters bulkImportParameters = new BulkImportParameters();
            bulkImportParameters.setTarget(folderNode);
            bulkImportParameters.setReplaceExisting(true);
            bulkImportParameters.setDisableRulesService(true);
            bulkImportParameters.setBatchSize(40);
            bulkImporter.bulkImport(bulkImportParameters, nodeImporter);
        }
        catch(Throwable e)
        {
            fail(e.getMessage());
        }

        System.out.println(bulkImporter.getStatus());
        assertEquals(false, bulkImporter.getStatus().inProgress());

        txn.commit();
        txn = transactionService.getUserTransaction();
        txn.begin();

        files = getFiles(folderNode, null);
        assertEquals("One file is expected to be imported:", 1, files.size());
        file = files.get(0);
        assertEquals("File name is not equal:", "fileWithVersions.txt", file.getName());

        fileNodeRef = file.getNodeRef();
        assertTrue("Imported file should be versioned:", versionService.isVersioned(fileNodeRef));

        history = versionService.getVersionHistory(fileNodeRef);
        assertNotNull(history);


        assertEquals("Imported file should have 5 versions:", 5, history.getAllVersions().size());

        Version[] versions = history.getAllVersions().toArray(new Version[5]);

        //compare the content of each version
        ContentReader contentReader;
        contentReader = this.contentService.getReader(versions[0].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is the final version of replaced on import fileWithVersions.txt.", contentReader.getContentString());

        contentReader = this.contentService.getReader(versions[1].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is version 4 of replaced on import fileWithVersions.txt.", contentReader.getContentString());

        contentReader = this.contentService.getReader(versions[2].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is version 3 of replaced on import fileWithVersions.txt.", contentReader.getContentString());

        contentReader = this.contentService.getReader(versions[3].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is version 2 of replaced on import fileWithVersions.txt.", contentReader.getContentString());

        contentReader = this.contentService.getReader(versions[4].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is version 1 of replaced on import fileWithVersions.txt.", contentReader.getContentString());

        txn.commit();
        txn = transactionService.getUserTransaction();
        txn.begin();

        //import non versioned file
        try
        {
            NodeImporter nodeImporter = streamingNodeImporterFactory.getNodeImporter(ResourceUtils.getFile("classpath:bulkimport3/replace_with_non_versioned"));
            BulkImportParameters bulkImportParameters = new BulkImportParameters();
            bulkImportParameters.setTarget(folderNode);
            bulkImportParameters.setReplaceExisting(true);
            bulkImportParameters.setDisableRulesService(true);
            bulkImportParameters.setBatchSize(40);
            bulkImporter.bulkImport(bulkImportParameters, nodeImporter);
        }
        catch(Throwable e)
        {
            fail(e.getMessage());
        }

        txn.commit();
        txn = transactionService.getUserTransaction();
        txn.begin();

        System.out.println(bulkImporter.getStatus());
        assertEquals(false, bulkImporter.getStatus().inProgress());

        files = getFiles(folderNode, null);
        assertEquals("One file is expected to be imported:", 1, files.size());
        file = files.get(0);
        assertEquals("File name is not equal:", "fileWithVersions.txt", file.getName());

        fileNodeRef = file.getNodeRef();
        assertTrue("Imported file should be non versioned:", !versionService.isVersioned(fileNodeRef));

        contentReader = this.contentService.getReader(fileNodeRef, ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is non versioned fileWithVersions.txt.", contentReader.getContentString());

        txn.commit();
        txn = transactionService.getUserTransaction();
        txn.begin();

        //use initial file again to replace non versioned file
        try
        {
            NodeImporter nodeImporter = streamingNodeImporterFactory.getNodeImporter(ResourceUtils.getFile("classpath:bulkimport3/initial"));
            BulkImportParameters bulkImportParameters = new BulkImportParameters();
            bulkImportParameters.setTarget(folderNode);
            bulkImportParameters.setReplaceExisting(true);
            bulkImportParameters.setDisableRulesService(true);
            bulkImportParameters.setBatchSize(40);
            bulkImporter.bulkImport(bulkImportParameters, nodeImporter);
        }
        catch(Throwable e)
        {
            fail(e.getMessage());
        }

        txn.commit();
        txn = transactionService.getUserTransaction();
        txn.begin();

        System.out.println(bulkImporter.getStatus());
        assertEquals(false, bulkImporter.getStatus().inProgress());

        files = getFiles(folderNode, null);
        assertEquals("One file is expected to be imported:", 1, files.size());
        file = files.get(0);
        assertEquals("File name is not equal:", "fileWithVersions.txt", file.getName());

        fileNodeRef = file.getNodeRef();
        assertTrue("Imported file should be versioned:", versionService.isVersioned(fileNodeRef));

        history = versionService.getVersionHistory(fileNodeRef);
        assertNotNull(history);


        assertEquals("Imported file should have 4 versions:", 4, history.getAllVersions().size());

        versions = history.getAllVersions().toArray(new Version[4]);

        //compare the content of each version

        contentReader = this.contentService.getReader(versions[0].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is the final version of fileWithVersions.txt.", contentReader.getContentString());

        contentReader = this.contentService.getReader(versions[1].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is version 3 of fileWithVersions.txt.", contentReader.getContentString());

        contentReader = this.contentService.getReader(versions[2].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is version 2 of fileWithVersions.txt.", contentReader.getContentString());

        contentReader = this.contentService.getReader(versions[3].getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        assertNotNull(contentReader);
        assertEquals("This is version 1 of fileWithVersions.txt.", contentReader.getContentString());

    }

}
