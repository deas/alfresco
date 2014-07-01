/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events;

/**
 * Mongo property names for events.
 * 
 * @author steveglover
 *
 */
public interface Fields
{
    public static final String FIELD_ID = "id";
    public static final String FIELD_CLIENT_ID = "clientId";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_NODE_NEW_NAME = "newNodeName";
    public static final String FIELD_CHECKED_OUT_NODE_ID = "checkedOutNodeId";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_NODE_ID = "nodeId";
    public static final String FIELD_REMOVED = "removed";
    public static final String FIELD_OBJECT_ID = "objectId";
    public static final String FIELD_TXN_ID = "txnId";
    public static final String FIELD_SEQ_NO = "seqNo";
    public static final String FIELD_COMMIT_TIME = "commitTime";
    public static final String FIELD_EVENT_TIMESTAMP = "eventTimestamp";
    public static final String FIELD_TIMESTAMP = "timestamp";
    public static final String FIELD_NETWORK_ID = "networkId";
	public static final String FIELD_SITE_ID = "siteId";
	public static final String FIELD_IS_SITE = "isSite";
	public static final String FIELD_USER_ID = "userId";
	public static final String FIELD_PATH_GUID = "pathGuid";
	public static final String FIELD_PATHS = "paths";
	public static final String FIELD_PARENT_NODE_IDS = "parentNodeIds";
	public static final String FIELD_TO_PATHS = "toPaths";
	public static final String FIELD_TO_PARENT_NODE_IDS = "toParentNodeIds";
	public static final String FIELD_NODE_TYPE = "nodeType";
	public static final String FIELD_ASPECTS = "nodeAspects";
	public static final String FIELD_PROPERTIES = "nodeProperties";
	public static final String FIELD_NODE_MODIFICATION_TIME = "nodeModificationTime";
	public static final String FIELD_IS_FILTERED = "isFiltered";
	public static final String FIELD_SIZE = "size";
	public static final String FIELD_MIME_TYPE = "mimeType";
	public static final String FIELD_ENCODING = "encoding";
    public static final String FIELD_OLD_PARENT_NODE_ID = "oldParentNodeId";
    public static final String FIELD_NEW_PARENT_NODE_ID = "newParentNodeId";
    public static final String FIELD_CHANGE_TYPE = "changeType";
    public static final String FIELD_PRIMARY_PATH = "primaryPath";
    public static final String FIELD_PRIMARY_TO_PATH = "primaryToPath";
    public static final String FIELD_CHECKSUM = "checksum";
    public static final String FIELD_SUBSCRIBER_ID = "subscriberId";
    public static final String FIELD_SUBSCRIPTION_IDS = "subscriptionIds";
    public static final String FIELD_CREATED_AT = "createdAt";
    public static final String FIELD_TXN_STATE = "txnState";
    public static final String FIELD_SKIP = "skip";
    public static final String FIELD_ERROR = "error";
}
