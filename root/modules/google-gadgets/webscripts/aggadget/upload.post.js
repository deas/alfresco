var filename = null;
var content = null;
var title = "";
var description = "";
var fdrnodeid = "";

// locate file attributes
for each (field in formdata.fields)
{
   if (field.name == "title")
   {
      title = field.value;
   }
   else if (field.name == "desc")
   {
      description = field.value;
   }
   else if (field.name == "file" && field.isFile)
   {
      filename = field.filename;
      content = field.content;
   }
   else if (field.name == "fdrnodeid")
   {
      fdrnodeid = field.value;
   }
   else if (field.name == "returl")
   {
      model.returl = field.value;
   }
}

// ensure mandatory file attributes have been located
if (filename == null || content == null)
{
   status.code = 400;
   status.message = "Uploaded file cannot be located in request.";
   status.redirect = true;
}
else
{
   // create document in the passed in folder
   var fdr = search.findNode("workspace://SpacesStore/" + fdrnodeid);
   if (fdr != null) 
   {
      // find a filename without a conflict
      var destname = filename;
      while (fdr.childByNamePath(destname) != null)
      {
         var extIndex = destname.lastIndexOf(".");
         if (extIndex != -1)
         {
            destname = destname.substring(0, extIndex) + "_new" + destname.substring(extIndex);
         }
         else
         {
            destname += "_new";
         }
      }
      upload = fdr.createFile(destname);
      upload.properties.content.write(content);
      upload.properties.content.encoding = "UTF-8";
      
      upload.properties.title = title;
      upload.properties.description = description;
      upload.save();
      
      // setup model for response template
      model.upload = upload;
   }
   else
   {
      status.code = 400;
      status.message = "Error: Upload folder does not exist";
      status.redirect = true;
   }
}