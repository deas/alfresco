/**
 * Customise Site Layout component GET method
 */

function getNoOfColumns(template)
{
   var noOfColumns = 0;
   while (template.properties["gridColumn" + (noOfColumns + 1)] !== null)
   {
      noOfColumns++;
   }                                       
   return noOfColumns;
}

function main()
{
   // Get current template
   var dashboardId;
   if (args.dashboardType == "user")
   {                              
      dashboardId = "user/" + user.name + "/dashboard";
   }
   else if (args.dashboardType == "site")
   {
      dashboardId = "site/" + page.url.templateArgs.site + "/dashboard";   
   }
   
   // Hardcoded templates until proper service exists
   var layouts = [
      {templateId: "dashboard-1-column",             noOfColumns: 1, description: msg.get("msg.template-1-column")},
      {templateId: "dashboard-2-columns-wide-right", noOfColumns: 2, description: msg.get("msg.template-2-columns-wide-right")},
      {templateId: "dashboard-2-columns-wide-left",  noOfColumns: 2, description: msg.get("msg.template-2-columns-wide-left")},
      {templateId: "dashboard-3-columns",            noOfColumns: 3, description: msg.get("msg.template-3-columns")},
      {templateId: "dashboard-4-columns",            noOfColumns: 4, description: msg.get("msg.template-4-columns")}
   ];
   
   var currentTemplate = sitedata.findTemplate(dashboardId),
      currentNoOfColumns = getNoOfColumns(currentTemplate),
      currentTemplateDescription = "NONE";
   
   for (var i=0; i<layouts.length; i++)
   {
      if (layouts[i].templateId == currentTemplate.id)
      {
         currentTemplateDescription = layouts[i].description;
      }
   }
   
   var currentLayout =
      {
         templateId: currentTemplate.id,
         noOfColumns: currentNoOfColumns,
         description: currentTemplateDescription
      };
   
   // Prepare model for template
   model.currentLayout = currentLayout;
   model.layouts = layouts;
}

main();