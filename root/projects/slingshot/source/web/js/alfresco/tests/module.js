require(["doh/runner"], function(doh) {
   try 
   {
      doh.registerUrl("Menus Test Suite", "/share/page/dp/ws/menus-test", 999999);
      doh.registerUrl("Header Test Suite", "/share/page/dp/ws/header-test", 999999);
   } 
   catch(e) 
   {
      doh.debug(e);
   }
});