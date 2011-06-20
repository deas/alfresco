<#assign el=args.htmlid>
<div id="${el}-body" class="profile">
   <div id="${el}-readview">
      <div>          
         <form id="${el}-form-private" action="${url.context}/service/components/profile/private" method="post">
             <input type="checkbox" name="private" value="0" /> Private
         </form>        
      </div>
      <div class="viewcolumn">
         <div class="header-bar">${msg("label.following")}</div>
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
               <div>
                  <form id="${el}-form-unfollow" action="${url.context}/service/components/profile/following" method="post">
                     <button id="${el}-button-unfollow" name="unfollow">${msg("button.unfollow")}</button>
                  </form>      
               </div> 
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