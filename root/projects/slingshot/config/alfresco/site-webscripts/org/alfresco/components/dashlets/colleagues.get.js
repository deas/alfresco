function sortByName(membership1, membership2)
{
   var name1 = membership1.authority ? membership1.authority.firstName + membership1.authority.lastName : "";
   var name2 = membership2.authority ? membership2.authority.firstName + membership2.authority.lastName : "";
   return (name1 > name2) ? 1 : (name1 < name2) ? -1 : 0;
}

/**
 * Site Colleagues component GET method
 */
function main()
{
   // Call the repo for the site memberships
   var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships?size=100&authorityType=USER");
   
   var memberships = [];
   
   if (json.status == 200)
   {
      // Create javascript objects from the repo response
      var obj = eval('(' + json + ')');
      if (obj)
      {
         memberships = obj;
         var userObj, member;
         for (var i = 0, j = memberships.length; i < j; i++)
         {
            member = memberships[i];
         }
         memberships.sort(sortByName);
      }
   }
   
   // Prepare the model
   model.memberships = memberships;
}

main();