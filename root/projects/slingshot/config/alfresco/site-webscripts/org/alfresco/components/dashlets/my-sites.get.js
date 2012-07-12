function main()
{
   // Check for IMAP server status
   var result = remote.call("/imap/servstatus"),
      imapServerEnabled = (result.status == 200 && result == "enabled");

   // Prepare the model for the template
   model.imapServerEnabled = imapServerEnabled;
   
   // Widget instantiation metadata...
   model.widgets = [];
   
   var dashboardconfig = config.scoped['Dashboard']['dashboard'];
   var listSize = dashboardconfig.getChildValue('summary-list-size');
   if (listSize == null)
   {
      listSize = 100;
   }
   
   var mySites = {
      name : "Alfresco.dashlet.MySites",
      options : {
         imapEnabled : imapServerEnabled,
         listSize : listSize
      }
   };
   model.widgets.push(mySites);
   
   var dashletResizer = {
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"", "\"" + instance.object.id + "\""],
      useMessages : false
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