/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Actions
   var myConfig = new XML(config.script),
      filters = [];
   
   for each(var xmlFilter in myConfig..filter)
   {
      filters.push(
      {
         id: xmlFilter.@id.toString(),
         data: xmlFilter.@data.toString(),
         label: xmlFilter.@label.toString()
      });
   }
   
   model.filters = filters;
}

main();