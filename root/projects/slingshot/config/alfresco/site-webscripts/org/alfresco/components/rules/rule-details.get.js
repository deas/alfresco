function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var ruleDetails = {
      name : "Alfresco.RuleDetails",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         nodeRef : (page.url.templateArgs.nodeRef != null) ? page.url.templateArgs.nodeRef : ""
      }
   };
   model.widgets.push(ruleDetails);
}

main();

