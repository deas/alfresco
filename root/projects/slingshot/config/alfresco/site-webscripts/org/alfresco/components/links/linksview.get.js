// Widget instantiation metadata...
model.webScriptWidgets = [];
var linksView = {};
linksView.name = "Alfresco.LinksView";
linksView.provideMessages = true;
linksView.provideOptions = true;
linksView.options = {};
linksView.options.siteId = page.url.templateArgs.site;
linksView.options.containerId = "links";
linksView.options.linkId = page.url.args.linkId;
model.webScriptWidgets.push(linksView);