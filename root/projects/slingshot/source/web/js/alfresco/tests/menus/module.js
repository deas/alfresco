require(["doh/runner"], function(doh) {
   try 
   {
      doh.registerUrl("Menus Test Suite", "/share/page/dp/ws/menus-test", 50000);
   } 
   catch(e) 
   {
      doh.debug(e);
   }
});