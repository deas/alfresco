// Widget instantiation metadata...
model.webScriptWidgets = [];
var wikiToolbar = {};
wikiToolbar.name = "Alfresco.WikiToolbar";
wikiToolbar.provideMessages = true;
wikiToolbar.provideOptions = true;
wikiToolbar.options = {};
wikiToolbar.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
wikiToolbar.options.title = (page.url.templateArgs.title != null) ? page.url.templateArgs.title : "";
wikiToolbar.options.showBackLink = (args.showBackLink == "true");
model.webScriptWidgets.push(wikiToolbar);