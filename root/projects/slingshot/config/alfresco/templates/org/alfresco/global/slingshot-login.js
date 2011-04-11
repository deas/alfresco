/**
 * Login template controller script
 */

function main()
{
   var successUrl = context.properties["alfRedirectUrl"];
   if (successUrl == null)
   {
      successUrl = page.url.context;
   }
   model.successUrl = successUrl;
   model.lastUsername = context.properties["alfLastUsername"];
}

main();