<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var folderDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (folderDetails)
   {
      model.folder = folderDetails.item;
      var repositoryUrl = AlfrescoUtil.getRepositoryUrl(),
         webdavUrl = folderDetails.item.webdavUrl;

      if (repositoryUrl && webdavUrl)
      {
         model.webdavUrl = AlfrescoUtil.combinePaths(repositoryUrl, webdavUrl);
      }
   }
}

main();

// Widget instantiation metadata...
model.widgets = [];
var folderLinks = {};
folderLinks.name = "Alfresco.FolderLinks";
folderLinks.useMessages = true;
folderLinks.useOptions = true;
folderLinks.options = {};
folderLinks.options.nodeRef = model.nodeRef;
folderLinks.options.siteId = (model.site != null) ? model.site : null;
model.widgets.push(folderLinks);
