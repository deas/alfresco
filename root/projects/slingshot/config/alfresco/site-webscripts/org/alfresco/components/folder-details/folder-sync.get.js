<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var folderDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site,
   {
      actions: true
   });
   if (folderDetails)
   {
      model.folderDetails = jsonUtils.toJSONString(folderDetails);
   }
   model.syncMode = syncMode.getValue();
   model.syncEnabled = (syncMode.getValue() != "OFF");
}

main();