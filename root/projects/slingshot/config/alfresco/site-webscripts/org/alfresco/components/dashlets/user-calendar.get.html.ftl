<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.widget.DashletResizer("${args.htmlid?js_string}", "${instance.object.id}");
   new Alfresco.widget.DashletTitleBarActions("${args.htmlid}").setOptions(
   {
      actions:
      [
         {
            cssClass: "help",
            bubbleOnClick:
            {
               message: "${msg("dashlet.help")?js_string}"
            },
            tooltip: "${msg("dashlet.help.tooltip")?js_string}"
         }
      ]
   });
})();
//]]></script>
<div class="dashlet">
   <div class="title">${msg("label.header")}</div>
   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
<#if eventList??>
   <#if eventList?size &gt; 0>
      <#list eventList as event>
         <#assign startDate = event.when?string(msg("date-format.mediumDateFTL"))>
         <#assign endDate = event.endDate?date("yyyy-MM-dd")?string(msg("date-format.mediumDateFTL"))>
         <div class="detail-list-item <#if event_index = 0>first-item<#elseif !event_has_next>last-item</#if>">
            <div class="icon"><img src="${url.context}/res/components/calendar/images/calendar-16.png" alt="event" /></div>
            <div class="details2">
               <h4><a href="${url.context}/${event.url}" class="theme-color-1">${event.title?html}</a></h4>
               <div>
                  <#if startDate != endDate && event.allday != "true">
                     <#-- Simple Multiday -->
                     ${startDate} ${event.start} - ${endDate} ${event.end}
                  <#else>
                     ${startDate} 
                     <#if event.allday = "true">
                        <#if startDate != endDate>
                           <#-- Allday Multiday -->
                           - ${endDate}
                         </#if>
                        ${msg("label.allday")}
                     <#else>
                        <#-- Single day -->
                        ${event.start}
                        <#if event.start != event.end>
                            - ${event.end}
                        </#if>
                     </#if>
                  </#if>
               </div>
               <#assign siteLink><a href='${url.context}/page/site/${event.site}/dashboard' class="theme-link-1">${event.siteTitle?html}</a></#assign>
               <div>${msg("label.in-site", siteLink)}</div>
            </div>
         </div>
      </#list>
   <#else>
      <div class="detail-list-item first-item last-item">
          <span>${msg("label.noEvents")}</span>
      </div>
   </#if>
<#else>
      <div class="detail-list-item first-item last-item">
          <span>${msg("label.error")}</span>
      </div>
</#if>
   </div>
</div>