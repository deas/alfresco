define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "dojo/request/xhr",
        "dojo/_base/lang"],
        function(declare, AlfCore, AlfXhr, xhr, lang) {
   
   return declare([AlfCore, AlfXhr], {
      
      /**
       * Sets up the subscriptions for the PageService
       * 
       * @constructor
       * @param {array} args Constructor arguments
       */
      constructor: function alfresco_services_PageService__constructor(args) {
         this.alfSubscribe("ALF_GET_PAGES", lang.hitch(this, "loadPages"));
      },
      
      /**
       * Makes an XHR request to retrieve the pages that are available. The pages returned are those
       * that have been created and stored in the Data Dictionary on the Alfresco repository. Having 
       * retrieved the available pages it will publish on the "AvailablePages" topic.
       * @method loadPages
       */
      loadPages: function alfresco_services_PageService__loadPages() {
         var _this = this;
         xhr(appContext + "/service/share/page/options", {
            handleAs: "json",
         }).then(function(data) {
            _this.alfPublish("AvailablePages", data)
         });
      }
   });
});