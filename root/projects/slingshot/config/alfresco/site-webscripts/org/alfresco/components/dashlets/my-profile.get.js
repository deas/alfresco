<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/dashlets/my-workspaces.inc.js">
main("meeting-workspace");

// Widget instantiation metdata...
model.webScriptWidgets = [];

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
});

model.webScriptWidgets.push(dashletTitleBarActions);