const PREFERENCES_ROOT = "org.alfresco.share.documentList";

/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   var result, preferences = {};
   
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

   // Actions
   var myConfig = new XML(config.script),
      xmlActionSet = myConfig..actionSet.(@id == "default"),
      actionSet = [];
   
   // Found match?
   if (xmlActionSet.@id == "default")
   {
      for each(var xmlAction in xmlActionSet..action)
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

main();