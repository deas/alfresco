function getFilters(filterType)
{
   var myConfig = new XML(config.script),
      filters = [];

   for each (var xmlFilter in myConfig[filterType].filter)
   {
      filters.push(
      {
         type: xmlFilter.@type.toString(),
         label: xmlFilter.@label.toString()
      });
   }

   return filters;
}

function getActivitiesFilter()
{
   var myConfig = config.scoped["ActivitiesFilter"]["filters"].childrenMap["filter"],
      filters = [];
   
   for (var i = 0; i < myConfig.size(); i++)
   {
      var filterLabel = myConfig.get(i).attributes["label"];
      if (!filterLabel)
      {
         filterLabel = "?";
      }
      
      var filterValue = myConfig.get(i).value;
      if (!filterValue)
      {
          filterValue = "";
      }
      
      filters.push(
      {
         label: filterLabel,
         activities: filterValue
      });
   }

   return filters;
}

model.filterRanges = getFilters("filter-range");
model.filterTypes = getFilters("filter-type");
model.filterActivities  = getActivitiesFilter();