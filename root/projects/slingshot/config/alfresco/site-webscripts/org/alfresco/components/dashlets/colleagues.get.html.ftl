<script type="text/javascript">//<![CDATA[
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>
<div class="dashlet">
   <div class="title">${msg("header.colleagues")}</div>
   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
<#if (memberships?size > 0)>
   <#list memberships as m>
      <div class="detail-list-item <#if m_index = 0>first-item<#elseif !m_has_next>last-item</#if>">
         <div class="avatar">
            <img src="${url.context}<#if m.authority.avatar??>/proxy/alfresco/${m.authority.avatar}?c=force<#else>/components/images/no-user-photo-64.png</#if>" alt="Avatar" />
         </div>
         <div class="person">
            <h4><a href="${url.context}/page/user/${m.authority.userName?url}/profile" class="theme-color-1">${m.authority.firstName?html} <#if m.authority.lastName??>${m.authority.lastName?html}</#if></a></h4>
            <div>${msg("role." + m.role)}</div>
            <div class="user-status">${(m.authority.userStatus!"")?html}</div>
         </div>
      </div>
   </#list>
<#else>
      <div class="detail-list-item first-item last-item">
         <h3>${msg("label.noMembers")}</h3>
      </div>
</#if>
   </div>
</div>