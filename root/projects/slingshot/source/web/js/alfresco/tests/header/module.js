require(["doh/runner"], function(doh) {
   try 
   {
      doh.registerUrl("Header Test Suite", "/share/page/dp/ws/header-test", 50000);
      doh.registerUrl("Sites Menu Test Suite", "/share/page/dp/ws/sitesmenu-test", 50000);
   } 
   catch(e) 
   {
      doh.debug(e);
   }
});