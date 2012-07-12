<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

model.rootNode = DocumentList.getConfigValue("RepositoryLibrary", "root-node", "alfresco://company/home");

// Widget instantiation metadata...
model.widgets = [];
var rulesNone = {};
rulesNone.name = "Alfresco.RulesNone";
rulesNone.useMessages = true;
rulesNone.useOptions = true;
rulesNone.options = {};
rulesNone.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
rulesNone.options.nodeRef = (page.url.args.nodeRef != null) ? page.url.args.nodeRef : "";
rulesNone.options.repositoryBrowsing = (model.rootNode != null);
model.widgets.push(rulesNone);