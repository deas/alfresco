
/* *********************************************************************************
 *                                                                                 *
 * LICENSE INFO                                                                    *
 *                                                                                 *
 ***********************************************************************************/

/**
 *
 * @returns {object} The usage information for the current license.
 */
function getLicenseUsage() {
   // Only retrieve license usage information for the first few seconds after login
   // this ensures the usage information is not continually queried.
   // This could be improved by having a central usage service on the web-tier that
   // is responsible for retrieving the usage in a more sensible schedule.
   var usage = null;
   if (user.properties["alfUserLoaded"] > new Date().getTime() - 5000)
   {
      // retrieve license usage information
      var result = remote.call("/api/admin/usage");
      if (result.status.code == status.STATUS_OK)
      {
         usage = eval('(' + result + ')');
      }
   }
   return usage;
}


/* *********************************************************************************
 *                                                                                 *
 * RECENT SITES HANDLING                                                           *
 *                                                                                 *
 ***********************************************************************************/

/**
 * This function will update the Recent Sites preferences for the user if required. An update will only
 * be needed if the user is currently on a site page and that site page is not already the most recent
 * entry in the recent sites list.
 */
function updateRecentSites() {

   if (page.url.templateArgs.site)
   {
      // Get the preferences for the current user...
      var result = remote.call("/api/people/" + encodeURIComponent(user.name) + "/preferences"),
          prefs,
          recentSites;
      if (result.status == 200 && result != "{}")
      {
         prefs = eval('(' + result + ')');
         recentSites = eval('try{(prefs.org.alfresco.share.sites.recent)}catch(e){}');
      }
      // Check that recentSites and favourites have been initialised by the successful
      // response of requesting preferences. If not then just make them a new object and
      // this will be reflected in the UI as there being no recent sites or favourites.
      if (typeof recentSites != "object")
      {
         recentSites = {};
      }

      // Make an array of the recent sites...
      var orderedRecentSites = [],
          siteId = page.url.templateArgs.site,
          currentSiteIndex = null,
          updateRequired = false;
      for (var index in recentSites)
      {
         // Add the site to the array if its mapped to a valid index
         // The only way to use a number as a JSON object key and have it encoded successfully
         // was to add a "_" prefix. Therefore it is necessary to check that we have at least
         // 2 characters and then discard the first character (the underscore)...
         if (index.length > 1)
         {
            var numericalIndex = index.substr(1);
            if (!isNaN(numericalIndex))
            {
               orderedRecentSites[numericalIndex] = recentSites[index];
            }
         }
      }

      // Clean up the array...
      for (var i=0; i < orderedRecentSites.length; i++)
      {
         // Remove any gaps...
         if (orderedRecentSites[i] == undefined)
         {
            orderedRecentSites.splice(i,1);
         }
         else if (orderedRecentSites[i] == siteId)
         {
            // If the current recent site is the site being visited then it needs to go to the top of the list
            currentSiteIndex = i;
         }
      }

      // Update the array...
      if (siteId)
      {
         if (currentSiteIndex == null)
         {
            // We're on a site but it's not in the list. We need to push the site to the top of the list and
            // remove the last element (if the list already contains 10 entries).
            orderedRecentSites.unshift(siteId);  // Push the current site to the front of the queue
            updateRequired = true;
         }
         else if (currentSiteIndex != 0)
         {
            // We're on a site and it's not the most recent site (this would be the case when moving between pages
            // in a site, e.g. from the dashboard to the document library. In that instance we wouldn't want to
            // save any changes to recent sites because it would be an unnecessary post.
            orderedRecentSites.splice(currentSiteIndex, 1); // Remove the siteId from it's current location
            orderedRecentSites.unshift(siteId);             // Push it to the front of the queue
            updateRequired = true;
         }
      }

      var maxRecentSites = 5;
      if (config.global.header && config.global.header.maxRecentSites)
      {
         maxRecentSites = config.global.header.maxRecentSites;
      }
      while (orderedRecentSites.length > maxRecentSites)
      {
         orderedRecentSites.pop(); // Remove the last entry
         updateRequired = true;
      }

      if (updateRequired)
      {
         // Build a map of the latest recent sites information to save as the latest preferences.
         // Even if a save isn't required this object is useful for building the Recent Site menu
         // item widget configuration...
         var recentSitePrefsUpdate = {};
         for (var i=0; i < orderedRecentSites.length; i++)
         {
            // The "_" prefix is to prevent org.mozilla.javascript.UniqueTag errors...
            recentSitePrefsUpdate["_" + i] = orderedRecentSites[i];
         }

         // Construct the JSON object with the updated Recent Site preferences and save them back to the repository...
         var recentSitesUpdate = { org : { alfresco : { share : { sites: { recent : recentSitePrefsUpdate}}}}};
         var jsonString = jsonUtils.toJSONString(recentSitesUpdate);
         var connector = remote.connect("alfresco");
         connector.post("/api/people/" + encodeURIComponent(user.name) + "/preferences", jsonString, "application/json");
      }
   }
}

/* *********************************************************************************
 *                                                                                 *
 * CONSTRUCT SITE NAVIGATION MENU ITEMS                                            *
 *                                                                                 *
 ***********************************************************************************/

/**
 * Collaboration Site Title component GET method
 */
function getSiteData()
{
   var siteId = page.url.templateArgs.site,
       siteData = null;
   if (siteId != null)
   {
      if (model.siteData == null)
      {
         // Call the repository for the site profile
         var json = remote.call("/api/sites/" + siteId);
         var profile =
         {
            title: "",
            shortName: "",
            visibility: "PRIVATE"  // Default to PRIVATE as if the site is PRIVATE and the user doesn't have access, this won't get updated!!
         };

         if (json.status == 200)
         {
            // Create javascript objects from the repo response
            var obj = eval('(' + json + ')');
            if (obj)
            {
               profile = obj;
            }
         }

         // Call the repository to see if the user is site manager or not
         var userIsSiteManager = false,
             userIsMember = false;
         json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + encodeURIComponent(user.name));
         if (json.status == 200)
         {
            var obj = eval('(' + json + ')');
            if (obj)
            {
               userIsMember = true;
               userIsSiteManager = obj.role == "SiteManager";
            }
         }

         siteData = {};
         siteData.profile = profile;
         siteData.userIsSiteManager = userIsSiteManager;
         siteData.userIsMember = userIsMember;

         // Store this in the model to allow for repeat calls to the function (and therefore
         // prevent multiple REST calls to the Repository)...
         // It also needs to be set in the model as the "userIsSiteManager" is required by the template...
         model.siteData = siteData;
      }
      else
      {
         siteData = model.siteData;
      }
   }
   return siteData;
}

/**
 * @method getPages
 * @param includeUnusedPages IF true all pages will be returned, if false only the pages used by the current site
 * @return the pages used in the site and optionally unused as well, if so at the end of list.
 * [
 *    {
 *       pageId:        {String}  // The id of the page
 *       pageUrl:       {String}  // The page's url, either page id or overriden in the the share-config.xml SitePages/pages/page
 *       sitePageTitle: {String}  // Title, if given by the Site's administrator in the customise page ui, if null use ...
 *       title:         {String}  // ... title from page's xml descriptor or i18n msg key.
 *       description:   {String}  // Description from page's xml descriptor or i18n msg key
 *       used:          {boolean} // Set to true if page is used on this site
 *    }
 * ]
 */
