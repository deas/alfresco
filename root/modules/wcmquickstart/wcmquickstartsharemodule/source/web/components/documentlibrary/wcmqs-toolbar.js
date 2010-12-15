(function()
{
   /**
    * Store reference to onReady() function to allow extension.
    *
    * @method onReady_WCMQS
    */
   Alfresco.DocListToolbar.prototype.onReady_WCMQS = Alfresco.DocListToolbar.prototype.onReady;

   /**
    * Extend prototype onReady() function to allow creation of "New Article" button.
    *
    * @method onReady
    */
   Alfresco.DocListToolbar.prototype.onReady = function WCMQS_toolbar_onReady()
   {
      // New Article button: user needs "create" access
      this.widgets.newArticleButton = Alfresco.util.createYUIButton(this, "newArticle-button", this.onNewArticle,
      {
         disabled: true,
         value : "create"
      });

      this.onReady_WCMQS.apply(this, arguments);
   },
   
   /**
    * New Article button click handler
    *
    * @method onNewArticle
    * @param e {object} DomEvent
    * @param p_obj {object} Object passed back from addListener method
    */
   Alfresco.DocListToolbar.prototype.onNewArticle = function WCMQS_toolbar_onNewArticle(e, p_obj)
   {
      Alfresco.util.PopupManager.displayMessage(
      {
         text: "Not implemented"
      });
   };
})();