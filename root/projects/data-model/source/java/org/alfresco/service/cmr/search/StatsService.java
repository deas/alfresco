package org.alfresco.service.cmr.search;
/**
 * 
 * A service that returns stats about the Alfresco repository
 * 
 * @author Gethin James
 * @since 5.0
 */
public interface StatsService
{
    /**
     * Query the repository for information
     * @param searchParameters parameter to use
     * @return StatsResultSet results of the search
     */
    StatsResultSet query(StatsParameters searchParameters);
}
