function main()
{
   var blogPostList = {
      id : "BlogPostList",
      name : "Alfresco.BlogPostList",
      options : {
         siteId :(page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : (template.properties.container != null) ? template.properties.container : "blog",
         initialFilter : {
            filterId : (page.url.args.filterId != null) ? page.url.args.filterId : "new", 
            filterOwner : (page.url.args.filterOwner != null) ? page.url.args.filterOwner : "Alfresco.BlogPostListFilter",
            filterData : page.url.args.filterData
         }
      }
   };
   model.widgets = [blogPostList];
}

main();
