<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.CategoryManager("${el}").setOptions(
   {
      nodeRef: "alfresco://category/root"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-body" class="category-manager">
   
   <!-- List panel -->
   <div id="${el}-list">
      
         <div class="yui-u first">
            <div class="title">${msg("title.category-manager")}</div>
         </div>
         
         <div class="yui-u align-left">
         	<div id="${el}-category-manager"></div>
         </div>
   
   </div>
 	
</div>