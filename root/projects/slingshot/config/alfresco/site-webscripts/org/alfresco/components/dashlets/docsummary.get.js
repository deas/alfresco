<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

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

model.preferences = AlfrescoUtil.getPreferences("org.alfresco.share.docsummary.dashlet");
model.maxItems = getMaxItems();

// Widget instantiation metadata...
model.webScriptWidgets = [];

var docSummary = {};
docSummary.name = "Alfresco.dashlet.DocSummary";
docSummary.provideOptions = true;
docSummary.provideMessages = true;
docSummary.options = {};
docSummary.options.simpleView = model.preferences.prefSimpleView != null ? model.preferences.prefSimpleView : false; 
docSummary.options.maxItems = model.maxItems;
model.webScriptWidgets.push(docSummary);

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

