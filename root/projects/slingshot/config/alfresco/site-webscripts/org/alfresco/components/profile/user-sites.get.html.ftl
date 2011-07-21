<#assign el=args.htmlid?html>
<div id="${el}-body" class="profile">
   <div class="viewcolumn">
      <div class="header-bar">${msg("label.sites")}</div>
      <#if (numSites >0)>
      <ul class="sites">
      <#list sites as site>
         <li <#if (site_index == 0)>class="first"</#if>>
            <a href="${url.context}/page/site/${site.shortName}/dashboard" class="thmb"><img src="${url.context}/res/components/site-finder/images/site-64.png"/></a>
            <p><a href="${url.context}/page/site/${site.shortName}/dashboard" class="theme-color-1">${site.title?html!""}</a>
            <span>${site.description?html!""}</span></p>
         </li>
      </#list>
      </ul>
      <#else>
      <p>${msg("label.noSiteMemberships")}</p>
      </#if>
   </div>
</div>