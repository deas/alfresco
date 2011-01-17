<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

var uri = unescape(args['uri']);
model.result = doGetCall(uri);

//yyyy-MM-dd
var startIndex = uri.lastIndexOf("?date=") + 6,
   endIndex = startIndex + 10,
   curDate = uri.substring(startIndex, endIndex).split("-");

//MM/dd/yyyy
var from = model.result.from.split("/"),
   to = model.result.to.split("/");

var fromDate = new Date();
fromDate.setYear(from[2]);
fromDate.setMonth(from[0] - 1);
fromDate.setDate(from[1]);

var toDate = new Date();
toDate.setYear(to[2]);
toDate.setMonth(to[0] - 1);
toDate.setDate(to[1]);

var duration = toDate.getTime() - fromDate.getTime(),
   curDate2 = new Date();
curDate2.setYear(curDate[0]);
curDate2.setMonth(curDate[1] - 1);
curDate2.setDate(curDate[2]);	

fromDate = curDate2;
toDate.setTime(curDate2.getTime()  + duration);

model.result.from = (fromDate.getMonth() + 1) + "/" + fromDate.getDate() + "/" +  fromDate.getFullYear();
model.result.to = (toDate.getMonth() + 1) + "/" + toDate.getDate() + "/" +  toDate.getFullYear();