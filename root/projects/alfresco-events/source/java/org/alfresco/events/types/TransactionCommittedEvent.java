/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

/**
 * Transaction committed event.
 * 
 * @author steveglover
 *
 */
public class TransactionCommittedEvent extends TransactionEvent
{
	private static final long serialVersionUID = -921613586043213951L;

	public static final String EVENT_TYPE = "TRANSACTION_COMMITTED";

	public TransactionCommittedEvent()
	{
	}

	public TransactionCommittedEvent(long seqNumber, String txnId, String networkId, long timestamp, String username)
	{
		super(seqNumber, EVENT_TYPE, txnId, networkId, timestamp, username);
	}

	@Override
	public String toString()
	{
		return "TransactionCommittedEvent [id=" + id + ", type=" + type
				+ ", txnId=" + txnId + ", timestamp=" + timestamp + "]";
	}
}