function getPages(includeUnusedPages)
{
   var siteId = page.url.templateArgs.site,
       pages = null;
   if (siteId)
   {
      var dashboardPageData = sitedata.getPage("site/" + siteId + "/dashboard");
      if (dashboardPageData !== null)
      {
         pages = [];

         // Wrap sitePages array in a temporary object so jsonUtils.toObject can be used to parse the string
         var sitePages = dashboardPageData.properties.sitePages,
            pageMetadata = dashboardPageData.properties.pageMetadata,
            configPages = config.scoped["SitePages"]["pages"].childrenMap["page"],
            urlMap = {},
            pageId;

         if (sitePages)
         {
            try
            {
               // Parse json using Java to a org.json.simple.JSONArray (wrap in an object to keep toObject happy)
               sitePages = jsonUtils.toObject('{"tmp":' + dashboardPageData.properties.sitePages + '}').tmp;

               // Print array as json and use eval so we get a Rhino javascript array to execute as usual
               sitePages = eval("(" + sitePages.toString() + ")");
            }
            catch(e)
            {
               sitePages = [];
            }
         }
         else
         {
            sitePages = [];
         }
         if (pageMetadata)
         {
            try
            {
               // Parse json using Java to a org.json.simple.JSONObject with an Array
               pageMetadata = jsonUtils.toObject('{"tmp":[' + pageMetadata + ']}').tmp;

               // Print object as json and use eval so we get a Rhino javascript object to execute as usual
               pageMetadata = eval("(" + pageMetadata.toString() + ")")[0];
            }
            catch(e){
               pageMetadata = {};
            }
         }
         else
         {
            pageMetadata = {};
         }

         // Get the page urls from config
         for (var i = 0; i < configPages.size(); i++)
         {
            // Get page id from config file
            pageId = configPages.get(i).attributes["id"];
            if (pageId)
            {
               urlMap[pageId] = configPages.get(i).value;
            }
         }

         // Add used pages in the order decided by user
         for (var i = 0; i < sitePages.length; i++)
         {
            pages.push(
            {
               pageId: sitePages[i].pageId,
               sitePageTitle: sitePages[i].sitePageTitle || null,
               used: true
            });
         }

         // Add the unused pages if requested
         for (var i = 0, il = configPages.size(); includeUnusedPages && i < il; i++)
         {
            pageId = configPages.get(i).attributes["id"];
            for (var j = 0, jl = pages.length; j < jl; j++)
            {
               if (pageId == pages[j].pageId)
               {
                  break;
               }
            }
            if (j == jl)
            {
               pages.push(
               {
                  pageId: pageId,
                  used: false
               });
            }
         }

         var titleId, descriptionId, pageData, pageMeta, p;

         // Get page details
         for (var i = 0, il = pages.length; i < il; i++)
         {
            p = pages[i];
            pageId = p.pageId;
            pageData = sitedata.getPage(pageId);
            pageMeta = pageMetadata[pageId] || {};
            if (pageData != null)
            {
               p.pageUrl = urlMap[pageId] || pageId;

               // Title from page's xml descriptor or property bundle if key is given
               p.title = pageMeta.title || pageData.title;
               titleId = pageMeta.titleId || pageData.titleId;
               p.title = titleId ? msg.get(titleId) : p.title;
               p.titleId = titleId;

               // Description from page's xml descriptor or property bundle if key is given
               p.description = pageMeta.description || pageData.description;
               descriptionId = pageMeta.descriptionId || pageData.descriptionId;
               p.description = descriptionId ? msg.get(descriptionId) : p.description;
            }
            else
            {
               // page does not exist! output error to help the developer
               p.title = "ERROR: page " + pageId + " not found!";
            }
         }
      }
   }
   // Prepare template model
   return pages;
};

/**
 *
 * @returns {array} An array of objects that represent pages.
 */
function getSitePages() {
   //Get site specific information...
   var sitePages = null;
   if (page.url.templateArgs.site)
   {
      getSiteData();
      updateRecentSites();
      sitePages = getPages();
   }
   return sitePages;
}


/**
 * Constructs the model of widgets for navigation around a site, e.g. "Dashboard", "DocumentLibrary", etc.
 * The contents of the model are based on the preset defined for the current site. If the current page
 * does not live within a site then this will simply return an empty array.
 *
 * @returns {array} An array of widgets that allow the user to navigate around a site.
 */
function getSiteNavigationWidgets() {
   /*
    * Currently pages are only defined for sites. By default the site dashboard and members
    * pages are always included for sites.
    */
   var navigationWidgets = [],
       pages = getSitePages();
   if (pages != null)
   {
      // Dashboard always appears in the displayed menu
      // Members always appears in the More menu
      // The current page should always be in the displayed menu
      // IF the current page is normally in the More menu then one other item gets bumped into the More menu?
      // The current page is ALWAYS displayed in the main menu (regardless of whether it is usually in the More menu)?

      // Construct an array of all the pages in the site...
      navigationWidgets.push({
         id: "HEADER_SITE_DASHBOARD",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            id: "HEADER_SITE_DASHBOARD",
            label: msg.get("page.siteDashboard.title"),
            targetUrl: "site/" + page.url.templateArgs.site + "/dashboard",
            selected: (page.titleId == "page.siteDashboard.title")
         }
      });
      for (var i=0; i<pages.length; i++)
      {
         var targetUrl = "site/" + page.url.templateArgs.site + "/" + pages[i].pageUrl;
         navigationWidgets.push({
            id: "HEADER_SITE_" + pages[i].pageId.toUpperCase(),
            name: "alfresco/menus/AlfMenuBarItem",
            config: {
               id: "HEADER_SITE_" + pages[i].pageId.toUpperCase(),
               label: (pages[i].sitePageTitle) ? pages[i].sitePageTitle : pages[i].title,
               pageId: pages[i].pageId,
               targetUrl: targetUrl,
               selected: ((page.url.url.startsWith(page.url.servletContext + "/" + targetUrl)) || 
                          (pages[i].pageId == "documentlibrary" && page.url.url.startsWith(page.url.servletContext + "/site/" + page.url.templateArgs.site + "/document-details")) ||
                          (pages[i].pageId == "wiki-page" && (page.url.url.startsWith(page.url.servletContext + "/site/" + page.url.templateArgs.site + "/wiki"))) ||
                          (pages[i].pageId == "blog-postlist" && (page.url.url.startsWith(page.url.servletContext + "/site/" + page.url.templateArgs.site + "/blog"))) ||
                          (pages[i].pageId == "discussions-topiclist" && (page.url.url.startsWith(page.url.servletContext + "/site/" + page.url.templateArgs.site + "/discussions"))))
            }
         });
      }
      navigationWidgets.push({
         id: "HEADER_SITE_MEMBERS",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            id: "HEADER_SITE_MEMBERS",
            label: msg.get("page.siteMembers.title"),
            targetUrl: "site/" + page.url.templateArgs.site + "/site-members",
            selected: ((page.titleId == "page.siteMembers.title") ||
                       (page.url.url.startsWith(page.url.servletContext + "/site/" + page.url.templateArgs.site + "/site-groups")) ||
                       (page.url.url.startsWith(page.url.servletContext + "/site/" + page.url.templateArgs.site + "/invite")) ||
                       (page.url.url.startsWith(page.url.servletContext + "/site/" + page.url.templateArgs.site + "/add-groups")) ||
                       (page.url.url.startsWith(page.url.servletContext + "/site/" + page.url.templateArgs.site + "/pending-invites")))
         }
      });

      var maxDisplayedSitePages = 3;
      if (config.global.header && config.global.header.maxDisplayedSitePages)
      {
         maxDisplayedSitePages = config.global.header.maxDisplayedSitePages;
      }
      if (navigationWidgets.length > maxDisplayedSitePages)
      {
         // Make sure that if the current page is in the main menu (e.g. if it would otherwise be in the
         // "More" menu...
         for (var i=maxDisplayedSitePages-1; i<navigationWidgets.length; i++)
         {
            if (navigationWidgets[i].config.selected)
            {
               navigationWidgets.splice(maxDisplayedSitePages-2, 0, navigationWidgets.splice(i, 1)[0]);
            }
         }
         // Move the appropriate number of items into the More menu...
         var forMoreMenu = navigationWidgets.splice(maxDisplayedSitePages -1, navigationWidgets.length - maxDisplayedSitePages + 1);
         navigationWidgets.push({
            id: "HEADER_SITE_MORE_PAGES",
            name: "alfresco/menus/AlfMenuBarPopup",
            config: {
               id: "HEADER_SITE_MORE_PAGES",
               label: "page.navigation.more.label",
               widgets: [
                  {
                     name: "alfresco/menus/AlfMenuGroup",
                     config: {
                        widgets: forMoreMenu
                     }
                  }
               ]
            }
         });
      }
   }
   return navigationWidgets;
}


