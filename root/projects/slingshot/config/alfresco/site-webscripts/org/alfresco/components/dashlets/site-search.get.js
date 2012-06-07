// Widget instantiation metadata...
model.webScriptWidgets = [];

var siteSearch = {};
siteSearch.name = "Alfresco.dashlet.SiteSearch";
siteSearch.provideOptions = true;
siteSearch.provideMessages = true;
siteSearch.options = {};
siteSearch.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
siteSearch.options.searchRootNode = (config.scoped['RepositoryLibrary']['root-node']).value;
model.webScriptWidgets.push(siteSearch);

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
dashletTitleBarActions.options.actions.push({
   cssClass: "help",
   bubbleOnClick:
   {
      message: msg.get("dashlet.help")
   },
   tooltip:  msg.get("dashlet.help.tooltip")
});

model.webScriptWidgets.push(dashletTitleBarActions);