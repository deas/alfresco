/**
 * User Profile Component - Following list GET method
 */

function main()
{
   var userId = page.url.templateArgs["userid"];
   if (userId == null)
   {
      userId = user.name;
   }
   
   model.activeUserProfile = (userId == null || userId == user.name);
   
   var result = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/following");
   model.numPeople = 0;
   if (result.status == 200)
   {
      model.data = eval('(' + result + ')');
      model.numPeople = model.data.people.length;
   }
   
   var result = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/private");
   model.privatelist = false;
   if (result.status == 200)
   {
      model.privatelist = eval('(' + result + ')')['private'];
   }
}
main();