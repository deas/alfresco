<import resource="classpath:/alfresco/templates/org/alfresco/documentlibrary.js">

function toJSType(doclibType)
{
   var type = "Alfresco.DocumentDetails";
   switch (String(doclibType))
   {
      case "dod5015":
         type = "Alfresco.RecordsDocumentDetails";
         break;
   }
   return type;
}

model.jsType = toJSType(doclibType);