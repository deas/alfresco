define(["dojo/_base/declare",
        "dijit/PopupMenuItem",
        "alfresco/menus/_AlfMenuItemMixin",
        "alfresco/menus/AlfMenuGroups",
        "alfresco/core/Core",
        "dojo/_base/array",
        "dojo/dom-class",
        "dojo/dom-style"], 
        function(declare, PopupMenuItem, _AlfMenuItemMixin, AlfMenuGroups, AlfCore, array, domClass, domStyle) {
   
   /**
    * Currently this extends the default Dojo implementation of a MenuItem without making any changes. Despite
    * it not providing any additional value-add yet it should still be used such that changes can be applied
    * without needing to modify page definition files.
    */
   return declare([PopupMenuItem, _AlfMenuItemMixin, AlfCore], {
      
      
      /**
       * Ensures that the supplied menu item label is translated.
       * @method postCreate
       */
      postCreate: function alfresco_menus__AlfCascadingMenu__postCreate() {
         this.inherited(arguments);
         
         // Create a popup menu and add children to it...
         this.popup = new AlfMenuGroups({widgets: this.widgets});
      }
   });
});