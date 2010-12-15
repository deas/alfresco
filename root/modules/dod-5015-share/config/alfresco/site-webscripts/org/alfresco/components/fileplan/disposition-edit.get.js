/**
 * Sort helper function for objects with labels
 *
 * @param obj1
 * @param obj2
 */
function sortByLabel(obj1, obj2)
{
   return (obj1.label > obj2.label) ? 1 : (obj1.label < obj2.label) ? -1 : 0;
}

/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Call the repo to create the site
   var scriptRemoteConnector = remote.connect("alfresco");
   var repoResponse = scriptRemoteConnector.get("/api/rma/admin/listofvalues");
   if (repoResponse.status == 401)
   {
      status.setCode(repoResponse.status, "error.loggedOut");
      return;
   }
   else
   {
      var repoJSON = eval('(' + repoResponse + ')');

      // Check if we got a positive result
      if (repoJSON.data)
      {
         var data = repoJSON.data;
         if(data && data.dispositionActions)
         {
            model.dispositionActions = data.dispositionActions.items;
         }
         if(data && data.events)
         {
            var events = data.events.items;
            events.sort(sortByLabel);
            model.events = events;
         }
         if(data && data.periodTypes)
         {
            var periodTypes = data.periodTypes.items;
            periodTypes.sort(sortByLabel);
            model.periodTypes = periodTypes;
         }
         if(data && data.periodProperties)
         {
            var periodProperties = data.periodProperties.items;
            periodProperties.sort(sortByLabel);
            model.periodProperties = periodProperties;
         }
      }
      else if (repoJSON.status.code)
      {
         status.setCode(repoJSON.status.code, repoJSON.message);
         return;
      }
   }

   repoResponse = scriptRemoteConnector.get("/api/rma/rmconstraints/rmc_tlList");
   if (repoResponse.status == 401)
   {
      status.setCode(repoResponse.status, "error.loggedOut");
      return;
   }
   else
   {
      var repoJSON = eval('(' + repoResponse + ')');

      // Check if we got a positive result
      if (repoJSON.data)
      {
         var data = repoJSON.data;
         if(data && data.allowedValuesForCurrentUser)
         {
            var transferLocations = data.allowedValuesForCurrentUser;
            transferLocations.sort(sortByLabel);
            model.transferLocations = transferLocations;
         }
      }
      else if (repoJSON.status.code)
      {
         status.setCode(repoJSON.status.code, repoJSON.message);
         return;
      }
   }

}

main();