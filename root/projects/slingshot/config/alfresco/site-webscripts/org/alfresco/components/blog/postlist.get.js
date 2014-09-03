function main()
{
   var blogPostList = {
      id : "BlogPostList",
      name : "Alfresco.BlogPostList",
      options : {
         siteId :(page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : (template.properties.container != null) ? template.properties.container : "blog",
         initialFilter : {
            filterId : (page.url.args.tag != null) ? "tag" : "new", 
            filterOwner : (page.url.args.tag != null) ? "Alfresco.TagFilter" : "Alfresco.BlogPostListFilter",
            filterData : page.url.args.tag
         }
      }
   };
   model.widgets = [blogPostList];
}

main();
