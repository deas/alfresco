<#function formatDay theDate>
   <#if dateCompare(date?date, theDate?date, 0, "==") == 1><#return msg("dateFormat.friendly.today")>
   <#elseif dateCompare(yesterday?date, theDate?date, 0, "==") == 1><#return msg("dateFormat.friendly.yesterday")>
   <#elseif dateCompare(lastSunday?date, theDate?date) == 0><#return msg("dateFormat.friendly.earlierThisWeek")>
   <#elseif dateCompare(previousSunday?date, theDate?date) == 0><#return msg("dateFormat.friendly.lastWeek")>
   </#if>
   <#return "Older actvities">
</#function>
<#if activities?exists && activities?size &gt; 0>
   <#assign mode = args.mode!"">
   <#assign lastDay = "">
   <#list activities as activity>
      <#if activity.userProfile??>
         <#assign userLink><a href="${activity.userProfile?html}" class="theme-color-1">${activity.fullName?html}</a></#assign>
      <#else>
         <#assign userLink>&quot;<em>${activity.fullName?html}</em>&quot;</#assign>
      </#if>
      <#if activity.secondUserProfile??>
         <#assign secondUserLink><a href="${activity.secondUserProfile?html}" class="theme-color-1">${(activity.secondFullName!"")?html}</a></#assign>
      <#else>
         <#assign secondUserLink>&quot;<em>${(activity.secondFullName!"")?html}</em>&quot;</#assign>
      </#if>
      <#if activity.itemPage??>
         <#assign itemLink><a href="${activity.itemPage?html}" class="${(cssClasses[activity.type])!""} item-link theme-color-1">${activity.title?html}</a></#assign>
      <#else>
         <#assign itemLink>&quot;<em>${activity.title?html}</em>&quot;</#assign>
      </#if>
      <#assign siteLink><a href="${activity.sitePage?html}" class="site-link theme-color-1">${(siteTitles[activity.siteId]!activity.siteId)?html}</a></#assign>

      <#assign thisDay = formatDay(activity.date.fullDate?date)>
      <#if thisDay != lastDay>
         <#assign lastDay = thisDay>
<div class="new-day"><div class="ruler"></div><span>${thisDay}</span></div>
      </#if>
      <#assign detail = msg(activity.type, itemLink, userLink, activity.custom0?html, activity.custom1?html, siteLink, secondUserLink)>
      <#if mode = "user" && !activity.suppressSite><#assign detail = msg("in.site", detail, siteLink)></#if>
<div class="activity">
   <div class="avatar"><img src="${url.context}/proxy/alfresco/slingshot/profile/avatar/${activity.userName?url}/thumbnail/avatar32" alt="${activity.fullName?html}" /></div>
   <div class="content">
      <span class="detail">${detail}</span><br />
      <span class="time" title="${activity.date.fullDate?date?string(msg("date-format.defaultFTL"))}">${activity.date.relativeTime?html}</span>
   </div>
</div>
   </#list>
</#if>