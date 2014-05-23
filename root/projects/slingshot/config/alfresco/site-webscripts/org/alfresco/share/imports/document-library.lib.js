<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-footer.lib.js">


/* *********************************************************************************
 *                                                                                 *
 * GET ALL USER PREFERENCES                                                        *
 *                                                                                 *
 ***********************************************************************************/

function getUserPreferences() {
   var userPreferences = {};
   var prefs = jsonUtils.toObject(preferences.value);
   return prefs
}


/* *********************************************************************************
 *                                                                                 *
 * DEFAULTS                                                                        *
 *                                                                                 *
 ***********************************************************************************/
var userPreferences = getUserPreferences();
var docLibPreferences = eval('try{(userPreferences.org.alfresco.share.documentList)}catch(e){}');
if (typeof docLibPreferences != "object")
{
   docLibPreferences = {};
}
var viewRendererName =  (docLibPreferences.viewRendererName ? docLibPreferences.viewRendererName : "detailed");
var sortField = (docLibPreferences.sortField ? docLibPreferences.sortField : "cm:name");
var sortAscending = ((docLibPreferences.sortAscending != null) ? docLibPreferences.sortAscending : true);
var showFolders = ((docLibPreferences.showFolders != null) ? docLibPreferences.showFolders : true);
var hideBreadcrumbTrail = ((docLibPreferences.hideNavBar != null) ? docLibPreferences.hideNavBar : true); // Using "hideNavBar" for breadcrumb trail as it previously existed
var showSidebar = ((docLibPreferences.showSidebar != null) ? docLibPreferences.showSidebar : true); // "showSidebar" is a new 4.2E preference

/* *********************************************************************************
 *                                                                                 *
 * QUICK SHARE LINK                                                                *
 *                                                                                 *
 ***********************************************************************************/

var quickShareLink = "",
    quickShareConfig = config.scoped["Social"]["quickshare"];
if (quickShareConfig)
{
   var configValue = quickShareConfig.getChildValue("url");
   if (configValue != null)
   {
      quickShareLink = configValue.replace("{context}", url.context);
   }
}

/* *********************************************************************************
 *                                                                                 *
 * SOCIAL LINKS                                                                    *
 *                                                                                 *
 ***********************************************************************************/


var socialLinks = [],
    socialLinksConfig = config.scoped["Social"]["linkshare"];
if (socialLinksConfig !== null)
{
   var configs = socialLinksConfig.getChildren(),
       configItem,
       sortLabel,
       sortValue,
       valueTokens;

   if (configs)
   {
      for (var i = 0; i < configs.size(); i++)
      {
         configItem = configs.get(i);
         sortLabel = String(configItem.attributes["label"]);
         sortValue = String(configItem.value);
         socialLinks[i] = {
            id: configItem.attributes["id"],
            type: configItem.attributes["type"],
            index: configItem.attributes["index"],
            params: []
         };
         var params = configItem.getChildren();
         if (params)
         {
            for (var j = 0; j < params.size(); j++)
            {
               var paramConfig = params.get(j);
               var param = {};
               param[paramConfig.attributes["name"]] = paramConfig.value
               socialLinks[i].params[j] = param;
            }
         }
      }
   }
}


/* *********************************************************************************
 *                                                                                 *
 * CUSTOM ACTION HANDLER DEPENDENCIES                                              *
 *                                                                                 *
 ***********************************************************************************/

// It is currently necessary to get all of the configured custom action handling JavaScript
// and CSS dependencies. These are then passed into the "alfresco/wrapped/DocumentList" widget 
// to request so that all custom actions can be handled appropriately. This isn't the ideal
// way in which we could handle custom actions, but it must be done to support existing
// customizations.
var getDependencies = function(configFamily)
{
   var fnGetConfig = function fnGetConfig(scopedRoot, dependencyType)
   {
      var dependencies = [], src, configs, dependencyConfig;
      try
      {
         configs = scopedRoot.getChildren(dependencyType);
         if (configs)
         {
            for (var i = 0; i < configs.size(); i++)
            {
               dependencyConfig = configs.get(i);
               if (dependencyConfig)
               {
                  src = dependencyConfig.attributes["src"];
                  if (src)
                  {
                     dependencies.push(src.toString());
                  }
               }
            }
         }
      }
      catch (e)
      {
      }
      return dependencies;
   }
   var scopedRoot = config.scoped[configFamily]["dependencies"];
   return (
   {
      css: fnGetConfig(scopedRoot, "css"),
      js: fnGetConfig(scopedRoot, "js")
   });
}

