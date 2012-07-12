<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function getTags(site, container)
{
    var theUrl = "/api/tagscopes/site/" + site + "/" + container + "/tags";
    var data = doGetCall(theUrl, true);
    return data;
}

var site = page.url.templateArgs.site,
   container = template.properties.container,
   tags = [];

var data = getTags(site, container);
if (data && data.tags)
{
   tags = data.tags;
}

model.tags = tags;

// Widget instantiation metadata...
model.widgets = [];
var tagComponent = {};
tagComponent.name = "Alfresco.TagComponent";
tagComponent.useMessages = true;
tagComponent.useOptions = true;
tagComponent.options = {};
tagComponent.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
tagComponent.options.containerId = (template.properties.container != null) ? template.properties.container : "";
model.widgets.push(tagComponent);