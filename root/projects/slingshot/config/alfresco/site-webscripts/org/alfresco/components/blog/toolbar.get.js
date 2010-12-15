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
}

main();