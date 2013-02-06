define(["dojo/_base/declare",
        "dijit/MenuItem",
        "alfresco/menus/_AlfMenuItemMixin",
        "alfresco/core/Core",
        "dojo/dom-class",
        "dojo/dom-style"], 
        function(declare, MenuItem, _AlfMenuItemMixin, AlfCore, domClass, domStyle) {
   
   /**
    * Currently this extends the default Dojo implementation of a MenuItem without making any changes. Despite
    * it not providing any additional value-add yet it should still be used such that changes can be applied
    * without needing to modify page definition files.
    */
   return declare([MenuItem, _AlfMenuItemMixin, AlfCore], {
      
      /**
       * Ensures that the supplied menu item label is translated.
       * @method postCreate
       */
      postCreate: function alfresco_menus__AlfMenuItemMixin__postCreate() {
         if (this.iconClass && this.iconClass != "dijitNoIcon" && this.iconNode)
         {
            domClass.add(this.iconNode, this.iconClass);
         }
         else if (this.iconImage && this.iconNode)
         {
            /* The Dojo CSS class "dijitNoIcon" will automatically have been applied to a menu item
             * if it is not overridden. Therefore in order to ensure that the icon is displayed it
             * is necessary to set the height and width and to ensure that the display is set to
             * block. Because the style is being explicitly set it will take precedence over the
             * Dojo CSS class.
             */
            domStyle.set(this.iconNode, { backgroundImage: "url(" + this.iconImage + ")",
                                          width: this.iconImageWidth,
                                          height: this.iconImageHeight,
                                          display: "block" });
         }
         else
         {
            // If there is no iconClass or iconImage then we need to explicitly set the the
            // parent element of the icon node to have an inherited width. This is because there
            // is a CSS selector that fixes the width of menu items with icons to ensure that 
            // they are all aligned. This means that there would be a space for an icon even if
            // one was not available.
            domStyle.set(this.iconNode.parentNode, {
               width: "auto"
            });
         }
         
         this.inherited(arguments);
      }
   });
});