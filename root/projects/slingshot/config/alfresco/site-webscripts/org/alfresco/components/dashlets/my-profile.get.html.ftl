<div class="dashlet">
   <div class="title">${msg("header.myLimitedProfile")}</div>
   <div class="toolbar">
      <a href="${url.context}/page/user/${user.name?url}/profile" class="theme-color-1">${msg("link.viewFullProfile")}</a>
   </div>
   <div class="body profile">
      <div class="photorow">
         <div class="photo">
            <#if user.properties.avatar??>
               <img  class="photoimg" src="${url.context}/proxy/alfresco/api/node/${user.properties.avatar?replace('://','/')?html}/content/thumbnails/avatar?c=force" alt="" />
            <#else>
               <img class="photoimg" src="${url.context}/res/components/images/no-user-photo-64.png" alt="" />
            </#if>
         </div>
<#escape x as x?html>
         <div class="namelabel"><a href="${url.context}/page/user/profile" class="theme-color-1">${user.properties["firstName"]!""} ${user.properties["lastName"]!""}</a></div>
         <div class="fieldlabel">${user.properties["jobtitle"]!""}</div>
      </div>
      <div class="clear"></div>
      <div class="row">
         <div class="fieldlabelright">${msg("label.email")}:</div>
         <div class="fieldvalue"><#if user.properties["email"]??><a href="mailto:${user.properties["email"]!""}" class="theme-color-1">${user.properties["email"]}</a></#if></div>
      </div>
      <div class="row">
         <div class="fieldlabelright">${msg("label.phone")}:</div>
         <div class="fieldvalue">${user.properties["telephone"]!""}</div>
      </div>
      <div class="row">
         <div class="fieldlabelright">${msg("label.skype")}:</div>
         <div class="fieldvalue">${user.properties["skype"]!""}</div>
      </div>
      <div class="row">
         <div class="fieldlabelright">${msg("label.im")}:</div>
         <div class="fieldvalue">${user.properties["instantmsg"]!""}</div>
      </div>
      <div class="clear"></div>
   </div>
</div>
</#escape>