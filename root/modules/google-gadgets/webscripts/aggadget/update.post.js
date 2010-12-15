var content = null;
var id = null;

// locate file attributes
for each (field in formdata.fields)
{
   if (field.name == "id")
   {
      id = field.value;
   }
   else if (field.name == "file" && field.isFile)
   {
      content = field.content;
   }
   else if (field.name == "returl")
   {
      model.returl = field.value;
   }
}

// ensure mandatory file attributes have been located
if (content == null)
{
   status.code = 400;
   status.message = "Uploaded file cannot be located in request.";
   status.redirect = true;
}
else
{
   // update document with uploaded content
   var document = search.findNode("workspace://SpacesStore/" + id);
   if (document != null) 
   {
      document.properties.content.write(content);
      document.properties.content.encoding = "UTF-8";
      document.save();
      
      // setup model for response template
      model.document = document;
   }
   else
   {
      status.code = 400;
      status.message = "Error: File for update does not exist";
      status.redirect = true;
   }
}