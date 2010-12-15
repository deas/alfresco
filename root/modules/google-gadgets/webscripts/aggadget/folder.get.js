// location the node as specified by the path argument
var path = null;
var home = userhome;
if (url.extension != null)
{
   path = '/' + url.extension;
}
// resolve the path (from Company Home) into a node or fall back to userhome
if (path[path.length-1] == '/')
{
   // strip trailing slash
   path = path.substring(0, path.length-2);
}
if (path.length != 0)
{
   path = path.substring(companyhome.name.length() + 2);
   if (path.length != 0)
   {
      home = companyhome.childByNamePath(path);
      if (home == null || !home.isContainer)
      {
         logger.log("Unable to find folder path: " + path);
         home = userhome;
      }
   }
   else
   {
      home = companyhome;
   }
}
path = home.displayPath + "/" + home.name;

// save values in model passed to template
model.path = path;
model.folder = home;

// get user prefs from url args
model.sortby = "name";  // defaults
model.up_sortby = "Name";
var sortby = args["up_sortby"];
if (sortby != null)
{
   if (sortby == "Name") model.sortby = "name";
   if (sortby == "Date") model.sortby = "cm:modified";
   if (sortby == "Size") model.sortby = "size";
   model.up_sortby = sortby;
}

model.filter = 0;
if (args["f"] != null)
{
   model.filter = Number(args["f"]);
}

model.mode = 0;
if (args["m"] != null)
{
   model.mode = Number(args["m"]);
}