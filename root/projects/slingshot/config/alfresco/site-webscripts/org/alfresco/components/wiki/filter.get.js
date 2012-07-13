function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var baseFilter = {
      name : "Alfresco.component.BaseFilter",
      initArgs : [ "Alfresco.WikiFilter", "\"" + args.htmlid + "\""],
      useMessages : false
   };
   model.widgets.push(baseFilter);
}

main();

