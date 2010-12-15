/**
 * Sort helper function for objects with titles
 *
 * @param obj1
 * @param obj2
 */
function sortByTitle(obj1, obj2)
{
   return (obj1.title > obj2.title) ? 1 : (obj1.title < obj2.title) ? -1 : 0;
}

/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Call the repo to get the list of custom types
   var scriptRemoteConnector = remote.connect("alfresco"),
      repoResponse = scriptRemoteConnector.get("/api/rma/admin/dodcustomtypes");
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
         if (data && data.dodCustomTypes)
         {
            var recordTypes = data.dodCustomTypes;
            recordTypes.sort(sortByTitle);
            model.recordTypes = recordTypes;
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