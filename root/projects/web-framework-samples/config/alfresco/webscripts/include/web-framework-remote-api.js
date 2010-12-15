/**
 * POST call
 */
function doPostCall(url, paramsJSON)
{
   var connector = remote.connect("alfresco");
   var result = connector.post(url, paramsJSON, "application/json");
   if (result.status != status.STATUS_OK)
   {
      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Unable to do backend call. " +
                     "status: " + result.status + ", response: " + result.response);
      return null;
   }
   return eval('(' + result.response + ')');
};


/**
 * PUT call
 */
function doPutCall(url, paramsJSON)
{
   var connector = remote.connect("alfresco");
   var result = connector.put(url, paramsJSON, "application/json");
   if (result.status != status.STATUS_OK)
   {
      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Unable to do backend call. " +
                     "status: " + result.status + ", response: " + result.response);
      return null;
   }
   return eval('(' + result.response + ')');
};


/**
 * GET call
 */
function doGetCall(url)
{
   var connector = remote.connect("alfresco");
   var result = connector.get(url);
   if (result.status != status.STATUS_OK)
   {
      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Unable to do backend call. " +
                     "status: " + result.status + ", response: " + result.response);
      return null;
   }
   return eval('(' + result.response + ')');
};
