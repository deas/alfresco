// http://localhost:8080/alfresco/service/api/sites/{shortname}
function getSiteInfo(site)
{
  var data  = remote.call("/api/sites/" + stringUtils.urlEncode(site));
  return JSON.parse(data);
}

model.site = getSiteInfo(page.url.args.site);
model.pageTitle = 'Sites';
model.backButton = true;