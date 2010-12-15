/**
 * Create/update Saved Search
 */
function main()
{
   var siteId = url.templateArgs.site,
       siteNode = siteService.getSite(siteId);
   
   if (siteNode === null)
   {
      status.setCode(status.STATUS_NOT_FOUND, "Site not found: '" + siteId + "'");
      return null;
   }
   
   // Example format of posted Saved Search JSON:
   /*
      {
         "name": "search name",
         "description": "the search description",
         "query": "the complete search query string",
         "public": boolean,
         "params": "terms=keywords:xyz&undeclared=true",
         "sort": "cm:name/asc"
      }
      
      where: name and query values are mandatory
             params are in URL encoded name/value pair format
             sort is in comma separated "property/dir" packed format i.e. "cm:name/asc,cm:title/desc"
   */
   if (json.isNull("name") || json.get("name").length() == 0)
   {
      status.setCode(status.STATUS_BAD_REQUEST, "Mandatory 'name' value missing when saving search.");
      return;
   }
   var name = json.get("name");
   
   if (json.isNull("query") || json.get("query").length() == 0)
   {
      status.setCode(status.STATUS_BAD_REQUEST, "Mandatory 'query' value missing when saving search.");
      return;
   }
   
   var description = null;
   if (json.has("description"))
   {
      description = json.get("description");
   }
   
   var bPublic = (json.has("public") ? json.get("public") : true);
   
   // locate the Saved Searches container and commit the saved search JSON node
   var searchNode = siteNode.getContainer("Saved Searches");
   if (searchNode == null)
   {
      searchNode = siteNode.createContainer("Saved Searches");
   }
   if (searchNode != null)
   {
      // public searches are stored in the root of the folder
      if (bPublic == false)
      {
         // user specific searches are stored in a sub-folder of username
         var userNode = searchNode.childByNamePath(person.properties.userName);
         if (userNode != null)
         {
            // create sub-folder for the user
            searchNode = searchNode.createFolder(person.properties.userName);
         }
      }
      
      // create or update the search based on the given name
      var sNode = searchNode.childByNamePath(name);
      if (sNode == null)
      {
         sNode = searchNode.createFile(name);
      }
      sNode.properties.description = description;
      sNode.content = json.toString();
      sNode.mimetype = "application/json";
      sNode.save();
   }
   
   model.success = true;
}

main();