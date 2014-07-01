/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

/**
 * Exception generated event bean. Used to convey a repository exception event.
 * 
 * @author steveglover
 *
 */
public class ExceptionGeneratedEvent extends RepositoryEventImpl implements TransactionOrderingAware
{
	private static final long serialVersionUID = -7936576493028335953L;

	public static final String EVENT_TYPE = "EXCEPTIONGENERATED";
	// Seq number relative to the transaction in which the event occurs
	protected Long seqNumber;
	private Throwable cause;

	public ExceptionGeneratedEvent()
	{
	}

	public ExceptionGeneratedEvent(long seqNumber, String txnId, long time, String networkId, Throwable cause, String username)
	{
		super(EVENT_TYPE, txnId, networkId, time, username);
        this.seqNumber = seqNumber;
		this.cause = cause;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause)
	{
		this.cause = cause;
	}

	@Override
	public Long getSeqNumber() {
		return seqNumber;
	}

	@Override
	public String toString()
	{
		return "ExceptionGeneratedEvent [cause=" + cause + ", type=" + type + ", txnId=" + txnId + ", timestamp=" + timestamp
				+ "]";
	}


}
