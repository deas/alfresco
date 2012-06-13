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
}

main();

// Widget instantiation metadata...
model.webScriptWidgets = [];
var documentVersions = {};
documentVersions.name = "Alfresco.DocumentVersions";
documentVersions.provideMessages = true;
documentVersions.provideOptions = true;
documentVersions.options = {};
documentVersions.options.nodeRef = model.nodeRef;
documentVersions.options.siteId = model.site;
documentVersions.options.containerId = model.container;
documentVersions.options.workingCopyVersion = model.workingCopyVersion;
documentVersions.options.allowNewVersionUpload = model.allowNewVersionUpload;
model.webScriptWidgets.push(documentVersions);
