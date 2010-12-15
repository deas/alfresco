/*
  Alfresco Extension 1.0
*/

var alfrescoext =
{

  loaded: null,
  currentversion: "1.0",

  init: function () 
  {
    if (alfrescoext.loaded)
      return; 
    else
    {
      alfrescoext.loaded = true;
    }
    getBrowser().addEventListener("mousedown", alfrescoext.mousedown, true);
  },

  mousedown: function (aEvent)
  {
    var linkHref, linkClick;

    if (!aEvent)
      return;
    if (aEvent.button != 0)
      return;

    if (aEvent.target)
      var targ = aEvent.originalTarget;
  
    if (targ.tagName.toUpperCase() != "A")
    {
      // Recurse until reaching root node
      while (targ.parentNode) 
      {
        targ = targ.parentNode;
        // stop if an anchor is located
        if (targ.tagName && targ.tagName.toUpperCase() == "A")
        break;
      }
      if (!targ.tagName || targ.tagName.toUpperCase() != "A")
        return;
    }

    linkHref = targ.getAttribute("href");
    if (linkHref)
    {
      if (linkHref.substring(0,5) == "file:")
      {
         try {
            var len = 1;
            var exargs = new Array();

            var lfile = Components.classes['@mozilla.org/file/local;1'].createInstance(Components.interfaces.nsILocalFile);

            // Find path for filemgr exe
            var prefservice = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService);
            var prefs = prefservice.getBranch("");
            var exloc = null;
            var exfil = true;

            if (prefs.getPrefType("alfrescoext.exapp") == prefs.PREF_STRING)
            {
              exloc = prefs.getCharPref("alfrescoext.exapp");
            }
            if (exloc == null || exloc.length == 0) exloc = "c:\\windows\\explorer.exe";

            if (prefs.getPrefType("alfrescoext.exfil") == prefs.PREF_BOOL)
            {
               exfil = prefs.getBoolPref("alfrescoext.exfil");
            }
            // strip file:// if required
            if (!exfil) linkHref = linkHref.substring(8);

            //alert("Calling: " + exloc + " With path: " + linkHref);
            // Try launching
            lfile.initWithPath(exloc);

            if (lfile.isFile() && lfile.isExecutable()) {
               try {
                  var process = Components.classes['@mozilla.org/process/util;1'].createInstance(Components.interfaces.nsIProcess);
                  process.init(lfile);
                  exargs[0] = linkHref;
                  exargs = process.run(false, exargs, len);
                  return false;
               } catch (e) {
                  // foobar!
               }
            }
            else {
               alert("Unable to find explorer, please check current options");
            }
         }
         catch(e) {
            alert("Unable to find explorer, please check current options");
            // foobar!
         }
       }
     }
   }, /* end mousedown */

   setOptions: function()
   {
      var prefService = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService);
      var prefs = prefService.getBranch("");

      prefs.setCharPref("alfrescoext.exapp", document.getElementById('exloc').value);
      prefs.setBoolPref("alfrescoext.exfil", document.getElementById('exfil').checked);
      //window.close();
      return true;
   },   /* end setOptions */

   initOptions: function()
   {
       var prefservice = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService);
       var prefs = prefservice.getBranch("");
       var exloc = null;
       var exfil = true;

       if (prefs.getPrefType("alfrescoext.exapp") == prefs.PREF_STRING)
       {
         exloc = prefs.getCharPref("alfrescoext.exapp");
       }

       if ((exloc != null) && (exloc.length > 0))
       {
           document.getElementById('exloc').value = exloc;
       }

       if (prefs.getPrefType("alfrescoext.exfil") == prefs.PREF_BOOL)
       {
         exfil = prefs.getBoolPref("alfrescoext.exfil");
       }
       document.getElementById('exfil').checked = exfil;

       return true;
   } /* end initOptions */

}

window.addEventListener("load",alfrescoext.init,false);
