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

   if (nodeDetails && nodeDetails.error && nodeDetails.error.status && nodeDetails.error.status.code === 403)
   {
      // 403 returned when not is not a sync set member.
      model.synced = false;
   }
   else if (nodeDetails)
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