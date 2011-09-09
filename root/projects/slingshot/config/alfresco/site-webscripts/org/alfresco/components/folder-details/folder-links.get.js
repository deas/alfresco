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
