function getDocuments(site,container,filter,amount)
{
    var uri = '/slingshot/doclib/doclist/documents/site/'+site+'/'+container+'/?filter='+filter+'&size='+amount;
    var data  = remote.call(uri);
    var data = eval('('+ data+')');

    var imgTypes = 'png,gif,jpg,jpeg,tiff,bmp';
    for (var i=0,len=data.items.length;i<len;i++)
    {
      var doc = data.items[i];
      doc.modifiedOn = new Date(doc.modifiedOn);
      doc.createdOn = new Date(doc.createdOn);

      var type = doc.mimetype.split('/')[1];
      if (imgTypes.indexOf(type)!=-1)
      {
        doc.type = 'img';
      }
      else if (type == 'pdf')
      {
        doc.type = 'pdf';
      }
      else if (type == 'msword')
      {
        doc.type = 'doc';
      }
      else if (type == 'msexcel')
      {
        doc.type = 'xls';
      }      
      else if (type == 'mspowerpoint')
      {
        doc.type = 'ppt';
      }
      else {
        doc.type = 'unknown';
      }
      //make valid dom id using docTitle - prob needs fixing for unicode characters
      doc.domId = doc.displayName.replace(/ /g,'');
      data.items[i]=doc;
    }

    return data;
}

function getUserTasks(filter)
{
  var filter = filter || 'all';
  var data = remote.call("/slingshot/dashlets/my-tasks?filter=" + filter);
  data = eval('(' + data + ')');
  return data;
}

function getUserEvents()
{
  var data = remote.call("/calendar/events/user");
  return eval('(' + data + ')');
}

const PREF_FAVOURITE_SITES = "org.alfresco.share.sites.favourites";

function getAllSites()
{
  var data  = remote.call("/api/sites");
  return eval('('+ data+')');
}

function getUserSites(u)
{
   var userName = u || user.name;
   // Call the repo for sites the user is a member of
   var result = remote.call("/api/people/" + stringUtils.urlEncode(userName) + "/sites");
   if (result.status == 200)
   {
      var i, ii, j, jj;

      // Create javascript objects from the server response
      var sites = eval('(' + result + ')'), site, favourites = {}, userfavs = [];

      if (sites.length > 0)
      {
         // Call the repo for the user's favourite sites
         result = remote.call("/api/people/" + stringUtils.urlEncode(userName) + "/preferences?pf=" + PREF_FAVOURITE_SITES);
         if (result.status == 200 && result != "{}")
         {
            var prefs = eval('(' + result + ')');
            
            // Populate the favourites object literal for easy look-up later
            favourites = eval('(prefs.' + PREF_FAVOURITE_SITES + ')');
            if (typeof favourites != "object")
            {
               favourites = {};
            }
         }

         for (i = 0, ii = sites.length; i < ii; i++)
         {
            site = sites[i];
            
            // Is this site a user favourite?
            if (favourites[site.shortName]) 
            {
              site.isFavourite = true;
              userfavs.push(site);
            }
            site.isFavourite = !!(favourites[site.shortName]);
         }
         var userSites =
         {
            sites : sites,
            favSites: userfavs
         };
         return userSites;
      }
      return {
         sites:[],
         favSites:[]
      };
   }
};

/**
 * Generate URL to thumbnail image
 *
 * @method generateThumbnailUrl
 * @param path {YAHOO.widget.Record} File record
 * @return {string} URL to thumbnail
 */
var generateThumbnailUrl = function generateThumbnailUrl(record)
{
   return "api/node/" + record.nodeRef.replace(":/", "") + "/content/thumbnails/doclib?c=queue&ph=true";
};