function getSubNavigationWidgets() {
   var navigationWidgets = []
   if (page.id == "search")
   {
      // Build the advanced search query...
      var args = page.url.args;
      if (args["t"] != null || args["tag"] != null || args["q"] != null)
      {
         var query = "st=" + (args["t"] != null ? encodeURIComponent(args["t"]) : "") +
                     "&stag=" + (args["tag"] != null ? encodeURIComponent(args["tag"]) : "") +
                     "&ss=" + (args["s"] != null ? encodeURIComponent(args["s"]) : "") +
                     "&sa=" + (args["a"] != null ? encodeURIComponent(args["a"]) : "") +
                     "&sr=" + (args["r"] != null ? encodeURIComponent(args["r"]) : "") +
                     "&sq=" + (args["q"] != null ? encodeURIComponent(args["q"]) : "");
         
      }
      var advancedSearchUrl = "advsearch?" + query;
      if (page.url.templateArgs.site == null)
      {
         // We're on the basic search page
         // No additional navigation links.
      }
      else
      {
         // We're on the search page launched from a site
         // Make sure the site data is loaded so that we can get the title...
         var siteData = getSiteData();
         navigationWidgets.push({
            id: "HEADER_SEARCH_BACK_TO_SITE_DASHBOARD",
            name: "alfresco/menus/AlfMenuBarItem",
            config: {
               id: "HEADER_SEARCH_BACK_TO_SITE_DASHBOARD",
               label: msg.get("header.backlink", [siteData.profile.title]),
               iconClass: "alf-back-icon",
               targetUrl: "site/" + page.url.templateArgs.site + "/dashboard",
               selected: false
            }
         });
         
         advancedSearchUrl = "site/" + page.url.templateArgs.site + "/" + advancedSearchUrl;
      }
      
      // Add the advanced search link...
      navigationWidgets.push({
         id: "HEADER_ADVANCED_SEARCH",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            id: "HEADER_ADVANCED_SEARCH",
            label: msg.get("header.advanced"),
            iconClass: "alf-forward-icon",
            targetUrl: advancedSearchUrl,
            selected: false
         }
      });
   }
   else if (page.id == "advsearch")
   {
      // We're on the advanced search page
      var args = page.url.args;
      if (args["st"] != null || args["stag"] != null)
      {
         var query = "t=" + (args["st"] != null ? encodeURIComponent(args["st"]) : "") +
                     "&tag=" + (args["stag"] != null ? encodeURIComponent(args["stag"]) : "") +
                     "&s=" + (args["ss"] != null ? encodeURIComponent(args["ss"]) : "") +
                     "&a=" + (args["sa"] != null ? encodeURIComponent(args["sa"]) : "") +
                     "&r=" + (args["sr"] != null ? encodeURIComponent(args["sr"]) : "") +
                     "&q=" + (args["sq"] != null ? encodeURIComponent(args["sq"]) : "");
         model.backlink = query;
         
         var searchUrl = "search?" + query;
         if (page.url.templateArgs.site == null)
         {
            // No update if not a site...
         }
         else
         {
            searchUrl = "site/" + page.url.templateArgs.site + "/" + searchUrl;
         }
         
         navigationWidgets.push({
            id: "HEADER_SEARCH_BACK_TO_RESULTS",
            name: "alfresco/menus/AlfMenuBarItem",
            config: {
               id: "HEADER_SEARCH_BACK_TO_RESULTS",
               label: msg.get("header.results"),
               iconClass: "alf-back-icon",
               targetUrl: searchUrl,
               selected: false
            }
         });
      }
      
      if (page.url.templateArgs.site == null)
      {
         // We're on the basic search page
         // No additional navigation links.
      }
      else
      {
         // We're on the search page launched from a site
         // Make sure the site data is loaded so that we can get the title...
         var siteData = getSiteData();
         navigationWidgets.push({
            id: "HEADER_SEARCH_BACK_TO_SITE_DASHBOARD",
            name: "alfresco/menus/AlfMenuBarItem",
            config: {
               id: "HEADER_SEARCH_BACK_TO_SITE_DASHBOARD",
               label: msg.get("header.backlink", [siteData.profile.title]),
               iconClass: "alf-back-icon",
               targetUrl: "site/" + page.url.templateArgs.site + "/dashboard",
               selected: false
            }
         });
      }
   }
   else if (page.url.templateArgs.site != null)
   {
      // Get the standard navigation widgets (expected to be site pages)...
      var siteData = getSiteData();
      if (siteData.profile.visibility != "PUBLIC" && siteData.userIsMember == false)
      {
         navigationWidgets = [];
      }
      else
      {
         navigationWidgets = getSiteNavigationWidgets();
      }
   }
   else
   {
      // No navigation widgets. Leave as default empty array.
   }
   return navigationWidgets;
}


/* *********************************************************************************
 *                                                                                 *
 * USER STATUS WIDGET DEFINITION                                                   *
 *                                                                                 *
 ***********************************************************************************/
function getUserStatusWidget()
{
   var userStatus = "",
      userStatusTime = "";

   if (user.properties["userStatus"] != null)
   {
      userStatus = user.properties["userStatus"];
   }
   if (user.properties["userStatusTime"] != null)
   {
      userStatusTime = user.properties["userStatusTime"];
   }

   return {
      id: "HEADER_USER_STATUS",
      name: "alfresco/header/CurrentUserStatus",
      config: {
         id: "HEADER_USER_STATUS",
         userStatus: userStatus,
         userStatusTime: userStatusTime
      }
   };
}

/* *********************************************************************************
 *                                                                                 *
 * BUILD URI TEMPLATE MAP                                                          *
 *                                                                                 *
 ***********************************************************************************/
/*
 * Create map of all the URI templates (mapping the id to the template)
 * This map will be used when processing header items of type "link" when
 * rendering the header using the legacy configuration. It is possible to
 * specify the name of a URI template and for the tokens to be substituted.
 */
