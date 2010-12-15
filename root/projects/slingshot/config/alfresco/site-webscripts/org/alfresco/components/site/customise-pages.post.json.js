// Get clients json request as a "normal" js object literal
var clientRequest = json.toString();
var clientJSON = eval('(' + clientRequest + ')');

// The site and pages we are modifiying
var siteId = clientJSON.siteId;
var newPages = clientJSON.pages;

/**
 * The web framework doesn't have a model for pages.
 * Since the dashboard page always exist for a page it can be used to save the pages.
 * Create a proeprty named "sitePages" in the dashboard page's properties object
 * and store a json string representing the pages.
 */

// Cast from object string
var newPagesString = jsonUtils.toJSONString(newPages) + "";

var p = sitedata.getPage("site/" + siteId + "/dashboard");
p.properties.sitePages = newPagesString;
p.save();

model.success = true;