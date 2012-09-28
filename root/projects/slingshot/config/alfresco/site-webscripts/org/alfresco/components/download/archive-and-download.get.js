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

// Get the file-upload settings...
var _inMemoryLimit, _maximumFileSizeLimit;
var docLibConfig = config.scoped["DocumentLibrary"];
if (docLibConfig != null)
{
   var fileUpload = docLibConfig["file-upload"];
   if (fileUpload != null)
   {
      _inMemoryLimit = fileUpload["in-memory-limit"];
      _maximumFileSizeLimit = fileUpload["maximum-file-size-limit"];
   }
}
model.inMemoryLimit = (_inMemoryLimit != null) ? _inMemoryLimit : "262144000";
model.fileUploadSizeLimit = (_maximumFileSizeLimit != null) ? _maximumFileSizeLimit : "0";
