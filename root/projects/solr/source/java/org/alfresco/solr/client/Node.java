package org.alfresco.solr.client;


public class Node
{
    public static enum STATUS
    {
        UPDATED, DELETED, UNKNOWN;
    };

    private long id;
    private long txnId;
    private STATUS status;
    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    public long getTxnId()
    {
        return txnId;
    }
    public void setTxnId(long txnId)
    {
        this.txnId = txnId;
    }
    public STATUS getStatus()
    {
        return status;
    }
    public void setStatus(STATUS status)
    {
        this.status = status;
    }
    @Override
    public String toString()
    {
        return "NodeInfo [id=" + id + ", txnId=" + txnId + ", status=" + status + "]";
    }

    
}
