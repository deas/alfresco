define(["dojo/_base/declare",
        "alfresco/core/Core",
        "dojo/_base/lang"],
        function(declare, AlfCore, lang) {
   
   return declare([AlfCore], {
      
      /**
       * Sets up the subscriptions for the NavigationService
       * 
       * @constructor
       * @param {array} args Constructor arguments
       */
      constructor: function alfresco_services_NavigationService__constructor(args) {
         this.alfSubscribe("ALF_NAVIGATE_TO_PAGE", lang.hitch(this, "navigateToPage"));
         this.alfSubscribe("ALF_RELOAD_PAGE", lang.hitch(this, "reloadPage"));
      },
      
      /**
       * This is the default page navigation handler. It is called when the service receives a publication on 
       * the "ALF_NAVIGATE_TO_PAGE" topic. At the moment it makes the assumption that the URL data will be relative
       * to the Share page context.
       * 
       * @method navigateToPage
       * @param {object} data An object containing the information about the page to navigate to.
       */
      navigateToPage: function alfresco_services_NavigationService__navigateToPage(data) {
         if (typeof data.url == "undefined" || data.url == null || data.url == "")
         {
            this.alfLog("error", "A page navigation request was made without a target URL defined as a 'url' attribute", data);
         }
         else
         {
            this.alfLog("log", "Page navigation request received:", data);
            var url;
            if (typeof data.type == "undefined" ||
                data.type == null ||
                data.type == "" ||
                data.type == "SHARE_PAGE_RELATIVE")
            {
               url = Alfresco.constants.URL_PAGECONTEXT + data.url;
            }
            else if (data.type == "FULL_PATH")
            {
               url = data.url;
            }
            
            // Determine the location of the URL...
            if (typeof data.target == "undefined" ||
                  data.target == null ||
                  data.target == "" ||
                  data.target == "CURRENT")
              {
                 window.location = url;
              }
              else if (data.target == "NEW")
              {
                 window.open(url);
              }
           
         }
      },
      
      /**
       * Reloads the current page. Despite the simplicity of the action, page refreshes should still be handled over the
       * pub/sub layer as this provides an opportunity for additional logging and other actions required by third party
       * extensions.
       * 
       * @method reloadPage
       * @param {object} data An object containing additional information. NOTE: Currently, no additional data is processed :)
       */
      reloadPage: function alfresco_services_NavigationService__reloadPage(data) {
         this.alfLog("log", "Page reload request received:", data);
         window.location.reload(true);
      }
   });
});