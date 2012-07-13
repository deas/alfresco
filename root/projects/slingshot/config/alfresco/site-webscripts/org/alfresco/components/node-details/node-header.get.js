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
   AlfrescoUtil.param("showDownload", "true");
   AlfrescoUtil.param("showPath", "true");
   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (nodeDetails)
   {
      model.item = nodeDetails.item;
      model.node = nodeDetails.item.node;
      model.isContainer = nodeDetails.item.node.isContainer;
      model.paths = AlfrescoUtil.getPaths(nodeDetails, model.rootPage, model.rootLabelId);
      model.showComments = ((nodeDetails.item.node.permissions.user["CreateChildren"] || false) && model.showComments).toString();
      model.showDownload = (!model.isContainer && model.showDownload).toString();
      var count = nodeDetails.item.node.properties["fm:commentCount"];
      model.commentCount = (count != undefined ? count : null);
   }
   
   
   // Widget instantiation metadata...
   var likes = {};
   if (model.item.likes != null)
   {
      likes.isLiked = model.item.likes.isLiked || false;
      likes.totalLikes = model.item.likes.totalLikes || 0;
   }

   var nodeHeader = {
      id : "NodeHeader", 
      name : "Alfresco.component.NodeHeader",
      options : {
         nodeRef : model.nodeRef,
         siteId : model.site,
         rootPage : model.rootPage,
         rootLabelId : model.rootLabelId,
         showFavourite : model.showFavourite,
         showLikes : model.showLikes,
         showComments : model.showComments,
         showDownload : model.showDownload,
         showPath : model.showPath,
         displayName : (model.item.displayName != null) ? model.item.displayName : model.item.fileName,
         likes : likes,
         isFavourite : model.item.isFavourite || false,
         isContainer : model.isContainer
      }
   };
   model.widgets = [nodeHeader];
}

main();

