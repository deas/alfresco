model.lists = ['listA', 'listB'];

//Widget instantiation metadata...
model.widgets = [];
var list = {};
list.name = "Alfresco.DataListList";
list.useMessages = true;
list.useOptions = true;
list.options = {};
list.options.siteId = (page.url.templateArgs["site"] != null) ? page.url.templateArgs["site"] : "";
model.widgets.push(list);