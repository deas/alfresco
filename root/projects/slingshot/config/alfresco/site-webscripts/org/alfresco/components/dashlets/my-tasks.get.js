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


function main()
{
   // Widget instantiation metadata...
   model.widgets = [];

   var myTasks = {
      name : "Alfresco.dashlet.MyTasks",
      options : {
         hiddenTaskTypes : model.hiddenTaskTypes,
         maxItems : model.maxItems,
         filters : filterMap
      }
   };
   model.widgets.push(myTasks);

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
               tooltip:  msg.get("dashlet.help.tooltip")
            }
         ]
      }
   };
   model.widgets.push(dashletTitleBarActions);
}

main();
