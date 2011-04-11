<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.ConsoleTrashcan("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="trashcan">
   
   <!-- List panel -->
   <div id="${el}-list" class="hidden">
      
      <div class="yui-g">
         <div class="yui-u first">
            <div class="title">${msg("title.trashcan")}</div>
         </div>
         <div class="yui-u align-right">
            <!-- Empty trashcan button -->
            <div class="empty-button">
               <span class="yui-button yui-push-button" id="${el}-empty-button">
                  <span class="first-child"><button>${msg("button.empty")}</button></span>
               </span>
            </div>
         </div>
      </div>
      
      <div id="${el}-datalist"></div>
      <div>
         <div id="${el}-paginator" class="paginator">&nbsp;</div>
      </div>
   </div>

</div>