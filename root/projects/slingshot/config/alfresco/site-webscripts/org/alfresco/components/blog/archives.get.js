model.webScriptWidgets = [];
var blogPostListArchive = {};
blogPostListArchive.name = "Alfresco.BlogPostListArchive";
blogPostListArchive.provideMessages = true;
blogPostListArchive.provideOptions = true;
blogPostListArchive.options = {};
blogPostListArchive.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
blogPostListArchive.options.containerId = template.properties.container != null ? template.properties.container : "documentLibrary";
model.webScriptWidgets.push(blogPostListArchive);
