<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/dashlets/rssfeed.get.js">

main();

function defineWidgets()
{
   // Widget instantiation metadata...
   
   var rssFeed = {
      id : "RssFeed",
      name : "Alfresco.dashlet.RssFeed",
      assignTo : "addOnsRssFeed",
      options : {
         componentId : instance.object.id,
         feedURL : model.uri,
         limit : (!isNaN(model.limit) && model.limit != 100) ? model.limit : "all",
         titleElSuffix : "-title",
         targetElSuffix : "-scrollableList"
      }
   };

   var actions = [];
   if (model.userIsSiteManager)
   {
      actions.push({
         cssClass: "edit",
         eventOnClick: { ___value : "addOnsRssFeedDashletEvent", ___type: "REFERENCE"},
         tooltip: msg.get("dashlet.edit.tooltip")
      });
   }
   actions.push({
      cssClass: "help",
      bubbleOnClick:
      {
         message: msg.get("dashlet.help")
      },
      tooltip: msg.get("dashlet.help.tooltip")
   });
   
   var dashletTitleBarActions = {
      id : "DashletTitleBarActions",
      name : "Alfresco.widget.DashletTitleBarActions",
      useMessages : false,
      options : {
         actions : actions
      }
   };
   model.widgets = [rssFeed, dashletTitleBarActions];
}

defineWidgets();

