// Widget instantiation metadata...
model.widgets = [];
var wikiCreateForm = {};
wikiCreateForm.name = "Alfresco.WikiCreateForm";
wikiCreateForm.useMessages = true;
wikiCreateForm.useOptions = true;
wikiCreateForm.options = {};
wikiCreateForm.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
wikiCreateForm.options.locale = locale.substring(0, 2);
model.widgets.push(wikiCreateForm);