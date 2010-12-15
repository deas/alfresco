/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   var siteId = page.url.templateArgs.site,
      savedSearches = [];

   var connector = remote.connect("alfresco");
   result = connector.get("/slingshot/doclib/dod5015/savedsearches/site/" + siteId);

   if (result.status == 200)
   {
      var ss = eval('(' + result + ')');
      try
      {
         for each (var s in ss.items)
         {
            savedSearches.push(
            {
               data: s.name,
               label: s.name,
               description: s.description
            });
         }
      }
      catch (e)
      {
      }
   }
   
   model.savedSearches = savedSearches;
}

main();