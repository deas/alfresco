var connector = remote.connect("alfresco");
var noderef = args.nodeRef;

/* Fetch the details from the repo */
var detailsData = connector.call("/api/webassettranslations?nodeRef="+noderef);
var detailsObj = eval( '(' + detailsData + ')' );

/* TODO Check if it worked or not */

/* Languages in the site */
model.locales = detailsObj.data.locales;
/* Translations of the node */
model.translations = detailsObj.data.translations;
/* Translated parents */
model.parents = detailsObj.data.parents;

/* TODO Are these still needed? */
//model.parentNodeRef = null;
//model.originalName = null;
