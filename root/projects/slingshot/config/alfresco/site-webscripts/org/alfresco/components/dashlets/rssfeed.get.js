<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils/feed.utils.js">

/**
 * Main entry point for the webscript
 */
function main()
{
   var uri = args.feedurl;
   if (!uri)
   {
      // Use the default
      var conf = new XML(config.script);
      uri = getValidRSSUri(conf.feed[0].toString());
   }
   model.uri = uri;
   model.limit = args.limit || 100;
   model.target = args.target || "_self";

   var userIsSiteManager = true;
   if (page.url.templateArgs.site)
   {
      // We are in the context of a site, so call the repository to see if the user is site manager or not
      userIsSiteManager = false;
      var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));

      if (json.status == 200)
      {
         var obj = eval('(' + json + ')');
         if (obj)
         {
            userIsSiteManager = (obj.role == "SiteManager");
         }
      }
   }
   model.userIsSiteManager = userIsSiteManager;
}

main();

//Widget instantiation metadata...
model.webScriptWidgets = [];

var rssFeed = {};
rssFeed.name = "Alfresco.dashlet.RssFeed";
rssFeed.assignToVariable = "rssFeed";
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
if (model.userIsSiteManager)
{
   dashletTitleBarActions.options.actions.push({
      cssClass: "edit",
      eventOnClick: { ___value : "rssFeedDashletEvent", ___type: "REFERENCE"},
      tooltip: msg.get("dashlet.edit.tooltip")
   });
}
dashletTitleBarActions.options.actions.push({
   cssClass: "help",
   bubbleOnClick:
   {
      message: msg.get("dashlet.help")
   },
   tooltip: msg.get("dashlet.help.tooltip")
});
model.webScriptWidgets.push(dashletTitleBarActions);