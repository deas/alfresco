// return an array of tool information
var toolInfo = [];

// the current tool may have been specified on the URL
var currentToolId = page.url.templateArgs["toolid"];

// family of tools to use for this console is linked to the current pageId from the URL
var family = page.url.templateArgs["pageid"];
if (family != null)
{
   // find the existing current tool component binding
   var component = sitedata.getComponent("page", "tool", family);
   
   // collect the tools required for this console
   var tools = sitedata.findWebScripts(family);
   
   // process each tool and generate the data so that a label+link can
   // be output by the component template for each tool required
   for (var i = 0; i < tools.length; i++)
   {
      var tool = tools[i],
         id = tool.id,
         scriptName = id.substring(id.lastIndexOf('/') + 1, id.lastIndexOf('.')),
         toolUrl = (new String(tool.getURIs()[0])).toString();
      
      // handle the case when no tool selection in the URL - select the first
      if (currentToolId.length == 0)
      {
         currentToolId = scriptName;
      }
      
      // use the webscript ID to generate message bundle IDs
      var labelId = "tool." + scriptName + ".label";
      var descId = "tool." + scriptName + ".description";
      
      // generate the tool info structure for template usage
      toolInfo[i] =
      {
         id: scriptName,
         url: toolUrl,
         label: labelId,
         description: descId,
         selected: (currentToolId == scriptName)
      };
      
      // dynamically update the component binding if this tool is the current selection
      if (toolInfo[i].selected)
      {
         if (component == null)
         {
            // first ever visit to the page - there is no component binding yet
            component = sitedata.newComponent("page", "tool", family);
         }

         if (component.properties.url != toolUrl)
         {
            component.properties.url = toolUrl;
            component.save();
         }
      }
   }
}

// Save the tool info structure into the request context, it is used
// downstream by the console-tools component to dynamically render tool links.
// Processing is performed here as the component binding must be set before rendering begins!
context.setValue("console-tools", toolInfo);