var s = new Object();

if (args.sp == undefined)
{
   s.startPage = 0;
}
else
{
   s.startPage = parseInt(args.sp);
}

if (args.pp == undefined)
{
   s.itemsPerPage = 5;
}
else
{
   s.itemsPerPage = parseInt(args.pp);
}

s.startIndex = s.startPage * s.itemsPerPage;

s.searchTerms = "";
if (args.q != null && args.q.length != 0)
{
   s.searchTerms = args.q;
   var terms = args.q.split(" ");
   var query = "TYPE:\"{http://www.alfresco.org/model/content/1.0}content\" AND (";
   for each (var t in terms)
   {
      query += "@\\{http\\://www.alfresco.org/model/content/1.0\\}name:" + t + " " +
               "TEXT:" + t + " ";
   }
   query += ")";
   s.query = query;
   var results = search.luceneSearch(query);
   s.results = results;
   s.totalResults = results.length;
   s.totalPages = Math.ceil(results.length / s.itemsPerPage);
}
model.s = s;