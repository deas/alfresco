<#assign el=args.htmlid?html>
<div id="${el}-body" class="profile">
   <div class="viewcolumn">
      <div class="header-bar">${msg("label.following")}
         <#if activeUserProfile>
         <div class="private">
            <form id="${el}-form-following-private" action="${url.context}/service/components/profile/following-private" method="post">
               <input id="${el}-checkbox-following-private" type="checkbox" name="private" value="1" onclick="submit();" <#if privatelist>checked=""</#if>/>
               <label for="${el}-checkbox-following-private">Private</label>
            </form>
         </div>
         </#if>
      </div>
      <#if (numPeople > 0)>
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
                     <span class="lighter"> ${user.userStatusRelativeTime?html}<span>
                   </div>
                  </#if>
               </div>
               <#if (activeUserProfile)>
               <div class="button-wrapper">
                  <form id="${el}-form-unfollow" action="${url.context}/service/components/profile/following" method="post">
                     <input type="hidden" name="unfollowuser" value="${user.userName?html}"/>
                     <span class="yui-button yui-push-button" id="${el}-button-unfollow">
                        <span class="first-child"><button name="unfollow">${msg("button.unfollow")}</button></span>
                     </span>
                  </form>
               </div>
               </#if>
            </div>
         </li>
      </#list>
      </ul>
      <#else>
      <p>${msg("label.noFollowing")}</p>
      </#if>
   </div>
</div>