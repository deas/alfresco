/*global args:true */
/**
 * Customisable areas
 */
var DocList_Custom =
{
   /**
    * Overidable function to calculate correct action group based on node details
    *
    * @method calculateActionGroupId
    * @param record {Object} Record object representing a node, as returned from data webscript
    * @param view {String} Current page view, currently "details" or "browse"
    * @return {String} Action Group Id
    */
   calculateActionGroupId: function calculateActionGroupId(record, view)
   {
      // Default
      return (record.node.isContainer ? "folder-" : "document-") + (record.node.isLink ? "link-" : "") + (view == "details" ? "details" : "browse");
   }
};

var this_DocList = this;

/**
 * Main implemenation
 */
var DocList =
{
   PROP_NAME: "cm:name",
   PROP_TITLE: "cm:title",

   processResult: function processResult(doclist)
   {
      var p_view = (args.view || "details").toLowerCase(),
         allActions = DocList.getAllActions(), // <-- this can be cached until config is reset
         allIndicators = DocList.getAllIndicators(), // <-- this can also be cached
         nodeActions, nodeIndicators,
         item, node, actionGroupId, actions, actionTemplate, action, finalActions, indicatorTemplate, indicator, i, index,
         workingCopyLabel = doclist.metadata.workingCopyLabel;

      var fnSortByIndex = function fnSortByIndex(item1, item2)
      {
         return (item1.index > item2.index) ? 1 : (item1.index < item2.index) ? -1 : 0;
      };

      var fnProcessItem = function processItem(item)
      {
         node = item.node;
         
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

         /**
          * Actions
          */
         actionGroupId = DocList_Custom.calculateActionGroupId(item, p_view);
         actions = DocList.getGroupActions(actionGroupId, allActions);
         nodeActions = [];

         for each (actionTemplate in actions)
         {
            action = DocList.merge(actionTemplate, {});

            // Permission Check
            if (action.permissions)
            {
               var permissionCheck = true;
               for (index in action.permissions)
               {
                  if (action.permissions[index] != node.permissions.user[index])
                  {
                     permissionCheck = false;
                     break;
                  }
               }
               if (!permissionCheck)
               {
                  continue;
               }

               delete action.permissions;
            }

            // Evaluator check
            if (action.evaluators)
            {
               var evaluatorCheck = true, index, evaluator;
               for (index in action.evaluators)
               {
                  evaluator = action.evaluators[index].evaluator;
                  if (evaluator.evaluate(jsonUtils.toJSONString(item), args) == action.evaluators[index].result)
                  {
                     evaluatorCheck = false;
                     break;
                  }
               }
               if (!evaluatorCheck)
               {
                  continue;
               }

               delete action.evaluators;
            }

            nodeActions.push(action);
         }

         // Filter overrides
         DocList.filterOverrides(nodeActions);

         item.actionGroupId = actionGroupId;
         item.actions = nodeActions.sort(fnSortByIndex);

         /**
          * Status Indicators
          */
         nodeIndicators = [];
         for each (indicatorTemplate in allIndicators)
         {
            indicator = DocList.merge(indicatorTemplate, {});

            // Evaluator check
            if (indicator.evaluators)
            {
               var evaluatorCheck = true, index, evaluator;
               for (index in indicator.evaluators)
               {
                  evaluator = indicator.evaluators[index].evaluator;
                  if (evaluator.evaluate(jsonUtils.toJSONString(item), args) == indicator.evaluators[index].result)
                  {
                     evaluatorCheck = false;
                     break;
                  }
               }
               if (!evaluatorCheck)
               {
                  continue;
               }

               delete indicator.evaluators;
            }

            nodeIndicators.push(indicator);
         }

         // Filter overrides
         DocList.filterOverrides(nodeIndicators);

         item.indicators = nodeIndicators.sort(fnSortByIndex);

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
    *
    * TODO: Config reader
    *
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
                                    logger.log("[SURF-DOCLIST] Action definition not found: " + actionId);

                                 continue;
                              }

                              DocList.fnAddIfNotNull(action, actionConfig.getAttribute("icon"), "icon");
                              DocList.fnAddIfNotNull(action, actionConfig.getAttribute("type"), "type");
                              DocList.fnAddIfNotNull(action, actionConfig.getAttribute("label"), "label");

                              DocList.fnAddIfNotNull(action, DocList.getActionParamConfig(actionConfig), "params");
                              DocList.fnAddIfNotNull(action, DocList.getEvaluatorConfig(actionConfig), "evaluators");
                              DocList.fnAddIfNotNull(action, DocList.getActionConditionConfig(actionConfig), "conditions");
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
                  action =
                  {
                     id: actionId,
                     icon: actionConfig.getAttribute("icon") || actionId,
                     type: actionConfig.getAttribute("type"),
                     label: actionConfig.getAttribute("label")
                  };

                  DocList.fnAddIfNotNull(action, DocList.getActionParamConfig(actionConfig), "params");
                  DocList.fnAddIfNotNull(action, DocList.getEvaluatorConfig(actionConfig), "evaluators");
                  DocList.fnAddIfNotNull(action, DocList.getActionConditionConfig(actionConfig), "conditions");
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
         configs, indicators = {}, indicatorConfig, indicatorId, indicatorIndex, indicator, indicatorParamConfig;

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

   /**
    *
    * TODO: Config readers
    *
    */
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
               result = evaluatorConfig.getAttribute("negate") == "true";
               evaluator = evaluatorHelper.getEvaluator(value);
               if (evaluator != null)
               {
                  evaluators[value] =
                  {
                     evaluator: evaluator,
                     result: result
                  };
               }
            }
         }
      }

      return evaluators;
   },

   /**
    * TODO: Implementation
    */
   getActionConditionConfig: function getActionConditionConfig(itemConfig)
   {
      return null;
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

   filterOverrides: function filterOverrides(p_array)
   {
      var fnArrayRemove = function fnArrayRemove(array, from, to)
      {
        var rest = array.slice((to || from) + 1 || array.length);
        array.length = from < 0 ? array.length + from : from;
        return array.push.apply(array, rest);
      };

      // Remove any indicators overridden by others
      var item, override, i, ii, j, jj;
      for each (item in p_array)
      {
         if (item.overrides)
         {
            for (i = 0, ii = item.overrides.length; i < ii; i++)
            {
               override = item.overrides[i];
               for (j = 0, jj = p_array.length; j < jj; j++)
               {
                  if (p_array[j].id == override)
                  {
                     fnArrayRemove(p_array, j);
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

   fnAddIfNotNull: function fnAddIfNotNull(p_targetObj, p_obj, p_name)
   {
      if (p_obj != null)
      {
         p_targetObj[p_name] = p_obj;
      }
   }
};
