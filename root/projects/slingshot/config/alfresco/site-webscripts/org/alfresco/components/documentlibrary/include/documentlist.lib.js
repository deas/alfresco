var this_DocumentList = this;

var DocumentList =
{
   PREFERENCES_ROOT: "org.alfresco.share.documentList",

   /* Sort the actions by preference order */
   sortByOrder: function sortByOrder(a, b)
   {
      return (a.order - b.order);
   },

   /* Get user preferences */
   getPreferences: function getPreferences()
   {
      var preferences = {};

      try
      {
         // Request the current user's preferences
         var result = remote.call("/api/people/" + encodeURIComponent(user.name) + "/preferences");
         if (result.status == 200 && result != "{}")
         {
            var prefs = eval('(' + result + ')');
            // Populate the preferences object literal for easy look-up later
            preferences = eval('(prefs.' + DocumentList.PREFERENCES_ROOT + ')');
            if (typeof preferences != "object")
            {
               preferences = {};
            }
         }
      }
      catch (e)
      {
      }

      return preferences;
   },

   /* Get actions */
   getActionSets: function getActionSets(preferences)
   {
      var actionSets = {};

      if (typeof preferences == "undefined")
      {
         preferences = DocumentList.getPreferences();
      }

      try
      {
         // Actions
         var prefActionSet, actionSet, actionSetId, actionId, defaultOrder,
            myConfig = new XML(config.script),
            prefActions = preferences.actions || {};

         for each (var xmlActionSet in myConfig..actionSet)
         {
            actionSet = [];
            actionSetId = xmlActionSet.@id.toString();
            prefActionSet = prefActions[actionSetId] || {};
            defaultOrder = 100;

            for each (var xmlAction in xmlActionSet..action)
            {
               defaultOrder++;
               actionId = xmlAction.@id.toString();

               actionSet.push(
               {
                  order: prefActionSet[actionId] || defaultOrder,
                  id: actionId,
                  type: xmlAction.@type.toString(),
                  permission: xmlAction.@permission.toString(),
                  href: xmlAction.@href.toString(),
                  label: xmlAction.@label.toString()
               });
            }
            actionSets[actionSetId] = actionSet.sort(DocumentList.sortByOrder);
         }
      }
      catch (e)
      {
      }

      return actionSets;
   },

   /* Get configuration value */
   getConfigValue: function getConfigValue(configFamily, configName, defaultValue)
   {
      var value = defaultValue,
         theConfig = config.scoped[configFamily][configName];

      if (theConfig !== null)
      {
         value = theConfig.value;
      }

      return value;
   },

   /* Replication URL Mapping */
   getReplicationUrlMappingJSON: function getReplicationUrlMappingJSON()
   {
      var mapping = {};

      try
      {
         var urlConfig, repositoryId,
            configs = config.scoped["Replication"]["share-urls"].getChildren("share-url");

         if (configs)
         {
            for (var i = 0; i < configs.size(); i++)
            {
               // Get repositoryId and Share URL from each config entry
               urlConfig = configs.get(i);
               repositoryId = urlConfig.attributes["repositoryId"];
               if (repositoryId)
               {
                  mapping[repositoryId] = urlConfig.value.toString();
               }
            }
         }
      }
      catch (e)
      {
      }

      return jsonUtils.toJSONString(mapping);
   },

   /* Vti (SharePoint Protocol) server */
   getVtiServerJSON: function getVtiServerJSON()
   {
      var vtiServerJSON = "{}";

      result = remote.call("/api/vti/serverDetails");
      if (result.status == 200 && result != "")
      {
         vtiServerJSON = result;
      }

      return vtiServerJSON;
   },

   /* Sort Options */
   getSortOptions: function getSortOptions()
   {
      // New Content
      var sortOptions = [],
         sortingConfig = config.scoped["DocumentLibrary"]["sorting"];

      if (sortingConfig !== null)
      {
         var configs = sortingConfig.getChildren(),
            configItem,
            sortLabel,
            sortValue,
            valueTokens;

         if (configs)
         {
            for (var i = 0; i < configs.size(); i++)
            {
               configItem = configs.get(i);
               // Get label and value from each config item
               sortLabel = String(configItem.attributes["label"]);
               sortValue = String(configItem.value);
               if (sortLabel && sortValue)
               {
                  valueTokens = sortValue.split("|");
                  sortOptions.push(
                  {
                     value: valueTokens[0],
                     direction: valueTokens[1],
                     label: sortLabel
                  });
               }
            }
         }
      }

      return sortOptions;
   },

   isUserSiteManager: function isUserSiteManager()
   {
      // Call the repository to see if the user is site manager or not
      var userIsSiteManager = false,
         obj = null,
         site = args.site;

      if (!site)
      {
         site = this_DocumentList.hasOwnProperty('page') ? page.url.templateArgs.site : null;
      }
      if (site)
      {
         json = remote.call("/api/sites/" + site + "/memberships/" + encodeURIComponent(user.name));
         if (json.status == 200)
         {
            obj = eval('(' + json + ')');
         }
         if (obj)
         {
            userIsSiteManager = obj.role == "SiteManager";
         }
      }
      else
      {
         userIsSiteManager = false;
      }
      return userIsSiteManager;
   }
};

/**
 * Main entrypoint for common doclib functionality
 *
 * @method doclibCommon
 */
function doclibCommon()
{
   var preferences = DocumentList.getPreferences();
   model.preferences = preferences;
   model.actionSets = DocumentList.getActionSets(preferences);
   model.repositoryUrl = DocumentList.getConfigValue("DocumentLibrary", "repository-url", null);
   model.replicationUrlMappingJSON = DocumentList.getReplicationUrlMappingJSON();
   model.vtiServer = DocumentList.getVtiServerJSON();
   model.rootNode = DocumentList.getConfigValue("RepositoryLibrary", "root-node", "alfresco://company/home");
   model.sortOptions = DocumentList.getSortOptions();
   model.useTitle = DocumentList.getConfigValue("DocumentLibrary", "use-title", null);
   model.userIsSiteManager = DocumentList.isUserSiteManager();
}
