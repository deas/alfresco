<?php

$file = $_ALF_MODEL["file"];
$folder = $_ALF_MODEL["folder"];

assertTrue($file instanceof File, "$file is not a File");
assertTrue($folder instanceof Folder, "$folder is not a folder");

// Check the file content properties
assertEquals("UTF-8", $file->encoding);
assertEquals("text/plain", $file->mimetype);
assertEquals(12, $file->size);
assertEquals("test content", $file->content);
assertNotNull($file->url);
$file->encoding = "UTF-16";
$file->mimetype = "text/xml";
$file->content = "12341234";
assertEquals("UTF-16", $file->encoding);
assertEquals("text/xml", $file->mimetype);
assertEquals(8, $file->size);
assertEquals("12341234", $file->content);
$_ALF_SESSION->save();
assertEquals("UTF-16", $file->encoding);
assertEquals("text/xml", $file->mimetype);
//assertEquals(8, $file->size);
assertEquals("12341234", $file->content);
$contentData = $file->cm_content;
assertNotNull($contentData);
assertEquals("UTF-16", $contentData->encoding);
assertEquals("text/xml", $contentData->mimetype);
//assertEquals(8, $contentData->size);
assertEquals("12341234", $contentData->content);
		
// Create folder
$subFolder = $folder->createFolder("mySubFolder");
assertNotNull($subFolder);
assertEquals("mySubFolder", $subFolder->cm_name);	
assertEquals($folder, $subFolder->primaryParent);
assertEquals(2, count($folder->children));
$_ALF_SESSION->save();
echo "sub folder node: ".$subFolder->__toString();
assertEquals("mySubFolder", $subFolder->cm_name);	
assertEquals($folder, $subFolder->primaryParent);
assertEquals(2, count($folder->children));
	
// Create file
$subFile = $folder->createFile("mytestFile1.txt");
assertNotNull($subFile, "sub file should not be null");
assertEquals("mytestFile1.txt", $subFile->cm_name);	
assertEquals($folder, $subFile->primaryParent);
assertNull($subFile->cm_content, "cm_content should be null");
assertEquals(3, count($folder->children));
$_ALF_SESSION->save();
assertEquals("mytestFile1.txt", $subFile->cm_name);	
assertEquals($folder, $subFile->primaryParent);
assertNull($subFile->cm_content, "after save cm_content should not be null");
assertEquals(3, count($folder->children));

// Create file with content
$subFile2 = $folder->createFile("mytestFile2.txt");
assertNotNull($subFile2, "subFile2 should not be null");
$subFile2->updateContent("cm_content", "text/plain", "UTF-8", "12345");
assertEquals("mytestFile2.txt", $subFile2->cm_name);	
assertEquals($folder, $subFile2->primaryParent);
assertNotNull($subFile2->cm_content, "cm_content should not be be null before save");
assertEquals("text/plain", $subFile2->mimetype);
assertEquals("UTF-8", $subFile2->encoding);
assertEquals("12345", $subFile2->content);
assertEquals(4, count($folder->children));
$_ALF_SESSION->save();
assertEquals("mytestFile2.txt", $subFile2->cm_name);	
assertEquals($folder, $subFile2->primaryParent);
assertNotNull($subFile2->cm_content, "cm_content should not be null after save");
assertEquals("text/plain", $subFile2->mimetype);
assertEquals("UTF-8", $subFile2->encoding);
assertEquals("12345", $subFile2->content);
assertEquals(4, count($folder->children));

// Check the file list
$files = $folder->files;
assertNotNull($files, "files unexpectedly null");
assertEquals(3, count($files));

// Check the folder list
$folders = $folder->folders;
assertNotNull($folders, "folders unexpectedly null");
assertEquals(1, count($folders));
	
		
?>