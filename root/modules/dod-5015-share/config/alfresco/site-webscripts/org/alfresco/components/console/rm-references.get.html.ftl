<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />

<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsReferences("${args.htmlid}").setMessages(
      ${messages}
   );
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="references">

   <!-- View panel -->
   <div id="${el}-view" class="hidden">
      <div class="yui-g">
         <div class="yui-u first title">
            ${msg("label.view-references-title")}
         </div>
         <div class="yui-u buttons">
            <span class="yui-button yui-push-button" id="${el}-newreference-button">
               <span class="first-child"><button>${msg("button.newreference")}</button></span>
            </span>
         </div>
      </div>
      <div id="${el}-references" class="references-list"></div>
   </div>

   <!-- Edit panel -->
   <div id="${el}-edit" class="hidden">
      <div class="title">
         <span id="${el}-create-title">${msg("label.create-references-title")}:&nbsp;</span>
         <span id="${el}-edit-title">${msg("label.edit-references-title")}:&nbsp;</span>
      </div>

      <form id="${el}-edit-form" method="" action="">
         <div class="edit-main">
            <div class="header-bar">
               <span>${msg("label.general")}:</span>
            </div>

            <!-- General -->
            <div class="field-row">
               <span>${msg("label.type")}:</span>
            </div>
            
            <!-- Bi-directional -->
            <div id="${el}-bidirectional-section">
               <div class="field-row">
                  <input id="${el}-type-bidirectional" name="referenceType" type="radio" value="bidirectional"/>
                  <label for="${el}-type-bidirectional">${msg("label.bidirectional")}</label>
               </div>
               <div>
                  <span class="crud-label">${msg("label.label")}:</span>
               </div>
               <div>
                  <input class="crud-input" id="${el}-bidirectional-label" name="label" type="text"/>
               </div>
            </div>

            <!-- Parent / Child -->
            <div id="${el}-parentchild-section">
               <div class="field-row">
                  <input id="${el}-type-parentchild" name="referenceType" type="radio" value="parentchild"/>
                  <label for="${el}-type-parentchild">${msg("label.parentchild")}</label>
               </div>
               <div>
                  <span class="crud-label">${msg("label.source")}:</span>
               </div>
               <div>
                  <input class="crud-input" id="${el}-parentchild-source" name="source" type="text"/>
               </div>
               <div>
                  <span class="crud-label">${msg("label.target")}:</span>
               </div>
               <div>
                  <input class="crud-input" id="${el}-parentchild-target" name="target" type="text"/>
               </div>
            </div>
            
         </div>

         <!-- Buttons -->
         <div class="button-row">
            <span class="yui-button yui-push-button" id="${el}-save-button">
               <span class="first-child"><button>${msg("button.save")}</button></span>
            </span>
            <span class="yui-button yui-push-button" id="${el}-cancel-button">
               <span class="first-child"><button>${msg("button.cancel")}</button></span>
            </span>
         </div>

      </form>
   </div>

</div>
</#if>