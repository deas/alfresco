<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('container', 'documentLibrary');

   var folderDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site,
   {
      actions: true
   });
   if (folderDetails)
   {
      model.folderDetails = folderDetails;
      model.folderDetailsJSON = jsonUtils.toJSONString(folderDetails);
      doclibCommon();
   }
   
   // Widget instantiation metadata...
   model.widgets = [];
   var folderActions = {
      name : "Alfresco.FolderActions",
      options : {
         nodeRef : model.nodeRef,
         siteId : (model.site != null) ? model.site : null,
         containerId : model.container,
         rootNode : model.rootNode,
         replicationUrlMapping : (model.replicationUrlMappingJSON != null) ? model.replicationUrlMappingJSON : "{}",
         repositoryBrowsing : (model.rootNode != null),
         folderDetails : model.folderDetails
      }
   };
   model.widgets.push(folderActions);
}

main();

