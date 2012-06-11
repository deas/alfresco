model.webScriptWidgets = [];

var dataLoader = {};
dataLoader.name = "Alfresco.DataLoader";
dataLoader.provideOptions = true;
dataLoader.provideMessages = true;
dataLoader.options = {};
dataLoader.options.url = (args.url != null) ? args.url : "";
if (args.eventData != null)
{
   dataLoader.options.eventData = args.eventData;
}
if (args.useProxy != null)
{
   dataLoader.options.useProxy = args.useProxy;
}
if (args.failureMessageKey != null)
{
   dataLoader.options.failureMessageKey = args.failureMessageKey;
}
model.webScriptWidgets.push(dataLoader);