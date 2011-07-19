<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function setPermissions(folderDetails)
{
   var rawPerms = folderDetails.item.node.permissions.roles,
      permParts,
      group,
      permission;

   model.roles = rawPerms;
   model.readPermission = false;

   if (rawPerms.length > 0)
   {
      model.readPermission = true;
      model.managers = "None";
      model.collaborators = "None";
      model.contributors = "None";
      model.consumers = "None";
      model.everyone = "None";

      for (i = 0, ii = rawPerms.length; i < ii; i++)
      {
         permParts = rawPerms[i].split(";");
         group = permParts[1];
         permission = permParts[2]
         if (group.indexOf("_SiteManager") != -1)
         {
            model.managers = permission;
         }
         else if (group.indexOf("_SiteCollaborator") != -1)
         {
            model.collaborators = permission;
         }
         else if (group.indexOf("_SiteContributor") != -1)
         {
            model.contributors = permission;
         }
         else if (group.indexOf("_SiteConsumer") != -1)
         {
            model.consumers = permission;
         }
         else if (group.indexOf("GROUP_EVERYONE") == 0)
         {
            model.everyone = permission;
         }
      }
   }
}

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);

   var folderDetails = AlfrescoUtil.getDocumentDetails(model.nodeRef, model.site, null);
   if (folderDetails)
   {
      setPermissions(folderDetails);
      model.allowPermissionsUpdate = folderDetails.item.node.permissions.user["ChangePermissions"] || false;
      model.displayName = folderDetails.item.displayName;
   }
}

main();