var uriTemplateMap = {};
if (config.scoped["UriTemplate"] &&
    config.scoped["UriTemplate"]["uri-templates"] &&
    config.scoped["UriTemplate"]["uri-templates"].childrenMap["uri-template"])
{
   var uriTemplates = config.scoped["UriTemplate"]["uri-templates"].childrenMap["uri-template"];
   for (var ii=0; ii<uriTemplates.size(); ii++)
   {
      var currUriTemplate = uriTemplates.get(ii);
      uriTemplateMap[currUriTemplate.attributes["id"]] = currUriTemplate.value;
   }
}

/* *********************************************************************************
 *                                                                                 *
 * BUILD HELP LINK MAP                                                             *
 *                                                                                 *
 ***********************************************************************************/
/*
 * Create a map of all the help links. Just like the URI templates these can be
 * used in the legacy header configuration to specify a link. This map is used int
 * the "substituteTokens" function.
 */
var helpMap = {};
if (config.scoped["HelpPages"] &&
    config.scoped["HelpPages"]["help-pages"] &&
    config.scoped["HelpPages"]["help-pages"].getChildren() != null)
{
   var helpConfig = config.scoped["HelpPages"]["help-pages"].getChildren();
   for (var iii=0; iii<helpConfig.size(); iii++)
   {
      helpMap[helpConfig.get(iii).getName()] = helpConfig.get(iii).getValue();
   }
}

/* *********************************************************************************
 *                                                                                 *
 * TOKEN SUBSTITUTION HANDLING                                                     *
 *                                                                                 *
 ***********************************************************************************/
/*
 * Creates a map of the tokens that can be be used for substitutions in links when
 * processing legacy header configuration. The token map will only be generated
 * once, however the getTokenMap() function will be called repeatedly. If more tokens
 * need to be added by an extension then this function should be overridden and
 * the page definition functions called again.
 */
var tokenMap = null;
function getTokenMap()
{
   if (tokenMap == null)
   {
      tokenMap = {
         site: (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         pageid: (page.url.templateArgs.pageid != null) ? page.url.templateArgs.pageid : "",
         userid: encodeURIComponent(user.name)
      }
   }
   return tokenMap;
}

/*
 * This function can be used to replace any tokens that exist in the link. The function
 * is only used for header items of type "link" when generating a header bar from the
 * legacy configuration. The substitution tokens are generated by the getTokenMap()
 * function. Extensions can add more tokens by overriding this function and then
 * calling the functions to build the header definition again.
 */
function substituteTokens(link)
{
   // Perform URI template substitution first (because the resulting URI template may
   // need additional substitutions performed on it). NOTE: It is intentional that the
   // whole link must be token with nothing before or after it so that we get a
   // full template as the link...
   var re = /^{([^}]*)}$/g,
      res = re.exec(link);
   if (res != null && res.length > 1)
   {
      var uriTemplate = uriTemplateMap[res[1]];
      if (uriTemplate != null)
      {
         link = uriTemplate;
      }

      // Substitute any help links...
      var helpLink = helpMap[res[1]];
      if (helpLink != null)
      {
         link = helpLink;
      }
   }

   // Substitute any additional tokens...
   var localTokenMap = getTokenMap();
   for (var key in localTokenMap)
   {
      link = link.replace(new java.lang.String("{" + key + "}"), localTokenMap[key]);
   }
   return link;
}



/* *********************************************************************************
 *                                                                                 *
 * CONSTRUCT HELP LINK                                                             *
 *                                                                                 *
 ***********************************************************************************/
/*
 * This is the default function for getting the help URL for Share.
 */
function getHelpLink() {
   var helpConfig = config.scoped["HelpPages"],
       helpLink = "";
   if (helpConfig != null)
   {
      helpConfig = helpConfig["help-pages"];
      helpLink = (helpConfig != null) ? helpConfig.getChildValue("share-help") : "";
   }
   return helpLink;
}

/* *********************************************************************************
 *                                                                                 *
 * PROCESS LEGACY CONFIGURATION                                                    *
 *                                                                                 *
 ***********************************************************************************/
/*
 * This function checks whether the supplied items satisfies any permissions attached to it. The only
 * permissions that can be checked are whether the current user is a guest (which isn't really supported
 * by Share) or an administrator. Each item can specify "guest" or "admin" as a permission (all other
 * permission entries will result in the function returning "true") and if the current user falls
 * into the specified role then permission will be granted.
 */
function satisfiesPermissions(item)
{
   var permissions = {
      guest: user.isGuest,
      admin: user.isAdmin
   };
   var success = item.getPermission().length() == 0;
   if (!success)
   {
      var declaredPermission = permissions[item.getPermission()];
      success = (declaredPermission != null && declaredPermission);
   }
   return success;
}

/*
 * Setup some default condition information that is provided to allow header items to check for certain things.
 */
var showRepositoryLink = false;
if (config.scoped["RepositoryLibrary"] &&
    config.scoped["RepositoryLibrary"]["visible"])
{
   showRepositoryLink = config.scoped["RepositoryLibrary"]["visible"].getValue();
}

var repoRootNode = "";
if (config.scoped["RepositoryLibrary"] &&
    config.scoped["RepositoryLibrary"]["root-node"])
{
   repoRootNode = config.scoped["RepositoryLibrary"]["root-node"].getValue();
}

var conditionRepositoryRootNode = repoRootNode != "",
    conditionvalueEdition = (context.properties["editionInfo"].edition) ? context.properties["editionInfo"].edition : "UNKNOWN",
    conditionEditionCommunity = (conditionvalueEdition == "UNKNOWN"),
    conditionEditionEnterprise = (conditionvalueEdition == "ENTERPRISE"),
    conditionEditionTeam = (conditionvalueEdition == "TEAM");

/*
 * This function checks whether the supplied item satsifies any conditions attached to it. There are some conditions that
 * can be compared against values that are initialised in this controller (e.g. "conditionEditionCommunity",
 * "conditionEditionEnterprise", etc) or any other value that can be successfully evaluated.
 */
function satisfiesConditions(item)
{
   var success = item.getCondition().length() == 0;
   if (!success)
   {
      var condition = item.getCondition();
      success = eval("" + condition); // The empty String is required to ensure that the condition is evaluated as a String
   }
   return success;
}

/*
 * This function generates a set of widget definitions from legacy configuration.
 */
function generateLegacyItems(items)
{
   var nestingIndex = 0
       widgetDefs = [];
   for (var i=0; i<items.length; i++)
   {
     var item = items[i];
     widgetDef = generateWidgetDef(item, nestingIndex);
     if (widgetDef != null)
     {
        widgetDefs.push(widgetDef)
     }
   }
   return widgetDefs;
}

/*
 * This function generates the header application menu items definition from the legacy configuration.
 */
function generateLegacyAppItems() {
   return generateLegacyItems(config.global.header.appItems.items);
}

/*
 * This function generates the header user menu items definition from the legacy configuration.
 */
function generateLegacyUserItems() {
   return generateLegacyItems(config.global.header.userItems.items);
}

/*
 * This function returns the appropriate menu item widget for the current nesting index.
 * By default this means that the when the menu item is not nested (i.e. the argument
 * is 0) then the widget is a menu bar item and at any other nesting it is a menu item.
 *
 * This function could be overridden by extensions to change the widgets for different
 * nesting indexes but that extension would also need to rebuild the Dojo model.
 */
