define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "dijit/_TemplatedMixin",
        "dijit/_KeyNavContainer",
        "dojo/text!./templates/AlfMenuGroups.html",
        "alfresco/core/Core",
        "dojo/keys",
        "dojo/_base/array",
        "alfresco/menus/AlfMenuGroup"], 
        function(declare, _WidgetBase, _TemplatedMixin, _KeyNavContainer, template, AlfCore, keys, array, AlfMenuGroup) {
   
   /**
    * This class has been created to act as the main container for the popup referenced by "alfresco/menus/AlfMenuBarPopup".
    * It currently just acts as a container object but is intended to allow instances of "alfresco/menus/AlfMenuGroup" to be
    * added into a menu bar popup.
    */
   return declare([_WidgetBase, _TemplatedMixin, _KeyNavContainer, AlfCore], {
      
      /**
       * The HTML template to use for the widget.
       * @property template {String}
       */
      templateString: template,
      
      /**
       * Process any widgets provided.
       * 
       * @method postCreate
       */
      postCreate: function alf_menus_AlfMenuGroups__postCreate() {
         
         // Set up keyboard handling...
         var l = this.isLeftToRight();
         this._openSubMenuKey = l ? keys.RIGHT_ARROW : keys.LEFT_ARROW;
         this._closeSubMenuKey = l ? keys.LEFT_ARROW : keys.RIGHT_ARROW;
         this.connectKeyNavHandlers([keys.UP_ARROW], [keys.DOWN_ARROW]);
         
         if (this.widgets)
         {
            this.processWidgets(this.widgets);
         }
      },
      
      /**
       * 
       * @method allWidgetsProcessed
       */
      allWidgetsProcessed: function alfresco_menus_AlfMenuGroups__allWidgetsProcessed(widgets) {
         var _this = this;
         array.forEach(widgets, function(widget, i) {
            _this.addChild(widget);
         });
      },
      
      /**
       * Extends the default implementation to ensure that all child widgets are an instance
       * of AlfMenuGroup.
       * 
       * @method addChild
       * @param {object} widget The widget to be added
       * @param {integer} insertIndex The index to add the widget at
       */
      addChild: function alfresco_menus_AlfMenuGroups__addChild(widget, insertIndex) {
         
         if (widget.isInstanceOf(AlfMenuGroup))
         {
            // Check to see if the current entry is an AlfMenuGroup.
            this.alfLog("log", "Adding group: ", widget);
            this.inherited(arguments);
         }
         else
         {
            this.alfLog("log", "Creating group for: ", widget);
            var newGroup = new AlfMenuGroup();
            newGroup.addChild(widget);
            this.inherited(arguments, [newGroup, insertIndex]);
         }
      }
   });
});