<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var documentDetails = AlfrescoUtil.getDocumentDetails(model.nodeRef, model.site, null);
   if (documentDetails)
   {
      model.document = documentDetails.item;
      model.repositoryUrl = AlfrescoUtil.getRepositoryUrl();
   }
}

main();