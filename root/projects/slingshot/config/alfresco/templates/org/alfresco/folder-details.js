<import resource="classpath:/alfresco/templates/org/alfresco/documentlibrary.js">

function toJSType(doclibType)
{
   var type = "Alfresco.FolderDetails";
   switch (String(doclibType))
   {
      case "dod5015":
         type = "Alfresco.RecordsFolderDetails";
         break;
   }
   return type;
}

model.jsType = toJSType(doclibType);