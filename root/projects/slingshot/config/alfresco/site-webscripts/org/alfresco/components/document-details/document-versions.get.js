<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('container', 'documentLibrary');
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (documentDetails)
   {
      var userPermissions = documentDetails.item.node.permissions.user;
      model.allowNewVersionUpload = (userPermissions["Write"] && userPermissions["Delete"]) || false;
      if (documentDetails.workingCopy && documentDetails.workingCopy.workingCopyVersion)
      {
         model.workingCopyVersion = documentDetails.workingCopy.workingCopyVersion;
      }
   }
   
   // Widget instantiation metadata...
   var documentVersions = {
      id : "DocumentVersions", 
      name : "Alfresco.DocumentVersions",
      options : {
         nodeRef : model.nodeRef,
         siteId : model.site,
         containerId : model.container,
         workingCopyVersion : model.workingCopyVersion,
         allowNewVersionUpload : model.allowNewVersionUpload
      }
   };
   
   model.widgets = [documentVersions];
}

main();

