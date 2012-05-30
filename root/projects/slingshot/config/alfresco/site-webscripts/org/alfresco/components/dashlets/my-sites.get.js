function main()
{
   // Check for IMAP server status
   var result = remote.call("/imap/servstatus"),
      imapServerEnabled = (result.status == 200 && result == "enabled");

   // Prepare the model for the template
   model.imapServerEnabled = imapServerEnabled;
   
   // Widget instantiation metadata...
   model.webScriptWidgets = [];
   
   var dashboardconfig = config.scoped['Dashboard']['dashboard'];
   var listSize = dashboardconfig.getChildValue('summary-list-size');
   if (listSize == null)
   {
      listSize = 100;
   }
   
   var mySites = {};
   mySites.name = "Alfresco.dashlet.MySites";
   mySites.provideOptions = true;
   mySites.provideMessages = true;
   mySites.options = {};
   mySites.options.imapEnabled = imapServerEnabled;
   mySites.options.listSize = listSize;
   model.webScriptWidgets.push(mySites);
   
   var dashletResizer = {};
   dashletResizer.name = "Alfresco.widget.DashletResizer";
   dashletResizer.instantiationArguments = [];
   dashletResizer.instantiationArguments.push("\"" + args.htmlid + "\"");
   dashletResizer.instantiationArguments.push("\"" + instance.object.id + "\"");
   dashletResizer.provideOptions = false;
   dashletResizer.provideMessages = false;
   model.webScriptWidgets.push(dashletResizer);
   
   var dashletTitleBarActions = {};
   dashletTitleBarActions.name = "Alfresco.widget.DashletTitleBarActions";
   dashletTitleBarActions.provideOptions = true;
   dashletTitleBarActions.provideMessages = false;
   dashletTitleBarActions.options = {};
   dashletTitleBarActions.options.actions = [];
   dashletTitleBarActions.options.actions.push({
      cssClass: "help",
      bubbleOnClick:
      {
         message: msg.get("dashlet.help")
      },
      tooltip: msg.get("dashlet.help.tooltip")
   })
   model.webScriptWidgets.push(dashletTitleBarActions);
}

main();