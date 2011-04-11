<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/document-details/dod5015-document-details.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('container', 'documentLibrary');

   var documentDetails = getDod5015DocumentDetails(model.nodeRef, model.site, null);
   if (documentDetails)
   {
      model.documentDetailsJSON = jsonUtils.toJSONString(documentDetails);
      doclibCommon();
   }
}

main();