/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

import java.io.Serializable;

public enum EventType implements Serializable
{
	CONTENTGET, CONTENTPUT, NODEFAVOURITED, NODEUNFAVOURITED, NODECOMMENTED, NODECOMMENTSLISTED, NODELIKED, NODEUNLIKED, NODEUPDATED, NODETAGGED,
	NODETAGREMOVED, NODEADDED, NODEREMOVED, NODEVERSIONED, EXCEPTIONGENERATED, SYNCSUBSCRIBE, SYNCUNSUBSCRIBE;
}
