model.library = search.findNode("workspace://SpacesStore/" + url.extension);

// locate file attributes
var filename = null;
var content = null;
var mimetype = null;

for each (field in formdata.fields)
{
  if (field.name == "file" && field.isFile)
  {
    filename = field.filename;
    content = field.content;
    mimetype = field.mimetype;
  }
}

if (filename != undefined && content != undefined)
{
  // create document in library
  var upload = model.library.createFile(filename);
  upload.properties.content.write(content);
  upload.properties.content.mimetype = mimetype;
  upload.properties.title = args.name;
  upload.properties.description = args.desc;
  
  var targs = {};
  targs.user = facebook.user;
  targs.canvasURL = facebook.canvasURL;
  var title = upload.processTemplate("<fb:userlink uid='${args.user}'/> added the <a href='${args.canvasURL}/library/${space.id}'>${document.name}</a> document", targs);
  facebook.postUserAction(title, null);
    
  upload.save();
}