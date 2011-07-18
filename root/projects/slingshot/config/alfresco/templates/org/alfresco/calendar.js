<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/calendar/enabledViews.js">

/**
 * Calendar template controller script
 * 
 * This script is utilised so that all page components can access the filtered 'view' parameter.
 * 
 */

var filteredView = function()
{
   var view = escape(page.url.args["view"]);
   if (typeof(model.enabledViews) != "undefined" && !model.enabledViews[view]) 
   {
      return model.defaultView
   }
   return view
}()

context.setValue("filteredView",filteredView);
