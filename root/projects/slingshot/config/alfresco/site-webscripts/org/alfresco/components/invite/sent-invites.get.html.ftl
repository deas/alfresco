<script type="text/javascript">//<![CDATA[
   new Alfresco.SentInvites("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      minSearchTermLength: ${args.minSearchTermLength!config.scoped['Search']['search'].getChildValue('min-search-term-length')},
      maxSearchResults: ${args.maxSearchResults!config.scoped['Search']['search'].getChildValue('max-search-results')},
      setFocus: ${args.setFocus!'false'}
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-sentinvites" class="sent-invites">

   <div class="title">
      <label for="${args.htmlid}-search-text">${msg("sentinvites.title")}</label>
   </div>

   <div id="${args.htmlid}-wrapper" class="sent-invites-wrapper">
      <div class="search-bar theme-bg-color-3">
         <div class="search-label"><label for="${args.htmlid}-search-text">${msg("label.search")}</label></div>
         <div class="search-text"><input type="text" id="${args.htmlid}-search-text" name="-" value="" /></div>
         <div class="search-button"><button id="${args.htmlid}-search-button">${msg("button.search")}</button></div>
      </div>
      
      <#--
      <div class="tool-bar yui-gc">
         <div id="${args.htmlid}-paginator" class="paginator yui-b first">
         </div>
         <div class="tools yui-b">
            <button name="select-clear-button">Clear All...</button>
            <button name="clear-pending-button">Clear All Pending</button>
         </div>
      </div>
      -->
      <div id="${args.htmlid}-results" class="results"></div>
   </div>
</div>
