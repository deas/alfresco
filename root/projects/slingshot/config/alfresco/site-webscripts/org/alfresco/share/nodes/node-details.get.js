var nodeRefUri;
if (url.templateArgs.store_type == null || url.templateArgs.store_id == null || url.templateArgs.id == null)
{
   // If the WebScript is called from within the context of a page (it has been designed to be called from either
   // the full-page.get.html.ftl or hybrid-page.get.html.ftl) then the store_type, store_id and id templateArgs
   // will be null (but WILL be included in the "webscript" templateArg.
   
   // NOTE: This assumes that "node-details" is the prefix to the WebScript - if this changes then the substring call will need to be updated
   var tmp = url.templateArgs.webscript;
   nodeRefUri = tmp.substring(tmp.indexOf("/") + 1);
}
else
{
   // If the WebScript is called directly then the explicit templateArgs should be passed...
   nodeRefUri = url.templateArgs.store_type + "/" + url.templateArgs.store_id +"/" + url.templateArgs.id;
}

var uri = "/slingshot/doclib2/node/" + nodeRefUri;
var nodeData = {},
    json = remote.call(uri);
if (json.status == 200)
{
   nodeData = eval('(' + json + ')');
}

model.jsonModel = {
   services: [
      "alfresco/services/RatingsService"
   ],
   widgets: [
      {
         name: "alfresco/renderers/InlineEditProperty",
         config: {
            currentItem: nodeData.item,
            propertyToRender: "cm:name"
         }
      },
      {
         name: "alfresco/renderers/Like",
         config: {
            currentItem: nodeData.item
         }
      }
   ]
};
