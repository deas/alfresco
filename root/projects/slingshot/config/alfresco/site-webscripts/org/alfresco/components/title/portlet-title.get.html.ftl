<#assign siteTitle><#if profile.title != "">${profile.title?html}<#else>${profile.shortName}</#if></#assign>
<div class="page-title theme-bg-color-1 theme-border-1">
   <div class="title">
      <h1 class="theme-color-3">${msg("header.site", "<span>${siteTitle}</span>")}</h1>
   </div>
</div>