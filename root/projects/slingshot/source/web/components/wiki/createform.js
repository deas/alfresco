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

/*
 *** Alfresco.WikiCreateForm
 *
 * @namespace Alfresco
 * @class Alfresco.Wiki
 * @extends Alfresco.component.Base
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
    * WikiCreateForm constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.LinksView} The new LinksView instance
    * @constructor
    */
   Alfresco.WikiCreateForm = function(htmlId)
   {
      Alfresco.WikiCreateForm.superclass.constructor.call(this, "Alfresco.WikiCreateForm", htmlId, ["button", "container", "connection", "editor"]);      
      return this;
   };
   
   YAHOO.extend(Alfresco.WikiCreateForm, Alfresco.component.Base,
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
          * Current siteId.
          *
          * @property siteId
          * @type string
          * @default ""
          */
         siteId: "",

         /**
          * The user locale
          *
          * @property locale
          * @type string
          * @default ""
          */
         locale: ""
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */
      onReady: function WikiCreateForm_onReady()
      {
         this.tagLibrary = new Alfresco.module.TagLibrary(this.id);
         this.tagLibrary.setOptions(
         {
            siteId: this.options.siteId
         });

         // Tiny MCE
         this.widgets.editor = Alfresco.util.createImageEditor(this.id + '-content',
         {
            height: 300,
            width: 600,
            inline_styles: false,
            convert_fonts_to_spans: false,
            theme: "advanced",
            plugins: "table,visualchars,emotions,advhr,print,directionality,fullscreen,insertdatetime",
            theme_advanced_buttons1: "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect,forecolor",
            theme_advanced_buttons2: "bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,alfresco-imagelibrary,image,cleanup,help,code,removeformat,|,insertdate,inserttime",
            theme_advanced_buttons3: "tablecontrols,|,hr,removeformat,visualaid,|,sub,sup,|,charmap,emotions,advhr,|,print,|,ltr,rtl,|,fullscreen",
            theme_advanced_toolbar_location: "top",
            theme_advanced_toolbar_align: "left",
            theme_advanced_statusbar_location: "bottom",
            theme_advanced_path: false,
            theme_advanced_resizing: true,
            siteId: this.options.siteId,
            language: this.options.locale
         });
         this.widgets.editor.addPageUnloadBehaviour(this.msg("message.unsavedChanges.wiki"));
         this.widgets.editor.render();

         this.widgets.saveButton = new YAHOO.widget.Button(this.id + "-save-button",
         {
            type: "submit"
         });

         Alfresco.util.createYUIButton(this, "cancel-button", null,
         {
            type: "link"
         });

         // Add validation to the rich text editor
         this.widgets.validateOnZero = 0;
         var keyUpIdentifier = (Alfresco.constants.HTML_EDITOR === 'YAHOO.widget.SimpleEditor') ? 'editorKeyUp' : 'onKeyUp';         
         this.widgets.editor.subscribe(keyUpIdentifier, function (e)
         {
            this.widgets.validateOnZero++;
            YAHOO.lang.later(1000, this, this.validateAfterEditorChange);
         }, this, true);
         
         // Create the form that does the validation/submit
         this.widgets.form = new Alfresco.forms.Form(this.id + "-form");
         var form = this.widgets.form;
         form.addValidation(this.id + "-title", Alfresco.forms.validation.mandatory, null, "blur");
         form.addValidation(this.id + "-title", Alfresco.forms.validation.nodeName, null, "keyup");
         form.addValidation(this.id + "-title", Alfresco.forms.validation.length,
         {
            max: 256,
            crop: true
         }, "keyup");

         // Content is mandatory
         form.addValidation(this.id + "-content", Alfresco.forms.validation.mandatory, null);

         form.setShowSubmitStateDynamically(true);
         form.setSubmitElements(this.widgets.saveButton);
         form.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onPageCreated,
               scope: this
            },
            failureCallback:
            {
               fn: this.onPageCreateFailed,
               scope: this
            }
         });

         form.setSubmitAsJSON(true);
         form.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
         form.doBeforeFormSubmit =
         {
            fn: function WikiCreateForm_doBeforeFormSubmit(form, obj)
            {
               // Disable save button to prevent double-submission
               this.widgets.saveButton.set("disabled", true);
               // Put the HTML back into the text area
               this.widgets.editor.save();
               // Update the tags set in the form
               this.tagLibrary.updateForm(this.id + "-form", "tags");

               // Avoid submitting the input field used for entering tags
               var tagInputElem = Dom.get(this.id + "-tag-input-field");
               if (tagInputElem)
               {
                  tagInputElem.disabled = true;
               }
               
               var title = Dom.get(this.id + "-title").value;
               title = title.replace(/\s+/g, "_");
               // Set the "action" attribute of the form based on the page title
               form.action =  Alfresco.constants.PROXY_URI + "slingshot/wiki/page/" + this.options.siteId + "/" + encodeURIComponent(title);
               
               // Display pop-up to indicate that the page is being saved
               this.widgets.savingMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  displayTime: 0,
                  text: '<span class="wait">' + $html(this.msg("message.saving")) + '</span>',
                  noEscape: true
               });
            },
            scope: this
         };

         this.tagLibrary.initialize(form);
         form.init();
         Dom.get(this.id + "-title").focus();
      },

      /**
       * Called when a key was pressed in the rich text editor.
       * Will trigger form validation after the last key stroke after a seconds pause.
       *
       * @method validateAfterEditorChange
       */
      validateAfterEditorChange: function WikiCreateForm_validateAfterEditorChange()
      {
         this.widgets.validateOnZero--;
         if (this.widgets.validateOnZero == 0)
         {
            var oldLength = Dom.get(this.id + '-content').value.length;
            this.widgets.editor.save();
            var newLength = Dom.get(this.id + '-content').value.length;
            if ((oldLength == 0 && newLength != 0) || (oldLength > 0 && newLength == 0))
            {
               this.widgets.form.updateSubmitElements();
            }
         }
      },

      /**
       * Event handler that gets called when the page is successfully created.
       * Redirects the user to the newly created page.
       *
       * @method onPageCreated
       * @param e {object} DomEvent
       */      
      onPageCreated: function WikiCreateForm_onPageCreated(e)
      {
         var name = "Main_Page"; // safe default
         
         var obj = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
         if (obj)
         {
            name = obj.name;
         }
      
         // Redirect to the page that has just been created
         window.location =  Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/wiki-page?title=" + encodeURIComponent(name);
      },

      /**
       * Event handler that gets called when the page has failed to be created.
       *
       * @method onPageCreateFailed
       * @param e {object} DomEvent
       */      
      onPageCreateFailed: function WikiCreateForm_onPageCreateFailed(e)
      {
         if (this.widgets.savingMessage)
         {
            this.widgets.savingMessage.destroy();
            this.widgets.savingMessage = null;
         }

         var pageTitle = e.config.dataObj.pageTitle;
         var me = this;

         if (e.serverResponse.status === 409)
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: this.msg("message.failure.title"),
               text: this.msg("message.failure.duplicate", pageTitle),
               buttons: [
               {
                  text: this.msg("button.ok"),
                  handler: function()
                  {
                     this.destroy();
                     Dom.get(me.id + "-title").focus();
                  },
                  isDefault: true
               }]
            });
         }
         else if (e.serverResponse.status == 401)
         {
            // Unauthenticated, which is probably due to a web-tier timeout or restart
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: this.msg("message.sessionTimeout.title"),
               text: this.msg("message.sessionTimeout.text")
            });
         }
         else
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: this.msg("message.failure"),
               text: e.json.message
            });
         }

         // Enable the tags input field again
         var tagInputElem = Dom.get(this.id + "-tag-input-field");
         if (tagInputElem)
         {
            tagInputElem.disabled = false;
         }
      }

   });
      
})();      
