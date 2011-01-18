<div class="page-title theme-bg-color-1 theme-border-1">
   <div class="title">
      <h1 class="theme-color-3"><span>${msg("header.title")}</span></h1>
   </div>
</div>

<div class="accept-invite-body">
<#if (!doRedirect)>
   <h1>${msg("error.acceptfailed.title")}</h1>
   <p>${msg("error.acceptfailed.text")}</p>
<#else>
<script type="text/javascript">//<![CDATA[
   window.location = "${page.url.context}/page/site-index?site=${siteShortName}";
//]]></script>
   <h1>${msg("acceptregistered.title")}</h1>
   <p>${msg("acceptregistered.text")}</p>
   <br />
   <a href="${page.url.context}/page/site-index?site=${siteShortName}">${page.url.context}/page/site-index?site=${siteShortName}</a>
</#if>
</div>