define(["dojo/_base/declare",
        "dijit/PopupMenuBarItem",
        "alfresco/core/Core",
        "dojo/dom-construct",
        "dojo/dom-class",
        "alfresco/menus/AlfMenuGroups"], 
        function(declare, PopupMenuBarItem, AlfCore, domConstruct, domClass, AlfMenuGroups) {
   
   return declare([PopupMenuBarItem, AlfCore], {
      
      /**
       * The scope to use for i18n messages.
       * 
       * @property i18nScope {String}
       */
      i18nScope: "org.alfresco.Menus",
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/AlfMenuBarPopup.css"}],
      
      /**
       * Used to indicate whether or not to display a down arrow that indicates that this is a drop-down menu.
       * True by default.
       * 
       * @property {boolean} showArrow
       * @default true
       */
      showArrow: true,
      
      /**
       * This CSS class is added to the container node when an icon is to be included with the label. By
       * default it simply makes room for the icon - but this can be overridden.
       * 
       * @property {string} labelWithIconClass
       * @default "alf-menu-bar-popup-label-node"
       */
      labelWithIconClass: "alf-menu-bar-popup-label-node",
      
      /**
       * Sets the label of the menu item that represents the popup and creates a new alfresco/menus/AlfMenuGroups
       * instance containing all of the widgets to be displayed in the popup. Ideally the array of widgets should
       * be instances of alfresco/menus/AlfMenuGroup (where instance has its own list of menu items). However, this
       * widget should be able to accommodate any widget.
       * 
       * @method postCreate
       */
      postCreate: function alf_menus_AlfMenuBarPopup__postCreate() {
         if (this.label)
         {
            this.set("label", this.message(this.label));
         }
         if (this.iconClass && this.iconClass != "dijitNoIcon")
         {
            domConstruct.create("span", { className: this.iconClass, innerHTML: "&nbsp;"}, this.focusNode, "first");
            if (this.label)
            {
               domClass.add(this.containerNode, this.labelWithIconClass);
            }
         }
         if (this.showArrow)
         {
            // Add in the "arrow" image to indicate a drop-down menu. We do this with DOM manipulation
            // rather than overriding the default template for such a minor change. This means that we
            // have some protection against changes to the template in future Dojo releases.
            domConstruct.create("span", { className: "alf-menu-arrow",
                                          innerHTML: "&nbsp;&nbsp;&nbsp;&nbsp;"}, this.focusNode);
         }
         this.inherited(arguments);
         
         // A class in the hierarchy (PopupMenuItem) is expecting a "popup" attribute that contains the
         // dropdown menu item. We are going to construct this from the widgets provided.
         this.popup = new AlfMenuGroups({widgets: this.widgets});
      }
   });
});