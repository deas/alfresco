<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

var documentDetails = AlfrescoUtil.getDocumentDetails(args.nodeRef, null);
if (documentDetails)
{
   model.document = documentDetails.item;
}