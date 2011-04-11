<#assign activePage = page.url.templateArgs.pageid!"">
<#assign siteTitle><#if profile.title != "">${profile.title}<#else>${profile.shortName}</#if></#assign>
<script type="text/javascript">//<![CDATA[
   new Alfresco.CollaborationTitle("${args.htmlid}").setOptions(
   {
      site: "${page.url.templateArgs.site!""}",
      siteTitle: "${siteTitle?js_string}",
      user: "${user.name!""}"
   }).setMessages(
      ${messages}
   );
   Alfresco.constants.DASHLET_RESIZE = ${userIsSiteManager?string};
//]]></script>
<div class="page-title theme-bg-color-1 theme-border-1">
   <div class="title">
      <h1 class="theme-color-3">${msg("header.site", "<span>${siteTitle?html}</span>")}</h1>
   </div>
   <div class="links title-button">
   <#if userIsSiteManager>
      <#assign linkClass><#if "invite" == activePage>class="active-page"</#if></#assign>
      <span class="yui-button yui-link-button">
         <span class="first-child">
            <a href="${url.context}/page/site/${page.url.templateArgs.site!}/invite" ${linkClass}>${msg("link.invite")}</a>
         </span>
      </span>
   </#if>
   <#if !userIsMember>
      <span class="yui-button yui-link-button">
         <span class="first-child">
      <#if profile.visibility == "PUBLIC">
            <a id="${args.htmlid}-join-link" href="#">${msg("link.join")}</a>
      <#else>
            <a id="${args.htmlid}-requestJoin-link" href="#">${msg("link.request-join")}</a>
      </#if>
         </span>
      </span>
   </#if>   
   <#assign siteDashboardUrl = page.url.context + "/page/site/" + page.url.templateArgs.site + "/dashboard">
   <#if userIsSiteManager && (page.url.uri == siteDashboardUrl || "customise-site-dashboard" == activePage) >
      <#assign linkClass><#if "customise-site-dashboard" == activePage>class="active-page"</#if></#assign>
      <span class="yui-button yui-link-button">
         <span class="first-child">
            <a href="${url.context}/page/site/${page.url.templateArgs.site!}/customise-site-dashboard" ${linkClass}>${msg("link.customiseDashboard")}</a>
         </span>
      </span>
   </#if>
   <#if userIsSiteManager>
      <input type="button" id="${args.htmlid}-more" name="${args.htmlid}-more" value="${msg("link.more")}"/>
      <select id="${args.htmlid}-more-menu">
         <option value="editSite">${msg("link.editSite")}</option>
         <option value="customiseSite">${msg("link.customiseSite")}</option>
         <option value="leaveSite">${msg("link.leave")}</option>         
      </select> 
   <#elseif userIsMember>
      <input type="button" id="${args.htmlid}-more" name="${args.htmlid}-more" value="${msg("link.actions")}"/>    
      <select id="${args.htmlid}-more-menu">
         <option value="leaveSite">${msg("link.leave")}</option>
      </select> 
   </#if>   
   </div>
   <div style="clear: both"></div>
</div>