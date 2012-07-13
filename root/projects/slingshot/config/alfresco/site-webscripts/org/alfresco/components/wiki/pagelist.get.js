function main()
{
   // retrieve the wiki pages for the current site
   var uri = "/slingshot/wiki/pages/" + page.url.templateArgs.site;
   var filter = page.url.args.filter;
   if (filter)
   {
      uri += "?filter=" + filter;
   }

   var connector = remote.connect("alfresco");
   var result = connector.get(uri);
   if (result.status.code == status.STATUS_OK)
   {
      model.pageList = eval('(' + result.response + ')');
   }
   else
   {
      model.error = "Error during remote call. Server code " + result.status + ".";
   }

   // Widget instantiation metadata...
   var pages = [];
   if (model.pageList != null)
   {
      for (var i=0; i<model.pageList.length; i++)
      {
         pages.push(model.pageList[i].name);
      }
   }

   
   model.widgets = [];
   var wikiList = {
      name : "Alfresco.WikiList",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         pages: pages,
         permissions : {
           create : (model.pageList != null && model.pageList.permissions != null && model.pageList.permissions.create != null) ? model.pageList.permissions.create : "false"
         },
         filterId : (page.url.args.filter != null) ? page.url.args.filter : "recentlyModified"
      }
   };
   model.widgets.push(wikiList);
}

main();

