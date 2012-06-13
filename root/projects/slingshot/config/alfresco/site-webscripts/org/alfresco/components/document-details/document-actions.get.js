<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('container', 'documentLibrary');

   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site,
   {
      actions: true
   });
   if (documentDetails)
   {
      model.documentDetails = documentDetails;
      model.documentDetailsJSON = jsonUtils.toJSONString(documentDetails);
      doclibCommon();
   }
}

main();

// Widget instantiation metadata...
model.webScriptWidgets = [];
var documentActions = {};
documentActions.name = "Alfresco.DocumentActions";
documentActions.provideMessages = true;
documentActions.provideOptions = true;
documentActions.options = {};
documentActions.options.nodeRef = model.nodeRef;
documentActions.options.siteId = (model.site != null) ? model.site : null;
documentActions.options.containerId = model.container;
documentActions.options.rootNode = model.rootNode;
documentActions.options.replicationUrlMapping = (model.replicationUrlMappingJSON != null) ? model.replicationUrlMappingJSON : "{}";
documentActions.options.documentDetails = model.documentDetails;
documentActions.options.repositoryBrowsing = (model.rootNode != null);
model.webScriptWidgets.push(documentActions);
