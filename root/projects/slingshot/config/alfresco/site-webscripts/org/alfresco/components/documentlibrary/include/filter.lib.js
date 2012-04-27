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

   model.webScriptWidgets = [];
   var docListFilter = {};
   docListFilter.name = "Alfresco.component.BaseFilter";
   docListFilter.instantiationArguments = [ "Alfresco.DocListFilter", "\"" + args.htmlid + "\""];
   docListFilter.assignToVariable = "filter"
   docListFilter.provideOptions = false;
   docListFilter.provideMessages = false;
   model.webScriptWidgets.push(docListFilter);
}

main();