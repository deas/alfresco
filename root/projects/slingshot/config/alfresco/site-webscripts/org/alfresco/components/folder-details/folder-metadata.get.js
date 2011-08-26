<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('formId', null);
   var folderDetails = AlfrescoUtil.getDocumentDetails(model.nodeRef, model.site);
   if (folderDetails)
   {
      model.allowMetaDataUpdate = folderDetails.item.node.permissions.user["Write"] || false;
   }
}

main();
