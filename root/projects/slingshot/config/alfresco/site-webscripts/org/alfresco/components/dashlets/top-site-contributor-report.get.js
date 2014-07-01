model.jsonModel = {
   rootNodeId: args.htmlid,
   services: ["alfresco/services/ReportService"],
   widgets: [
      {
         id: "DASHLET",
         name: "alfresco/dashlets/TopSiteContributorReportDashlet",
         config: {
            generatePubSubScope: true
         }
      }
   ]
};
