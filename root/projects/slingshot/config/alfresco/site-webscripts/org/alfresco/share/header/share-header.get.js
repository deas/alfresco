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
   // Call the repository for the site profile
   var json = remote.call("/api/sites/" + page.url.templateArgs.site);
   var profile =
   {
      title: "",
      shortName: "",
      visibility: "PUBLIC"
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
   model.profile = profile;
   model.userIsSiteManager = userIsSiteManager;
   model.userIsMember = userIsMember;
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
var getPages = function(includeUnusedPages)
{
   var siteId = page.url.templateArgs.site,
       pages = null;
   if (siteId)
   {
      var dashboardPageData = sitedata.getPage("site/" + siteId + "/dashboard");
      if (dashboardPageData !== null)
      {
         pages = [];

         // todo: Use a proper json parser since json may consist of user input,
         var sitePages = eval('(' + dashboardPageData.properties.sitePages + ')') || [],
            pageMetadata = eval('(' + dashboardPageData.properties.pageMetadata + ')') || {},
            configPages = config.scoped["SitePages"]["pages"].childrenMap["page"],
            urlMap = {},
            pageId;

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

//Get site specific information...
if (page.url.templateArgs.site)
{
   getSiteData();
   updateRecentSites();
   model.pages = getPages();
}

/*
 * Currently pages are only defined for sites. By default the site dashboard and members
 * pages are always included for sites. 
 */
var navigationWidgets = [];
if (model.pages != null)
{
   // Dashboard always appears in the displayed menu
   // Members always appears in the More menu
   // The current page should always be in the displayed menu
   // IF the current page is normally in the More menu then one other item gets bumped into the More menu?
   // The current page is ALWAYS displayed in the main menu (regardless of whether it is usually in the More menu)?
   
   // Construct an array of all the pages in the site...
   navigationWidgets.push({
      name: "alfresco/menus/AlfMenuBarItem",
      config: {
         label: "Dashboard",
         targetUrl: "site/" + page.url.templateArgs.site + "/dashboard",
         selected: (page.titleId == "page.siteDashboard.title")
      }
   });
   for (var i=0; i<model.pages.length; i++)
   {
      navigationWidgets.push({
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            label: model.pages[i].title,
            pageId: model.pages[i].pageId,
            targetUrl: "site/" + page.url.templateArgs.site + "/" + model.pages[i].pageUrl,
            selected: (model.pages[i].titleId == page.titleId)
         }
      });
   }
   navigationWidgets.push({
      name: "alfresco/menus/AlfMenuBarItem",
      config: {
         label: "Members",
         targetUrl: "site/" + page.url.templateArgs.site + "/site-members",
         selected: (page.titleId == "page.siteMembers.title")
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
         name: "alfresco/menus/AlfMenuBarPopup",
         config: {
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
      id: "UserStatus",
      name: "alfresco/header/UserStatus",
      config: {
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
         userid: user.name
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
         id: "UserHomeLink",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
//            label: "header.menu.home.label",
            iconClass: "alf-home-icon",
            targetUrl: "user/" + user.name + "/dashboard"
         }
      },
//      {
//         id: "UserFilesLink",
//         name: "alfresco/menus/AlfMenuBarItem",
//         config: {
//            label: "header.menu.myfiles.label"
//         }
//      },
//      {
//         id: "SharedFilesLink",
//         name: "alfresco/menus/AlfMenuBarItem",
//         config: {
//            label: "header.menu.shared.label"
//         }
//      },
      {
         id: "SitesMenu",
         name: "alfresco/header/AlfSitesMenu",
         config: {
            label: "header.menu.sites.label",
            currentSite: page.url.templateArgs.site,
            currentUser: user.name
         }
      },
      {
         id: "UserTasksLink",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            label: "header.menu.tasks.label",
            targetUrl: "my-tasks#filter=workflows|active"
         }
      },
      {
         id: "PeopleLink",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            label: "header.menu.people.label",
            targetUrl: "people-finder"
         }
      }
   ];
   if (user.isAdmin)
   {
      appItems.push({
         id: "RepositoryLink",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            label: "header.menu.repository.label",
            targetUrl: "repository"
         }
      });
      appItems.push({
         id: "AdminToolsLink",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            label: "header.menu.admin.label",
            targetUrl: "console/admin-console/"
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
         id: "PopupUserMenu",
         name: "alfresco/header/AlfMenuBarPopup",
         config: {
            label: user.fullName,
            widgets: [
               getUserStatusWidget(),
               {
                  id: "UserProfileLink",
                  name: "alfresco/header/AlfMenuItem",
                  config:
                  {
                     label: "my_profile.label",
                     iconClass: "alf-user-profile-icon",
                     targetUrl: "user/" + user.name + "/profile"
                  }
               },
               {
                  id: "ChangePasswordLink",
                  name: "alfresco/header/AlfMenuItem",
                  config:
                  {
                     label: "change_password.label",
                     iconClass: "alf-user-password-icon",
                     targetUrl: "user/" + user.name + "/change-password"
                  }
               },
               {
                  id: "HelpLink",
                  name: "alfresco/header/AlfMenuItem",
                  config:
                  {
                     label: "help.label",
                     iconClass: "alf-user-help-icon",
                     targetUrl: getHelpLink(),
                     targetUrlType: "FULL_PATH",
                     targetUrlLocation: "NEW"
                  }
               },
               {
                  id: "LogoutLink",
                  name: "alfresco/header/AlfMenuItem",
                  config:
                  {
                     label: "logout.label",
                     iconClass: "alf-user-logout-icon",
                     targetUrl: "dologout"
                  }
               }
            ] 
         }
      }
   ];
}

/* *********************************************************************************
 *                                                                                 *
 * DECIDE ON NEW OR LEGACY CONFIGURATION                                           *
 *                                                                                 *
 ***********************************************************************************/

/* By default the header does not support the legacy header bar configuration. However, it 
 * can be enabled so that the header is rendered as it was before (and therefore support
 * 3rd party customizations of the header). 
 */
var useLegacyHeaderConfig = false;
if (config.global.header && config.global.header.legacyMode)
{
   useLegacyHeaderConfig = (config.global.header.legacyMode == true);
}
var appItems = [],
    userItems = [];

if (useLegacyHeaderConfig)
{
   // Generate the header definition using the legacy configuration (the default definition can be found in the
   // "share-config.xml" file, although there may be a 3rd party definition that customizes the default settings).
   appItems = generateLegacyAppItems();
   userItems = generateLegacyUserItems();
}
else
{
   // Generate the new header definition.
   appItems = generateAppItems();
   userItems = generateUserItems();
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
   pageTitle = model.profile.title;
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

/* *********************************************************************************
 *                                                                                 *
 * CONSTRUCT TITLE BAR                                                             *
 *                                                                                 *
 ***********************************************************************************/
/*
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
var titleConfig = [
];
if (page.titleId == "page.userDashboard.title")
{
   // If the page is a user dashboard then make the customize dashboard item an
   // option...
   // NOTE: At the moment this is just a single menu item and not the child of a popup?
   // NOTE: Should this still be shown if the user is not the dashboard owner?
   var userDashboardConfiguration = {
      id: "UserDashBoardCustomization",
      name: "alfresco/menus/AlfMenuBarItem",
      config: {
         label: "",
         iconClass: "alf-cog-icon",
         targetUrl: "customise-user-dashboard"
      }
   };
   titleConfig.push(userDashboardConfiguration);
}
else if (page.url.templateArgs.site != null)
{
   // Create the basic site configuration menu...
   var siteConfig = {
      id: "SiteConfigurationPopup",
      name: "alfresco/menus/AlfMenuBarPopup",
      config: {
         label: "",
         iconClass: "alf-configure-icon",
         widgets: []
      }
   };
   
   if (user.isAdmin && model.userIsMember && !model.userIsSiteManager)
   {
      // If the user is an admin, and a site member, but NOT the site manager then 
      // add the menu item to let them become a site manager...
      siteConfig.config.widgets.push({
         name: "alfresco/menus/AlfMenuItem",
         config: {
            label: "become_site_manager.label",
            iconClass: "alf-cog-icon",
            publishTopic: "ALF_BECOME_SITE_MANAGER",
            publishPayload: {
               site: page.url.templateArgs.site,
               user: user.name
            }
         }
      })
   }
   if (model.userIsSiteManager)
   {
      // If the user is a site manager then let them make custmomizations...
      // Add the invite option...
      titleConfig.push({
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            label: "",
            iconClass: "alf-user-icon",
            targetUrl: "site/" + page.url.templateArgs.site + "/invite"
         }
      });
      
      // If on the dashboard then add the customize dashboard option...
      if (page.titleId == "page.siteDashboard.title")
      {
         // Add Customize Dashboard
         siteConfig.config.widgets.push({
            name: "alfresco/menus/AlfMenuItem",
            config: {
               label: "customize_dashboard.label",
               iconClass: "alf-cog-icon",
               targetUrl: "site/" + page.url.templateArgs.site + "/customise-site-dashboard"
            }
         });
      }
      
      // Add the regular site manager options (edit site, customize site, leave site)
      siteConfig.config.widgets.push(
         {
            name: "alfresco/menus/AlfMenuItem",
            config: {
               label: "edit_site_details.label",
               iconClass: "alf-edit-icon",
               publishTopic: "ALF_EDIT_SITE",
               publishPayload: {
                  site: page.url.templateArgs.site,
                  user: user.name
               }
            }
         },
         {
            name: "alfresco/menus/AlfMenuItem",
            config: {
               label: "customize_site.label",
               iconClass: "alf-cog-icon",
               targetUrl: "site/" + page.url.templateArgs.site + "/customise-site"
            }
         },
         {
            name: "alfresco/menus/AlfMenuItem",
            config: {
               label: "leave_site.label",
               iconClass: "alf-leave-icon",
               publishTopic: "ALF_LEAVE_SITE",
               publishPayload: {
                  site: page.url.templateArgs.site,
                  user: user.name
               }
            }
         }
      );
   }
   else if (model.userIsMember)
   {
      // If the user is a member of a site then give them the option to leave...
      siteConfig.config.widgets.push({
         name: "alfresco/menus/AlfMenuItem",
         config: {
            label: "leave_site.label",
            iconClass: "alf-leave-icon",
            publishTopic: "ALF_LEAVE_SITE",
            publishPayload: {
               site: page.url.templateArgs.site,
               user: user.name
            }
         }
      });
   }
   else
   {
      // If the member is not a member of a site then give them the option to join...
      siteConfig.config.widgets.push({
         name: "alfresco/menus/AlfMenuItem",
         config: {
            label: "join_site.label",
            iconClass: "alf-leave-icon",
            publishTopic: "ALF_JOIN_SITE",
            publishPayload: {
               site: page.url.templateArgs.site,
               user: user.name
            }
         }
      });
   }
   
   // Add the site configuration to the title options...
   titleConfig.push(siteConfig);
}

/* *********************************************************************************
 *                                                                                 *
 * ASSEMBLE HEADER                                                                 *
 *                                                                                 *
 ***********************************************************************************/
// Set up the page defintion using the configuration that will have been generated by either the "generateLegacyHeaderDefinition"
// function or the "generateHeaderDefinition" function. 

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

model.jsonModel = {
   rootNodeId: "share-header",
   services: [
      "alfresco/services/NavigationService",
      "alfresco/services/UserService",
      "alfresco/services/SiteService"
   ],   
   widgets: [
      {
         id: "MainVerticalStack",
         name: "alfresco/layout/VerticalWidgets",
         config: 
         {
            widgets: 
            [
               {
                  id: "Header",
                  className: "alf-header",
                  name: "alfresco/header/Header",
                  config: {
                     widgets: [
                        {
                           name: "alfresco/header/AlfMenuBar",
                           align: "left",
                           config: {
                              widgets: appItems
                           }
                        },
                        {
                           name: "alfresco/header/AlfMenuBar",
                           align: "right",
                           config: {
                              widgets: userItems
                           }
                        },
                        {
                           name: "alfresco/header/SearchBox",
                           align: "right",
                           config: {
                           }
                        }
                     ],
                  }
               },
               {
                  name: "alfresco/layout/LeftAndRight",
                  config:
                  {
                     widgets:
                     [
                        {
                           id: "CompanyLogo",
                           name: "alfresco/logo/Logo",
                           align: "left",
                           config:
                           {
                              logoClasses: "alfresco-logo-only",
                              currentTheme: theme,
                              logoSrc: logoSrc
                           }
                        },
                        {
                           id: "Title",
                           name: "alfresco/header/Title",
                           align: "left",
                           config: {
                              title: pageTitle
                           }
                        },
                        {
                           id: "TitleMenu",
                           name: "alfresco/menus/AlfMenuBar",
                           align: "left",
                           className: "title-menu",
                           config: {
                              widgets: titleConfig
                           }
                        },
                        {
                           id: "Navigator",
                           name: "alfresco/header/AlfMenuBar",
                           align: "right",
                           className: "navigation-menu",
                           config: {
                              widgets: navigationWidgets
                           }
                        }
                     ]
                  }
               }
            ]
         }
      }
   ]
};