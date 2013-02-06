define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/Links.html",
        "alfresco/core/Core",
        "dojo/dom-construct",
        "dojo/_base/array"], 
        function(declare, _Widget, _Templated, template, AlfCore, domConstruct, array) {
   
   return declare([_Widget, _Templated, AlfCore], {
      
      cssRequirements: [{cssFile:"./css/Links.css",mediaType:"screen"}],
      
      templateString: template,
      
      postCreate: function() {
         this.inherited(arguments);
         var _this = this;
         this.alfPublish("GetPages", {});
         this.alfSubscribe("AvailablePages", function(pageList) {
            
            domConstruct.empty(_this.linksNode);
            
            array.forEach(pageList, function(page, i) {
               
               if (page.label && page.value)
               {
                  var nodeRef = page.value.replace("://", "/"),
                      uri = Alfresco.constants.URL_PAGECONTEXT + "hdp/ws/rpr/" + nodeRef;
                  domConstruct.create("div", {
                     innerHTML: "<a href='" + uri + "'>" + page.label + "</a>"
                  }, _this.linksNode);
               }
            });
         });
      },
      
      startup: function() {
         this.inherited(arguments);
      }
   });
});