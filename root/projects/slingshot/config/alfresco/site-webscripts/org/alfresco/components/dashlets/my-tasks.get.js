<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
model.hiddenTaskTypes = getHiddenTaskTypes();

var myConfig = new XML(config.script),
   filters = [],
   filterMap = {};

for each(var xmlFilter in myConfig..filter)
{
   filters.push(
   {
      type: xmlFilter.@type.toString(),
      parameters: xmlFilter.@parameters.toString()
   });
   filterMap[xmlFilter.@type.toString()] = xmlFilter.@parameters.toString();
}
model.filters = filters;

model.maxItems = getMaxItems();

// Widget instantiation metadata...
model.webScriptWidgets = [];

var myTasks = {};
myTasks.name = "Alfresco.dashlet.MyTasks";
myTasks.provideOptions = true;
myTasks.provideMessages = true;
myTasks.options = {};
myTasks.options.hiddenTaskTypes = model.hiddenTaskTypes;
myTasks.options.maxItems = model.maxItems;
myTasks.options.filters = filterMap;
model.webScriptWidgets.push(myTasks);

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
dashletTitleBarActions.options.actions.push({
   cssClass: "help",
   bubbleOnClick:
   {
      message: msg.get("dashlet.help")
   },
   tooltip:  msg.get("dashlet.help.tooltip")
});

model.webScriptWidgets.push(dashletTitleBarActions);