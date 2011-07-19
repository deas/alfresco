<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function getPaths(folderDetails, targetPage, targetPageLabel)
{
   var folder = folderDetails.item,
      path = folder.location.path,
      paths = [];
   
   paths.push(
   {
      href: targetPage + (path == "/" && folder.location.file.length > 0 ? "?file=" + encodeURIComponent(folder.fileName) : ""),
      label: msg.get(targetPageLabel)
   });

   path = AlfrescoUtil.combinePaths(path, folder.location.file);
   if (path.length > 1)
   {
      var folders = path.substring(1, path.length).split("/"),
         pathUrl = "";

      for (var x = 0, y = folders.length; x < y; x++)
      {
         pathUrl += "/" + folders[x];
         paths.push(
         {
            href: targetPage + (y - x == 2 ? "?file=" + encodeURIComponent(folder.fileName) + "&path=" : "?path=") + encodeURIComponent(pathUrl),
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
   AlfrescoUtil.param("showComments", "false");
   var folderDetails = AlfrescoUtil.getDocumentDetails(model.nodeRef, model.site, null);
   if (folderDetails)
   {
      model.folder = folderDetails.item;
      model.node = folderDetails.item.node;
      model.paths = getPaths(folderDetails, model.rootPage, model.rootLabelId);
      model.showComments = (folderDetails.item.node.permissions.user["CreateChildren"] || false).toString();
   }
}

main();
