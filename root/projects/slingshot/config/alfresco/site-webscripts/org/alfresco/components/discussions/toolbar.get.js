function main()
{
   // A default blog description
   var defaultForum =
   {
      forumPermissions:
      {
         create: false
      }
   };

   // Call the repo to get the permissions for the user for this blog
   var result = remote.call("/api/forum/site/" + page.url.templateArgs.site + "/" + (template.properties.container ? template.properties.container : "discussions") + "/posts?startIndex=0&pageSize=0");
   // Create javascript objects from the server response
   var obj = eval('(' + result + ')');
   if (result.status == 200)
   {
      forum = obj;
   }
   else
   {
      forum = defaultForum;
   }

   // Prepare the model for the template
   model.forum = forum;
}

main();

// Widget instantiation metadata...
model.webScriptWidgets = [];

var toolbar = {};
toolbar.name = "Alfresco.DiscussionsToolbar";
toolbar.provideOptions = true;
toolbar.provideMessages = true;
toolbar.options = {};
toolbar.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
toolbar.options.containerId = (page.url.args.containerId != null) ? page.url.args.containerId : "discussions";
toolbar.options.allowCreate = model.forum.forumPermissions.create;
model.webScriptWidgets.push(toolbar);