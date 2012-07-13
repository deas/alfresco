/**
 * Custom content types
 */
function getContentTypes()
{
   // TODO: Data webscript call to return list of available types
   var contentTypes = [
   {
      id: "cm:content",
      value: "cm_content"
   }];

   return contentTypes;
}

model.contentTypes = getContentTypes();

function main()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var flashUpload = {
      name : "Alfresco.FlashUpload"
   };
   model.widgets.push(flashUpload);
}

main();

