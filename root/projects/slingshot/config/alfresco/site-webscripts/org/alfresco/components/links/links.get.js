// Widget instantiation metadata...
model.webScriptWidgets = [];
var links = {};
links.name = "Alfresco.Links";
links.provideMessages = true;
links.provideOptions = true;
links.options = {};
links.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
links.options.containerId = (page.url.templateArgs.container != null) ? page.url.templateArgs.container : "links";
links.options.initialFilter = {};
links.options.initialFilter.filterId = (page.url.args.filterId != null) ? page.url.args.filterId : "all";
links.options.initialFilter.filterOwner = (page.url.args.filterOwner != null) ? page.url.args.filterOwner : "Alfresco.LinkFilter";
links.options.initialFilter.filterData = page.url.args.filterData;
model.webScriptWidgets.push(links);