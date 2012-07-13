function main()
{
   model.contentTypes = 
   [{
      id: "cm:content",
      value: "cm_content"
   }];
   
   
   //Widget instantiation metadata...
   var dndUpload = {
      id : "DNDUpload", 
      name : "Alfresco.DNDUpload",
      assignTo : "dndUpload"
   };
   model.widgets = [dndUpload];
}

main();