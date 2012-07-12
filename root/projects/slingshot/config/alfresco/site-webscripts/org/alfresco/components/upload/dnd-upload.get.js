model.contentTypes = 
[{
   id: "cm:content",
   value: "cm_content"
}];


//Widget instantiation metadata...
model.widgets = [];
var dndUpload = {};
dndUpload.name = "Alfresco.DNDUpload";
dndUpload.assignTo = "dndUpload";
dndUpload.useOptions = false;
dndUpload.useMessages = true;
model.widgets.push(dndUpload);