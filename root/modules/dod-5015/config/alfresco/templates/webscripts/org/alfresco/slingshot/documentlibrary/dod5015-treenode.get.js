<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/parse-args.lib.js">

/**
 * Document List Component: treenode
 */
model.treenode = getTreenode();

/* Create collection of folders in the given space */
function getTreenode()
{
   try
   {
      var items = new Array(),
         hasSubfolders = true,
         parsedArgs = ParseArgs.getParsedArgs(),
         skipPermissionCheck = args["perms"] == "false",
         evalChildFolders = args["children"] !== "false",
         item, rmNode, capabilities, cap;
   
      // Use helper function to get the arguments
      if (parsedArgs === null)
      {
         return;
      }

      // Quick version if "skipPermissionCheck" flag set
      if (skipPermissionCheck)
      {
         for each (item in parsedArgs.pathNode.children)
         {
            if (itemIsAllowed(item))
            {
               if (evalChildFolders)
               {
                  hasSubfolders = item.childFileFolders(false, true, "fm:forum").length > 0;
               }

               items.push(
               {
                  node: item,
                  hasSubfolders: hasSubfolders
               });
            }
         }
      }
      else
      {
         for each (item in parsedArgs.pathNode.children)
         {
            if (itemIsAllowed(item))
            {
               capabilities = {};
               rmNode = rmService.getRecordsManagementNode(item);
               for each (cap in rmNode.capabilities)
               {
                  capabilities[cap.name] = true;
               }

               if (evalChildFolders)
               {
                  hasSubfolders = item.childFileFolders(false, true, "fm:forum").length > 0;
               }

               items.push(
               {
                  node: item,
                  hasSubfolders: hasSubfolders,
                  permissions:
                  {
                     create: capabilities["Create"]
                  }
               });
            }
         }
      }
   
      items.sort(sortByName);
   
      return (
      {
         parent: parsedArgs.pathNode,
         resultsTrimmed: false,
         items: items
      });
   }
   catch(e)
   {
      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, e.toString());
      return;
   }
}


/* Sort the results by case-insensitive name */
function sortByName(a, b)
{
   return (b.node.name.toLowerCase() > a.node.name.toLowerCase() ? -1 : 1);
}

/* Filter allowed types, etc. */
function itemIsAllowed(item)
{
   if (!item.isSubType("cm:folder"))
   {
      return false;
   }
   
   if (item.typeShort == "rma:hold" || item.typeShort == "rma:transfer")
   {
      return false;
   }
   
   return true;
}