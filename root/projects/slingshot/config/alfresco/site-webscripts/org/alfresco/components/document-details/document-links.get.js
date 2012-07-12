<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (documentDetails)
   {
      model.document = documentDetails.item;
      model.repositoryUrl = AlfrescoUtil.getRepositoryUrl();
   }
}

main();

// Widget instantiation metadata...
model.widgets = [];
var documentActions = {};
documentActions.name = "Alfresco.DocumentLinks";
documentActions.useMessages = true;
documentActions.useOptions = true;
documentActions.options = {};
documentActions.options.nodeRef = model.nodeRef;
documentActions.options.siteId = (model.site != null) ? model.site : null;
model.widgets.push(documentActions);