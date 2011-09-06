<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />

<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsMetaData("${args.htmlid}").setMessages(${messages});
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="metadata">

   <!-- View panel -->
   <div id="${el}-view" class="hidden">
      <div class="title">${msg("label.custom-metadata-title")}</div>
      
      <div class="view-main">
         <div class="yui-gf">
            <div class="yui-u first">
               <div class="list-header theme-bg-color-4">${msg("label.list-title")}</div>
               <div class="object-list">
                  <ul id="${el}-object-list">
                    <#list customisable as custom>
                       <li id="${el}-${custom.name}" class="customisable theme-bg-color-2" title="${custom.title}">${custom.title}</li>                    
                    </#list>
                  </ul>
               </div>
            </div>
            <div class="yui-u separator">
               <div class="list-title-button">
                  <!-- New Metadata Property button -->
                  <div class="newproperty-button">
                     <span class="yui-button yui-push-button" id="${el}-newproperty-button">
                        <span class="first-child"><button>${msg("button.new")}</button></span>
                     </span>
                  </div>
               </div>
               <div class="list-title theme-bg-color-4">
                  <span>${msg("label.custom-metadata")}:&nbsp;</span>
                  <span id="${el}-view-metadata-item"></span>
               </div>
               <!-- dynamically generated property list -->
               <div id="${el}-property-list"></div>
            </div>
         </div>
      </div>
   </div>
   
   <!-- Create panel -->
   <div id="${el}-create" class="hidden">
      <div class="title">
         <span>${msg("label.create-metadata-title")}:&nbsp;</span>
         <span id="${el}-create-metadata-item"></span>
      </div>
      
      <form id="${el}-create-form" action="">
      <div class="create-main">
         <div class="label-row">
            <span>${msg("label.label")}:</span>
         </div>
         <div class="field-row">
            <input id="${el}-create-label" type="text" maxlength="255" />&nbsp;*
         </div>
         <div class="label-row">
            <span>${msg("label.type")}:</span>
         </div>
         <div class="field-row">
            <select id="${el}-create-type">
               <option value="d:text">${msg("label.datatype.text")}</option>
               <option value="d:boolean">${msg("label.datatype.boolean")}</option>
               <option value="d:date">${msg("label.datatype.date")}</option>
            </select>
         </div>
         <div class="field-row"  <#if (constraints?size == 0)>style="display:none;"</#if>>
            <input type="checkbox" id="${el}-create-use-list" /><label for="${el}-create-use-list">${msg("label.use-list")}:</label>
            <!-- generated list of values constraints drop-down-->
            <select id="${el}-create-list">
               <#list constraints as c>
               <option value="${c.constraintName}">${c.constraintTitle?html}</option>
               </#list>
            </select>
         </div>
         <div class="field-row">
            <input type="checkbox" id="${el}-create-mandatory" /><label for="${el}-create-mandatory">${msg("label.mandatory")}</label>
         </div>
         <div class="button-row">
            <!-- Create Metadata Property button -->
            <span class="yui-button yui-push-button" id="${el}-createproperty-button">
               <span class="first-child"><button>${msg("button.create")}</button></span>
            </span>
            <!-- Cancel Create Metadata Property button -->
            <span class="yui-button yui-push-button" id="${el}-cancelcreateproperty-button">
               <span class="first-child"><button>${msg("button.cancel")}</button></span>
            </span>
         </div>
      </div>
      </form>
   </div>
   
   <!-- Edit panel -->
   <div id="${el}-edit" class="hidden">
      <div class="title">
         <span>${msg("label.edit-metadata-title")}:&nbsp;</span>
         <span id="${el}-edit-metadata-item"></span>
      </div>
      
      <form id="${el}-edit-form" action="">
      <div class="edit-main">
         <div class="label-row">
            <span>${msg("label.label")}:</span>
         </div>
         <div class="field-row">
            <input id="${el}-edit-label" type="text" maxlength="255" />&nbsp;*
         </div>
         <div class="label-row">
            <span>${msg("label.type")}:</span>&nbsp;<span id="${el}-edit-type"></span>
         </div>
         <div class="field-row" <#if (constraints?size == 0)>style="display:none;"</#if>>
            <input type="checkbox" id="${el}-edit-use-list" /><label for="${el}-edit-use-list">${msg("label.use-list")}:</label>
            <!-- generated list of values constraints drop-down -->
            <select id="${el}-edit-list">
               <#list constraints as c>
               <option value="${c.constraintName}">${c.constraintTitle?html}</option>
               </#list>
            </select>
         </div>
         <div class="field-row">
            <input type="checkbox" id="${el}-edit-mandatory" /><label for="${el}-edit-mandatory">${msg("label.mandatory")}</label>
         </div>
         <div class="button-row">
            <!-- Edit Metadata Property button -->
            <span class="yui-button yui-push-button" id="${el}-saveproperty-button">
               <span class="first-child"><button>${msg("button.save")}</button></span>
            </span>
            <!-- Cancel Edit Metadata Property button -->
            <span class="yui-button yui-push-button" id="${el}-cancelsaveproperty-button">
               <span class="first-child"><button>${msg("button.cancel")}</button></span>
            </span>
         </div>
      </div>
      </form>
   </div>

</div>
</#if>