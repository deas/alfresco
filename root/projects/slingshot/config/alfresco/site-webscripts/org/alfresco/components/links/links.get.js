function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var links = {
      name : "Alfresco.Links",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : (page.url.templateArgs.container != null) ? page.url.templateArgs.container : "links",
         initialFilter : {
            filterId : (page.url.args.filterId != null) ? page.url.args.filterId : "all",
            filterOwner : (page.url.args.filterOwner != null) ? page.url.args.filterOwner: "Alfresco.LinkFilter",
            filterData : page.url.args.filterData
         }
      }
   };
   model.widgets.push(links);
}

main();

