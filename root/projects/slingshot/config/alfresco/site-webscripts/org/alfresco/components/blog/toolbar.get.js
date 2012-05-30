function main()
{
   // A default blog description
   var defaultBlog = 
   {
      permissions:
      {
         create: false,
         edit: false
      }
   };

   // Call the repo to get the permissions for the user for this blog
   var result = remote.call("/api/blog/site/" + page.url.templateArgs.site + "/" + (template.properties.container ? template.properties.container : "blog"));
   var obj = eval('(' + result + ')');
   if (result.status == 200)
   {
      // Prepare the model for the template
      model.blog = obj.item;
   }
   else
   {
      model.blog = defaultBlog;
   }
   
   // Widget instantiation metadata...
   model.webScriptWidgets = [];
   var blogToolbar = {};
   blogToolbar.name = "Alfresco.BlogToolbar";
   blogToolbar.provideMessages = true;
   blogToolbar.provideOptions = true;
   blogToolbar.options = {};
   blogToolbar.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
   blogToolbar.options.containerId = (template.properties.container != null) ? template.properties.container : "blog";
   blogToolbar.options.allowCreate = model.blog.permissions.create;
   blogToolbar.options.allowConfigure = model.blog.permissions.edit;
   model.webScriptWidgets.push(blogToolbar);
}

main();