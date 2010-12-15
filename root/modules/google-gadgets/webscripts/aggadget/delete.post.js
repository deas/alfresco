var name = null;
var id = null;

// locate folder attributes
for each (field in formdata.fields)
{
   if (field.name == "name")
   {
      name = field.value;
   }
   else if (field.name == "id")
   {
      id = field.value;
   }
   else if (field.name == "returl")
   {
      model.returl = field.value;
   }
}

// ensure mandatory attributes have been located
if (id == null)
{
   status.code = 400;
   status.message = "Missing node ID in request arguments.";
   status.redirect = true;
}
else
{
   // delete the specified item from the repository
   var node = search.findNode("workspace://SpacesStore/" + id);
   if (node != null) 
   {
      node.remove();
      
      // setup model for response template
      model.name = name;
   }
   else
   {
      status.code = 400;
      status.message = "Error: Item for deletion does not exist";
      status.redirect = true;
   }
}