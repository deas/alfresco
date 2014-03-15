<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/document-library.lib.js">

// TODO: This currently only contains the model for the main content (i.e. it will work with the hybrid-template)
//       but ideally we should sort out all the lib files so that it runs in the full template...
//       /share/page/dp/ws/manage-sites as opposed to /share/page/hdp/ws/manage-sites


model.jsonModel = {
   services: services,
   widgets: [
      {
         id: "SET_PAGE_TITLE",
         name: "alfresco/header/SetTitle",
         config: {
            title: msg.get("manage-sites.page.title")
         }
      },
      {
         id: "SHARE_VERTICAL_LAYOUT",
         name: "alfresco/layout/VerticalWidgets",
         config: 
         {
            widgets: widgets
         }
      }
   ]
};
