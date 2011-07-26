<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.PeopleFinder("${el}").setOptions(
   {
      userId: "${user.name?js_string}",
      siteId: "<#if page?exists>${page.url.templateArgs.site!""}<#else>${(args.site!"")?js_string}</#if>",
      minSearchTermLength: ${(args.minSearchTermLength!config.scoped['Search']['search'].getChildValue('min-search-term-length'))?js_string},
      maxSearchResults: ${(args.maxSearchResults!config.scoped['Search']['search'].getChildValue('max-search-results'))?js_string},
      setFocus: ${(args.setFocus!'false')?js_string},
      addButtonSuffix: "${(args.addButtonSuffix!'')?js_string}",
      dataWebScript: "${(args.dataWebScript!'api/people')?replace("[", "{")?replace("]", "}")?js_string}",
      viewMode: ${args.viewMode!"Alfresco.PeopleFinder.VIEW_MODE_DEFAULT"}
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body" class="people-finder list">
   
   <div class="title"><label for="${el}-search-text">${msg("title")}</label></div>
   
   <div class="finder-wrapper">
      <div class="search-bar theme-bg-color-3">
         <div class="search-text"><input type="text" id="${el}-search-text" name="-" value="" maxlength="256" tabindex="0"/></div>
         <div class="search-button">
            <span id="${el}-search-button" class="yui-button yui-push-button"><span class="first-child"><button>${msg("button.search")}</button></span></span>
         </div>
      </div>
      
      <div id="${el}-help" class="yui-g theme-bg-color-2 help hidden">
         <span class="title">${msg("help.title")}</span>
         <div class="yui-u first">
            <span class="subtitle">${msg("help.subtitle1")}</span>
            <span>${msg("help.info1")}</span>
            <span class="example">${msg("help.example1")}</span>
            <span>${msg("help.result1")}</span>
            <span>${msg("help.info2")}</span>
            <span class="example">${msg("help.example2")}</span>
            <span>${msg("help.result2")}</span>
            <span>${msg("help.info3")}</span>
            <span class="example">${msg("help.example3")}</span>
            <span>${msg("help.result3")}</span>
         </div>
         <div class="yui-u">
            <span class="subtitle">${msg("help.subtitle2")}</span>
            <span>${msg("help.info4")}</span>
            <span class="example">${msg("help.example4")}</span>
            <span>${msg("help.result4")}</span>
            <span>${msg("help.info5")}</span>
            <span class="example">${msg("help.example5")}</span>
            <span>${msg("help.result5")}</span>
            <span>${msg("help.info6")}</span>
            <span class="example">${msg("help.example6")}</span>
            <span>${msg("help.result6")}</span>
            <span class="example">${msg("help.example7")}</span>
            <span>${msg("help.result7")}</span>
         </div>
      </div>
      
      <div id="${el}-results" class="results hidden"></div>
   </div>
</div>