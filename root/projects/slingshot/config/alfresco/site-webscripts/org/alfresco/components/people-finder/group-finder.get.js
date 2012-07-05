// Widget instantiation metadata...
var searchConfig = config.scoped['Search']['search'],
    defaultMinSearchTermLength = searchConfig.getChildValue('min-search-term-length'),
    defaultMaxSearchResults = searchConfig.getChildValue('max-search-results');

model.webScriptWidgets = [];
var groupFinder = {};
groupFinder.name = "Alfresco.GroupFinder";
groupFinder.provideMessages = true;
groupFinder.provideOptions = true;
groupFinder.options = {};
groupFinder.options.siteId = (page.exists == true) ? ((page.url.templateArgs.site != null) ? page.url.templateArgs.site : "") : ((args.site != null) ? args.site : "");
groupFinder.options.minSearchTermLength = (args.minSearchTermLength != null) ? args.minSearchTermLength : defaultMinSearchTermLength;
groupFinder.options.maxSearchResults = (args.maxSearchResults != null) ? args.maxSearchResults : defaultMaxSearchResults;
groupFinder.options.setFocus = (args.setFocus != null) ? args.setFocus : "false";
groupFinder.options.addButtonSuffix = (args.addButtonSuffix != null) ? args.addButtonSuffix : "";
groupFinder.options.dataWebScript = ((args.dataWebScript != null) ? args.dataWebScript : "api/groups").replace(/{/g, "[").replace(/}/g, "]");
model.webScriptWidgets.push(groupFinder);