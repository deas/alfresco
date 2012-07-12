// Widget instantiation metadata...
model.widgets = [];
var ruleDetails = {};
ruleDetails.name = "Alfresco.RuleDetails";
ruleDetails.useMessages = true;
ruleDetails.useOptions = true;
ruleDetails.options = {};
ruleDetails.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
ruleDetails.options.nodeRef = (page.url.templateArgs.nodeRef != null) ? page.url.templateArgs.nodeRef : "";
model.widgets.push(ruleDetails);