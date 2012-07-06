// Widget instantiation metadata...
model.webScriptWidgets = [];
var wikiCreateForm = {};
wikiCreateForm.name = "Alfresco.WikiCreateForm";
wikiCreateForm.provideMessages = true;
wikiCreateForm.provideOptions = true;
wikiCreateForm.options = {};
wikiCreateForm.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
wikiCreateForm.options.locale = locale.substring(0, 2);
model.webScriptWidgets.push(wikiCreateForm);