/**
 *
 * TODO: Config readers to cache the configuration between requests
 *
 */

/**
 * Customisable areas
 */
var DocList_Custom = (function()
{
   var doclistActionGroupResolver = null;
   var doclistDataUrlResolver = null;

   return {
      /**
       * Overridable function to calculate correct action group based on node details.
       * Its also possible to instead override the "resolver.doclib.actionGroupResolver" bean OR
       * to change the resolver bean by configuring "DocLibActions actionGroupResolver".
       *
       * @method calculateActionGroupId
       * @param item {Object} Record object representing a node, as returned from data webscript
       * @param view {String} Current page view, currently "details" or "browse"
       * @param itemJSON {String} item object as a json string
       * @return {String} Action Group Id
       */
      calculateActionGroupId: function calculateActionGroupId(item, view, itemJSON)
      {
         // Default group calculation
         if (!doclistActionGroupResolver)
         {
            doclistActionGroupResolver = resolverHelper.getDoclistActionGroupResolver(config.scoped["DocLibActions"]["actionGroupResolver"].value)
         }
         return (doclistActionGroupResolver.resolve(itemJSON, view) + "");
      },

      /**
       * Overridable function to calculate the remote data URL.
       * The returned URL will be used as the parameter for remote.call()
       *
       * Its also possible to instead override the "resolver.doclib.doclistDataUrl" bean OR
       * to change the resolver bean by configuring "DocumentLibrary doclist data-url-resolver".
       *
       * @method calculateRemoteDataURL
       * @return {String} Remote Data URL
       */
      calculateRemoteDataURL: function calculateRemoteDataURL()
      {
         if (!doclistDataUrlResolver)
         {
            doclistDataUrlResolver = resolverHelper.getDoclistDataUrlResolver(config.scoped["DocumentLibrary"]["doclist"].childrenMap["data-url-resolver"].get(0).value)
         }
         return (doclistDataUrlResolver.resolve(url.templateArgs.webscript, url.templateArgs.params, args) + "");
      }
   };
})();

var this_DocList = this;

/**
 * Main implemenation
 */
