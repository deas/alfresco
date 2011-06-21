<#assign el=args.htmlid>
<div id="${el}-body" class="profile">
   <div class="header-bar">${msg("label.followers")}</div>
   <div id="${el}-readview">
      <div class="viewcolumn">
         <#if (numPeople >0)>
         <ul class="people">
         <#list data.people as user>
            <#if (user_index == 0)>
            <li class="first">
            <#else>
            <li>
            </#if>
               <a href="${url.context}/page/user/${user.userName}/profile" class="theme-color-1">${user.firstName!""?html} ${user.lastName!""?html}</a>
               <span class="lighter">(${user.userName?html!""})</span>
               <#if (user.userStatus??)>
               <div class="user-status">${user.userStatus!""?html}
                  <#if (user.userStatusTime??)>
                  <span class="lighter">(${user.userStatusTime.iso8601!""?html})<span>
                  </#if>
                </div>  
               </#if>
               <hr/>
            </li>
         </#list>
         </ul>
         <#else>
         <p>${msg("label.noFollowing")}</p>
         </#if>         
      </div>
   </div>
</div>