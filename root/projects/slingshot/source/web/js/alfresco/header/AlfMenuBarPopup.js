define(["dojo/_base/declare",
        "alfresco/menus/AlfMenuBarPopup",
        "dojo/dom-class",
        "dojo/dom-construct"], 
        function(declare, AlfMenuBarPopup, domClass, domConstruct) {
   
   return declare([AlfMenuBarPopup], {
      
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
       * Extends the default implementation to create an additional <span> element with the show arrow CSS class to the 
       * focusNode of the widget.
       * 
       * @method postCreate
       */
      postCreate: function alfresco_header_AlfMenuBarPopup__postCreate() {
         this.inherited(arguments);
         if (this.popup && this.popup.domNode)
         {
            // This ensures that we can differentiate between header menu popups and regular menu popups with our CSS selectors
            domClass.add(this.popup.domNode, "alf-header-menu-bar");
         }
         
      }
   });
});