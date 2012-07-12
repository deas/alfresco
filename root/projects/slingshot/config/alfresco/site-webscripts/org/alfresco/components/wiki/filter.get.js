// Widget instantiation metadata...
model.widgets = [];
var baseFilter = {};
baseFilter.name = "Alfresco.component.BaseFilter";
baseFilter.initArgs = [ "Alfresco.WikiFilter", "\"" + args.htmlid + "\""];
baseFilter.useMessages = false;
baseFilter.useOptions = false;
model.widgets.push(baseFilter);