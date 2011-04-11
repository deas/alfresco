function getDod5015DocumentDetails(nodeRef, site, defaultValue)
{
   var result = remote.connect("alfresco").get('/slingshot/doclib/dod5015/node/' + nodeRef.replace('://', '/'));
   if (result.status != 200)
   {
      if (defaultValue !== undefined)
      {
         return defaultValue;
      }
      AlfrescoUtil.error(result.status, 'Could not load dod5015 document details for ' + nodeRef);
   }
   return eval('(' + result + ')');
}

