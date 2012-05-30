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
   
   // Widget instantiation metadata...
   model.webScriptWidgets = [];
   var blogPostListFilter = {};
   blogPostListFilter.name = "Alfresco.component.BaseFilter";
   blogPostListFilter.instantiationArguments = [ "Alfresco.BlogPostListFilter", "\"" + args.htmlid + "\""];
   blogPostListFilter.provideMessages = false;
   blogPostListFilter.provideOptions = false;
   model.webScriptWidgets.push(blogPostListFilter);
}

main();