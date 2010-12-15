const PREFERENCES_ROOT = "org.alfresco.share.documentList";

function getPreferences()
{
   var preferences = {};
   
   // Request the current user's preferences
   var result = remote.call("/api/people/" + encodeURIComponent(user.name) + "/preferences?pf=" + PREFERENCES_ROOT);
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

function getCreateContent(myConfig)
{
   // New Content
   var xmlCreateContent = myConfig.createContent,
      createContent = [];
   
   if (xmlCreateContent != null)
   {
      for each (var xmlContent in xmlCreateContent.content)
      {
         createContent.push(
         {
            mimetype: xmlContent.@mimetype.toString(),
            icon: xmlContent.@icon.toString(),
            permission: xmlContent.@permission.toString(),
            itemid: xmlContent.@itemid.toString() || "cm:content",
            formid: xmlContent.@formid.toString(),
            label: xmlContent.@label.toString()
         });
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
   
   model.googleDocsEnabled = googleDocsEnabled;
   model.createContent = createContent;
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
}

main();