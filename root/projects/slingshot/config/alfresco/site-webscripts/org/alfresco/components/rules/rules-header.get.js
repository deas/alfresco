function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var rulesHeader = {
      name : "Alfresco.RulesHeader",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         nodeRef : (page.url.args.nodeRef != null) ? page.url.args.nodeRef : ""
      }
   };
   model.widgets.push(rulesHeader);
}

main();

