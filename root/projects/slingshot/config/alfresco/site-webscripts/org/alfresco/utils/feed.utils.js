/**
 * Controls how many items are displayed at any one time in the RSS dashlet.
 * Defaults to a large number, the theory being is that you aren't going to get 9999 items
 * in any RSS feed.
 */
const DISPLAY_ITEMS = 999;

/**
 * Takes a URL of an RSS feed and returns an rss object
 * with title and an array of items in the feed.
 *
 * @param uri {String} the uri of the RSS feed
 */
function getRSSFeed(uri, limit)
{
   var re = /^(http|https):\/\//;
   if (!re.test(uri))
   {
      uri = "http://" + uri;
   }

   limit = limit || DISPLAY_ITEMS;

   // We only handle "http" connections for the time being
   var connector = remote.connect("http");
   var result = connector.call(uri);

   if (result !== null && result.status == 200)
   {
      var rssXml = new String(result),
         rss;

      // Prepare string for E4X
      rssXml = prepareForE4X(rssXml);

      // Find out what type of feed
      try
      {
         rss = new XML(rssXml);
         if (rss.name().localName.toLowerCase() == "rss")
         {
             return parseRssFeed(rss, rssXml, limit);
         }
         else if(rss.name().localName.toLowerCase() == "feed")
         {
             return parseAtomFeed(rss, rssXml, limit);
         }
      }
      catch (e)
      {
         return {
            error: "bad_data"
         };
      }
   }
   else
   {
      return {
         error: "unavailable"
      };
   }
}

/**
 * Removes leading and trainling whitespace from str
 *
 * @param str {string} String that will be trimmed
 * @return {string} A trimmed string
 */
function trim(str)
{
   return str ? str.replace(/^\s+|\s+$/g, "") : null;
}

/**
 * Takes am xml string and prepares it for E4X
 *
 * @param xmlStr {string} An string representing an xml document
 * @return {string} An E4X compatible string
 */
function prepareForE4X(xmlStr)
{
   // Trim
   if (xmlStr)
   {
      xmlStr = trim(xmlStr)
   }
   else
   {
      return xmlStr;
   }

   /**
    * Strip out:
    * - any processing instructions in the beginning so E4X will work
    * - any comment blocks in the end so E4X won't complain about multiple top nodes
    */
   var filters = [
      {
         start: "<!--",
         end: "-->",
         after: true
      },
      {
         start: "<?",
         end: "?>",
         before: true
      }
   ];
   var filter;
   for (var i = 0, il = filters.length; i < il; i++)
   {
      filter = filters[i];
      if(filter.before)
      {
         while (xmlStr.indexOf(filter.start) == 0)
         {
            xmlStr = trim(xmlStr.substring(xmlStr.indexOf(filter.end) + filter.end.length));
         }
      }
      if(filter.after)
      {
         while (xmlStr.lastIndexOf(filter.end) == (xmlStr.length - filter.end.length))
         {
            xmlStr = trim(xmlStr.substring(0, xmlStr.lastIndexOf(filter.start)));
         }
      }
   }

   return xmlStr;
}

/**
 * Takes a rss feed string and returns feed object
 *
 * @param rss {XML} represents an Rss feed
 * @param rssStr {String} represents an Rss feed
 * @param limit {int} The maximum number of items to display
 * @return {object} A feed object with title and items
 */
function parseRssFeed(rss, rssStr, limit)
{

   /**
    * We do this (dynamically) as some feeds, e.g. the BBC, leave the trailing slash
    * off the end of the Yahoo Media namespace! Technically this is wrong but what to do.
    */
   var mediaRe = /xmlns\:media="([^"]+)"/;
   var hasMediaExtension = mediaRe.test(rssStr);
          
   if (hasMediaExtension)
   {
      var result = mediaRe.exec(rssStr);
      // The default (correct) namespace should be 'http://search.yahoo.com/mrss/'
      var media = new Namespace( result[1] );
      var fileext = /([^\/]+)$/;
   }

   var items = [],
      item,
      obj,
      count = 0;
   for each (item in rss.channel..item)
   {
      if (count >= limit)
      {
         break;
      }
    		   
      obj =
      {
         "title": item.title.toString(),
         "description": item.description.toString(),
         "link": item.link.toString()
      };
            
      if (hasMediaExtension)
      {
         // We only look for the thumbnail as a direct child in RSS
         var thumbnail = item.media::thumbnail;
         if (thumbnail && thumbnail.@url.toString())
         {
            obj["image"] = thumbnail.@url.toString();
         }

         var attachment = item.media::content;
         if (attachment)
         {
            var contenturl = attachment.@url.toString();
            if (contenturl.length > 0)
            {
               var filename = fileext.exec(contenturl)[0];
               // Use the file extension to figure out what type it is for now
               var ext = filename.split(".");

               obj["attachment"] =
               {
                  "url": contenturl,
                  "name": filename,
                  "type": (ext[1] ? ext[1] : "_default")
               };
            }
         }
      }
    		  
      items.push(obj);
      ++count;
   }

   return {
      title: rss.channel.title.toString(),
      items: items
   };
}

/**
 * Takes an atom feed and returns an array of entries.
 *
 * @param atom {XML} represents an Atom feed
 * @param atomStr {String} represents an Atom feed
 * @param limit {int} The maximum number of items to display
 * @return {object} A feed object with title and items
 */
function parseAtomFeed(atom, atomStr, limit)
{
   // Recreate the xml with default namespace
   default xml namespace = new Namespace("http://www.w3.org/2005/Atom");
   atom = new XML(atomStr);

   // Do we have the media extensions such as thumbnails?
   var mediaRe = /xmlns\:media="([^"]+)"/;
   var hasMediaExtension = mediaRe.test(atomStr);
   if(hasMediaExtension)
   {
      var media = new Namespace("http://search.yahoo.com/mrss/");
   }

   var items = [],
      entry,
      link,
      obj,
      count = 0;
   for each (entry in atom.entry)
   {
      if (count >= limit)
      {
         break;
      }

      obj = {
   		"title": entry.title.toString(),
   		"description": entry.summary.toString().replace(/(target=)/g, "rel="),
         "link": entry.link[0] ? entry.link[0].@href.toString() : null
   	};
     
      if (hasMediaExtension)
      {
         // In Atom, it could be a direct child
         var thumbnail = entry.media::thumbnail;
         if (thumbnail && thumbnail.@url.toString())
         {
            obj["image"] = thumbnail.@url.toString();
         }
         else 
         {
            // If not, it could be attached to one of the link tags,
            //  typically a <link rel="alternate">
            var found = false;
            for each (link in entry.link)
            {
               var rel = link.@rel.toString();
               if(! found && (!rel || rel == "alternate"))
               {
                  // Thumbnail can be on the link, or inside a media:content
                  thumbnail = link.media::thumbnail;
                  if (!thumbnail || !thumbnail.@url.toString())
                  {
                     thumbnail = link.media::content.media::thumbnail[0];
                  }

                  if (thumbnail && thumbnail.@url.toString())
                  {
                     found = true;
                     obj["image"] = thumbnail.@url.toString();
                  }
               }
            }
         }
      }

      items.push(obj);

      ++count;
   }
   
   return {
      title: atom.title.toString(),
      items: items
   };
}