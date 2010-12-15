<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />

<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.ConsoleApplication("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="application">
   
   <!-- Options panel -->
   <div id="${el}-options" class="hidden">
      
      <form id="${el}-options-form" action="${url.context}/service/components/console/application" method="post">
         
         <div class="title">${msg("label.options")}</div>
         
         <div class="row">
            <span class="label">${msg("label.theme")}:</span>
            <div class="flat-button">
               <select id="console-options-theme-menu">
                  <#list themes as t>
                  <option value="${t.id}"<#if t.selected> selected="selected"</#if>>${t.title?html}</option>
                  </#list>
               </select>
            </div>
         </div>
         
         <div class="buttons">
            <button id="${el}-apply-button" name="apply">${msg("button.apply")}</button>
         </div>
      
      </form>
   </div>

</div>