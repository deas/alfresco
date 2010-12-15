<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RMPropertyMenu('${htmlid}-audit-property').setOptions(
   {
      showIdentiferField: true,
      showAllField: true,
      customFields: YAHOO.lang.JSON.parse('[<#list meta as d>{"id": "${d.name}", "title": "${d.title?js_string}"}<#if d_has_next>,</#if></#list>]')
   });
   new Alfresco.RM_Audit('${htmlid}-audit').setOptions(
   {
      siteId: "${page.url.templateArgs.site!"rm"}",
      containerId: "${template.properties.container!"documentLibrary"}",
      viewMode: Alfresco.RM_Audit.VIEW_MODE_DEFAULT,
      auditEvents: ${eventsStr}
   }).setMessages(${messages});
//]]</script>
  
  <div id="${htmlid}-audit" class="audit">
    <div class="yui-gc">
      <div class="yui-u first">
          <div id="${htmlid}-audit-info" class="audit-info">
             <h2>${msg("label.header-title")}</h2>
             <p id="${htmlid}-audit-status-date" class="audit-status-date">${msg('label.default-message')}</p>
          </div>
      </div>
      <div class="yui-u">
         <div id="${htmlid}-auditActions" class="auditActions">
            <button id="${htmlid}-audit-toggle" disabled name="${htmlid}-audit-toggle"  class="audit-toggle">${msg('label.button-start')}</button>
            <button id="${htmlid}-audit-view" name="audit-view" class="audit-view">${msg("label.button-view-log")}</button>
            <button id="${htmlid}-audit-clear" name="audit-clear" class="audit-clear">${msg("label.button-clear")}</button>
         </div>
      </div>
    </div>    
    <div class="filters">
      <div class="yui-gb">
         <div class="yui-g first">
            <div class="yui-u first">
               <div id="${htmlid}-entriesFilter" class="filter">
                  <div class="hd">
                     <label for="${htmlid}-audit-entries">${msg('label.header-entries')}:</label>                     
                  </div>
                  <div class="bd">
                     <input type="text" name="${htmlid}-audit-entries" value="" id="${htmlid}-audit-entries" />
                  </div>
               </div>
            </div>   
            <div class="yui-u">
               <div id="${htmlid}-dateFilter" class="filter"> 
                  <div class="hd">
                     <label for="${htmlid}-audit-fromDate">${msg('label.header-from')}:</label>
                  </div>
                  <div class="bd">
                     <input type="text" name="${htmlid}-audit-fromDate" value="" id="${htmlid}-audit-fromDate" />
                     <a id="${htmlid}-audit-fromDate-icon" class="datepicker-icon">
                        <img class="datepicker-icon" src="${url.context}/res/components/form/images/calendar.png" />
                     </a>
                     <div id="${htmlid}-audit-fromDate-cal" class="datepicker"></div>
                     <label for="${htmlid}-audit-toDate">${msg('label.header-to')}:</label><input type="text"  name="${htmlid}-audit-toDate" value="" id="${htmlid}-audit-toDate" />
                     <a id="${htmlid}-audit-toDate-icon" class="datepicker-icon">
                        <img class="datepicker-icon" src="${url.context}/res/components/form/images/calendar.png" />
                     </a>
                     <div id="${htmlid}-audit-toDate-cal" class="datepicker"></div>
                  </div>
               </div>
            </div>
         </div>
      
         <div class="yui-g">
            <div class="yui-u first">
               <div id="${htmlid}-eventsFilter" class="filter">
                  <div class="hd">
                     <label for="${htmlid}-events=menu">${msg('label.header-event')}:</label>                     
                  </div>
                  <div class="bd">
                     <input id="${htmlid}-audit-events" type="button" name="${htmlid}-audit-events" value="${msg("label.all")}" />
                     <select name="${htmlid}-audit-events-menu" id="${htmlid}-audit-events-menu" onchange="" size="1">
                        <option value="ALL">${msg("label.all")}</option>
                        <#list events as e>
                           <option value="${e.value}">${e.label}</option>
                        </#list>
                     </select>
                  </div>
               </div>
            </div>
            <div class="yui-u">
               <div id="${htmlid}-propertyFilter" class="filter">
                  <div class="hd">
                     <label for="property_menu">${msg('label.header-property')}:</label>
                  </div>
                  <div class="bd">
                     <input id="${htmlid}-audit-property" type="button" name="${htmlid}-audit-property" value="${msg("label.all")}" />
                  </div>
               </div>
            </div>
         </div>
         <div class="yui-g">
            <div class="yui-u first">
               <div id="${htmlid}-userFilter" class="filter">
                  <div class="hd">
                     <label for="specify">${msg('label.header-users')}:</label>
                  </div>
                  <div class="bd">
                     <label for="audit-specifyfilter">${msg("label.show-log-for")}</label> 
                     <div id="${htmlid}-audit-personFilter" class="personFilter"><span>${msg("label.all-users")}</span><a id="${htmlid}-personFilterRemove" class="personFilterRemove"><img src="${page.url.context}/res/components/images/remove-icon-16.png"  alt="${msg('label.remove-filter')}"/></a></div>
                     <button id="${htmlid}-audit-specifyfilter" name="audit-specifyfilter" class="audit-specifyfilter">${msg("label.button-specify")}</button>
                     <div id="${htmlid}-audit-peoplefinder" class="audit-peoplefinder"></div>
                  </div>
               </div>
            </div>
            <div class="yui-u">
               <div class="filter">
                  <div class="hd">
                     <button id="${htmlid}-apply" class="audit-apply">${msg('label.button-apply')}</button>
                  </div>
               </div>
            </div>
         </div>   
      </div>
   </div>    
    <div id="${htmlid}-audit-log" class="yui-gc audit-log">
      <div class="yui-u">
         <button id="${htmlid}-audit-export" disabled name="audit-export" class="audit-export">${msg("label.button-export")}</button>
         <button id="${htmlid}-audit-file-record" disabled name="audit-file-record" class="audit-file-record">${msg("label.button-file-record")}</button>            
         <div id="${htmlid}-audit-auditDT" class="auditDT">
         </div>    
      </div>
    </div>
  </div>
</#if>