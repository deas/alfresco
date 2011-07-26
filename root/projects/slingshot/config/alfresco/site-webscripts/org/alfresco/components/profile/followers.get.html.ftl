<#assign el=args.htmlid?html>
<div id="${el}-body" class="profile">
   <div class="viewcolumn">
      <div class="header-bar">${msg("label.followers")}</div>
      <#if (numPeople >0)>
      <ul class="people">
      <#list data.people as user>
         <li <#if (user_index == 0)>class="first"</#if>>
            <div class="wrapper">
               <div class="img-wrapper"><img alt="avatar" src="${url.context}/proxy/alfresco/slingshot/profile/avatar/${user.userName?url}"></div>
               <div class="meta">
                  <a href="${url.context}/page/user/${user.userName?url}/profile" class="theme-color-1">${(user.firstName!"")?html} ${(user.lastName!"")?html}</a>
                  <span class="lighter">(${user.userName?html})</span>
                  <div class="user-info">${(user.jobtitle!"")?html}</div>
                  <div class="user-info">${(user.organization!"")?html}</div>
                  <#if (user.userStatus??)>
                  <div class="user-status">${(user.userStatus!"")?html}
                     <span class="lighter"> (${user.userStatusRelativeTime?html})<span>
                   </div>
                  </#if>
               </div>
            </div>
         </li>
      </#list>
      </ul>
      <#else>
      <p>${msg("label.noFollowing")}</p>
      </#if>         
   </div>
</div>