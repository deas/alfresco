script:
{
  var appId = "FB" + facebook.appId;
  var appFolder = companyhome.childByNamePath(appId);
  if (appFolder == undefined)
  {
    status.code = 500;
    status.message = "The Alfresco Repository has not been setup for this application (missing folder \"" + companyhome.name + "/" + appId + "\"). Contact Administrator.";
    status.redirect = true;
    break script;
  }

  var userFolder = appFolder.childByNamePath("FB" + facebook.user);
  if (userFolder == undefined)
  {
    userFolder = appFolder.createFolder("FB" + facebook.user);
    userFolder.save();
  }

  var library = userFolder.childByNamePath(args["name"]);
  if (library == undefined)
  {
    library = userFolder.createFolder(args["name"]);
    library.properties.description = args["desc"];
    library.save();
  }

  model.library = library;
}