var maxResults = 100;
function getDocType(doc)
{
  var displayType = '';
  var docTypes = 'png,gif,jpg,jpeg,tiff,bmp,ppt,pdf,doc,xls';
  if (doc.type == 'document')
  {
    displayType = doc.name.match(/([^\/\\]+)\.(\w+)$/)[2] 
  }
  if (docTypes.indexOf(displayType)==-1)
  {
     displayType = 'unknown';
  }
  return displayType;
}

function getContentSearchResults(term)
{
  var data  = remote.call("/slingshot/search?term="+stringUtils.urlEncode(term)+"&site=&container=&maxResults="+maxResults);
  data = eval('('+ data+')');
  for (var i=0,len=data.items.length;i<len;i++)
  {
    var doc = data.items[i];
    doc.modifiedOn = new Date(doc.modifiedOn);
    doc.displayType = getDocType(doc);
    doc.doclink =  "api/node/content/"+doc.nodeRef.replace(':/','')+'/'+stringUtils.urlEncode(doc.name);
    data.items[i]=doc;
  } 
  //work out if there we need pagination 
  if (data.items.length===(maxResults+1))
  {
    data.hasMore = true;
    //remove last
    data.items.pop();
  }
  return data;
}

function getSiteResults(term)
{
  var data =remote.call("/api/sites?size=" + maxResults +"&nf=" + stringUtils.urlEncode(term));
  return eval('('+ data+')');
}

function getPeopleResults(term)
{
  var data = remote.call("/api/people?filter="+ stringUtils.urlEncode(term) +"&maxResults=" +maxResults)
  return eval('('+ data+')');  
}

var query = page.url.args.term;
model.contentResults = getContentSearchResults(query);
model.numContentResults = model.contentResults.items.length;
model.siteResults = getSiteResults(query);
model.numSiteResults = model.siteResults.length;
model.pplResults = getPeopleResults(query);
model.numPplResults = model.pplResults.people.length;
model.backButton = true;
