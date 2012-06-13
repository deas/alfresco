model.contentTypes = 
[{
   id: "cm:content",
   value: "cm_content"
}];


//Widget instantiation metadata...
model.webScriptWidgets = [];
var dndUpload = {};
dndUpload.name = "Alfresco.DNDUpload";
dndUpload.assignToVariable = "dndUpload";
dndUpload.provideOptions = false;
dndUpload.provideMessages = true;
model.webScriptWidgets.push(dndUpload);