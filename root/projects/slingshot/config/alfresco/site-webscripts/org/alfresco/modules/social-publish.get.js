<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function getDocumentChannels(nodeRef)
{
   var result = remote.call("/api/publishing/channels");
   if (result.status != 200)
   {
      AlfrescoUtil.error(result.status, 'Could not load publishing channels');
   }
   return eval('(' + result + ')').data;
}

function main() 
{
   channels = getDocumentChannels();
   model.urlLength = channels.urlLength;
   model.publishChannels = channels.publishChannels
   model.statusUpdateChannels = channels.statusUpdateChannels
}

main();