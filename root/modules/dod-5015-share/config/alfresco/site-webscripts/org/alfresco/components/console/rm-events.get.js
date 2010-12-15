<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/console/rm-console.lib.js">

/**
 * Sort helper function for objects with labels
 *
 * @param obj1
 * @param obj2
 */
function sortByLabel(obj1, obj2)
{
   return (obj1.eventTypeDisplayLabel > obj2.eventTypeDisplayLabel) ? 1 : (obj1.eventTypeDisplayLabel < obj2.eventTypeDisplayLabel) ? -1 : 0;
}

/**
 * Main entry point for component webscript logic
 *
 * @method main
 */
function main()
{
   var conn = remote.connect("alfresco");
   
   // test user capabilities - can they access Events?
   model.hasAccess = hasCapability(conn, "CreateModifyDestroyEvents");
   
   // retrieve event types
   var repoResponse = conn.get("/api/rma/admin/rmeventtypes");
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
         // Transform events from object to array and sort it
         var data = repoJSON.data;
         var eventTypes = [];
         if (data)
         {
            for (var key in data)
            {
               eventTypes.push(data[key]);
            }
            eventTypes.sort(sortByLabel);
         }

         // Set model values
         model.eventTypes = eventTypes;
      }
      else if (repoJSON.status.code)
      {
         status.setCode(repoJSON.status.code, repoJSON.message);
         return;
      }
   }
}

main();