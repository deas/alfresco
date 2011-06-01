/**
 * Record Search Component: rmsearch
 *
 * Inputs:
 *   mandatory: site = site ID to search in
 *   optional:  query = the query to perform.
 *   optional:  sortby = comma separated list of sort fields
 *              each sort field is in the format of "namespace:attribute/[asc|desc]"
 *              for example: cm:name/asc
 *   optional:  maxResults = max items to return.
 * 
 * Outputs:
 *  data.items/data.error - object containing list of search results
 */

const DEFAULT_MAX_RESULTS = 500;
const SITES_SPACE_QNAME_PATH = "/app:company_home/st:sites/";
const QUERY_TEMPLATES = [
   {field: "keywords", template: "%(cm:name cm:title cm:description TEXT)"},
   {field: "name", template: "%(cm:name)"},
   {field: "title", template: "%(cm:title)"},
   {field: "description", template: "%(cm:description)"},
   {field: "creator", template: "%(cm:creator)"},
   {field: "created", template: "%(cm:created)"},
   {field: "modifier", template: "%(cm:modifier)"},
   {field: "modified", template: "%(cm:modified)"},
   {field: "author", template: "%(cm:author)"},
   {field: "markings", template: "%(rmc:supplementalMarkingList)"},
   {field: "dispositionEvents", template: "%(rma:recordSearchDispositionEvents)"},
   {field: "dispositionActionName", template: "%(rma:recordSearchDispositionActionName)"},
   {field: "dispositionActionAsOf", template: "%(rma:recordSearchDispositionActionAsOf)"},
   {field: "dispositionEventsEligible", template: "%(rma:recordSearchDispositionEventsEligible)"},
   {field: "dispositionPeriod", template: "%(rma:recordSearchDispositionPeriod)"},
   {field: "hasDispositionSchedule", template: "%(rma:recordSearchHasDispositionSchedule)"},
   {field: "dispositionInstructions", template: "%(rma:recordSearchDispositionInstructions)"},
   {field: "dispositionAuthority", template: "%(rma:recordSearchDispositionAuthority)"},
   {field: "holdReason", template: "%(rma:recordSearchHoldReason)"},
   {field: "vitalRecordReviewPeriod", template: "%(rma:recordSearchVitalRecordReviewPeriod)"}
];

/**
 * Returns person display name string as returned to the user.
 * 
 * Caches the person full name to avoid repeatedly querying the repository.
 */
var personDataCache = [];
function getPersonDisplayName(userId)
{
   if (personDataCache[userId] != undefined)
   {
      return personDataCache[userId];
   }
   
   var displayName = "";
   var person = people.getPerson(userId);
   if (person != null)
   {
      displayName = person.properties.firstName + " " + person.properties.lastName;
   }
   personDataCache[userId] = displayName;
   return displayName;
}

/**
 * Returns a record from the fileplan container with properties attached.
 */
function getRecord(siteId, node)
{
   var item =
   {
      node: node,
      nodeRef: node.nodeRef.toString(),
      type: node.typeShort,
      name: node.name,
      title: node.properties["cm:title"],
      description: node.properties["cm:description"],
      modifiedOn: node.properties["cm:modified"],
      modifiedByUser: node.properties["cm:modifier"],
      createdOn: node.properties["cm:created"],
      createdByUser: node.properties["cm:creator"],
      author: node.properties["cm:author"],
      properties: {}
   };
   
   // specific type related properties
   // TODO: add caveat permission check for ViewRecords for parent folder access check?
   var parent = node.parent;
   item.parentFolder = "";
   if (node.isContainer)
   {
      item.size = -1;
      var displayPaths = node.displayPath.split("/");
      if (displayPaths.length >= 5)
      {
         if (displayPaths.length > 5)
         {
            item.parentFolder = parent.name;
         }
         var relPath = "/" + displayPaths.slice(5, displayPaths.length).join("/");
         relPath += (relPath.length > 1 ? ("/" + node.name) : (node.name));
         item.browseUrl = "documentlibrary?path=" + encodeURIComponent(relPath);
      }
   }
   else
   {
      item.size = node.size;
      item.browseUrl = "document-details?nodeRef=" + node.nodeRef.toString();
      item.parentFolder = parent.name;
   }
   
   // generated properties
   item.modifiedBy = getPersonDisplayName(item.modifiedByUser);
   item.createdBy = getPersonDisplayName(item.createdByUser);
   
   // collect up the RMA namespace properties
   // collect up custom props under the rmc:customProperties marker
   for (var k in node.properties)
   {
      if (k.match("^{http://www.alfresco.org/model/recordsmanagement/1.0}") == "{http://www.alfresco.org/model/recordsmanagement/1.0}")
      {
         item.properties["rma_" + k.split('}')[1]] = node.properties[k];
      }
      else if (k.match("^{http://www.alfresco.org/model/rmcustom/1.0}") == "{http://www.alfresco.org/model/rmcustom/1.0}")
      {
         item.properties["rmc_" + k.split('}')[1]] = node.properties[k];
      }
   }
   
   return item;
}

/**
 * Processes the search results. Converts to objects and filters out unwanted nodes
 * 
 * @return the final search results object
 */
function processResults(nodes, siteId)
{    
   var results = [],
      added = 0,
      item,
      i, j;
   
   for (i = 0, j = nodes.length; i < j; i++)
   {
      item = getRecord(siteId, nodes[i]);
      if (item !== null)
      {
         results.push(item);
      }
   }
   
   return (
   {
      items: results
   });
}

/**
 * Return Search results with the given FTS-Alfresco search query.
 */
function getSearchResults(query, sort, maxResults, siteId)
{
   var nodes;
   
   // suffix the rm doclib fileplan site PATH query
   var alfQuery = 'PATH:"' + SITES_SPACE_QNAME_PATH + 'cm:' + search.ISO9075Encode(siteId) + '/cm:documentLibrary//*"';
   
   // build up final query components
   if (query != null && query.length != 0)
   {
      alfQuery += ' AND (' + query + ')';
   }
   
   // gather up the sort by fields
   // they are encoded as "property/dir" i.e. "cm:name/asc"
   var sorts = [];
   for (var i=0, j; i<sort.length; i++)
   {
      if (sort[i].length != 0)
      {
         j = sort[i].indexOf("/");
         sorts.push(
         {
            column: sort[i].substring(0, j),
            ascending: (sort[i].substring(j+1) == "asc")
         });
      }
   }
   
   // perform fts-alfresco language query for records
   var queryDef = {
      query: alfQuery,
      language: "fts-alfresco",
      page: {maxItems: maxResults},
      sort: sorts,
      templates: QUERY_TEMPLATES,
      namespace: "http://www.alfresco.org/model/recordsmanagement/1.0"
   };
   nodes = search.query(queryDef);
   
   return processResults(nodes, siteId);
}

function main()
{
   var siteId = url.templateArgs.site;
   
   // query is in fts-alfresco format
   var query = args.query;
   
   // sort is comma separated list of sort attributes in sort order
   var sort = [];
   var sortby = args.sortby;
   if (sortby != null && sortby.length != 0)
   {
      sort = sortby.split(",");
   }
   
   // maximum results to return - or use default
   var maxResults = (args.maxResults !== null) ? parseInt(args.maxResults) : DEFAULT_MAX_RESULTS;
   
   model.data = getSearchResults(query, sort, maxResults, siteId);
}

main();