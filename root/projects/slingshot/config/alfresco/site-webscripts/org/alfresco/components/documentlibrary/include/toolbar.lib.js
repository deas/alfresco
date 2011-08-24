const PREFERENCES_ROOT = "org.alfresco.share.documentList";

function getPreferences()
{
   var preferences = {};
   
   // Request the current user's preferences
   var result = remote.call("/api/people/" + encodeURIComponent(user.name) + "/preferences");
   if (result.status == 200 && result != "{}")
   {
      var prefs = eval('(' + result + ')');
      try
      {
         // Populate the preferences object literal for easy look-up later
         preferences = eval('(prefs.' + PREFERENCES_ROOT + ')');
         if (typeof preferences != "object")
         {
            preferences = {};
         }
      }
      catch (e)
      {
      }
   }
   
   model.preferences = preferences;
}

function getActionSet(myConfig)
{
   // Actions
   var xmlActionSet = myConfig..actionSet.(@id == "default"),
      actionSet = [];
   
   // Found match?
   if (xmlActionSet.@id == "default")
   {
      for each(var xmlAction in xmlActionSet.action)
      {
         actionSet.push(
         {
            id: xmlAction.@id.toString(),
            type: xmlAction.@type.toString(),
            permission: xmlAction.@permission.toString(),
            asset: xmlAction.@asset.toString(),
            href: xmlAction.@href.toString(),
            label: xmlAction.@label.toString()
         });
      }
   }
   
   model.actionSet = actionSet;
}

function getCreateContent()
{
   var createContent = [];

   // Create content config items
   var createContentConfig = config.scoped["DocumentLibrary"]["create-content"];
   if (createContentConfig !== null)
   {
      var contentConfigs = createContentConfig.getChildren("content");
      if (contentConfigs)
      {
         var attr;
         for (var i = 0; i < contentConfigs.size(); i++)
         {
            attr = contentConfigs.get(i).attributes;
            createContent.push(
            {
               mimetype: attr["mimetype"] ? attr["mimetype"].toString() : null,
               icon: attr["icon"] ? attr["icon"].toString() : attr["id"] ? attr["id"].toString() : "generic",
               permission: attr["permission"] ? attr["permission"].toString() : null,
               itemid: attr["itemid"] ? attr["itemid"].toString() : null,
               formid: attr["formid"] ? attr["formid"].toString() : null,
               label: attr["label"] ? attr["label"].toString() : attr["id"] ? "create-content." + attr["id"].toString() : null
            });
         }
      }
   }

   // Google Docs enabled?
   var googleDocsEnabled = false,
      googleDocsConfig = config.scoped["DocumentLibrary"]["google-docs"];

   if (googleDocsConfig !== null)
   {
      googleDocsEnabled = (googleDocsConfig.getChildValue("enabled").toString() == "true");
      
      var configs = googleDocsConfig.getChildren("creatable-types"),
         creatableConfig,
         configItem,
         creatableType,
         mimetype;

      if (configs)
      {
         for (var i = 0; i < configs.size(); i++)
         {
            creatableConfig = configs.get(i).childrenMap["creatable"];
            if (creatableConfig)
            {
               for (var j = 0; j < creatableConfig.size(); j++)
               {
                  configItem = creatableConfig.get(j);
                  // Get type and mimetype from each config item
                  creatableType = configItem.attributes["type"].toString();
                  mimetype = configItem.value.toString();
                  if (creatableType && mimetype)
                  {
                     createContent.push(
                     {
                        mimetype: mimetype,
                        icon: creatableType,
                        permission: "create-google-doc",
                        itemid: "cm:content",
                        formid: "doclib-create-googledoc",
                        label: "google-docs." + creatableType
                     });
                  }
               }
            }
         }
      }
   }

   // Create content by template
   var createContentByTemplateConfig = config.scoped["DocumentLibrary"]["create-content-by-template"];
   createContentByTemplateEnabled = createContentByTemplateConfig !== null ? createContentByTemplateConfig.value.toString() == "true" : false;

   model.googleDocsEnabled = googleDocsEnabled;
   model.createContent = createContent;
   model.createContentByTemplateEnabled = createContentByTemplateEnabled;
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

   model.rootNode = rootNode;
}

/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   var myConfig = new XML(config.script);
   
   getPreferences();
   getActionSet(myConfig);
   getCreateContent(myConfig);
   getRepositoryBrowserRoot();
}

main();