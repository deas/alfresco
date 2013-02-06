define(["dojo/_base/declare",
        "alfresco/menus/AlfMenuBar"], 
        function(declare, AlfMenuBar) {
   
   /**
    * Extend the default alfresco/menus/AlfMenuBar implementation to set specific CSS for the menus.
    * TODO: This will actually effect ALL of the menus unless we add an additional CSS classes to the nodes - check the CSS selectors 
    */
   return declare([AlfMenuBar], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/AlfMenuBar.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @property i18nRequirements {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/AlfMenuBar.properties"}]
   });
});