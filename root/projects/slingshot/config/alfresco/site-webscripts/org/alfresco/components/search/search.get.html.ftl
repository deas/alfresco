<#assign el=args.htmlid>
<#assign searchconfig=config.scoped['Search']['search']>
<script type="text/javascript">//<![CDATA[
   new Alfresco.Search("${el}").setOptions(
   {
      siteId: "${siteId}",
      siteTitle: "${siteTitle?js_string}",
      initialSearchTerm: "${searchTerm?js_string}",
      initialSearchTag: "${searchTag?js_string}",
      initialSearchAllSites: ${searchAllSites?string},
      initialSearchRepository: ${searchRepo?string},
      initialSort: "${searchSort?js_string}",
      searchQuery: "${searchQuery?js_string}",
      minSearchTermLength: ${args.minSearchTermLength!searchconfig.getChildValue('min-search-term-length')},
      maxSearchResults: ${args.maxSearchResults!searchconfig.getChildValue('max-search-results')}
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body" class="search">
   <#if searchQuery?length == 0 && searchconfig.getChildValue('repository-search') != "always">
   <div class="search-sites">
      <#if siteId?length != 0><a id="${el}-site-link" href="#" <#if !searchAllSites && !searchRepo>class="bold"</#if>>${msg('message.singlesite', siteTitle)?html}</a> |</#if>
      <a id="${el}-all-sites-link" href="#" <#if searchAllSites && !searchRepo>class="bold"</#if>>${msg('message.allsites')}</a>
      <span <#if searchconfig.getChildValue('repository-search') == "none">class="hidden"</#if>>| <a id="${el}-repo-link" href="#" <#if searchRepo>class="bold"</#if>>${msg('message.repository')}</a></span>
   </div>
   </#if>
   <div class="search-box">
      <div>
         <input type="text" class="terms" name="${el}-search-text" id="${el}-search-text" value="" maxlength="1024" />
      </div>
      <div>
         <span id="${el}-search-button" class="yui-button yui-push-button search-icon">
            <span class="first-child">
               <button type="button">${msg('button.search')}</button>
            </span>
         </span>
      </div>
   </div>
   
   <div class="yui-gc search-bar theme-bg-color-3">
      <div class="yui-u first">
         <div id="${el}-search-info" class="search-info">${msg("search.info.searching")}</div>
         <div id="${el}-paginator-top" class="paginator hidden"></div>
      </div>
      <div class="yui-u align-right">
         <span class="yui-button yui-push-button" id="${el}-sort-menubutton">
            <span class="first-child"><button></button></span>
         </span>
         <select id="${el}-sort-menu" class="yuimenu hidden">
            <#list sortFields as sort>
            <option value="${sort.type!""}">${sort.label}</option>
            </#list>
         </select>
      </div>
   </div>
   
   <div id="${el}-results" class="results"></div>
   
   <div id="${el}-search-bar-bottom" class="yui-gc search-bar search-bar-bottom theme-bg-color-3 hidden">
      <div class="yui-u first">
         <div class="search-info">&nbsp;</div>
         <div id="${args.htmlid}-paginator-bottom" class="paginator paginator-bottom"></div>
      </div>
   </div>
</div>