/* The resourceUtils helper allows us to build aggregated resources for the additional JS and CSS
 * content that needs to be loaded onto the page. The additional resources are requested and 
 * an MD5 checksum is returned that can then be used to reference the generated resource. The checksums
 * for the resources will be passed to the DocumentList widget to ensure that they are loaded onto
 * the page in the correct location (i.e. AFTER the non-AMD action handlers have been loaded).
 */
var customActionHandlers = getDependencies("DocLibCustom"),
    customAggregatedJsResource = resourceUtils.getAggregratedJsResources(customActionHandlers.js),
    customAggregatedCssResource = resourceUtils.getAggregratedCssResources(customActionHandlers.css);

/* *********************************************************************************
 *                                                                                 *
 * SORT FILE OPTIONS                                                               *
 *                                                                                 *
 ***********************************************************************************/
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
               name: "alfresco/menus/AlfCheckableMenuItem",
               config: {
                  label: msg.get(sortLabel),
                  value: valueTokens[0],
                  group: "DOCUMENT_LIBRARY_SORT_FIELD",
                  publishTopic: "ALF_DOCLIST_SORT_FIELD_SELECTION",
                  checked: (sortField == valueTokens[0]),
                  publishPayload: {
                     label: msg.get(sortLabel),
                     direction: valueTokens[1] || null
                  }
               }
            });
         }
      }
   }
}

/* *********************************************************************************
 *                                                                                 *
 * SELECTED ITEMS ACTION OPTIONS                                                   *
 *                                                                                 *
 ***********************************************************************************/
// Actions
var getMultiActionImage = function(attr) {
   var imageUrl = url.context + "/res/components/documentlibrary/actions/";
   if (attr["icon"])
   {
      imageUrl += attr["icon"];
   }
   else if (attr["id"])
   {
      imageUrl += attr["id"];
   }
   else
   {
      imageUrl += generic;
   }
   imageUrl += "-16.png";
   return imageUrl;
};

var multiSelectConfig = config.scoped["DocumentLibrary"]["multi-select"],
    multiSelectActions = multiSelectConfig.getChildren("action"),
    actionSet = [];

var multiSelectAction;
for (var i = 0; i < multiSelectActions.size(); i++)
{
   multiSelectAction = multiSelectActions.get(i);
   attr = multiSelectAction.attributes;

   if(!attr["syncMode"] || attr["syncMode"].toString() == syncMode.value)
   {
      var getActionItemImage = function(attr) {
         if (attr["icon"])
         {
            return url.context + "/res/components/documentlibrary/actions/" + attr["icon"] + "-16.png";
         }
         else
         {
            return url.context + "/res/components/documentlibrary/actions/" + attr["type"] + "-16.png";
         }
      };
      
      // Multi-Select Actions
      // Note that we're using an AlfDocumentActionMenuItem widget here...
      // This particular widget extends the AlfFilteringMenuItem (which in turn
      // extends AlfMenuItem) to subscribe to publications on the "ALF_FILTER_SELECTED_FILE_ACTIONS"
      // topic (although that could be overridden by setting a 'filterTopic' attribute
      // in the widget config). The ActionService on the page will publish on this topic
      // each time the selected documents changes and the payload will contain all of the
      // permissions and aspect data for the selected documents. The AlfDocumentActionMenuItem
      // widget will compare that data against it's configuration and show/hide itself appropriately.
      var action = {
         name: "alfresco/documentlibrary/AlfDocumentActionMenuItem",
         config: {
            id: attr["id"] ? attr["id"].toString() : "",
            label: attr["label"] ? attr["label"].toString() : "",
            iconImage: getMultiActionImage(attr),
            type: attr["type"] ? attr["type"].toString() : "",
            permission: attr["permission"] ? attr["permission"].toString() : "",
            asset: attr["asset"] ? attr["asset"].toString() : "",
            href: attr["href"] ? attr["href"].toString() : "",
            hasAspect: attr["hasAspect"] ? attr["hasAspect"].toString() : "",
            notAspect: attr["notAspect"] ? attr["notAspect"].toString() : "",
            publishTopic: "ALF_MULTIPLE_DOCUMENT_ACTION_REQUEST",
            publishPayload: {
               action: attr["id"] ? attr["id"].toString() : ""
            }
         }
      };
      actionSet.push(action);
   }
}