function getMenuItemWidget(nestingIndex)
{
   var widget = "alfresco/menus/AlfMenuBarItem";
   if (nestingIndex > 0)
   {
      widget = "alfresco/menus/AlfMenuItem";
   }
   return widget;
}

// At the moment there is a common widget for all levels of nesting.
function getMenuGroupWidget(nestingIndex)
{
   var widget = "alfresco/header/AlfMenuBarPopup";
   return widget;
}

/*
 * This function is provided to ensure that when generating the header from legacy configuration
 * that icons are NOT shown on the top-level items of the header bar.
 */
function getMenuItemImage(image, nestingIndex)
{
   var imageURL = "";
   if (nestingIndex > 0)
   {
      imageURL = url.context + "/res/components/images/header/" + image;
   }
   return imageURL;
}

/*
 * Generates the widget definition for the supplied item.
 */
function generateWidgetDef(item, nestingIndex)
{
   // Get the localized form of the label...
   var labelTokens = labelTokens = [ user.name || "", user.firstName || "", user.lastName || "", user.fullName || ""],
       label = msg.get(item.label, labelTokens),
       description = msg.get(item.description, labelTokens);

   var widgetDef = null;
   if (!satisfiesPermissions(item))
   {
      // Does not satisfy permissions. No action.
   }
   else if (!satisfiesConditions(item))
   {
      // Does not satisfy conditions. No action.
   }
   else if (item.type == "container")
   {
      // Handle "container" items...
      widgetDef = {
         id: item.id,
         name: getMenuGroupWidget(nestingIndex),
         config: {
            label: label,
            widgets: []
         }
      };
      var contents = [];
      if (item.containers && item.containers.length)
      {
         for (var i=0; i<item.containers.length; i++)
         {
            // Define the group...
            var containerGroup = item.containers[i];
            var containerGroupDef = {
               id: containerGroup.id,
               name: "alfresco/menus/AlfMenuGroup",
               config: {
                  label: containerGroup.label,
                  widgets: []
               }
            };
            widgetDef.config.widgets.push(containerGroupDef);

            // Add each item to the container group...
            for (var j=0; j<containerGroup.items.length; j++)
            {
               var subItem = containerGroup.items[j];
               var subWidgetDef = generateWidgetDef(subItem, nestingIndex+1);
               if (subWidgetDef != null)
               {
                  containerGroupDef.config.widgets.push(subWidgetDef);
               }
            }
         }
      }
   }
   else if (item.type == "js")
   {
      // Handle "js" items...
      // Any item that is marked as type "js" will be rendered by a JavaScript entry that will have been specified in the <dependencies>
      // element. A special wrapper widget "alfresco/wrapped/HeaderJsWrapper" has been provided for the custom JavaScript object to be
      // instantiated within. The legacy code is somewhat restricted to what can actually be created, for example it must be linked to
      // a button that is placed on the header (and this is one of the things that the HeaderJsWrapper widget provide).
      var itemId = item.id + "-" + args.htmlid + "_" + item.generatedId;
      widgetDef = {
         id: item.id,
         name: "alfresco/wrapped/HeaderJsWrapper",
         config: {
            objectToInstantiate: item.value,
            label: label,
            id: itemId,
            itemId: itemId,
            siteId: page.url.templateArgs.site
         }
      };
   }
   else if (item.type == "link")
   {
      var link = item.value;

      // Perform URI template token substitution...
      // It's possible for items of type link to include tokens to be substituted. These tokens will either
      // be the names of URI templates (defined in "share-config.xml"), help pages (defined in "share-help-config.xml")
      // or possible token that have been passed into the header.
      // By default (in previous versions of Share) the following tokens were provided:
      // - site
      // - pageid
      // - userid
      // These tokens will be honoured here. But this function can be called again by extensions after new tokens
      // have been added
      link = substituteTokens(link);

      // Remove any leading slashes on the link...
      if (link.startsWith("/"))
      {
         link = link.substring(1);
      }

      widgetDef = {
         id: item.id,
         name: getMenuItemWidget(nestingIndex),
         config: {
            label: label,
            iconImage: getMenuItemImage(item.icon, nestingIndex),
            targetUrl: link,
            targetUrlType: "SHARE_PAGE_RELATIVE"
         }
      }
   }
   else if (item.type == "external-link")
   {
      // The only difference between the "link" and the "external-link" types is that the
      // "targetUrlType" attribute is set to FULL_PATH which indicates that the "targetUrl"
      // supplied will be a full URL rather than relative to the Share page context
      var link = item.value;
      link = substituteTokens(link);
      widgetDef = {
         id: item.id,
         name: getMenuItemWidget(nestingIndex),
         config: {
            label: label,
            iconImage: getMenuItemImage(item.icon, nestingIndex),
            targetUrl: link,
            targetUrlType: "FULL_PATH",
            targetUrlLocation: "NEW"
         }
      }
   }
   else if (item.type == "user")
   {
      // The item type "user" maps to the user status information
      widgetDef = getUserStatusWidget();
   }
   else
   {
      // Unknown item type.
   }
   return widgetDef;
}

/* *********************************************************************************
 *                                                                                 *
 * CONSTRUCT LEFT MENU BAR (NEW)                                                   *
 *                                                                                 *
 ***********************************************************************************/
/*
 * This function generates the "app items" to include in the header. In actual fact this just refers to the
 * items that are rendered on the left-hand-side of the screen but for historical reasons (and because of the
 * default values) can be regarded as application items.
 */
function generateAppItems() {
   var appItems = [
      {
         id: "HEADER_HOME",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            id: "HEADER_HOME",
            label: "header.menu.home.label",
            targetUrl: "user/" + encodeURIComponent(user.name) + "/dashboard"
         }
      },
      {
         id: "HEADER_MY_FILES",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            id: "HEADER_MY_FILES",
            label: "header.menu.myfiles.label",
            targetUrl: "context/mine/myfiles"
         }
      },
      {
         id: "HEADER_SHARED_FILES",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            id: "HEADER_SHARED_FILES",
            label: "header.menu.shared.label",
            targetUrl: "context/shared/sharedfiles"
         }
      },
      {
         id: "HEADER_SITES_MENU",
         name: "alfresco/header/AlfSitesMenu",
         config: {
            id: "HEADER_SITES_MENU",
            label: "header.menu.sites.label",
            currentSite: page.url.templateArgs.site,
            currentUser: user.name
         }
      },
      {
         id: "HEADER_TASKS",
         name: "alfresco/header/AlfMenuBarPopup",
         config: {
            id: "HEADER_TASKS",
            label: "header.menu.tasks.label",
            widgets: [
               {   
                  name: "alfresco/menus/AlfMenuGroup",
                  config: {
                     widgets: [
                        {
                           id: "HEADER_MY_TASKS",
                           name: "alfresco/header/AlfMenuItem",
                           config:
                           {
                              id: "HEADER_MY_TASKS",
                              label: "header.menu.mytasks.label",
                              iconClass: "alf-mytasks-icon",
                              targetUrl: "my-tasks#filter=workflows|active"
                           }
                        },
                        {
                           id: "HEADER_MY_WORKFLOWS",
                           name: "alfresco/header/AlfMenuItem",
                           config:
                           {
                              id: "HEADER_MY_WORKFLOWS",
                              label: "header.menu.myworkflows.label",
                              iconClass: "alf-myworkflows-icon",
                              targetUrl: "my-workflows#filter=workflows|active"
                           }
                        }
                     ]
                  }
               }
            ]
         }
      },
      {
         id: "HEADER_PEOPLE",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            id: "HEADER_PEOPLE",
            label: "header.menu.people.label",
            targetUrl: "people-finder"
         }
      }
   ];
   if (user.isAdmin || showRepositoryLink == "true")
   {
      appItems.push({
         id: "HEADER_REPOSITORY",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            id: "HEADER_REPOSITORY",
            label: "header.menu.repository.label",
            targetUrl: "repository"
         }
      });
   }
   if (user.isAdmin)
   {
      appItems.push({
         id: "HEADER_ADMIN_CONSOLE",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            id: "HEADER_ADMIN_CONSOLE",
            label: "header.menu.admin.label",
            targetUrl: "console/admin-console/application"
         }
      });
   }
   return appItems;
}

