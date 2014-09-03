// By default this options WebScript defines a hard-coded set of QName options to use when configuring
// facets for search, but this can be overridden to change the hard-coded values or to retrieve options
// from Solr...
model.items = [
   {
      label: msg.get("facetQName.mimetype"),
      value: "{http://www.alfresco.org/model/content/1.0}content.mimetype"
   },
   {
      label: msg.get("facetQName.description"),
      value: "{http://www.alfresco.org/model/content/1.0}description.__"
   },
   {
      label: msg.get("facetQName.creator"),
      value: "{http://www.alfresco.org/model/content/1.0}creator.__.u"
   },
   {
      label: msg.get("facetQName.modifier"),
      value: "{http://www.alfresco.org/model/content/1.0}modifier.__.u"
   },
   {
      label: msg.get("facetQName.created"),
      value: "{http://www.alfresco.org/model/content/1.0}created"
   },
   {
      label: msg.get("facetQName.modified"),
      value: "{http://www.alfresco.org/model/content/1.0}modified"
   },
   {
      label: msg.get("facetQName.size"),
      value: "{http://www.alfresco.org/model/content/1.0}content.size"
   }
];