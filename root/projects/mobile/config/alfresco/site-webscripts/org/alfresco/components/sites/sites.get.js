<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils.js">

if (page.url.args.view != undefined) {
  model.view = getSiteInfo(page.url.args.view);
}
else {
  model.view = "Fav";
}

var userSites = getUserSites();
model.sites = userSites.sites;
model.favSites = userSites.favSites;
model.allSites = getAllSites();
model.backButton = true;