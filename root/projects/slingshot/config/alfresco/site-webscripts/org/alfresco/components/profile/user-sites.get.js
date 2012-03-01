/**
 * User Profile Component - User Sites list GET method
 */

function main()
{
   // read config - use default values if not found
   var maxItems = 100,
       conf = new XML(config.script);
   if (conf["max-items"] != null)
   {
      maxItems = parseInt(conf["max-items"]);
   }

   // Call the repo for sites the user is a member of
   var userId = page.url.templateArgs["userid"];
   if (userId == null)
   {
      userId = user.name;
   }
var result = remote.call("/api/people/" + encodeURIComponent(userId) + "/sites?size=" + maxItems);
   model.sites = [];
   if (result.status == 200)
   {
      // Create javascript objects from the server response
      model.sites = eval('(' + result + ')');
   }
   model.numSites = model.sites.length;
}

main();