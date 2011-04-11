<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

function main()
{
   AlfrescoUtil.param('nodeRef');

   var documentDetails = AlfrescoUtil.getDocumentDetails(model.nodeRef, null, null);
   if (documentDetails)
   {
      model.documentDetailsJSON = jsonUtils.toJSONString(documentDetails);
      doclibCommon();
   }
}

main();