define(["dojo/_base/declare",
        "alfresco/menus/AlfCascadingMenu",
        "dojo/dom-class",
        "dojo/dom-construct"], 
        function(declare, AlfCascadingMenu, domClass, domConstruct) {
   
   return declare([AlfCascadingMenu], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/AlfCascadingMenu.css"}],
   
      /**
       * 
       * 
       * @method postCreate
       */
      postCreate: function alfresco_header_AlfMenuBarPopup__postCreate() {
         this.inherited(arguments);
         if (this.popup && this.popup.domNode)
         {
            // This ensures that we can differentiate between header menu popups and regular menu popups with our CSS selectors
            domClass.add(this.popup.domNode, "alf-header-cascading-menu");
         }
      }
   });
});