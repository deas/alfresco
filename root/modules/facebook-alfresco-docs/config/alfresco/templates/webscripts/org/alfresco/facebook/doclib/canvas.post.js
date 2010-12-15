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

  var recentDocs = new Array();
  if (facebook.friends.length > 0)
  {
    // build the query
    var recentDocsQuery = "";
    for each (friend in facebook.appFriends)
    {
      var userFolder = appFolder.childByNamePath("FB" + friend);
      if (userFolder != undefined)
      {
        if (recentDocsQuery == "")
        {
          recentDocsQuery = "TYPE:\"{http://www.alfresco.org/model/content/1.0}content\" AND ";
        }
        recentDocsQuery += "PATH:\"" + userFolder.qnamePath + "//*\" ";
      } 
    }
    
    // only execute query, if we need to
    if (recentDocsQuery != "")
    {
      recentDocs = search.luceneSearch(recentDocsQuery, "@cm:modified", false);
    }
  }

  model.recentDocs = recentDocs.splice(0, Math.min(recentDocs.length, 10));
}
