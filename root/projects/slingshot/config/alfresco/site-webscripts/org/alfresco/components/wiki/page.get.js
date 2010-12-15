<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function sortByLabel(version1, version2)
{
   var major1 = new Number(version1.version.substring(0, version1.version.indexOf(".")));
   var major2 = new Number(version2.version.substring(0, version2.version.indexOf(".")));
   if(major1 - 0 == major2 - 0)
   {
        var minor1 = new Number(version1.version.substring(version1.version.indexOf(".")+1));
        var minor2 = new Number(version2.version.substring(version2.version.indexOf(".")+1));
        return (minor1 < minor2) ? 1 : (minor1 > minor2) ? -1 : 0;
   }
   else
   {
       return (major1 < major2) ? 1 : -1;
   }
}

function main()
{
   var title = page.url.args.title;
   if (title)
   {
      var context = page.url.context + "/page/site/" + page.url.templateArgs.site + "/wiki-page?title=" + page.url.args.title,
         uri = "/slingshot/wiki/page/" + encodeURIComponent(page.url.templateArgs.site) + "/" + encodeURIComponent(page.url.args.title) + "?context=" + escape(context),
         connector = remote.connect("alfresco"),
         result = connector.get(uri);
      
      // we allow 200 and 404 as valid responses - any other error then cannot show page
      // the 404 response means we can create a new page for the title
      if (result.status.code == status.STATUS_OK || result.status.code == status.STATUS_NOT_FOUND)
      {
         var response = eval('(' + result.response + ')'),
            myConfig = new XML(config.script);
         
         if (response.pagetext)
         {
            response.pagetext = myConfig.allowUnfilteredHTML == true ? response.pagetext : stringUtils.stripUnsafeHTML(response.pagetext);
         }
         if (response.versionhistory != undefined)
         {
            response.versionhistory.sort(sortByLabel);
         }
         model.result = response;
      }
      else
      {
         model.result = {"pagetext" : null};
      }
   }
   else
   {
      status.redirect = true;
      status.code = 301;
      status.location = page.url.service + "?title=Main_Page";
   }
}
 
main();