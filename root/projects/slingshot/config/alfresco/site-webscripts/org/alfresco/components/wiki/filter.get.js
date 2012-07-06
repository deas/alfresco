// Widget instantiation metadata...
model.webScriptWidgets = [];
var baseFilter = {};
baseFilter.name = "Alfresco.component.BaseFilter";
baseFilter.instantiationArguments = [ "Alfresco.WikiFilter", "\"" + args.htmlid + "\""];
baseFilter.provideMessages = false;
baseFilter.provideOptions = false;
model.webScriptWidgets.push(baseFilter);