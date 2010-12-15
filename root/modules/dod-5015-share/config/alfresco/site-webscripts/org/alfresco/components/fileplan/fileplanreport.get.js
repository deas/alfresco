/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Request the disposition actions
   var nodeRef = page.url.args.nodeRef.replace(":/", "");

   // Call the repo to get the fileplan report
   var scriptRemoteConnector = remote.connect("alfresco");
   var repoResponse = scriptRemoteConnector.get("/api/node/" + nodeRef + "/fileplanreport");
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
         var report = repoJSON.data;
         model.firstName = report.firstName;
         model.lastName = report.lastName;
         model.printDate = report.printDate;
         if (report.recordSeries)
         {
            model.recordSeries = report.recordSeries;
         }
         else if (report.recordCategories)
         {
            model.recordCategories = report.recordCategories;
         }
         else if (report.recordFolders)
         {
               model.recordFolders = report.recordFolders;
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