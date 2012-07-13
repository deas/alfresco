function main()
{
   model.widgets = [];

   var dashboardconfig = config.scoped['Dashboard']['dashboard'];
   var listSize = dashboardconfig.getChildValue('summary-list-size');
   if (listSize == null)
   {
      listSize = 100;
   }

   var userCalendar = {
      name : "Alfresco.dashlet.UserCalendar",
      options : {
         listSize : listSize
      }
      
   };
   model.widgets.push(userCalendar);

   var dashletResizer = {
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"", "\"" + instance.object.id + "\""],
      useMessages: false
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
                tooltip: msg.get("dashlet.help.tooltip")
             }
         ]
      }
   };
   model.widgets.push(dashletTitleBarActions);
}

main();
