<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

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
      var people = eval('(' + result + ')');
      var peopleCount = people.people.length;
      // convert status update times to relative time messages
      for (var i=0; i<peopleCount; i++)
      {
         person = people.people[i];
         if (typeof person.userStatusTime != "undefined")
         {
            person.userStatusRelativeTime = AlfrescoUtil.relativeTime(person.userStatusTime.iso8601);
         }
      }
      model.data = people;
      model.numPeople = peopleCount;
   }
   
   var result = remote.call("/api/subscriptions/" + encodeURIComponent(userId) + "/private");
   model.privatelist = false;
   if (result.status == 200)
   {
      model.privatelist = eval('(' + result + ')')['private'];
   }
}
main();