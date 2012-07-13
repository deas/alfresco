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
   var docSummary = {
      id : "DocSummary",
      name : "Alfresco.dashlet.DocSummary",
      options : {
         simpleView : model.preferences.prefSimpleView != null ? model.preferences.prefSimpleView : false, 
         maxItems : model.maxItems
      }
   };

   var dashletResizer = {
      id : "DashletResizer",
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"", "\"" + instance.object.id + "\""],
      useMessages: false
   };

   var dashletTitleBarActions = {
      id : "DashletTitleBarActions",
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
   model.widgets = [docSummary, dashletResizer, dashletTitleBarActions];
}

main();
