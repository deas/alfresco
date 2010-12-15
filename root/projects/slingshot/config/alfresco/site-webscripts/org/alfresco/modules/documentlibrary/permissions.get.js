var json = remote.call("/api/sites/" + args.site + "/roles");
var roles = eval('(' + json + ')');

var groupNames = [];
var permGroups = [];
var permissionGroups = roles.permissionGroups;
for (group in permissionGroups)
{
   // strip group name down to group identifier
   var permissionGroup = permissionGroups[group];
   var groupName = permissionGroup.substring(permissionGroup.lastIndexOf("_") + 1);
   
   // ignore the SiteManager group as we do not allow it to be modified
   if (groupName != "SiteManager")
   {
      groupNames.push(groupName);
      permGroups.push(permissionGroup);
   }
}

var roleNames = [];
var siteRoles = roles.siteRoles;
for (role in siteRoles)
{
   var roleName = siteRoles[role];
   
   // ignore the SiteManager role as we do not allow it to be applied
   if (roleName != "SiteManager")
   {
      roleNames.push(roleName);
   }
}

model.siteRoles = roleNames;
model.permissionGroups = permGroups;
model.groupNames = groupNames;