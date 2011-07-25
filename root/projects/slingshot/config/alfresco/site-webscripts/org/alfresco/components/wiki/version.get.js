
function main()
{
   // retrieve wiki page if a specific version
   var uri = "/slingshot/wiki/version/" + url.templateArgs.siteId + "/" + encodeURIComponent(url.templateArgs.pageTitle) + "/" + url.templateArgs.versionId;
   var connector = remote.connect("alfresco");
   var result = connector.get(encodeURI(uri));
   if (result.status.code == status.STATUS_OK)
   {
      // Strip out possible malicious code
      return stringUtils.stripUnsafeHTML(result.response);
   }
   else
   {
      return msg.get("message.failure");
   }
}

model.content = main();