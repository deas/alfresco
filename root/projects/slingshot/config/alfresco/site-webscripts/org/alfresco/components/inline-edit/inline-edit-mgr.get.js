function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var inlineEditMgr = {
      name : "Alfresco.InlineEditMgr",
      options : {
         nodeRef : page.url.args.nodeRef,
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : ""
      }
   };
   model.widgets.push(inlineEditMgr);
}

main();
