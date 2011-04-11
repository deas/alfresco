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
   var documentDetails = AlfrescoUtil.getDocumentDetails(model.nodeRef, model.site, null);
   if (documentDetails)
   {
      model.destination = documentDetails.metadata.parent.nodeRef
      model.workflows = getDocumentWorkflows(model.nodeRef);
   }
}

main();
