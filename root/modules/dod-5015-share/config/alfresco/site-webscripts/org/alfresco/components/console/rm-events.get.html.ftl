<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />

<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsEvents("${args.htmlid}").setOptions(
   {
      eventTypes: { <#list eventTypes as eventType>"${eventType.eventTypeName}" : "${eventType.eventTypeDisplayLabel}"<#if eventType_has_next>, </#if></#list> }
   }).setMessages(${messages});
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="events">

   <!-- View panel -->
   <div id="${el}-view" class="hidden">
      <div class="yui-g">
         <div class="yui-u first title">
            ${msg("label.viewevents.title")}
         </div>
         <div class="yui-u buttons">
            <span class="yui-button yui-push-button" id="${el}-newevent-button">
               <span class="first-child"><button>${msg("button.newevent")}</button></span>
            </span>
         </div>
      </div>
      <div id="${el}-events" class="events-list"></div>
   </div>

   <!-- Edit panel -->
   <div id="${el}-edit" class="hidden">
      <div class="yui-g">
         <div class="yui-u first title">
            <span id="${el}-create-title">${msg("label.createevent.title")}:&nbsp;</span>
            <span id="${el}-edit-title">${msg("label.editevent.title")}:&nbsp;</span>
         </div>
         <div class="yui-u caption">
            <span class="mandatory-indicator">*</span>${msg("label.required")}
         </div>
      </div>

      <form id="${el}-edit-form" method="" action="">
         <input id="${el}-eventName" name="eventName" type="hidden" value=""/>
         <div class="edit-main">
            <div class="header-bar">
               <span>${msg("label.general")}:</span>
            </div>

            <!-- Label -->
            <div>
               <span class="crud-label">${msg("label.label")}: *</span>
            </div>
            <div>
               <input class="crud-input" id="${el}-eventDisplayLabel" name="eventDisplayLabel" type="text"/>
            </div>

            <!-- Type -->
            <div>
               <span class="crud-label">${msg("label.type")}: *</span>
            </div>
            <div>
               <select class="crud-input type" id="${el}-eventType" name="eventType">
                  <#list eventTypes as eventType>
                  <option value="${eventType.eventTypeName}">${eventType.eventTypeDisplayLabel}</option>
                  </#list>
               </select>
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