/* *********************************************************************************
 *                                                                                 *
 * CREATE CONTENT OPTIONS                                                          *
 *                                                                                 *
 ***********************************************************************************/

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

         var getCreateContentImage = function(attr) {
            var imageUrl = url.context + "/res/components/images/filetypes/";
            if (attr["icon"])
            {
               imageUrl += attr["icon"];
            }
            else if (attr["id"])
            {
               imageUrl += attr["id"];
            }
            else
            {
               imageUrl += generic;
            }
            imageUrl += "-file-16.png";
            return imageUrl;
         };
         
         var content = {
            name: "alfresco/documentlibrary/AlfCreateContentMenuItem",
            config: {
               iconImage: getCreateContentImage(attr),
               label: attr["label"] ? attr["label"].toString() : attr["id"] ? "create-content." + attr["id"].toString() : null,
               index: parseInt(attr["index"] || "0"),
               permission: "CreateChildren",
               publishTopic: "ALF_CREATE_CONTENT",
               publishPayload: {
                  action: attr["id"] ? attr["id"].toString() : "",
                  type: attr["type"] ? attr["type"].toString() : null,
                  params: {},
               }
            }
         };

         // Read params
         paramConfigs = contentConfig.getChildren("param");
         for (var pi = 0; pi < paramConfigs.size(); pi++)
         {
            paramConfig = paramConfigs.get(pi);
            if (paramConfig.attributes["name"])
            {
               content.config.publishPayload.params[paramConfig.attributes["name"]] = (paramConfig.value || "").toString();
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
                  content.config.permission += (content.config.permission.length == 0 ? "" : ",") + (value + ":" + match);
               }
            }
         }

         if (!content.config.publishPayload.type)
         {
            /**
             * Support simple/old configs like below by making them of type "pagelink" pointing to the create-content page.
             * <content id="xml" mimetype="text/xml" label="create-content.xml" itemid="cm:content" permission="Write" formid=""/>
             */
            var permission = attr["permission"] ? attr["permission"].toString() : null,
                mimetype = attr["mimetype"] ? attr["mimetype"].toString() : null,
                itemid = attr["itemid"] ? attr["itemid"].toString() : null,
                formid = attr["formid"] ? attr["formid"].toString() : null,
                _url = "create-content?destination={node.nodeRef}";
            if (permission)
            {
               content.config.permission += (content.config.permission.length == 0 ? "" : ",") + permission;
            }
            if (itemid)
            {
               _url += "&itemId=" + itemid;
            }
            if (formid)
            {
               _url += "&formId=" + formid;
            }
            if (mimetype)
            {
               _url += "&mimeType=" + mimetype;
            }

            content.config.publishPayload.type = "pagelink";
            content.config.publishPayload.params.page = _url;
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
   googleDocsEnabled = googleDocsStatus.enabled;
   if (googleDocsEnabled)
   {
      var configs = googleDocsConfig.getChildren("creatable-types"),
         creatableConfig,
         configItem,
         creatableType,
         mimetype,
         index,
         _url;

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
                     _url = "create-content?destination={nodeRef}&itemId=cm:content&formId=doclib-create-googledoc&mimeType=" + mimetype;
                     var content = {
                        name: "alfresco/documentlibrary/AlfCreateContentMenuItem",
                        config: {
                           icon: creatableType,
                           label: "google-docs." + creatableType,
                           index: index,
                           permission: "CreateChildren",
                           publishTopic: "ALF_CREATE_CONTENT",
                           publishPayload: {
                              action: attr["id"] ? attr["id"].toString() : "",
                              type: "pagelink",
                              params:
                              {
                                 page: _url
                              }
                           }
                        }
                     };
                     createContent.push(content);
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

if (createContentByTemplateEnabled)
{
   createContent.push({
      name: "alfresco/documentlibrary/AlfCreateTemplateContentMenu"
   });
}

// Add the option to create folders...
createContent.splice(0, 0, {
   name: "alfresco/menus/AlfFormDialogMenuItem",
   config: {
      label: "Create Folder",
      iconClass: "alf-showfolders-icon",
      dialogTitle: "Create Folder",
      formSubmissionTopic: "ALF_CREATE_CONTENT_REQUEST",
      widgets: [
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "type",
               value: "cm:folder",
               visibilityConfig: {
                  initialValue: false
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               label: "Name",
               description: "The name to give the new folder",
               name: "prop_cm_name",
               value: "",
               requirementConfig: {
                  initialValue: true
               }
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               label: "Title",
               description: "The title to give to the new folder",
               name: "prop_cm_title",
               value: ""
            }
         },
         {
            name: "alfresco/forms/controls/DojoTextarea",
            config: {
               label: "Description",
               description: "A description of the folder",
               name: "prop_cm_description",
               value: ""
            }
         }
      ]
   }
});

// createContent.push({
//    name: "alfresco/menus/AlfMenuItem",
//    config: {
//       label: "Create JSON Content",
//       iconClass: "alf-textdoc-icon",
//       publishTopic: "ALF_SHOW_DIALOG_REQUEST",
//       publishPayload: {
//          id: "ALF_CREATE_JSONCONTENT"
//       }
//    }
// })

function addCreateContentMenuItem(menuLabel, menuIcon, dialogTitle, editMode, mimeType) {
   return {
      name: "alfresco/menus/AlfFormDialogMenuItem",
      config: {
         label: menuLabel,
         iconClass: menuIcon,
         dialogTitle: dialogTitle,
         formSubmissionTopic: "ALF_CREATE_CONTENT_REQUEST",
         widgets: [
            {
               name: "alfresco/forms/controls/DojoValidationTextBox",
               config: {
                  name: "type",
                  value: "cm:content",
                  visibilityConfig: {
                     initialValue: false
                  }
               }
            },
            {
               name: "alfresco/forms/controls/DojoValidationTextBox",
               config: {
                  name: "prop_mimetype",
                  value: mimeType,
                  visibilityConfig: {
                     initialValue: false
                  }
               }
            },
            {
               name: "alfresco/forms/controls/DojoValidationTextBox",
               config: {
                  name: "prop_app_editInline",
                  value: true,
                  visibilityConfig: {
                     initialValue: false
                  }
               }
            },
            {
               name: "alfresco/forms/controls/DojoValidationTextBox",
               config: {
                  label: "Name",
                  description: "The name to give the new document",
                  name: "prop_cm_name",
                  value: "",
                  requirementConfig: {
                     initialValue: true
                  }
               }
            },
            {
               name: "alfresco/forms/controls/DojoValidationTextBox",
               config: {
                  label: "Title",
                  description: "The title to give to the new document",
                  name: "prop_cm_title",
                  value: ""
               }
            },
            {
               name: "alfresco/forms/controls/DojoTextarea",
               config: {
                  label: "Description",
                  description: "A description of the folder",
                  name: "prop_cm_description",
                  value: ""
               }
            },
            {
               name: "alfresco/forms/controls/AceEditor",
               config: {
                  editMode: editMode,
                  label: "Content",
                  description: "The HTML content for the document",
                  name: "prop_cm_content",
                  value: ""
               }
            }
         ]
      }
   };
}

createContent.push(addCreateContentMenuItem("Plain Text", "alf-textdoc-icon", "Create Text Content", "text", "text/plain"));
createContent.push(addCreateContentMenuItem("HTML", "alf-htmldoc-icon", "Create HTML Content", "html", "text/plain"));
createContent.push(addCreateContentMenuItem("XML", "alf-xmldoc-icon", "Create XML Content", "xml", "text/xml"));
createContent.push(addCreateContentMenuItem("JavaScript", "alf-textdoc-icon", "Create JavaScript Content", "javascript", "text/javascript"));



/* *********************************************************************************
 *                                                                                 *
 * TREE OPTIONS                                                                    *
 *                                                                                 *
 ***********************************************************************************/

/**
 * 
 * @return {object} An object containing the configured options for trees.
 */
function getTreeOptions() {
   var treeOptions = {
      evaluateChildFolders: "true",
      maximumFolderCount: "-1"
   };
   var docLibConfig = config.scoped["RepositoryLibrary"];
   if (docLibConfig != null)
   {
     var tree = docLibConfig["tree"];
     if (tree != null)
     {
        var tmp = tree.getChildValue("evaluate-child-folders");
        treeOptions.evaluateChildFolders = tmp != null ? tmp : "true";
        tmp = tree.getChildValue("maximum-folder-count");
        treeOptions.maximumFolderCount = tmp != null ? tmp : "-1";
     }
   }
   return treeOptions;
}

/**
 * Helper function to retrieve configuration values.
 * 
 * @method getConfigValue
 * @param {string} configFamily
 * @param {string} configName
 * @param {string} defaultValue
 */
function getConfigValue(configFamily, configName, defaultValue)
{
   var value = defaultValue,
       theConfig = config.scoped[configFamily][configName];
   if (theConfig !== null)
   {
      value = theConfig.value;
   }
   return value;
}

/**
 * Replication URL Mapping
 */
function getReplicationUrlMappingJSON()
{
   var mapping = {};
   try
   {
      var urlConfig, 
          repositoryId,
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

var syncMode = syncMode.getValue(),
    useTitle = getConfigValue("DocumentLibrary", "use-title", null);

var userIsSiteManager = false,
    siteData = getSiteData();
if (siteData != null)
{
   userIsSiteManager = siteData.userIsSiteManager;
}

/* *********************************************************************************
 *                                                                                 *
 * REPOSITORY URL                                                                  *
 *                                                                                 *
 ***********************************************************************************/
getRepositoryUrl: function getRepositoryUrl()
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

/* *********************************************************************************
 *                                                                                 *
 * PAGE CONSTRUCTION                                                               *
 *                                                                                 *
 ***********************************************************************************/

/**
 * Returns a JSON array of the configuration for all the services required by the document library
 */
function getDocumentLibraryServices(siteId, containerId, rootNode) {
   return [
   "alfresco/services/ContentService",
   "alfresco/services/DocumentService",
   "alfresco/dialogs/AlfDialogService",
   {
      name: "alfresco/services/ActionService",
      config: {
         customAggregatedJsResource: customAggregatedJsResource,
         customAggregatedCssResource: customAggregatedCssResource,
         siteId: siteId,
         containerId: containerId, 
         rootNode: rootNode,
         repositoryUrl: getRepositoryUrl(),
         replicationUrlMapping: getReplicationUrlMappingJSON()
      }
   },
   {
      name: "alfresco/services/TagService",
      config: {
         siteId: siteId,
         containerId: containerId, 
         rootNode: rootNode
      }
   },
   "alfresco/services/RatingsService",
   {
      name: "alfresco/services/QuickShareService",
      config: {
         quickShareLink: quickShareLink,
         socialLinks: socialLinks
      }
   }]
}

/**
 * Builds the JSON model for rendering a DocumentLibrary. 
 * 
 * @param {string} siteId The id of the site to render the document library for (if applicable)
 * @param {string} containerId The id of the container to render (if applicable - sites only)
 * @param {string} rootNode The node that is the root of the DocumentLibrary to render
 * @returns {object} An object containing the JSON model for a DocumentLibrary
 */
function getDocumentLibraryModel(siteId, containerId, rootNode) {
   
   var treeOptions = getTreeOptions();
   
   
   var docLibModel = 
   {
      id: "DOCLIB_SIDEBAR",
      name: "alfresco/layout/AlfSideBarContainer",
      className: "undo-share-margin",
      config: {
         showSidebar: showSidebar,
         customResizeTopics: ["ALF_DOCLIST_READY","ALF_RESIZE_SIDEBAR"],
         footerHeight: 50,
         widgets: [
            {
               id: "DOCLIB_SIDEBAR_BAR",
               align: "sidebar",
               name: "alfresco/layout/VerticalWidgets",
               config: {
                  widgets: [
                     {
                        id: "DOCLIB_FILTERS",
                        name: "alfresco/documentlibrary/AlfDocumentFilters",
                        config: {
                           label: "filter.label.documents",
                           widgets: [
                              {
                                 name: "alfresco/documentlibrary/AlfDocumentFilter",
                                 config: {
                                    label: "link.all",
                                    filter: "all",
                                    description: "link.all.description"
                                 }
                              },
                              {
                                 name: "alfresco/documentlibrary/AlfDocumentFilter",
                                 config: {
                                    label: "link.editingMe",
                                    filter: "editingMe",
                                    description: "link.editingMe.description"
                                 }
                              },
                              {
                                 name: "alfresco/documentlibrary/AlfDocumentFilter",
                                 config: {
                                    label: "link.editingOthers",
                                    filter: "editingOthers",
                                    description: "link.editingOthers.description"
                                 }
                              },
                              {
                                 name: "alfresco/documentlibrary/AlfDocumentFilter",
                                 config: {
                                    label: "link.recentlyModified",
                                    filter: "recentlyModified",
                                    description: "link.recentlyModified.description"
                                 }
                              },
                              {
                                 name: "alfresco/documentlibrary/AlfDocumentFilter",
                                 config: {
                                    label: "link.recentlyAdded",
                                    filter: "recentlyAdded",
                                    description: "link.recentlyAdded.description"
                                 }
                              },
                              {
                                 name: "alfresco/documentlibrary/AlfDocumentFilter",
                                 config: {
                                    label: "link.favourites",
                                    filter: "favourites",
                                    description: "link.favourites.description"
                                 }
                              }
                           ]
                        }
                     },
                     {
                        id: "DOCLIB_TREE",
                        name: "alfresco/navigation/PathTree",
                        config: {
                           label: "twister.library.label",
                           siteId: siteId,
                           containerId: containerId,
                           rootNode: rootNode
                        }
                     },
                     {
                        id: "DOCLIB_TAGS",
                        name: "alfresco/documentlibrary/AlfTagFilters",
                        config: {
                           label: "filter.label.tags"
                        }
                     },
                     {
                        id: "DOCLIB_CATEGORIES",
                        name: "alfresco/navigation/CategoryTree",
                        config: {
                           label: "twister.categories.label"
                        }
                     }
                  ]
               }
            },
            {
               id: "DOCLIB_SIDEBAR_MAIN",
               name: "alfresco/layout/VerticalWidgets",
               config: 
               {
                  widgets: 
                  [
                     {
                        id: "DOCLIB_TOOLBAR",
                        name: "alfresco/documentlibrary/AlfToolbar",
                        config: {
                           id: "DOCLIB_TOOLBAR",
                           widgets: [
                              {
                                 id: "DOCLIB_TOOLBAR_LEFT_MENU",
                                 name: "alfresco/menus/AlfMenuBar",
                                 align: "left",
                                 config: {
                                    widgets: [
                                       {
                                          id: "DOCLIB_SELECT_ITEMS_MENU",
                                          name: "alfresco/documentlibrary/AlfSelectDocumentListItems"
                                       },
                                       {
                                          id: "DOCLIB_CREATE_CONTENT_MENU",
                                          name: "alfresco/documentlibrary/AlfCreateContentMenuBarPopup",
                                          config: {
                                             widgets: [
                                                {
                                                   id: "DOCLIB_CREATE_CONTENT_MENU_GROUP1",
                                                   name: "alfresco/menus/AlfMenuGroup",
                                                   config: {
                                                      widgets: createContent
                                                   }
                                                }
                                             ]
                                          }
                                       },
                                       {
                                          id: "DOCLIB_UPLOAD_BUTTON",
                                          name: "alfresco/documentlibrary/AlfCreateContentMenuBarItem",
                                          config: {
                                             label: msg.get("upload.label"),
                                             iconClass: "alf-upload-icon",
                                             publishTopic: "ALF_SHOW_UPLOADER"
                                          }
                                       },
                                       {
                                          id: "DOCLIB_SYNC_TO_CLOUD_BUTTON",
                                          name: "alfresco/documentlibrary/AlfCloudSyncFilteredMenuBarItem",
                                          config: {
                                             label: "Sync to Cloud",
                                             publishTopic: "ALF_SYNC_CURRENT_LOCATION"
                                          }
                                       },
                                       {
                                          id: "DOCLIB_UNSYNC_FROM_CLOUD_BUTTON",
                                          name: "alfresco/documentlibrary/AlfCloudSyncFilteredMenuBarItem",
                                          config: {
                                             label: "Unsync from Cloud",
                                             invertFilter: true,
                                             publishTopic: "ALF_UNSYNC_CURRENT_LOCATION"
                                          }
                                       },
                                       {
                                          id: "DOCLIB_SELECTED_ITEMS_MENU",
                                          name: "alfresco/documentlibrary/AlfSelectedItemsMenuBarPopup",
                                          config: {
                                             label: msg.get("selected-items.label"),
                                             widgets: [
                                                {
                                                   id: "DOCLIB_SELECTED_ITEMS_MENU_GROUP1",
                                                   name: "alfresco/menus/AlfMenuGroup",
                                                   config: {
                                                      widgets: actionSet
                                                   }
                                                }
                                             ]
                                          }
                                       }
                                    ]
                                 }
                              },
                              {
                                 id: "DOCLIB_PAGINATION_MENU",
                                 name: "alfresco/documentlibrary/AlfDocumentListPaginator",
                                 align: "left"
                              },
                              {
                                 id: "DOCLIB_TOOLBAR_RIGHT_MENU",
                                 name: "alfresco/menus/AlfMenuBar",
                                 align: "right",
                                 config: {
                                    widgets: [
                                       {
                                          id: "DOCLIB_SORT_ORDER_TOGGLE",
                                          name: "alfresco/menus/AlfMenuBarToggle",
                                          config: {
                                             checked: sortAscending,
                                             onConfig: {
                                                iconClass: "alf-sort-ascending-icon",
                                                publishTopic: "ALF_DOCLIST_SORT",
                                                publishPayload: {
                                                   direction: "ascending"
                                                }
                                             },
                                             offConfig: {
                                                iconClass: "alf-sort-descending-icon",
                                                publishTopic: "ALF_DOCLIST_SORT",
                                                publishPayload: {
                                                   direction: "descending"
                                                }
                                             }
                                          }
                                       },
                                       {
                                          id: "DOCLIB_SORT_FIELD_SELECT",
                                          name: "alfresco/menus/AlfMenuBarSelect",
                                          config: {
                                             selectionTopic: "ALF_DOCLIST_SORT_FIELD_SELECTION",
                                             widgets: [
                                                {
                                                   id: "DOCLIB_SORT_FIELD_SELECT_GROUP",
                                                   name: "alfresco/menus/AlfMenuGroup",
                                                   config: {
                                                      widgets: sortOptions
                                                   }
                                                }
                                             ]
                                          }
                                       },
                                       {
                                          id: "DOCLIB_CONFIG_MENU",
                                          name: "alfresco/menus/AlfMenuBarPopup",
                                          config: {
                                             iconClass: "alf-configure-icon",
                                             widgets: [
                                                {
                                                   id: "DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP",
                                                   name: "alfresco/documentlibrary/AlfViewSelectionGroup"
                                                },
                                                {
                                                   id: "DOCLIB_CONFIG_MENU_OPTIONS_GROUP",
                                                   name: "alfresco/menus/AlfMenuGroup",
                                                   config: {
                                                      label: "Options",
                                                      widgets: [
                                                         {
                                                            id: "DOCLIB_SHOW_FOLDERS_OPTION",
                                                            name: "alfresco/menus/AlfCheckableMenuItem",
                                                            config: {
                                                               label: msg.get("show-folders.label"),
                                                               iconClass: "alf-showfolders-icon",
                                                               checked: showFolders,
                                                               publishTopic: "ALF_DOCLIST_SHOW_FOLDERS"
                                                            }
                                                         },
                                                         {
                                                            id: "DOCLIB_SHOW_PATH_OPTION",
                                                            name: "alfresco/menus/AlfCheckableMenuItem",
                                                            config: {
                                                               label: msg.get("show-path.label"),
                                                               checked: !hideBreadcrumbTrail,
                                                               iconClass: "alf-showpath-icon",
                                                               publishTopic: "ALF_DOCLIST_SHOW_PATH"
                                                            }
                                                         },
                                                         {
                                                            id: "DOCLIB_SHOW_SIDEBAR_OPTION",
                                                            name: "alfresco/menus/AlfCheckableMenuItem",
                                                            config: {
                                                               label: msg.get("show-sidebar.label"),
                                                               iconClass: "alf-showsidebar-icon",
                                                               checked: showSidebar,
                                                               publishTopic: "ALF_DOCLIST_SHOW_SIDEBAR"
                                                            }
                                                         }
                                                      ]
                                                   }
                                                },
                                                {
                                                   id: "DOCLIB_RWD_PAGINATION_OPTIONS",
                                                   name: "alfresco/documentlibrary/AlfResultsPerPageGroup",
                                                   config: {
                                                      label: msg.get("pagination.options.label"),
                                                      groupName: "DOCUMENTS_PER_PAGE_GROUP_RWD",
                                                      maxRwdWidth: 1024
                                                   }
                                                }
                                             ]
                                          }
                                       }
                                    ]
                                 }
                              }
                           ]
                        }
                     },
                     {
                        id: "DOCLIB_BREADCRUMB_TRAIL",
                        name: "alfresco/documentlibrary/AlfBreadcrumbTrail",
                        config: {
                           hide: hideBreadcrumbTrail
                        }
                     },
                     {
                        id: "DOCLIB_DOCUMENT_LIST",
                        name: "alfresco/documentlibrary/AlfDocumentList",
                        config: {
                           useHash: true,
                           containerId: containerId,
                           customAggregatedJsResource: customAggregatedJsResource,
                           customAggregatedCssResource: customAggregatedCssResource,
                           googleDocsEnabled: googleDocsEnabled,
                           highlightFile: page.url.args["file"] != null ? page.url.args["file"] : "",
                           rootNode: rootNode,
                           replicationUrlMapping: getReplicationUrlMappingJSON(),
                           showFolders: showFolders,
                           siteId: siteId,
                           sortAscending: sortAscending,
                           sortField: sortField,
                           syncMode : syncMode != null ? model.syncMode : "",
                           userCanUpload: true,
                           usePagination: true,
                           userIsSiteManager: userIsSiteManager,
                           useTitle: (useTitle != null ? useTitle == "true" : true),
                           view: viewRendererName,
                           widgets: [
                              {
                                 name: "alfresco/documentlibrary/views/AlfSimpleView"
                              },
                              {
                                 name: "alfresco/documentlibrary/views/AlfDetailedView"
                              },
                              {
                                 name: "alfresco/documentlibrary/views/AlfGalleryView"
                              }
                           ]
                        }
                     },
                     {
                        name: "alfresco/upload/AlfUpload"
                     }
                  ]
               }
            }
         ]
      }
   };
   
   // Add the additional cloud synchronization related filters...
   if (syncMode != "OFF")
   {
      var filters = widgetUtils.findObject(docLibModel, "id", "DOCLIB_FILTERS");
      if (filters != null)
      {
         filters.config.widgets.push({
            name: "alfresco/documentlibrary/AlfDocumentFilter",
            config: {
               label: "link.synced",
               filter: "synced",
               description: "link.synced.description"
            }
         });
      }
   }
   if (syncMode == "ON_PREMISE")
   {
      var filters = widgetUtils.findObject(docLibModel, "id", "DOCLIB_FILTERS");
      if (filters != null)
      {
         filters.config.widgets.push({
            name: "alfresco/documentlibrary/AlfDocumentFilter",
            config: {
               label: "link.syncedErrors",
               filter: "syncedErrors",
               description: "link.syncedErrors.description"
            }
         });
      }
   }
   return docLibModel;
}