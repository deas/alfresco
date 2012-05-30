function sortByTitle(list1, list2)
{
   return (list1.title > list2.title) ? 1 : (list1.title < list2.title) ? -1 : 0;
}

function main()
{
   var site = page.url.templateArgs.site,
      theUrl = "/slingshot/datalists/lists/site/" + site + "/dataLists?page=1&pageSize=512",
      result = remote.call(theUrl),
      canCreate = false,
      lists = [];
   
   if (result.status == 200)
   {
      response = eval('(' + result.response + ')');
      lists = response.datalists;
      lists.sort(sortByTitle);
      canCreate = response.permissions.create;
   }

   model.lists = lists;
   model.canCreate = canCreate;
   
   // Widget instantiation metadata...
   model.webScriptWidgets = [];

   var dashletResizer = {};
   dashletResizer.name = "Alfresco.widget.DashletResizer";
   dashletResizer.instantiationArguments = [];
   dashletResizer.instantiationArguments.push("\"" + args.htmlid + "\"");
   dashletResizer.instantiationArguments.push("\"" + instance.object.id + "\"");
   model.webScriptWidgets.push(dashletResizer);

   var dashletTitleBarActions = {};
   dashletTitleBarActions.name = "Alfresco.widget.DashletTitleBarActions";
   dashletTitleBarActions.provideOptions = true;
   dashletTitleBarActions.provideMessages = false;
   dashletTitleBarActions.options = {};
   dashletTitleBarActions.options.actions = [];
   dashletTitleBarActions.options.actions.push({
      cssClass: "help",
      bubbleOnClick:
      {
         message: msg.get("dashlet.help")
      },
      tooltip: msg.get("dashlet.help.tooltip")
   });

   model.webScriptWidgets.push(dashletTitleBarActions);
}

main();