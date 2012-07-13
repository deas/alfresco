function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var linksView = {
      name : "Alfresco.LinksView",
      options : {
         siteId : page.url.templateArgs.site,
         containerId : "links",
         linkId : page.url.args.linkId
      }
   };
   model.widgets.push(linksView);
}

main();

