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

//Widget instantiation metadata...
model.webScriptWidgets = [];
var filter = {};
filter.name = "Alfresco.component.BaseFilter";
filter.instantiationArguments = [];
filter.instantiationArguments.push("Alfresco.TopicListFilter");
filter.instantiationArguments.push("\"" + args.htmlid + "\"");
filter.provideMessages = false;
filter.provideOptions = false;
model.webScriptWidgets.push(filter);