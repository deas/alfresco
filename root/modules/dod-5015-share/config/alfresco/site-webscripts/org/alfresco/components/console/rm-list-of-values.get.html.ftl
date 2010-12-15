<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />

<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsListOfValues("${args.htmlid}").setMessages(
      ${messages}
   );
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="listofvalues">

   <!-- View panel -->
   <div id="${el}-view" class="hidden">
      <!-- Title -->
      <div class="yui-g">
         <div class="yui-u first title">
            ${msg("label.view-listofvalues-title")}
         </div>
         <div class="yui-u buttons">
            <span class="yui-button yui-push-button" id="${el}-newlist-button">
               <span class="first-child"><button>${msg("button.newlist")}</button></span>
            </span>
         </div>
      </div>
      <!-- Lists -->
      <div id="${el}-listofvalues" class="listofvalues-list"></div>


   </div>

   <!-- Edit panel -->
   <div id="${el}-edit" class="hidden">
      <!-- Title -->
      <div id="${el}-edittitle" class="yui-u first title">
         ${msg("label.edit-listofvalue-title", "")}
      </div>

      <div class="yui-g list-header">
         <div class="yui-gf first">
            <!-- Values -->
            <div class="list-header-button">
               <span class="yui-button yui-push-button" id="${el}-newvalue-button">
                  <span class="first-child"><button>${msg("button.add")}</button></span>
               </span>
            </div>
            <div class="list-header-input">
               <input id="${el}-newvalue-input" type="text" value=""/>
            </div>
            <div class="list-header-title">${msg("label.values")}</div>
         </div>
         <div class="yui-gf">
            <!-- Access -->
            <div class="list-header-button">
               <span class="yui-button yui-push-button" id="${el}-addaccess-button">
                  <span class="first-child"><button>${msg("button.add")}</button></span>
               </span>
            </div>
            <div class="list-header-title">${msg("label.access")}</div>
         </div>
      </div>

      <div class="yui-g list-body">
         <div class="yui-u first">
            <!-- Values -->
            <div id="${el}-values" class="values-list"></div>
         </div>
         <div class="yui-u">
            <!-- Access -->
            <div id="${el}-access" class="access-list"></div>
         </div>
      </div>

      <!-- Done -->
      <div class="main-buttons">
         <hr />
         <span id="${el}-done-button" class="yui-button done">
             <span class="first-child">
                 <button>${msg("button.done")}</button>
             </span>
         </span>
      </div>

      <!-- Auhtority/Access Dialog -->
      <div id="${el}-authoritypicker" class="listofvalues authority-picker">
         <div class="hd"><span id="${el}-authoritypicker-title">${msg("panel.addaccess.header")}</span></div>
         <div class="bd">
            <div>
               <div id="${el}-search-authorityfinder"></div>
            </div>
         </div>
      </div>

   </div>

</div>
</#if>