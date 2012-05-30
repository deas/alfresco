model.webScriptWidgets = [];
var blogPostList = {};
blogPostList.name = "Alfresco.BlogPostList";
blogPostList.provideMessages = true;
blogPostList.provideOptions = true;
blogPostList.options = {};
blogPostList.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
blogPostList.options.containerId = (template.properties.container != null) ? template.properties.container : "blog";


blogPostList.options.initialFilter = {};
blogPostList.options.initialFilter.filterId = (page.url.args.filterId != null) ? page.url.args.filterId : "new"; 
blogPostList.options.initialFilter.filterOwner = (page.url.args.filterOwner != null) ? page.url.args.filterOwner : "Alfresco.BlogPostListFilter";
blogPostList.options.initialFilter.filterData = page.url.args.filterData;

model.webScriptWidgets.push(blogPostList);
