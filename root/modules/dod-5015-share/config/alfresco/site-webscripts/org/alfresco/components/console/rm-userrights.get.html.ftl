<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<script type="text/javascript" charset="utf-8">
    new Alfresco.RM.UserRights('${htmlid}').setOptions({}).setMessages(${messages});
</script>

<div id="${htmlid}" class="userRights">
   <div class="yui-gc">
      <div class="yui-u first">
         <h1>${msg('label.user-rights')}</h1>
         <h2>${msg('label.users')}</h2>
         <div id="userrightsDT">
         </div>
         <h2>${msg('label.roles')}</h2>
         <div id="userrightsRoles">
            <p>${msg('label.no-roles')}</p>
         </div>
         <h2>${msg('label.groups')}</h2>
         <div id="userrightsGroups">
            <p>${msg('label.no-groups')}</p>
         </div>
      </div>
   </div>
</div>
</#if>