<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function getDocumentWorkflows(nodeRef)
{
   var result = remote.call("/api/node/" + nodeRef.replace(":/", "") + "/workflow-instances");
   if (result.status != 200)
   {
      AlfrescoUtil.error(result.status, 'Could not load document workflows for ' + nodeRef);
   }
   return eval('(' + result + ')').data;
}

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (documentDetails)
   {
      model.destination = documentDetails.item.parent.nodeRef
      model.workflows = getDocumentWorkflows(model.nodeRef);
   }
}

main();

// Widget instantiation metadata...
model.webScriptWidgets = [];
var documentWorkflows = {};
documentWorkflows.name = "Alfresco.DocumentWorkflows";
documentWorkflows.provideMessages = true;
documentWorkflows.provideOptions = true;
documentWorkflows.options = {};
documentWorkflows.options.nodeRef = model.nodeRef;
documentWorkflows.options.siteId = model.site;
documentWorkflows.options.destination = model.destination;
model.webScriptWidgets.push(documentWorkflows);