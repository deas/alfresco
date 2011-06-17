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
   var result = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/following");
   model.people = [];
   if (result.status == 200)
   {
      // Create javascript objects from the server response
      model.data = eval('(' + result + ')');
   }
   model.numPeople = model.data.people.length;
}

main();