const PREFERENCES_ROOT = "org.alfresco.share.documentList";

function sortByIndex(obj1, obj2)
{
   return (obj1.index > obj2.index) ? 1 : (obj1.index < obj2.index) ? -1 : 0;
}

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
         // Remove multi-select actions if the syncMode element is defined or the syncMode isn't the same as the configured mode
         if (xmlAction.@syncMode.toString() === "" || xmlAction.@syncMode.toString() == syncMode.value)
         {
            actionSet.push(
            {
               id: xmlAction.@id.toString(),
               type: xmlAction.@type.toString(),
               permission: xmlAction.@permission.toString(),
               asset: xmlAction.@asset.toString(),
               href: xmlAction.@href.toString(),
               label: xmlAction.@label.toString(),
               hasAspect: xmlAction.@hasAspect.toString(),
               notAspect: xmlAction.@notAspect.toString()
            });
         }
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
         var attr, content, contentConfig, paramConfigs, paramConfig, permissionsConfigs, permissionConfigs, permissionConfig;
         for (var i = 0; i < contentConfigs.size(); i++)
         {
            contentConfig = contentConfigs.get(i);
            attr = contentConfig.attributes;

            // Create content menu items
            content = {
               type: attr["type"] ? attr["type"].toString() : null,
               icon: attr["icon"] ? attr["icon"].toString() : attr["id"] ? attr["id"].toString() : "generic",
               label: attr["label"] ? attr["label"].toString() : attr["id"] ? "create-content." + attr["id"].toString() : null,
               index: parseInt(attr["index"] || "0"),
               permission: "",
               params: {}
            };

            // Read params
            paramConfigs = contentConfig.getChildren("param");
            for (var pi = 0; pi < paramConfigs.size(); pi++)
            {
               paramConfig = paramConfigs.get(pi);
               if (paramConfig.attributes["name"])
               {
                  content.params[paramConfig.attributes["name"]] = (paramConfig.value || "").toString();
               }
            }

            // Read permissions
            permissionsConfigs = contentConfig.getChildren("permissions");
            if (permissionsConfigs.size() > 0)
            {
               var allow, deny, value, match;
               permissionConfigs = permissionsConfigs.get(0).getChildren("permission");
               for (var pi = 0; pi < permissionConfigs.size(); pi++)
               {
                  permissionConfig = permissionConfigs.get(pi);
                  allow = permissionConfig.attributes["allow"];
                  deny = permissionConfig.attributes["deny"];
                  value = (permissionConfig.value || "").toString();
                  if (value.length() > 0)
                  {
                     match = true;
                     if (allow != null)
                     {
                        match = (allow == "true");
                     }
                     else if (deny != null)
                     {
                        match = (deny == "false");
                     }
                     content.permission += (content.permission.length == 0 ? "" : ",") + (value + ":" + match);
                  }
               }
            }

            if (!content.type)
            {
               /**
                * Support simple/old configs like below by making them of type "pagelink" pointing to the create-content page.
                * <content id="xml" mimetype="text/xml" label="create-content.xml" itemid="cm:content" permission="Write" formid=""/>
                */
               var permission = attr["permission"] ? attr["permission"].toString() : null,
                  mimetype = attr["mimetype"] ? attr["mimetype"].toString() : null,
                  itemid = attr["itemid"] ? attr["itemid"].toString() : null,
                  formid = attr["formid"] ? attr["formid"].toString() : null,
                  url = "create-content?destination={node.nodeRef}";
               if (permission)
               {
                  content.permission += (content.permission.length == 0 ? "" : ",") + permission;
               }
               if (itemid)
               {
                  url += "&itemId=" + itemid;
               }
               if (formid)
               {
                  url += "&formId=" + formid;
               }
               if (mimetype)
               {
                  url += "&mimeType=" + mimetype;
               }

               content.type = "pagelink";
               content.params.page = url;
            }

            createContent.push(content);
         }
      }
   }

   // Google Docs enabled?
   var googleDocsEnabled = false,
      googleDocsConfig = config.scoped["DocumentLibrary"]["google-docs"];

   if (googleDocsConfig !== null)
   {
      // Request the Google Docs status on the Repository
      var result = remote.call("/api/googledocs/status");
      if (result.status == 200 && result != "{}")
      {
         var obj = eval('(' + result + ')');
         try
         {
            googleDocsEnabled = obj.data.enabled;
         }
         catch (e)
         {
         }
      }

      if (googleDocsEnabled)
      {
         var configs = googleDocsConfig.getChildren("creatable-types"),
            creatableConfig,
            configItem,
            creatableType,
            mimetype,
            index,
            url;

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
                     index = parseInt(configItem.attributes["index"] || "0");
                     if (creatableType && mimetype)
                     {
                        url = "create-content?destination={nodeRef}&itemId=cm:content&formId=doclib-create-googledoc&mimeType=" + mimetype;
                        createContent.push(
                        {
                           type: "pagelink",
                           icon: creatableType,
                           label: "google-docs." + creatableType,
                           index: index,
                           permission: "",
                           params:
                           {
                              page: url
                           }
                        });
                     }
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
   model.createContent = createContent.sort(sortByIndex);
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

   model.syncMode = syncMode.value;
}

main();