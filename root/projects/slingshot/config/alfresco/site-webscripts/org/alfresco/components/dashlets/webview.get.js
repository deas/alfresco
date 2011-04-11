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
}

main();