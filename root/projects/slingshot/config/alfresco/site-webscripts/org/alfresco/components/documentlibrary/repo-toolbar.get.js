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

   var repoDocListToobar = {
      id: "RepositoryDocListToolbar", 
      name: "Alfresco.RepositoryDocListToolbar",
      options: {
         rootNode: toolbar.rootNode != null ? toolbar.rootNode : "",
         hideNavBar: Boolean(toolbar.preferences.hideNavBar != null ? toolbar.preferences.hideNavBar != null : "false"),
         googleDocsEnabled: Boolean(toolbar.googleDocsEnabled != null ? toolbar.googleDocsEnabled : "false"),
         repositoryBrowsing: toolbar.rootNode != null,
         useTitle: Boolean(useTitle),
         createContentByTemplateEnabled: Boolean(model.createContentByTemplateEnabled),
         createContentActions: model.createContent
      }
   };
   model.widgets = [repoDocListToobar];
}

widgets();
