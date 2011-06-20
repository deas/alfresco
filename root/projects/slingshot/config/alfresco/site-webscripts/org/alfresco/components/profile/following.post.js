
var params = new Array(2);
params["userid"] = oldpass;


var connector = remote.connect("alfresco");
var result = connector.post(
      "/api/person/subscriptions/" + encodeURIComponent(user.name),
      jsonUtils.toJSONString(params),
      "application/json");


status.code = 307;
status.location = url.service + "/temp";