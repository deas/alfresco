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
 * Alfresco.EventInfo
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Selector = YAHOO.util.Selector,
       Event = YAHOO.util.Event,
       KeyListener = YAHOO.util.KeyListener;

   Alfresco.EventInfo = function(containerId)
   {
      this.name = "Alfresco.EventInfo";
      this.id = containerId;

      this.panel = null;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.EventInfo.prototype =
   {
      /**
       * EventInfo instance.
       *
       * @property panel
       * @type Alfresco.EventInfo
       */
      panel: null,
      
      /**
       * A reference to the current event. 
       *
       * @property event
       * @type object
       */
      event: null,
      
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Current siteId.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Callback called when the event info panel is closed.
          *
          * @property onClose callback object with fn, scopt & obj attributes
          * @type {object}
          */
         onClose: null,
         eventUri: null,
         displayDate: null
      },      
      
       /**
        * Set multiple initialization options at once.
        *
        * @method setOptions
        * @param obj {object} Object literal specifying a set of options
        */
       setOptions: function EventInfo_setOptions(obj)
       {
          this.options = YAHOO.lang.merge(this.options, obj);
          return this;
       },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function EventInfo_onComponentsLoaded()
      {
         /* Shortcut for dummy instance */
         if (this.id === null)
         {
            return;
         }
      },

      /**
       * Renders the event info panel. 
       *
       * @method show
       * @param event {object} JavaScript object representing an event
       */
      show: function EventInfo_show(event)
      {
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/info",
            // TODO: The uri may need a leading slash for Day, Week and Month views until the event model is standardised.
            dataObj:
            { 
               "htmlid": this.id,
               "uri": event.uri 
            },
            
            successCallback:
            {
               fn: this.templateLoaded,
               scope: this
            },
            failureMessage: "Could not load event info panel",
            execScripts: true
         });

         this.event = event;
      },

      /**
       * Fired when the event info panel has loaded successfully.
       *
       * @method templateLoaded
       * @param response {object} DomEvent
       */
      templateLoaded: function EventInfo_templateLoaded(response)
      {
         var div = Dom.get("eventInfoPanel");
         div.innerHTML = response.serverResponse.responseText;

         this.panel = Alfresco.util.createYUIPanel(div,
         {
            width: "35em"
         });
         this.widgets = this.widgets || {};
         // Buttons
         this.widgets.deleteButton = Alfresco.util.createYUIButton(this, "delete-button", this.onDeleteClick);
         this.widgets.editButton = Alfresco.util.createYUIButton(this, "edit-button", this.onEditClick);
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelClick);
         this.widgets.escapeListener = new KeyListener(document,
         {
            keys: KeyListener.KEY.ESCAPE
         },
         {
            fn: function(id, keyEvent)
            {
               this.onCancelClick();
            },
            scope: this,
            correctScope: true
         });
         this.widgets.escapeListener.enable();
         if (this.options.permitToEditEvents!=='true')
         {
           this.widgets.deleteButton.set("disabled", true);
           this.widgets.editButton.set("disabled", true);
         }
         if (Dom.get(this.id+"-edit-available") == null)
         {
           this.widgets.deleteButton.set("disabled", true);
         }
         //convert iso date to readable human text
         var dateElIds = [this.id+'-startdate',this.id+'-enddate'];
         for (var i=0,len=dateElIds.length;i<len;i++)
         {
            var dateTextEl = Dom.get(dateElIds[i]);
            var textvalue = dateTextEl.innerHTML.split(' ');
            //only show date for allday events otherwise show time too
            if (textvalue.length>1)
            {
               dateTextEl.innerHTML = Alfresco.util.formatDate(Alfresco.util.fromISO8601(textvalue[0]), Alfresco.util.message("calendar.dateFormat.full")) + ' ' + textvalue[1] + ' ' + textvalue[2];               
            }
            else 
            {
               dateTextEl.innerHTML = Alfresco.util.formatDate(Alfresco.util.fromISO8601(textvalue[0]), Alfresco.util.message("calendar.dateFormat.full"));
            }
         }
         //decode html for text values of event
         var textData = Selector.query('.yui-gd .yui-u', div);
         for (var i=1;i<6;i+=2)
         {
            textData[i].innerHTML = Alfresco.util.decodeHTML(textData[i].innerHTML);
         }
         // Display the panel
         this.panel.show();
      },
      
      /**
       * Fired when the use selected the "Cancel" button.
       *
       * @method onCancelClick
       * @param e {object} DomEvent
       */
      onCancelClick: function EventInfo_onCancelClick(e)
      {
         this._hide();
      },
      
      /**
       * Fired when the user selects the "Edit" button.
       *
       * @method onEventClick
       * @param e {object} DomEvent
       */
      onEditClick: function(e)
      {
         if (this.isShowing) 
         {
          this._hide();
         }
         this.eventDialog = Alfresco.util.DialogManager.registerDialog('CalendarView.editEvent');
         this.eventDialog.id = "eventEditPanel";
         this.eventDialog.siteId = this.options.siteId;
          this.eventDialog.event = this.event;

         // add the tags that are already set on the post
         if (this.eventDialog.tagLibrary == undefined)
         {
            // If there is an existing TagLibrary component on the page, use that, otherwise create a new one.
            var existingTagLibComponent = Alfresco.util.ComponentManager.find({name: "Alfresco.module.TagLibrary"});
            if (existingTagLibComponent.length > 0) 
            {
               this.eventDialog.tagLibrary = existingTagLibComponent[0];
            } 
				else 
            {
               this.eventDialog.tagLibrary = new Alfresco.module.TagLibrary( this.eventDialog.id);
               this.eventDialog.tagLibrary.setOptions({ siteId: this.options.siteId });	
            }
         }
         this.eventDialog.tags = [];
         YAHOO.Bubbling.on('onTagLibraryTagsChanged',function(e,o) { 
            this.tags=o[1].tags;
         },
         this.eventDialog);
         
         var options = 
         {
            site : this.options.siteId,
            displayDate :this.options.displayDate,
            actionUrl : Alfresco.constants.PROXY_URI + this.options.eventUri + "&page=calendar",
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/add-event",
            templateRequestParams : {
                   site : this.options.siteId,
                   uri : '/'+this.options.eventUri
            },
            doBeforeFormSubmit : 
            {
              fn : function(form, obj)
                   {                           
                     // Update the tags set in the form
                     this.tagLibrary.updateForm(this.id + "-form", "tags");
                     // Avoid submitting the input field used for entering tags
                     var tagInputElem = YAHOO.util.Dom.get(this.id + "-tag-input-field");
                     if (tagInputElem)
                     {
                        tagInputElem.disabled = true;
                     }
                     var errorEls = YAHOO.util.Dom.getElementsByClassName('error',null,YAHOO.util.Dom.get(this.id + "-form"));
                     
                     for (var i = 0; i <errorEls.length;i++)
                     {
                       YAHOO.util.Dom.removeClass(errorEls[i],'error');                           
                     }
                     Dom.get(this.id+'-title').disabled = false;
                     Dom.get(this.id+'-location').disabled = false;
               },
              scope:this.eventDialog
            },
            doBeforeAjaxRequest : {
                fn : function(p_config, p_obj) 
                 {
                     var isAllDay = document.getElementsByName('allday').checked===true;
                     var startEl = document.getElementsByName('start')[0];
                     var endEl = document.getElementsByName('end')[0];

                     p_config.method = Alfresco.util.Ajax.PUT;
                     p_config.dataObj.tags = this.tags.join(' ');
                     //all day
                     if (YAHOO.lang.isUndefined(p_config.dataObj.start))
                     {
                        p_config.dataObj.start = startEl.value;
                        p_config.dataObj.end = endEl.value;
                     }
                     // if times start and end at 00:00 and not allday then add 1 hour
                     if (!isAllDay && (p_config.dataObj.start == '00:00' && p_config.dataObj.end =='00:00') )
                     {
                        p_config.dataObj.end = '01:00';
                     } 
                     
                     this.form.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
                   
                     return true;
                 },
                scope : this.eventDialog
            },
            doBeforeDialogShow : {
               fn : function()
                  {
                     var editEvent = this.event;
                     var Dom = YAHOO.util.Dom;
                     
                     var dts  = Alfresco.util.fromISO8601(editEvent.startAt.iso8601);
                     var dte  = Alfresco.util.fromISO8601(editEvent.endAt.iso8601);
                     
                     // Pretty formatting
                     var dateStr = Alfresco.util.formatDate(dts, Alfresco.util.message("calendar.dateFormat.full"));
                     Dom.get("fd").value = dateStr;
                     var dateStr = Alfresco.util.formatDate(dte, Alfresco.util.message("calendar.dateFormat.full"));
                     Dom.get("td").value = dateStr;
                     Dom.get(this.id+"-from").value =  Alfresco.util.formatDate(dts,'yyyy/mm/dd');
                     Dom.get(this.id+"-to").value = Alfresco.util.formatDate(dte,'yyyy/mm/dd');
                     var a = ['what','where','desc'];
                     for (var i=0;i<a.length;i++)
                     {
                      var el = document.getElementsByName(a[i])[0];
                      el.value = Alfresco.util.decodeHTML(el.value);
                     }
                     
                     //init taglib
                     Dom.get(this.id + "-tag-input-field").disabled=false;
                     Dom.get(this.id + "-tag-input-field").tabIndex = 8;
                     Dom.get(this.id + "-add-tag-button").tabIndex = 9;
                     var tags = YAHOO.util.Dom.get(this.id + "-tag-input-field").value;
                     YAHOO.util.Dom.get(this.id + "-tag-input-field").value = '';
                     this.tagLibrary.setTags(tags.split(' '));
                     this.form.errorContainer=null;
                     if (document.getElementsByName('allday')[0].checked===true) 
                     {
                        // hide time boxes if they're not relevent.
                        YAHOO.util.Dom.addClass(document.getElementsByName('start')[0].parentNode, "hidden")
                        YAHOO.util.Dom.addClass(document.getElementsByName('end')[0].parentNode, "hidden")
                     } else 
                     {
                        // show them if they are.
                        YAHOO.util.Dom.removeClass(document.getElementsByName('start')[0].parentNode, "hidden")
                        YAHOO.util.Dom.removeClass(document.getElementsByName('end')[0].parentNode, "hidden")
                     }                         
                     
                     if (Dom.get(this.id+"-edit-available") == null)
                     {
                        Dom.get(this.id+'-title').disabled = true;
                        Dom.get(this.id+'-location').disabled = true;
                        Dom.get(this.id+'-allday').disabled = true;
                        Dom.get(this.id+'-start').disabled = true;
                        Dom.get(this.id+'-end').disabled = true;
                     }
                     else
                     {
                        Dom.get(this.id+"-form").removeChild(Dom.get(this.id+"-edit-available"));
                     }
                                        
                     //hide mini-cal
                     this.dialog.hideEvent.subscribe(function() 
                     {
                        var oCal = Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView');
                        if (oCal && oCal.oCalendar)
                        {
                           oCal.oCalendar.hide();                    
                        }
                     },this,true);                              
                  },
               scope : this.eventDialog
            },
            doSetupFormsValidation:
            {
               fn: function (form)
               {
                   var Dom = YAHOO.util.Dom;
                   var cal = Alfresco.util.ComponentManager.findFirst('Alfresco.CalendarView');
                   
                   //validate text fields
                   var validateTextRegExp = {pattern:/({|})/, match:false };
                   var textElements = [this.id+"-title", this.id+"-location", this.id+"-description"];

                   form.addValidation(textElements[0], Alfresco.forms.validation.mandatory, null, "blur");
                   form.addValidation(textElements[0], Alfresco.forms.validation.mandatory, null, "keyup");

                   for (var i=0; i < textElements.length; i++)
                   {
                      form.addValidation(textElements[i],Alfresco.forms.validation.regexMatch, validateTextRegExp, "blur");
                      form.addValidation(textElements[i],Alfresco.forms.validation.regexMatch, validateTextRegExp, "keyup");
                   }
                   //validate time fields
                   var validateTimeRegExp = {pattern:/^\d{1,2}:\d{2}/, match:true};
                   var timeElements = [this.id + "-start", this.id + "-end"];
                   for (var i=0; i < timeElements.length; i++)
                   {
                      form.addValidation(timeElements[i],Alfresco.forms.validation.regexMatch, validateTimeRegExp, "blur");
                   }

                   form.addValidation(this.id + "-tag-input-field", Alfresco.module.event.validation.tags, null, "keyup");

                   this.tagLibrary.initialize(form);

                   var dateElements = ["td", "fd", this.id + "-start", this.id + "-end"];
                   for (var i=0; i < dateElements.length; i++)
                   {
                      form.addValidation(dateElements[i],this.options._onDateValidation, { "obj": this }, "blur");
                   }

                   // Setup date validation
                   form.addValidation("td", this.options._onDateValidation, { "obj": this }, "focus");
                   form.addValidation("fd", this.options._onDateValidation, { "obj": this }, "focus");

                   form.setShowSubmitStateDynamically(true, false);
                   form.setSubmitElements(this.widgets.okButton);
                   
                   /**
                    * keyboard handler for popup calendar button. Requried as YUI button's click
                    * event doesn't fire in firefox
                    */
                   var buttonKeypressHandler = function()
                   {
                     var dialogObject = Alfresco.util.DialogManager.getDialog('CalendarView.addEvent');
                     return function(e)
                     {
                       if (e.keyCode===YAHOO.util.KeyListener.KEY['ENTER'])
                       {
                         dialogObject.options.onDateSelectButton.apply(this,arguments);
                         return false;
                       }
                     };
                   }();

                   var browseButton = Alfresco.util.createYUIButton(this, "browse-button", function()
                   {
                      if (!this.browsePanel)
                      {
                         this.hide();
                         Alfresco.util.Ajax.request(
                         {
                            url: Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/browse-docfolder",
                            dataObj: {site: this.siteId},
                            successCallback:
                            {
                               fn: function(response)
                               {
                                  var containerDiv = document.createElement("div");
                                  containerDiv.innerHTML = response.serverResponse.responseText;
                                  var panelDiv = Dom.getFirstChild(containerDiv);
                                  this.browsePanel = Alfresco.util.createYUIPanel(panelDiv);
                               
                                  var parentDialog = this;
                                  var selectedDocfolder = Dom.get(parentDialog.id + "-docfolder").value;
                                  Alfresco.util.createYUIButton(this.browsePanel, "ok", function()
                                  {
                                     if (selectedDocfolder.charAt(selectedDocfolder.length - 1) == '/')
                                     {
                                        selectedDocfolder = selectedDocfolder.substring(0, selectedDocfolder.length - 1);
                                     }
                                     Dom.get(parentDialog.id + "-docfolder").value = selectedDocfolder;
                                     parentDialog.browsePanel.hide();
                                     parentDialog.show();
                                  });
                                  Alfresco.util.createYUIButton(this.browsePanel, "cancel", function()
                                  {
                                     parentDialog.browsePanel.hide();
                                     parentDialog.show();
                                  });

                                  Alfresco.util.createTwister("twister");
                                  var tree = new YAHOO.widget.TreeView("treeview");
                                  tree.setDynamicLoad(function(node, fnLoadComplete) 
                                  {
                                     var nodePath = node.data.path;
                                     var uri = Alfresco.constants.PROXY_URI + "slingshot/doclib/treenode/site/" + $combine(encodeURIComponent(parentDialog.siteId), encodeURIComponent("documentLibrary"), Alfresco.util.encodeURIPath(nodePath));
                                     var callback =
                                     {
                                        success: function (oResponse)
                                        {
                                           var results = YAHOO.lang.JSON.parse(oResponse.responseText), item, treeNode;
                                           if (results.items)
                                           {
                                              for (var i = 0, j = results.items.length; i < j; i++)
                                              {
                                                 item = results.items[i];
                                                 item.path = $combine(nodePath, item.name);
                                                 treeNode = _buildTreeNode(item, node, false);
                                                 if (!item.hasChildren)
                                                 {
                                                    treeNode.isLeaf = true;
                                                 }
                                              }
                                           }
                                           oResponse.argument.fnLoadComplete();
                                        },
 
                                        failure: function (oResponse)
                                        {
                                           Alfresco.logger.error("", oResponse);
                                        },
 
                                        argument:
                                        {
                                           "node": node,
                                           "fnLoadComplete": fnLoadComplete
                                        },
 
                                        scope: this
                                     };
                                     YAHOO.util.Connect.asyncRequest('GET', uri, callback);
                                  });

                                  tree.subscribe("clickEvent", function (args)
                                  {
                                     selectedDocfolder =  "documentLibrary" + args.node.data.path;
                                  });
                                  tree.subscribe("collapseComplete", function(node)
                                  {
                                     selectedDocfolder = "documentLibrary" + node.data.path;
                                  });
 
                                  var tempNode = _buildTreeNode(
                                  {
                                     name: "documentLibrary",
                                     path: "/",
                                     nodeRef: ""
                                  }, tree.getRoot(), false);

                                  tree.render();
                                  this.browsePanel.show();
                               },
                               scope: this
                            },
                            failureMessage: "Could not load dialog template from '" + Alfresco.constants.URL_SERVICECONTEXT + "components/calendar/browse-docfolder" + "'.",
                            scope: this,
                            execScripts: true
                         });
                      }
                      else
                      {
                         this.hide();
                         this.browsePanel.show();
                      }
                   });
                 
                   /**
                     * Button declarations that, when clicked, display
                     * the calendar date picker widget.
                     */
                  if (Dom.get(this.id+"-edit-available") != null)
                  {
                    if (!this.startButton)
                    {
                       this.startButton = new YAHOO.widget.Button(
                       {
                           type: "link",
                           id: "calendarpicker",
                           label:'',
                           href:'',
                           tabindex:4,                        
                           container: this.id + "-startdate"
                       });
                    
                       this.startButton.on("click", this.options.onDateSelectButton);
                        Event.on(Dom.get("fd") , "click" , this.options.onDateSelectButton , {} , this)
                       this.startButton.on("keypress", buttonKeypressHandler);                       
                    }
                    if (!this.endButton)
                    {
                       this.endButton = new YAHOO.widget.Button(
                       {
                          type: "link",                       
                          id: "calendarendpicker",
                          label:'',
                          href:'test',
                          tabindex:6,     
                          container: this.id + "-enddate"
                       });
                    
                       this.endButton.on("click", this.options.onDateSelectButton);
                        Event.on(Dom.get("td") , "click" , this.options.onDateSelectButton , {} , this)
                       this.endButton.on("keypress", buttonKeypressHandler);                       
                    }
                  }
                  /* disable time fields if all day is selected */
                  YAHOO.util.Event.addListener(document.getElementsByName('allday')[0], 'click', function(e)
                  {
                     if (YAHOO.util.Event.getTarget(e).checked===true) 
                     {
                        // hide time boxes if they're not relevent.
                        YAHOO.util.Dom.addClass(document.getElementsByName('start')[0].parentNode, "hidden")
                        YAHOO.util.Dom.addClass(document.getElementsByName('end')[0].parentNode, "hidden")
                     } else 
                     {
                        // show them if they are.
                        YAHOO.util.Dom.removeClass(document.getElementsByName('start')[0].parentNode, "hidden")
                        YAHOO.util.Dom.removeClass(document.getElementsByName('end')[0].parentNode, "hidden")
                     }
                  });
               },
               scope: this.eventDialog
            },
            onSuccess : {
               fn : this.onEdited,
               scope : this
            },
            onFailure : {
               fn : function() 
               {
                   Alfresco.util.PopupManager.displayMessage(
                   {
                     text: Alfresco.util.message('message.edited.failure','Alfresco.CalendarView')
                  });
              },
              scope : this
           }
          };
          this.eventDialog.setOptions(options);
          DEBUG_edit = options;
          this.eventDialog.show();
      },

      /**
       * Called when an event is successfully edited.
       *
       * @method onEdited
       * @param e {object} DomEvent
       */
      onEdited: function(o)
      {
         YAHOO.Bubbling.fire('eventEdited',
         {
            id: this.options.event, // so we know which event we are dealing with
            data : o.json.data
         });
         if (this.panel) 
         {
            this.panel.hide();
            this.panel.destroy();
         }
         this.eventDialog.dialog.destroy();
      },
      
      /**
       * Fired when the delete is clicked. Kicks off a DELETE request
       * to the Alfresco repo to remove an event.
       * 
       * Also triggered by the delete action link in the Agenda DataTable.
       *
       * @method onDeleteClick
       * @param e {object} DomEvent
       */
      onDeleteClick: function EventInfo_onDeleteClick(e)
      {
         var me = this,
            displayDate = Alfresco.thirdparty.dateFormat(Alfresco.util.fromISO8601(this.event.from), this._msg("date-format.mediumDate"));
            
         Alfresco.util.PopupManager.displayPrompt(
         {
            noEscape: true,
            title: this._msg("message.confirm.delete.title"),
            text: this._msg("message.confirm.delete", this.event.name, displayDate),
            buttons: [
            {
               text: this._msg("button.delete"),
               handler: function EventInfo_onActionDelete_delete()
               {
                  this.destroy();
                  me._onDeleteConfirm.call(me);
               }
            },
            {
               text: this._msg("button.cancel"),
               handler: function EventInfo_onActionDelete_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },
      
      /**
       * Delete Event confirmed.
       * Kicks off a DELETE request to the Alfresco repo to remove an event.
       *
       * @method _onDeleteConfirm
       * @private
       */
      _onDeleteConfirm: function EventInfo_onDeleteConfirm()
      {
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.DELETE,
            url: Alfresco.constants.PROXY_URI + this.event.uri + "&page=calendar",
            successCallback:
            {
               fn: this.onDeleted,
               scope: this
            },
            failureMessage: this._msg("message.delete.failure", this.event.name)
         });
      },
      
      /**
       * Called when an event is successfully deleted.
       *
       * @method onDeleted
       * @param e {object} DomEvent
       */
       onDeleted: function EventInfo_onDeleted(e)
       {
          this._hide();
          Alfresco.util.PopupManager.displayMessage(
          {
              text: this._msg("message.delete.success", this.event.name)
          });
          YAHOO.Bubbling.fire('eventDeleted',
          {
              id: this.options.event // so we know which event we are dealing with
          });
          if (this.panel) 
          {
             this.panel.destroy();
          }        
       },


       /**
        * PRIVATE FUNCTIONS
        */

       /**
        * Gets a custom message
        *
        * @method _msg
        * @param messageId {string} The messageId to retrieve
        * @return {string} The custom message
        * @private
        */
       _msg: function EventInfo__msg(messageId)
       {
          return Alfresco.util.message.call(this, messageId, "Alfresco.EventInfo", Array.prototype.slice.call(arguments).slice(1));
       },

      /**
       * Hides the panel and calls onClose callback if present
       *
       * @method _hide
       * @param e {object} DomEvent
       * @private
       */
      _hide: function EventInfo__hide()
      {
         if (this.widgets && this.widgets.escapeListener)
         {
            this.widgets.escapeListener.disable();
         }
         if (this.panel) 
         {
            this.panel.hide();
         }
         var callback = this.options.onClose;
         if (callback && typeof callback.fn == "function")
         {
            // Call the onClose callback in the correct scope
            callback.fn.call((typeof callback.scope == "object" ? callback.scope : this), callback.obj);
         }
      }
   };
})();


