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

  var userFolder = appFolder.childByNamePath("FB" + url.extension);
  if (userFolder == undefined)
  {
    if (parseInt(facebook.user) != parseInt(url.extension))
    {
      status.code = 500;
      status.message = "User " + url.extension + " does not have a document libraries folder";
      status.redirect = true;
      break script;
    }
    
    userFolder = appFolder.createFolder("FB" + facebook.user);
  }

  model.userFolder = userFolder; 
}