<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.GroupFinder("${el}").setOptions(
   {
      siteId: "<#if page?exists>${page.url.templateArgs.site!""}<#else>${(args.site!"")?js_string}</#if>",
      minSearchTermLength: ${(args.minSearchTermLength!config.scoped['Search']['search'].getChildValue('min-search-term-length'))?js_string},
      maxSearchResults: ${(args.maxSearchResults!config.scoped['Search']['search'].getChildValue('max-search-results'))?js_string},
      setFocus: ${(args.setFocus!'false')?js_string},
      addButtonSuffix: "${(args.addButtonSuffix!'')?js_string}",
      dataWebScript: "${(args.dataWebScript!'api/groups')?replace("[", "{")?replace("]", "}")?js_string}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body" class="group-finder list">
   
   <div class="title"><label for="${el}-search-text">${msg("title")}</label></div>
   
   <div class="finder-wrapper">
      <div class="search-bar theme-bg-color-3">
         <div class="search-text"><input type="text" id="${el}-search-text" name="-" value="" maxlength="255"/></div>
         <div class="group-search-button">
            <span id="${el}-group-search-button" class="yui-button yui-push-button"><span class="first-child"><button>${msg("button.search")}</button></span></span>
         </div>
      </div>
      
      <div id="${el}-results" class="results"></div>
   </div>
</div>