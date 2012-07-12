<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/toolbar.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/upload/uploadable.lib.js">

function widgets()
{
   model.widgets = [];

   var useTitle = "true";
   var docLibConfig = config.scoped["DocumentLibrary"];
   if (docLibConfig != null)
   {
      var tmp = docLibConfig["use-title"];
      useTitle = tmp != null ? tmp : "true";
   }

   var docListToolbar = {
      name : "Alfresco.DocListToolbar",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         rootNode : model.rootNode != null ? model.rootNode : "",
         hideNavBar : model.preferences.hideNavBar != null ? model.preferences.hideNavBar != null : "false",
         googleDocsEnabled : model.googleDocsEnabled != null ? model.googleDocsEnabled : "false",
         repositoryBrowsing : model.rootNode != null,
         useTitle : useTitle
      }
   };
   model.widgets.push(docListToolbar);

}

widgets();
