<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils.js">

function assignWorkflow() 
{
   var args = page.url.args;
   var params = 
   {
     "date" :         new Date(Date.parse(args.date)).toString(),
     "description" :  args.description,
     "nodeRefs" :     [args.nodeRef],
     "people" :       [args.user],
     "type" :         args.type
   }.toSource();
   var connector = remote.connect("alfresco");
   //send post, not forgetting to strip wrapped rounded brackets that toSource() adds.
   var result = connector.post('/slingshot/doclib/action/assign-workflow',params.slice(1,params.length-1) , "application/json");
   return eval('('+ result +')');
}

model.recentDocs = getDocuments(page.url.args.site,'documentLibrary','recentlyModified',30).items;
model.allDocs = getDocuments(page.url.args.site,'documentLibrary','all',30).items;
model.myDocs = getDocuments(page.url.args.site,'documentLibrary','favouriteDocuments',30).items;
model.backButton = true;
var workflowResult = assignWorkflow();
if (workflowResult.overallSuccess==true)
{
   model.workflowResult = 'true';
}
else
{
   model.workflowResult = 'false';
}