function main()
{
   // Widget instantiation metadata...
   model.widgets = [];

   var siteSearch = {
      name : "Alfresco.dashlet.SiteSearch",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         searchRootNode : (config.scoped['RepositoryLibrary']['root-node']).value
      }
   };
   model.widgets.push(siteSearch);

   var dashletResizer = {
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"", "\"" + instance.object.id + "\""]
   };
   model.widgets.push(dashletResizer);

   var dashletTitleBarActions = {
      name : "Alfresco.widget.DashletTitleBarActions",
      useMessages : false,
      options : {
         actions: [
            {
               cssClass: "help",
               bubbleOnClick:
               {
                  message: msg.get("dashlet.help")
               },
               tooltip:  msg.get("dashlet.help.tooltip")
            }
         ]
      }
   };

   model.widgets.push(dashletTitleBarActions);
}

main();
