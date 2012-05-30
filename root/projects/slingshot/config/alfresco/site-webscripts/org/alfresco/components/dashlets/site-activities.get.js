function getFilters(filterType)
{
   var myConfig = new XML(config.script),
       filters = [];

   for each (var xmlFilter in myConfig[filterType].filter)
   {
      filters.push(
      {
         type: xmlFilter.@type.toString(),
         label: xmlFilter.@label.toString()
      });
   }

   return filters;
}

model.filterRanges = getFilters("filter-range");
model.filterTypes = getFilters("filter-type");
model.filterActivities = getFilters("filter-activities");

// Widget instantiation metadata...
model.webScriptWidgets = [];

var myActivities = {};
myActivities.name = "Alfresco.dashlet.Activities";
myActivities.assignToVariable = "activities";
myActivities.provideOptions = true;
myActivities.provideMessages = true;
myActivities.options = {};
myActivities.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
myActivities.options.mode = "site";
myActivities.options.regionId = args['region-id'];
model.webScriptWidgets.push(myActivities);

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
      cssClass: "rss",
      eventOnClick: { ___value : "activitiesFeedDashletEvent", ___type: "REFERENCE"},
      tooltip: msg.get("dashlet.rss.tooltip")
   });
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