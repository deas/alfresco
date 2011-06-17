function main()
{
   // Call the repo to create the site
   var siteJson =
   {
      shortName: args["shortname"],
      sitePreset: "rm-site-dashboard",
      title: msg.get("title.rmsite"),
      description: msg.get("description.rmsite"),
      type: "{http://www.alfresco.org/model/recordsmanagement/1.0}rmsite"
   };
   var scriptRemoteConnector = remote.connect("alfresco"),
      repoResponse = scriptRemoteConnector.post("/api/sites", jsonUtils.toJSONString(siteJson), "application/json");
   if (repoResponse.status == 401)
   {
      status.setCode(repoResponse.status, "error.loggedOut");
      return;
   }
   else
   {
      var repoJSON = eval('(' + repoResponse + ')');
      
      // Check if we got a positive result
      if (repoJSON.shortName)
      {
         // Yes we did, now create the site in the webtier
         var tokens = new Array();
         tokens["siteid"] = repoJSON.shortName;
         sitedata.newPreset("rm-site-dashboard", tokens);
         
         model.success = true;
      }
      else if (repoJSON.status.code)
      {
         status.setCode(repoJSON.status.code, repoJSON.message);
         return;
      }
   }
}

main();