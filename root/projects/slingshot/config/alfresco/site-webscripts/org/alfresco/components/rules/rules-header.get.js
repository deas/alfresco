// Widget instantiation metadata...
model.widgets = [];
var rulesHeader = {};
rulesHeader.name = "Alfresco.RulesHeader";
rulesHeader.useMessages = true;
rulesHeader.useOptions = true;
rulesHeader.options = {};
rulesHeader.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
rulesHeader.options.nodeRef = (page.url.args.nodeRef != null) ? page.url.args.nodeRef : "";
model.widgets.push(rulesHeader);