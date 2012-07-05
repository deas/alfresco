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
model.webScriptWidgets = [];
var baseFilter = {};
baseFilter.name = "Alfresco.component.BaseFilter";
baseFilter.instantiationArguments = ["Alfresco.LinkFilter", "\"" + args.htmlid + "\""];
baseFilter.provideMessages = false;
baseFilter.provideOptions = false;
model.webScriptWidgets.push(baseFilter);