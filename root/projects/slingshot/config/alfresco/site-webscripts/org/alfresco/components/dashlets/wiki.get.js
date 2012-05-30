<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function main()
{
   var wikipage = args.wikipage;
   if (wikipage)
   {
      var uri = "/slingshot/wiki/page/" + page.url.templateArgs.site + "/" + encodeURIComponent(wikipage) + "?format=mediawiki",
          connector = remote.connect("alfresco"),
          result = connector.get(uri),
          myConfig = new XML(config.script);
      
      if (result.status == status.STATUS_OK)
      {
         model.wikipage = myConfig.allowUnfilteredHTML == true ? result.response : stringUtils.stripUnsafeHTML(result.response);
      }
      
      model.wikiLink = String(wikipage);
      model.pageTitle = String(wikipage).replace(/_/g, " ");
   
      // Get all pages for the site so we can display links correctly
      model.pageList = doGetCall("/slingshot/wiki/pages/" + page.url.templateArgs.site + "?pageMetaOnly=true");
   }
   
   // Call the repository to see if the user is site manager or not
   var userIsSiteManager = false,
       json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));
   
   if (json.status == 200)
   {
      var obj = eval('(' + json + ')');
      if (obj)
      {
         userIsSiteManager = (obj.role == "SiteManager");
      }
   }
   model.userIsSiteManager = userIsSiteManager;
   
   // Widget instantiation metadata...
   model.webScriptWidgets = [];

   var wikiDashlet = {};
   wikiDashlet.name = "Alfresco.dashlet.WikiDashlet";
   wikiDashlet.assignToVariable = "wiki";
   wikiDashlet.provideOptions = true;
   wikiDashlet.provideMessages = true;
   wikiDashlet.options = {};
   var pages = [];
   if (model.pageList != null)
   {
      for (var p in pageList.pages)
      {
         pages.push(p.name);
      }
   }
   wikiDashlet.options.pages = pages;
   wikiDashlet.options.guid = instance.object.id;
   wikiDashlet.options.siteId = siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
   model.webScriptWidgets.push(wikiDashlet);

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
      dashletTitleBarActions.options.actions.push(
      {
         cssClass: "edit",
         eventOnClick: { ___value : "editWikiDashletEvent", ___type: "REFERENCE"},
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
}
main();