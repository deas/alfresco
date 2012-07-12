<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

doclibCommon();

function main()
{
   model.widgets = [];

   var documentList = {
      name : "Alfresco.DocumentList",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : template.properties.container != null ? template.properties.container : "documentLibrary",
         rootNode : model.rootNode != null ? model.rootNode : "null",
         usePagination : args.pagination != null ? args.pagination : false,
         sortAscending : model.preferences.sortAscending != null ? model.preferences.sortAscending : true,
         sortField : model.preferences.sortField != null ? model.preferences.sortField : "cm:name",
         showFolders : model.preferences.showFolders != null ? model.preferences.showFolders : true,
         simpleView : model.preferences.simpleView != null ? model.preferences.simpleView : "null",
         viewRendererName : model.preferences.viewRendererName != null ? model.preferences.viewRendererName : "detailed",
         viewRendererNames : model.viewRendererNames != null ? model.viewRendererNames : ["simple", "detailed"],
         highlightFile : page.url.args["file"] != null ? page.url.args["file"] : "",
         replicationUrlMapping : model.replicationUrlMapping != null ? model.replicationUrlMapping : "{}",
         repositoryBrowsing : model.repositoryBrowsing != null, 
         useTitle : model.useTitle != null ? model.useTitle : true,
         userIsSiteManager : model.userIsSiteManager != null ? model.userIsSiteManager : false
      }
   };
   if (model.repositoryUrl != null)
   {
      documentList.options.repositoryUrl = model.repositoryUrl;
   }
   
   model.widgets.push(documentList);
}

main();