/* *********************************************************************************
 *                                                                                 *
 * CONSTRUCT RIGHT MENU BAR (NEW)                                                  *
 *                                                                                 *
 ***********************************************************************************/
/*
 * This function generates the "user items" to include in the header. In actual fact this just refers to the
 * items that are rendered on the right-hand-side of the screen but for historical reasons (and because of the
 * default values) can be regarded as user items.
 */
function generateUserItems() {
   return [
      {
         id: "HEADER_USER_MENU_POPUP",
         name: "alfresco/header/AlfMenuBarPopup",
         config: {
            id: "HEADER_USER_MENU_POPUP",
            label: user.fullName,
            widgets: [
               {
                  id: "HEADER_USER_MENU",
                  name: "alfresco/menus/AlfMenuGroup",
                  config: {
                     id: "HEADER_USER_MENU",
                     widgets: getUserMenuWidgets()
                  }
               }
            ]
         }
      }
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * USER MENU WIDGETS                                                               *
 *                                                                                 *
 ***********************************************************************************/
function getUserMenuWidgets()
{
   var userMenuWidgets = [
      getUserStatusWidget(),
      {
         id: "HEADER_USER_MENU_SET_STATUS",
         name: "alfresco/header/AlfMenuItem",
         config:
         {
            id: "HEADER_USER_MENU_SET_STATUS",
            label: "set_status.label",
            iconClass: "alf-user-status-icon",
            publishTopic: "ALF_SET_USER_STATUS"
         }
      },
      {
         id: "HEADER_USER_MENU_PROFILE",
         name: "alfresco/header/AlfMenuItem",
         config:
         {
            id: "HEADER_USER_MENU_PROFILE",
            label: "my_profile.label",
            iconClass: "alf-user-profile-icon",
            targetUrl: "user/" + encodeURIComponent(user.name) + "/profile"
         }
      }
   ];
   if (user.capabilities.isMutable)
   {
      userMenuWidgets.push({
         id: "HEADER_USER_MENU_PASSWORD",
         name: "alfresco/header/AlfMenuItem",
         config:
         {
            id: "HEADER_USER_MENU_CHANGE_PASSWORD",
            label: "change_password.label",
            iconClass: "alf-user-password-icon",
            targetUrl: "user/" + encodeURIComponent(user.name) + "/change-password"
         }
      });
   }
   userMenuWidgets.push({
         id: "HEADER_USER_MENU_HELP",
         name: "alfresco/header/AlfMenuItem",
         config:
         {
            id: "HEADER_USER_MENU_HELP",
            label: "help.label",
            iconClass: "alf-user-help-icon",
            targetUrl: getHelpLink(),
            targetUrlType: "FULL_PATH",
            targetUrlLocation: "NEW"
         }
      });
   if (!context.externalAuthentication)
   {
      userMenuWidgets.push({
         id: "HEADER_USER_MENU_LOGOUT",
         name: "alfresco/header/AlfMenuItem",
         config:
         {
            id: "HEADER_USER_MENU_LOGOUT",
            label: "logout.label",
            iconClass: "alf-user-logout-icon",
            targetUrl: "dologout"
         }
      });
   }
   return userMenuWidgets;
}

/* *********************************************************************************
 *                                                                                 *
 * DECIDE ON NEW OR LEGACY CONFIGURATION                                           *
 *                                                                                 *
 ***********************************************************************************/

/**
 * This builds an object with two attributes "appItems" and "userItems" where each
 * attribute is an array of widgets to include in menu bars on the header. The "appItems"
 * are typically aligned to the left and control navigation and actions relating to the
 * application itself, whereas the "userItems" are aligned to the right and typically
 * (although not always) deal with user specific actions.
 *
 * @returns {object} The menus to include in header
 */
function getHeaderMenus() {

   var headerMenus = {};

   /* By default the header does not support the legacy header bar configuration. However, it
    * can be enabled so that the header is rendered as it was before (and therefore support
    * 3rd party customizations of the header).
    */
   var useLegacyHeaderConfig = false;
   if (config.global.header && config.global.header.legacyMode)
   {
      useLegacyHeaderConfig = (config.global.header.legacyMode == true);
   }
   if (useLegacyHeaderConfig)
   {
      // Generate the header definition using the legacy configuration (the default definition can be found in the
      // "share-config.xml" file, although there may be a 3rd party definition that customizes the default settings).
      headerMenus.appItems = generateLegacyAppItems();
      headerMenus.userItems = generateLegacyUserItems();
   }
   else
   {
      // Generate the new header definition.
      headerMenus.appItems = generateAppItems();
      headerMenus.userItems = generateUserItems();
   }
   return headerMenus;
}


/* *********************************************************************************
 *                                                                                 *
 * ESTABLISH PAGE TITLE                                                            *
 *                                                                                 *
 ***********************************************************************************/
/**
 * Returns info about the current page in the same same format as the page info array in getPages().
 * Note that the title and description are picked from all valid places:
 * User input set in pageMeta property, message bundles AND xml descriptors.
 *
 * @method getMetaPage
 * @return page info or null if the pageId wasn't found.
 */
function getMetaPage()
{
   var pages = this.getPages(true);
   if (pages != null)
   {
      for (var i = 0, il = pages.length; i < il; i++)
      {
         if (pages[i].pageId == page.id)
         {
            return pages[i];
         }
      }
   }
   return null;
}

/**
 * Attempts to work out the title for the current page.
 *
 * @returns {String}
 */
function getPageTitle() {
   var pageTitle = "";
   if (page.titleId == "page.userDashboard.title")
   {
      // Hard-coded handling for user dashboard to support legacy pages. This is required
      // because the user dashboard pages are created from a preset and when migrating it
      // is not possible to update the existing pages created. Therefore we will manually
      // address user dashboard pages.
      pageTitle = msg.get("user.dashboard", [user.fullName]);
   }
   else if (page.url.templateArgs.site != null)
   {
      // Hard-coded handling for site dashboards (see comment from last condition)...
      var siteData = getSiteData();
      if (siteData != null)
      {
         pageTitle = siteData.profile.title;
      }
   }
   else
   {
      //This logic is ported directly from the title.get.html.ftl file!
      var metaPage = getMetaPage();
      if (metaPage)
      {
         if (metaPage.sitePageTitle)
         {
            pageTitle = metaPage.sitePageTitle;
         }
         else if (metaPage.title)
         {
            pageTitle = metaPage.title;
         }
      }
      else if (page)
      {
         pageTitle = page.title;
         if (page.titleId)
         {
            var pageTitleId = msg.get(page.titleId);
            if (pageTitleId != pageTitle)
            {
               pageTitle = pageTitleId;
            }
            if (context.properties["page-titleId"])
            {
               pageTitle = msg.get(context.properties["page-titleId"]);
            }
         }
      }
   }
   return pageTitle;
}


/* *********************************************************************************
 *                                                                                 *
 * CONSTRUCT TITLE BAR                                                             *
 *                                                                                 *
 ***********************************************************************************/
/**
 *
 * TITLE CONFIGURATION MENU
 * This section of code handles the setup of the title configuration widgets. In the original Share interface
 * this items were placed on the right-hand-side of the navigation bar (e.g. "Customize Dashboard" on a User Dashboard)
 * but these have now been moved to a section after the page title.
 *
 * Known conditions...
 * 1) User Dashboard = "Customize Dashboard"
 * 2) Site page (Manager) = Invite, Edit Site Details, Customize Site, Customize Dashboard, Leave Site
 * 3) Site page (Non-Manager) = Leave Site
 * 4) Site page (Admin, not Manager) = become manager + join
 * 5) Site page (Non-Member) = join
 */
function getTitleBarModel() {
   var titleConfig = [];
   if (page.titleId == "page.userDashboard.title")
   {
      // If the page is a user dashboard then make the customize dashboard item an
      // option...
      // NOTE: At the moment this is just a single menu item and not the child of a popup?
      // NOTE: Should this still be shown if the user is not the dashboard owner?
      var userDashboardConfiguration = {
         id: "HEADER_CUSTOMIZE_USER_DASHBOARD",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            id: "HEADER_CUSTOMIZE_USER_DASHBOARD",
            label: "",
            title: msg.get("customize_dashboard.label"),
            iconAltText: msg.get("customize_dashboard.label"),
            iconClass: "alf-configure-icon",
            targetUrl: "customise-user-dashboard"
         }
      };
      titleConfig.push(userDashboardConfiguration);
   }
   else if (page.url.templateArgs.site != null)
   {
      // Create the basic site configuration menu...
      var siteConfig = {
         id: "HEADER_SITE_CONFIGURATION_DROPDOWN",
         name: "alfresco/menus/AlfMenuBarPopup",
         config: {
            id: "HEADER_SITE_CONFIGURATION_DROPDOWN",
            label: "",
            iconClass: "alf-configure-icon",
            iconAltText: msg.get("header.menu.siteConfig.altText"),
            title: msg.get("header.menu.siteConfig.altText"),
            widgets: []
         }
      };

      var siteData = getSiteData();
      if (siteData != null)
      {
         if (user.isAdmin && siteData.userIsMember && !siteData.userIsSiteManager)
         {
            // If the user is an admin, and a site member, but NOT the site manager then
            // add the menu item to let them become a site manager...
            siteConfig.config.widgets.push({
               id: "HEADER_BECOME_SITE_MANAGER",
               name: "alfresco/menus/AlfMenuItem",
               config: {
                  id: "HEADER_BECOME_SITE_MANAGER",
                  label: "become_site_manager.label",
                  iconClass: "alf-cog-icon",
                  publishTopic: "ALF_BECOME_SITE_MANAGER",
                  publishPayload: {
                     site: page.url.templateArgs.site,
                     siteTitle: siteData.profile.title,
                     user: user.name,
                     userFullName: user.fullName
                  }
               }
            })
         }
         if (siteData.userIsSiteManager)
         {
            // If the user is a site manager then let them make custmomizations...
            // Add the invite option...
            titleConfig.push({
               id: "HEADER_SITE_INVITE",
               name: "alfresco/menus/AlfMenuBarItem",
               config: {
                  id: "HEADER_SITE_INVITE",
                  label: "",
                  iconClass: "alf-user-icon",
                  iconAltText: msg.get("header.menu.invite.altText"),
                  title: msg.get("header.menu.invite.altText"),
                  targetUrl: "site/" + page.url.templateArgs.site + "/invite"
               }
            });

            // If on the dashboard then add the customize dashboard option...
            if (page.titleId == "page.siteDashboard.title")
            {
               // Add Customize Dashboard
               siteConfig.config.widgets.push({
                  id: "HEADER_CUSTOMIZE_SITE_DASHBOARD",
                  name: "alfresco/menus/AlfMenuItem",
                  config: {
                     id: "HEADER_CUSTOMIZE_SITE_DASHBOARD",
                     label: "customize_dashboard.label",
                     iconClass: "alf-cog-icon",
                     targetUrl: "site/" + page.url.templateArgs.site + "/customise-site-dashboard"
                  }
               });
            }

            // Add the regular site manager options (edit site, customize site, leave site)
            siteConfig.config.widgets.push(
               {
                  id: "HEADER_EDIT_SITE_DETAILS",
                  name: "alfresco/menus/AlfMenuItem",
                  config: {
                     id: "HEADER_EDIT_SITE_DETAILS",
                     label: "edit_site_details.label",
                     iconClass: "alf-edit-icon",
                     publishTopic: "ALF_EDIT_SITE",
                     publishPayload: {
                        site: page.url.templateArgs.site,
                        siteTitle: siteData.profile.title,
                        user: user.name,
                        userFullName: user.fullName
                     }
                  }
               },
               {
                  id: "HEADER_CUSTOMIZE_SITE",
                  name: "alfresco/menus/AlfMenuItem",
                  config: {
                     id: "HEADER_CUSTOMIZE_SITE",
                     label: "customize_site.label",
                     iconClass: "alf-cog-icon",
                     targetUrl: "site/" + page.url.templateArgs.site + "/customise-site"
                  }
               },
               {
                  id: "HEADER_LEAVE_SITE",
                  name: "alfresco/menus/AlfMenuItem",
                  config: {
                     id: "HEADER_LEAVE_SITE",
                     label: "leave_site.label",
                     iconClass: "alf-leave-icon",
                     publishTopic: "ALF_LEAVE_SITE",
                     publishPayload: {
                        site: page.url.templateArgs.site,
                        siteTitle: siteData.profile.title,
                        user: user.name,
                        userFullName: user.fullName
                     }
                  }
               }
            );
         }
         else if (siteData.userIsMember)
         {
            // If the user is a member of a site then give them the option to leave...
            siteConfig.config.widgets.push({
               id: "HEADER_LEAVE_SITE",
               name: "alfresco/menus/AlfMenuItem",
               config: {
                  id: "HEADER_LEAVE_SITE",
                  label: "leave_site.label",
                  iconClass: "alf-leave-icon",
                  publishTopic: "ALF_LEAVE_SITE",
                  publishPayload: {
                     site: page.url.templateArgs.site,
                     siteTitle: siteData.profile.title,
                     user: user.name,
                     userFullName: user.fullName
                  }
               }
            });
         }
         else if (siteData.profile.visibility != "PRIVATE" || user.isAdmin)
         {
            // If the member is not a member of a site then give them the option to join...
            siteConfig.config.widgets.push({
               id: "HEADER_JOIN_SITE",
               name: "alfresco/menus/AlfMenuItem",
               config: {
                  id: "HEADER_JOIN_SITE",
                  label: "join_site.label",
                  iconClass: "alf-leave-icon",
                  publishTopic: (siteData.profile.visibility == "MODERATED" ? "ALF_REQUEST_SITE_MEMBERSHIP" : "ALF_JOIN_SITE"),
                  publishPayload: {
                     site: page.url.templateArgs.site,
                     siteTitle: siteData.profile.title,
                     user: user.name,
                     userFullName: user.fullName
                  }
               }
            });
         }
      }

      // Add the site configuration to the title options...
      titleConfig.push(siteConfig);

   }
   return titleConfig;
}



// Set up the page defintion using the configuration that will have been generated by either the "generateLegacyHeaderDefinition"
// function or the "generateHeaderDefinition" function.

/* *********************************************************************************
 *                                                                                 *
 * HEADER LOGO HANDLING                                                            *
 *                                                                                 *
 ***********************************************************************************/

/**
 * Attempt to build a URL for retrieving a logo image for the title bar.
 *
 * @returns {string}
 */
function getHeaderLogoUrl() {
   // Generate the source for the logo...
   var logoSrc = context.getSiteConfiguration().getProperty("logo");
   if (logoSrc && logoSrc.length() > 0)
   {
      // Use the site configured logo as the source for the logo image.
      logoSrc = url.context + "/proxy/alfresco/api/node/" + logoSrc.replace("://", "/") + "/content";
   }
   else
   {
      // Use the message bundled configured logo as the logo source.
      // This is theme specific
      var propsLogo = msg.get("header.logo");
      if (propsLogo == "header.logo")
      {
         propsLogo = "app-logo-48.png";
      }
      logoSrc = url.context + "/res/themes/" + theme + "/images/" + propsLogo;
   }
   return logoSrc;
}

/* *********************************************************************************
 *                                                                                 *
 * GET ALL USER PREFERENCES                                                        *
 *                                                                                 *
 ***********************************************************************************/

function getUserPreferences() {
   var userPreferences = {};
   var prefs = eval('(' + preferences.value + ')');
   return prefs
}
var userPreferences = getUserPreferences();

/* *********************************************************************************
 *                                                                                 *
 * ASSEMBLE HEADER                                                                 *
 *                                                                                 *
 ***********************************************************************************/
function getHeaderServices() {
   var services = [
      {
         name: "alfresco/services/PreferenceService",
         config: {
            localPreferences: userPreferences
         }
      },
      "alfresco/services/NavigationService",
      "alfresco/services/UserService",
      "alfresco/services/SiteService"
   ];
   // Only add the logging service when in client-debug mode...
   if (config.global.flags.getChildValue("client-debug") == "true")
   {
      services.push("alfresco/services/LoggingService");
   }
   return services;
}

function getHeaderModel() {

   var headerMenus = getHeaderMenus();

   // When in debug mode (as opposed to production mode) add an additional debug menu item...
   if (config.global.flags.getChildValue("client-debug") == "true")
   {
      var loggingEnabled = false,
          allEnabled = false,
          warnEnabled = false,
          errorEnabled = false;
      if (userPreferences &&
          userPreferences.org &&
          userPreferences.org.alfresco &&
          userPreferences.org.alfresco.share &&
          userPreferences.org.alfresco.share.logging)
      {
         var loggingPreferences = userPreferences.org.alfresco.share.logging;
         loggingEnabled = loggingPreferences.enabled && true;
         allEnabled = (loggingPreferences.all != null) ?  loggingPreferences.all : false;
         warnEnabled = (loggingPreferences.warn != null) ?  loggingPreferences.warn : false;
         errorEnabled = (loggingPreferences.error != null) ?  loggingPreferences.error : false;
      }
      var loggingWidget = {
         name: "alfresco/header/AlfMenuBarPopup",
         config: {
            label: "Debug Menu",
            widgets: [
               {
                  name: "alfresco/menus/AlfMenuGroup",
                  config: {
                     label: "Quick Settings",
                     widgets: [
                        {
                           name: "alfresco/menus/AlfCheckableMenuItem",
                           config: {
                              label: "Debug Logging",
                              value: "enabled",
                              publishTopic: "ALF_LOGGING_STATUS_CHANGE",
                              checked: loggingEnabled
                           }
                        },
                        {
                           name: "alfresco/menus/AlfCheckableMenuItem",
                           config: {
                              label: "Show All Logs",
                              value: "all",
                              publishTopic: "ALF_LOGGING_STATUS_CHANGE",
                              checked: allEnabled
                           }
                        },
                        {
                           name: "alfresco/menus/AlfCheckableMenuItem",
                           config: {
                              label: "Show Warning Messages",
                              value: "warn",
                              publishTopic: "ALF_LOGGING_STATUS_CHANGE",
                              checked: warnEnabled
                           }
                        },
                        {
                           name: "alfresco/menus/AlfCheckableMenuItem",
                           config: {
                              label: "Show Error Messages",
                              value: "error",
                              publishTopic: "ALF_LOGGING_STATUS_CHANGE",
                              checked: errorEnabled
                           }
                        },

                     ]
                  }
               },
               {
                  name: "alfresco/menus/AlfMenuGroup",
                  config: {
                     label: "Logging Configuration",
                     widgets: [
                        {
                           name: "alfresco/menus/AlfMenuItem",
                           config: {
                              label: "Update Logging Preferences",
                              publishTopic: "ALF_UPDATE_LOGGING_PREFERENCES"
                           }
                        }
                     ]
                  }
               }
            ]
         }
      };
      headerMenus.appItems.push(loggingWidget);
   }

   var headerModel = [{
      id: "SHARE_HEADER",
      className: "alf-header",
      name: "alfresco/header/Header",
      config: {
         widgets: [
            {
               id: "HEADER_APP_MENU_BAR",
               name: "alfresco/header/AlfMenuBar",
               align: "left",
               config: {
                  widgets: headerMenus.appItems
               }
            },
            {
               id: "HEADER_USER_MENU_BAR",
               name: "alfresco/header/AlfMenuBar",
               align: "right",
               config: {
                  widgets: headerMenus.userItems
               }
            },
            {
               id: "HEADER_SEARCH",
               name: "alfresco/header/SearchBox",
               align: "right",
               config: {
                  id: "HEADER_SEARCH_BOX",
                  site: page.url.templateArgs.site
               }
            }
         ]
      }
   },
   {
      id: "HEADER_LICENSE_WARNING",
      name: "alfresco/header/LicenseWarning",
      config: {
         usage: getLicenseUsage(),
         userIsAdmin: user.isAdmin
      }
   },
   {
      id: "HEADER_TITLE_BAR",
      name: "alfresco/layout/LeftAndRight",
      config:
      {
         widgets:
         [
            {
               id: "HEADER_LOGO",
               name: "alfresco/logo/Logo",
               align: "left",
               config:
               {
                  logoClasses: "alfresco-logo-only",
                  currentTheme: theme,
                  logoSrc: getHeaderLogoUrl()
               }
            },
            {
               id: "HEADER_TITLE",
               name: "alfresco/header/Title",
               align: "left",
               config: {
                  targetUrl: page.url.templateArgs.site != null ? "site/" + page.url.templateArgs.site + "/dashboard" : null,
                  label: getPageTitle()
               }
            },
            {
               id: "HEADER_TITLE_MENU",
               name: "alfresco/menus/AlfMenuBar",
               align: "left",
               className: "title-menu",
               config: {
                  widgets: getTitleBarModel()
               }
            },
            {
               id: "HEADER_NAVIGATION_MENU_BAR",
               name: "alfresco/header/AlfMenuBar",
               align: "right",
               className: "navigation-menu",
               config: {
                  widgets: getSubNavigationWidgets()
               }
            }
         ]
      }
   }];
   return headerModel;
}
