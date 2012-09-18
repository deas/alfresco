function main()
{
   model.linkButtons = [];

   // Login link
   if (args.loginLink == "login" && user && user.isGuest)
   {
      model.linkButtons.push({
         id: "login",
         href: url.context,
         label: msg.get("button.login"),
         cssClass: "brand-bgcolor-2"
      });
   }
   else if (args.loginLink == "document-details")
   {
      model.linkButtons.push({
         id: "document-details",
         href: url.context + "/page/quickshare-redirect?id=" + args.shareId,
         label: (user && user.isGuest) ? msg.get("button.login") : msg.get("button.document-details"),
         cssClass: "brand-bgcolor-2"
      });
   }
}

main();
