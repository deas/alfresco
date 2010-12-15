<script type="text/javascript">//<![CDATA[
   new Alfresco.GroupsList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      roles:
      [
         <#list siteRoles as siteRole>"${siteRole}"<#if siteRole_has_next>,</#if></#list>
      ]
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-grouplistWrapper" class="grouplistWrapper">

<div class="title">${msg("groupslist.title")}</div>

<div id="${args.htmlid}-groupslist" class="groupslist">

   <div id="${args.htmlid}-invitationBar" class="invitelist-bar">
      <button id="${args.htmlid}-selectallroles-button">${msg("groupslist.selectallroles")}</button>
      <select id="${args.htmlid}-selectallroles-menu">
      <#list siteRoles as siteRole>
         <option value="${siteRole}">${msg('role.' + siteRole)}</option>
      </#list>
      </select>
   </div>

   <div id="${args.htmlid}-inviteelist" class="body inviteelist"></div>
   
   <div id="${args.htmlid}-role-column-template" style="display:none">
      <button class="role-selector-button" value="">${msg("role")}</button>
   </div>

</div>
<div class="sinvite">
   <button id="${args.htmlid}-add-button">${msg("button.add-groups")}</button>
   <span id="${args.htmlid}-backTo" class="back-to">${msg("groupslist.or")} <a href="site-groups">${msg("groupslist.back-to")}</a></span>
</div>
</div>