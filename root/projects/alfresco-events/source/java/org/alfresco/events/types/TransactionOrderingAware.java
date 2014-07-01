/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

/**
 *
 * An event that understands its position relative to the transaction in which the event occurs.
 * 
 * @author Gethin James
 * @since 5.0
 */
public interface TransactionOrderingAware
{

    public Long getSeqNumber();

}