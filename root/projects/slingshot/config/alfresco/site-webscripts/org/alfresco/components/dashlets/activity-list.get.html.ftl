<#if activities?exists && activities?size &gt; 0>
   <#assign mode = args.mode!"">
   <#assign lastDate = "3000-01-01"?date("yyyy-MM-dd") lastHour = -1>
   <#list activities as activity>
      <#if activity.userProfile??>
         <#assign userLink="<a href=\"${activity.userProfile?html}\" class=\"theme-color-1\">${activity.fullName?html}</a>">
      <#else>
         <#assign userLink="&quot;<em>" + activity.fullName?html + "</em>&quot;">
      </#if>
      <#if activity.itemPage??>
         <#assign itemLink="<a href=\"${activity.itemPage?html}\" class=\"theme-color-1\">${activity.title?html}</a>">
      <#else>
         <#assign itemLink="&quot;<em>" + activity.title?html + "</em>&quot;">
      </#if>
      <#assign siteLink="<a href=\"${activity.sitePage?html}\" class=\"theme-color-1\">${(siteTitles[activity.siteId]!activity.siteId)?html}</a>">
      <#if dateCompare(lastDate?date, activity.date.fullDate?date) == 1>
         <#assign lastDate = activity.date.fullDate lastHour = activity.date.hour>
<div class="new-day <#if activity_index = 0>first-item</#if>"><div class="ruler"></div><span>${lastHour?string("00")}:00, ${lastDate?string(msg("date-format.mediumDateFTL"))}</span></div>
      <#elseif lastHour != activity.date.hour>
         <#assign lastHour = activity.date.hour>
<div class="new-hour"><div class="ruler"></div><span>${lastHour?string("00")}:00</span></div>
      </#if>
      <#assign detail = msg(activity.type?html, itemLink, userLink, activity.custom0?html, activity.custom1?html, siteLink)>
      <#if mode = "user" && !activity.suppressSite><#assign detail = msg("in.site", detail, siteLink)></#if>
<div class="activity <#if !activity_has_next>last-item</#if>">
   <div class="time">${activity.date.fullDate?time?string("HH:mm")}</div>
   <div class="detail">${detail}</div>
</div>
   </#list>
<#else>
<div class="detail-list-item first-item last-item">
   <span>${msg("label.no-activities")}</span>
</div>
</#if>
