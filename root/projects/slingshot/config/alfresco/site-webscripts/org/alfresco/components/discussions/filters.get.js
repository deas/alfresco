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
model.widgets = [];
var filter = {};
filter.name = "Alfresco.component.BaseFilter";
filter.initArgs = [];
filter.initArgs.push("Alfresco.TopicListFilter");
filter.initArgs.push("\"" + args.htmlid + "\"");
filter.useMessages = false;
filter.useOptions = false;
model.widgets.push(filter);