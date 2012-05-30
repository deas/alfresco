<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/dashlets/my-workspaces.inc.js">
main("meeting-workspace");

// Widget instantiation metadata...
model.webScriptWidgets = [];
var myMeetingWorkspaces = {};
myMeetingWorkspaces.name = "Alfresco.dashlet.MyMeetingWorkspaces";
myMeetingWorkspaces.provideOptions = true;
myMeetingWorkspaces.provideMessages = true;
myMeetingWorkspaces.options = {};
myMeetingWorkspaces.options.imapEnabled = model.imapServerEnabled;
myMeetingWorkspaces.options.sites = model.sites;
model.webScriptWidgets.push(myMeetingWorkspaces);

var dashletResizer = {};
dashletResizer.name = "Alfresco.widget.DashletResizer";
dashletResizer.instantiationArguments = [];
dashletResizer.instantiationArguments.push("\"" + args.htmlid + "\"");
dashletResizer.instantiationArguments.push("\"" + instance.object.id + "\"");
model.webScriptWidgets.push(dashletResizer);

var dashletTitleBarActions = {};
dashletTitleBarActions.name = "Alfresco.widget.DashletTitleBarActions";
dashletTitleBarActions.provideOptions = true;
dashletTitleBarActions.provideMessages = false;
dashletTitleBarActions.options = {};
dashletTitleBarActions.options.actions = [];
dashletTitleBarActions.options.actions.push(
      {
         cssClass: "help",
         bubbleOnClick:
         {
            message: msg.get("dashlet.help")
         },
         tooltip: msg.get("dashlet.help.tooltip")
      });

model.webScriptWidgets.push(dashletTitleBarActions);