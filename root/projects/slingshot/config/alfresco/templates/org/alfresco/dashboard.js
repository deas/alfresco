<import resource="classpath:alfresco/templates/org/alfresco/valid-user-site-access.lib.js">

/**
 * Dashboard template controller script
 */

function main()
{
   // 1 - ""
   // 2 - "yui-g" "yui-gc" "yui-gd" "yui-ge" "yui-gf"
   // 3 - "yui-gb"
   // 4 - "yui-g"
   // 5 - "yui-gb" // note, will leave an empty column to the right
   // 6 - "yui-gb"
   
   var columns = [];
   model.gridClass = template.properties.gridClass;
   if (isValidUserOrSite())
   {
      for (var i = 0; true; i++)
      {
         var noOfComponents = template.properties["gridColumn" + (i + 1)];
         if (noOfComponents)
         {
            columns[i] =
            {
               components: parseInt(noOfComponents)
            };
         }
         else
         {
            break;
         }
      }
   }
   model.gridColumns = columns;
}

main();