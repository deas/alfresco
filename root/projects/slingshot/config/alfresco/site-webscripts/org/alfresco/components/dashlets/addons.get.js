<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/dashlets/rssfeed.get.js">

main();

// Widget instantiation metadata...
model.webScriptWidgets = [];

var rssFeed = {};
rssFeed.name = "Alfresco.dashlet.RssFeed";
rssFeed.assignToVariable = "addOnsRssFeed";
rssFeed.provideOptions = true;
rssFeed.provideMessages = true;
rssFeed.options = {};
rssFeed.options.componentId = instance.object.id;
rssFeed.options.feedURL = model.uri;
if (!isNaN(model.limit) && model.limit != 100)
{
   rssFeed.options.limit = model.limit;
}
else
{
   rssFeed.options.limit = "all";
}
rssFeed.options.titleElSuffix = "-title";
rssFeed.options.targetElSuffix = "-scrollableList";
model.webScriptWidgets.push(rssFeed);

var dashletTitleBarActions = {};
dashletTitleBarActions.name = "Alfresco.widget.DashletTitleBarActions";
dashletTitleBarActions.provideOptions = true;
dashletTitleBarActions.provideMessages = false;
dashletTitleBarActions.options = {};
dashletTitleBarActions.options.actions = [];
if (model.userIsSiteManager)
{
   dashletTitleBarActions.options.actions.push(
   {
      cssClass: "edit",
      eventOnClick: { ___value : "addOnsRssFeedDashletEvent", ___type: "REFERENCE"},
      tooltip: msg.get("dashlet.edit.tooltip")
   });
}
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

