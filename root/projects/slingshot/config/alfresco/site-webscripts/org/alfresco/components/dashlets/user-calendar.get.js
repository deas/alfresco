model.webScriptWidgets = [];

var userCalendar = {};
userCalendar.name = "Alfresco.dashlet.UserCalendar";
userCalendar.provideOptions = true;
userCalendar.provideMessages = true;
userCalendar.options = {};
var dashboardconfig = config.scoped['Dashboard']['dashboard'];
var listSize = dashboardconfig.getChildValue('summary-list-size');
if (listSize == null)
{
   listSize = 100;
}
userCalendar.options.listSize = listSize
model.webScriptWidgets.push(userCalendar);

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