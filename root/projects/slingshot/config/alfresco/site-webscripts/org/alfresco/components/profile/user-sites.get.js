/**
 * User Profile Component - User Sites list GET method
 */

function main()
{
   // Call the repo for sites the user is a member of
   var userId = page.url.templateArgs["userid"];
   if (userId == null)
   {
      userId = user.name;
   }
   var result = remote.call("/api/people/" + encodeURIComponent(userId) + "/sites?size=100");
   model.sites = [];
   if (result.status == 200)
   {
      // Create javascript objects from the server response
      model.sites = eval('(' + result + ')');
   }
   model.numSites = model.sites.length;
}

main();