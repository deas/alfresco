/**
 * Returns the "referrer" which can be used to decide which components that shall be bound in to a page.
 * 
 * Note! Requires documentlibrary.js to have been imported so it can look at the value of model.doclibType.
 * Note!! The referrer may only contain numbers or characters.
 *
 * In a lof of pages it is enough to use model.doclibType to decide which title and navigation components
 * to display but some pages can be displayed in so many "contexts" that instructions need to be passed
 * in through the url.
 */
function getReferrer()
{
   if (page.url.args.referrer)
   {
      // The referrer is decided by the page who linked (i.e "tasks" or "workflows")
      if (page.url.args.referrer.match(/^\w*$/))
      {
         // Make sure only referrer only contains characters and numbers since it will be used in the html
         return page.url.args.referrer + "-";
      }
      return null;
   }
   else if (model.doclibType && model.doclibType.length > 0)
   {
      // "repo" mode
      return model.doclibType;
   }
   else
   {
      // "site" mode
      return "";
   }
}