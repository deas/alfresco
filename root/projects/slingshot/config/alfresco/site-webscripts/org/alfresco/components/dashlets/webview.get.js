function main()
{
   var uri = args.webviewURI,
      webviewTitle = '',
      isDefault = false;

   if (!uri)
   {
      // Use the default
      var conf = new XML(config.script);
      uri = conf.uri[0].toString();
      isDefault = true;
   }

   if (args.webviewTitle)
   {
      webviewTitle = args.webviewTitle;
   }

   var height = args.height;
   if (!height)
   {
      height = "";
   }

   var re = /^(http|https):\/\//;

   if (!isDefault && !re.test(uri))
   {
      uri = "http://" + uri;
   }

   model.webviewTitle = webviewTitle;
   model.uri = uri;
   model.height = height;
   model.isDefault = isDefault;

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
   
   // Widget instantiation metadata...
   model.webScriptWidgets = [];

   var webView = {};
   webView.name = "Alfresco.dashlet.WebView";
   webView.assignToVariable = "webView";
   webView.provideOptions = true;
   webView.provideMessages = true;
   webView.options = {};
   webView.options.componentId = instance.object.id;
   webView.options.webviewURI = model.uri;
   webView.options.webviewTitle = model.webviewTitle;
   webView.options.webviewHeight = model.height;
   webView.options.isDefault = model.isDefault;
   model.webScriptWidgets.push(webView);

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
         eventOnClick: { ___value : "editWebViewDashletEvent", ___type: "REFERENCE"}, 
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
}

main();