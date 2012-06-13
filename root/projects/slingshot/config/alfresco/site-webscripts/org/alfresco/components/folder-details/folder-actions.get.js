<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('container', 'documentLibrary');

   var folderDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site,
   {
      actions: true
   });
   if (folderDetails)
   {
      model.folderDetails = folderDetails;
      model.folderDetailsJSON = jsonUtils.toJSONString(folderDetails);
      doclibCommon();
   }
}

main();

// Widget instantiation metadata...
model.webScriptWidgets = [];
var folderActions = {};
folderActions.name = "Alfresco.FolderActions";
folderActions.provideMessages = true;
folderActions.provideOptions = true;
folderActions.options = {};
folderActions.options.nodeRef = model.nodeRef;
folderActions.options.siteId = (model.site != null) ? model.site : null;
folderActions.options.containerId = model.container;
folderActions.options.rootNode = model.rootNode;
folderActions.options.replicationUrlMapping = (model.replicationUrlMappingJSON != null) ? model.replicationUrlMappingJSON : "{}";
folderActions.options.repositoryBrowsing = (model.rootNode != null);
folderActions.options.folderDetails = model.folderDetails;
model.webScriptWidgets.push(folderActions);