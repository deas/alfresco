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

   var docListFilter = {
      id : "BaseFilter",
      name : "Alfresco.component.BaseFilter",
      initArgs: [ "Alfresco.DocListFilter", "\"" + args.htmlid + "\""],
      assignTo : "filter",
      useMessages : false
   };
   model.widgets = [docListFilter];
}

main();