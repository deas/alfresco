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
      id : "RepositoryDocListToolbar", 
      name : "Alfresco.RepositoryDocListToolbar",
      options : {
         rootNode : model.rootNode != null ? model.rootNode : "",
         hideNavBar : Boolean(model.preferences.hideNavBar != null ? model.preferences.hideNavBar != null : "false"),
         googleDocsEnabled : Boolean(model.googleDocsEnabled != null ? model.googleDocsEnabled : "false"),
         repositoryBrowsing : model.rootNode != null,
         useTitle : Boolean(useTitle)
      }
   };
   model.widgets = [repoDocListToobar];
}

widgets();

