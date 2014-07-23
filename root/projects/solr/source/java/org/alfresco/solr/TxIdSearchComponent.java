package org.alfresco.solr;

import java.io.IOException;

import org.alfresco.solr.tracker.CoreTracker;
import org.alfresco.solr.tracker.Tracker;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.request.SolrQueryRequest;

public class TxIdSearchComponent extends SearchComponent
{
    @Override
    public void prepare(ResponseBuilder rb) throws IOException
    {
        // No preparation required.
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException
    {
        SolrQueryRequest req = rb.req;
        AlfrescoCoreAdminHandler adminHandler = (AlfrescoCoreAdminHandler)
                    req.getCore().
                    getCoreDescriptor().
                    getCoreContainer().
                    getMultiCoreHandler();
        InformationServer infoSrv = adminHandler.getInformationServers().get(req.getCore().getName());
        long lastIndexedTx = infoSrv.getTrackerState().getLastIndexedTxId();
        rb.rsp.add("lastIndexedTx", lastIndexedTx);
    }

    @Override
    public String getDescription()
    {
        return "Adds the last indexed transaction ID to the search results.";
    }

    @Override
    public String getSourceId()
    {
        return "";
    }

    @Override
    public String getSource()
    {
        return "http://www.alfresco.com";
    }

    @Override
    public String getVersion()
    {
        return "1.0";
    }

}
