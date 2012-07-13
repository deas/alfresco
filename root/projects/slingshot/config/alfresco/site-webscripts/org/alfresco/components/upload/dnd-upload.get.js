function main()
{
   model.contentTypes = 
   [{
      id: "cm:content",
      value: "cm_content"
   }];
   
   
   //Widget instantiation metadata...
   model.widgets = [];
   var dndUpload = {
      name : "Alfresco.DNDUpload",
      assignTo : "dndUpload"
   };
   model.widgets.push(dndUpload);
}

main();