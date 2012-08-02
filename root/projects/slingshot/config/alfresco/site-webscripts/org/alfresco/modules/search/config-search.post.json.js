/**
 * Saved search configuration component POST method
 */

function main()
{
   var searchTerm = String(json.get("searchTerm")),
      limit = String(json.get("limit"));

   model.searchTerm = searchTerm;
   model.limit = limit;

   var c = sitedata.getComponent(url.templateArgs.componentId);
   c.properties["searchTerm"] = searchTerm;
   c.properties["limit"] = limit;
   c.save();
}

main();