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

function main()
{
   // Widget instantiation metadata...
   model.widgets = [];

   var docSummary = {
      name : "Alfresco.dashlet.DocSummary",
      options : {
         simpleView : model.preferences.prefSimpleView != null ? model.preferences.prefSimpleView : false, 
         maxItems : model.maxItems
      }
   };
   model.widgets.push(docSummary);

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
