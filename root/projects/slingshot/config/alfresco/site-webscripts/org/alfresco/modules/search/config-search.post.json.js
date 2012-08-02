/**
 * Saved search configuration component POST method
 */

function main()
{
   var searchTerm = String(json.get("searchTerm")),
      limit = String(json.get("limit")),
      title = String(json.get("title"));

   model.searchTerm = searchTerm;
   model.limit = limit;
   model.title = title;

   var c = sitedata.getComponent(url.templateArgs.componentId);
   c.properties["searchTerm"] = searchTerm;
   c.properties["limit"] = limit;
   c.properties["title"] = title;
   c.save();
}

main();