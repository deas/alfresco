<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

/* Get filters */
function getFilters()
{
   var myConfig = new XML(config.script),
      filters = [];

   for each (var xmlFilter in myConfig..filter)
   {
      filters.push(
      {
         type: xmlFilter.@type.toString(),
         parameters: xmlFilter.@parameters.toString()
      });
   }
   return filters
}

/* Max Items */
function getMaxItems()
{
   var myConfig = new XML(config.script),
      maxItems = myConfig["max-items"];

   if (maxItems)
   {
      maxItems = myConfig["max-items"].toString();
   }
   return parseInt(maxItems && maxItems.length > 0 ? maxItems : 50, 10);
}

model.preferences = AlfrescoUtil.getPreferences("org.alfresco.share.mydocuments.dashlet");
model.filters = getFilters();
model.maxItems = getMaxItems();