/**
 * Tags entry field validation handler, tests that the given field's value is a valid.
 * This is identical to the test for the name for a node in the repository minus the requirement
 * that there must not be any white space; tags are separated by white space.
 *
 * @method nodeName
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @static
 */
// Ensure namespaces exist
Alfresco.module.event =  Alfresco.module.event || {}; 
Alfresco.module.event.validation = Alfresco.module.event.validation || {};
 
Alfresco.module.event.validation.tags = function mandatory(field, args, event, form, silent)
{
   if (!args)
   {
      args = {};
   }

   args.pattern = /([\"\*\\\>\<\?\/\:\|]+)|([\.]?[\.]+$)/;
   args.match = false;

   return Alfresco.forms.validation.regexMatch(field, args, event, form, silent); 
};

	   
/**
 * Alfresco Slingshot aliases
 */
var $html = Alfresco.util.encodeHTML,
   $combine = Alfresco.util.combinePaths;

/**
 * Build a tree node using passed-in data
 *
 * @method _buildTreeNode
 * @param p_oData {object} Object literal containing required data for new node
 * @param p_oParent {object} Optional parent node
 * @param p_expanded {object} Optional expanded/collaped state flag
 * @return {YAHOO.widget.TextNode} The new tree node
*/
function _buildTreeNode(p_oData, p_oParent, p_expanded)
{
   return new YAHOO.widget.TextNode(
   {
      label: $html(p_oData.name),
      path: p_oData.path,
      nodeRef: p_oData.nodeRef,
      description: p_oData.description
   }, p_oParent, p_expanded);
}
