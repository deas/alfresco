// ensure mandatory attributes have been located
if (args.id == null)
{
   status.code = 400;
   status.message = "Missing node ID in request arguments.";
   status.redirect = true;
}
else
{
   // find the item in the repository
   var node = search.findNode("workspace://SpacesStore/" + args.id);
   if (node != null) 
   {
      // setup model for response template
      model.node = node;
   }
   else
   {
      status.code = 400;
      status.message = "Error: Node for details page does not exist";
      status.redirect = true;
   }
}