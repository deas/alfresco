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
         label: xmlFilter.@label.toString()
      });
   }
   
   model.filters = filters;
}

main();

// Widget instantiation metadata...
model.widgets = [];
var baseFilter = {};
baseFilter.name = "Alfresco.component.BaseFilter";
baseFilter.initArgs = ["Alfresco.LinkFilter", "\"" + args.htmlid + "\""];
baseFilter.useMessages = false;
baseFilter.useOptions = false;
model.widgets.push(baseFilter);