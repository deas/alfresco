<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (documentDetails)
   {
      model.document = documentDetails;
      model.isWorkingCopy = (documentDetails.item && documentDetails.item.workingCopy && documentDetails.item.workingCopy.isWorkingCopy) ? true : false;
   }
   
   // Widget instantiation metadata...
   var documentPublishing = {
      id : "DocumentPublishing", 
      name : "Alfresco.DocumentPublishing",
      options : {
         nodeRef : model.nodeRef
      }
   };
   model.widgets = [documentPublishing];
}

main();
