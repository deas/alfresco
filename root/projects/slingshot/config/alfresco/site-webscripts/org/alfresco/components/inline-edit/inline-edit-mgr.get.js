// Widget instantiation metadata...
model.widgets = [];
var inlineEditMgr = {};
inlineEditMgr.name = "Alfresco.InlineEditMgr";
inlineEditMgr.useMessages = true;
inlineEditMgr.useOptions = true;
inlineEditMgr.options = {};
inlineEditMgr.options.nodeRef = page.url.args.nodeRef;
inlineEditMgr.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
model.widgets.push(inlineEditMgr);