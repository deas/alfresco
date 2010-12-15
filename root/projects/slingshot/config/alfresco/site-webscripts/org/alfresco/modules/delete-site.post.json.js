function main()
{
   var req = json.toString();
   var reqJSON = eval('(' + req + ')');
   
   // Call the repo to delete the site
   var conn = remote.connect("alfresco");
   var res = conn.del("/api/sites/" + reqJSON.shortName);
   var resJSON = eval('(' + res + ')');
   
   // Check if we got a positive result
   if (resJSON.success)
   {
      // Yes we did - now remove sitestore model artifacts...
      
      // remove dashboard page instance
      var dashboardURL = "site/" + reqJSON.shortName + "/dashboard";
      var dashboardPage = sitedata.getPage(dashboardURL);
      if (dashboardPage != null)
      {
         dashboardPage.remove();
      }
      
      // remove component instances
      var components = sitedata.findComponents("page", null, dashboardURL, null);
      for (var i=0; i < components.length; i++)
      {
         components[i].remove();
      }
      
      // the client will refresh on success
      model.success = true;
   }
   else
   {
      // Error occured - report back to client with the status and message
      status.setCode(resJSON.status.code, resJSON.message);
      model.success = false;
   }
}

main();