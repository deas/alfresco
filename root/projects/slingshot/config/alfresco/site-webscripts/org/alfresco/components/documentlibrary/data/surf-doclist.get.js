<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/data/surf-doclist.lib.js">

function main()
{
   var json = "{}",
      webscript = url.templateArgs.webscript,
      params = url.templateArgs.params,
      dataUrl = "/slingshot/doclib2/" + webscript + "/" + encodeURI(params),
      argsArray = [];

   // Need to reconstruct and encode original args
   if (args.length > 0)
   {
      for (arg in args)
      {
         argsArray.push(arg + "=" + encodeURIComponent(args[arg]));
      }
      
      dataUrl += "?" + argsArray.join("&");
   }
   
   var result = remote.call(dataUrl);
   if (result.status == 200)
   {
      var obj = eval('(' + result + ')');
      if (obj && (obj.item || obj.items))
      {
         DocList.processResult(obj);
         json = jsonUtils.toJSONString(obj);
      }
   }

   model.json = json;
}

main();