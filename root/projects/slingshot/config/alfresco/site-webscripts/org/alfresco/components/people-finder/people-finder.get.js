// Widget instantiation metadata...
var searchConfig = config.scoped['Search']['search'],
    defaultMinSearchTermLength = searchConfig.getChildValue('min-search-term-length'),
    defaultMaxSearchResults = searchConfig.getChildValue('max-search-results');

model.webScriptWidgets = [];
var peopleFinder = {};
peopleFinder.name = "Alfresco.PeopleFinder";
peopleFinder.provideMessages = true;
peopleFinder.provideOptions = true;
peopleFinder.options = {};
peopleFinder.options.userId = user.name;
peopleFinder.options.siteId = (this.page != null) ? ((this.page.url.templateArgs.site != null) ? this.page.url.templateArgs.site : "") : ((args.site != null) ? args.site : "");
peopleFinder.options.minSearchTermLength = (args.minSearchTermLength != null) ? args.minSearchTermLength : defaultMinSearchTermLength;
peopleFinder.options.maxSearchResults = (args.maxSearchResults != null) ? args.maxSearchResults : defaultMaxSearchResults;
peopleFinder.options.setFocus = (args.setFocus != null) ? args.setFocus : "false";
peopleFinder.options.addButtonSuffix = (args.addButtonSuffix != null) ? args.addButtonSuffix : "";
peopleFinder.options.dataWebScript = ((args.dataWebScript != null) ? args.dataWebScript : "api/groups").replace(/{/g, "[").replace(/}/g, "]");
peopleFinder.options.viewMode = { ___value : "Alfresco.PeopleFinder.VIEW_MODE_DEFAULT", ___type: "REFERENCE"};
model.webScriptWidgets.push(peopleFinder);