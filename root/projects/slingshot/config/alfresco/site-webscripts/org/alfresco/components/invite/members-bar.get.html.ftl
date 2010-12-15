<#assign activePage = page.url.templateArgs.pageid?lower_case!"">
<div id="${args.htmlid}-body" class="members-bar theme-bg-2">
   <div class="member-link"><a href="site-members" <#if activePage == "site-members" || activePage == "invite">class="activePage theme-color-4"</#if>>${msg("link.site-members")}</a></div>
   <div class="separator">|</div>
   <div class="member-link"><a href="site-groups" <#if activePage == "site-groups" || activePage == "add-groups">class="activePage theme-color-4"</#if>>${msg("link.site-groups")}</a></div>
<#if isManager>
   <div class="separator">|</div>
   <div class="member-link"><a href="pending-invites" <#if activePage == "pending-invites">class="activePage theme-color-4"</#if>>${msg("link.pending-invites")}</a></div>
</#if>
</div>