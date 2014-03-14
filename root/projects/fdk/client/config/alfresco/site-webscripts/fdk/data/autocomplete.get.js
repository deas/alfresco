var results = {};
var result= new Array();
var resource = "" + url.extension;
var query  = args.q;
var status = "succeed";

switch (resource)
{
   case "google":

      var googleServiceUrl = "http://google.com/complete/search?output=toolbar&q="+query;
      var connector = remote.connect("http");
      var str = new String(connector.call(googleServiceUrl));

      //Javascript E4X module has problems with XML header
      if ( str.substr(0,5).indexOf("?xml") != -1 ) 
      {
         positionRootElement = str.indexOf("<", 10);//get first real tag
         str = str.substr( positionRootElement, str.length - 1 ); 
      }

      var suggestions = new XML(str);
      var suggestion;
      for each (suggestion in suggestions.CompleteSuggestion)
      {
         var resultItem = {};
         resultItem.name  = suggestion.suggestion.@data.toString();
         resultItem.value = suggestion.suggestion.@data.toString();
         result.push(resultItem);
      }
   
      break;
   
   case "yahoo":
   
      // get appid from configuration
      var s = new XML(config.script);
      var appid = s.yahoo.appid;

      var yahooServiceUrl = "http://search.yahooapis.com/WebSearchService/V1/relatedSuggestion?query="+query+"&appid="+appid+"&output=json";
      var connector = remote.connect("http");
      var jsonStr = JSON.parse(connector.call(yahooServiceUrl));

      var suggestion;
      for each (suggestion in jsonStr.ResultSet.Result)
      {
         var resultItem = {};
         resultItem.name  = suggestion;
         resultItem.value = suggestion;
         result.push(resultItem);
      }
   
      break;
   
   default : 
      status="failed";
}

results.result = result;
model.result = jsonUtils.toJSONString(results);

