<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param("nodeRef");
   AlfrescoUtil.param("site", null);
   AlfrescoUtil.param("rootPage", "documentlibrary");
   AlfrescoUtil.param("rootLabelId", "path.documents");
   AlfrescoUtil.param("showFavourite", "true");
   AlfrescoUtil.param("showLikes", "true");
   AlfrescoUtil.param("showComments", "false");
   var folderDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (folderDetails)
   {
      model.folder = folderDetails.item;
      model.node = folderDetails.item.node;
      model.paths = AlfrescoUtil.getPaths(folderDetails, model.rootPage, model.rootLabelId);
      model.showComments = (folderDetails.item.node.permissions.user["CreateChildren"] || false).toString();
   }
}

main();
