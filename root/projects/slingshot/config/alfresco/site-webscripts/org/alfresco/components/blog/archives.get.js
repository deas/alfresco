function main()
{
   model.widgets = [];
   var blogPostListArchive = {
      name : "Alfresco.BlogPostListArchive",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : template.properties.container != null ? template.properties.container : "documentLibrary"
      }
   };
   model.widgets.push(blogPostListArchive);
}

main();
