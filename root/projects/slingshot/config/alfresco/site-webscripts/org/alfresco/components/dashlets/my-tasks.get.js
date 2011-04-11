<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
model.hiddenTaskTypes = getHiddenTaskTypes();

var myConfig = new XML(config.script),
   filters = [];

for each(var xmlFilter in myConfig..filter)
{
   filters.push(
   {
      type: xmlFilter.@type.toString(),
      parameters: xmlFilter.@parameters.toString()
   });
}
model.filters = filters;

model.maxItems = getMaxItems();