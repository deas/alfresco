define(["dojo/_base/declare",
        "alfresco/core/ProcessWidgets",
        "dojo/text!./templates/LeftAndRight.html",
        "dojo/dom-construct",
        "dojo/_base/array"], 
        function(declare,ProcessWidgets, template, domConstruct, array) {
   
   return declare([ProcessWidgets], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/LeftAndRight.css",mediaType:"screen"}],
      
      /**
       * The HTML template to use for the widget.
       * @property template {String}
       */
      templateString: template,
      
      /**
       * Iterates through the array of widgets to be created and creates the appropriate DOM node based
       * on the "align" attribute of the widget configuration. 
       * 
       * @method postCreate
       */
      postCreate: function alfresco_layout_LeftAndRight__postCreate() {
         var _this = this;
         if (this.widgets)
         {
            // Iterate over all the widgets in the configuration object and add them...
            array.forEach(this.widgets, function(entry, i) {
               var domNode = null;
               if (entry.align == "right")
               {
                  domNode = _this.createWidgetDomNode(entry, _this.rightWidgets, entry.className);
               }
               else
               {
                  domNode = _this.createWidgetDomNode(entry, _this.leftWidgets, entry.className);
               }
               _this.createWidget(entry.name, entry.config, domNode);
            });
         }
      },
      
      /**
       * Overrides the default implementation to ensure that the DOM node created has the appropriate CSS
       * classes applied such that they are aligned appropriately.
       * 
       * @method createWidgetDomNode
       * @param {object} widget The configuration for the widget to create a new DOM node for.
       * @param {element} rootNode The DOM node to add the new DOM node as a child of
       * @param {string} rootClassName The CSS class (or space separated list of CSS classes) to be applied to the DOM node
       */
      createWidgetDomNode: function alfresco_layout_LeftAndRight__createWidgetDomNode(widget, rootNode, rootClassName) {
         var className = (rootClassName) ? rootClassName + " horizontal-widget" : "horizontal-widget";
         var outerDiv = domConstruct.create("div", { className: className}, rootNode);
         var innerDiv = domConstruct.create("div", {}, outerDiv);
         return innerDiv;
      }
   });
});