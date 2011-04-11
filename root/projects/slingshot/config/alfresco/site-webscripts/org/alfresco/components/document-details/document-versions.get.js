<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('container', 'documentLibrary');
   var documentDetails = AlfrescoUtil.getDocumentDetails(model.nodeRef, model.site, null);
   if (documentDetails)
   {
      model.allowNewVersionUpload = documentDetails.item.permissions.userAccess.edit || false;
      if (documentDetails.item.custom && documentDetails.item.custom.workingCopyVersion)
      {
         model.workingCopyVersion = documentDetails.item.custom.workingCopyVersion;
      }
   }
}

main();
