<#assign el=args.htmlid?html>


<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/profile/profile.css" group="profile"/>
</@>

<@markup id="js">
   <@script src="${url.context}/res/components/profile/following.js" group="profile"></@script>
</@>

<@markup id="widgets">
   <@createWidgets group="profile"/>
</@>
 
<@markup id="html">
   <@uniqueIdDiv>
      <div id="${el}-body" class="profile">
         <div class="viewcolumn">
            <div class="header-bar">${msg("label.following")}
            <#if activeUserProfile>
               <div class="private">
                  <input id="${el}-checkbox-following-private" type="checkbox" name="private" value="1" <#if privatelist>checked=""</#if>/>
                <label for="${el}-checkbox-following-private">${msg("label.private")}</label>
               </div>
            </#if>
            </div>
            <#if (numPeople > 0)>
            <ul class="people">
            <#list data.people as user>
               <li <#if (user_index == 0)>class="first"</#if>>
                  <div class="wrapper">
                     <div class="img-wrapper"><img alt="avatar" src="${url.context}/proxy/alfresco/slingshot/profile/avatar/${user.avatar?string?replace('://','/')}"></div>
                     <div class="meta">
                        <a href="${url.context}/page/user/${user.userName?url}/profile" class="theme-color-1">${(user.firstName!"")?html} ${(user.lastName!"")?html}</a>
                        <span class="lighter">(${user.userName?html})</span>
                        <div class="user-info">${(user.jobtitle!"")?html}</div>
                        <div class="user-info">${(user.organization!"")?html}</div>
                        <#if (user.userStatus??)>
                        <div class="user-status">${(user.userStatus!"")?html}
                           <span class="lighter"> (<span class="relativeTime">${user.userStatusTime.iso8601?html}</span>)</span>
                         </div>
                        </#if>
                     </div>
                     <#if (activeUserProfile)>
                        <div class="button-wrapper">
                           <button class="alfresco-button" name=".onFollowingClick" value="${user.userName?html}">${msg("button.unfollow")}</button>
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
      <script>Alfresco.util.renderRelativeTime("${args.htmlid?js_string}-body");</script>
   </@>
</@>