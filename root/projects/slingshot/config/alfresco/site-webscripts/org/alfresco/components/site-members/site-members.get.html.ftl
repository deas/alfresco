<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/site-members/site-members.css" group="site-members"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/site-members/site-members.js" group="site-members"/>
</@>

<@markup id="pre">
   <#-- No pre-instantiation JavaScript required -->
</@>

<@markup id="widgets">
   <@createWidgets group="site-members"/>
</@>

<@markup id="post">
   <#-- No post-instantiation JavaScript required -->
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-body" class="site-members">
         <div class="title"><label for="${args.htmlid}-term">${msg("site-members.heading")}</label></div>
         <div class="invite-people">
         <#if currentUserRole = "SiteManager">
            <span id="${args.htmlid}-invitePeople" class="yui-button yui-link-button">
               <span class="first-child">
                  <a href="invite">${msg("site-members.invite-people")}</a>
               </span>
            </span>
         </#if>
         </div>
         <div class="finder-wrapper">
            <div class="search-controls theme-bg-color-3">
               <div class="search-text"><input id="${args.htmlid}-term" type="text" class="search-term" /></div>
               <div class="search-button"><button id="${args.htmlid}-button">${msg("button.search")}</button></div>
            </div>
            <div id="${args.htmlid}-members" class="results"></div>
         </div>
      </div>
   </@>
</@>