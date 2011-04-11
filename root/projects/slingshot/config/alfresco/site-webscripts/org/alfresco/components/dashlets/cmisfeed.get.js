<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils/feed.utils.js">

// Use the default
var conf = new XML(config.script);
uri = getValidRSSUri(conf.feed[0].toString());

var connector = remote.connect("http");
model.uri = uri;
model.limit = args.limit || 100;
model.target = args.target || "_self";

var feed = getRSSFeed(uri);
model.title = feed.title;
model.items = feed.items;