// Widget instantiation metadata...
model.webScriptWidgets = [];
var ruleDetails = {};
ruleDetails.name = "Alfresco.RuleDetails";
ruleDetails.provideMessages = true;
ruleDetails.provideOptions = true;
ruleDetails.options = {};
ruleDetails.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
ruleDetails.options.nodeRef = (page.url.templateArgs.nodeRef != null) ? page.url.templateArgs.nodeRef : "";
model.webScriptWidgets.push(ruleDetails);