<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/toolbar.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/upload/uploadable.lib.js">

function widgets()
{
   var useTitle = "true";
   var docLibConfig = config.scoped["DocumentLibrary"];
   if (docLibConfig != null)
   {
      var tmp = docLibConfig["use-title"];
      useTitle = tmp != null ? tmp : "true";
   }

   var docListToolbar = {
      id: "DocListToolbar", 
      name: "Alfresco.DocListToolbar",
      options: {
         siteId: (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         rootNode: toolbar.rootNode != null ? toolbar.rootNode : "",
         hideNavBar: Boolean(toolbar.preferences.hideNavBar != null ? toolbar.preferences.hideNavBar != null : "false"),
         googleDocsEnabled: Boolean(toolbar.googleDocsEnabled != null ? toolbar.googleDocsEnabled : "false"),
         repositoryBrowsing: toolbar.rootNode != null,
         useTitle: Boolean(useTitle),
         syncMode: toolbar.syncMode != null ? toolbar.syncMode : "",
         createContentByTemplateEnabled: Boolean(model.createContentByTemplateEnabled),
         createContentActions: model.createContent
      }
   };
   model.widgets = [docListToolbar];
}

widgets();
