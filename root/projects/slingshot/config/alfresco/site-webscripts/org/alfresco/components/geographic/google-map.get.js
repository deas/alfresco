<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('container', 'documentLibrary');

   var documentDetails = AlfrescoUtil.getDocumentDetails(model.nodeRef, model.site, null);
   if (documentDetails)
   {
      var properties = documentDetails.item.node.properties,
         dTO = properties["exif:dateTimeOriginal"],
         eT = properties["exif:exposureTime"];

      if (dTO)
      {
         properties["exif:dateTimeOriginal"].relativeTime = AlfrescoUtil.relativeTime(dTO.iso8601);
      }
      if (eT)
      {
         if (parseInt(eT * 1000, 10) > 0)
         {
            properties["exif:exposureTime"] = "1/" + Math.ceil(1/eT);
         }
      }
      model.document = documentDetails.item;
      model.documentDetailsJSON = jsonUtils.toJSONString(documentDetails);
      doclibCommon();
   }
}

main();