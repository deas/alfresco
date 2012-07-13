function main()
{
   // Widget instantiation metadata...
   var rulesHeader = {
      id : "RulesHeader", 
      name : "Alfresco.RulesHeader",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         nodeRef : (page.url.args.nodeRef != null) ? page.url.args.nodeRef : ""
      }
   };
   model.widgets = [rulesHeader];
}

main();

