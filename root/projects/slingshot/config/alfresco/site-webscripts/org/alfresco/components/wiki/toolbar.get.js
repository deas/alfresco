// Widget instantiation metadata...
model.widgets = [];
var wikiToolbar = {};
wikiToolbar.name = "Alfresco.WikiToolbar";
wikiToolbar.useMessages = true;
wikiToolbar.useOptions = true;
wikiToolbar.options = {};
wikiToolbar.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
wikiToolbar.options.title = (page.url.templateArgs.title != null) ? page.url.templateArgs.title : "";
wikiToolbar.options.showBackLink = (args.showBackLink == "true");
model.widgets.push(wikiToolbar);