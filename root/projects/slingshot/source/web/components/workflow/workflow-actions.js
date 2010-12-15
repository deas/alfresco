/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Workflow WorkflowActions util
 *
 * @namespace Alfresco.action
 * @class Alfresco.action.WorkflowActions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
         $combine = Alfresco.util.combinePaths,
         $siteURL = Alfresco.util.siteURL;

   /**
    * Alfresco.action.WorkflowActions implementation
    */
   Alfresco.action.WorkflowActions = {};
   Alfresco.action.WorkflowActions.prototype =
   {

      /**
       * Prompts the user if the workflow really should be cancelled
       *
       * @method _showDialog
       * @param workflowId {String} The workflow id
       * @param workflowTitle {String} THe workflow title
       * @private
       */
      cancelWorkflow: function WA_cancelWorkflow(workflowId, workflowTitle)
      {
         var me = this,
               wid = workflowId;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("workflow.cancel.title"),
            text: this.msg("workflow.cancel.label", workflowTitle),
            noEscape: true,
            buttons: [
               {
                  text: Alfresco.util.message("button.yes", this.name),
                  handler: function WA_cancelWorkflow_yes()
                  {
                     this.destroy();
                     me._cancelWorkflow.call(me, wid);
                  }
               },
               {
                  text: Alfresco.util.message("button.no", this.name),
                  handler: function WA_cancelWorkflow_no()
                  {
                     this.destroy();
                  },
                  isDefault: true
               }]
         });
      },

      /**
       * Cancels the workflow
       *
       * @method _cancelWorkflow
       * @param workflowId {String} The workflow id
       * @private
       */
      _cancelWorkflow: function WA__cancelWorkflow(workflowId)
      {
         var me = this;
         var feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("workflow.cancel.feedback"),
            spanClass: "wait",
            displayTime: 0
         });

         // user has confirmed, perform the actual delete
         Alfresco.util.Ajax.jsonDelete(
         {
            url: Alfresco.constants.PROXY_URI + "api/workflow-instances/" + workflowId,
            successCallback:
            {
               fn: function(response, workflowId)
               {
                  feedbackMessage.destroy();
                  if (response.json && response.json.success)
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("workflow.cancel.success", this.name)
                     });

                     // Tell other components that the site has been deleted
                     YAHOO.Bubbling.fire("workflowCancelled",
                     {
                        workflow:
                        {
                           id: workflowId
                        }
                     });
                  }
                  else
                  {
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: Alfresco.util.message("workflow.cancel.failure", this.name)
                     });
                  }
               },
               obj: workflowId,
               scope: this
            },
            failureCallback:
            {
               fn: function(response)
               {
                  feedbackMessage.destroy();
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: Alfresco.util.message("workflow.cancel.failure", this.name)
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Takes a filter and looks for its url parameter representation
       *
       * @method createFilterURLParameters
       * @param filter {object} The filter to create url parameters for
       * @param filterParameters {Array} List of configured filter parameters that shall create url parameters
       * @return URL parameters created from the instructions in filterParameters based on data from the filter OR null no instructions were found
       * @override
       */
      createFilterURLParameters: function DateFilter_createFilterURLParameters(filter, filterParameters)
      {
         if (YAHOO.lang.isString(filter.filterData))
         {
            var filterParameter,
               result = null;
            for (var fpi = 0, fpil = filterParameters.length; fpi < fpil; fpi++)
            {
               filterParameter = filterParameters[fpi];
               if ((filter.filterId == filterParameter.id || filterParameter.id == "*") &&
                     (filter.filterData == filterParameter.data || filterParameter.data == "*"))
               {
                  return this.substituteParameters(filterParameter.parameters, {
                     id: filter.filterId,
                     data: filter.filterData
                  });
               }
            }
         }
         return null;
      },

      /**
       * Takes a template and performs substituion against "Obj" and according to date instructions as described below.
       *
       * Assumes the template data may contain date instructions where the instructions are placed inside curly brackets:
       * "param={attr}" - the name of an attribute in "obj"
       * "param={0dt}" - the current date time in iso8601 format
       * "param={1d}" - the current date (time set to end of day) and rolled l days forward
       * "param={-2d}" - the current date (time set to end of day) and rolled 2 days backward

       * @param template The template containing attributes from obj and dates to resolve
       * @param obj Contains runtime values
       */
      substituteParameters: function (template, obj)
      {
         var unresolvedTokens = template.match(/{[^}]+}/g);
         if (unresolvedTokens)
         {
            var resolvedTokens = {},
                  name, value, date;
            for (var i = 0, il = unresolvedTokens.length; i < il; i++)
            {
               name = unresolvedTokens[i].substring(1, unresolvedTokens[i].length - 1);
               value = name;
               date = new Date();
               if (/^[\-\+]?\d+(d|dt)$/.test(value))
               {
                  if (/^[\-\+]?\d+(d)$/.test(value))
                  {
                     // Only date (and not datetime) that was requested
                     date.setHours(11);
                     date.setMinutes(59);
                     date.setSeconds(59);
                     date.setMilliseconds(999);
                  }
                  date.setDate(date.getDate() + parseInt(value));
                  value = date;
               }
               else
               {
                  value = obj[name];
               }
               resolvedTokens[name] = Alfresco.util.isDate(value) ? Alfresco.util.toISO8601(value) :  value;
            }
            return YAHOO.lang.substitute(template, resolvedTokens);
         }
         return template;
      },

      /**
       * @method createAction
       * @param label
       * @param css
       * @param action
       * @param oRecord
       */
      createAction: function WA_createAction(elCell, label, css, action, oRecord)
      {
         var div = document.createElement("div");
         Dom.addClass(div, css);
         div.onmouseover = function()
         {
            Dom.addClass(this, css + "-over");
         };
         div.onmouseout = function()
         {
            Dom.removeClass(this, css + "-over");

         };
         var a = document.createElement("a");
         if (YAHOO.lang.isFunction(action))
         {
            Event.addListener(a, "click", action, oRecord, this);
            a.setAttribute("href", "#");
         }
         else
         {
            a.setAttribute("href", action);
         }
         var span = document.createElement("span");
         Dom.addClass(span, "theme-color-1");
         span.appendChild(document.createTextNode(label));
         a.appendChild(span);
         div.appendChild(a);
         elCell.appendChild(div);
      }
   }          
})();  
