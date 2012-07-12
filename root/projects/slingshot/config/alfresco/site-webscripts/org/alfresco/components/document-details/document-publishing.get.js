<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (documentDetails)
   {
      model.document = documentDetails;
   }
}

main();


// Widget instantiation metadata...
model.widgets = [];
var documentPublishing = {};
documentPublishing.name = "Alfresco.DocumentPublishing";
documentPublishing.useMessages = true;
documentPublishing.useOptions = true;
documentPublishing.options = {};
documentPublishing.options.nodeRef = model.nodeRef;
model.widgets.push(documentPublishing);