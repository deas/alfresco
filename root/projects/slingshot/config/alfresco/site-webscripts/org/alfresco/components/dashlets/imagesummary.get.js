model.webScriptWidgets = [];

var imageSummary = {};
imageSummary.name = "Alfresco.dashlet.ImageSummary";
imageSummary.provideOptions = true;
imageSummary.provideMessages = true;
imageSummary.options = {};
imageSummary.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
model.webScriptWidgets.push(imageSummary);

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
