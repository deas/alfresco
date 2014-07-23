/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.solr.cache;

/**
 * Constants for per-searcher cache names and keys.
 * 
 * @author Matt Ward
 */
public class CacheConstants
{
    public static String ALFRESCO_CACHE = "alfrescoCache";
    public static String ALFRESCO_ARRAYLIST_CACHE = "alfrescoArrayListCache";
    public static String ALFRESCO_AUTHORITY_CACHE = "alfrescoAuthorityCache";
    public static String ALFRESCO_PATH_CACHE = "alfrescoPathCache";
    public static String ALFRESCO_READER_TO_ACL_IDS_CACHE = "alfrescoReaderToAclIdsCache";
    public static String ALFRESCO_DENY_TO_ACL_IDS_CACHE = "alfrescoDenyToAclIdsCache";
    
    public static String KEY_GLOBAL_READERS = "KEY_GLOBAL_READERS";
    public static String KEY_ALL_LEAF_DOCS = "KEY_ALL_LEAF_DOCS";
    public static String KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF = "KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF";
    public static String KEY_DBID_LEAF_PATH_BY_OWNER_ID_THEN_LEAF = "KEY_DBID_LEAF_PATH_BY_OWNER_ID_THEN_LEAF";
    public static String KEY_OWNER_LOOKUP = "KEY_OWNER_LOOKUP";
    public static String KEY_ACL_LOOKUP = "KEY_ACL_LOOKUP";
    public static String KEY_PUBLIC_DOC_SET = "KEY_PUBLIC_DOC_SET";
    public static String KEY_ACL_ID_BY_DOC_ID = "KEY_ACL_ID_BY_DOC_ID";
}
