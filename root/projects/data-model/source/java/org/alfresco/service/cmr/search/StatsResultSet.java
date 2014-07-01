package org.alfresco.service.cmr.search;

import java.util.List;

/**
 * A result of a search for Stats
 *
 * @author Gethin James
 * @since 5.0
 */
public interface StatsResultSet
{
    long getNumberFound(); //long For compatibility with SolrJSONResultSet
    Long getSum();
    Long getMax();
    Long getMean();
    List<StatsResultStat> getStats();
}
