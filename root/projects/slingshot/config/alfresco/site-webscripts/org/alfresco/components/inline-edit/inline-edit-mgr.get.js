// Widget instantiation metadata...
model.webScriptWidgets = [];
var inlineEditMgr = {};
inlineEditMgr.name = "Alfresco.InlineEditMgr";
inlineEditMgr.provideMessages = true;
inlineEditMgr.provideOptions = true;
inlineEditMgr.options = {};
inlineEditMgr.options.nodeRef = page.url.args.nodeRef;
inlineEditMgr.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
model.webScriptWidgets.push(inlineEditMgr);