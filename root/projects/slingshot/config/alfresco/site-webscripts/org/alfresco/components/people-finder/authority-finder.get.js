// Widget instantiation metadata...
model.widgets = [];
var authorityFinder = {};
authorityFinder.name = "Alfresco.AuthorityFinder";
authorityFinder.useMessages = true;
authorityFinder.useOptions = true;
authorityFinder.options = {};
authorityFinder.options.siteId = (page.exists == true) ? ((page.url.templateArgs.site != null) ? page.url.templateArgs.site : "") : ((args.site != null) ? args.site : "");
authorityFinder.options.minSearchTermLength = (args.minSearchTermLength != null) ? args.minSearchTermLength : "3";
authorityFinder.options.maxSearchResults = (args.maxSearchResults != null) ? args.maxSearchResults : "100";
authorityFinder.options.setFocus = (args.setFocus != null) ? args.setFocus : "false";
authorityFinder.options.addButtonSuffix = (args.addButtonSuffix != null) ? args.addButtonSuffix : "";
authorityFinder.options.dataWebScript = { ___value : "dataWebScript", ___type: "REFERENCE"};
authorityFinder.options.viewMode = { ___value : "Alfresco.AuthorityFinder.VIEW_MODE_DEFAULT", ___type: "REFERENCE"};
authorityFinder.options.authorityType = { ___value : "Alfresco.AuthorityFinder.AUTHORITY_TYPE_ALL", ___type: "REFERENCE"};
model.widgets.push(authorityFinder);