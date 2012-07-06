// Widget instantiation metadata...
model.webScriptWidgets = [];
var rulesList = {};
rulesList.name = "Alfresco.RulesList";
rulesList.provideMessages = true;
rulesList.provideOptions = true;
rulesList.options = {};
rulesList.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
rulesList.options.nodeRef = (page.url.args.nodeRef != null) ? page.url.args.nodeRef : "";
rulesList.options.filter = (args.filter != null) ? args.filter : "";
rulesList.options.selectDefault = (args.selectDefault != null) ? args.selectDefault : "false";
rulesList.options.editable = (args.editable != null) ? args.editable : "false";
model.webScriptWidgets.push(rulesList);