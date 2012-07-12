<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('formId', null);
   var folderDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (folderDetails)
   {
      model.allowMetaDataUpdate = folderDetails.item.node.permissions.user["Write"] || false;
   }
}

main();

// Widget instantiation metadata...
model.widgets = [];
var folderMetadata = {};
folderMetadata.name = "Alfresco.FolderMetadata";
folderMetadata.useMessages = true;
folderMetadata.useOptions = true;
folderMetadata.options = {};
folderMetadata.options.nodeRef = model.nodeRef;
folderMetadata.options.siteId = model.site;
folderMetadata.options.formId = model.formId;
model.widgets.push(folderMetadata);