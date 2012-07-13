function main()
{
   // Widget instantiation metadata...
   var usersConfig = config.scoped['Users']['users'],
       minPasswordLength = usersConfig.getChildValue('password-min-length');
   
   model.widgets = [];
   var changePassword = {
      name : "Alfresco.ChangePassword",
      options : {
         minPasswordLength : minPasswordLength
      }
   };
   model.widgets.push(changePassword);
}

main();

