/**
 *  Adapter for tinyMCE html editor (http://tinymce.moxiecode.com).
 * 
 */
Alfresco.util.RichEditorManager.addEditor('tinyMCE', function(id,config)
{
   var editor;
   
   return (
   {
      init: function RichEditorManager_tinyMCE_init(id, config)
      {
         config.mode = 'exact';
         config.relative_urls = false;
         config.elements = id;
         
         // Ensure that we use the <font> tag for font colour, size and type. Even though it is deprecated we
         // need to use this as we will remove all style attributes from posted content to protect against 
         // XSS vulnerabilities in IE6 & IE7 (this can be removed once those browsers are no longer supported)
         if (config.formats == null)
         {
            config.formats = {};
         }
         config.formats.forecolor = {inline : 'font', attributes : {color : '%value'}};
         config.formats.fontname = {inline : 'font', attributes : {face : '%value'}};
         config.formats.fontsize = {inline : 'font', attributes : {size : '%value'}};
         config.formats.fontsize_class = {inline : 'font', attributes : {'class' : '%value'}},
         config.font_size_style_values = "1,2,3,4,5,6,7";
         config.plugins = (config.plugins && config.plugins != '') ? config.plugins + ', safari': 'safari';
         if (!config.init_instance_callback) 
         {
            config.init_instance_callback = function(o)
            {
               return function(inst)
               {
                  YAHOO.Bubbling.fire("editorInitialized", o);
               };
            }(this);
         }
         editor = new tinymce.Editor(id, config);
         return this;
      },

      getEditor: function RichEditorManager_tinyMCE_getEditor()
      {
         return editor;
      },

      clear: function RichEditorManager_tinyMCE_clear() 
      {
         YAHOO.util.Dom.get(editor.id).value = '';
         editor.setContent('');
      },

      render: function RichEditorManager_tinyMCE_render() 
      {
         editor.render();
      },

      execCommand: 'execCommand',

      disable: function RichEditorManager_tinyMCE_disable()
      {
         editor.hide();
      },

      enable: function RichEditorManager_tinyMCE_enable()
      {
         editor.show();
      },
      
      focus: function RichEditorManager_tinyMCE_focus()
      {
         editor.focus();
      },

      getContent: function RichEditorManager_tinyMCE_getContent() 
      { 
         return editor.getContent();
      }, 

      setContent: function RichEditorManager_tinyMCE_setContent(html) 
      { 
         editor.setContent(html);
      }, 

      save: function RichEditorManager_tinyMCE_save()
      {
         editor.save();
      },

      getContainer: function RichEditorManager_tinyMCE_getContainer()
      {
         return editor["editorId"] + "_tbl";
      },
      
      activateButton: function RichEditorManager_tinyMCE_activateButton(buttonId)
      {
         editor.controlManager.setActive(buttonId, true);
      },
      
      deactivateButton: function RichEditorManager_tinyMCE_deactivateButton(buttonId)
      {
         editor.controlManager.setActive(buttonId, false);
      },

      isDirty: function RichEditorManager_tinyMCE_isDirty()
      {
         return editor.isDirty();
      },

      clearDirtyFlag: function RichEditorManager_tinyMCE_clearDirtyFlag()
      {
         editor.isNotDirty = 1;
      },
      
      addPageUnloadBehaviour: function RichEditorManage_tinyMCE_addUnloadBehaviour(message, callback)
      {
         // Page unload / unsaved changes behaviour
         window.onbeforeunload = function(e)
         {
            if (callback())
            {
               var e = e || window.event;
               if (editor.isDirty())
               {
                  if (e)
                  {
                     e.returnValue = message;
                  }
                  return message;
               }
            }
         };
      }
   });
});
