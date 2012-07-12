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

function main()
{
   // Widget instantiation metadata...
   model.widgets = [];

   var myActivities = {
      name : "Alfresco.dashlet.Activities",
      assignTo : "activities",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         mode : "site",
         regionId : args['region-id']
      }
   };
   model.widgets.push(myActivities);

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
               cssClass: "rss",
               eventOnClick: { ___value : "activitiesFeedDashletEvent", ___type: "REFERENCE"},
               tooltip: msg.get("dashlet.rss.tooltip")
            },
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
