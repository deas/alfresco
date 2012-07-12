// Widget instantiation metadata...
model.widgets = [];
var linksView = {};
linksView.name = "Alfresco.LinksView";
linksView.useMessages = true;
linksView.useOptions = true;
linksView.options = {};
linksView.options.siteId = page.url.templateArgs.site;
linksView.options.containerId = "links";
linksView.options.linkId = page.url.args.linkId;
model.widgets.push(linksView);