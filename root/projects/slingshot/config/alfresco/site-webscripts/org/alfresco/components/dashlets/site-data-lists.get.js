function sortByTitle(list1, list2)
{
   return (list1.title > list2.title) ? 1 : (list1.title < list2.title) ? -1 : 0;
}

function main()
{
   var site, container, theUrl, connector, result, lists;
   
   site = page.url.templateArgs.site;
   container = 'dataLists';
   theUrl = '/slingshot/datalists/lists/site/' + site + '/' + container + '?page=1&pageSize=512';
   connector = remote.connect("alfresco");
   result = connector.get(theUrl);
   if (result.status == 200)
   {
      response = eval('(' + result.response + ')');
      lists = response.datalists;
      lists.sort(sortByTitle);
      model.lists = lists;
      model.create = response.permissions.create;
      model.numLists = lists.length;
   }
}

main();