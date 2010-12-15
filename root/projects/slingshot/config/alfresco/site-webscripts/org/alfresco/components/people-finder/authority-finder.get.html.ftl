<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.AuthorityFinder("${el}").setOptions(
   {
      siteId: "<#if page?exists>${page.url.templateArgs.site!""}<#else>${(args.site!"")?js_string}</#if>",
      minSearchTermLength: ${(args.minSearchTermLength!'3')?js_string},
      maxSearchResults: ${(args.maxSearchResults!'100')?js_string},
      setFocus: ${(args.setFocus!'false')?js_string},
      addButtonSuffix: "${(args.addButtonSuffix!'')?js_string}",
      dataWebScript: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/authority-query",
      viewMode: Alfresco.AuthorityFinder.VIEW_MODE_DEFAULT,
      authorityType: Alfresco.AuthorityFinder.AUTHORITY_TYPE_ALL
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body" class="authority-finder list">
   
   <div id="${el}-title" class="title"><label for="${el}-search-text">&nbsp;</label></div>
   
   <div class="finder-wrapper">
      <div class="search-bar theme-bg-color-3">
         <div class="search-text"><input type="text" id="${el}-search-text" name="-" value="" /></div>
         <div class="authority-search-button">
            <span id="${el}-authority-search-button" class="yui-button yui-push-button"><span class="first-child"><button>${msg("button.search")}</button></span></span>
         </div>
      </div>
      
      <div id="${el}-results" class="results"></div>
   </div>
</div>