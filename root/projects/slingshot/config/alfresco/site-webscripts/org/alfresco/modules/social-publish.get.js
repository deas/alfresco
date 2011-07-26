<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function getDocumentChannels(nodeRef)
{
   var result = remote.call("/api/publishing/" + nodeRef.replace("://", "/") + "/channels");
   if (result.status != 200)
   {
      AlfrescoUtil.error(result.status, 'Could not load publishing channels');
   }
   return eval('(' + result + ')').data;
}

function main() 
{
   AlfrescoUtil.param('nodeRef');
   channels = getDocumentChannels(model.nodeRef);
   model.urlLength = channels.urlLength;
   model.publishChannels = channels.publishChannels
   model.statusUpdateChannels = channels.statusUpdateChannels
}

main();