var DocList =
{
   PROP_NAME: "cm:name",
   PROP_TITLE: "cm:title",

   /**
    * Process the response from the Repository webscripts, decorating with appropriate action config.
    *
    * @method processResult
    * @param doclist {Object} JSON response from Repository webscripts
    * @param options {Object} Determines optional processing steps
    * <pre>
    *    actions: true|false           // Calculate list of viable actions
    *    indicators: true|false        // Calculate indicator icon visilibity
    *    metadataTemplate: true|false  // Calculate metadata view template (browse view)
    * </pre>
    */
   processResult: function processResult(doclist, options)
   {
      var p_view = (args.view || "details").toLowerCase(),
         allActions = DocList.getAllActions(), // <-- this can be cached until config is reset
         allIndicators = DocList.getAllIndicators(), // <-- this can also be cached
         allTemplates = DocList.getAllMetadataTemplates(), // <-- this too
         nodeActions, nodeIndicators,
         item, node, actionGroupId, actions, actionTemplate, action, finalActions,
         indicatorTemplate, indicator,
         metadataTemplate, template,
         i, index,
         metadata = doclist.metadata,
         metaJSON = jsonUtils.toJSONObject(doclist.metadata),
         workingCopyLabel = metadata.workingCopyLabel;

      /**
       * Sort actions by index attribute
       */
      var fnSortByIndex = function fnSortByIndex(item1, item2)
      {
         return (item1.index > item2.index) ? 1 : (item1.index < item2.index) ? -1 : 0;
      };

      // Processing options
      options = DocList.merge(
      {
         actions: false,
         indicators: false,
         metadataTemplate: false
      }, options || {});

      doclist.metadata.parent = doclist.metadata.parent || {};
      doclist.metadata.parent.permissions = doclist.metadata.parent.permissions || {};
      doclist.metadata.parent.permissions.user = doclist.metadata.parent.permissions.user || {};
      doclist.metadata.parent.permissions.roles = doclist.metadata.parent.permissions.roles || [];

      /**
       * Process a repository item (representing a node and associated metadata)
       */
      var fnProcessItem = function processItem(item)
      {
         var permissionCheck, evaluatorQualified, index, evaluator, i;

         node = item.node;
         node.permissions = node.permissions || {};
         node.permissions.user = node.permissions.user || {};
         node.permissions.roles = node.permissions.roles || [];

         // If this node shares a common parent, then copy a reference to the common parent into this item
         if (!item.parent)
         {
            item.parent = doclist.metadata.parent;
         }

         /**
          * Calculated convenience properties
          */

         item.nodeRef = node.nodeRef;
         item.fileName = node.properties[DocList.PROP_NAME];
         if (node.isLink)
         {
            item.displayName = node.properties[DocList.PROP_TITLE];
         }
         else
         {
            item.displayName = node.properties[DocList.PROP_NAME];
            if (item.workingCopy)
            {
               item.displayName = item.displayName.replace(workingCopyLabel, "");
            }
         }

         var itemJSON = jsonUtils.toJSONObject(item);

         /**
          * Actions
          */

         if (options.actions)
         {
            var actionGroupId = DocList_Custom.calculateActionGroupId(item, p_view, itemJSON),
               actions = DocList.getGroupActions(actionGroupId, allActions),
               nodeActions = [];

            for each (actionTemplate in actions)
            {
               action = DocList.merge(actionTemplate, {});

               // Permission Check
               if (action.permissions)
               {
                  permissionCheck = true;
                  for (index in action.permissions)
                  {
                     if (action.permissions[index] != node.permissions.user[index])
                     {
                        // No need to check any more for this action
                        permissionCheck = false;
                        break;
                     }
                  }
                  if (!permissionCheck)
                  {
                     // Permission check failed - skip to next action
                     continue;
                  }

                  // Remove permissions from response
                  delete action.permissions;
               }

               // Evaluator check
               if (action.evaluators)
               {
                  evaluatorQualified = true;
                  for (index in action.evaluators)
                  {
                     evaluator = action.evaluators[index].evaluator;
                     if (evaluator.evaluate(itemJSON, metaJSON, args) != action.evaluators[index].qualify)
                     {
                        // No need to run any more evaluators for this action
                        evaluatorQualified = false;
                        break;
                     }
                  }
                  if (!evaluatorQualified)
                  {
                     // Evaluators didn't qualify - skip to next action
                     continue;
                  }

                  // Remove evaluators from response
                  delete action.evaluators;
               }

               // Permission(s) and evaluator(s) passed; action is valid
               nodeActions.push(action);
            }

            // Filter out any actions overridden by the presence of other actions
            DocList.filterOverrides(nodeActions);

            // Add actionGroupId and final, sorted action list to the item
            item.actionGroupId = actionGroupId;
            item.actions = nodeActions.sort(fnSortByIndex);
         }

         /**
          * Status Indicators
          */

         if (options.indicators)
         {
            nodeIndicators = [];
            for each (indicatorTemplate in allIndicators)
            {
               indicator = DocList.merge(indicatorTemplate, {});

               // Evaluator check
               if (indicator.evaluators)
               {
                  evaluatorQualified = true;
                  for (index in indicator.evaluators)
                  {
                     evaluator = indicator.evaluators[index].evaluator;
                     if (evaluator.evaluate(itemJSON, metaJSON, args) != indicator.evaluators[index].qualify)
                     {
                        // No need to run any more evaluators for this indicator
                        evaluatorQualified = false;
                        break;
                     }
                  }
                  if (!evaluatorQualified)
                  {
                     // Evaluators didn't qualify - skip to next indicator
                     continue;
                  }

                  // Remove evaluators from response
                  delete indicator.evaluators;
               }

               // Evaluator(s) passed; indicator is valid
               nodeIndicators.push(indicator);
            }

            // Filter out any overridden indicators
            DocList.filterOverrides(nodeIndicators);

            // Add final, sorted indicator list to the item
            item.indicators = nodeIndicators.sort(fnSortByIndex);
         }

         /**
          * Metadata Template
          */

         if (options.metadataTemplate)
         {
            nodeTemplate = DocList.merge(allTemplates["default"], {});
            for each (metadataTemplate in allTemplates)
            {
               if (metadataTemplate.id != "default")
               {
                  template = DocList.merge(metadataTemplate, {});

                  // Evaluator check
                  if (template.evaluators)
                  {
                     evaluatorQualified = true;
                     for (index in template.evaluators)
                     {
                        evaluator = template.evaluators[index].evaluator;
                        if (evaluator.evaluate(itemJSON, metaJSON, args) != template.evaluators[index].qualify)
                        {
                           // No need to run any more evaluators for this template
                           evaluatorQualified = false;
                           break;
                        }
                     }
                     if (!evaluatorQualified)
                     {
                        // Evaluators didn't qualify - try next template
                        continue;
                     }

                     // Remove evaluators from response
                     delete template.evaluators;

                     // Found a suitable template
                     nodeTemplate = template;
                     break;
                  }
               }
            }

            // Check for evaluators in each banner
            var banners = [];
            for each (banner in nodeTemplate.banners)
            {
               if (banner == null)
               {
                  continue;
               }

               if (!banner.evaluator || banner.evaluator.evaluate(itemJSON, metaJSON, args))
               {
                  // Add display banner for this item
                  banners.push(
                  {
                     index: banner.index,
                     template: banner.template,
                     view: banner.view
                  });
               }
            }
            nodeTemplate.banners = banners.sort(fnSortByIndex);
            item.metadataTemplate = nodeTemplate;

            // Check for evaluators in each line
            var lines = [];
            for each (line in nodeTemplate.lines)
            {
               if (line == null)
               {
                  continue;
               }

               if (!line.evaluator || line.evaluator.evaluate(itemJSON, metaJSON, args))
               {
                  // Add display line for this item
                  lines.push(
                  {
                     index: line.index,
                     template: line.template,
                     view: line.view
                  });
               }
            }
            nodeTemplate.lines = lines.sort(fnSortByIndex);
            item.metadataTemplate = nodeTemplate;
         }

         return item;
      };

      if (doclist.item)
      {
         fnProcessItem(doclist.item);
      }
      else if (doclist.items)
      {
         for each (item in doclist.items)
         {
            fnProcessItem(item);
         }
      }
   },

   /**
    * Get action definitions for a given groupId
    *
    * @method getGroupActions
    * @param groupId {String} The groupId
    * @param allActions {Object} Object literal containing all actions from config
    * @return {Object} Object literal containing actions for the given groupId
    */
   getGroupActions: function getGroupActions(groupId, allActions)
   {
      var scopedRoot = config.scoped["DocLibActions"]["actionGroups"],
         groupConfigs, actionGroup, actionConfigs, actionConfig, actionId, actionIndex, actionLabel, action,
         actions = {};

      try
      {
         groupConfigs = scopedRoot.getChildren("actionGroup");
         if (groupConfigs)
         {
            for (var i = 0; i < groupConfigs.size(); i++)
            {
               actionGroup = groupConfigs.get(i);
               if (actionGroup.getAttribute("id") == groupId)
               {
                  actionConfigs = actionGroup.childrenMap["action"];
                  if (actionConfigs)
                  {
                     for (var j = 0; j < actionConfigs.size(); j++)
                     {
                        actionConfig = actionConfigs.get(j);
                        if (actionConfig)
                        {
                           // Get each action item for this actionGroup
                           actionId = actionConfig.getAttribute("id");
                           actionIndex = actionConfig.getAttribute("index");
                           if (actionId)
                           {
                              action = DocList.merge(allActions[actionId],
                              {
                                 index: actionIndex || 0
                              });
                              if (typeof action == "undefined")
                              {
                                 if (logger.isLoggingEnabled())
                                    logger.warn("[SURF-DOCLIST] Action definition not found: " + actionId);

                                 continue;
                              }

                              DocList.fnAddIfNotNull(action, actionConfig.getAttribute("icon"), "icon");
                              DocList.fnAddIfNotNull(action, actionConfig.getAttribute("type"), "type");
                              DocList.fnAddIfNotNull(action, actionConfig.getAttribute("label"), "label");

                              DocList.fnAddIfNotNull(action, DocList.getActionParamConfig(actionConfig), "params");
                              DocList.fnAddIfNotNull(action, DocList.getEvaluatorConfig(actionConfig), "evaluators");
                              DocList.fnAddIfNotNull(action, DocList.getActionPermissionConfig(actionConfig), "permissions");
                              DocList.fnAddIfNotNull(action, DocList.getOverrideConfig(actionConfig), "overrides");

                              actions[actionId] = action;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
      catch (e)
      {
      }

      return actions;
   },

   /**
    *
    * TODO: Config reader
    *
    */
   getAllActions: function getAllActions()
   {
      var scopedRoot = config.scoped["DocLibActions"]["actions"],
         configs, actions = {}, actionConfig, actionId, action, actionParamConfig;

      try
      {
         configs = scopedRoot.getChildren("action");
         if (configs)
         {
            for (var i = 0; i < configs.size(); i++)
            {
               actionConfig = configs.get(i);
               actionId = actionConfig.getAttribute("id");
               if (actionId)
               {
                  action = actions[actionId] ||
                  {
                     id: actionId,
                     icon: actionId
                  };

                  DocList.fnAddIfNotNull(action, actionConfig.getAttribute("icon"), "icon");
                  DocList.fnAddIfNotNull(action, actionConfig.getAttribute("type"), "type");
                  DocList.fnAddIfNotNull(action, actionConfig.getAttribute("label"), "label");

                  DocList.fnAddIfNotNull(action, DocList.getActionParamConfig(actionConfig), "params");
                  DocList.fnAddIfNotNull(action, DocList.getEvaluatorConfig(actionConfig), "evaluators");
                  DocList.fnAddIfNotNull(action, DocList.getActionPermissionConfig(actionConfig), "permissions");
                  DocList.fnAddIfNotNull(action, DocList.getOverrideConfig(actionConfig), "overrides");

                  actions[actionId] = action;
               }
            }
         }
      }
      catch(e)
      {
      }

      return actions;
   },

   /**
    *
    * TODO: Config reader
    *
    */
   getAllIndicators: function getAllIndicators()
   {
      var scopedRoot = config.scoped["DocumentLibrary"]["indicators"],
         configs, indicators = {}, indicatorConfig, indicatorId, indicatorIndex, indicatorAction, indicator, indicatorParamConfig;

      try
      {
         configs = scopedRoot.getChildren("indicator");
         if (configs)
         {
            for (var i = 0; i < configs.size(); i++)
            {
               indicatorConfig = configs.get(i);
               indicatorId = indicatorConfig.getAttribute("id");
               indicatorIndex = indicatorConfig.getAttribute("index");
               if (indicatorId)
               {
                  indicator =
                  {
                     id: indicatorId,
                     index: indicatorIndex || 0,
                     icon: indicatorConfig.getAttribute("icon") || (indicatorId + "-16.png"),
                     label: indicatorConfig.getAttribute("label") || ("status." + indicatorId)
                  };

                  DocList.fnAddIfNotNull(indicator, DocList.getEvaluatorConfig(indicatorConfig), "evaluators");
                  DocList.fnAddIfNotNull(indicator, DocList.getLabelParamConfig(indicatorConfig), "labelParams");
                  DocList.fnAddIfNotNull(indicator, DocList.getOverrideConfig(indicatorConfig), "overrides");
                  DocList.fnAddIfNotNull(indicator, indicatorConfig.getAttribute("action"), "action");

                  indicators[indicatorId] = indicator;
               }
            }
         }
      }
      catch(e)
      {
      }

      return indicators;
   },

   getActionParamConfig: function getActionParamConfig(itemConfig)
   {
      var params = {},
         paramConfig = itemConfig.childrenMap["param"],
         param, name, value;

      if (!paramConfig)
      {
         return null;
      }

      for (var i = 0; i < paramConfig.size(); i++)
      {
         param = paramConfig.get(i);
         name = param.getAttribute("name");
         if (name != null)
         {
            value = "" + param.value;
            if (value.length > 0)
            {
               params[name] = value;
            }
         }
      }

      return params;
   },

   getEvaluatorConfig: function getEvaluatorConfig(itemConfig)
   {
      var evaluators = {},
         evaluatorConfigs = itemConfig.childrenMap["evaluator"],
         evaluatorConfig, value, evaluator, result;

      if (!evaluatorConfigs)
      {
         return null;
      }

      for (var i = 0; i < evaluatorConfigs.size(); i++)
      {
         evaluatorConfig = evaluatorConfigs.get(i);
         if (evaluatorConfig != null)
         {
            value = "" + evaluatorConfig.value;
            if (value.length > 0)
            {
               qualify = !(evaluatorConfig.getAttribute("negate") == "true");
               evaluator = evaluatorHelper.getEvaluator(value);
               if (evaluator != null)
               {
                  evaluators[value] =
                  {
                     evaluator: evaluator,
                     qualify: qualify
                  };
               }
               else
               {
                  if (logger.isLoggingEnabled())
                     logger.warn("[SURF-DOCLIST] Bad evaluator config: " + jsonUtils.toJSONString(itemConfig));
               }
            }
         }
      }

      return evaluators;
   },

   getActionPermissionConfig: function getActionPermissionConfig(itemConfig)
   {
      var permissions = {},
         permsConfig = itemConfig.childrenMap["permissions"],
         perms, permConfig, perm, allow, deny, value, i, j;

      if (!permsConfig)
      {
         return null;
      }

      for (i = 0; i < permsConfig.size(); i++)
      {
         perms = permsConfig.get(i);
         if (perms != null)
         {
            permConfig = perms.childrenMap["permission"];
            if (!permConfig || permConfig.size() == 0)
            {
               // TODO: Support empty <permissions /> tag to indicate removal of permissions?
               return {};
            }

            for (j = 0; j < permConfig.size(); j++)
            {
               perm = permConfig.get(j);
               allow = perm.getAttribute("allow");
               deny = perm.getAttribute("deny");
               if (allow != null)
               {
                  value = (perm.value || "").toString();
                  if (value.length() > 0)
                  {
                     permissions[value] = (allow == "true");
                  }
               }
               else if (deny != null)
               {
                  value = (perm.value || "").toString();
                  if (value.length() > 0)
                  {
                     permissions[value] = (deny == "false");
                  }
               }
            }
         }
      }

      return permissions;
   },

   getOverrideConfig: function getOverrideConfig(itemConfig)
   {
      var overrides = [],
         overrideConfig = itemConfig.childrenMap["override"],
         override, value;

      if (!overrideConfig)
      {
         return null;
      }

      for (var i = 0; i < overrideConfig.size(); i++)
      {
         override = overrideConfig.get(i);
         value = "" + override.value;
         if (value.length > 0)
         {
            overrides.push(value);
         }
      }

      return overrides;
   },

   getLabelParamConfig: function getLabelParamConfig(itemConfig)
   {
      var labelParams = [],
         labelConfig = itemConfig.childrenMap["labelParam"],
         label, index, value;

      if (!labelConfig)
      {
         return null;
      }

      for (var i = 0; i < labelConfig.size(); i++)
      {
         label = labelConfig.get(i);
         index = label.getAttribute("index");
         if (index != null)
         {
            value = "" + label.value;
            if (value.length > 0)
            {
               labelParams[index] = value;
            }
         }
      }

      return labelParams;
   },

   getAllMetadataTemplates: function getAllMetadataTemplates()
   {
      var scopedRoot = config.scoped["DocumentLibrary"]["metadata-templates"],
         configs, templates = {}, templateConfig, templateId, templateIndex, template, templateParamConfig;

      try
      {
         configs = scopedRoot.getChildren("template");
         if (configs)
         {
            for (var i = 0; i < configs.size(); i++)
            {
               templateConfig = configs.get(i);
               templateId = templateConfig.getAttribute("id");
               if (templateId)
               {
                  template = templates[templateId] ||
                  {
                     id: templateId
                  };

                  DocList.fnAddIfNotNull(template, DocList.getEvaluatorConfig(templateConfig), "evaluators");
                  template.title = DocList.getTemplateTitleConfig(templateConfig);
                  // Banners and Lines are special cases: we need to merge instead of replace to allow for custom overrides by id
                  template.banners = DocList.merge(template.banners || {}, DocList.getTemplateBannerConfig(templateConfig) || {});
                  template.lines = DocList.merge(template.lines || {}, DocList.getTemplateLineConfig(templateConfig) || {});

                  templates[templateId] = DocList.merge({}, template);
               }
            }
         }
      }
      catch(e)
      {
      }

      return templates;
   },

   getTemplateTitleConfig: function getTemplateTitleConfig(templateConfig)
   {
      var templateTitle = null,
         titleConfig = templateConfig.childrenMap["title"],
         title;
      
      if (!titleConfig)
      {
         return null;
      }

      title = titleConfig.get(titleConfig.size() - 1);
      if (title.value != null)
      {
         templateTitle = title.value;
      }

      return templateTitle;
   },

   getTemplateBannerConfig: function getTemplateBannerConfig(templateConfig)
   {
      return DocList.getTemplateMetadataConfig(templateConfig, "banner");
   },

   getTemplateLineConfig: function getTemplateLineConfig(templateConfig)
   {
      return DocList.getTemplateMetadataConfig(templateConfig, "line");
   },

   getTemplateMetadataConfig: function getTemplateMetadataConfig(templateConfig, configElementName)
   {
      var templateMetadata = {},
         metaConfig = templateConfig.childrenMap[configElementName],
         meta, id, index, evaluator, view;

      if (!metaConfig)
      {
         return null;
      }

      for (var i = 0; i < metaConfig.size(); i++)
      {
         meta = metaConfig.get(i);
         id = meta.getAttribute("id");
         index = meta.getAttribute("index");
         evaluator = meta.getAttribute("evaluator");
         view = meta.getAttribute("view");
         if (id != null)
         {
            if (meta.value == null)
            {
               templateMetadata[id] = null;
            }
            else
            {
               templateMetadata[id] =
               {
                  index: index || 0,
                  template: meta.value,
                  view: view || ""
               };
               if (evaluator != null)
               {
                  templateMetadata[id].evaluator = evaluatorHelper.getEvaluator(evaluator);
               }
            }
         }
      }

      return templateMetadata;
   },

   filterOverrides: function filterOverrides(p_array)
   {
      // Remove any indicators overridden by others
      var item, override, i, ii, j, jj;
      for each (item in p_array)
      {
         if (item.overrides)
         {
            for (i = 0, ii = item.overrides.length; i < ii; i++)
            {
               override = item.overrides[i];
               for (j = 0; j < p_array.length; j++)
               {
                  if (p_array[j].id == override)
                  {
                     DocList.arrayRemove(p_array, j);
                     break;
                  }
               }
            }
         }
      }

      return p_array;
   },

   merge: function merge()
   {
      var augmentObject = function augmentObject(r, s)
      {
         if (!s||!r)
         {
             throw new Error("Absorb failed, verify dependencies.");
         }
         var i, p, overrideList=arguments[2];
         for (p in s)
         {
            if (overrideList || !(p in r))
            {
               r[p] = s[p];
            }
         }

          return r;
      };
      
      var o={}, l=arguments.length, i;
      for (i=0; i<l; i=i+1)
      {
         augmentObject(o, arguments[i], true);
      }
      return o;
   },

   arrayRemove: function arrayRemove(array, from, to)
   {
     var rest = array.slice((to || from) + 1 || array.length);
     array.length = from < 0 ? array.length + from : from;
     return array.push.apply(array, rest);
   },

   fnAddIfNotNull: function fnAddIfNotNull(p_targetObj, p_obj, p_name)
   {
      if (p_obj != null)
      {
         p_targetObj[p_name] = p_obj;
      }
   }
};

var surfDoclist_main = function surfDoclist_main(includeThumbnails)
{
   var json = "{}",
      dataUrl = DocList_Custom.calculateRemoteDataURL();
   if (includeThumbnails === true)
   {
      dataUrl += "&includeThumbnails=true";
   }

   var result = remote.call(dataUrl);

   if (result.status == 200)
   {
      var obj = JSON.parse(result);
      if (obj && (obj.item || obj.items))
      {
         DocList.processResult(obj,
         {
            actions: true,
            indicators: true,
            metadataTemplate: true
         });
         json = jsonUtils.toJSONString(obj);
      }
   }
   else
   {
      status.setCode(result.status);
   }

   model.json = json;
};
