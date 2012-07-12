<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('formId', null);
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (documentDetails)
   {
      model.document = documentDetails;
      model.allowMetaDataUpdate = documentDetails.item.node.permissions.user["Write"] || false;
   }
   
   // Widget instantiation metadata...
   model.widgets = [];
   var documentMetadata = {
      name : "Alfresco.DocumentMetadata",
      options : {
         nodeRef : model.nodeRef,
         siteId : model.site,
         formId : model.formId
      }
   };
   model.widgets.push(documentMetadata);
}

main();

