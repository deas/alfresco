function main()
{
   model.widgets = [];
   var blogPostView = {
      name : "Alfresco.BlogPostView",
      options : {
         siteId : page.url.templateArgs.site,
         containerId : "blog",
         postId : page.url.args.postId
      }
   };
   model.widgets.push(blogPostView);
}

main();
