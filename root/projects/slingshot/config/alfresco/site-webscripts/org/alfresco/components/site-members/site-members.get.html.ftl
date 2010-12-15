<script type="text/javascript">//<![CDATA[
   new Alfresco.SiteMembers("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      currentUser: "${user.name?js_string}",
      currentUserRole: "${currentUserRole}",
      <#if error??>error: "${error}",</#if>
      roles:
      [
         <#list siteRoles as siteRole>"${siteRole}"<#if siteRole_has_next>,</#if></#list>
      ],
      minSearchTermLength: ${args.minSearchTermLength!config.scoped['Search']['search'].getChildValue('min-search-term-length')},
      maxSearchResults: ${args.maxSearchResults!config.scoped['Search']['search'].getChildValue('max-search-results')},
      setFocus: ${args.setFocus!'false'}
   }).setMessages(
      ${messages}
   );
//]]></script>

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