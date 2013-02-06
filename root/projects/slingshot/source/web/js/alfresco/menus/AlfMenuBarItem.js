define(["dojo/_base/declare",
        "dijit/MenuBarItem",
        "alfresco/menus/_AlfMenuItemMixin",
        "alfresco/core/Core",
        "dojo/dom-construct"], 
        function(declare, MenuBarItem, _AlfMenuItemMixin, AlfCore, domConstruct) {
   
   /**
    * Currently this extends the default Dojo implementation of a MenuBarItem without making any changes. Despite
    * it not providing any additional value-add yet it should still be used such that changes can be applied
    * without needing to modify page definition files.
    */
   return declare([MenuBarItem, _AlfMenuItemMixin, AlfCore], {
      /**
       * Sets the label of the menu item that represents the popup and creates a new alfresco/menus/AlfMenuGroups
       * instance containing all of the widgets to be displayed in the popup. Ideally the array of widgets should
       * be instances of alfresco/menus/AlfMenuGroup (where instance has its own list of menu items). However, this
       * widget should be able to accommodate any widget.
       * 
       * @method postCreate
       */
      postCreate: function alf_menus_AlfMenuBarPopup__postCreate() {
         if (this.iconClass && this.iconClass != "dijitNoIcon")
         {
            domConstruct.create("span", { className: this.iconClass, innerHTML: "&nbsp;"}, this.focusNode);
         }
         else if (this.iconImage)
         {
            domConstruct.create("span", { style: "background-image: url(" + this.iconImage + ");", innerHTML: "&nbsp;"}, this.focusNode);
         }
         
         this.inherited(arguments);
      }
   });
});