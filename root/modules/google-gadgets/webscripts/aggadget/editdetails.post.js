var name = null;
var title = null;
var description = null;
var author = null;
var id = null;

// locate node attributes
for each (field in formdata.fields)
{
   if (field.name == "name")
   {
      name = field.value;
   }
   else if (field.name == "title")
   {
      title = field.value;
   }
   else if (field.name == "desc")
   {
      description = field.value;
   }
   else if (field.name == "author")
   {
      author = field.value;
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

// ensure mandatory file attributes have been located
if (name == null)
{
   status.code = 400;
   status.message = "Missing node name in request arguments.";
   status.redirect = true;
}
else
{
   // set the modified node properties
   var node = search.findNode("workspace://SpacesStore/" + id);
   if (node != null) 
   {
      if (node.name != name)
      {
         // forces a rename operation
         node.name = name;
      }
      node.properties.title = title;
      node.properties.description = description;
      if (author != null)
      {
         node.properties.author = author;
      }
      node.save();
      
      // setup model for response template
      model.node = node;
   }
   else
   {
      status.code = 400;
      status.message = "Error: Node no longer exists";
      status.redirect = true;
   }
}