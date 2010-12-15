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
 * Dashboard MyTasks component.
 *
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.MyTasks
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
   var $html = Alfresco.util.encodeHTML;

   /**
    * Dashboard MyTasks constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.MyTasks} The new component instance
    * @constructor
    */
   Alfresco.dashlet.MyTasks = function MyTasks_constructor(htmlId)
   {
      Alfresco.dashlet.MyTasks.superclass.constructor.call(this, "Alfresco.dashlet.MyTasks", htmlId, ["button", "container", "datasource", "datatable", "paginator", "history", "animation"]);
      return this;
   };

   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.dashlet.MyTasks, Alfresco.component.Base);

   /**
    * Augment prototype with Common Workflow actions to reuse createFilterURLParameters
    */
   YAHOO.lang.augmentProto(Alfresco.dashlet.MyTasks, Alfresco.action.WorkflowActions);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.dashlet.MyTasks.prototype,
   {

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Task types not to display
          *
          * @property hiddenTaskTypes
          * @type object
          * @default []
          */
         hiddenTaskTypes: [],

         /**
          * Maximum number of tasks to display in the dashlet.
          *
          * @property maxItems
          * @type int
          * @default 50
          */
         maxItems: 50,

         /**
          * The filters to display in the filter menu.
          *
          * @property filters
          * @type Array
          */
         filters: []
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function MyTasks_onReady()
      {
         // Prepare webscript url to task instances
         var webscript = YAHOO.lang.substitute("api/task-instances?authority={authority}&properties={properties}&exclude={exclude}",
         {
            authority: encodeURIComponent(Alfresco.constants.USERNAME),
            properties: ["bpm_priority", "bpm_status", "bpm_dueDate", "bpm_description"].join(","),
            exclude: this.options.hiddenTaskTypes.join(",")
         });

         /**
          * Create datatable with a simple pagination that only displays number of results.
          * The pagination is handled in the "base" data source url and can't be changed in the dashlet
          */
         this.widgets.alfrescoDataTable = new Alfresco.util.DataTable(
         {
            dataSource:
            {
               url: Alfresco.constants.PROXY_URI + webscript,
               initialParameters: this.options.filters.length > 0 ? this.options.filters[0].value : ""
            },
            dataTable:
            {
               container: this.id + "-tasks",
               columnDefinitions:
               [
                  { key: "isPooled", sortable: false, formatter: this.bind(this.renderCellIcons), width: 20 },
                  { key: "title", sortable: false, formatter: this.bind(this.renderCellTaskInfo) },
                  { key: "name", sortable: false, formatter: this.bind(this.renderCellActions), width: 45 }
               ],
               config:
               {
                  MSG_EMPTY: this.msg("message.noTasks")
               }
            },
            paginator:
            {
               config:
               {
                  containers: [this.id + "-paginator"],
                  template: this.msg("pagination.template"),
                  pageReportTemplate: this.msg("pagination.template.page-report"),
                  rowsPerPage: this.options.maxItems
               }
            }
         });

         // Create filter menu
         this.widgets.filterMenuButton = Alfresco.util.createYUIButton(this, "filters", this.onFilterSelected,
         {
            type: "menu",
            menu: this.options.filters,
            lazyloadmenu: false
         });
         if (this.options.filters.length > 0)
         {
            this.widgets.filterMenuButton.set("label", this.options.filters[0].text);
         }
         Dom.removeClass(this.id + "-filters", "hide");
      },

      /**
       * Reloads the list with the new filter and updates the filter menu button's label
       *
       * @param event
       * @param args
       */
      onFilterSelected: function(event, args)
      {
         this.widgets.filterMenuButton.set("label", args[1].cfg.getProperty("text"));
         var parameters = this.substituteParameters(args[1].value, {});
         this.widgets.alfrescoDataTable.loadDataTable(parameters);
      },

      /**
       * Priority & pooled icons custom datacell formatter
       */
      renderCellIcons: function MyTasks_onReady_renderCellIcons(elCell, oRecord, oColumn, oData)
      {
         var priority = oRecord.getData("properties")["bpm_priority"],
               priorityMap = { "1": "high", "2": "medium", "3": "low" },
               priorityKey = priorityMap[priority + ""],
               pooledTask = oRecord.getData("isPooled");
         var desc = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/priority-' + priorityKey + '-16.png" title="' + this.msg("label.priority", this.msg("priority." + priorityKey)) + '"/>';
         if (pooledTask)
         {
            desc += '<br/><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/pooled-task-16.png" title="' + this.msg("label.pooledTask") + '"/>';
         }
         elCell.innerHTML = desc;
      },

      /**
       * Task info custom datacell formatter
       */
      renderCellTaskInfo: function MyTasks_onReady_renderCellTaskInfo(elCell, oRecord, oColumn, oData)
      {
         var taskId = oRecord.getData("id"),
               message = oRecord.getData("properties")["bpm_description"],
               dueDateStr = oRecord.getData("properties")["bpm_dueDate"],
               dueDate = dueDateStr ? Alfresco.util.fromISO8601(dueDateStr) : null,
               today = new Date(),
               type = oRecord.getData("title"),
               status = oRecord.getData("properties")["bpm_status"],
               assignee = oRecord.getData("owner");
            
         // if message is the same as the task type show the <no message> label
         if (message == type)
         {
            message = this.msg("workflow.no_message");
         }
               
         var messageDesc = '<h4><a href="task-edit?taskId=' + taskId + '&referrer=tasks" class="theme-color-1" title="' + this.msg("title.editTask") + '">' + $html(message) + '</a></h4>',
               dateDesc = dueDate ? '<h4><span class="' + (today > dueDate ? "task-delayed" : "") + '" title="' + 
                          this.msg("title.dueOn", Alfresco.util.formatDate(dueDate, "longDate")) + '">' + Alfresco.util.formatDate(dueDate, "longDate") + '</span></h4>' : "",
               statusDesc = '<div title="' + this.msg("title.taskSummary", type, status) + '">' + this.msg("label.taskSummary", type, status) + '</div>',
               unassignedDesc = '';
         if (!assignee || !assignee.userName)
         {
            unassignedDesc = '<span class="theme-bg-color-5 theme-color-5 unassigned-task">' + this.msg("label.unassignedTask") + '</span>';
         }
         elCell.innerHTML = messageDesc + dateDesc + statusDesc + unassignedDesc;
      },

      /**
       * Actions custom datacell formatter
       */
      renderCellActions:function MyTasks_onReady_renderCellActions(elCell, oRecord, oColumn, oData)
      {
         var task = oRecord.getData();
         var actions = "";
         if (task.isEditable)
         {
            actions += '<a href="task-edit?taskId=' + task.id + '&referrer=tasks" class="edit-task" title="' + this.msg("title.editTask") + '">&nbsp;</a>';
         }
         actions += '<a href="task-details?taskId=' + task.id + '&referrer=tasks" class="view-task" title="' + this.msg("title.viewTask") + '">&nbsp;</a>';
         elCell.innerHTML = actions;
      }

   });
})();
