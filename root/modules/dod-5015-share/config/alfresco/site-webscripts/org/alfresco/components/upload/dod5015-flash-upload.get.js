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

/**
 * Record types
 */
function getRecordTypes()
{
   var recordTypes = [
   {
      id: "-",
      value: "default"
   },
   {
      id: "dod:scannedRecord",
      value: "scannedRecord"
   },
   {
      id: "dod:pdfRecord",
      value: "pdfRecord"
   },
   {
      id: "dod:digitalPhotographRecord",
      value: "digitalPhotographRecord"
   },
   {
      id: "dod:webRecord",
      value: "webRecord"
   }];
   
   return recordTypes;
}

model.contentTypes = getContentTypes();
model.recordTypes = getRecordTypes();