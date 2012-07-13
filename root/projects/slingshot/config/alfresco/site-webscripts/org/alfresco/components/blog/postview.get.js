function main()
{
   var blogPostView = {
      id: "BlogPostView",
      name : "Alfresco.BlogPostView",
      options : {
         siteId : page.url.templateArgs.site,
         containerId : "blog",
         postId : page.url.args.postId
      }
   };
   model.widgets = [blogPostView];
}

main();
