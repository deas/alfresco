const PREFERENCES_ROOT = "org.alfresco.share.documentList";

/* Sort the actions by preference order */
function sortByOrder(a, b)
{
   return (a.order - b.order);
}

/* Get user preferences */
function getPreferences()
{
   var preferences = {};

   try
   {
      // Request the current user's preferences
      var result = remote.call("/api/people/" + encodeURIComponent(user.name) + "/preferences?pf=" + PREFERENCES_ROOT);
      if (result.status == 200 && result != "{}")
      {
         var prefs = eval('(' + result + ')');
         // Populate the preferences object literal for easy look-up later
         preferences = eval('(prefs.' + PREFERENCES_ROOT + ')');
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
}

/* Get actions */
function getActionSets(preferences)
{
   var actionSets = {};
   
   if (typeof preferences == "undefined")
   {
      preferences = getPreferences();
   }
   
   try
   {
      // Actions
      var prefActionSet, order, actionSet, actionSetId, actionId, defaultOrder,
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
         actionSets[actionSetId] = actionSet.sort(sortByOrder);
      }
   }
   catch (e)
   {
   }
   
   return actionSets;
}

/* Repository URL */
function getRepositoryUrl()
{
   // Repository Url
   var repositoryUrl = null,
      repositoryConfig = config.scoped["DocumentLibrary"]["repository-url"];

   if (repositoryConfig !== null)
   {
      repositoryUrl = repositoryConfig.value;
   }

   return repositoryUrl;
}

/* Replication URL Mapping */
function getReplicationUrlMappingJSON()
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
}

/* Vti (SharePoint Protocol) server */
function getVtiServerJSON()
{
   var vtiServerJSON = "{}";

   result = remote.call("/api/vti/serverDetails");
   if (result.status == 200 && result != "")
   {
      vtiServerJSON = result;
   }

   return vtiServerJSON;
}

/* Repository Browser root */
function getRepositoryBrowserRoot()
{
   // Repository Library root node
   var rootNode = "alfresco://company/home",
      repoConfig = config.scoped["RepositoryLibrary"]["root-node"];

   if (repoConfig !== null)
   {
      rootNode = repoConfig.value;
   }

   return rootNode;
}

/**
 * Main entrypoint for common doclib functionality
 *
 * @method doclibCommon
 */
function doclibCommon()
{
   var preferences = getPreferences();
   model.preferences = preferences;
   model.actionSets = getActionSets(preferences);
   model.repositoryUrl = getRepositoryUrl();
   model.replicationUrlMappingJSON = getReplicationUrlMappingJSON();
   model.vtiServer = getVtiServerJSON();
   model.rootNode = getRepositoryBrowserRoot();
}
