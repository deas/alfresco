/**
 * Login component controller GET method
 */
function main()
{
   var successUrl = context.properties["alfRedirectUrl"];
   if (successUrl === null)
   {
      successUrl = url.context;
   }
   model.successUrl = successUrl;
   model.lastUsername = context.properties["alfLastUsername"];
   model.errorDisplay = (args.errorDisplay !== null ? args.errorDisplay : "container");
   model.error = (args.error === "true");
   
   var login = {
      id: "Login", 
      name: "Alfresco.component.Login",
      options: {
         error: model.error,
         errorDisplay: model.errorDisplay,
         lastUsername: model.lastUsername
      }
   };
   model.widgets = [login];
}

main();