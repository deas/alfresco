var name = null;
var title = "";
var description = "";
var fdrnodeid = "";

// locate folder attributes
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
if (name == null)
{
   status.code = 400;
   status.message = "Missing folder name in request arguments.";
   status.redirect = true;
}
else
{
   // create new folder within the passed in folder
   var fdr = search.findNode("workspace://SpacesStore/" + fdrnodeid);
   if (fdr != null) 
   {
      // find a folder name without a conflict
      var destname = name;
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
      folder = fdr.createFolder(destname);
      folder.properties.title = title;
      folder.properties.description = description;
      folder.save();
      
      // setup model for response template
      model.folder = folder;
   }
   else
   {
      status.code = 400;
      status.message = "Error: Parent folder does not exist";
      status.redirect = true;
   }
}