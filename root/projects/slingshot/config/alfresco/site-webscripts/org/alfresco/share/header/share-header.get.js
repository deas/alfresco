<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">

model.jsonModel = {
   rootNodeId: "share-header",
   services: [
      "alfresco/services/NavigationService",
      "alfresco/services/UserService",
      "alfresco/services/SiteService"
   ],   
   widgets: [
      {
         id: "SHARE_VERTICAL_LAYOUT",
         name: "alfresco/layout/VerticalWidgets",
         config: 
         {
            widgets: getHeaderModel()
         }
      }
   ]
};
