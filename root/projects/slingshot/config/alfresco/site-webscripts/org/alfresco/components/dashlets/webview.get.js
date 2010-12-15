var uri = args.webviewURI;
var webviewTitle = '';
var isDefault = false;

if (!uri)
{
   // Use the default
   var conf = new XML(config.script);
   uri = conf.uri[0].toString();
   isDefault = true;
}


if (args.webviewTitle)
{
   webviewTitle = args.webviewTitle;
}

var height = args.height;
if (!height)
{
   height = "";
}

var connector = remote.connect("http");
var re = /^(http|https):\/\//;
if (!isDefault && !re.test(uri))
{
   uri = "http://" + uri;
}

model.webviewTitle = webviewTitle;
model.uri = uri;
model.height = height;
model.isDefault = isDefault;