/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Request the disposition actions
   var nodeRef = page.url.args.nodeRef.replace(":/", "");

   // Call the repo to get the disposition schedule
   var scriptRemoteConnector = remote.connect("alfresco");
   var repoResponse = scriptRemoteConnector.get("/api/node/" + nodeRef + "/dispositionschedule");
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
         repoResponse = scriptRemoteConnector.get("/api/rma/admin/listofvalues");
         var listOfValuesResult= eval('(' + repoResponse + ')');
         var periodTypesArray = listOfValuesResult.data.periodTypes.items,
            periodTypeLabels = {},
            periodType;
         for (var pti = 0; pti < periodTypesArray.length; pti++)
         {
            periodType = periodTypesArray[pti];
            periodTypeLabels[periodType.value] = periodType.label;
         }

         var schedule = repoJSON.data;
         if(schedule.instructions)
         {
            model.instructions = schedule.instructions;
         }
         if(schedule.authority)
         {
            model.authority = schedule.authority;                     
         }
         if(schedule.nodeRef)
         {
            model.dipositionScheduleNodeRef = schedule.nodeRef;            
         }
         
         model.publishInProgress = schedule.publishInProgress;
         model.unpublishedUpdates = schedule.unpublishedUpdates;
         
         model.recordLevelDisposition = schedule.recordLevelDisposition;
         var actions = schedule.actions,
               periodTypeLabel;
         for(var i = 0; i < actions.length; i++)
         {
            var action = actions[i];
            var p = action.period ? action.period.split("|") : [];
            var periodType = p.length > 0 ? p[0] : null;
            var periodAmount = p.length > 1 ? p[1] : null;
            if(periodType && periodType != "none")
            {
               periodTypeLabel = periodTypeLabels[periodType];
               periodTypeLabel = periodTypeLabel ? periodTypeLabel.toLowerCase() : "";
               if(!periodAmount || periodAmount == "" || periodAmount == "0")
               {
                  action.title = msg.get("label.title.noTime", [action.label, periodTypeLabel]);                  
               }
               else
               {
                  action.title = msg.get("label.title.complex", [action.label, periodAmount, periodTypeLabel]);
               }
            }
            else
            {
               action.title = msg.get("label.title.simple", [action.label]);
            }
         }
         model.actions = actions;
      }
      else if (repoJSON.status.code)
      {
         status.setCode(repoJSON.status.code, repoJSON.message);
         return;
      }
   }

}

main();