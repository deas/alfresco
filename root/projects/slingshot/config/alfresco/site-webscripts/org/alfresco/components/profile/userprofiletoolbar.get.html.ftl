<#assign activePage = page.url.templateArgs.pageid?lower_case!"">
<div id="${args.htmlid}-body" class="toolbar userprofile">
   <div class="link"><a href="profile" <#if activePage=="profile">class="activePage theme-color-4"</#if>>${msg("link.info")}</a></div>
   <div class="separator">&nbsp;</div>
   <div class="link"><a href="user-sites" <#if activePage=="user-sites">class="activePage theme-color-4"</#if>>${msg("link.sites")}</a></div>
   <div class="separator">&nbsp;</div>
   <div class="link"><a href="user-content" <#if activePage=="user-content">class="activePage theme-color-4"</#if>>${msg("link.content")}</a></div>
   <#if activeUserProfile>
      <#if user.capabilities.isMutable>
   <div class="separator">&nbsp;</div>
   <div class="link"><a href="change-password" <#if activePage=="change-password">class="activePage theme-color-4"</#if>>${msg("link.changepassword")}</a></div>
      </#if>
   <div class="separator">&nbsp;</div>
   <div class="link"><a href="user-notifications" <#if activePage=="user-notifications">class="activePage theme-color-4"</#if>>${msg("link.notifications")}</a></div>
   </#if>
</div>