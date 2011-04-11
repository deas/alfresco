<script type="text/javascript">//<![CDATA[
(function()
{
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
   new Alfresco.widget.DashletTitleBarActions("${args.htmlid}").setOptions(
   {
      actions:
      [
         {
            cssClass: "help",
            bubbleOnClick:
            {
               message: "${msg("dashlet.help")?js_string}"
            },
            tooltip: "${msg("dashlet.help.tooltip")?js_string}"
         }
      ]
   });
})();
//]]></script>
<div class="dashlet colleagues">
   <div class="title">${msg("header")}</div>
<#if userMembership.isManager>
   <div class="toolbar flat-button">
      <div>
         <span class="align-right yui-button-align">
            <span class="first-child">
               <a href="invite" class="theme-color-1">
                  <img src="${url.context}/res/components/images/user-16.png" style="vertical-align: text-bottom" width="16" />
                  ${msg("link.invite")}</a>
            </span>
         </span>
      </div>
   </div>
</#if>
   <div class="toolbar flat-button">
      <div>
         <div class="align-left paginator">
            ${msg("pagination.template", 1, memberships?size, totalResults?string)}
         </div>
         <span class="align-right yui-button-align">
            <span class="first-child">
               <a href="site-members#showall" class="theme-color-1">${msg("link.all-members")}</a>
            </span>
         </span>
         <div class="clear"></div>
      </div>
   </div>
   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
<#if (memberships?size == 1 && memberships[0].authority.userName = user.id)>
      <div class="info">
         <h3>${msg("empty.title")}</h3>
      </div>
</#if>
<#list memberships as m>
      <div class="detail-list-item">
         <div class="avatar">
            <img src="${url.context}/proxy/alfresco/slingshot/profile/avatar/${m.authority.userName?url}" alt="Avatar" />
         </div>
         <div class="person">
            <h3><a href="${url.context}/page/user/${m.authority.userName?url}/profile" class="theme-color-1">${m.authority.firstName?html} <#if m.authority.lastName??>${m.authority.lastName?html}</#if></a></h3>
            <div>${msg("role." + m.role)}</div>
   <#if m.authority.userStatus??>
            <div class="user-status">${(m.authority.userStatus!"")?html} <span class="time">(${(m.authority.userStatusRelativeTime!"")?html})</span></div>
   </#if>
         </div>
      </div>
</#list>
   </div>
</div>