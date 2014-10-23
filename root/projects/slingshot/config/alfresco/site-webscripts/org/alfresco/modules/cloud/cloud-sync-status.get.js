<import resource="classpath:alfresco/site-webscripts/org/alfresco/config.lib.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param("nodeRef"); // pass in localNodeRef.
   AlfrescoUtil.param("rootPage", "documentlibrary");
   AlfrescoUtil.param("rootLabelId", msg.get("location.path.documents"));
   model.synced = true;

   model.syncMode = syncMode.value;
   var remoteNodeInfo = AlfrescoUtil.getRemoteNodeRef(model.nodeRef),
      nodeDetails = null;

   if (remoteNodeInfo && remoteNodeInfo.remoteNodeRef)
   {
      nodeDetails = AlfrescoUtil.getRemoteNodeDetails(remoteNodeInfo.remoteNodeRef, remoteNodeInfo.remoteNetworkId);
   }
   else if (remoteNodeInfo && remoteNodeInfo.remoteParentNodeRef)
   {
      model.isParentPath = true;
      nodeDetails = AlfrescoUtil.getRemoteNodeDetails(remoteNodeInfo.remoteParentNodeRef, remoteNodeInfo.remoteNetworkId);
   }
   
   if (nodeDetails && nodeDetails.error && nodeDetails.error.status)
   {
      model.error = {
         code: nodeDetails.error.status.code, 
         message: nodeDetails.error.message};
   }

   if (nodeDetails && nodeDetails.error && nodeDetails.error.status && nodeDetails.error.status.code === 403)
   {
      // 403 returned when not is not a sync set member.
      model.synced = false;
      model.error.message = msg.get("sync.status.unknown-location.unauthorized");
   }
   else if (nodeDetails && nodeDetails.error && nodeDetails.error.status && nodeDetails.error.status.code === 410)
   {
      // 410 returned if there is no permissions to get node details
      model.error.message = msg.get("sync.status.unknown-location.no-permissions");
   }
   // there is no error here but we unable to build node path
   else if (nodeDetails && !nodeDetails.error && !nodeDetails.item.location.site)
   {
      model.error = {
         code: 410, 
         message: msg.get("sync.status.unknown-location.no-permissions")};
   }
   else if (nodeDetails && !nodeDetails.error)
   {
      model.nodeFound = true;
      model.item = nodeDetails.item;
      model.node = nodeDetails.item.node;
      model.paths = AlfrescoUtil.getPaths(nodeDetails, model.rootPage, model.rootLabelId);
      model.site = nodeDetails.item.location.site;
      model.remoteNetworkId = remoteNodeInfo.remoteNetworkId;
      model.nodeTitle = nodeDetails.item.node.properties["cm:name"];
      model.shareURL = nodeDetails.metadata.shareURL;
      model.isContainer = nodeDetails.item.node.isContainer;
      model.isDirectSync = nodeDetails.item.node.properties["sync:directSync"];
      model.rootNodeRef = remoteNodeInfo.localRootNodeRef;
      model.rootNodeName = remoteNodeInfo.localRootNodeName;
      model.syncOwnerFullName = (remoteNodeInfo.syncSetOwnerFirstName + " " + remoteNodeInfo.syncSetOwnerLastName).replace(/^\s+|\s+$/g, "");
   }
}

main();