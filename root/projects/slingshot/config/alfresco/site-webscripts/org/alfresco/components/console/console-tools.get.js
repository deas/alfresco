// get the tool info from the request context - as supplied by the console template script
var toolInfo = context.properties["console-tools"];

// resolve the message labels
for (var i = 0; i < toolInfo.length; i++)
{
   toolInfo[i].label = msg.get(toolInfo[i].label);
   toolInfo[i].description = msg.get(toolInfo[i].description);
}

model.tools = toolInfo;