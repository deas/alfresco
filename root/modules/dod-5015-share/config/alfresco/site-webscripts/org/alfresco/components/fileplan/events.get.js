<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (!documentDetails)
   {
      // Signal to the template that the node doesn't exist and that events therefore shouldn't be displayed.
      model.nodeRef = null;
   }
}

main();
