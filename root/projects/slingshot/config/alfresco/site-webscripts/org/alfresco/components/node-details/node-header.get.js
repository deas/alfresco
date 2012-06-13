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
}

main();

// Widget instantiation metadata...
model.webScriptWidgets = [];
var nodeHeader = {};
nodeHeader.name = "Alfresco.component.NodeHeader";
nodeHeader.provideOptions = true;
nodeHeader.provideMessages = true;
nodeHeader.options = {};
nodeHeader.options.nodeRef = model.nodeRef;
nodeHeader.options.siteId = model.site;
nodeHeader.options.rootPage = model.rootPage;
nodeHeader.options.rootLabelId = model.rootLabelId;
nodeHeader.options.showFavourite = model.showFavourite;
nodeHeader.options.showLikes = model.showLikes;
nodeHeader.options.showComments = model.showComments;
nodeHeader.options.showDownload = model.showDownload;
nodeHeader.options.showPath = model.showPath;
nodeHeader.options.displayName = (model.item.displayName != null) ? model.item.displayName : model.item.fileName;
var likes = {};
if (model.item.likes != null)
{
   likes.isLiked = model.item.likes.isLiked || false;
   likes.totalLikes = model.item.likes.totalLikes || 0;
}
nodeHeader.options.likes = likes;
nodeHeader.options.isFavourite = model.item.isFavourite || false;
nodeHeader.options.isContainer = model.isContainer;
model.webScriptWidgets.push(nodeHeader);