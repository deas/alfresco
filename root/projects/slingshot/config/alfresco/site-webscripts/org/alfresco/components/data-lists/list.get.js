model.lists = ['listA', 'listB'];

//Widget instantiation metadata...
model.webScriptWidgets = [];
var list = {};
list.name = "Alfresco.DataListList";
list.provideMessages = true;
list.provideOptions = true;
list.options = {};
list.options.siteId = (page.url.templateArgs["site"] != null) ? page.url.templateArgs["site"] : "";
model.webScriptWidgets.push(list);