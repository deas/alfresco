<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.SiteFinder("${el}").setOptions(
   {
      currentUser: "${user.name?js_string}",
      minSearchTermLength: ${(args.minSearchTermLength!config.scoped['Search']['search'].getChildValue('min-search-term-length'))?js_string},
      maxSearchResults: ${(args.maxSearchResults!config.scoped['Search']['search'].getChildValue('max-search-results'))?js_string},
      inviteData: [
   <#list inviteData as invite>
      {
         id: "${invite.inviteId}",
         siteId: "${invite.resourceName}",
         type: "${invite.invitationType}"
      }<#if invite_has_next>,</#if>
   </#list>
      ],
      setFocus: ${(args.setFocus!'false')?js_string}
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body" class="site-finder">
	
	<div class="title"><label for="${el}-term">${msg("site-finder.heading")}</label></div>
	
   <div class="finder-wrapper">
      <div class="search-bar theme-bg-color-3">
         <div class="search-text"><input type="text" id="${el}-term" class="search-term" maxlength="256" /></div>
         <div class="search-button"><button id="${el}-button">${msg("site-finder.search-button")}</button></div>
      </div>

      <div id="${el}-sites" class="results"></div>
   </div>
	
</div>