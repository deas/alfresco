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
* A TransactionEvent event that is also aware of its ordering.
* 
* @author Gethin James
* @since 5.0
*/
public abstract class TransactionEvent extends RepositoryEventImpl implements TransactionOrderingAware
{
    private static final long serialVersionUID = -3217767152720757859L;
    
    protected Long seqNumber;
    
    public TransactionEvent()
    {
        super();
    }

    public TransactionEvent(long seqNumber, String type, String txnId, String networkId, long timestamp,
                String username)
    {
        super(type, txnId, networkId, timestamp, username);
        this.seqNumber = seqNumber;
    }

    @Override
    public Long getSeqNumber()
    {
        return this.seqNumber;
    }

    public void setSeqNumber(Long seqNumber)
    {
        this.seqNumber = seqNumber;
    }
    


}
