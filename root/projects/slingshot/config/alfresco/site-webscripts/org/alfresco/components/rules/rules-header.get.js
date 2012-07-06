// Widget instantiation metadata...
model.webScriptWidgets = [];
var rulesHeader = {};
rulesHeader.name = "Alfresco.RulesHeader";
rulesHeader.provideMessages = true;
rulesHeader.provideOptions = true;
rulesHeader.options = {};
rulesHeader.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
rulesHeader.options.nodeRef = (page.url.args.nodeRef != null) ? page.url.args.nodeRef : "";
model.webScriptWidgets.push(rulesHeader);