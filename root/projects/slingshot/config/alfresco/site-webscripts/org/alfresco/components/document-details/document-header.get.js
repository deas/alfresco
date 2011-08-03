<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function getPaths(documentDetails, targetPage, targetPageLabel)
{
   var document = documentDetails.item,
      path = document.location.path,
      paths = [];

   paths.push(
   {
      href: targetPage + (path.length < 2 ? "?file=" + encodeURIComponent(document.fileName) : ""),
      label: msg.get(targetPageLabel)
   });
   if (path.length > 1)
   {
      var folders = path.substring(1, path.length).split("/");
      var pathUrl = "";
      for (var x = 0, y = folders.length; x < y; x++)
      {
         pathUrl += "/" + folders[x];
         paths.push(
         {
            href: targetPage + (y - x < 2 ? "?file=" + encodeURIComponent(document.fileName) + "&path=" : "?path=") + encodeURIComponent(pathUrl),
            label: folders[x]
         });
      }
   }
   return paths;
}

function main()
{
   AlfrescoUtil.param("nodeRef");
   AlfrescoUtil.param("site", null);
   AlfrescoUtil.param("rootPage", "documentlibrary");
   AlfrescoUtil.param("rootLabelId", "path.documents");
   AlfrescoUtil.param("showFavourite", "true");
   AlfrescoUtil.param("showLikes", "true");
   AlfrescoUtil.param("showComments", "true");
   var documentDetails = AlfrescoUtil.getDocumentDetails(model.nodeRef, model.site, null);
   if (documentDetails)
   {
      model.document = documentDetails.item;
      model.node = documentDetails.item.node;
      model.paths = getPaths(documentDetails, model.rootPage, model.rootLabelId);
      model.showComments = (documentDetails.item.node.permissions.user["CreateChildren"] && model.showComments).toString();
      var count = documentDetails.item.node.properties["fm:commentCount"];
      model.commentCount = (count != undefined ? count : null);
   }
}

main();
