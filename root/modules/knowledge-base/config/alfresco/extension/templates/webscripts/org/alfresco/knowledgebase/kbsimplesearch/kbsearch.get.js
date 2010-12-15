// check that search term has been provided
if (args.q == undefined || args.q.length == 0)
{
   	status.code = 400;
   	status.message = "Search term has not been provided.";
   	status.redirect = true;
}
else
{
	var articleHome = companyhome.childByNamePath("Knowledge Base");
 	var query="+PATH:\"/app:company_home/cm:Knowledge_x0020_Base//.\"+ASPECT:\"{http://www.alfresco.org/model/knowledgebase/1.0}article\"";
	query=query+ "+@kb\\:status:\"published\"";
 	var str = (args.q).split(" ");
 	query=query + '+(';
    for(var i=0;i<str.length;i++)
    {
    	query=query + ' TEXT:"' + str[i] + '"';
    }      
    query=query + ')';
 
 	//query = query + " +@cm\\:content.mimetype:\"text/html\"";

 	var nodes =search.luceneSearch(query); 
 	model.resultset = nodes;
}
