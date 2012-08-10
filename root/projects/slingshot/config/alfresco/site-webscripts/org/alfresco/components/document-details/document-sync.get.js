<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site,
   {
      actions: true
   });
   if (documentDetails)
   {
      model.documentDetails = jsonUtils.toJSONString(documentDetails);
   }
   model.syncMode = syncMode.getValue();
   model.syncEnabled = (syncMode.getValue() != "OFF");
}

main();