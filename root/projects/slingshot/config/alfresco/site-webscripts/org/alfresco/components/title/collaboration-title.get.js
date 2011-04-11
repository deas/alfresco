/**
 * Collaboration Site Title component GET method
 */
function main()
{
   // Call the repository for the site profile
   var json = remote.call("/api/sites/" + page.url.templateArgs.site);
   
   var profile =
   {
      title: "",
      shortName: "",
      visibility: "PUBLIC"
   };
   
   if (json.status == 200)
   {
      // Create javascript objects from the repo response
      var obj = eval('(' + json + ')');
      if (obj)
      {
         profile = obj;
      }
   }
   
   // Call the repository to see if the user is site manager or not
   var userIsSiteManager = false,
      userIsMember = false;

   json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));
   if (json.status == 200)
   {
      var obj = eval('(' + json + ')');
      if (obj)
      {
         userIsMember = true;
         userIsSiteManager = obj.role == "SiteManager";
      }
   }
   
   // Prepare the model
   model.profile = profile;
   model.userIsSiteManager = userIsSiteManager;
   model.userIsMember = userIsMember;
}

main();