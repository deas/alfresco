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
model.webScriptWidgets = [];
var documentPublishing = {};
documentPublishing.name = "Alfresco.DocumentPublishing";
documentPublishing.provideMessages = true;
documentPublishing.provideOptions = true;
documentPublishing.options = {};
documentPublishing.options.nodeRef = model.nodeRef;
model.webScriptWidgets.push(documentPublishing);