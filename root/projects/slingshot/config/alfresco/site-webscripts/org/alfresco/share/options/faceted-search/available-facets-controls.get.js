// By default their is only a single Aikau widget control for rendering facets results in the 
// search page. However, this WebScript can be extended by modules that provide additional 
// controls to make those controls available for selection in the faceted search configuration page...
model.items = [
   {
      label: msg.get("facet.rendering.control.simple"),
      value: "alfresco/search/FacetFilters"
   }
];