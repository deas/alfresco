/**
 * Site Members component GET method
 */
function main()
{
   model.isManager = false;
   
   // Check the role of the user - only SiteManagers are allowed to invite people/view invites
   var obj = context.properties["memberships"];
   if (!obj)
   {
      var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));
      if (json.status == 200)
      {
         obj = eval('(' + json + ')');
      }
   }
   if (obj)
   {
      model.isManager = (obj.role == "SiteManager");
   }
}

main();