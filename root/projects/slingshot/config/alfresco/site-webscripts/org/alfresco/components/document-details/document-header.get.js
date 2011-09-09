<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param("nodeRef");
   AlfrescoUtil.param("site", null);
   AlfrescoUtil.param("rootPage", "documentlibrary");
   AlfrescoUtil.param("rootLabelId", "path.documents");
   AlfrescoUtil.param("showFavourite", "true");
   AlfrescoUtil.param("showLikes", "true");
   AlfrescoUtil.param("showComments", "true");
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (documentDetails)
   {
      model.document = documentDetails.item;
      model.node = documentDetails.item.node;
      model.paths = AlfrescoUtil.getPaths(documentDetails, model.rootPage, model.rootLabelId);
      model.showComments = (documentDetails.item.node.permissions.user["CreateChildren"] && model.showComments).toString();
      var count = documentDetails.item.node.properties["fm:commentCount"];
      model.commentCount = (count != undefined ? count : null);
   }
}

main();
