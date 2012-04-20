function main()
{
   model.success = false;
   
   // Get clients json request as a "normal" js object literal
   var clientRequest = json.toString();
   var clientJSON = eval('(' + clientRequest + ')');
   
   // Call the repo to create the site
   var scriptRemoteConnector = remote.connect("alfresco");
   var repoResponse = scriptRemoteConnector.post("/api/sites", clientRequest, "application/json");
   if (repoResponse.status == 401)
   {
      status.setCode(repoResponse.status, "error.loggedOut");
   }
   else
   {
      var repoJSON = eval('(' + repoResponse + ')');
      
      // Check if we got a positive result
      if (repoJSON.shortName)
      {
         // Yes we did, now create the site in the webtier
         var tokens = [];
         tokens["siteid"] = repoJSON.shortName;
         sitedata.newPreset(clientJSON.sitePreset, tokens);
         model.success = true;
      }
      else if (repoJSON.status.code)
      {
         // Default error handler
         status.setCode(repoJSON.status.code, repoJSON.message);
      }
   }
}

main();