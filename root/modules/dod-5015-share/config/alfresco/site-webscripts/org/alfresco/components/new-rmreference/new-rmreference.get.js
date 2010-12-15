function getReferenceDefinitions()
{
   var result = remote.call("/api/rma/admin/customreferencedefinitions");
   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      return data.data.customReferences;
   }
   else return [];
}

model.reference_types = getReferenceDefinitions()