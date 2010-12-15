<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils.js">

function getDocDetails(nodeRef) 
{
   var data = remote.call('/slingshot/doclib/doclist/documents/node/' + nodeRef.replace(":/", "") + '?filter=node');
   data = eval('(' + data + ')');
   var imgTypes = 'png,gif,jpg,jpeg,tiff,bmp';
   for (var i=0,len=data.items.length; i<len; i++)
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
      else
      {
         doc.type = 'unknown';
      }
      
      doc.tags = doc.tags.join(' ');
      doc.thumbnailUrl = generateThumbnailUrl(doc);
      
      data.items[i]=doc;
   }
   return data;
}

model.doc = getDocDetails(page.url.args.nodeRef).items[0];
model.siteTitle = model.doc.location.site;
model.backButton = true;