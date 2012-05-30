<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

/* Get filters */
function getFilters()
{
   var myConfig = new XML(config.script),
      filters = [];

   for each (var xmlFilter in myConfig..filter)
   {
      filters.push(xmlFilter.@type.toString());
   }
   return filters
}

/* Max Items */
function getMaxItems()
{
   var myConfig = new XML(config.script),
      maxItems = myConfig["max-items"];

   if (maxItems)
   {
      maxItems = myConfig["max-items"].toString();
   }
   return parseInt(maxItems && maxItems.length > 0 ? maxItems : 50, 10);
}

model.preferences = AlfrescoUtil.getPreferences("org.alfresco.share.mydocuments.dashlet");
model.filters = getFilters();
model.maxItems = getMaxItems();

// Widget instantiation metadata...
model.webScriptWidgets = [];
model.prefFilter = model.preferences.filter;
if (model.prefFilter == null)
{
   model.prefFilter = "recentlyModifiedByMe";
}

model.prefSimpleView = model.preferences.simpleView;
if (model.prefSimpleView == null)
{
   model.prefSimpleView = true;
}

var myDocs = {};
myDocs.name = "Alfresco.dashlet.MyDocuments";
myDocs.provideOptions = true;
myDocs.provideMessages = true;
myDocs.options = {};
myDocs.options.filter = model.prefFilter;
myDocs.options.maxItems = model.maxItems;
myDocs.options.simpleView = model.prefSimpleView;
myDocs.options.validFilters = model.filters;
model.webScriptWidgets.push(myDocs);

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