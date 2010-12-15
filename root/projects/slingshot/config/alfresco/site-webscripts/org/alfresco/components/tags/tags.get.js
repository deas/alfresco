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