<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/dashlets/rssfeed.get.js">

main();

function defineWidgets()
{
   // Widget instantiation metadata...
   model.widgets = [];

   var rssFeed = {
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
   model.widgets.push(rssFeed);

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
      name : "Alfresco.widget.DashletTitleBarActions",
      useMessages : false,
      options : {
         actions : actions
      }
   };
   model.widgets.push(dashletTitleBarActions);
}

defineWidgets();

