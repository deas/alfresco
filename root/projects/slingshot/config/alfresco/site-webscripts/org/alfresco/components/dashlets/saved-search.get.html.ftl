<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/dashlets/saved-search.css" group="dashlets" />
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/search/search-lib.js" group="dashlets"/>
   <@script type="text/javascript" src="${url.context}/res/components/dashlets/saved-search.js" group="dashlets"/>
   <@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="dashlets"/>
</@>

<@markup id="widgets">
   <@inlineScript group="dashlets">
      var savedSearchDashletEvent = new YAHOO.util.CustomEvent("onConfigSearchClick");
   </@>
   <@createWidgets group="dashlets"/>
   <@inlineScript group="dashlets">
      savedSearchDashletEvent.subscribe(savedSearch.onConfigSearchClick, savedSearch, true);
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div class="dashlet savedsearch">
         <div class="title">${msg("header.title")}</div>
         <div id="${el}-list" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
            <div id="${el}-search-results"></div>
         </div>
      </div>
   </@>
</@>