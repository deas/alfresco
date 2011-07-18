<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

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

model.preferences = AlfrescoUtil.getPreferences("org.alfresco.share.docsummary.dashlet");
model.maxItems = getMaxItems();
