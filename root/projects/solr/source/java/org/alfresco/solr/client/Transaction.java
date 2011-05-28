package org.alfresco.solr.client;

public class Transaction
{
    private long id;
    private long commitTimeMs;
    private long updates;
    private long deletes;

    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    public long getCommitTimeMs()
    {
        return commitTimeMs;
    }
    public void setCommitTimeMs(long commitTimeMs)
    {
        this.commitTimeMs = commitTimeMs;
    }
    public long getUpdates()
    {
        return updates;
    }
    public void setUpdates(long updates)
    {
        this.updates = updates;
    }
    public long getDeletes()
    {
        return deletes;
    }
    public void setDeletes(long deletes)
    {
        this.deletes = deletes;
    }
    @Override
    public String toString()
    {
        return "TransactionInfo [id=" + id + ", commitTimeMs=" + commitTimeMs + ", updates=" + updates + ", deletes="
                + deletes + "]";
    }
}
